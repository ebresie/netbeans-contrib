/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.settings;

import java.io.*;
import org.omg.CORBA.*;

import org.openide.options.SystemOption;
//import org.openide.options.ContextSystemOption;
import org.openide.util.NbBundle;
import org.openide.execution.NbProcessDescriptor;

import org.netbeans.modules.java.settings.JavaSettings;

import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextSupport;
import java.beans.beancontext.BeanContextProxy;

import org.openide.TopManager;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.corba.*;

public class CORBASupportSettings extends SystemOption implements BeanContextProxy { 
								  //PropertyChangeListener {

    private static final boolean DEBUG = false;
    //private static final boolean DEBUG = true;

    private static final boolean DYNLOAD = true;
    //private static final boolean DYNLOAD = false;

    private static final boolean PRODUCTION = true;
    //private static final boolean PRODUCTION = false;

    static final long serialVersionUID = -2809668725556980488L;
    /*
      private String[] checkSections = {"CTL_NAME", "IMPORT", "SETTINGS_ORB_PROPERTIES",
      "ORB_SERVER_INIT", "ORB_CLIENT_INIT", "ORB_SERVER_RUN",
      "ORB_OBJECT_ACTIVATION", "DIR_PARAM",
      "PACKAGE_PARAM", "COMPILER", "PACKAGE_DELIMITER",
      "ERROR_EXPRESSION", "FILE_POSITION", "LINE_POSITION",
      "COLUMN_POSITION", "MESSAGE_POSITION", "TIE_PARAM",
      // added for implementation generator
      "IMPLBASE_IMPL_PREFIX", "IMPLBASE_IMPL_POSTFIX",
      "EXT_CLASS_PREFIX", "EXT_CLASS_POSTFIX",
      "TIE_IMPL_PREFIX", "TIE_IMPL_POSTFIX",
      "IMPL_INT_PREFIX", "IMPL_INT_POSTFIX"};

      private String[] cbindings = {"NS", "IOR_FROM_FILE", "IOR_FROM_INPUT", "BINDER"};
      
      private String[] sbindings = {"NS", "IOR_TO_FILE", "IOR_TO_OUTPUT", "BINDER"};
    */

    /*
      private Vector clientBindings;
      
      private Vector serverBindings;
      
      private Vector props;

      private Vector names;
    */

    protected static String _M_orb_name;

    // used in CORBASupportSettings::getActiveSetting ()
    protected static String _M_orb_name_cache;
    protected static ORBSettings _M_setting_cache;

    protected static BeanContextSupport _M_implementations = new BeanContextSupport ();
    //= new BeanContextSupport ();
    protected static Vector _M_orb_names;

    /*
      public static String skels = CORBASupport.INHER;

      public static String params;

      public static String _server_binding;

      public static String _client_binding;


      public static boolean _hide_generated_files = true;

      public static String generation = CORBASupport.GEN_NOTHING;
    
      public static String synchro = CORBASupport.SYNCHRO_ON_UPDATE;
    */
    // advanced settings
    /*
      public static String _test;
      
      public static NbProcessDescriptor idl;
      
      public static String _tie_param;
      
      public static String _package_param;
      
      public static String _dir_param;
      
      public static String _orb_class;
      
      public static String _orb_singleton;
      
      public static String _orb_import;
      
      public static String _package_delimiter;
      
      public static String _error_expression;
      
      public static String _file_position;
      
      public static String _line_position;
      
      public static String _column_position;
      
      public static String _message_position;
      
      public static String _impl_prefix;
      public static String _impl_postfix;
      public static String _ext_class_prefix;
      public static String _ext_class_postfix;
      public static String _tie_prefix;
      public static String _tie_postfix;
      public static String _impl_int_prefix;
      public static String _impl_int_postfix;
      
      private boolean _is_tie;

      public static String _table = "USER="+System.getProperty("user.name")+"\n";
      //      + "VERSION="+System.getProperty ("org.openide.major.version")+"\n";


      String addition = "";
    */

    private ORB _M_orb;

    public static Vector namingChildren;

    public static Vector IRChildren;
    
    private boolean _M_loaded = false;
    private boolean _M_in_init = false;
    //private boolean deserealization;

    /** @return human presentable name */
    public String displayName() {
        return CORBASupport.bundle.getString("CTL_CORBASupport_options");
    }

    public CORBASupportSettings () {
        //	setOrb (CORBASupport.bundle.getString ("CTL_ORBIX"));
        //addOption (getCORBASupportAdvancedSettings ());
        if (DEBUG) 
            System.out.println ("CORBASupportSettings () ...");
	    //Thread.dumpStack ();
	//TopManager.getDefault ().getWindowManager ().addPropertyChangeListener (this);
    }

