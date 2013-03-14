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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import org.sexydock.tabs.jhrome.JhromeTabBorderAttributes;
import org.sexydock.tabs.jhrome.JhromeTabCloseButtonIcons;

/**
 * The UI for a {@link Tab}.
 * 
 * @author andy.edwards
 */
public class BasicTabUI extends TabUI
{
	public BasicTabUI( )
	{
		init( );
	}
	
	Tab							tab;
	
	JLabel						label;
	Component					displayedTabComponent;
	JButton						closeButton;
	
	Color						selectedLabelColor		= Color.BLACK;
	Color						unselectedLabelColor	= new Color( 80 , 80 , 80 );
	
	PropertyChangeHandler		propertyChangeHandler	= new PropertyChangeHandler( );
	
	public static final String	CLOSE_BUTTON_VISIBLE	= "closeButtonVisible";
	
	private void init( )
	{
		label = new JLabel( );
		closeButton = new JButton( "X" );
		
		label.setFont( label.getFont( ).deriveFont( Font.PLAIN ) );
		label.setForeground( unselectedLabelColor );
		
		closeButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JTabbedPane tabbedPane = JhromeTabbedPaneUI.getJTabbedPaneAncestor( tab );
				int index = tabbedPane.indexOfComponent( tab.getContent( ) );
				if( index >= 0 )
				{
					tabbedPane.removeTabAt( index );
				}
			}
		} );
	}
	
	protected Tab getTab( )
	{
		return tab;
	}
	
	protected void onHighlightTimerEvent( ActionEvent e )
	{
		if( tab != null )
		{
			tab.repaint( );
		}
	}
	
	public static BasicTabUI createUI( JComponent c )
	{
		return new BasicTabUI( );
	}
	
	@SuppressWarnings( "serial" )
	private class TabLayout extends BorderLayout
	{
		@Override
		public Dimension minimumLayoutSize( Container target )
		{
			Dimension minSize = super.minimumLayoutSize( target );
			if( minSize != null )
			{
				Insets insets = target.getInsets( );
				minSize.width = insets.left + insets.right;
			}
			return minSize;
		}
	}
	
	@Override
	public void installUI( JComponent c )
	{
		super.installUI( c );
		tab = ( Tab ) c;
		
		tab.setLayout( new TabLayout( ) );
		tab.add( label , BorderLayout.CENTER );
		tab.add( closeButton , BorderLayout.EAST );
		
		tab.addPropertyChangeListener( propertyChangeHandler );
	}
	
	@Override
	public void uninstallUI( JComponent c )
	{
		if( c != tab )
		{
			throw new IllegalArgumentException( "c is not the Tab this UI is bound to" );
		}
		
		super.uninstallUI( c );
		
		tab = ( Tab ) c;
		
		tab.removePropertyChangeListener( propertyChangeHandler );
		tab.remove( label );
		tab.remove( closeButton );
		tab.setLayout( null );
		
		tab = null;
	}
	
	@Override
	public void paint( Graphics g , JComponent c )
	{
		tab = ( Tab ) c;
		update( );
		super.paint( g , c );
	}
	
	protected void update( )
	{
		label.setText( tab.getTitle( ) );
		label.setIcon( tab.getIcon( ) );
		
		boolean closeButtonVisible = false;
		Object cbvProp = tab.getClientProperty( CLOSE_BUTTON_VISIBLE );
		if( cbvProp != null && cbvProp instanceof Boolean )
		{
			closeButtonVisible = ( Boolean ) cbvProp;
		}
		else
		{
			JTabbedPane tabbedPane = JhromeTabbedPaneUI.getJTabbedPaneAncestor( tab );
			if( tabbedPane != null )
			{
				cbvProp = tabbedPane.getClientProperty( CLOSE_BUTTON_VISIBLE );
				if( cbvProp != null && cbvProp instanceof Boolean )
				{
					closeButtonVisible = ( Boolean ) cbvProp;
				}
			}
		}
		
		closeButton.setVisible( closeButtonVisible );
		
		if( tab.getTabComponent( ) != displayedTabComponent )
		{
			if( displayedTabComponent != null )
			{
				tab.remove( displayedTabComponent );
			}
			
			displayedTabComponent = tab.getTabComponent( );
			
			if( displayedTabComponent != null )
			{
				tab.remove( label );
				tab.add( displayedTabComponent , BorderLayout.CENTER );
			}
			else
			{
				tab.add( label , BorderLayout.CENTER );
			}
		}
		
		if( tab.isSelected( ) )
		{
			label.setForeground( selectedLabelColor );
		}
		else
		{
			label.setForeground( unselectedLabelColor );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isDraggableAt(java.awt.Point)
	 */
	@Override
	public boolean isDraggableAt( Tab tab , Point p )
	{
		return isHoverableAt( tab , p ) && !closeButton.contains( SwingUtilities.convertPoint( tab , p , closeButton ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isSelectableAt(java.awt.Point)
	 */
	@Override
	public boolean isSelectableAt( Tab tab , Point p )
	{
		return isDraggableAt( tab , p );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isHoverableAt(java.awt.Point)
	 */
	@Override
	public boolean isHoverableAt( Tab tab , Point p )
	{
		Insets insets = tab.getInsets( );
		Rectangle bounds = tab.getBounds( );
		bounds.x = insets.left;
		bounds.width -= insets.left + insets.right;
		bounds.y = insets.top;
		bounds.height -= insets.top + insets.bottom;
		return bounds.contains( p );
	}
	
	public Color getSelectedLabelColor( )
	{
		return selectedLabelColor;
	}
	
	public void setSelectedLabelColor( Color selectedLabelColor )
	{
		this.selectedLabelColor = selectedLabelColor;
	}
	
	public Color getUnselectedLabelColor( )
	{
		return unselectedLabelColor;
	}
	
	public void setUnselectedLabelColor( Color unselectedLabelColor )
	{
		this.unselectedLabelColor = unselectedLabelColor;
	}
	
	public JButton getCloseButton( )
	{
		return closeButton;
	}
	
	public JLabel getLabel( )
	{
		return label;
	}
	
	private class PropertyChangeHandler implements PropertyChangeListener
	{
		@Override
		public void propertyChange( PropertyChangeEvent evt )
		{
			update( );
		}
	}
}
