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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.sexydock.tabs.jhrome.JhromeTabUI;

/**
 * The default implementation of {@link ITab}. Contains a title label and close button. The label can be replaced with any {@link Component}, and the
 * jhromeTab's look and feel can be customized with {@link DefaultTabUI}.
 * 
 * @author andy.edwards
 */
@SuppressWarnings( "serial" )
public class DefaultTab extends JComponent implements ITab
{
	private static final String	uiClassId	= DefaultTabUI.class.getName( );
	
	static
	{
		UIManager.getDefaults( ).put( uiClassId , JhromeTabUI.class.getName( ) );
	}
	
	JLabel						label;
	Component					overrideLabel;
	JButton						closeButton;
	
	Component					content;
	
	boolean						selected;
	boolean						rollover;
	
	public DefaultTab( String title )
	{
		this( title , null );
	}
	
	public DefaultTab( String title , Component content )
	{
		setLayout( new BorderLayout( ) );
		
		label = new JLabel( title );
		add( label , BorderLayout.CENTER );
		
		closeButton = new JButton( );
		add( closeButton , BorderLayout.EAST );
		
		this.content = content;
		
		updateUI( );
	}
	
	public void setTitle( String title )
	{
		label.setText( title );
	}
	
	public void setOverrideLabel( Component component )
	{
		if( overrideLabel != component )
		{
			if( overrideLabel != null )
			{
				remove( overrideLabel );
			}
			else
			{
				remove( label );
			}
			
			overrideLabel = component;
			
			if( component != null )
			{
				add( component , BorderLayout.CENTER );
			}
			else
			{
				add( label , BorderLayout.CENTER );
			}
		}
	}
	
	public Component getOverrideLabel( )
	{
		return overrideLabel;
	}
	
	@Override
	public Dimension getMinimumSize( )
	{
		Dimension min = super.getMinimumSize( );
		if( min != null )
		{
			Insets insets = getInsets( );
			min.width = insets.left + insets.right;
		}
		return min;
	}
	
	@Override
	public void paint( Graphics g )
	{
		Dimension minSize = getMinimumSize( );
		if( minSize != null )
		{
			if( getWidth( ) < minSize.getWidth( ) )
			{
				return;
			}
		}
		super.paint( g );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#getRenderer()
	 */
	@Override
	public Component getRenderer( )
	{
		return this;
	}
	
	@Override
	public void setContent( Component content )
	{
		this.content = content;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#getContent()
	 */
	@Override
	public Component getContent( )
	{
		return content;
	}
	
	public JLabel getLabel( )
	{
		return label;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#getCloseButton()
	 */
	@Override
	public JButton getCloseButton( )
	{
		return closeButton;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isDraggableAt(java.awt.Point)
	 */
	@Override
	public boolean isDraggableAt( Point p )
	{
		if( ui != null && ui instanceof DefaultTabUI )
		{
			DefaultTabUI jui = ( DefaultTabUI ) ui;
			return jui.isDraggableAt( this , p );
		}
		return isHoverableAt( p ) && !closeButton.contains( SwingUtilities.convertPoint( this , p , closeButton ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isSelectableAt(java.awt.Point)
	 */
	@Override
	public boolean isSelectableAt( Point p )
	{
		if( ui != null && ui instanceof DefaultTabUI )
		{
			DefaultTabUI jui = ( DefaultTabUI ) ui;
			return jui.isDraggableAt( this , p );
		}
		return isDraggableAt( p );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isHoverableAt(java.awt.Point)
	 */
	@Override
	public boolean isHoverableAt( Point p )
	{
		if( ui != null && ui instanceof DefaultTabUI )
		{
			DefaultTabUI jui = ( DefaultTabUI ) ui;
			return jui.isDraggableAt( this , p );
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isSelected()
	 */
	@Override
	public boolean isSelected( )
	{
		return selected;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#setSelected(boolean)
	 */
	@Override
	public void setSelected( boolean selected )
	{
		if( this.selected != selected )
		{
			this.selected = selected;
			repaint( );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isRollover()
	 */
	@Override
	public boolean isRollover( )
	{
		return rollover;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#setRollover(boolean)
	 */
	@Override
	public void setRollover( boolean rollover )
	{
		if( this.rollover != rollover )
		{
			this.rollover = rollover;
			repaint( );
		}
	}
	
	@Override
	public String getUIClassID( )
	{
		return uiClassId;
	}
	
	@Override
	public void updateUI( )
	{
		setUI( ( DefaultTabUI ) UIManager.getUI( this ) );
	}
	
	public void setUI( DefaultTabUI ui )
	{
		super.setUI( ui );
	}
}
