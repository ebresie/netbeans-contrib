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

package org.netbeans.modules.erlang.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.erlang.makeproject.spi.support.RakeBasedProjectType;
import org.netbeans.modules.erlang.makeproject.spi.support.RakeProjectHelper;

/**
 * Factory for simple Ruby projects.
 * @author Jesse Glick
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.erlang.makeproject.spi.support.RakeBasedProjectType.class)
public final class RubyProjectType implements RakeBasedProjectType {

    public static final String TYPE = "org.netbeans.modules.erlang.project"; // NOI18N
    private static final String PROJECT_CONFIGURATION_NAME = "data"; // NOI18N
    public static final String PROJECT_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/erlang-project/1"; // NOI18N
    private static final String PRIVATE_CONFIGURATION_NAME = "data"; // NOI18N
    private static final String PRIVATE_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/erlang-project-private/1"; // NOI18N
    
    /** Do nothing, just a service. */
    public RubyProjectType() {}
    
    public String getType() {
        return TYPE;
    }
    
    public Project createProject(RakeProjectHelper helper) throws IOException {
        return new RubyProject(helper);
    }

    public String getPrimaryConfigurationDataElementName(boolean shared) {
        return shared ? PROJECT_CONFIGURATION_NAME : PRIVATE_CONFIGURATION_NAME;
    }
    
    public String getPrimaryConfigurationDataElementNamespace(boolean shared) {
        return shared ? PROJECT_CONFIGURATION_NAMESPACE : PRIVATE_CONFIGURATION_NAMESPACE;
    }
    
}
