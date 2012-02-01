
package org.jhrome.demos;

import java.awt.Window;

import org.jhrome.DefaultTabbedPaneWindowFactory;
import org.jhrome.ITab;
import org.jhrome.ITabbedPaneWindow;

public class NonUniformTabWidthDemo implements IJhromeDemo
{
	@Override
	public void start( )
	{
		DefaultTabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( )
		{
			@Override
			public ITabbedPaneWindow createWindow( )
			{
				ITabbedPaneWindow window = super.createWindow( );
				window.getTabbedPane( ).setUseUniformWidth( false );
				return window;
			}
		};
		ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
		Window window = tabbedPaneWindow.getWindow( );
		
		ITab tab1 = tabbedPaneWindow.getTabbedPane( ).getTabFactory( ).createTab( "Tab 1" );
		tabbedPaneWindow.getTabbedPane( ).addTab( tab1 );
		tabbedPaneWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
