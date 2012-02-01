
package org.jhrome.demos;

import java.awt.Window;

import org.jhrome.DefaultTabbedPaneWindowFactory;
import org.jhrome.ITab;
import org.jhrome.ITabbedPaneDnDPolicy;
import org.jhrome.ITabbedPaneWindow;
import org.jhrome.TabbedPane;

public class NoSnapInDemo implements IJhromeDemo
{
	@Override
	public void start( )
	{
		DefaultTabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
		ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
		Window window = tabbedPaneWindow.getWindow( );
		
		final ITab tab1 = tabbedPaneWindow.getTabbedPane( ).getTabFactory( ).createTab( "Try to snap tabs in!" );
		tabbedPaneWindow.getTabbedPane( ).addTab( tab1 );
		tabbedPaneWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		tabbedPaneWindow.getTabbedPane( ).setDnDPolicy( new ITabbedPaneDnDPolicy( )
		{
			@Override
			public boolean isTearAwayAllowed( TabbedPane tabbedPane , ITab tab )
			{
				return tab != tab1;
			}
			
			@Override
			public boolean isSnapInAllowed( TabbedPane tabbedPane , ITab tab )
			{
				return false;
			}
		} );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
