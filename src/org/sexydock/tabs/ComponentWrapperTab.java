/*
Copyright 2012 James Edwards

This file is part of Jhrome.

Jhrome is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Jhrome is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Jhrome.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sexydock.tabs;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JButton;

/**
 * An basic implementation of {@link ITab} that allows you to specify your own jhromeTab component. You will probably want to override many of the methods.
 * 
 * @author andy.edwards
 */
public class ComponentWrapperTab implements ITab
{
	public ComponentWrapperTab( Component renderer , Component content )
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
	public void setContent( Component content )
	{
		this.content = content;
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
