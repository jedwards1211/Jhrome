
package org.jhrome;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.SwingUtilities;

public class JhromeTest
{
	public static void main( String[ ] args )
	{
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				IJhromeWindowFactory windowFactory = new JhromeWindowFactory( );
				IJhromeWindow jhromeWindow = windowFactory.createWindow( );
				Window window = jhromeWindow.getWindow( );
				JhromeTabbedPane tabbedPane = jhromeWindow.getTabbedPane( );
				
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
		} );
	}
}
