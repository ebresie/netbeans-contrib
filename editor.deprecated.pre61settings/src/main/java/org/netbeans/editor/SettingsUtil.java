/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.editor;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import org.netbeans.modules.editor.lib.ColoringMap;
import org.netbeans.modules.editor.lib.KitsTracker;

/**
* Utility methods for managing settings
*
* @author Miloslav Metelka
* @version 1.00
*/

public class SettingsUtil {

    public static final String TOKEN_COLORING_INITIALIZER_NAME_SUFFIX
        = "token-coloring-initializer"; // NOI18N

    public static final PrintColoringEvaluator defaultPrintColoringEvaluator
    = new PrintColoringEvaluator();

    private static final float defaultPrintFontSize = 10;

    /** Get either the cloned list or new list if the old
    * one was null.
    * @param l list to check
    * @return the cloned list if it was non-null or the new list
    */
    public static List getClonedList(List l) {
        return (l != null) ? new ArrayList(l) : new ArrayList();
    }

    public static List getClonedList(Class kitClass, String settingName) {
        return getClonedList((List)Settings.getValue(kitClass, settingName));
    }

    /** Useful for initializers */
    public static List getClonedList(Map settingsMap, String settingName) {
        if (settingsMap != null) {
            return getClonedList((List)settingsMap.get(settingName));
        } else {
            return null;
        }
    }


    public static Map getClonedMap(Map m) {
        return (m != null) ? new HashMap(m) : new HashMap();
    }

    public static Map getClonedMap(Class kitClass, String settingName) {
        return getClonedMap((Map)Settings.getValue(kitClass, settingName));
    }

    /** Useful for initializers */
    public static Map getClonedMap(Map settingsMap, String settingName) {
        if (settingsMap != null) {
            return getClonedMap((Map)settingsMap.get(settingName));
        } else {
            return null;
        }
    }


    public static Object getValue(Class kitClass, String settingName,
                                  Object defaultValue) {
        Object value = Settings.getValue(kitClass, settingName);
        return (value != null) ? value : defaultValue;
    }

    public static int getInteger(Class kitClass, String settingName,
                                 int defaultValue) {
        Object value = Settings.getValue(kitClass, settingName);
        return (value instanceof Integer) ?  ((Integer)value).intValue() : defaultValue;
    }

    public static int getInteger(Class kitClass, String settingName,
                                 Integer defaultValue) {
        return getInteger(kitClass, settingName, defaultValue.intValue());
    }

    public static int getPositiveInteger(Class kitClass, String settingName,
                                         int defaultValue) {
        int ret = getInteger(kitClass, settingName, defaultValue);
        if (ret <= 0) {
            ret = defaultValue;
        }
        return ret;
    }

    public static int getPositiveInteger(Class kitClass, String settingName,
                                         Integer defaultValue) {
        return getPositiveInteger(kitClass, settingName, defaultValue.intValue());
    }

    public static int getNonNegativeInteger(Class kitClass, String settingName,
                                            int defaultValue) {
        int ret = getInteger(kitClass, settingName, defaultValue);
        if (ret < 0) {
            ret = defaultValue;
        }
        return ret;
    }

    public static int getNonNegativeInteger(Class kitClass, String settingName,
                                            Integer defaultValue) {
        return getNonNegativeInteger(kitClass, settingName, defaultValue.intValue());
    }

    public static boolean getBoolean(Class kitClass, String settingName,
                                     boolean defaultValue) {
        Object value = Settings.getValue(kitClass, settingName);
        return (value instanceof Boolean) ? ((Boolean)value).booleanValue() : defaultValue;
    }

    public static boolean getBoolean(Class kitClass, String settingName,
                                     Boolean defaultValue) {
        return getBoolean(kitClass, settingName, defaultValue.booleanValue());
    }

