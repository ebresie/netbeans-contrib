/*
 * CBOMetric.java
 *
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.netbeans.modules.metrics.options.*;

import java.util.Iterator;
import org.netbeans.modules.classfile.ClassName;

/**
 * Class which calculates Coupling Between Objects (fan-out) for a
 * ClassElement.  CBO is defined as the number of dependencies to other 
 * classes plus the number of classes which are dependent on this class.
 *
 * @author  tball
 * @version 
 */
public class CBOMetric extends AbstractMetric {

    static final String displayName = 
        MetricsNode.bundle.getString ("LBL_CBOMetric");

    static final String shortDescription = 
	MetricsNode.bundle.getString ("HINT_CBOMetric");

    protected CBOMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "CBOMetric";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public MetricSettings getSettings() {
	return CBOMetricSettings.getDefault();
    }

    private boolean includeClass(ClassName cls, boolean includeJDK, 
                                 boolean includeOpenIDE) {
        // Don't check unless we have to...
        if (includeJDK && includeOpenIDE)
            return true;

        String name = cls.getExternalName();
        if (!includeJDK && 
            ((name.startsWith("java.") ||
              name.startsWith("javax.") ||
              name.startsWith("sun.") ||
              name.startsWith("com.sun.corba") ||
              name.startsWith("com.sun.image") ||
              name.startsWith("com.sun.imageio") ||
              name.startsWith("com.sun.java") ||
              name.startsWith("com.sun.naming") ||
              name.startsWith("com.sun.security"))))
            return false;

        if (!includeOpenIDE &&
            name.startsWith("org.openide."))
            return false;

        return true;
    }

    public Integer getMetricValue() {
        boolean includeJDK = 
            CBOMetricSettings.getDefault().includeJDKClasses();
        boolean includeIDE = 
            CBOMetricSettings.getDefault().includeOpenIDEClasses();

        int metric = 0;
        Iterator it = classMetrics.getDependentClasses().iterator();
        while (it.hasNext()) {
            ClassName cls = (ClassName)it.next();
            if (includeClass(cls, includeJDK, includeIDE))
                metric++;
        }

        it = classMetrics.getClientClasses().iterator();
        while (it.hasNext()) {
            ClassName cls = (ClassName)it.next();
            // check that it is not already counted as a dependent class
            if (includeClass(cls, includeJDK, includeIDE) && 
                !classMetrics.hasDependency(cls))
                    metric++;
        }
        return new Integer(metric);
    }
    
    public String getDetails() {
        boolean includeJDK = 
            CBOMetricSettings.getDefault().includeJDKClasses();
        boolean includeIDE = 
            CBOMetricSettings.getDefault().includeOpenIDEClasses();

        StringBuffer buf = new StringBuffer();
        buf.append("CBO for ");
        buf.append(classMetrics.getName());

        buf.append("\n  classes this class references:");
        int refs = 0;
        Iterator it = classMetrics.getDependentClasses().iterator();
        while (it.hasNext()) {
            ClassName cls = (ClassName)it.next();
            if (includeClass(cls, includeJDK, includeIDE)) {
                buf.append("\n    ");
                buf.append(cls.getExternalName());
                refs++;
            }
        }
        if (refs == 0)
            buf.append("\n    none");

        buf.append("\n  classes which reference this class:");
        it = classMetrics.getClientClasses().iterator();
        int clients = 0;
        while (it.hasNext()) {
            ClassName cls = (ClassName)it.next();
            if (includeClass(cls, includeJDK, includeIDE) && 
                !classMetrics.hasDependency(cls)) {
                buf.append("\n    ");
                buf.append(cls.getExternalName());
                clients++;
            }
        }
        if (clients == 0) {
            buf.append("\n    none");
        }
        return buf.toString();
    }

    public boolean needsOtherClasses() {
        return true;
    }

    /**
     * Actually a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new CBOMetric(cm);
	}
	public Class getMetricClass() {
	    return CBOMetric.class;
	}
    }
}