    public void init () {
	_M_in_init = true;
	/*
	  //names = new Vector (5);
	  initOrb ();
	  props = new Vector (5);
	  clientBindings = new Vector (5);
	  serverBindings = new Vector (5);
	*/
	namingChildren = new Vector ();	
	IRChildren = new Vector ();
	//loadImpl ();
	//addPropertyChangeListener (this);
	//addOption (getCORBASupportAdvancedSettings ());
	//      setOrb (CORBASupport.bundle.getString ("CTL_ORBIX"));
	//this.getCookieSet.add (UpdateCookie.class);
	  
        // test for default settings
        //setOrb (CORBASupport.bundle.getString ("CTL_ORBIX"));
        if (DYNLOAD && !PRODUCTION) {
	    setOrb ("ORBacus for Java 3.1.x");
	    /*
	      loadImpl ();
	      setOrb ("ORBacus for Java 3.1.x");
	      setClientBinding (CORBASupport.CLIENT_IOR_FROM_FILE);
	      setServerBinding (CORBASupport.SERVER_IOR_TO_FILE);
	      generation = CORBASupport.GEN_EXCEPTION;
	    */
        }

        if (PRODUCTION) {
            //loadImpl ();
            setOrb ("JDK 1.2 ORB");
            //setClientBinding (CORBASupport.CLIENT_NS);
            //setServerBinding (CORBASupport.SERVER_NS);
        }

	_M_in_init = false;
    }
    
    public void readExternal (ObjectInput __in) 
	throws java.io.IOException, 
	       java.lang.ClassNotFoundException {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::readExternal (" + __in + ")");
	_M_in_init = true;
	//deserealization = true;
	super.readExternal (__in);
	IDLDataLoader __loader = (IDLDataLoader)IDLDataLoader.findObject 
	    (IDLDataLoader.class, true);
	//System.out.println ("CORBASupportSettings (IDLDataLoader)__in.readObject ()");
	__loader = (IDLDataLoader)__in.readObject ();
	//System.out.println ("CORBASupportSettings done");
	_M_in_init = false;
	//deserealization = false;
	//this.setOrb (_M_orb_name);
    }
    
    
    public void writeExternal (ObjectOutput __out) throws IOException {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::writeExternal (" + __out + ")");
	//_M_implementations.writeExternal (__out);
	//((BeanContextSupport)_M_implementations).writeObject (__out);
	super.writeExternal (__out);
	IDLDataLoader __loader = (IDLDataLoader)IDLDataLoader.findObject 
	    (IDLDataLoader.class, true);
	__out.writeObject (__loader);
      }
    
    /*
      private void writeObject (java.io.ObjectOutputStream __out) throws IOException {
      if (DEBUG)
      System.out.println ("CORBASupportSettings::writeObject (" + __out + ")");
      __out.defaultWriteObject ();
      //__out.writeObject (this.getBeans ());
      }
      
      
      private void readObject (java.io.ObjectInputStream __in) throws IOException, ClassNotFoundException {
      if (DEBUG)
      System.out.println ("CORBASupportSettings::readObject (" + __in + ")");
      __in.defaultReadObject ();
      //__in.readObject ();
      }
    */

    public boolean isGlobal () {
        return false;
    }
    /*
      public void propertyChange (PropertyChangeEvent event) {
      if (DEBUG)
      System.out.println ("propertyChange: " + event.getPropertyName ());
    */
	/*
        if (event.getPropertyName () != null) {
            if (event.getPropertyName ().equals ("orb"))
                setAdvancedOrbOptions ((String) event.getNewValue ());
            if (event.getPropertyName ().equals ("_client_binding"))
                setAdvancedClientBinding ((String) event.getNewValue ());
            if (event.getPropertyName ().equals ("_server_binding"))
                setAdvancedServerBinding ((String) event.getNewValue ());
        }
	*/
    /*
      }
    */

    /*    
	  public void setAdvancedClientBinding (String binding) {
	  
	  if (DEBUG)
	  System.out.println ("client binding: " + binding);
	  //if (DEBUG)
	  // System.out.println ("ctl_client_binding: " + getCtlClientBindingName ());
	  //setJavaTemplateTable ();
	  }
    */
    /*
      public void setAdvancedServerBinding (String binding) {
      
      if (DEBUG)
      System.out.println ("server binding: " + binding);
      //if (DEBUG)
      //	 System.out.println ("ctl_server_binding: " + getCtlServerBindingName ());
      //setJavaTemplateTable ();
      }
    */
    
    public Vector getNames () {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::getNames ()");
	/*
	  if (_M_orb_names == null) {
	  // lazy initialization
	  loadImpl ();
	  }
	*/
	return _M_orb_names;
	/*
	  Vector __names = new Vector ();
	  if (_M_implementations == null) {
	  // lazy initialization
	  loadImpl ();
	  }
	  for (int __i = 0; __i < _M_implementations.size (); __i++) {
	  __names.add (((ORBSettings)_M_implementations.elementAt (__i)).getOrbName ());
	  }
	  return __names;
	*/
    }

    public String getOrb () {
        //loadImpl ();
        return _M_orb_name;
    }

