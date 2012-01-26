package org.jhrome;

import java.awt.Color;

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
	
}
