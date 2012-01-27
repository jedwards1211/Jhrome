
package org.jhrome.demos;

import java.awt.Color;
import java.awt.Window;

import org.jhrome.IJhromeWindow;
import org.jhrome.JhromeTab;
import org.jhrome.JhromeTabUI;
import org.jhrome.JhromeWindowFactory;

public class ColoredTabDemo implements IJhromeDemo
{
	@Override
	public void start( )
	{
		JhromeWindowFactory windowFactory = new JhromeWindowFactory( );
		IJhromeWindow jhromeWindow = windowFactory.createWindow( );
		final Window window = jhromeWindow.getWindow( );
		
		JhromeTab tab1 = new JhromeTab( "Tab 1" );
		JhromeTabUI ui = new JhromeTabUI( );
		ui.getSelectedAttributes( ).topColor = Color.RED;
		ui.getRolloverAttributes( ).topColor = Color.BLUE;
		tab1.setUI( ui );
		jhromeWindow.getTabbedPane( ).addTab( tab1 );
		jhromeWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
