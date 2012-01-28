
package org.jhrome;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JButton;

public interface IJhromeTab
{
	
	public abstract Component getRenderer( );
	
	public abstract Component getContent( );
	
	public abstract void setContent( Component content );
	
	public abstract JButton getCloseButton( );
	
	public abstract boolean isDraggableAt( Point p );
	
	public abstract boolean isSelectableAt( Point p );
	
	public abstract boolean isHoverableAt( Point p );
	
	public abstract boolean isSelected( );
	
	public abstract void setSelected( boolean selected );
	
	public abstract boolean isRollover( );
	
	public abstract void setRollover( boolean rollover );
	
}
