
package org.sexydock.tabs.demos;

import java.awt.Window;

import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITab;
import org.sexydock.tabs.ITabbedPaneDnDPolicy;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.TabbedPane;

public class NoTearAwayDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		DefaultTabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
		ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
		Window window = tabbedPaneWindow.getWindow( );
		
		ITab tab1 = tabbedPaneWindow.getTabbedPane( ).getTabFactory( ).createTab( "Try to tear tabs away!" );
		tabbedPaneWindow.getTabbedPane( ).addTab( tab1 );
		tabbedPaneWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		tabbedPaneWindow.getTabbedPane( ).setDnDPolicy( new ITabbedPaneDnDPolicy( )
		{
			@Override
			public boolean isTearAwayAllowed( TabbedPane tabbedPane , ITab tab )
			{
				return false;
			}
			
			@Override
			public boolean isSnapInAllowed( TabbedPane tabbedPane , ITab tab )
			{
				return true;
			}
		} );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
