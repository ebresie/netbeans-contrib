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

package org.netbeans.modules.javafx.project.ui.customizer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.modules.javafx.project.JavaFXProject;
import org.netbeans.modules.javafx.project.JavaFXProjectUtil;
import org.netbeans.modules.javafx.project.SourceRoots;
import org.netbeans.modules.javafx.project.UpdateHelper;
import org.netbeans.modules.javafx.project.classpath.ClassPathSupport;
import org.netbeans.spi.java.project.support.ui.IncludeExcludeVisualizer;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.support.ant.ui.StoreGroup;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * @author Petr Hrebejk
 */
public class JavaFXProjectProperties {
    
    //Hotfix of the issue #70058
    //Should be removed when the StoreGroup SPI will be extended to allow false default value in ToggleButtonModel
    private static final Integer BOOLEAN_KIND_TF = new Integer( 0 );
    private static final Integer BOOLEAN_KIND_YN = new Integer( 1 );
    private static final Integer BOOLEAN_KIND_ED = new Integer( 2 );
    private Integer javacDebugBooleanKind;
    private Integer javadocPreviewBooleanKind;
    
    // Special properties of the project
    public static final String JAVAFX_PROJECT_NAME = "javafx.project.name"; // NOI18N
    public static final String JAVA_PLATFORM = "platform.active"; // NOI18N
    
    public static final String FX_RUN_CLASS_NAME 
                = "net.java.javafx.FXShell";           // NOI18N
    public static final String FX_BUILD_CLASS_NAME 
                = "javafx.netbeans.fxuserlib.FXBuild";           // NOI18N
    public static final String FX_LIBS      = "libs.JavaFXUserLib.classpath"; //NOI18N
    
    // Properties stored in the PROJECT.PROPERTIES    
    public static final String DIST_DIR = "dist.dir"; // NOI18N
    public static final String DIST_JAR = "dist.jar"; // NOI18N
    public static final String JAVAC_CLASSPATH = "javac.classpath"; // NOI18N
    public static final String RUN_CLASSPATH = "run.classpath"; // NOI18N
    public static final String RUN_JVM_ARGS = "run.jvmargs"; // NOI18N
    public static final String RUN_WORK_DIR = "work.dir"; // NOI18N
    public static final String DEBUG_CLASSPATH = "debug.classpath"; // NOI18N
    public static final String JAR_COMPRESS = "jar.compress"; // NOI18N
    public static final String MAIN_CLASS = "main.class"; // NOI18N
    public static final String MAIN_FX_RUN_CLASS = "main.fx.class"; // NOI18M
    public static final String MAIN_FX_BUILD_CLASS = "FXBuild.class"; // NOI18M
    public static final String JAVAC_SOURCE = "javac.source"; // NOI18N
    public static final String JAVAC_TARGET = "javac.target"; // NOI18N
    public static final String JAVAC_TEST_CLASSPATH = "javac.test.classpath"; // NOI18N
    public static final String JAVAC_DEBUG = "javac.debug"; // NOI18N
    public static final String JAVAC_DEPRECATION = "javac.deprecation"; // NOI18N
    public static final String JAVAC_COMPILER_ARG = "javac.compilerargs";    //NOI18N
    public static final String RUN_TEST_CLASSPATH = "run.test.classpath"; // NOI18N
    public static final String BUILD_CLASSPATH = "build.classpath"; // NOI18N
    public static final String BUILD_DIR = "build.dir"; // NOI18N
    public static final String BUILD_CLASSES_DIR = "build.classes.dir"; // NOI18N
    public static final String BUILD_TEST_CLASSES_DIR = "build.test.classes.dir"; // NOI18N
    public static final String BUILD_TEST_RESULTS_DIR = "build.test.results.dir"; // NOI18N
    public static final String BUILD_CLASSES_EXCLUDES = "build.classes.excludes"; // NOI18N
    public static final String DIST_JAVADOC_DIR = "dist.javadoc.dir"; // NOI18N
    public static final String NO_DEPENDENCIES="no.dependencies"; // NOI18N
    public static final String DEBUG_TEST_CLASSPATH = "debug.test.classpath"; // NOI18N
    public static final String SOURCE_ENCODING="source.encoding"; // NOI18N
    /** @since org.netbeans.modules.javafx.project/1 1.11 */
    public static final String INCLUDES = "includes"; // NOI18N
    /** @since org.netbeans.modules.javafx.project/1 1.11 */
    public static final String EXCLUDES = "excludes"; // NOI18N
    