    public void setOrb (String __value) {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::setOrb (" + __value + ")");
        String __old = _M_orb_name;
	_M_orb_name = __value;
	//setJavaTemplateTable ();
	ORBSettings __settings = this.getActiveSetting ();
	if (__settings != null)
	    __settings.setJavaTemplateTable ();
	firePropertyChange ("_M_orb_name", __old, _M_orb_name);
	this.cacheThrow ();

	if (!_M_in_init) {
	    boolean __orb_hide = this.getActiveSetting ().hideGeneratedFiles ();
	    IDLDataLoader __loader = (IDLDataLoader)IDLDataLoader.findObject 
		(IDLDataLoader.class, true);
	    boolean __old_hide = __loader.getHide ();
	    //System.out.println ("__orb_hide: " + __orb_hide);
	    //System.out.println ("__old_hide: " + __old_hide);
	    //if (__old_hide != __orb_hide)
	    __loader.setHide (__orb_hide);
	}
    }

    /*

      public String getOrbName () {
        
      String name = "";

      if (orb.equals (CORBASupport.ORBIX))
      name = "ORBIX";
      if (orb.equals (CORBASupport.VISIBROKER))
      name = "VISIBROKER";
      if (orb.equals (CORBASupport.ORBACUS))
      name = "ORBACUS";
      if (orb.equals (CORBASupport.JAVAORB))
      name = "JAVAORB";

      return name;
      }
     
      public String getCtlOrbName () {
      return "CTL_" + getOrbName () + "_";
      }

    */

