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

package org.netbeans.modules.metrics.options;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

/** Options for DIT metric.
 *
 * @author  tball
 */
public class DITMetricSettings extends SystemOption implements MetricSettings {

    private static final long serialVersionUID = -7928276308936873905L;

    public static final String PROP_DEFAULT_WARNING = "DITMetric.default_warning_value";
    public static final String PROP_DEFAULT_ERROR = "DITMetric.default_error_value";

    /** Singleton instance */
    private static DITMetricSettings singleton;

    protected void initialize () {
        super.initialize ();
        setWarningLevel(5);
        setErrorLevel(10);
    }

    public String displayName () {
        return NbBundle.getMessage(DITMetricSettings.class, "LBL_DITMetricSettings");
    }

    /** Default instance of this system option. */
    public static DITMetricSettings getDefault() {
        if (singleton == null) {
            singleton = (DITMetricSettings) 
                findObject(DITMetricSettings.class, true);
        }
        return singleton;
    }

    public int getWarningLevel () {
        return ((Integer)getProperty(PROP_DEFAULT_WARNING)).intValue();
    }

    public void setWarningLevel (int value) {
        // Automatically fires property changes if needed etc.:
        putProperty (PROP_DEFAULT_WARNING, new Integer(value), true);
    }

    public int getErrorLevel () {
        return ((Integer)getProperty(PROP_DEFAULT_ERROR)).intValue();
    }

    public void setErrorLevel (int value) {
        putProperty (PROP_DEFAULT_ERROR, new Integer(value), true);
    }

    // this metric doesn't support per-method values
    public int getMethodWarningLevel() { return -1; }
    public void setMethodWarningLevel(int value) {}
    public int getMethodErrorLevel() { return -1; }
    public void setMethodErrorLevel(int value) {}

}
