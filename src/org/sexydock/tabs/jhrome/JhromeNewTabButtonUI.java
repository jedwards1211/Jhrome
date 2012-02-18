/*
Copyright 2012 James Edwards

Thiimport java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

import org.sexydock.tabs.jhrome.JhromeNewTabButtonBorder.Attributes;
 Lesser General Public License
along with Jhrome.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sexydock.tabs.jhrome;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

import org.sexydock.tabs.jhrome.JhromeNewTabButtonBorder.Attributes;

public class JhromeNewTabButtonUI extends BasicButtonUI
{
	public JhromeNewTabButtonUI( )
	{
		init( );
	}
	
	@Override
	public void installUI( JComponent c )
	{
		super.installUI( c );
		button = ( AbstractButton ) c;
		button.setIcon( JhromeNewTabButtonUI.createNewTabButtonIcon( ) );
		button.setFocusable( false );
		button.setContentAreaFilled( false );
		button.setBorder( border );
		button.setContentAreaFilled( false );
		button.setOpaque( false );
		button.setPreferredSize( new Dimension( 26 , 16 ) );
	}
	
	@Override
	public void uninstallUI( JComponent c )
	{
		super.uninstallUI( c );
	}
	
	AbstractButton				button			= null;
	
	JhromeNewTabButtonBorder	border;
	
	Attributes					normalAttrs		= JhromeNewTabButtonBorder.UNSELECTED_ATTRIBUTES.clone( );
	
	float						highlight		= 0f;
	float						highlightSpeed	= 0.2f;
	javax.swing.Timer			highlightTimer;
	
	private void init( )
	{
		border = new JhromeNewTabButtonBorder( );
		
		highlightTimer = new javax.swing.Timer( 30 , new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( button != null )
				{
					button.repaint( );
				}
			}
		} );
	}
	
	@Override
	public void paint( Graphics g , JComponent c )
	{
		button = ( AbstractButton ) c;
		updateBorder( ( AbstractButton ) c );
		paintFill( g , c );
		super.paint( g , c );
	}
	
	public void paintFill( Graphics g , JComponent c )
	{
		border.paint( c , g , 0 , 0 , c.getWidth( ) , c.getHeight( ) , false , true );
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
	
	protected void updateBorder( AbstractButton button )
	{
		if( button.getModel( ).isPressed( ) )
		{
			border.attrs.copy( JhromeNewTabButtonBorder.PRESSED_ATTRIBUTES );
			highlightTimer.stop( );
		}
		else
		{
			float targetHighlight = button.getModel( ).isRollover( ) ? 1 : 0;
			if( highlight != targetHighlight )
			{
				highlight = animate( highlight , targetHighlight );
				highlightTimer.start( );
			}
			else
			{
				highlightTimer.stop( );
			}
			border.attrs.copy( JhromeNewTabButtonBorder.UNSELECTED_ATTRIBUTES );
			border.attrs.interpolateColors( JhromeNewTabButtonBorder.UNSELECTED_ATTRIBUTES , JhromeNewTabButtonBorder.ROLLOVER_ATTRIBUTES , highlight );
		}
	}
	
	public static Icon createNewTabButtonIcon( )
	{
		BufferedImage image = new BufferedImage( 10 , 10 , BufferedImage.TYPE_INT_ARGB );
		Path2D path = new Path2D.Double( );
		path.moveTo( 3 , 0 );
		path.lineTo( 6 , 0 );
		path.lineTo( 6 , 3 );
		path.lineTo( 9 , 3 );
		path.lineTo( 9 , 6 );
		path.lineTo( 6 , 6 );
		path.lineTo( 6 , 9 );
		path.lineTo( 3 , 9 );
		path.lineTo( 3 , 6 );
		path.lineTo( 0 , 6 );
		path.lineTo( 0 , 3 );
		path.lineTo( 3 , 3 );
		path.lineTo( 3 , 0 );
		
		Graphics2D g2 = ( Graphics2D ) image.getGraphics( );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON );
		
		g2.setColor( Color.WHITE );
		g2.fill( path );
		g2.setColor( JhromeTabBorderAttributes.UNSELECTED_BORDER.outlineColor );
		g2.setStroke( new BasicStroke( 1.1f ) );
		g2.draw( path );
		
		return new ImageIcon( image );
	}
}