    public static String getString(Class kitClass, String settingName,
                                   String defaultValue) {
        Object value = Settings.getValue(kitClass, settingName);
        return (value instanceof String) ? (String)value : defaultValue;
    }

    public static Acceptor getAcceptor(Class kitClass, String settingName,
                                       Acceptor defaultValue) {
        Object value = Settings.getValue(kitClass, settingName);
        return (value instanceof Acceptor) ? (Acceptor)value : defaultValue;
    }

    public static List getList(Class kitClass, String settingName,
                               List defaultValue) {
        Object value = Settings.getValue(kitClass, settingName);
        return (value instanceof List) ? (List)value : defaultValue;
    }

    public static List getCumulativeList(Class kitClass, String settingName,
                                         List defaultValue) {
        Settings.KitAndValue[] kva = Settings.getValueHierarchy(kitClass, settingName);
        if (kva != null && kva.length > 0) {
            List l = new ArrayList();
            for (int i = 0; i < kva.length; i++) {
                if (kva[i].value instanceof List) {
                    l.addAll((List)kva[i].value);
                }
            }
            return l;
        } else {
            return defaultValue;
        }
    }

    public static Map getMap(Class kitClass, String settingName,
                             Map defaultValue) {
        Object value = Settings.getValue(kitClass, settingName);
        return (value instanceof Map) ? (Map)value : defaultValue;
    }


    public static void updateListSetting(Class kitClass,
                                         String settingName, Object[] addToList) {
        if (addToList != null && addToList.length > 0) {
            List l = getClonedList(kitClass, settingName);
            l.addAll(Arrays.asList(addToList));
            Settings.setValue(kitClass, settingName, l);
        }
    }

    public static void updateListSetting(Map settingsMap,
                                         String settingName, Object[] addToList) {
        if (settingsMap != null && addToList != null && addToList.length > 0) {
            List l = getClonedList(settingsMap, settingName);
            l.addAll(Arrays.asList(addToList));
            settingsMap.put(settingName, l);
        }
    }
    
    private static String getColoringSettingName(String coloringName,
    boolean printingSet) {
        return (coloringName
                + (printingSet ? SettingsNames.COLORING_NAME_PRINT_SUFFIX : SettingsNames.COLORING_NAME_SUFFIX)
               ).intern();
    }

    /**
     * @deprecated Use Editor Settings API instead.
     */
    public static Coloring getColoring(Class kitClass, String coloringName, boolean printingSet) {
        return (Coloring)getColoring(kitClass, coloringName, printingSet, true);
    }

    /**
     * @deprecated Use Editor Settings API instead.
     */
    public static Object getColoring(Class kitClass, String coloringName, 
    boolean printingSet, boolean evaluateEvaluators) {
        return Settings.getValue(kitClass,
            getColoringSettingName(coloringName, printingSet),
            evaluateEvaluators
        );
    }

    /**
     * @deprecated Use Editor Settings API instead.
     */
    public static Coloring getTokenColoring(TokenContextPath tcp, 
        TokenCategory tokenIDOrCategory, boolean printingSet)
    {
        return (Coloring)getTokenColoring(tcp, tokenIDOrCategory, printingSet, true);
    }

    /**
     * @deprecated Use Editor Settings API instead.
     */
    public static Object getTokenColoring(TokenContextPath tcp,
        TokenCategory tokenIDOrCategory, boolean printingSet, boolean evaluateEvaluators)
    {
        return getColoring(BaseKit.class, tcp.getFullTokenName(tokenIDOrCategory),
                printingSet, evaluateEvaluators);
    }

    /** 
     * Get the coloring setting from the map that holds the settings values
     * for the particular kit.
     * 
     * @param settingsMap map that holds the [setting-name, setting-value] pairs
     *  for some kit-class.
     * @param coloringName name of the coloring to retrieve
     * @param printingSet retrieve the value of printing coloring
     *  instead of component coloring.
     */
    public static Object getColoring(Map settingsMap, String coloringName, boolean printingSet) {
        return settingsMap.get(getColoringSettingName(coloringName, printingSet));
    }

