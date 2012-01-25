
package org.jhrome;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import javax.swing.border.Border;

public class JhromeTabBorder implements Border
{
	public static final JhromeTabBorder	SELECTED_BORDER		= new JhromeTabBorder( );
	public static final JhromeTabBorder	UNSELECTED_BORDER;
	static
	{
		UNSELECTED_BORDER = new JhromeTabBorder( );
		UNSELECTED_BORDER.topColor = UNSELECTED_BORDER.bottomColor = new Color( 211 , 211 , 222 );
		UNSELECTED_BORDER.outlineColor = new Color( 177 , 160 , 179 );
		UNSELECTED_BORDER.topShadowVisible = false;
	}
	
	public static final JhromeTabBorder	UNSELECTED_ROLLOVER_BORDER;
	static
	{
		UNSELECTED_ROLLOVER_BORDER = new JhromeTabBorder( );
		UNSELECTED_ROLLOVER_BORDER.topColor = UNSELECTED_ROLLOVER_BORDER.bottomColor = new Color( 221 , 221 , 229 );
		UNSELECTED_ROLLOVER_BORDER.outlineColor = new Color( 154 , 144 , 156 );
		UNSELECTED_ROLLOVER_BORDER.topShadowVisible = false;
	}
	
	public final Insets					insets				= new Insets( 3 , 15 , 0 , 15 );
	
	public boolean						topShadowVisible	= true;
	public Stroke						shadowStroke		= new BasicStroke( 2.5f );
	public Color						shadowColor			= new Color( 55 , 55 , 55 , 48 );
	
	public boolean						outlineVisible		= true;
	public Stroke						outlineStroke		= new BasicStroke( 1f );
	public Color						outlineColor		= new Color( 149 , 135 , 150 );
	
	public Color						topColor			= new Color( 255 , 255 , 255 );
	public Color						bottomColor			= new Color( 248 , 248 , 248 );
	
	public void copyAttributes( JhromeTabBorder other )
	{
		insets.set( other.insets.top , other.insets.left , other.insets.bottom , other.insets.right );
		
		topShadowVisible = other.topShadowVisible;
		shadowStroke = other.shadowStroke;
		shadowColor = other.shadowColor;
		
		outlineVisible = other.outlineVisible;
		outlineStroke = other.outlineStroke;
		outlineColor = other.outlineColor;
		
		topColor = other.topColor;
		bottomColor = other.bottomColor;
	}
	
	public void interpolateColors( JhromeTabBorder a , JhromeTabBorder b , float f )
	{
		shadowColor = interpolate( a.shadowColor , b.shadowColor , f );
		outlineColor = interpolate( a.outlineColor , b.outlineColor , f );
		topColor = interpolate( a.topColor , b.topColor , f );
		bottomColor = interpolate( a.bottomColor , b.bottomColor , f );
	}
	
	private static Color interpolate( Color a , Color b , float f )
	{
		float rf = 1 - f;
		int red = ( int ) ( a.getRed( ) * rf + b.getRed( ) * f );
		int green = ( int ) ( a.getGreen( ) * rf + b.getGreen( ) * f );
		int blue = ( int ) ( a.getBlue( ) * rf + b.getBlue( ) * f );
		int alpha = ( int ) ( a.getAlpha( ) * rf + b.getAlpha( ) * f );
		
		return new Color( red , green , blue , alpha );
	}
	
	private Path2D	openPath;
	private Path2D	closedPath;
	
	private void updatePaths( int x , int y , int width , int height )
	{
		if( width < insets.left + insets.right )
		{
			return;
		}
		
		openPath = new Path2D.Double( Path2D.WIND_EVEN_ODD );
		
		openPath.moveTo( x , y + height - insets.bottom );
		openPath.curveTo( x + insets.left / 2 , y + height - insets.bottom , x + insets.left / 2 , y + insets.top , x + insets.left , y + insets.top );
		openPath.lineTo( x + width - insets.right , y + insets.top );
		openPath.curveTo( x + width - insets.right / 2 , y + insets.top , x + width - insets.right / 2 , y + height - insets.bottom , x + width , y + height - insets.bottom );
		
		closedPath = ( Path2D ) openPath.clone( );
		closedPath.closePath( );
	}
	
	public boolean contains( Point p )
	{
		if( closedPath != null )
		{
			return closedPath.contains( p );
		}
		return false;
	}
	
	@Override
	public void paintBorder( Component c , Graphics g , int x , int y , int width , int height )
	{
		if( width < insets.left + insets.right )
		{
			return;
		}
		
		Graphics2D g2 = ( Graphics2D ) g;
		
		updatePaths( x , y , width , height );
		
		Object prevAntialias = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON );
		
		Stroke prevStroke = g2.getStroke( );
		Paint prevPaint = g2.getPaint( );
		
		if( topShadowVisible )
		{
			g2.setStroke( shadowStroke );
			g2.setColor( shadowColor );
			g2.draw( openPath );
		}
		
		g2.setPaint( new GradientPaint( 0 , y , topColor , 0 , y + height - 1 , bottomColor ) );
		g2.fill( closedPath );
		
		if( outlineVisible )
		{
			g2.setStroke( outlineStroke );
			g2.setColor( outlineColor );
			g2.draw( openPath );
		}
		
		g2.setPaint( prevPaint );
		g2.setStroke( prevStroke );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , prevAntialias );
	}
	
	@Override
	public Insets getBorderInsets( Component c )
	{
		return ( Insets ) insets.clone( );
	}
	
	@Override
	public boolean isBorderOpaque( )
	{
		return true;
	}
}
