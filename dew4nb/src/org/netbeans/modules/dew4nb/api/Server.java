/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.dew4nb.JavacEndpoint;
import org.netbeans.modules.dew4nb.Status;
import org.openide.util.Exceptions;
import org.openide.util.Pair;
import org.openide.util.Parameters;
import org.openide.util.Union2;

/**
 *
 * @author Jaroslav Tulach
 * @author Tomas Zezula
 */
public final class Server {

    private final HttpServer http;

    private Server(
        @NonNull final Union2<Integer,Pair<Integer,Integer>> portCfg,
        @NonNull final Collection<Pair<File,Pair<String,Boolean>>> staticRoots) {
        Parameters.notNull("portCfg", portCfg); //NOI18N
        Parameters.notNull("staticRoots", staticRoots); //NOI18N
        http = portCfg.hasFirst() ?
            HttpServer.createSimpleServer(null, portCfg.first()) :
            HttpServer.createSimpleServer(null, new PortRange(
                portCfg.second().first(),
                portCfg.second().second()));
        for (Pair<File,Pair<String,Boolean>> staticRoot : staticRoots) {
            final StaticHttpHandler sh = new StaticHttpHandler(staticRoot.first().getAbsolutePath());
            sh.setFileCacheEnabled(staticRoot.second().second());
            http.getServerConfiguration().addHttpHandler(sh, staticRoot.second().first());
        }
        final WebSocketAddOn addon = new WebSocketAddOn();
        for (NetworkListener listener : http.getListeners()) {
            listener.registerAddOn(addon);
        }
        WebSocketEngine.getEngine().register("", "/javac", new JavacApplication());
    }

    public void start () throws IOException {
        http.start();
    }

    public void stop () {
        http.stop();
    }

    /*test*/
    public int getPort() {
        return http.getListeners().iterator().next().getPort();
    }

    @NonNull
    public static Builder createBuilder() {
        return new Builder();
    }
    
    public static final class Builder {

        private final Collection<Pair<File,Pair<String,Boolean>>> staticRoots;
        private Union2<Integer,Pair<Integer,Integer>> portCfg;

        private Builder() {
            this.staticRoots = new ArrayList<>();
        }

        @NonNull
        public Builder setPort(int port) {
            if (portCfg != null) {
                if (portCfg.hasFirst()) {
                    throw new IllegalStateException("Port already configured to: " + portCfg.first());  //NOI18N
                } else {
                    throw new IllegalStateException("Port range already configured to: " + portCfg.second());   //NOI18N
                }
            }
            this.portCfg = Union2.createFirst(port);
            return this;
        }

        @NonNull
        public Builder setPortRange(int from, int to) {
            if (portCfg != null) {
                if (portCfg.hasFirst()) {
                    throw new IllegalStateException("Port already configured to: " + portCfg.first());  //NOI18N
                } else {
                    throw new IllegalStateException("Port range already configured to: " + portCfg.second());   //NOI18N
                }
            }
            this.portCfg = Union2.createSecond(Pair.of(from, to));
            return this;
        }

        @NonNull
        public Builder addStaticHttpRoot(@NonNull final File root, final boolean cache) {
            return addStaticHttpRoot(root, cache, "/");   //NOI18N
        }

        @NonNull
        public Builder addStaticHttpRoot(
                @NonNull final File root,
                final boolean  cache,
                @NonNull String mapping) {
            Parameters.notNull("root", root);   //NOI18N
            Parameters.notNull("mapping", mapping); //NOI18N
            this.staticRoots.add(Pair.of(root, Pair.of(mapping, cache)));
            return this;
        }        

        @NonNull
        public Server build() {
            return new Server(portCfg, staticRoots);
        }
    }

     private static class JavacApplication extends WebSocketApplication {
        private final JavacEndpoint endpoint;

        JavacApplication() {
            this.endpoint = JavacEndpoint.newCompiler();
        }

        @Override
        public void onMessage(WebSocket socket, String text) {
            try {
                socket.send(endpoint.doCompile(text).toString());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                socket.send(endpoint.error(
                    Status.runtime_error,
                    ex.getMessage(),
                    null).toString());
            }
        }
    }
}
