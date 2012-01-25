
package org.jhrome;

import java.awt.Dimension;

public class JhromeTest
{
	public static void main( String[ ] args )
	{
		JhromeWindowFactory windowFactory = new JhromeWindowFactory( );
		JhromeWindow window = windowFactory.createWindow( );
		
		JhromeTabbedPane tabbedPane = window.getTabbedPane( );
		
		for( int i = 0 ; i < 1 ; i++ )
		{
			JhromeTab tab = new JhromeTab( "Tab " + i );
			tabbedPane.addTab( i , tab );
			tabbedPane.setSelectedTab( tab );
		}
		
		window.setSize( new Dimension( 800 , 600 ) );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
