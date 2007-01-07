/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import org.netbeans.api.javahelp.Help;

import org.netbeans.modules.tasklist.usertasks.dependencies.DependenciesPanel;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.renderers.PriorityListCellRenderer;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.DurationFormat;

/**
 * Panel used to enter/edit a user task.
 * Please read comment at the beginning of initA11y before editing
 * this file using the form builder.
 *
 * @author Tor Norbye
 * @author tl
 */
public class EditTaskPanel extends JPanel {

    private static final long serialVersionUID = 1;

    private static String[] PERCENTS = {
        "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", // NOI18N
        "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%" // NOI18N
    };
    
    /**
     * Parses values with % sign
     *
     * @param text text to parse
     * @return parsed value
     */
    private static int parsePercents(String text) throws NumberFormatException {
        text = text.trim();
        if (text.endsWith("%")) // NOI18N
            text = text.substring(0, text.length() - 1);
        int n = Integer.parseInt(text);
        if (n < 0 || n > 100)
            throw new NumberFormatException("Wrong range"); // NOI18N
        return n;
    }
    
    /**
     * InputVerifier for the combobox with percents
     */
    private static class PercentsInputVerifier extends InputVerifier {
        public boolean verify(javax.swing.JComponent input) {
            String p = ((JTextComponent) input).getText();
            try {
                parsePercents(p);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
    
    /**
     * InputVerifier for a JTextField with an integer
     */
    private static class IntTextFieldInputVerifier extends InputVerifier {
        public boolean verify(javax.swing.JComponent input) {
            String p = ((JTextComponent) input).getText();
            try {
                Integer.parseInt(p);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
    
    private static boolean appendDefault = Settings.getDefault().getAppend();
    
    private SimpleDateFormat format;
    private ComboBoxModel prioritiesModel = 
        new DefaultComboBoxModel(new Integer[] {
            new Integer(UserTask.HIGH),
            new Integer(UserTask.MEDIUM_HIGH),
            new Integer(UserTask.MEDIUM),
            new Integer(UserTask.MEDIUM_LOW),
            new Integer(UserTask.LOW),
        });
    private ListCellRenderer priorityRenderer = new PriorityListCellRenderer();
    private DependenciesPanel dp;
    
    /** 
     * Creates new form.
     *
     * @param editing true = no append/prepend options
     */
    public EditTaskPanel(boolean editing) {
        initComponents();
        initA11y();
        priorityComboBox.setSelectedIndex(2);
        
        format = new SimpleDateFormat();
        
        if (editing) {
            addLabel.setVisible(false);
            beginningToggle.setVisible(false);
            endToggle.setVisible(false);
            jLinkButtonAddToSource.setVisible(false);
            jCheckBoxTopLevel.setVisible(false);
        } else {
            boolean append = appendDefault;
            if (append) {
                endToggle.setSelected(true);
            } else {
                beginningToggle.setSelected(true);
            }
        }
        
        jComboBoxProgress.setModel(new DefaultComboBoxModel(PERCENTS));
        ((JComponent) jComboBoxProgress.getEditor().getEditorComponent()).
            setInputVerifier(new EditTaskPanel.PercentsInputVerifier());
        
        dp = new DependenciesPanel();
        dp.setBorder(new EmptyBorder(11, 11, 12, 12));
        jPanelDependencies.add(dp, BorderLayout.CENTER);
        
        Dimension d = jDateChooserDue.getPreferredSize();
        d.width = 120;
        jDateChooserDue.setPreferredSize(d);
        jDateChooserStart.setPreferredSize(d);
        
        d = jComboBoxProgress.getPreferredSize();
        d.width = 50;
        jComboBoxProgress.setPreferredSize(d);
    }
    
    public void addNotify() {
        super.addNotify();
        descriptionTextField.requestFocus();
    }    
    
    /**
     * Transfers the focus into the summary text field
     */
    public void focusSummary() {
        jTabbedPane.setSelectedIndex(0);
        descriptionTextField.requestFocus();
    }
    
    /**
     * Fills the panel with the values of a user task
     *
     * @param item a user task
     */
    public void fillPanel(UserTask item) {
        if (item.getSummary() != null) {
            descriptionTextField.setText(item.getSummary());
        }
        priorityComboBox.setSelectedItem(new Integer(item.getPriority()));
        
        URL url = item.getUrl();
        if (url != null) {
            fileTextField.setText(url.toExternalForm());
            if (fileTextField.getText().length() > 0)
                fileTextField.setCaretPosition(fileTextField.getText().length()-1);
            fileCheckBox.setSelected(true);
            lineTextField.setText(Integer.toString(item.getLineNumber() + 1));
        } else {
            fileCheckBox.setSelected(false);
        }
        detailsTextArea.setText(item.getDetails());
        setDueDate(item.getDueDate());
        setStart(item.getStart());

        // Initialize the Categories list
        String[] categories = item.getList().getCategories();
        if (categories.length > 0) {
            DefaultComboBoxModel model = new DefaultComboBoxModel(categories);
            categoryCombo.setModel(model);
        }
        categoryCombo.setSelectedItem(item.getCategory());
        
        jComboBoxProgress.setSelectedItem(item.getPercentComplete() + "%"); // NOI18N
        jCheckBoxCompute.setSelected(item.isValuesComputed());
        jCheckBoxComputeActionPerformed(null);
        
        durationPanelEffort.setDuration(item.getEffort());
        
        DateFormat df = DateFormat.getDateTimeInstance(
            DateFormat.LONG, DateFormat.LONG);
        jLabelCreated.setText(df.format(new Date(item.getCreatedDate())));
        jLabelLastEdited.setText(df.format(new Date(item.getLastEditedDate())));
        
        durationPanelSpent.setDuration(item.getSpentTime());

        dp.fillPanel(item);
        
        String[] owners = item.getList().getOwners();
        if (owners.length > 0) {
            DefaultComboBoxModel model = new DefaultComboBoxModel(owners);
            jComboBoxOwner.setModel(model);
        }
        jComboBoxOwner.setSelectedItem(item.getOwner());

        if (item.getCompletedDate() != 0)
            jLabelCompleted.setText(df.format(new Date(item.getCompletedDate())));
        else
            jLabelCompleted.setText(""); // NOI18N
        
        DurationFormat durf = new DurationFormat(DurationFormat.Type.LONG);
        DefaultListModel dlm = new DefaultListModel();
        for (int i = 0; i < item.getWorkPeriods().size(); i++) {
            UserTask.WorkPeriod wp = (UserTask.WorkPeriod)
                item.getWorkPeriods().get(i);
            dlm.addElement(
                df.format(new Date(wp.getStart())) + ", " + // NOI18N
                durf.format(new Duration(wp.getDuration(), 
                    Settings.getDefault().getMinutesPerDay(),
                    Settings.getDefault().getDaysPerWeek(), true
                ))
            );
        }
        jListWorkPeriods.setModel(dlm);
        
        jLabelSpentTimeToday.setText(
                durf.format(
                new Duration(item.getSpentTimeToday(),
                Settings.getDefault().getMinutesPerDay(),
                Settings.getDefault().getDaysPerWeek(), true)));
    }
    
    /**
     * Fills an object with the values from this panel
     *
     * @param task a user object
     */
    public void fillObject(UserTask task) {
        task.setSummary(descriptionTextField.getText().trim());
        task.setDetails(detailsTextArea.getText().trim());
        if (categoryCombo.getSelectedItem() == null)
            task.setCategory(""); // NOI18N
        else
            task.setCategory(categoryCombo.getSelectedItem().toString().trim());
        int p = ((Integer) priorityComboBox.getSelectedItem()).intValue();
        task.setPriority(p);
        if (fileCheckBox.isSelected()) {
            try {
                URL url = new URL(fileTextField.getText().trim());
                task.setUrl(url);
                try {
                    int lineno = Integer.parseInt(lineTextField.getText());
                    if (lineno > 0)
                        task.setLineNumber(lineno - 1);
                    else
                        ; // TODO: validation
                } catch (NumberFormatException e) {
                    // TODO validation
                    UTUtils.LOGGER.log(Level.INFO, "", e);
                }
            } catch (MalformedURLException e) {
                // TODO: validation
                UTUtils.LOGGER.log(Level.INFO, "", e);
            }
        } else {
            task.setLine(null);
        }
        
        task.setDueDate(getDueDate());
        task.setStart(getStart());

        task.setValuesComputed(jCheckBoxCompute.isSelected());
        if (!task.isValuesComputed()) {
            task.setPercentComplete(
                parsePercents((String) jComboBoxProgress.getSelectedItem()));
            task.setEffort(durationPanelEffort.getDuration());
        }

        boolean valuesComputed = jCheckBoxCompute.isSelected();
        int spentTime = durationPanelSpent.getDuration();
        if (spentTime != task.getSpentTime() || 
            valuesComputed != task.isValuesComputed()) {
            boolean started = task.isStarted();
            if (started)
                task.stop();
            task.setValuesComputed(valuesComputed);
            if (!task.isValuesComputed()) {
                task.setSpentTime(spentTime);
            }
            if (started && !valuesComputed)
                task.start();
        }

        dp.fillObject();
        
        if (jComboBoxOwner.getSelectedItem() == null)
            task.setOwner(""); // NOI18N
        else
            task.setOwner(jComboBoxOwner.getSelectedItem().toString().trim());
    }
    
    /**
     * Returns the value of the due date
     *
     * @return due date
     */
    private Date getDueDate() {
        Date ret = jDateChooserDue.getDate();
        if (ret != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(ret);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            ret = c.getTime();
        }
        return ret;
    }
    
    /**
     * Returns the value of the start date
     *
     * @return start date
     */
    private long getStart() {
        long ret;
        if (jDateChooserStart.getDate() != null) {
            ret = jDateChooserStart.getDate().getTime();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(ret);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            ret = c.getTimeInMillis();
        } else {
            ret = -1;
        }
        return ret;
    }

    /**
     * Returns "append/prepend" option.
     *
     * @return true = the task should be appended
     */
    public boolean getAppend() {
        appendDefault = endToggle.isSelected();
        return appendDefault;
    }
    
    /**
     * Set the due date field
     *
     * @param d the due date
     */
    private void setDueDate(Date d) {
        jDateChooserDue.setDate(d);
    }
    
    /**
     * Set the start date field
     *
     * @param d the date
     */
    private void setStart(long d) {
        if (d != -1) {
            jDateChooserStart.setDate(new Date(d));
        } else {
            jDateChooserStart.setDate(null);
        }
    }
    
    /**
     * Changes associated file position in the dialog
     *
     * @param n line number
     */
    public void setLineNumber(int n) {
        lineTextField.setText(Integer.toString(n + 1));
    }

    /**
     * Sets new URL.
     *
     * @param URL an URL or null
     */
    public void setUrl(URL url) {
        if (url != null)
            fileTextField.setText(url.toExternalForm());
        else
            fileTextField.setText(""); // NOI18N
    }
    
    public void setAssociatedFilePos(boolean set) {
        fileCheckBox.setSelected(set);
    }
    
    /**
     * Should the task be added at the topmost level 
     * 
     * @return true = yes
     */
    public boolean isTopLevel() {
        return jCheckBoxTopLevel.isSelected();
    }
    
    /**
     * Sets the flag for "add at the topmost level".
     * 
     * @param b true = topmost level 
     */
    public void setTopLevel(boolean b) {
        jCheckBoxTopLevel.setSelected(b);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        addButtonGroup = new javax.swing.ButtonGroup();
        effortButtonGroup = new javax.swing.ButtonGroup();
        buttonGroupProgress = new javax.swing.ButtonGroup();
        buttonGroupSpent = new javax.swing.ButtonGroup();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelGeneral = new javax.swing.JPanel();
        descLabel = new javax.swing.JLabel();
        categoryCombo = new javax.swing.JComboBox();
        detailsScrollPane = new javax.swing.JScrollPane();
        detailsTextArea = new javax.swing.JTextArea();
        addLabel = new javax.swing.JLabel();
        beginningToggle = new javax.swing.JRadioButton();
        detailsLabel = new javax.swing.JLabel();
        endToggle = new javax.swing.JRadioButton();
        descriptionTextField = new javax.swing.JTextField();
        priorityComboBox = new javax.swing.JComboBox();
        fileCheckBox = new javax.swing.JCheckBox();
        categoryLabel = new javax.swing.JLabel();
        prioLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxOwner = new javax.swing.JComboBox();
        jLinkButtonAddToSource = new org.netbeans.modules.tasklist.usertasks.util.JLinkButton();
        jCheckBoxTopLevel = new javax.swing.JCheckBox();
        lineLabel = new javax.swing.JLabel();
        lineTextField = new javax.swing.JTextField();
        fileTextField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jCheckBoxCompute = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        durationPanelSpent = new org.netbeans.modules.tasklist.usertasks.DurationPanel();
        jLabel10 = new javax.swing.JLabel();
        jComboBoxProgress = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        durationPanelEffort = new org.netbeans.modules.tasklist.usertasks.DurationPanel();
        jDateChooserDue = new com.toedter.calendar.JDateChooser();
        jDateChooserStart = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabelLastEdited = new javax.swing.JLabel();
        jLabelCompleted = new javax.swing.JLabel();
        jLabelCreated = new javax.swing.JLabel();
        jPanelDependencies = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListWorkPeriods = new javax.swing.JList();
        jLabel12 = new javax.swing.JLabel();
        jLabelSpentTimeToday = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(400, 300));
        setLayout(new java.awt.BorderLayout());

        jPanelGeneral.setBorder(javax.swing.BorderFactory.createEmptyBorder(11, 11, 12, 12));

        descLabel.setLabelFor(descriptionTextField);
        /*
        org.openide.awt.Mnemonics.setLocalizedText(descLabel, NbBundle.getMessage(EditTaskPanel.class, "Brief_Description")); // NOI18N); // NOI18N
    */

    categoryCombo.setEditable(true);

    detailsTextArea.setLineWrap(true);
    detailsTextArea.setRows(5);
    detailsTextArea.setWrapStyleWord(true);
    detailsScrollPane.setViewportView(detailsTextArea);

    org.openide.awt.Mnemonics.setLocalizedText(addLabel, NbBundle.getMessage(EditTaskPanel.class, "AddTo")); // NOI18N(); // NOI18N

    addButtonGroup.add(beginningToggle);
    /*
    org.openide.awt.Mnemonics.setLocalizedText(beginningToggle, NbBundle.getMessage(EditTaskPanel.class, "BeginningList")); // NOI18N(); // NOI18N
    */

    detailsLabel.setLabelFor(detailsTextArea);
    /*
    org.openide.awt.Mnemonics.setLocalizedText(detailsLabel, NbBundle.getMessage(EditTaskPanel.class, "DetailsLabel")); // NOI18N); // NOI18N
    */

    addButtonGroup.add(endToggle);
    /*
    org.openide.awt.Mnemonics.setLocalizedText(endToggle, NbBundle.getMessage(EditTaskPanel.class, "EndList")); // NOI18N(); // NOI18N
    */

    priorityComboBox.setModel(prioritiesModel);
    priorityComboBox.setRenderer(priorityRenderer);
    priorityComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            priorityComboBoxActionPerformed(evt);
        }
    });

    /*
    org.openide.awt.Mnemonics.setLocalizedText(fileCheckBox, NbBundle.getMessage(EditTaskPanel.class, "AssociatedFile")); // NOI18N); // NOI18N
    */
    fileCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    fileCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            fileCheckBoxItemStateChanged(evt);
        }
    });