    /**
     * @deprecated Use Editor Settings Storage API instead.
     */
    public static void setColoring(Class kitClass, String coloringName, Object newValue, boolean printingSet) {
// XXX: try harder to preserve backwards compatibility of this call
//        Settings.setValue(kitClass, getColoringSettingName(coloringName, printingSet), newValue);
    }

    /**
     * @deprecated Use Editor Settings Storage API instead.
     */
    public static void setColoring(Class kitClass, String coloringName,
    Object componentColoringNewValue) {
        setColoring(kitClass, coloringName, componentColoringNewValue, false);
//         setColoring(kitClass, coloringName, defaultPrintColoringEvaluator, true);
    }

    /**
     * @deprecated Use Editor Settings Storage API instead.
     */
    public static void setColoring(Class kitClass, String coloringName,
    Object componentColoringNewValue, Object printColoringNewValue) {
        setColoring(kitClass, coloringName, componentColoringNewValue, false);
//        setColoring(kitClass, coloringName, printColoringNewValue, true);
    }

    /** 
     * Put the coloring into a map holding the settings for the particular kit.
     */
    public static void setColoring(Map settingsMap, String coloringName,
    Object newValue, boolean printingSet) {
        settingsMap.put(getColoringSettingName(coloringName, printingSet), newValue);
    }

    /** 
     * Put the coloring into a map holding the settings for the particular kit and assign
     * a default print coloring Evaluator to the print coloring setting.
     */
    public static void setColoring(Map settingsMap, String coloringName,
    Object componentColoringNewValue) {
        setColoring(settingsMap, coloringName, componentColoringNewValue, false);
//        setColoring(settingsMap, coloringName, defaultPrintColoringEvaluator, true);
    }

    /**
     */
    public static void setColoring(Map settingsMap, String coloringName,
    Object componentColoringNewValue, Object printColoringNewValue) {
        setColoring(settingsMap, coloringName, componentColoringNewValue, false);
//        setColoring(settingsMap, coloringName, printColoringNewValue, true);
    }


    /**
     * Tries to gather all colorings defined for the kit's mime type. If the
     * mime type can't be determined from the editor kit's class passed in, this
     * method will try to load colorings defined for the empty mime path (ie. all
     * languages).
     * 
     * @param kitClass The kit class for which the colorings should be loaded.
     * @param printingSet Ignored.
     * @param evaluateEvaluators Ignored.
     * 
     * @return The map with all colorings defined for the mime type of the editor
     *   kit class passed in. The returned map may be inaccurate, please use 
     *   the new Editor Settings API and its <code>FontColorSettings</code> class.
     * 
     * @deprecated Use Editor Settings API instead.
     */
    public static Map<String, Coloring> getColoringMap(
        Class kitClass, 
        boolean printingSet, 
        boolean evaluateEvaluators
    ) {
        String mimeType = KitsTracker.getInstance().findMimeType(kitClass);
        Map<String, Coloring> cm = ColoringMap.get(mimeType).getMap();
        return new HashMap<String, Coloring>(cm);
    }
    
    /** 
     * Update the settings according to the changes provided in the coloringMap.
     * 
     * @param kitClass class of the kit for which the colorings are being updated.
     *   Only the colorings with the names contained in <code>COLORING_NAME_LIST</code>
     *   will be updated for the kitClass settings. The rest is considered
     *   to be the token colorings so they are updated in BaseKit settings.
     * @param coloringMap map containing [coloring-name, coloring-value] pairs.
     * @param printingSet whether printing colorings should be updated instead
     *   of component colorings.
     * 
     * @deprecated Use Editor Settings Storage API instead.
     */
    public static void setColoringMap( Class kitClass, Map coloringMap, boolean printingSet ) {
        throw new UnsupportedOperationException("Use Editor Settings Storage API instead"); //NOI18N
    }
    
