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

package org.netbeans.api.vcs.commands;

import org.openide.filesystems.FileObject;

/**
 * This interface represents the VCS command, that is executed to acually perform
 * a VCS action. <p>
 * This interface have sub-interfaces for individual VCS commands. The intended
 * usage is like:
 * <pre>
 * AddCommand addCmd =
 *     (AddCommand) VcsManager.getDefault().createCommand(AddCommand.class,
 *                                    new FileObject[] { fileToAdd });
 * addCmd.setMessage("Some new cool stuff");
 * addCmd.execute();
 * </pre>
 *
 * @author  Martin Entlicher
 */
public interface Command {
    
    /**
     * Get the name of the command.
     */
    public String getName();
    
    /**
     * Get the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public String getDisplayName();
    
    /**
     * Set files to act on. Only files that are applicable to this command can
     * be set. Use {@link #getApplicableFiles} method to find out which files
     * are applicable.
     * @param files the array of applicable files.
     * @throws an {@link IllegalArgumentException} when one or more files
     *         are not applicable.
     */
    public void setFiles(FileObject[] files);
    
    /**
     * Get files this command acts on.
     */
    public FileObject[] getFiles();
    
    /**
     * Find, whether this command can act on a set of files.
     * @param files The array of files to inspect
     * @return an array of files the command can act on or <code>null</code> when
     * it can not act on any file listed.
     */
    public FileObject[] getApplicableFiles(FileObject[] files);
    
    /**
     * Use this method to set or unset the GUI mode for the command execution.
     * If the GUI mode is set to false, the command tasks are not permitted to open
     * any GUI components. Setting this property does not influence the already
     * running tasks. The default is GUI mode (<code>true</code>).
     * @param gui <code>true</code> for GUI mode, <code>false</code> for non-GUI
     *            mode
     */
    public void setGUIMode(boolean gui);
    
    /**
     * Test whether the command is in GUI mode or not.
     * @return <code>true</code> for GUI mode, <code>false</code> for non-GUI
     *         mode
     */
    public boolean isGUIMode();
    
    /**
     * Use this method to set or unset the expert mode for the command execution.
     * If the expert mode is set to false, the command customization should be
     * simpler and tasks should present simpler GUI components.
     * Setting this property does not influence the already
     * running tasks. The default is non-expert mode (<code>false</code>).
     * @param expert <code>true</code> for expert mode, <code>false</code> for
     *               non-expert mode
     */
    public void setExpertMode(boolean expert);
    
    /**
     * Test whether the command is in expert mode or not.
     * @return <code>true</code> for expert mode, <code>false</code> for non-expert
     *         mode
     */
    public boolean isExpertMode();
    
    /**
     * Execute the command.
     */
    public CommandTask execute();
}

