
package org.jhrome.launchers;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.SwingUtilities;

import org.jhrome.DefaultTabbedPaneWindowFactory;
import org.jhrome.ITabbedPaneWindow;
import org.jhrome.ITabbedPaneWindowFactory;
import org.jhrome.DefaultTab;
import org.jhrome.TabbedPane;

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
