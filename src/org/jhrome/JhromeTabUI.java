
package org.jhrome;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

public class JhromeTabUI extends ComponentUI
{
	public JhromeTabUI( )
	{
		init( );
	}
	
	JhromeTab					tab;
	
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
		if( tab != null )
		{
			tab.repaint( );
		}
	}
	
	@Override
	public void installUI( JComponent c )
	{
		super.installUI( c );
		tab = ( JhromeTab ) c;
		
		tab.setBorder( compoundBorder );
		
		tab.label.setFont( tab.label.getFont( ).deriveFont( Font.PLAIN ) );
		tab.label.setForeground( unselectedLabelColor );
		
		tab.closeButton.setUI( new BasicButtonUI( ) );
		tab.closeButton.setBorderPainted( false );
		tab.closeButton.setContentAreaFilled( false );
		tab.closeButton.setOpaque( false );
		tab.closeButton.setIcon( JhromeTabCloseButtonIcons.getJhromeNormalIcon( ) );
		tab.closeButton.setRolloverIcon( JhromeTabCloseButtonIcons.getJhromeRolloverIcon( ) );
		tab.closeButton.setPressedIcon( JhromeTabCloseButtonIcons.getJhromePressedIcon( ) );
		tab.closeButton.setPreferredSize( new Dimension( tab.closeButton.getIcon( ).getIconWidth( ) + 1 , tab.closeButton.getIcon( ).getIconHeight( ) + 1 ) );
		
		tab.setOpaque( false );
		
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
	public void paint( Graphics g , JComponent c )
	{
		tab = ( JhromeTab ) c;
		update( tab );
		super.paint( g , c );
	}
	
	protected void update( JhromeTab tab )
	{
		if( tab.isSelected( ) )
		{
			outerBorder.attrs.copyAttributes( selectedAttributes );
			highlightTimer.stop( );
			tab.label.setForeground( selectedLabelColor );
		}
		else
		{
			tab.label.setForeground( unselectedLabelColor );
			float targetHighlight = tab.isRollover( ) ? 1f : 0f;
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
	
}