    categoryLabel.setLabelFor(categoryCombo);
    /*
    org.openide.awt.Mnemonics.setLocalizedText(categoryLabel, NbBundle.getMessage(EditTaskPanel.class, "CategoryLabel")); // NOI18N); // NOI18N
    */

    prioLabel.setLabelFor(priorityComboBox);
    /*
    org.openide.awt.Mnemonics.setLocalizedText(prioLabel, NbBundle.getMessage(EditTaskPanel.class, "PriorityLabel")); // NOI18N); // NOI18N
    */

    org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getBundle(EditTaskPanel.class).getString("OwnerLabel")); // NOI18N

    jComboBoxOwner.setEditable(true);

    org.openide.awt.Mnemonics.setLocalizedText(jLinkButtonAddToSource, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "AddToSource")); // NOI18N
    jLinkButtonAddToSource.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLinkButtonAddToSource.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jLinkButtonAddToSourceActionPerformed(evt);
        }
    });

    org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxTopLevel, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "TopLevel")); // NOI18N
    jCheckBoxTopLevel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    jCheckBoxTopLevel.setMargin(new java.awt.Insets(0, 0, 0, 0));

    lineLabel.setLabelFor(lineTextField);
    /*
    org.openide.awt.Mnemonics.setLocalizedText(lineLabel, NbBundle.getMessage(EditTaskPanel.class, "LineLabel")); // NOI18N); // NOI18N
    */

    lineTextField.setEditable(false);
    lineTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

    fileTextField.setColumns(100);
    fileTextField.setEditable(false);

    org.jdesktop.layout.GroupLayout jPanelGeneralLayout = new org.jdesktop.layout.GroupLayout(jPanelGeneral);
    jPanelGeneral.setLayout(jPanelGeneralLayout);
    jPanelGeneralLayout.setHorizontalGroup(
        jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanelGeneralLayout.createSequentialGroup()
            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelGeneralLayout.createSequentialGroup()
                    .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jPanelGeneralLayout.createSequentialGroup()
                            .add(fileCheckBox)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(fileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(lineLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(lineTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanelGeneralLayout.createSequentialGroup()
                            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(detailsLabel)
                                .add(jLabel1)
                                .add(descLabel)
                                .add(prioLabel)
                                .add(categoryLabel))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(priorityComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 137, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(categoryCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jComboBoxOwner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(descriptionTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                                .add(detailsScrollPane))))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                .add(jPanelGeneralLayout.createSequentialGroup()
                    .add(addLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(beginningToggle)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(endToggle)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jCheckBoxTopLevel))
                .add(jLinkButtonAddToSource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    jPanelGeneralLayout.linkSize(new java.awt.Component[] {categoryCombo, jComboBoxOwner, priorityComboBox}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    jPanelGeneralLayout.setVerticalGroup(
        jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanelGeneralLayout.createSequentialGroup()
            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(descLabel)
                .add(descriptionTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(detailsLabel)
                .add(detailsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(prioLabel)
                .add(priorityComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(categoryLabel)
                .add(categoryCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel1)
                .add(jComboBoxOwner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(fileCheckBox)
                .add(lineLabel)
                .add(lineTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(fileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jLinkButtonAddToSource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanelGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(addLabel)
                .add(beginningToggle)
                .add(endToggle)
                .add(jCheckBoxTopLevel)))
    );

    jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "General"), jPanelGeneral); // NOI18N

    jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 11, 12, 12));

    jCheckBoxCompute.setSelected(true);
    org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxCompute, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "ComputeAll")); // NOI18N
    jCheckBoxCompute.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    jCheckBoxCompute.setMargin(new java.awt.Insets(0, 0, 0, 0));
    jCheckBoxCompute.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jCheckBoxComputeActionPerformed(evt);
        }
    });

    jLabel8.setLabelFor(jCheckBoxCompute);
    org.openide.awt.Mnemonics.setLocalizedText(jLabel8, "<html>Effort and spent time will be computed as a sum of the subtask values. Progress will be computed as a weighted sum of the subtask values.</html>");

    jLabel9.setLabelFor(durationPanelEffort);
    org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "Effort_")); // NOI18N

    durationPanelSpent.setEnabled(false);

    jLabel10.setLabelFor(durationPanelSpent);
    org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "SpentTime_")); // NOI18N

    jComboBoxProgress.setEditable(true);
    jComboBoxProgress.setEnabled(false);
    jComboBoxProgress.setInputVerifier(new PercentsInputVerifier());

    jLabel11.setLabelFor(jComboBoxProgress);
    org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "Progress_")); // NOI18N

    durationPanelEffort.setEnabled(false);

    jLabel7.setLabelFor(jDateChooserStart);
    org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "LBL_Start")); // NOI18N

    jLabel4.setLabelFor(jDateChooserDue);
    org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "DueDateCb")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "LastEditedLabel")); // NOI18N); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "CreatedLabel")); // NOI18N); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getBundle(EditTaskPanel.class).getString("Completed")); // NOI18N

    org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel4Layout.createSequentialGroup()
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jLabel6)
                .add(jLabel2)
                .add(jLabel5))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jLabelLastEdited, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabelCompleted, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabelCreated))
            .addContainerGap())
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel4Layout.createSequentialGroup()
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel5)
                .add(jLabelCreated))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel6)
                .add(jLabelLastEdited))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel2)
                .add(jLabelCompleted)))
    );

    org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
        .add(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jLabel10)
                .add(jLabel11)
                .add(jLabel9))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(durationPanelEffort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(durationPanelSpent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jComboBoxProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(350, 350, 350))
        .add(jPanel3Layout.createSequentialGroup()
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jLabel7)
                .add(jLabel4))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(jDateChooserDue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jDateChooserStart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
            .addContainerGap(580, Short.MAX_VALUE))
        .add(jPanel3Layout.createSequentialGroup()
            .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
        .add(jPanel3Layout.createSequentialGroup()
            .add(jCheckBoxCompute)
            .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(jLabel7)
                .add(jDateChooserStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(jLabel4)
                .add(jDateChooserDue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(20, 20, 20)
            .add(jCheckBoxCompute)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jLabel8)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel9)
                .add(durationPanelEffort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel11)
                .add(jComboBoxProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel10)
                .add(durationPanelSpent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(30, 30, 30)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(187, Short.MAX_VALUE))
    );

    jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "TimeRelated"), jPanel3); // NOI18N

    jPanelDependencies.setLayout(new java.awt.BorderLayout());
    jTabbedPane.addTab(org.openide.util.NbBundle.getBundle(EditTaskPanel.class).getString("LBL_DependenciesTab"), jPanelDependencies); // NOI18N

    jScrollPane1.setViewportView(jListWorkPeriods);

    jLabel12.setLabelFor(jListWorkPeriods);
    org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "HistoryOfSpentTimes")); // NOI18N

    jLabel3.setText(org.openide.util.NbBundle.getBundle(EditTaskPanel.class).getString("SpentTimeToday")); // NOI18N

    org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE)
                .add(jPanel1Layout.createSequentialGroup()
                    .add(jLabel3)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabelSpentTimeToday, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE))
                .add(jLabel12))
            .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(jLabelSpentTimeToday, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jLabel12)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
            .addContainerGap())
    );

    jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "WorkPeriods"), jPanel1); // NOI18N

    add(jTabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxComputeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxComputeActionPerformed
        boolean b = jCheckBoxCompute.isSelected();
        durationPanelEffort.setEnabled(!b);
        jComboBoxProgress.setEnabled(!b);
        durationPanelSpent.setEnabled(!b);
    }//GEN-LAST:event_jCheckBoxComputeActionPerformed

    private void jLinkButtonAddToSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLinkButtonAddToSourceActionPerformed
        HelpCtx help = new HelpCtx(
                "org.netbeans.modules.tasklist.usertasks.AddTask"); // NOI18N

        Help h = (Help) Lookup.getDefault().lookup(Help.class);

        if (h != null) {
            h.showHelp(help);
            return;
        } else {
            // Did not work.
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_jLinkButtonAddToSourceActionPerformed

    private void priorityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priorityComboBoxActionPerformed
        // I don't know why JComboBox does not use my renderer to draw 
        // selected value
        int sel = priorityComboBox.getSelectedIndex();
        priorityComboBox.setForeground(PriorityListCellRenderer.COLORS[sel]);
    }//GEN-LAST:event_priorityComboBoxActionPerformed

    /** Initialize accessibility settings on the panel */
    private void initA11y() {
        /*
          I couldn't figure out how to use Mnemonics.setLocalizedText
          to set labels and checkboxes with a mnemonic using the
          form builder, so the closest I got was to use "/*" and "* /
          as code pre-init/post-init blocks, such that I don't actually
          execute the bundle lookup code - and then call it explicitly
          below. (I wanted to keep the text on the components so that
          I can see them when visually editing the GUI.
        */

        Mnemonics.setLocalizedText(descLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "Brief_Description")); // NOI18N
        Mnemonics.setLocalizedText(detailsLabel, NbBundle.getMessage(
                    EditTaskPanel.class, "DetailsLabel")); // NOI18N
        Mnemonics.setLocalizedText(prioLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "PriorityLabel")); // NOI18N
        Mnemonics.setLocalizedText(fileCheckBox, NbBundle.getMessage(
                 EditTaskPanel.class, "AssociatedFile")); // NOI18N
        Mnemonics.setLocalizedText(categoryLabel, NbBundle.getMessage(
                     EditTaskPanel.class, "CategoryLabel")); // NOI18N
        Mnemonics.setLocalizedText(lineLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "LineLabel")); // NOI18N
        Mnemonics.setLocalizedText(addLabel, NbBundle.getMessage(
                EditTaskPanel.class, "AddTo")); // NOI18N
        Mnemonics.setLocalizedText(beginningToggle, NbBundle.getMessage(
                       EditTaskPanel.class, "BeginningList")); // NOI18N
        Mnemonics.setLocalizedText(endToggle, NbBundle.getMessage(
                 EditTaskPanel.class, "EndList")); // NOI18N
        Mnemonics.setLocalizedText(jLinkButtonAddToSource, NbBundle.getMessage(
                       EditTaskPanel.class, "AddToSource")); // NOI18N

        this.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_NewTask")); // NOI18N
        descriptionTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Brief_Description")); // NOI18N
        detailsTextArea.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Details")); // NOI18N
        priorityComboBox.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Priority")); // NOI18N
        categoryCombo.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Category")); // NOI18N
        fileTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_File")); // NOI18N
        jDateChooserDue.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_DueCb")); // NOI18N

        // We're using a checkbox to "label" the textfield - of course JCheckBox
        // doesn't have a setLabelFor (since it is itself an input component)
        // so we have to label the associated component ourselves
        fileTextField.getAccessibleContext().setAccessibleName(fileCheckBox.getText());

        lineTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Line")); // NOI18N
        jDateChooserDue.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Due")); // NOI18N
        fileCheckBox.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_FileCb")); // NOI18N
        beginningToggle.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Beginning")); // NOI18N
        endToggle.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_End")); // NOI18N
        jLinkButtonAddToSource.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_AddSource")); // NOI18N

        // Gotta set accessible name - no more that I've set label for?
        // gotta set accessible description "everywhere" ?
    }

    private void fileCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fileCheckBoxItemStateChanged
        boolean s = fileCheckBox.isSelected();
        fileTextField.setEditable(s);
        lineTextField.setEditable(s);
    }//GEN-LAST:event_fileCheckBoxItemStateChanged

    // <editor-fold defaultstate="collapsed" desc=" Generated Code: Variables declarations ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup addButtonGroup;
    private javax.swing.JLabel addLabel;
    private javax.swing.JRadioButton beginningToggle;
    private javax.swing.ButtonGroup buttonGroupProgress;
    private javax.swing.ButtonGroup buttonGroupSpent;
    private javax.swing.JComboBox categoryCombo;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JLabel descLabel;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JLabel detailsLabel;
    private javax.swing.JScrollPane detailsScrollPane;
    private javax.swing.JTextArea detailsTextArea;
    private org.netbeans.modules.tasklist.usertasks.DurationPanel durationPanelEffort;
    private org.netbeans.modules.tasklist.usertasks.DurationPanel durationPanelSpent;
    private javax.swing.ButtonGroup effortButtonGroup;
    private javax.swing.JRadioButton endToggle;
    private javax.swing.JCheckBox fileCheckBox;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JCheckBox jCheckBoxCompute;
    private javax.swing.JCheckBox jCheckBoxTopLevel;
    private javax.swing.JComboBox jComboBoxOwner;
    private javax.swing.JComboBox jComboBoxProgress;
    private com.toedter.calendar.JDateChooser jDateChooserDue;
    private com.toedter.calendar.JDateChooser jDateChooserStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCompleted;
    private javax.swing.JLabel jLabelCreated;
    private javax.swing.JLabel jLabelLastEdited;
    private javax.swing.JLabel jLabelSpentTimeToday;
    private org.netbeans.modules.tasklist.usertasks.util.JLinkButton jLinkButtonAddToSource;
    private javax.swing.JList jListWorkPeriods;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelDependencies;
    private javax.swing.JPanel jPanelGeneral;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JTextField lineTextField;
    private javax.swing.JLabel prioLabel;
    private javax.swing.JComboBox priorityComboBox;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}