    /** 
     * Create initializer that reflects the colorings given by the coloring map.
     * 
     * @param kitClass class of the kit for which the colorings will be updated
     *   by the initializer.
     *   Only the colorings with the names contained in <code>COLORING_NAME_LIST</code>
     *   will be updated for the kitClass settings. The rest is considered
     *   to be the token colorings so they are updated in BaseKit settings
     *   by the initializer.
     * @param coloringMap map containing [coloring-name, coloring-value] pairs.
     * @param printingSet whether printing colorings should be updated instead
     *   of component colorings.
     * @param initializerName name that will be assigned to the initializer.
     * 
     * @deprecated Use Editor Settings and Editor Settings Storage API instead.
     */
    public static Settings.Initializer getColoringMapInitializer(
    Class kitClass, Map coloringMap, boolean printingSet,
    String initializerName) {
        return new ColoringMapInitializer(kitClass, coloringMap, printingSet,
            initializerName);
    }
    
    /** 
     * Evaluator that translates the regular coloring to the print coloring
     * by the default black-and-white rules.
     * 
     * @deprecated Fonts and colors for printing are the same as those on screen.
     *   Use Editor Settings and Editor Settings Storage API instead.
     */
    public static class PrintColoringEvaluator implements Settings.Evaluator {

        /** Translates the regular coloring to the print coloring
        * @param kitClass kit class for which the coloring is being retrieved
        * @param coloringName name of the coloring without the suffix
        * @param componentColoring component coloring retrieved from the settings. It's provided
        *   for convenience because the majority of Evaluator will derive
        *   the particular print coloring from the given component coloring.
        */
        protected Coloring getPrintColoring(Class kitClass,
                                            String coloringName, Coloring componentColoring) {
            Coloring printColoring = componentColoring;
            if (printColoring != null) {
                // Make the background color white
                if (printColoring.getBackColor() != null) {
                    printColoring = Coloring.changeBackColor(printColoring, Color.white);
                }
                // Make the foreground color black
                if (printColoring.getForeColor() != null) {
                    printColoring = Coloring.changeForeColor(printColoring, Color.black);
                }
                // Update the font height
                float pfh = getPrintFontSize();
                if (pfh >= 0) {
                    Font f = printColoring.getFont();
                    if (f != null) {
                        printColoring = Coloring.changeFont(printColoring, f.deriveFont(pfh));
                    }
                }
            }
            return printColoring;
        }

        /** Return the font size to which the coloring font should be updated.
        * Negative value means not to update the coloring font.
        */
        protected float getPrintFontSize() {
            return defaultPrintFontSize;
        }

        public Object getValue(Class kitClass, String settingName) {
            if (settingName.endsWith(SettingsNames.COLORING_NAME_PRINT_SUFFIX)) {
                String coloringName = settingName.substring(0,
                                      settingName.length() - SettingsNames.COLORING_NAME_PRINT_SUFFIX.length());
                Coloring c = getColoring(kitClass, coloringName, false);
                return getPrintColoring(kitClass, coloringName, c);
            }
            return null;
        }

    }

    /** 
     * Print coloring Evaluator that changes the font style. It's available here
     * because it's often used.
     * 
     * @deprecated Fonts and colors for printing are the same as those on screen.
     *   Use Editor Settings and Editor Settings Storage API instead.
     */
    public static class FontStylePrintColoringEvaluator extends PrintColoringEvaluator {

        private int fontStyle;

        public FontStylePrintColoringEvaluator(int fontStyle) {
            this.fontStyle = fontStyle;
        }

        protected @Override Coloring getPrintColoring(Class kitClass, String coloringName,
                                            Coloring componentColoring) {
            Coloring printColoring = super.getPrintColoring(kitClass, coloringName, componentColoring);
            Font f = printColoring.getFont();
            if (f != null) {
                printColoring = Coloring.changeFont(printColoring, f.deriveFont(fontStyle));
            }
            return printColoring;
        }

    }

