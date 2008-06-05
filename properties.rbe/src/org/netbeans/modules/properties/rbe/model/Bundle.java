/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s): Denis Stepanov
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.properties.rbe.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.netbeans.modules.properties.PropertiesDataObject;
import org.netbeans.modules.properties.rbe.ui.ResourceBundleEditorOptions;

/**
 * The Bundle
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class Bundle {

    /** The all locales which contains bundle */
    private Set<Locale> locales;
    /** The properties */
    private SortedMap<String, BundleProperty> properties;
    /* Tree values */
    private TreeItem<BundleProperty> treeRoot;
    /* Constants */
    /** The default locale */
    public final static Locale DEFAULT_LOCALE = new Locale("__", "", "");
    /** The property change event names */
    public static final String PROPERTY_LOCALES = "PROPERTY_LOCALES";
    static final LocaleComparator LOCALE_COMPARATOR = new LocaleComparator();
    private Bridge bridge;

    public Bundle(PropertiesDataObject dataObject) {
        locales = new TreeSet<Locale>(new LocaleComparator());
        bridge = Bridge.get(dataObject, this);
    }

    private void initProperties() {
        properties = new TreeMap<String, BundleProperty>();
        bridge.createProperties();
    }

    private void initTree() {
        treeRoot = new TreeItem<BundleProperty>(null);
        treeRoot = createChildren(null, null, new TreeSet<BundleProperty>(properties.values()));
    }

    /**
     * Adds property
     * @param fullname
     */
    public synchronized void createProperty(String key) {
        TreeItem<BundleProperty> tree = treeRoot;
        int index;
        int offset = 0;
        boolean finded = true;
        key = proceedKey(key);
        if (treeRoot != null) {
            while (true) {
                index = key.indexOf(getTreeSeparator(), offset);
                String group = index == -1 ? key : key.substring(0, index);
                offset = group.length() + getTreeSeparator().length();

                if (finded) {
                    finded = false;
                    for (TreeItem<BundleProperty> item : tree.getChildren()) {
                        if (item.getValue().getKey().equals(group)) {
                            if (item.getValue().getKey().equals(key)) {
                                createPropertyValues(item.getValue());
                            }
                            tree = item;
                            finded = true;
                            break;
                        }
                    }
                }
                if (!finded) {
                    BundleProperty property = createBundleProperty(group);
                    createPropertyValues(property);
                    TreeItem<BundleProperty> treeItem = new TreeItem<BundleProperty>(property);
                    tree.addChild(treeItem);
                    tree = treeItem;
                }
                if (index == -1) {
                    break;
                }
            }
        } else {
            properties.put(key, createBundleProperty(key));
        }
    }

    public void createPropertyLocaleRepresentation(Locale locale, String key, String value, String comment) {
        key = proceedKey(key);
        BundleProperty bundleProperty = properties.get(key);
        if (bundleProperty == null) {
            createProperty(key);
            properties.get(key);
        }
    }

    public void setPropertyValue(Locale locale, String key, String value) {
        bridge.setPropertyValue(locale, key, value);
    }

    public void setPropertyComment(Locale locale, String key, String comment) {
        bridge.setPropertyComment(locale, key, comment);
    }
//    public void save() {
//        treeRoot.accept(new TreeVisitor<TreeItem<BundleProperty>>() {
//
//            public void preVisit(TreeItem<BundleProperty> tree) {
//                if (tree.getHeight() == 1) {
//                    System.out.println();
//                }
//                System.out.println(tree.getValue() == null ? "" : tree.getValue().getKey());
//            }
//
//            public void postVisit(TreeItem<BundleProperty> tree) {
//            }
//
//            public boolean isDone() {
//                return false;
//            }
//        });
//
//    }
    public Set<Locale> getLocales() {
        return Collections.unmodifiableSet(locales);
    }

    public synchronized SortedMap<String, BundleProperty> getProperties() {
        if (properties == null) {
            initProperties();
        }
        return Collections.unmodifiableSortedMap(properties);
    }

    public TreeItem<BundleProperty> getPropertiesTree() {
        if (properties == null) {
            initProperties();
        }
        if (treeRoot == null) {
            initTree();
        }
        return treeRoot;
    }

    public BundleProperty getProperty(String key) {
        return properties.get(key);
    }

    public boolean isPropertyExists(Locale locale, String key) {
        return bridge.isPropertyExists(locale, key);
    }

    private void createPropertyValues(BundleProperty property) {
        bridge.createPropertyValues(property);
    }

    /**
     * Creates child properties for specefic group property
     * @param groupProperty root of the current tree
     * @param groupProperties all properties of the group property subtree
     * @return child properties of the group property
     */
    private TreeItem<BundleProperty> createChildren(TreeItem<BundleProperty> tree, BundleProperty groupProperty, Collection<BundleProperty> groupProperties) {
        TreeItem<BundleProperty> subtree = new TreeItem<BundleProperty>(groupProperty);
        int offset = groupProperty == null ? 0 : groupProperty.getKey().length() + getTreeSeparator().length();
        BundleProperty subgroup = null;
        Set<BundleProperty> subgroupProperties = new TreeSet<BundleProperty>();
        for (BundleProperty property : groupProperties) {
            if (subgroup != null && property.getKey().startsWith(subgroup.getKey() + getTreeSeparator())) {
                //Property is from the current group property subtree
                subgroupProperties.add(property);
            } else {
                if (subgroup != null) {
                    //Property is from the different group
                    createChildren(subtree, subgroup, subgroupProperties);
                    subgroup = null;
                    subgroupProperties.clear();
                }
                //Finding the new subgroup property
                int index = property.getKey().indexOf(getTreeSeparator(), offset);
                if (index != -1) {
                    String fullname = property.getKey().substring(0, index);
                    subgroup = createBundleProperty(fullname);
                    subgroupProperties.add(property);
                    while ((index = property.getKey().indexOf(getTreeSeparator(), fullname.length() + getTreeSeparator().length())) != -1) {
                        fullname = property.getKey().substring(0, index);
                        subgroupProperties.add(createBundleProperty(fullname));
                    }
                } else {
                    subgroup = property;
                }

            }
        }
        if (subgroup != null) {
            createChildren(subtree, subgroup, subgroupProperties);
        }
        if (tree != null) {
            tree.addChild(subtree);
        }
        return subtree;
    }

    private String proceedKey(String key) {
        return key;
    }

    BundleProperty createBundleProperty(String key) {
        BundleProperty bundleProperty = new BundleProperty(this, getPropertyName(key), key);
        properties.put(key, bundleProperty);
        return bundleProperty;
    }

    void addLocale(Locale locale) {
        locales.add(locale);
    }

    /**
     * Gets property name, property key "a.b.c" has name "c" where separator is '.'
     * @param fullname
     * @return
     */
    private String getPropertyName(String fullname) {
        int lastIndex = fullname.lastIndexOf(getTreeSeparator());
        return lastIndex == -1 ? fullname : fullname.substring(lastIndex + getTreeSeparator().length());
    }

    private String getTreeSeparator() {
        return ResourceBundleEditorOptions.getSeparator();
    }
}

class LocaleComparator implements Comparator<Locale> {

    public int compare(Locale locale1, Locale locale2) {
        int diff = locale1.getLanguage().compareTo(locale2.getLanguage());
        if (diff == 0) {
            diff = locale1.getCountry().compareTo(locale2.getCountry());
            if (diff == 0) {
                diff = locale1.getVariant().compareTo(locale2.getVariant());
            }
        }
        return diff;
    }
}
