
package org.jhrome.demos;

import java.awt.Window;

import org.jhrome.IJhromeTab;
import org.jhrome.IJhromeWindow;
import org.jhrome.JhromeWindowFactory;

public class NonUniformTabWidthDemo implements IJhromeDemo
{
	@Override
	public void start( )
	{
		JhromeWindowFactory windowFactory = new JhromeWindowFactory( )
		{
			@Override
			public IJhromeWindow createWindow( )
			{
				IJhromeWindow window = super.createWindow( );
				window.getTabbedPane( ).setUseUniformWidth( false );
				return window;
			}
		};
		IJhromeWindow jhromeWindow = windowFactory.createWindow( );
		Window window = jhromeWindow.getWindow( );
		
		IJhromeTab tab1 = jhromeWindow.getTabbedPane( ).getTabFactory( ).createTab( "Tab 1" );
		jhromeWindow.getTabbedPane( ).addTab( tab1 );
		jhromeWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
