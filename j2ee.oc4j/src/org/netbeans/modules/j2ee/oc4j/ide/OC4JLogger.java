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
package org.netbeans.modules.j2ee.oc4j.ide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.openide.ErrorManager;
import org.openide.util.RequestProcessor;
import org.openide.windows.InputOutput;

/**
 * This class is capable of tailing the specified file or input stream. It
 * checks for changes at the specified intervals and outputs the changes to
 * the given I/O panel in NetBeans
 *
 * @author  Michal Mocnak
 */
public class OC4JLogger {
    
    /**
     * Amount of time in milliseconds to wait between checks of the input
     * stream
     */
    private static final int delay = 1000;
    
    /**
     * The I/O window where to output the changes
     */
    private InputOutput io;
    
    /**
     * Creates and starts a new instance of OC4JLogger
     *
     * @param file the file for which to track changes
     * @param ioPanelName the I/O window where to output the changes
     */
    public OC4JLogger(File[] files, String uri) {
        this(getInputStreamsFromFiles(files), uri);
    }
    
    /**
     * Creates and starts a new instance of OC4JLogger
     *
     * @param file the input stream for which to track changes
     * @param ioPanelName the I/O window where to output the changes
     */
    public OC4JLogger(InputStream[] inputStreams, String uri) {
        io = UISupport.getServerIO(uri);
        
        if (io == null) {
            return; // finish, it looks like this server instance has been unregistered
        }
        
        // clear the old output
        try {
            io.getOut().reset();
        } catch (IOException ioe) {
            // no op
        }
        
        io.select();
        
        for(InputStream inputStream : inputStreams) {
            RequestProcessor.getDefault().post(new OC4JLoggerRunnable(inputStream));
        }
    }
    
    public synchronized void write(String s) {
        io.getOut().println(s);
    }
    
    public synchronized void selectIO() {
        io.select();
    }
    
    private static InputStream[] getInputStreamsFromFiles(File[] files) {
        InputStream[] inputStreams = new InputStream[files.length];
        
        try {
            for(int i=0 ; i<files.length ; i++)
                inputStreams[i] = new FileInputStream(files[i]);
        } catch(FileNotFoundException ex) {
            return new InputStream[] {};
        }
        
        return inputStreams;
    }
    
    private class OC4JLoggerRunnable implements Runnable {
        
        private InputStream inputStream;
        
        public OC4JLoggerRunnable(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        
        /**
         * Implementation of the Runnable interface. Here all tailing is
         * performed
         */
        public void run() {
            try {
                // create a reader from the input stream
                InputStreamReader reader = new InputStreamReader(inputStream);
                
                // read from the input stream and put all the changes to the
                // I/O window
                char[] chars = new char[1024];
                while (true) {
                    // while there is something in the stream to be read - read that
                    while (reader.ready()) {
                        write(new String(chars, 0, reader.read(chars)));
                        selectIO();
                    }
                    
                    // when the stream is empty - sleep for a while
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
            } finally {
                // close the opened stream
                try {
                    inputStream.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
                }
            }
        }
    }
}