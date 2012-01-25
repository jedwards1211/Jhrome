
package org.jhrome;

public class JhromeWindowFactory
{
	private static int	windowCounter	= 1;
	
	public JhromeWindow createWindow( )
	{
		JhromeWindow frame = new JhromeWindow( "Jhrome! " + ( windowCounter++ ) );
		frame.setDefaultCloseOperation( JhromeWindow.DISPOSE_ON_CLOSE );
		return frame;
	}
}
