
package org.sexydock.tabs.demos;

import java.awt.Color;
import java.awt.Window;

import org.sexydock.tabs.DefaultTab;
import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.ITabbedPaneWindowFactory;
import org.sexydock.tabs.jhrome.JhromeTabUI;

public class ColoredTabDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		ITabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
		ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
		final Window window = tabbedPaneWindow.getWindow( );
		
		DefaultTab tab1 = new DefaultTab( "Tab 1" );
		JhromeTabUI ui = new JhromeTabUI( );
		ui.getSelectedAttributes( ).topColor = Color.RED;
		ui.getRolloverAttributes( ).topColor = Color.BLUE;
		tab1.setUI( ui );
		tabbedPaneWindow.getTabbedPane( ).addTab( tab1 );
		tabbedPaneWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
