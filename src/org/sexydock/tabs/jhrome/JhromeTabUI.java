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

package org.sexydock.tabs.jhrome;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import org.sexydock.tabs.DefaultTab;
import org.sexydock.tabs.DefaultTabUI;

/**
 * The UI for a {@link DefaultTab}.
 * 
 * @author andy.edwards
 */
public class JhromeTabUI extends DefaultTabUI
{
	public JhromeTabUI( )
	{
		init( );
	}
	
	DefaultTab					defaultTab;
	
	CompoundBorder				compoundBorder;
	
	JhromeTabBorder				outerBorder;
	JhromeTabBorderAttributes	selectedAttributes		= JhromeTabBorderAttributes.SELECTED_BORDER.clone( );
	JhromeTabBorderAttributes	rolloverAttributes		= JhromeTabBorderAttributes.UNSELECTED_ROLLOVER_BORDER.clone( );
	JhromeTabBorderAttributes	normalAttributes		= JhromeTabBorderAttributes.UNSELECTED_BORDER.clone( );
	
	EmptyBorder					innerBorder;
	float						highlight				= 0f;
	float						highlightSpeed			= 0.1f;
	javax.swing.Timer			highlightTimer;
	
	Color						selectedLabelColor		= Color.BLACK;
	Color						unselectedLabelColor	= new Color( 80 , 80 , 80 );
	
	private void init( )
	{
		innerBorder = new EmptyBorder( 5 , 5 , 5 , 0 );
		outerBorder = new JhromeTabBorder( );
		outerBorder.attrs.copyAttributes( normalAttributes );
		compoundBorder = new CompoundBorder( outerBorder , innerBorder );
		
		highlightTimer = new javax.swing.Timer( 30 , new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				onHighlightTimerEvent( e );
			}
		} );
	}
	
	protected void onHighlightTimerEvent( ActionEvent e )
	{
		if( defaultTab != null )
		{
			defaultTab.repaint( );
		}
	}
	
	public static JhromeTabUI createUI( JComponent c )
	{
		return new JhromeTabUI( );
	}
	
	@Override
	public void installUI( JComponent c )
	{
		super.installUI( c );
		defaultTab = ( DefaultTab ) c;
		
		defaultTab.setBorder( compoundBorder );
		
		defaultTab.getLabel( ).setFont( defaultTab.getLabel( ).getFont( ).deriveFont( Font.PLAIN ) );
		defaultTab.getLabel( ).setForeground( unselectedLabelColor );
		
		defaultTab.getCloseButton( ).setUI( new BasicButtonUI( ) );
		defaultTab.getCloseButton( ).setBorderPainted( false );
		defaultTab.getCloseButton( ).setContentAreaFilled( false );
		defaultTab.getCloseButton( ).setOpaque( false );
		defaultTab.getCloseButton( ).setIcon( JhromeTabCloseButtonIcons.getJhromeNormalIcon( ) );
		defaultTab.getCloseButton( ).setRolloverIcon( JhromeTabCloseButtonIcons.getJhromeRolloverIcon( ) );
		defaultTab.getCloseButton( ).setPressedIcon( JhromeTabCloseButtonIcons.getJhromePressedIcon( ) );
		defaultTab.getCloseButton( ).setPreferredSize( new Dimension( defaultTab.getCloseButton( ).getIcon( ).getIconWidth( ) + 1 , defaultTab.getCloseButton( ).getIcon( ).getIconHeight( ) + 1 ) );
		
		defaultTab.setOpaque( false );
		
		highlightTimer = new javax.swing.Timer( 30 , new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				onHighlightTimerEvent( e );
			}
		} );
	}
	
	@Override
	public void uninstallUI( JComponent c )
	{
		super.uninstallUI( c );
		
		defaultTab = ( DefaultTab ) c;
		defaultTab.setBorder( null );
		
		defaultTab.getCloseButton( ).setBorderPainted( true );
		defaultTab.getCloseButton( ).setContentAreaFilled( true );
		defaultTab.getCloseButton( ).setOpaque( true );
		defaultTab.getCloseButton( ).setIcon( null );
		defaultTab.getCloseButton( ).setRolloverIcon( null );
		defaultTab.getCloseButton( ).setPressedIcon( null );
		defaultTab.setPreferredSize( null );
		
		defaultTab = null;
	}
	
	@Override
	public void paint( Graphics g , JComponent c )
	{
		defaultTab = ( DefaultTab ) c;
		update( defaultTab );
		super.paint( g , c );
	}
	
	protected void update( DefaultTab defaultTab )
	{
		if( defaultTab.isSelected( ) )
		{
			outerBorder.attrs.copyAttributes( selectedAttributes );
			highlightTimer.stop( );
			defaultTab.getLabel( ).setForeground( selectedLabelColor );
		}
		else
		{
			defaultTab.getLabel( ).setForeground( unselectedLabelColor );
			float targetHighlight = defaultTab.isRollover( ) ? 1f : 0f;
			if( highlight != targetHighlight )
			{
				highlight = animate( highlight , targetHighlight );
				highlightTimer.start( );
			}
			else
			{
				highlightTimer.stop( );
			}
			outerBorder.attrs.copyAttributes( rolloverAttributes );
			outerBorder.attrs.interpolateColors( normalAttributes , rolloverAttributes , highlight );
		}
	}
	
	protected float animate( float current , float target )
	{
		if( current < target )
		{
			current = Math.min( target , current + highlightSpeed );
		}
		else if( current > target )
		{
			current = Math.max( target , current - highlightSpeed );
		}
		return current;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isDraggableAt(java.awt.Point)
	 */
	@Override
	public boolean isDraggableAt( DefaultTab defaultTab , Point p )
	{
		return isHoverableAt( defaultTab , p ) && !defaultTab.getCloseButton( ).contains( SwingUtilities.convertPoint( defaultTab , p , defaultTab.getCloseButton( ) ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isSelectableAt(java.awt.Point)
	 */
	@Override
	public boolean isSelectableAt( DefaultTab defaultTab , Point p )
	{
		return isDraggableAt( defaultTab , p );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isHoverableAt(java.awt.Point)
	 */
	@Override
	public boolean isHoverableAt( DefaultTab defaultTab , Point p )
	{
		return outerBorder.contains( p );
	}
	
	public JhromeTabBorderAttributes getSelectedAttributes( )
	{
		return selectedAttributes;
	}
	
	public void setSelectedAttributes( JhromeTabBorderAttributes selectedAttributes )
	{
		this.selectedAttributes = selectedAttributes;
	}
	
	public JhromeTabBorderAttributes getRolloverAttributes( )
	{
		return rolloverAttributes;
	}
	
	public void setRolloverAttributes( JhromeTabBorderAttributes rolloverAttributes )
	{
		this.rolloverAttributes = rolloverAttributes;
	}
	
	public JhromeTabBorderAttributes getNormalAttributes( )
	{
		return normalAttributes;
	}
	
	public void setNormalAttributes( JhromeTabBorderAttributes normalAttributes )
	{
		this.normalAttributes = normalAttributes;
	}
	
	public float getHighlightSpeed( )
	{
		return highlightSpeed;
	}
	
	public void setHighlightSpeed( float highlightSpeed )
	{
		this.highlightSpeed = highlightSpeed;
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
	
}
