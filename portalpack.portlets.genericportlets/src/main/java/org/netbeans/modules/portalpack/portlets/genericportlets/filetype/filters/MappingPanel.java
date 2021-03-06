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


package org.netbeans.modules.portalpack.portlets.genericportlets.filetype.filters;

import org.netbeans.modules.portalpack.portlets.genericportlets.core.FilterMappingData;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.FilterContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.openide.WizardDescriptor;

import org.openide.util.NbBundle;

/* 
 * Wizard panel that collects deployment data for Servlets and Filters
 * @author Ana von Klopp 
 */

/* 
 * Wizard panel that collects deployment data for Servlets and Filters
 * @author Ana von Klopp 
 */

/* 
 * Wizard panel that collects deployment data for Servlets and Filters
 * @author Ana von Klopp 
 */

/* 
 * Wizard panel that collects deployment data for Servlets and Filters
 * @author Ana von Klopp 
 */


class MappingPanel extends JPanel implements ActionListener, 
					     TableModelListener, 
					     ListSelectionListener { 

    private final static String ADD = "add"; 
    private final static String EDIT = "edit"; 
    private final static String REMOVE = "remove"; 
    private final static String UP = "up"; 
    private final static String DOWN = "down"; 

    // UI Variables
    private JLabel jLtableheader;
    private MappingTable table; 
    private JButton jBnew, jBedit, jBdelete, jBdown, jBup; 
    private JScrollPane scrollP;
    private FilterMappingData deployData; 
    private NewFilterWizardWizardPanel2 parent;

    private boolean edited = false; 

    private static final boolean debug = false; 
    private List portlets = new ArrayList();

    private static final long serialVersionUID = 6540270797782597645L;
    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    
    public MappingPanel(FilterMappingData deployData, NewFilterWizardWizardPanel2 parent) { 
	this.deployData = deployData; 
        this.deployData.setName(parent.getFilterName());
	this.parent = parent; 
        setName(NbBundle.getMessage(MappingPanel.class,"TTL_FILTER_MAPPING"));
	initComponents ();
        TableColumn column = table.getColumnModel().getColumn(1);
        List availablePortlets = parent.getAvailablePortlets();
        availablePortlets.add(0,"");
        column.setCellEditor(new ComboBoxEditor((String [])availablePortlets.toArray(new String[0])));
        column.setCellRenderer(new ComboBoxRenderer((String [])availablePortlets.toArray(new String[0])));
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }

    private void initComponents () {
	// Layout description
	setLayout(new java.awt.GridBagLayout());

	// Entity covers entire row
	GridBagConstraints fullRowC = new GridBagConstraints();
	fullRowC.gridx = 0;
	fullRowC.gridy = 0;
	fullRowC.gridwidth = 2;
	fullRowC.anchor = GridBagConstraints.WEST;
	fullRowC.fill = GridBagConstraints.HORIZONTAL;
	fullRowC.insets = new Insets(4, 0, 4, 60);

	// Button
	GridBagConstraints bC = new GridBagConstraints();
	bC.gridx = 1;
	bC.gridy = 1; 
	bC.weightx = 0.1; 
	bC.fill = GridBagConstraints.HORIZONTAL; 
	bC.insets = new Insets(4, 20, 4, 60);

	// Table panel 
	GridBagConstraints tablePanelC = new GridBagConstraints();
	tablePanelC.gridx = 0;
	tablePanelC.gridy = 1;
	tablePanelC.gridheight = 6;
	tablePanelC.fill = GridBagConstraints.BOTH; 
	tablePanelC.weightx = 0.9;
	tablePanelC.weighty = 1.0; 
	tablePanelC.anchor = GridBagConstraints.WEST; 
	tablePanelC.insets = new Insets(4, 0, 4, 0);
        
	// Filler panel 
	GridBagConstraints fillerC = new GridBagConstraints();
	fillerC.gridx = 1;
        fillerC.gridy = GridBagConstraints.RELATIVE; 
	fillerC.fill = GridBagConstraints.BOTH; 
	fillerC.weighty = 1.0; 
	fillerC.insets = new Insets(4, 0, 4, 0);

	// 2. Table header 
	jLtableheader = new JLabel(NbBundle.getMessage(MappingPanel.class, "LBL_filter_mappings"));
	jLtableheader.setDisplayedMnemonic(NbBundle.getMessage (MappingPanel.class, "LBL_filter_mappings_mnemonic").charAt(0));
	jLtableheader.setLabelFor(table);
	this.add(jLtableheader, fullRowC); 

	// 3. Table row
	table = new MappingTable(deployData.getName(), 
				 new ArrayList()); 
	jLtableheader.setLabelFor(table); 
	table.getAccessibleContext().setAccessibleName(NbBundle.getMessage(MappingPanel.class, "ACSD_filter_mappings")); // NOI18N
	table.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(MappingPanel.class, "ACSD_filter_mappings_desc")); // NOI18N

	table.getModel().addTableModelListener(this); 
	table.getSelectionModel().addListSelectionListener(this); 
	scrollP = new JScrollPane(table); 
	table.setPreferredScrollableViewportSize(new Dimension(300, 200));
	this.add(scrollP, tablePanelC); 

	jBnew = new JButton(); 
	jBnew.setText(NbBundle.getMessage(MappingPanel.class, 
					  "LBL_newdots")); //NOI18N
	jBnew.setMnemonic(NbBundle.getMessage(MappingPanel.class, "LBL_new_mnemonic").charAt(0));
	jBnew.addActionListener(this); 
	jBnew.setActionCommand(ADD); 
	jBnew.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(MappingPanel.class, "ACSD_filter_mappings_new"));
	this.add(jBnew, bC); 
        /*
	bC.gridy++;
	jBedit = new JButton(); 
	jBedit.setText(NbBundle.getMessage(MappingPanel.class, 
					  "LBL_edit")); //NOI18N
	jBedit.setMnemonic(NbBundle.getMessage(MappingPanel.class, "LBL_edit_mnemonic").charAt(0));
	jBedit.addActionListener(this); 
	jBedit.setActionCommand(EDIT); 
	jBedit.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(MappingPanel.class, "ACSD_filter_mappings_edit")); 
	this.add(jBedit, bC); 
        */
	bC.gridy++;
	jBdelete = new JButton(); 
	jBdelete.setText(NbBundle.getMessage(MappingPanel.class, 
					     "LBL_delete")); //NOI18N
	jBdelete.setMnemonic(NbBundle.getMessage(MappingPanel.class, "LBL_delete_mnemonic").charAt(0));
	jBdelete.addActionListener(this); 
	jBdelete.setActionCommand(REMOVE); 
	jBdelete.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(MappingPanel.class, "ACSD_filter_mappings_delete")); 
	this.add(jBdelete, bC); 

	/*bC.gridy++; 
	jBup = new JButton(); 
	jBup.setText(NbBundle.getMessage(MappingPanel.class,
					 "LBL_move_up")); //NOI18N
	jBup.setMnemonic(NbBundle.getMessage(MappingPanel.class, "LBL_move_up_mnemonic").charAt(0));
	jBup.addActionListener(this);
	jBup.setActionCommand(UP); 
	jBup.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(MappingPanel.class, "ACSD_filter_mappings_up")); 
	this.add(jBup, bC); 

	bC.gridy++; 
	jBdown = new JButton(); 
	jBdown.setText(NbBundle.getMessage(MappingPanel.class,
					   "LBL_move_down")); //NOI18N
	jBdown.setMnemonic(NbBundle.getMessage(MappingPanel.class, "LBL_move_down_mnemonic").charAt(0));
	jBdown.addActionListener(this);
	jBdown.setActionCommand(DOWN); 
	jBdown.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(MappingPanel.class, "ACSD_filter_mappings_down")); 
	this.add(jBdown, bC); 
        */
	bC.gridy++; 
	bC.fill=GridBagConstraints.BOTH; 
	JPanel filler = new JPanel(); 
	this.add(filler, bC);
        
        this.add(new javax.swing.JPanel(),fillerC);
    }

    void setData() { 
	if(debug) log("::setData()"); //NOi18N
	    
	// Check if the name has changed - if it has, then we
	// change all the entries for this filter.
	
	table.setFilterName(deployData.getName()); 
    
	if(!edited) { 

	  /*  if(!deployData.makeEntry()) { 
		this.setEnabled(false); 
		return; 
	    }*/

	    table.setRowSelectionInterval(0,0);
	    edited = true; 
	} 

	//scrollP.repaint(); 
    } 

    public void actionPerformed(ActionEvent evt) { 

	if(debug) log("::actionPerformed()"); //NOI18n
	if(evt.getSource() instanceof JButton) { 
	    if(evt.getActionCommand() == ADD) { 
		FilterMappingData fmd =  new FilterMappingData(deployData.getName()); 
		
                table.addRow(0,fmd);
		/*MappingEditor editor = 
		    new MappingEditor(fmd, deployData.getServletNames()); 
		editor.showEditor(); 
		if(editor.isOK()) { 
		    table.addRow(0, fmd); 
		    if(debug) log(fmd.toString()); 
		} */
	    } 

	    else if (evt.getActionCommand() == EDIT) { 
		int index = table.getSelectedRow(); 
		FilterMappingData fmd, fmd2;
		fmd = table.getRow(index); 
		fmd2 = (FilterMappingData)(fmd.clone()); 
		/*MappingEditor editor = 
		    new MappingEditor(fmd2, deployData.getServletNames()); 
		editor.showEditor(); 
		if(editor.isOK()) { 
		    table.setRow(index, fmd2); 
		    if(debug) log(fmd2.toString()); 
		} */
	    }
	    else if (evt.getActionCommand() == REMOVE) { 
		int index = table.getSelectedRow(); 
		table.removeRow(index); 
		table.clearSelection(); 
	    }
	    else if (evt.getActionCommand() == UP) { 
		if(debug) log("\tMove up");//NOI18N
		int index = table.getSelectedRow(); 
		table.moveRowUp(index); 
		table.setRowSelectionInterval(index-1, index-1); 

	    }
	    else if (evt.getActionCommand() == DOWN) { 
		int index = table.getSelectedRow(); 
		table.moveRowDown(index); 
		table.setRowSelectionInterval(index+1, index+1); 
	    }
	} 
	edited = true; 
////	deployData.setFilterMappings(table.getFilterMappings()); 
	scrollP.revalidate(); 
	parent.fireChangeEvent();
    }

    public void tableChanged(TableModelEvent e) {
	if(debug) log("::tableChanged()"); //NOI18N
	edited = true; 
//////	deployData.setFilterMappings(table.getFilterMappings()); 
/////	parent.fireChangeEvent();
    }
	
    public void valueChanged(ListSelectionEvent e) {
	//Ignore extra messages.
	if (e.getValueIsAdjusting()) return;
/// 	this.setEnabled(deployData.makeEntry()); 
    }

    public void setEnabled(boolean enable) { 
	if(debug) log("::setEnabled()"); //NOI18N

	jLtableheader.setEnabled(enable);
	jBnew.setEnabled(enable); 

	if(!enable) { 
	    jBedit.setEnabled(false); 
	    jBdelete.setEnabled(false); 
	    jBup.setEnabled(false); 
	    jBdown.setEnabled(false); 
	    return; 
	}

	ListSelectionModel lsm = table.getSelectionModel(); 
	if (lsm.isSelectionEmpty()) {
	    // disable the relevant buttons
	    jBdelete.setEnabled(false); 
	    jBedit.setEnabled(false); 
	    jBdown.setEnabled(false); 
	    jBup.setEnabled(false); 
	} 
	else {
	    // We only allow single selections
	    int selectedRow = lsm.getMinSelectionIndex();
	    String str = (String)(table.getValueAt(selectedRow, 0)); 
	    boolean canEdit = str.equals(deployData.getName()); 
	    jBdelete.setEnabled(canEdit); 
	    jBedit.setEnabled(canEdit); 
	    int numRows = table.getRowCount(); 
	    if(selectedRow > 0) 
		jBup.setEnabled(true); 
	    else 
		jBup.setEnabled(false); 
	    if(selectedRow < numRows-1) 
		jBdown.setEnabled(true); 
	    else 
		jBdown.setEnabled(false); 
	}
    } 


    private void log(String s) { 
	logger.log(Level.FINE,s);
    }
    
     
     
     public void read(WizardDescriptor wizardDescriptor)
    {
        //portlets = (List)wizardDescriptor.getProperty("portlets");
         deployData.setName(parent.getFilterName());
        
    }
    public void store(WizardDescriptor wizardDescriptor)
    {
       List filterMappings = table.getFilterMappings();
       FilterContext context = (FilterContext)wizardDescriptor.getProperty("context");
    
       if(context == null)
       {
         context = new FilterContext();
         wizardDescriptor.putProperty("context", context);
       }
       
       context.setFilterMappingData((FilterMappingData [])filterMappings.toArray(new FilterMappingData[0]));
       
    }
    
    public class ComboBoxEditor extends DefaultCellEditor {
        public ComboBoxEditor(String[] items) {
            super(new JComboBox(items));
        }
    }
    
    public class ComboBoxRenderer extends JComboBox implements TableCellRenderer {
        public ComboBoxRenderer(String[] items) {
            super(items);
        }
    
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
    
            // Select the current value
            setSelectedItem(value);
            return this;
        }
    }
}
