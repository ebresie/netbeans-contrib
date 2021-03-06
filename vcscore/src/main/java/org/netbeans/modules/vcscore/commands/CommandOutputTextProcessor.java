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

package org.netbeans.modules.vcscore.commands;

import java.util.Hashtable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.openide.util.RequestProcessor;

/**
 * The processor of text output of a command.
 * This class takes care about passing the command output to a text area.<p>
 * The difference from simple JTextArea.append(...) call is that this class assures
 * that the append is performed in AWT thread, takes care not to make AWT thread
 * too busy and can buffer the output till the actual text area is created.
 * Also when the text is too long, it's cut to prevent OutOfMemoryError.
 *
 * @author  Martin Entlicher
 */
public class CommandOutputTextProcessor {
    
    private static final int MAX_BUFFER_SIZE = 3000*80;
    /** When both the buffer and the text area are full, replace only this part
     * of the buffer */
    private static final int FAST_APPEND_SIZE = 100*80;
    /** The maximum number of characters to keep in the text area */
    private static final int MAX_AREA_SIZE = MAX_BUFFER_SIZE - FAST_APPEND_SIZE;
    
    private static CommandOutputTextProcessor instance;
    
    private RequestProcessor outputDisplayRequestProcessor;
    private Hashtable outputDisplayStuff;

    /** Creates a new instance of CommandOutputTextProcessor */
    private CommandOutputTextProcessor() {
        outputDisplayRequestProcessor = new RequestProcessor("Output Display Request Processor"); // NOI18N
        outputDisplayRequestProcessor.post(new OutputDisplayer());
    }
    
    /**
     * Get the default instance of CommandOutputTextProcessor.
     */
    public static synchronized CommandOutputTextProcessor getDefault() {
        if (instance == null) {
            instance = new CommandOutputTextProcessor();
        }
        return instance;
    }
    
    /**
     * Create a new text output. Every text output is associated with one text area.
     */
    public TextOutput createOutput() {
        return new TextOutput();
    }
    
    /**
     * The text output.
     */
    public final class TextOutput {
        
        private StringBuffer buffer;
        private JTextArea area;
        
        TextOutput() {
            buffer = new StringBuffer();
        }
        
        /**
         * Set the text area that is used to display the text ouput.
         * Until the area is set, the output is buffered.
         */
        public void setTextArea(JTextArea area) {
            this.area = area;
            synchronized (buffer) {
                if (buffer.length() > 0) {
                    synchronized (outputDisplayStuff) {
                        outputDisplayStuff.put(area, buffer);
                        if (outputDisplayStuff.size() == 1) {
                            outputDisplayStuff.notify(); // it was empty before!
                        }
                    }
                }
            }
        }
        
        /**
         * Add the text to the text area (or to an internal buffer if the area
         * is not set yet.
         */
        public void addText(String text) {
            synchronized (buffer) {
                buffer.append(text);
            }
            if (area != null) {
                synchronized (outputDisplayStuff) {
                    outputDisplayStuff.put(area, buffer);
                    if (outputDisplayStuff.size() == 1) {
                        outputDisplayStuff.notify(); // it was empty before!
                    }
                }
            } else {
                synchronized (buffer) {
                    if (buffer.length() > MAX_BUFFER_SIZE) {
                        buffer.delete(0, buffer.length() - MAX_AREA_SIZE  - 1);
                    }
                }
            }
        }
        
    }
    
    private final class OutputDisplayer extends Object implements Runnable {
        
        private java.util.Random random;
        
        public OutputDisplayer() {
            outputDisplayStuff = new Hashtable();
            random = new java.util.Random();
        }
        
        public void run() {
            if (SwingUtilities.isEventDispatchThread()) {
                javax.swing.JTextArea area;
                String append;
                String replace;
                int start;
                int end;
                synchronized (outputDisplayStuff) {
                    int index = random.nextInt(outputDisplayStuff.size());
                    java.util.Enumeration keysEnum = outputDisplayStuff.keys();
                    do {
                        area = (javax.swing.JTextArea) keysEnum.nextElement();
                    } while (--index >= 0);
                    end = area.getDocument().getLength();
                    StringBuffer buffer = (StringBuffer) outputDisplayStuff.get(area);
                    synchronized (buffer) {
                        if (buffer.length() >= MAX_AREA_SIZE) {
                            append = null;
                            replace = buffer.substring(buffer.length() - FAST_APPEND_SIZE, buffer.length()).toString();
                            buffer.delete(0, replace.length());
                            start = end - replace.length();
                            if (start < 0) start = 0;
                        } else {
                            buffer = (StringBuffer) outputDisplayStuff.remove(area);
                            append = buffer.toString();
                            buffer.delete(0, buffer.length());
                            start = 0;
                            end += append.length();
                            if (end < MAX_AREA_SIZE) end = 0;
                            else end = end - MAX_AREA_SIZE + FAST_APPEND_SIZE;
                            replace = null;
                        }
                    }
                }
                if (append != null) {
                    area.append(append);
                }
                if (end > 0) {
                    area.replaceRange(replace, start, end);
                }
            } else {
                do {
                    synchronized (outputDisplayStuff) {
                        if (outputDisplayStuff.size() == 0) {
                            try {
                                outputDisplayStuff.wait();
                            } catch (InterruptedException iexc) {
                                break;
                            }
                        }
                    }
                    do {
                        try {
                            SwingUtilities.invokeAndWait(this);
                            // Let the AWT to catch it's breath
                            Thread.currentThread().yield();
                            Thread.currentThread().sleep(250);
                        } catch (InterruptedException iexc) {
                            break;
                        } catch (java.lang.reflect.InvocationTargetException itexc) {
                            org.openide.ErrorManager.getDefault().notify(itexc);
                            break;
                        }
                    } while (outputDisplayStuff.size() > 0);
                } while (true);
            }
        }
    }
}
