
package org.sexydock.tabs.launchers;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.SwingUtilities;

import org.sexydock.tabs.DefaultTab;
import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.ITabbedPaneWindowFactory;
import org.sexydock.tabs.TabbedPane;

public class JhromeTest
{
	public static void main( String[ ] args )
	{
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				ITabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
				ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
				Window window = tabbedPaneWindow.getWindow( );
				TabbedPane tabbedPane = tabbedPaneWindow.getTabbedPane( );
				
				for( int i = 0 ; i < 1 ; i++ )
				{
					DefaultTab defaultTab = new DefaultTab( "Tab " + i );
					tabbedPane.addTab( i , defaultTab );
					tabbedPane.setSelectedTab( defaultTab );
				}
				
				window.setSize( new Dimension( 800 , 600 ) );
				window.setLocationRelativeTo( null );
				window.setVisible( true );
			}
		} );
	}
}