    /** 
     * Print coloring Evaluator that changes the foreground color to the color given
     * in the constructor. It's available here because it's often used.
     * 
     * @deprecated Fonts and colors for printing are the same as those on screen.
     *   Use Editor Settings and Editor Settings Storage API instead.
     */
    public static class ForeColorPrintColoringEvaluator extends PrintColoringEvaluator {

        private Color foreColor;

        public ForeColorPrintColoringEvaluator(Color foreColor) {
            this.foreColor = foreColor;
        }

        protected @Override Coloring getPrintColoring(Class kitClass, String coloringName,
                                            Coloring componentColoring) {
            return Coloring.changeForeColor(
                       super.getPrintColoring(kitClass, coloringName, componentColoring),
                       foreColor
                   );
        }

    }

    /** 
     * Evaluator for the token coloring. 
     * 
     * @deprecated Use Editor Settings and Editor Settings Storage API instead.
     */
    public static class TokenColoringEvaluator implements Settings.Evaluator, java.io.Serializable {

        private String parentFullTokenIDName;

        private Coloring coloring;

        private boolean printingSet;

        /** Create new token-coloring evaluator.
        * @param tokenContextPath token-context path for which the evaluator
        *  is being created. The parent path is retrieved and full token name
        *  will be got on it for the token-id or category provided.
        * @param tokenIDOrCategory token-id or category for which the evaluator
        *  is being created.
        * @param coloring additional coloring that, if non-null will be applied
        *  to the coloring of the parentTokenID to form the resulting coloring.
        * @param printingSet whether this evaluator is for component or printing set coloring
        */
        public TokenColoringEvaluator(TokenContextPath tokenContextPath,
        TokenCategory tokenIDOrCategory, Coloring coloring, boolean printingSet) {
            this(tokenContextPath.getParent().getFullTokenName(tokenIDOrCategory),
                    coloring, printingSet);
        }

        /** Create new token-coloring evaluator.
        * @param parentFullTokenIDName name of the token from which is this token (for which
        *  is this evaluator created) derived.
        * @param coloring additional coloring that, if non-null will be applied
        *  to the coloring of the parentTokenID to form the resulting coloring.
        * @param printingSet whether this evaluator is for component or printing set coloring
        */
        public TokenColoringEvaluator(String parentFullTokenIDName, Coloring coloring,
        boolean printingSet) {
            this.parentFullTokenIDName = parentFullTokenIDName;
            this.coloring = coloring;
            this.printingSet = printingSet;
        }

        public Object getValue(Class kitClass, String settingName) {
            Coloring ret;
            ret = getColoring(BaseKit.class, parentFullTokenIDName, printingSet);

            if (coloring != null) { // make change only if coloring is non-null
                if (ret != null) { // parent is non-null
                    ret = coloring.apply(ret); // apply to parent
                }
            }

            return ret;
        }

    }

    /** 
     * Initializer for the token-coloring settings.
     * 
     * @deprecated Use Editor Settings and Editor Settings Storage API instead.
     */
    public static abstract class TokenColoringInitializer extends Settings.AbstractInitializer {

        /** Token context for which the colorings are being initialized. */
        private TokenContext tokenContext;

        /** Create settings-initializer for the colorings of the tokens
        * in the given context and all its context-paths. The name for the new
        * initializer is composed from the prefix of the tokenContext.
        * @param tokenContext context for which the colorings are being
        *   initialized.
        */
        public TokenColoringInitializer(TokenContext tokenContext) {
            this(tokenContext, tokenContext.getNamePrefix()
                    + TOKEN_COLORING_INITIALIZER_NAME_SUFFIX);
        }

