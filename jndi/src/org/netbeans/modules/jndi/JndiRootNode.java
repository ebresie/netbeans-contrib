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

package com.netbeans.enterprise.modules.jndi;

/*NetBeans*/

import com.netbeans.ide.*;
import com.netbeans.ide.actions.*;
import com.netbeans.ide.filesystems.*;
import com.netbeans.ide.nodes.*;
import com.netbeans.ide.util.actions.*;
import com.netbeans.ide.util.datatransfer.NewType;

/*JNDI*/
import javax.naming.*;
import javax.naming.directory.*;

/*JDK*/
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.StringTokenizer;


public class JndiRootNode extends AbstractNode
{
    
    public final static String NB_LABEL="NB_LABEL";
    public final static String NB_ROOT="NB_ROOT";
    
    protected SystemAction[] jndiactions=null;
    protected NewType[]	     jndinewtypes=null;
    
	public JndiRootNode()
	{
		super(new Children.Array());
		setName("JNDI");
		setIconBase(JndiIcons.ICON_BASE+JndiIcons.getIconName(JndiRootNode.NB_ROOT));
		initStartContexts();
		createProperties();		
		}
	
	public Object getValue()
	{
	    return this.getName();
	}
		

	public void setValue(Object name)
	{
	    if (name instanceof String)
		this.setName((String)name);
	}
	
	
	public boolean canDestroy()
	{
	    return false;
	}
	
	public boolean canCopy()
	{
	    return false;
	}
	
	public boolean canCut()
	{
	    return false;
	}
	
	public boolean canRename()
	{
	    return false;
	}
	
	public SystemAction getDefaultAction()
	{
	    return null;
	}

	// returns actions for this node	
	public SystemAction[] getActions()
	{
	    if (this.jndiactions== null) this.jndiactions=this.createActions();
	    return this.jndiactions;
	}
	
	// creates actions for this node 
	public SystemAction[] createActions()
	{
	    return new SystemAction[] {SystemAction.get(NewAction.class),
				       SystemAction.get(PropertiesAction.class)};	    
	}
	
	// cretes an JNDIType 
	public NewType[] getNewTypes()
	{
	    if (this.jndinewtypes==null)
	    {
		this.jndinewtypes= new NewType[]{ new JndiDataType(this)};
	    }
	    return this.jndinewtypes;
	}
	
	
	public Handle getHandle()
	{
	    return DefaultHandle.createHandle(this);
	}

	/**
	This function add an Context
	**/	
	public void addContext(String context) throws NamingException
	{   
	    JndiNode[] nodes;	    
	    nodes = new JndiNode[1]; 
	    Properties env=parseStartContext(context);		
	    DirContext ctx = new JndiDirContext(env);
	    nodes[0]=new JndiNode(ctx);
	    this.getChildren().add(nodes);    
	}
	
	//The same as above only takes parameters as separated strings
	public void addContext(String label, String factory, String context, String authentification, String principal, String credentials, Vector prop) throws NamingException
	{
	    if (label==null || factory==null || label.equals("") || factory.equals("")) throw new JndiException("Arguments missing");
	    JndiNode[] nodes = new JndiNode[1];
	    Properties env = new Properties();
	    env.put(JndiRootNode.NB_LABEL,label);
	    env.put(Context.INITIAL_CONTEXT_FACTORY,factory);
	    env.put(JndiRootNode.NB_ROOT,"");
	    if (context!=null && context.length()>0) env.put(Context.PROVIDER_URL,context);
	    if (authentification!=null && !authentification.equals("")) env.put(Context.SECURITY_AUTHENTICATION,authentification);
	    if (principal!=null && !principal.equals("")) env.put(Context.SECURITY_PRINCIPAL,principal);
	    if (credentials!=null && !credentials.equals("")) env.put(Context.SECURITY_CREDENTIALS,credentials);
	    for (int i=0; i<prop.size();i++)
		{
		    StringTokenizer tk = new StringTokenizer(((String)prop.elementAt(i)),"=");
		    if (tk.countTokens()!=2) continue;
		    env.put(tk.nextToken(),tk.nextToken());
		}
	    JndiDirContext ctx = new JndiDirContext(env);
	    nodes[0]= new JndiNode(ctx);
	    this.getChildren().add(nodes);
	}
	
	
	
	/**
	This function takes a string and converts it to set of properties
	Returns:
	    Properties if OK.
	    null       if Err **/ 
	protected Properties parseStartContext(String ident) throws NamingException
	{
	    StringTokenizer tk = new StringTokenizer(ident,"|");
	    Properties env = new Properties();
	    
	    try
	    {
		env.put(JndiRootNode.NB_LABEL,tk.nextToken());
		env.put(Context.INITIAL_CONTEXT_FACTORY,tk.nextToken());
		env.put(Context.PROVIDER_URL,tk.nextToken());
	    }catch(NoSuchElementException nee)
	    {
		// The parameters above are obligatory
		throw new JndiException("Argument missing");
	    }
	    try
	    {
		env.put(JndiRootNode.NB_ROOT,tk.nextToken());
	    }catch(NoSuchElementException nee)
	    {
		//If this parameter is missing set it to empty string.
		env.put(JndiRootNode.NB_ROOT,"");
	    }
	    try
	    {
		env.put(Context.SECURITY_AUTHENTICATION, tk.nextToken());
		env.put(Context.SECURITY_PRINCIPAL,tk.nextToken());
		env.put(Context.SECURITY_CREDENTIALS,tk.nextToken());
	    }catch(NoSuchElementException nee)
	    {
		// no more elements
	    }
	    return env;
	}
	
	
	/** 
	Set up initial start contexts 
	**/
	protected void initStartContexts()
	{
	}
	
	
	protected void createProperties()
	{
	}
}