    /*
      public String getClientBindingName () {

        String name = "";

        if (_client_binding != null) {
            if (_client_binding.equals (CORBASupport.CLIENT_NS))
                name = "NS";
            if (_client_binding.equals (CORBASupport.CLIENT_IOR_FROM_FILE))
                name = "IOR_FROM_FILE";
            if (_client_binding.equals (CORBASupport.CLIENT_IOR_FROM_INPUT))
                name = "IOR_FROM_INPUT";
            if (_client_binding.equals (CORBASupport.CLIENT_BINDER))
                name = "BINDER";
        }
        return name;
    }
    */
    /*
      public String getCtlClientBindingName () {
      return getCtlOrbName () + "CLIENT_" + getClientBindingName ();
      }
    */
    /*
    public String getServerBindingName () {

        String name = "";

        if (_server_binding != null) {
            if (_server_binding.equals (CORBASupport.SERVER_NS))
                name = "NS";
            if (_server_binding.equals (CORBASupport.SERVER_IOR_TO_FILE))
                name = "IOR_TO_FILE";
            if (_server_binding.equals (CORBASupport.SERVER_IOR_TO_OUTPUT))
                name = "IOR_TO_OUTPUT";
            if (_server_binding.equals (CORBASupport.SERVER_BINDER))
                name = "BINDER";
        }
        return name;
    }
    */
    /*
      public String getCtlServerBindingName () {
      return getCtlOrbName () + "SERVER_" + getServerBindingName ();
      }
    */
    /*
    public String getSkels () {
        return skels;
    }

    public void setSkels (String s) {
        String old = skels;
        skels = s;
        firePropertyChange ("skels", old, skels);
    }

    public void setParams (String s) {
        String old = params;
        params = s;
        firePropertyChange ("params", old, params);
    }

    public String getParams () {
        return params;
    }

    public static String param () {
        return params;
    }


    public String getClientBinding () {
        return _client_binding;
    }

    public void setClientBinding (String s) {
        String old = _client_binding;
        _client_binding = s;
        firePropertyChange ("_client_binding", old, _client_binding);
    }

    public String getServerBinding () {
        return _server_binding;
    }

    public void setServerBinding (String s) {
        String old = _server_binding;
        _server_binding = s;
        firePropertyChange ("_server_binding", old, _server_binding);
    }


    // advanced settings
    public NbProcessDescriptor getIdl () {
        return idl;
    }

    public static String idl () {
        return idl.getProcessName ();
    }

    public void setIdl (NbProcessDescriptor s) {
        //System.out.println ("setIdl :-)");
        NbProcessDescriptor old = idl;
        idl = s;
        //System.out.println ("switch: " + idl.getClasspathSwitch ());
        //int length = idl.getProcessArgs ().length;
        //String[] params = idl.getProcessArgs ();
        //for (int i=0; i<length; i++)
        //	 System.out.println ("param[" + i + "]: " + params[i]);

        //Thread.dumpStack ();
        firePropertyChange ("idl", old, idl);
    }

    public void setTieParam (String s) {
        String old = _tie_param;
        _tie_param = s;
        firePropertyChange ("_tie_param", old, _tie_param);
    }

    public boolean isTie () {

        if (skels.equals (CORBASupport.TIE)) {
            _is_tie = true;
            if (DEBUG)
                System.out.println ("is TIE");
        }
        else {
            _is_tie = false;
            if (DEBUG)
                System.out.println ("isn't TIE");
        }

        return _is_tie;
    }

    public String getTieParam () {
        return _tie_param;
    }

    //public static String tie_param () {
    //   return _tie_param;
    //}

    public void setPackageParam (String s) {
        String old = _package_param;
        _package_param = s;
        firePropertyChange ("_package_param", old, _package_param);
    }

    public String getPackageParam () {
        return _package_param;
    }

    public static String package_param () {
        return _package_param;
    }

    public void setDirParam (String s) {
        String old = _dir_param;
        _dir_param = s;
        firePropertyChange ("_dir_param", old, _dir_param);
    }

    public String getDirParam () {
        return _dir_param;
    }

    public static String dir_param () {
        return _dir_param;
    }

    public String getPackageDelimiter () {
        return _package_delimiter;
    }

    public void setPackageDelimiter (String s) {
        String old = _package_delimiter;
        _package_delimiter = s;
        firePropertyChange ("_package_delimiter", old, _package_delimiter);
    }

    public char delim () {
        return _package_delimiter.charAt (0);
    }

    public String getErrorExpression () {
        return _error_expression;
    }

    public void setErrorExpression (String s) {
        String old = _error_expression;
        _error_expression = s;
        firePropertyChange ("_error_expression", old, _error_expression);
    }

    public static String expression () {
        return _error_expression;
    }

    public String getFilePosition () {
        return _file_position;
    }

    public void setFilePosition (String s) {
        String old = _file_position;
        _file_position = s;
        firePropertyChange ("_file_position", old, _file_position);
    }

    public int file () {
        return new Integer(_file_position).intValue ();
    }

    public String getLinePosition () {
        return _line_position;
    }

    public void setLinePosition (String s) {
        String old = _line_position;
        _line_position = s;
        firePropertyChange ("_line_position", old, _line_position);
    }

    public int line () {
        return new Integer(_line_position).intValue ();
    }

    public String getColumnPosition () {
        return _column_position;
    }

    public void setColumnPosition (String s) {
        String old = _column_position;
        _column_position = s;
        firePropertyChange ("_column_position", old, _column_position);
    }

    public int column () {
        return new Integer(_column_position).intValue ();
    }

    public String getMessagePosition () {
        return _message_position;
    }

    public void setMessagePosition (String s) {
        String old = _message_position;
        _message_position = s;
        firePropertyChange ("_message_position", old, _message_position);
    }

    public int message () {
        return new Integer(_message_position).intValue ();
    }


    public void setImplBasePrefix (String s) {
        _impl_prefix = s;
    }

    public String getImplBasePrefix () {
        return _impl_prefix;
    }

    public void setImplBasePostfix (String s) {
        _impl_postfix = s;
    }

    public String getImplBasePostfix () {
        return _impl_postfix;
    }


    public void setExtClassPrefix (String s) {
        _ext_class_prefix = s;
    }

    public String getExtClassPrefix () {
        return _ext_class_prefix;
    }

    public void setExtClassPostfix (String s) {
        _ext_class_postfix = s;
    }

    public String getExtClassPostfix () {
        return _ext_class_postfix;
    }

    public void setTiePrefix (String s) {
        _tie_prefix = s;
    }

    public String getTiePrefix () {
        return _tie_prefix;
    }

    public void setTiePostfix (String s) {
        _tie_postfix = s;
    }

    public String getTiePostfix () {
        return _tie_postfix;
    }


    public void setImplIntPrefix (String s) {
        _impl_int_prefix = s;
    }

    public String getImplIntPrefix () {
        return _impl_int_prefix;
    }

    public void setImplIntPostfix (String s) {
        _impl_int_postfix = s;
    }

    public String getImplIntPostfix () {
        return _impl_int_postfix;
    }


    public void setReplaceableStringsTable (String s) {
        String old = _table;
        _table = s;
        firePropertyChange ("_table", old, _table);
    }

    public String getRaplaceableStringsTable () {
        return _table;
    }

    public Properties getReplaceableStringsProps () {
        Properties props = new Properties ();
        try {
            props.load (new StringBufferInputStream(_table));
            //props.load (new StringReader (_table));
        }
        catch (IOException e) {
        }
        return props;
    }

    public void fireChangeChoices () {
        firePropertyChange ("_client_binding", null, null);
        firePropertyChange ("_server_binding", null, null);
    }


    public String[] getClientBindingsChoices () {

        String[] choices;
        choices = new String[1];
        choices[0] = new String ("");
        int index = -1;
        int length = -1;

        for (int i=0; i<getNames ().size (); i++) {
            if (DEBUG)
                System.out.println ("names[" + i + "] = " + getNames ().elementAt (i));
            if (getNames ().elementAt (i).equals (orb)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            length = ((Vector)clientBindings.elementAt (index)).size ();
            choices = new String[length];
            if (DEBUG) {
                System.out.println ("index: " + index);
                System.out.println ("orb: " + orb);
                System.out.println ("length: " + length);
                System.out.println ("bindings: " + (Vector)clientBindings.elementAt (index));
            }
        }
        if (index >= 0)
            for (int i=0; i<length; i++) {
                choices[i] = (String)((Vector)clientBindings.elementAt (index)).elementAt (i);
                if (DEBUG)
                    System.out.println ("choice: " + choices[i]);
            }

        return choices;
    }



    public String[] getServerBindingsChoices () {

        String[] choices;
        choices = new String[1];
        choices[0] = new String ("");
        int index = -1;
        int length = -1;

        for (int i=0; i<getNames ().size (); i++) {
            if (DEBUG)
                System.out.println ("names[" + i + "] = " + getNames ().elementAt (i));
            if (getNames ().elementAt (i).equals (orb)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            length = ((Vector)serverBindings.elementAt (index)).size ();
            choices = new String[length];
            if (DEBUG) {
                System.out.println ("index: " + index);
                System.out.println ("orb: " + orb);
                System.out.println ("length: " + length);
                System.out.println ("bindings: " + (Vector)serverBindings.elementAt (index));
            }
        }
        if (index >= 0)
            for (int i=0; i<length; i++) {
                choices[i] = (String)((Vector)serverBindings.elementAt (index)).elementAt (i);
                if (DEBUG)
                    System.out.println ("choice: " + choices[i]);
            }

        return choices;
    }

    public boolean hideGeneratedFiles () {
        return _hide_generated_files;
    }


    public void setHideGeneratedFiles (boolean v) {
        _hide_generated_files = v;
    }

    public void setAdvancedOrbOptions (String orb) {

        if (DEBUG)
            System.out.println ("orb: " + orb);

        if (DEBUG)
            System.out.println ("setAdvancedOptions :)");
        JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);

        //ClientBindingPropertyEditor cbedit = (ClientBindingPropertyEditor)ClientBindingPropertyEditor.findObject (ClientBindingPropertyEditor.class, true);
        //cbedit.setChoices (getChoices);

        String old_tie = getTieParam ();
        String old_dir = getDirParam ();
        String old_package = getPackageParam ();
        String old_expression = getErrorExpression ();
        String old_file = getFilePosition ();
        String old_line = getLinePosition ();
        String old_column = getColumnPosition ();
        String old_message = getMessagePosition ();

        // added for generator
        String old_implbase_impl_prefix = getImplBasePrefix ();
        String old_implbase_impl_postfix = getImplBasePostfix ();
        String old_ext_class_prefix = getExtClassPrefix ();
        String old_ext_class_postfix = getExtClassPostfix ();
        String old_tie_prefix = getTiePrefix ();
        String old_tie_postfix = getTiePostfix ();
        String old_impl_int_prefix = getImplIntPrefix ();
        String old_impl_int_postfix = getImplIntPostfix ();



        String new_expression = "";
        String new_file = "";
        String new_line = "";
        String new_column = "";
        String new_message = "";
        String new_dir = "";
        String new_package = "";
        String new_tie = "";

        // added for generator
        String new_implbase_prefix;
        String new_implbase_postfix;
        String new_ext_class_prefix;
        String new_ext_class_postfix;
        String new_tie_prefix;
        String new_tie_postfix;
        String new_impl_int_prefix;
        String new_impl_int_postfix;

        NbProcessDescriptor old_idl = getIdl ();
        NbProcessDescriptor new_idl = null;
        String old_delimiter = getPackageDelimiter();
        String new_delimiter = ".";

        setJavaTemplateTable ();

        int index = -1;

        for (int i = 0; i<getNames ().size (); i++) {
            if (getNames ().elementAt (i).equals (orb)) {
                index = i;
                break;
            }
        }

        if (index == -1)
            return;

        new_tie = ((Properties)props.elementAt (index)).getProperty ("TIE_PARAM");
        new_dir = ((Properties)props.elementAt (index)).getProperty ("DIR_PARAM");
        new_package = ((Properties)props.elementAt (index)).getProperty ("PACKAGE_PARAM");

        //String[] tmp1 = new String[] {NbProcessDescriptor.CP_REPOSITORY};
        String[] tmp1 = new String[] {""};

        //new_idl = new NbProcessDescriptor ( (String)((Properties)props.elementAt (index)).getProperty ("COMPILER"), NbProcessDescriptor.NO_SWITCH, tmp1);
        String compiler = (String)((Properties)props.elementAt (index)).getProperty ("COMPILER");
        if (DEBUG)
            System.out.println ("compiler: " + compiler);

        StringTokenizer st = new StringTokenizer (compiler);
        //      String process = compiler.substring (0, compiler.indexOf (' '));
        String process = st.nextToken ();
        //      String args = compiler.substring (compiler.indexOf (' '), compiler.length () - 1);
        String args = "";
        while (st.hasMoreTokens ()) {
            if (args.length () > 0)
                args = args + " " + st.nextToken ();
            else
                args = st.nextToken ();
        }
        if (DEBUG) {
            System.out.println ("process: " + process);
            System.out.println ("args: " + args);
        }
        new_idl = new NbProcessDescriptor (process, args, "");
        new_expression = ((Properties)props.elementAt (index)).getProperty ("ERROR_EXPRESSION");
        new_file = ((Properties)props.elementAt (index)).getProperty ("FILE_POSITION");
        new_line = ((Properties)props.elementAt (index)).getProperty ("LINE_POSITION");
        new_column = ((Properties)props.elementAt (index)).getProperty ("COLUMN_POSITION");
        new_message = ((Properties)props.elementAt (index)).getProperty ("MESSAGE_POSITION");
        new_delimiter = ((Properties)props.elementAt (index)).getProperty ("PACKAGE_DELIMITER");

        // added for generator
        new_implbase_prefix = ((Properties)props.elementAt (index)).getProperty
                              ("IMPLBASE_IMPL_PREFIX");
        new_implbase_postfix = ((Properties)props.elementAt (index)).getProperty
                               ("IMPLBASE_IMPL_POSTFIX");
        new_ext_class_prefix = ((Properties)props.elementAt (index)).getProperty
                               ("EXT_CLASS_PREFIX");
        new_ext_class_postfix = ((Properties)props.elementAt (index)).getProperty
                                ("EXT_CLASS_POSTFIX");
        new_tie_prefix = ((Properties)props.elementAt (index)).getProperty
                         ("TIE_IMPL_PREFIX");
        new_tie_postfix = ((Properties)props.elementAt (index)).getProperty
                          ("TIE_IMPL_POSTFIX");
        new_impl_int_prefix = ((Properties)props.elementAt (index)).getProperty
                              ("IMPL_INT_PREFIX");
        new_impl_int_postfix = ((Properties)props.elementAt (index)).getProperty
                               ("IMPL_INT_POSTFIX");

        setTieParam (new_tie);
        setDirParam (new_dir);
        setPackageParam (new_package);
        setIdl (new_idl);
        setErrorExpression (new_expression);
        setFilePosition (new_file);
        setLinePosition (new_line);
        setColumnPosition (new_column);
        setMessagePosition (new_message);
        setPackageDelimiter (new_delimiter);

        // added for generator
        setImplBasePrefix (new_implbase_prefix);
        setImplBasePostfix (new_implbase_postfix);
        setExtClassPrefix (new_ext_class_prefix);
        setExtClassPostfix (new_ext_class_postfix);
        setTiePrefix (new_tie_prefix);
        setTiePostfix (new_tie_postfix);
        setImplIntPrefix (new_impl_int_prefix);
        setImplIntPostfix (new_impl_int_postfix);

        if (DEBUG)
            System.out.println ("setAdvancedOptions () - end!");

    }
    */

