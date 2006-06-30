/*
 * RFCMetric.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.netbeans.modules.metrics.options.*;

import java.util.*;

/**
 *
 * @author  tball
 * @version
 */
public class RFCMetric extends AbstractMetric {

    static final String displayName =
        MetricsNode.bundle.getString ("LBL_RFCMetric");

    static final String shortDescription =
	MetricsNode.bundle.getString ("HINT_RFCMetric");

    /** Creates new RFCMetric */
    protected RFCMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "RFCMetric";
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }
    
    public MetricSettings getSettings() {
	return RFCMetricSettings.getDefault();
    }

    private void buildMetric() {
        if (metric == null) {
            Set methods = new TreeSet();
            Set referencedMethods = new TreeSet();
            Iterator iter = classMetrics.getMethods().iterator();
            while (iter.hasNext()) {
                MethodMetrics mm = (MethodMetrics)iter.next();
                if (mm.isSynthetic())  // skip synthetic methods
                    continue;
                methods.add(mm.getFullName());
                Iterator iter2 = mm.getMethodReferences();
                while (iter2.hasNext()) {
                    String ref = (String)iter2.next();
                    referencedMethods.add(ref);
                }
            }
            metric = new Integer(methods.size() + referencedMethods.size());
            
            StringBuffer sb = new StringBuffer();
            sb.append(MetricsNode.bundle.getString ("STR_ClassMethods"));
            iter = methods.iterator();
            while (iter.hasNext()) {
                sb.append("\n   ");
                sb.append((String)iter.next());
            }
            sb.append("\n");
            sb.append(MetricsNode.bundle.getString ("STR_ReferencedMethods"));
            iter = referencedMethods.iterator();
            while (iter.hasNext()) {
                sb.append("\n   ");
                sb.append((String)iter.next());
            }
            details = sb.toString();
        }
    }
    
    public Integer getMetricValue() {
        buildMetric();
        return metric;
    }
    
    public String getDetails() {
        buildMetric();
        return details;
    }

    public boolean needsOtherClasses() {
        return false;
    }

    public boolean isMethodMetric() {
	return true;
    }

    public Integer getMetricValue(MethodMetrics mm) throws NoSuchMetricException {
	return new Integer(mm.getMethodReferencesCount());
    }

    /**
     * Actually a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new RFCMetric(cm);
	}
	public Class getMetricClass() {
	    return RFCMetric.class;
	}
    }
}