        /** Create settings-initializer for the colorings of the tokens
        * in the given context and all its context-paths.
        * @param tokenContext context for which the colorings are being
        *   initialized.
        * @param initializerName name that will be given to the initializer.
        */
        public TokenColoringInitializer(TokenContext tokenContext,
        String initializerName) {
            super(initializerName);
            this.tokenContext = tokenContext;
        }

        public void updateSettingsMap(Class kitClass, Map settingsMap) {

            if (kitClass == BaseKit.class) { // all token colorings on base-kit level

                TokenContextPath[] allPaths = tokenContext.getAllContextPaths();
                for (int i = 0; i < allPaths.length; i++) {
                    TokenContextPath tcp = allPaths[i]; // current path

                    boolean printingSet = false; // process both sets
                    do {

                        TokenContext firstContext = tcp.getContexts()[0];
                        TokenCategory[] tokenCategories = firstContext.getTokenCategories();
                        for (int j = 0; j < tokenCategories.length; j++) {
                            Object catColoring = getTokenColoring(tcp, tokenCategories[j],
                                    printingSet);
                            if (catColoring != null) {
                                String fullName = tcp.getFullTokenName(tokenCategories[j]);
                                SettingsUtil.setColoring(settingsMap, fullName, catColoring, printingSet);
                            }
                        }

                        // Add token-ids
                        TokenID[] tokenIDs = firstContext.getTokenIDs();
                        for (int j = 0; j < tokenIDs.length; j++) {
                            Object tokenColoring = getTokenColoring(tcp, tokenIDs[j],
                                    printingSet);
                            if (tokenColoring != null) {
                                String fullName = tcp.getFullTokenName(tokenIDs[j]);
                                SettingsUtil.setColoring(settingsMap, fullName, tokenColoring, printingSet);
                            }
                        }

                        printingSet = !printingSet;

                    } while (printingSet);
                }
            }

        }

        /** Get either coloring or settings evaluator for the given target token-id.
        * @param tokenContextPath token-context-path for which the coloring
        *  is being created. All the context paths for the given token-context
        *  are processed.
        * @param tokenIDOrCategory token-id or token-category for which
        *  the coloring or evaluator is being created.
        * @param printingSet whether the set of the colorings used for printing
        *  is created instead of the colorings used for the component coloring.
        * @return either Coloring instance or an evaluator that returns a coloring.
        */
        public abstract Object getTokenColoring(TokenContextPath tokenContextPath,
        TokenCategory tokenIDOrCategory, boolean printingSet);

    }

    /** 
     * Coloring map initializer is used when coloring map is given
     * and it's necessary to provide the same settings through initializer.
     */
    static class ColoringMapInitializer extends Settings.AbstractInitializer {
        
        private Class kitClass;
        
        private HashMap baseKitMap;
        
        private HashMap kitClassMap;
        
        ColoringMapInitializer(Class kitClass, Map coloringMap, boolean printingSet,
        String initializerName) {

            super(initializerName);
            this.kitClass = kitClass;
            baseKitMap = new HashMap(31);
            kitClassMap = new HashMap(37);

            List colNameList = getCumulativeList(kitClass, SettingsNames.COLORING_NAME_LIST, null);
            if (colNameList != null && coloringMap != null && coloringMap.size() > 0) {
                HashSet nameSet = new HashSet(colNameList);

                for( Iterator i = coloringMap.keySet().iterator(); i.hasNext(); ) {
                    String name = (String)i.next();
                    Object coloring = coloringMap.get( name );
                    if( nameSet.contains( name ) ) {
                        setColoring( kitClassMap, name, coloring, printingSet );
                    } else {
                        setColoring( baseKitMap, name, coloring, printingSet );
                    }
                }
            }
        }
     
        public void updateSettingsMap(Class kitClass, Map settingsMap) {
            
            if (kitClass == BaseKit.class) {
                settingsMap.putAll(baseKitMap);

            } else if (kitClass == this.kitClass) {
                settingsMap.putAll(kitClassMap);
            }
            
        }
        
    }

}