    public static final String JAVADOC_PRIVATE="javadoc.private"; // NOI18N
    public static final String JAVADOC_NO_TREE="javadoc.notree"; // NOI18N
    public static final String JAVADOC_USE="javadoc.use"; // NOI18N
    public static final String JAVADOC_NO_NAVBAR="javadoc.nonavbar"; // NOI18N
    public static final String JAVADOC_NO_INDEX="javadoc.noindex"; // NOI18N
    public static final String JAVADOC_SPLIT_INDEX="javadoc.splitindex"; // NOI18N
    public static final String JAVADOC_AUTHOR="javadoc.author"; // NOI18N
    public static final String JAVADOC_VERSION="javadoc.version"; // NOI18N
    public static final String JAVADOC_WINDOW_TITLE="javadoc.windowtitle"; // NOI18N
    public static final String JAVADOC_ENCODING="javadoc.encoding"; // NOI18N
    public static final String JAVADOC_ADDITIONALPARAM="javadoc.additionalparam"; // NOI18N
    
    public static final String APPLICATION_TITLE ="application.title"; // NOI18N
    public static final String APPLICATION_VENDOR ="application.vendor"; // NOI18N
    public static final String APPLICATION_DESC ="application.desc"; // NOI18N
    public static final String APPLICATION_HOMEPAGE ="application.homepage"; // NOI18N
    public static final String APPLICATION_SPLASH ="application.splash"; // NOI18N
/*    
    public static final String WEBSTART_ENABLE = "webstart.enable"; // NOI18N
    public static final String WEBSTART_ICON = "webstart.icon"; // NOI18N
    public static final String WEBSTART_CODEBASE = "webstart.codebase"; //NOI18N
    public static final String WEBSTART_DIST = "webstart.dist"; //NOI18N
    public static final String WEBSTART_NO_OFFLINE = "webstart.nooffline"; //NOI18N
    public static final String WEBSTART_SELFSIGNED = "webstart.selfsigned"; //NOI18N
*/    
    // Properties stored in the PRIVATE.PROPERTIES
    public static final String APPLICATION_ARGS = "application.args"; // NOI18N
    public static final String JAVADOC_PREVIEW="javadoc.preview"; // NOI18N

    
    // Well known paths
    public static final String[] WELL_KNOWN_PATHS = new String[] {            
            "${" + JAVAC_CLASSPATH + "}", 
            "${" + JAVAC_TEST_CLASSPATH  + "}", 
            "${" + RUN_CLASSPATH  + "}", 
            "${" + RUN_TEST_CLASSPATH  + "}", 
            "${" + BUILD_CLASSES_DIR  + "}", 
            "${" + BUILD_TEST_CLASSES_DIR  + "}", 
    };
    
    // Prefixes and suffixes of classpath
    public static final String LIBRARY_PREFIX = "${libs."; // NOI18N
    public static final String LIBRARY_SUFFIX = ".classpath}"; // NOI18N
    // XXX looks like there is some kind of API missing in ReferenceHelper?
    public static final String ANT_ARTIFACT_PREFIX = "${reference."; // NOI18N

    ClassPathSupport cs;
    
    
    // SOURCE ROOTS
    // public static final String SOURCE_ROOTS = "__virtual_source_roots__";   //NOI18N
    // public static final String TEST_ROOTS = "__virtual_test_roots__"; // NOI18N
                        
    // MODELS FOR VISUAL CONTROLS
    
    // CustomizerSources
    DefaultTableModel SOURCE_ROOTS_MODEL;
    DefaultTableModel TEST_ROOTS_MODEL;
    ComboBoxModel JAVAC_SOURCE_MODEL;
     
    // CustomizerLibraries
    DefaultListModel JAVAC_CLASSPATH_MODEL;
    DefaultListModel JAVAC_TEST_CLASSPATH_MODEL;
    DefaultListModel RUN_CLASSPATH_MODEL;
    DefaultListModel RUN_TEST_CLASSPATH_MODEL;
    ComboBoxModel PLATFORM_MODEL;
    ListCellRenderer CLASS_PATH_LIST_RENDERER;
    ListCellRenderer PLATFORM_LIST_RENDERER;
    ListCellRenderer JAVAC_SOURCE_RENDERER;
    
    // CustomizerCompile
    ButtonModel JAVAC_DEPRECATION_MODEL; 
    ButtonModel JAVAC_DEBUG_MODEL;
    ButtonModel NO_DEPENDENCIES_MODEL;
    Document JAVAC_COMPILER_ARG_MODEL;
    
    // CustomizerCompileTest
                
    // CustomizerJar
    Document DIST_JAR_MODEL; 
    Document BUILD_CLASSES_EXCLUDES_MODEL; 
    ButtonModel JAR_COMPRESS_MODEL;
                
