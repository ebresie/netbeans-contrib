/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.blueprints.catalog;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Bpcatalog;
import org.xml.sax.SAXException;

/**
 * Singleton for accessing the solutions catalog.
 *
 * @author Mark Roth
 */
public class SolutionsCatalog {
    
    private static SolutionsCatalog theInstance = null;
    private static final String BPCATALOG_XML_PATH =
        "/org/netbeans/modules/j2ee/blueprints/bpcatalog.xml"; // NOI18N
    
    /** Parsed representation of bpcatalog.xml */
    private Bpcatalog bpcatalogXml = null;
    
    /** Private Singleton constructor */
    private SolutionsCatalog() {}

    public static SolutionsCatalog getInstance() {
        if(theInstance == null) {
            createInstance();
        }
        return theInstance;
    }
    
    private synchronized static void createInstance() {
        if(theInstance == null) {
            theInstance = new SolutionsCatalog();
        }
    }
    
    public Bpcatalog getBpcatalogXml() {
        if(bpcatalogXml == null) {
            // Lazily parse bpcatalog.xml
            parseBpcatalogXml();
        }
        return bpcatalogXml;
    }
    
    private synchronized void parseBpcatalogXml() {
        if(this.bpcatalogXml == null) {
            this.bpcatalogXml = new Bpcatalog();
            try {
                InputStream in = getClass().getResourceAsStream(BPCATALOG_XML_PATH);
                this.bpcatalogXml = Bpcatalog.read(in);
                in.close();
            }
            catch(ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            catch(SAXException e) {
                throw new RuntimeException(e);
            }
            catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
