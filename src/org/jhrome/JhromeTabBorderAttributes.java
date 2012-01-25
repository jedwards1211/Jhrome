
package org.jhrome;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Stroke;

public class JhromeTabBorderAttributes
{
	public JhromeTabBorderAttributes( )
	{
	}
	
	public JhromeTabBorderAttributes( JhromeTabBorderAttributes other )
	{
		copyAttributes( other );
	}
	
	@Override
	public JhromeTabBorderAttributes clone( )
	{
		return new JhromeTabBorderAttributes( this );
	}
	
	public static final JhromeTabBorderAttributes	SELECTED_BORDER		= new JhromeTabBorderAttributes( );
	public static final JhromeTabBorderAttributes	UNSELECTED_BORDER;
	static
	{
		UNSELECTED_BORDER = new JhromeTabBorderAttributes( );
		UNSELECTED_BORDER.topColor = UNSELECTED_BORDER.bottomColor = new Color( 211 , 211 , 222 );
		UNSELECTED_BORDER.outlineColor = new Color( 177 , 160 , 179 );
		UNSELECTED_BORDER.topShadowVisible = false;
	}
	
	public static final JhromeTabBorderAttributes	UNSELECTED_ROLLOVER_BORDER;
	static
	{
		UNSELECTED_ROLLOVER_BORDER = new JhromeTabBorderAttributes( );
		UNSELECTED_ROLLOVER_BORDER.topColor = UNSELECTED_ROLLOVER_BORDER.bottomColor = new Color( 221 , 221 , 229 );
		UNSELECTED_ROLLOVER_BORDER.outlineColor = new Color( 154 , 144 , 156 );
		UNSELECTED_ROLLOVER_BORDER.topShadowVisible = false;
	}
	
	public final Insets								insets				= new Insets( 3 , 15 , 0 , 15 );
	
	public boolean									topShadowVisible	= true;
	public Stroke									shadowStroke		= new BasicStroke( 2.5f );
	public Color									shadowColor			= new Color( 55 , 55 , 55 , 48 );
	
	public boolean									outlineVisible		= true;
	public Stroke									outlineStroke		= new BasicStroke( 1f );
	public Color									outlineColor		= new Color( 149 , 135 , 150 );
	
	public Color									topColor			= new Color( 255 , 255 , 255 );
	public Color									bottomColor			= new Color( 248 , 248 , 248 );
	
	public void copyAttributes( JhromeTabBorderAttributes other )
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
	
	public void interpolateColors( JhromeTabBorderAttributes a , JhromeTabBorderAttributes b , float f )
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
	
}