    public void setJavaTemplateTable () {

        int index = 0;

        String tmp_property;

	ORBSettings __setting = this.getActiveSetting ();
	Properties __setting_properties = __setting.getReplaceableStringsProps ();

        if (DEBUG)
            System.out.println ("setJavaTemplateTable");

        JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
        Properties p = js.getReplaceableStringsProps ();

        if (_M_orb_name == null)
            return;

        try {
            if (DEBUG) {
                System.out.println ("orb: " + _M_orb_name);
		System.out.println ("properties: " + __setting_properties);
	    }
	    /*
            for (int i = 0; i<getNames ().size (); i++) {
                if (getNames ().elementAt (i).equals (orb)) {
                    index = i;
                    break;
                }
            }
	    */
            //if (DEBUG)
            //   System.out.println ("props at position: " + props.elementAt (index));

            //if (DEBUG)
            //   System.out.println (((Properties)props.elementAt (index)).getProperty
            //     ("SETTINGS_ORB_PROPERTIES"));

            //if (DEBUG)
            //    System.out.println ("sett: " + ((Properties)props.elementAt (index)).getProperty
            //			("SETTINGS_ORB_PROPERTIES"));
            //if (DEBUG)
            //    System.out.println ("cb: " + getClientBindingName ());
            //if (DEBUG)
            //    System.out.println ("sb: " + getServerBindingName ());

            p.setProperty ("ORB_NAME", getOrb ());

            if (__setting.getServerBinding () != null)
                p.setProperty ("SERVER_BINDING", __setting.getServerBinding ().getValue ());
            if (__setting.getClientBinding () != null)
                p.setProperty ("CLIENT_BINDING", __setting.getClientBinding ().getValue ());


            p.setProperty ("SETTINGS_ORB_PROPERTIES", __setting_properties.getProperty
                           ("SETTINGS_ORB_PROPERTIES"));
            if (__setting_properties.getProperty
		("IMPORT_" + __setting.getClientBindingName ()) != null) {
                p.setProperty ("ORB_IMPORT",__setting_properties.getProperty
                               ("IMPORT_" + __setting.getClientBindingName ()));
            }
            else {
                if (__setting_properties.getProperty
		    ("IMPORT_" + __setting.getServerBindingName ()) != null) {
                    p.setProperty ("ORB_IMPORT",__setting_properties.getProperty
                                   ("IMPORT_" + __setting.getServerBindingName ()));
                }
                else {
                    p.setProperty ("ORB_IMPORT", __setting_properties.getProperty
                                   ("IMPORT"));
                }
            }

            p.setProperty ("ORB_SERVER_INIT", __setting_properties.getProperty
                           ("ORB_SERVER_INIT"));
            p.setProperty ("ORB_CLIENT_INIT", __setting_properties.getProperty
                           ("ORB_CLIENT_INIT"));
	    /*
	      if (!getClientBindingName ().equals (""))
	      if ((tmp_property = ((Properties)props.elementAt (index)).getProperty
	      ("CLIENT_" + getClientBindingName ())) != null)
	      p.setProperty ("ORB_CLIENT_BINDING", tmp_property);
	      
	      if (!getServerBindingName ().equals (""))
	      if ((tmp_property = ((Properties)props.elementAt (index)).getProperty
	      ("SERVER_" + getServerBindingName ())) != null)
	      p.setProperty ("ORB_SERVER_BINDING", tmp_property);
	    */
            p.setProperty ("ORB_OBJECT_ACTIVATION", __setting_properties.getProperty
                           ("ORB_OBJECT_ACTIVATION"));

            p.setProperty ("ORB_SERVER_RUN", __setting_properties.getProperty
                           ("ORB_SERVER_RUN"));

            // added for implementation generator
            /*
            p.setProperty ("IMPL_PREFIX", ((Properties)props.elementAt (index)).getProperty 
            ("IMPL_PREFIX"));

            p.setProperty ("IMPL_POSTFIX", ((Properties)props.elementAt (index)).getProperty 
            ("IMPL_POSTFIX"));

            p.setProperty ("EXT_CLASS_PREFIX", ((Properties)props.elementAt (index)).getProperty 
            ("EXT_CLASS_PREFIX"));

            p.setProperty ("EXT_CLASS_POSTFIX", ((Properties)props.elementAt (index)).getProperty 
            ("EXT_CLASS_POSTFIX"));
            */
        } catch (Exception e) {
            e.printStackTrace ();
        }
	

        //js.setReplaceableStringsTable
        ByteArrayOutputStream bs = new ByteArrayOutputStream ();
        try {
            p.store (bs, null);
        } catch (IOException e) {
            if (DEBUG)
                System.out.println (e);
        }
        //if (DEBUG)
        //	 System.out.println ("properties: " + bs.toString ());
        js.setReplaceableStringsTable (bs.toString ());

    }