    // CustomizerJavadoc
    ButtonModel JAVADOC_PRIVATE_MODEL;
    ButtonModel JAVADOC_NO_TREE_MODEL;
    ButtonModel JAVADOC_USE_MODEL;
    ButtonModel JAVADOC_NO_NAVBAR_MODEL; 
    ButtonModel JAVADOC_NO_INDEX_MODEL; 
    ButtonModel JAVADOC_SPLIT_INDEX_MODEL; 
    ButtonModel JAVADOC_AUTHOR_MODEL; 
    ButtonModel JAVADOC_VERSION_MODEL;
    Document JAVADOC_WINDOW_TITLE_MODEL;
    ButtonModel JAVADOC_PREVIEW_MODEL; 
    Document JAVADOC_ADDITIONALPARAM_MODEL;

    // CustomizerRun
    Map<String/*|null*/,Map<String,String/*|null*/>/*|null*/> RUN_CONFIGS;
    String activeConfig;
    
    // CustomizerApplication
    Document APPLICATION_TITLE_DOC;
    Document APPLICATION_VENDOR_DOC;
    Document APPLICATION_DESC_DOC;
    Document APPLICATION_HOMEPAGE_DOC;
    Document APPLICATION_SPLASH_DOC;
    
    // CustomizerWebStart
    /*
    ButtonModel WEBSTART_ENABLE_MODEL;
    Document WEBSTART_ICON_MODEL;
    ComboBoxModel WEBSTART_CODEBASE_MODEL;
    Document WEBSTART_DIST_MODEL;
    ButtonModel WEBSTART_NO_OFFLINE_MODEL;
    ButtonModel WEBSTART_SELFSIGNED_MODEL;
    */
    // CustomizerRunTest

    // Private fields ----------------------------------------------------------    
    private JavaFXProject project;
    private UpdateHelper updateHelper;
    private PropertyEvaluator evaluator;
    private ReferenceHelper refHelper;
    private GeneratedFilesHelper genFileHelper;
    
    private StoreGroup privateGroup; 
    private StoreGroup projectGroup;
    
    private Map<String,String> additionalProperties;

    private String includes, excludes;
    
    private WebStartProjectProperties webStartProjectProperties;
    
    JavaFXProject getProject() {
        return project;
    }
    
    /** Creates a new instance of JavaFXUIProperties and initializes them */
    public JavaFXProjectProperties( JavaFXProject project, 
            UpdateHelper updateHelper, PropertyEvaluator evaluator, 
            ReferenceHelper refHelper, GeneratedFilesHelper genFileHelper) {
        this.project = project;
        this.updateHelper  = updateHelper;
        this.evaluator = evaluator;
        this.refHelper = refHelper;
        this.genFileHelper = genFileHelper;
        this.cs = new ClassPathSupport( evaluator, refHelper, updateHelper.getAntProjectHelper(), WELL_KNOWN_PATHS, LIBRARY_PREFIX, LIBRARY_SUFFIX, ANT_ARTIFACT_PREFIX );
                
        privateGroup = new StoreGroup();
        projectGroup = new StoreGroup();
        
        additionalProperties = new HashMap<String,String>();
        
        init(); // Load known properties     
        this.webStartProjectProperties = new WebStartProjectProperties(project, evaluator);
    }

