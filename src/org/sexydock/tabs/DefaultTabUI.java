
package org.sexydock.tabs;

import java.awt.Point;

import javax.swing.plaf.ComponentUI;

public abstract class DefaultTabUI extends ComponentUI
{
	public abstract boolean isDraggableAt( DefaultTab defaultTab , Point p );
	
	public abstract boolean isSelectableAt( DefaultTab defaultTab , Point p );
	
	public abstract boolean isHoverableAt( DefaultTab defaultTab , Point p );
}