    //public synchronized void loadImpl () {
    public void loadImpl () {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::loadImpl ()");

	//if (_M_loaded)
	//    return;

	_M_loaded = true;
	if (_M_orb_names == null)
	    _M_orb_names = new Vector (5);

        TopManager tm = TopManager.getDefault ();

        try {
            Enumeration folders 
		= tm.getRepository ().getDefaultFileSystem ().getRoot ().getFolders (false);
            CORBASupportSettings settings = (CORBASupportSettings)CORBASupportSettings.findObject
		(CORBASupportSettings.class, true);
            while (folders.hasMoreElements ()) {
                FileObject fo = (FileObject)folders.nextElement ();
                if (DEBUG)
                    System.out.println (fo.getName ());
                if (fo.toString ().equals ("CORBA")) {
                    FileObject[] files = fo.getChildren ();
                    for (int __i = 0; __i<files.length ; __i++) {
                        if (DEBUG)
                            System.out.println ("file: " + files[__i].toString ());

			ORBSettings __orb_settings = new ORBSettings ();
			__orb_settings.loadImpl (files[__i]);
			_M_implementations.add (__orb_settings);
			_M_orb_names.add (__orb_settings.getOrbName ());
			/*
			  Properties p = new Properties ();
			  p.load (files[i].getInputStream ());
			  
			  // checking of important properties fields
			  
			  boolean error = false;
			  for (int j=0; j<checkSections.length; j++)
			  if (p.getProperty (checkSections[j]) == null) {
			  System.out.println ("error in " + files[i].toString () 
			  + " missing " + checkSections[j] 
			  + " variable.");
			  error = true;
			  }
			  if (error)
			  continue;
			  if (DEBUG)
			  System.out.println ("impl: " + p.getProperty ("CTL_NAME"));
			  getNames ().add (__properties.getProperty ("CTL_NAME"));
			*/
			
                        //props.add (p);

                        // make client and server bindings
			/*
			  Vector tmp_clientBindings = new Vector (5);
			  for (int j=0; j<cbindings.length; j++)
			  if (p.getProperty ("CLIENT_" + cbindings[j]) != null) {
			  if (DEBUG)
			  System.out.println ("add cb: " + "CTL_CLIENT_" + cbindings[j]);
			  tmp_clientBindings.add (CORBASupport.bundle.getString
			  ("CTL_CLIENT_" + cbindings[j]));
			  }
			  clientBindings.add (tmp_clientBindings);

			  Vector tmp_serverBindings = new Vector (5);
			  for (int j=0; j<sbindings.length; j++)
			  if (p.getProperty ("SERVER_" + sbindings[j]) != null) {
			  if (DEBUG)
			  System.out.println ("add sb: " + "CTL_SERVER_" + sbindings[j]);
			  tmp_serverBindings.add (CORBASupport.bundle.getString
			  ("CTL_SERVER_" + sbindings[j]));
			  }
			  serverBindings.add (tmp_serverBindings);
                        //System.out.println ("props: ");
                        //props.list (System.out);
			*/
		    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
	if (DEBUG) {
	    System.out.println ("----!!!!");
	    java.lang.Object[] __beans = this.getBeans ();
	    for (int __i = 0; __i < __beans.length; __i++) {
		System.out.println (__i + " : " + __beans[__i]);
	    }
	}
    }


    public ORB getORB () {
        if (_M_orb == null)
            initOrb ();
        return _M_orb;
    }

    public void initOrb () {
        _M_orb = ORB.init (new String[] {""}, null);
    }


    public Vector getNamingServiceChildren () {
        //System.out.println ("getNamingServiceChildren: " + namingChildren);
        return namingChildren;
    }

    public void setNamingServiceChildren (Vector children) {
        //System.out.println ("setNamingServiceChildren: " + children);
        namingChildren = children;
    }

    public Vector getInterfaceRepositoryChildren () {
        //System.out.println ("getInterfaceRepositoryChildren: " + IRChildren.size ());
        return IRChildren;
    }

    public void setInterfaceRepositoryChildren (Vector children) {
        //System.out.println ("setInterfaceRepositoryChildren: " + children.size ());
        IRChildren = children;
    }

    /*
    public String getGeneration () {
        //System.out.println ("getGeneration () -> " + generation);
        return generation;
    }

    public void setGeneration (String value) {
        //System.out.println ("setGeneration (" + value + ");");
        generation = value;
    }

    public String getSynchro () {
        //System.out.println ("getSynchro () -> " + synchro);
        return synchro;
    }

    public void setSynchro (String value) {
        //System.out.println ("setSynchro (" + value + ");");
        synchro = value;
    }
    */

    public java.beans.beancontext.BeanContextChild getBeanContextProxy () {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::getBeanContextProxy ()");
	if (!_M_loaded)
	    loadImpl ();
        return _M_implementations;
    }
    
    public void addBean (java.lang.Object bean) {
        _M_implementations.add (bean);
        //__orb_names.add(bean);
    }
    
    public void removeBean (java.lang.Object bean) {
        _M_implementations.remove (bean);
        //set.remove(bean);
    }

    public java.lang.Object[] getBeans () {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::getBeans () -> " + _M_implementations);
	return _M_implementations.toArray ();
    }

    public void setBeans (java.lang.Object[] beans) {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::setBeans (java.lang.Object[] beans)");
	_M_implementations = new BeanContextSupport ();

	for(int i = 0; i < beans.length; i++) {
	    _M_implementations.add (beans[i]);
	}
    }

    public ORBSettings getSettingByName (String __name) {
	if (DEBUG) 
	    System.out.println ("CORBASupportSettings::getSettingByName (" + __name + ")");
	//Thread.dumpStack ();
	if (!_M_loaded)
	    loadImpl ();
	java.lang.Object[] __settings = getBeans ();
	for (int __i = 0; __i < __settings.length; __i++) {
	    ORBSettings __setting = (ORBSettings)__settings[__i];
	    if (__setting.getName ().equals (__name)) {
		return __setting;
	    }
	}	
	return null;
    }

    public void cacheThrow () {
	//System.out.println ("CCS::cacheThrow ()");
	_M_orb_name_cache = null;
	_M_setting_cache = null;
    }

    public ORBSettings getActiveSetting () {
	//if (DEBUG) 
	//System.out.println ("CORBASupportSettings::getActiveSetting ()");
	//Thread.dumpStack ();
	if (!_M_loaded)
	    loadImpl ();
	if (_M_orb_name_cache != null) {
	    if (_M_orb_name_cache.equals (this.getOrb ()) && (_M_setting_cache != null)) {
		if (DEBUG) {
		    System.out.println ("cache hit");
		    System.out.println ("orb: " + _M_orb_name_cache);
		}
		return _M_setting_cache;
	    }
	}

	_M_orb_name_cache = this.getOrb ();

	if (DEBUG)
	    System.out.println ("cache wasn't successfull");

	java.lang.Object[] __settings = getBeans ();
	for (int __i = 0; __i < __settings.length; __i++) {
	    ORBSettings __setting = (ORBSettings)__settings[__i];
	    String __name = __setting.getName ();
	    if (DEBUG)
		System.out.println (__name + " X " + this.getOrb ());
	    if (__name.equals (_M_orb_name_cache)) {
		//System.out.println ("orb: " + this.getOrb ());
		_M_setting_cache = __setting;
		return __setting;
	    }
	}	
	//System.out.println ("orb: " + _M_orb_name_cache);
	//Thread.dumpStack ();
	return null;
    }
    /*
      public ORBSettings findORBSettingsByListener (PropertyChangeListener __listener) {
      for (int __i = 0; __i < _M_implementations.size (); __i++) {
      ORBSettings[] __settings = (ORBSettings[])_M_implementations.toArray ();
      if (__settings[__i].hasListener (__listener))
      return __settings[__i];
      }
      return null;
      }
    */
}