    /** Initializes the visual models 
     */
    private void init() {
        
        CLASS_PATH_LIST_RENDERER = new JavaFXClassPathUi.ClassPathListCellRenderer( evaluator );
        
        // CustomizerSources
        SOURCE_ROOTS_MODEL = JavaFXSourceRootsUi.createModel( project.getSourceRoots() );
        TEST_ROOTS_MODEL = JavaFXSourceRootsUi.createModel( project.getTestSourceRoots() );        
        includes = evaluator.getProperty(INCLUDES);
        if (includes == null) {
            includes = "**"; // NOI18N
        }
        excludes = evaluator.getProperty(EXCLUDES);
        if (excludes == null) {
            excludes = ""; // NOI18N
        }
        
        // CustomizerLibraries
        EditableProperties projectProperties = updateHelper.getProperties( AntProjectHelper.PROJECT_PROPERTIES_PATH );                
        
        JAVAC_CLASSPATH_MODEL = ClassPathUiSupport.createListModel( cs.itemsIterator( (String)projectProperties.get( JAVAC_CLASSPATH )  ) );
        JAVAC_TEST_CLASSPATH_MODEL = ClassPathUiSupport.createListModel( cs.itemsIterator( (String)projectProperties.get( JAVAC_TEST_CLASSPATH ) ) );
        RUN_CLASSPATH_MODEL = ClassPathUiSupport.createListModel( cs.itemsIterator( (String)projectProperties.get( RUN_CLASSPATH ) ) );
        RUN_TEST_CLASSPATH_MODEL = ClassPathUiSupport.createListModel( cs.itemsIterator( (String)projectProperties.get( RUN_TEST_CLASSPATH ) ) );
        PLATFORM_MODEL = PlatformUiSupport.createPlatformComboBoxModel (evaluator.getProperty(JAVA_PLATFORM));
        PLATFORM_LIST_RENDERER = PlatformUiSupport.createPlatformListCellRenderer();
        JAVAC_SOURCE_MODEL = PlatformUiSupport.createSourceLevelComboBoxModel (PLATFORM_MODEL, evaluator.getProperty(JAVAC_SOURCE));
        JAVAC_SOURCE_RENDERER = PlatformUiSupport.createSourceLevelListCellRenderer ();
                
        // CustomizerCompile
        JAVAC_DEPRECATION_MODEL = projectGroup.createToggleButtonModel( evaluator, JAVAC_DEPRECATION );
                
        //Hotfix of the issue #70058
        //Should use the StoreGroup when the StoreGroup SPI will be extended to allow false default value in ToggleButtonModel
        Integer[] kind = new Integer[1];
        JAVAC_DEBUG_MODEL = createToggleButtonModel( evaluator, JAVAC_DEBUG, kind);
        javacDebugBooleanKind = kind[0];
        
        NO_DEPENDENCIES_MODEL = projectGroup.createInverseToggleButtonModel( evaluator, NO_DEPENDENCIES );
        JAVAC_COMPILER_ARG_MODEL = projectGroup.createStringDocument( evaluator, JAVAC_COMPILER_ARG );
        
        // CustomizerJar
        DIST_JAR_MODEL = projectGroup.createStringDocument( evaluator, DIST_JAR );
        BUILD_CLASSES_EXCLUDES_MODEL = projectGroup.createStringDocument( evaluator, BUILD_CLASSES_EXCLUDES );
        JAR_COMPRESS_MODEL = projectGroup.createToggleButtonModel( evaluator, JAR_COMPRESS );
        
        // CustomizerJavadoc
        JAVADOC_PRIVATE_MODEL = projectGroup.createToggleButtonModel( evaluator, JAVADOC_PRIVATE );
        JAVADOC_NO_TREE_MODEL = projectGroup.createInverseToggleButtonModel( evaluator, JAVADOC_NO_TREE );
        JAVADOC_USE_MODEL = projectGroup.createToggleButtonModel( evaluator, JAVADOC_USE );
        JAVADOC_NO_NAVBAR_MODEL = projectGroup.createInverseToggleButtonModel( evaluator, JAVADOC_NO_NAVBAR );
        JAVADOC_NO_INDEX_MODEL = projectGroup.createInverseToggleButtonModel( evaluator, JAVADOC_NO_INDEX ); 
        JAVADOC_SPLIT_INDEX_MODEL = projectGroup.createToggleButtonModel( evaluator, JAVADOC_SPLIT_INDEX );
        JAVADOC_AUTHOR_MODEL = projectGroup.createToggleButtonModel( evaluator, JAVADOC_AUTHOR );
        JAVADOC_VERSION_MODEL = projectGroup.createToggleButtonModel( evaluator, JAVADOC_VERSION );
        JAVADOC_WINDOW_TITLE_MODEL = projectGroup.createStringDocument( evaluator, JAVADOC_WINDOW_TITLE );
        //Hotfix of the issue #70058
        //Should use the StoreGroup when the StoreGroup SPI will be extended to allow false default value in ToggleButtonModel        
        JAVADOC_PREVIEW_MODEL = createToggleButtonModel ( evaluator, JAVADOC_PREVIEW, kind);
        javadocPreviewBooleanKind = kind[0];
        
        JAVADOC_ADDITIONALPARAM_MODEL = projectGroup.createStringDocument( evaluator, JAVADOC_ADDITIONALPARAM );
        
        // CustomizerApplication
        APPLICATION_TITLE_DOC = projectGroup.createStringDocument(evaluator, APPLICATION_TITLE);
        String title = evaluator.getProperty("application.title");
        if (title == null) {
            try {
                APPLICATION_TITLE_DOC.insertString(0, ProjectUtils.getInformation(project).getDisplayName(), null);
            } catch (BadLocationException ex) {
                // just do not set anything
            }
        }
        APPLICATION_VENDOR_DOC = projectGroup.createStringDocument(evaluator, APPLICATION_VENDOR);
        String vendor = evaluator.getProperty("application.vendor");
        if (vendor == null) {
            try {
                APPLICATION_VENDOR_DOC.insertString(0, System.getProperty("user.name", "User Name"), null);
            } catch (BadLocationException ex) {
                // just do not set anything
            }
        }
        APPLICATION_DESC_DOC = projectGroup.createStringDocument(evaluator, APPLICATION_DESC);
        APPLICATION_HOMEPAGE_DOC = projectGroup.createStringDocument(evaluator, APPLICATION_HOMEPAGE);
        APPLICATION_SPLASH_DOC = projectGroup.createStringDocument(evaluator, APPLICATION_SPLASH);
        /*
        // CustomizerWebStart
        WEBSTART_ENABLE_MODEL = projectGroup.createToggleButtonModel(evaluator, WEBSTART_ENABLE);
        WEBSTART_ICON_MODEL = projectGroup.createStringDocument(evaluator, WEBSTART_ICON);
        WEBSTART_CODEBASE_MODEL = WebStartUISupport.createCodebaseComboBoxModel();
        WEBSTART_DIST_MODEL = projectGroup.createStringDocument(evaluator, WEBSTART_DIST);
        WEBSTART_NO_OFFLINE_MODEL = projectGroup.createInverseToggleButtonModel(evaluator, WEBSTART_NO_OFFLINE);
        WEBSTART_SELFSIGNED_MODEL = projectGroup.createToggleButtonModel(evaluator, WEBSTART_SELFSIGNED);
        */
        // CustomizerRun
        RUN_CONFIGS = readRunConfigs();
        activeConfig = evaluator.getProperty("config");
                
    }
    
