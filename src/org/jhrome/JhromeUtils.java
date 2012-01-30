
package org.jhrome;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

public class JhromeUtils
{
	
	static Color interpolate( Color a , Color b , float f )
	{
		float rf = 1 - f;
		int red = ( int ) ( a.getRed( ) * rf + b.getRed( ) * f );
		int green = ( int ) ( a.getGreen( ) * rf + b.getGreen( ) * f );
		int blue = ( int ) ( a.getBlue( ) * rf + b.getBlue( ) * f );
		int alpha = ( int ) ( a.getAlpha( ) * rf + b.getAlpha( ) * f );
		
		return new Color( red , green , blue , alpha );
	}
	
	/**
	 * There is a bug in {@link Rectangle#inside(int, int)}!
	 */
	static boolean contains( Rectangle r , Point p )
	{
		return p.x >= r.x && p.y >= r.y && p.x < r.x + r.width && p.y < r.y + r.height;
	}
	
	static boolean contains( Component c , Point p )
	{
		return contains( c.getBounds( ) , p );
	}
}
