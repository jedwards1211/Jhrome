
package org.jhrome.demos;

import java.awt.Color;
import java.awt.Window;

import org.jhrome.JhromeTabUI;
import org.jhrome.DefaultTabbedPaneWindowFactory;
import org.jhrome.ITabbedPaneWindow;
import org.jhrome.ITabbedPaneWindowFactory;
import org.jhrome.DefaultTab;

public class ColoredTabDemo implements IJhromeDemo
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