    public void save() {
        try {                        
            // Store properties 
            boolean result = ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Boolean>() {
                final FileObject projectDir = updateHelper.getAntProjectHelper().getProjectDirectory();
                public Boolean run() throws IOException {
                    if ((genFileHelper.getBuildScriptState(GeneratedFilesHelper.BUILD_IMPL_XML_PATH,
                        JavaFXProject.class.getResource("resources/build-impl.xsl")) //NOI18N
                        & GeneratedFilesHelper.FLAG_MODIFIED) == GeneratedFilesHelper.FLAG_MODIFIED) {  //NOI18N
                        if (showModifiedMessage (NbBundle.getMessage(JavaFXProjectProperties.class,"TXT_ModifiedTitle"))) {
                            //Delete user modified build-impl.xml
                            FileObject fo = projectDir.getFileObject(GeneratedFilesHelper.BUILD_IMPL_XML_PATH);
                            if (fo != null) {
                                fo.delete();
                            }
                        }
                        else {
                            return false;
                        }
                    }
                    storeProperties();
                    return true;
                }
            });
            // and save the project
            if (result) {
                ProjectManager.getDefault().saveProject(project);
            }
        } 
        catch (MutexException e) {
            ErrorManager.getDefault().notify((IOException)e.getException());
        }
        catch ( IOException ex ) {
            ErrorManager.getDefault().notify( ex );
        }
    }
    
    
        
    private void storeProperties() throws IOException {
        // Store special properties
        
        // Modify the project dependencies properly        
        resolveProjectDependencies();
        
        // Encode all paths (this may change the project properties)
        String[] javac_cp = cs.encodeToStrings( ClassPathUiSupport.getIterator( JAVAC_CLASSPATH_MODEL ) );
        String[] javac_test_cp = cs.encodeToStrings( ClassPathUiSupport.getIterator( JAVAC_TEST_CLASSPATH_MODEL ) );
        String[] run_cp = cs.encodeToStrings( ClassPathUiSupport.getIterator( RUN_CLASSPATH_MODEL ) );
        String[] run_test_cp = cs.encodeToStrings( ClassPathUiSupport.getIterator( RUN_TEST_CLASSPATH_MODEL ) );
                
        // Store source roots
        storeRoots( project.getSourceRoots(), SOURCE_ROOTS_MODEL );
        storeRoots( project.getTestSourceRoots(), TEST_ROOTS_MODEL );
                
        // Store standard properties
        EditableProperties projectProperties = updateHelper.getProperties( AntProjectHelper.PROJECT_PROPERTIES_PATH );        
        EditableProperties privateProperties = updateHelper.getProperties( AntProjectHelper.PRIVATE_PROPERTIES_PATH );
        
        // Assure inegrity which can't shound not be assured in UI
        if ( !JAVADOC_NO_INDEX_MODEL.isSelected() ) {
            JAVADOC_SPLIT_INDEX_MODEL.setSelected( false ); // Can't split non existing index
        }
                                
        // Standard store of the properties
        projectGroup.store( projectProperties );        
        privateGroup.store( privateProperties );
        
        storeRunConfigs(RUN_CONFIGS, projectProperties, privateProperties);
        EditableProperties ep = updateHelper.getProperties("nbproject/private/config.properties");
        if (activeConfig == null) {
            ep.remove("config");
        } else {
            ep.setProperty("config", activeConfig);
        }
        updateHelper.putProperties("nbproject/private/config.properties", ep);
        
        //Hotfix of the issue #70058
        //Should use the StoreGroup when the StoreGroup SPI will be extended to allow false default value in ToggleButtonModel
        //Save javac.debug
        privateProperties.setProperty(JAVAC_DEBUG, encodeBoolean (JAVAC_DEBUG_MODEL.isSelected(), javacDebugBooleanKind));
                
        //Hotfix of the issue #70058
        //Should use the StoreGroup when the StoreGroup SPI will be extended to allow false default value in ToggleButtonModel
        //Save javadoc.preview
        privateProperties.setProperty(JAVADOC_PREVIEW, encodeBoolean (JAVADOC_PREVIEW_MODEL.isSelected(), javadocPreviewBooleanKind));
                
        // Save all paths
        projectProperties.setProperty( JAVAC_CLASSPATH, javac_cp );
        projectProperties.setProperty( JAVAC_TEST_CLASSPATH, javac_test_cp );
        projectProperties.setProperty( RUN_CLASSPATH, run_cp );
        projectProperties.setProperty( RUN_TEST_CLASSPATH, run_test_cp );
        
        //Handle platform selection and javac.source javac.target properties
        PlatformUiSupport.storePlatform (projectProperties, updateHelper,PLATFORM_MODEL.getSelectedItem(), JAVAC_SOURCE_MODEL.getSelectedItem());
                                
        // Handle other special cases
        if ( NO_DEPENDENCIES_MODEL.isSelected() ) { // NOI18N
            projectProperties.remove( NO_DEPENDENCIES ); // Remove the property completely if not set
        }

        projectProperties.putAll(additionalProperties);

        projectProperties.put(INCLUDES, includes);
        projectProperties.put(EXCLUDES, excludes);
        
        // Store the property changes into the project
        updateHelper.putProperties( AntProjectHelper.PROJECT_PROPERTIES_PATH, projectProperties );
        updateHelper.putProperties( AntProjectHelper.PRIVATE_PROPERTIES_PATH, privateProperties );
        
        String value = additionalProperties.get(SOURCE_ENCODING);
        if (value != null) {
            try {
                FileEncodingQuery.setDefaultEncoding(Charset.forName(value));
            } catch (UnsupportedCharsetException e) {
                //When the encoding is not supported by JVM do not set it as default
            }
        }
    }
    
    /** Finds out what are new and removed project dependencies and 
     * applyes the info to the project
     */
    private void resolveProjectDependencies() {
            
        // Create a set of old and new artifacts.
        Set oldArtifacts = new HashSet();
        EditableProperties projectProperties = updateHelper.getProperties( AntProjectHelper.PROJECT_PROPERTIES_PATH );        
        oldArtifacts.addAll( cs.itemsList( projectProperties.get( JAVAC_CLASSPATH ) ) );
        oldArtifacts.addAll( cs.itemsList( projectProperties.get( JAVAC_TEST_CLASSPATH ) ) );
        oldArtifacts.addAll( cs.itemsList( projectProperties.get( RUN_CLASSPATH ) ) );
        oldArtifacts.addAll( cs.itemsList( projectProperties.get( RUN_TEST_CLASSPATH ) ) );
                   
        Set newArtifacts = new HashSet();
        newArtifacts.addAll( ClassPathUiSupport.getList( JAVAC_CLASSPATH_MODEL ) );
        newArtifacts.addAll( ClassPathUiSupport.getList( JAVAC_TEST_CLASSPATH_MODEL ) );
        newArtifacts.addAll( ClassPathUiSupport.getList( RUN_CLASSPATH_MODEL ) );
        newArtifacts.addAll( ClassPathUiSupport.getList( RUN_TEST_CLASSPATH_MODEL ) );
                
        // Create set of removed artifacts and remove them
        Set removed = new HashSet( oldArtifacts );
        removed.removeAll( newArtifacts );
        Set added = new HashSet(newArtifacts);
        added.removeAll(oldArtifacts);
        
        // 1. first remove all project references. The method will modify
        // project property files, so it must be done separately
        for( Iterator it = removed.iterator(); it.hasNext(); ) {
            ClassPathSupport.Item item = (ClassPathSupport.Item)it.next();
            if ( item.getType() == ClassPathSupport.Item.TYPE_ARTIFACT ||
                    item.getType() == ClassPathSupport.Item.TYPE_JAR ) {
                refHelper.destroyReference(item.getReference());
            }
        }
        
        // 2. now read project.properties and modify rest
        EditableProperties ep = updateHelper.getProperties( AntProjectHelper.PROJECT_PROPERTIES_PATH );
        boolean changed = false;
        
        for( Iterator it = removed.iterator(); it.hasNext(); ) {
            ClassPathSupport.Item item = (ClassPathSupport.Item)it.next();
            if (item.getType() == ClassPathSupport.Item.TYPE_LIBRARY) {
                // remove helper property pointing to library jar if there is any
                String prop = item.getReference();
                prop = ClassPathSupport.getAntPropertyName(prop);
                ep.remove(prop);
                changed = true;
            }
        }
        for( Iterator it = added.iterator(); it.hasNext(); ) {
            ClassPathSupport.Item item = (ClassPathSupport.Item)it.next();
            if (item.getType() == ClassPathSupport.Item.TYPE_LIBRARY && !item.isBroken()) {
                // add property to project.properties pointing to relativized 
                // library jar(s) if possible                
                String prop = cs.getLibraryReference( item );
                prop = ClassPathSupport.getAntPropertyName(prop);
                changed |= ClassPathSupport.relativizeLibraryClassPath(ep,updateHelper.getAntProjectHelper(),prop);
            }
        }
        if (changed) {
            updateHelper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        }
    }            
    
    private void storeRoots( SourceRoots roots, DefaultTableModel tableModel ) throws MalformedURLException {
        Vector data = tableModel.getDataVector();
        URL[] rootURLs = new URL[data.size()];
        String []rootLabels = new String[data.size()];
        for (int i=0; i<data.size();i++) {
            File f = (File) ((Vector)data.elementAt(i)).elementAt(0);
            rootURLs[i] = JavaFXProjectUtil.getRootURL(f,null);            
            rootLabels[i] = (String) ((Vector)data.elementAt(i)).elementAt(1);
        }
        roots.putRoots(rootURLs,rootLabels);
    }
    
    /* This is used by CustomizerWSServiceHost */
    public void putAdditionalProperty(String propertyName, String propertyValue) {
        additionalProperties.put(propertyName, propertyValue);
    }
    
    private static boolean showModifiedMessage (String title) {
        String message = NbBundle.getMessage(JavaFXProjectProperties.class,"TXT_Regenerate");
        JButton regenerateButton = new JButton (NbBundle.getMessage(JavaFXProjectProperties.class,"CTL_RegenerateButton"));
        regenerateButton.setDefaultCapable(true);
        regenerateButton.getAccessibleContext().setAccessibleDescription (NbBundle.getMessage(JavaFXProjectProperties.class,"AD_RegenerateButton"));
        NotifyDescriptor d = new NotifyDescriptor.Message (message, NotifyDescriptor.WARNING_MESSAGE);
        d.setTitle(title);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        d.setOptions(new Object[] {regenerateButton, NotifyDescriptor.CANCEL_OPTION});        
        return DialogDisplayer.getDefault().notify(d) == regenerateButton;
    }
    
    //Hotfix of the issue #70058
    //Should be removed when the StoreGroup SPI will be extended to allow false default value in ToggleButtonModel
    private static String encodeBoolean (boolean value, Integer kind) {
        if ( kind == BOOLEAN_KIND_ED ) {
            return value ? "on" : "off"; // NOI18N
        }
        else if ( kind == BOOLEAN_KIND_YN ) { // NOI18N
            return value ? "yes" : "no";
        }
        else {
            return value ? "true" : "false"; // NOI18N
        }
    }
    
    //Hotfix of the issue #70058
    //Should be removed when the StoreGroup SPI will be extended to allow false default value in ToggleButtonModel
    private static JToggleButton.ToggleButtonModel createToggleButtonModel (final PropertyEvaluator evaluator, final String propName, Integer[] kind) {
        assert evaluator != null && propName != null && kind != null && kind.length == 1;
        String value = evaluator.getProperty( propName );
        boolean isSelected = false;
        if (value == null) {
            isSelected = true;
        }
        else {
           String lowercaseValue = value.toLowerCase();
           if ( lowercaseValue.equals( "yes" ) || lowercaseValue.equals( "no" ) ) { // NOI18N
               kind[0] = BOOLEAN_KIND_YN;
           }
           else if ( lowercaseValue.equals( "on" ) || lowercaseValue.equals( "off" ) ) { // NOI18N
               kind[0] = BOOLEAN_KIND_ED;
           }
           else {
               kind[0] = BOOLEAN_KIND_TF;
           }

           if ( lowercaseValue.equals( "true") || // NOI18N
                lowercaseValue.equals( "yes") ||  // NOI18N
                lowercaseValue.equals( "on") ) {  // NOI18N
               isSelected = true;                   
           } 
        }
        JToggleButton.ToggleButtonModel bm = new JToggleButton.ToggleButtonModel();
        bm.setSelected(isSelected );
        return bm;
    }
    
    /**
     * A mess.
     */
    public Map<String/*|null*/,Map<String,String>> readRunConfigs() {
        Map<String,Map<String,String>> m = new TreeMap<String,Map<String,String>>(new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1 != null ? (s2 != null ? s1.compareTo(s2) : 1) : (s2 != null ? -1 : 0);
            }
        });
        Map<String,String> def = new TreeMap<String,String>();
        for (String prop : new String[] {MAIN_CLASS, APPLICATION_ARGS, RUN_JVM_ARGS, RUN_WORK_DIR}) {
            String v = updateHelper.getProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH).getProperty(prop);
            if (v == null) {
                v = updateHelper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH).getProperty(prop);
            }
            if (v != null) {
                def.put(prop, v);
            }
        }
        m.put(null, def);
        FileObject configs = project.getProjectDirectory().getFileObject("nbproject/configs");
        if (configs != null) {
            for (FileObject kid : configs.getChildren()) {
                if (!kid.hasExt("properties")) {
                    continue;
                }
                m.put(kid.getName(), new TreeMap<String,String>(updateHelper.getProperties(FileUtil.getRelativePath(project.getProjectDirectory(), kid))));
            }
        }
        configs = project.getProjectDirectory().getFileObject("nbproject/private/configs");
        if (configs != null) {
            for (FileObject kid : configs.getChildren()) {
                if (!kid.hasExt("properties")) {
                    continue;
                }
                Map<String,String> c = m.get(kid.getName());
                if (c == null) {
                    continue;
                }
                c.putAll(new HashMap<String,String>(updateHelper.getProperties(FileUtil.getRelativePath(project.getProjectDirectory(), kid))));
            }
        }
        //System.err.println("readRunConfigs: " + m);
        return m;
    }

    public void storeRunConfigs(Map<String/*|null*/,Map<String,String/*|null*/>/*|null*/> configs,
            EditableProperties projectProperties, EditableProperties privateProperties) throws IOException {
        //System.err.println("storeRunConfigs: " + configs);
        Map<String,String> def = configs.get(null);
        for (String prop : new String[] {MAIN_CLASS, APPLICATION_ARGS, RUN_JVM_ARGS, RUN_WORK_DIR}) {
            String v = def.get(prop);
            EditableProperties ep = (prop.equals(APPLICATION_ARGS) || prop.equals(RUN_WORK_DIR)) ?
                privateProperties : projectProperties;
            if (!Utilities.compareObjects(v, ep.getProperty(prop))) {
                if (v != null && v.length() > 0) {
                    ep.setProperty(prop, v);
                } else {
                    ep.remove(prop);
                }
            }
        }
        for (Map.Entry<String,Map<String,String>> entry : configs.entrySet()) {
            String config = entry.getKey();
            if (config == null) {
                continue;
            }
            String sharedPath = "nbproject/configs/" + config + ".properties"; // NOI18N
            String privatePath = "nbproject/private/configs/" + config + ".properties"; // NOI18N
            Map<String,String> c = entry.getValue();
            if (c == null) {
                updateHelper.putProperties(sharedPath, null);
                updateHelper.putProperties(privatePath, null);
                continue;
            }
            for (Map.Entry<String,String> entry2 : c.entrySet()) {
                String prop = entry2.getKey();
                String v = entry2.getValue();
                String path = (prop.equals(APPLICATION_ARGS) || prop.equals(RUN_WORK_DIR)) ?
                    privatePath : sharedPath;
                EditableProperties ep = updateHelper.getProperties(path);
                if (!Utilities.compareObjects(v, ep.getProperty(prop))) {
                    if (v != null && (v.length() > 0 || (def.get(prop) != null && def.get(prop).length() > 0))) {
                        ep.setProperty(prop, v);
                    } else {
                        ep.remove(prop);
                    }
                    updateHelper.putProperties(path, ep);
                }
            }
            // Make sure the definition file is always created, even if it is empty.
            updateHelper.putProperties(sharedPath, updateHelper.getProperties(sharedPath));
        }
    }
    
    void loadIncludesExcludes(IncludeExcludeVisualizer v) {
        Set<File> roots = new HashSet<File>();
        for (DefaultTableModel model : new DefaultTableModel[] {SOURCE_ROOTS_MODEL, TEST_ROOTS_MODEL}) {
            for (Object row : model.getDataVector()) {
                roots.add((File) ((Vector) row).elementAt(0));
            }
        }
        v.setRoots(roots.toArray(new File[roots.size()]));
        v.setIncludePattern(includes);
        v.setExcludePattern(excludes);
    }

    void storeIncludesExcludes(IncludeExcludeVisualizer v) {
        includes = v.getIncludePattern();
        excludes = v.getExcludePattern();
    }
    
    public WebStartProjectProperties getWebStartProjectProperties() {
        return webStartProjectProperties;
    }

}