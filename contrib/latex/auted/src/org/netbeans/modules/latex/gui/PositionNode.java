/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui;

import java.awt.Point;

/**
 *
 * @author Jan Lahoda
 */
public abstract class PositionNode extends NamedNode {
    public static final String PROP_X = "x";
    public static final String PROP_Y = "y";

    private int x,y;
    
    /** Creates a new instance of PositionNode */
    public PositionNode(int x, int y) {
        this.x = x;
        this.y = y;        
    }
    
    /** Getter for property x.
     * @return Value of property x.
     *
     */
    public int getX() {
        return x;
    }
    
    /** Getter for property y.
     * @return Value of property y.
     *
     */
    public int getY() {
        return y;
    }
    
    /** Setter for property x.
     * @param x New value of property x.
     *
     */
    public void setX(int x) {
        this.x = x;
        firePropertyChange(PROP_X, null, null);
    }
    
    /** Setter for property y.
     * @param y New value of property y.
     *
     */
    public void setY(int y) {
        this.y = y;
        firePropertyChange(PROP_Y, null, null);
    }
    
    public Point getPosition() {
        return new Point((int) (getX() * UIProperties.getGridSize().getWidth()),
                         (int) (getY() * UIProperties.getGridSize().getHeight()));
    }
    
    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        PositionNode pn = (PositionNode) node;
        
        return getX() == pn.getX() && getY() == pn.getY();
    }
}
