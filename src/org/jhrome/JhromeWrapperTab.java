
package org.jhrome;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JButton;

public class JhromeWrapperTab implements IJhromeTab
{
	public JhromeWrapperTab( Component renderer , Component content )
	{
		this.renderer = renderer;
		this.content = content;
	}
	
	Component	renderer;
	Component	content;
	
	boolean		selected	= false;
	boolean		rollover	= false;
	
	@Override
	public Component getRenderer( )
	{
		return renderer;
	}
	
	@Override
	public Component getContent( )
	{
		return content;
	}
	
	@Override
	public JButton getCloseButton( )
	{
		return null;
	}
	
	@Override
	public boolean isDraggableAt( Point p )
	{
		return renderer.contains( p );
	}
	
	@Override
	public boolean isSelectableAt( Point p )
	{
		return renderer.contains( p );
	}
	
	@Override
	public boolean isHoverableAt( Point p )
	{
		return renderer.contains( p );
	}
	
	@Override
	public boolean isSelected( )
	{
		return selected;
	}
	
	@Override
	public void setSelected( boolean selected )
	{
		this.selected = selected;
	}
	
	@Override
	public boolean isRollover( )
	{
		return rollover;
	}
	
	@Override
	public void setRollover( boolean rollover )
	{
		this.rollover = rollover;
	}
	
}
