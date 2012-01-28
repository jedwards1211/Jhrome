
package org.jhrome.demos;

import java.awt.Window;

import javax.swing.border.EmptyBorder;

import org.jhrome.IJhromeTab;
import org.jhrome.IJhromeTabDnDPolicy;
import org.jhrome.IJhromeWindow;
import org.jhrome.JhromeTabbedPane;
import org.jhrome.JhromeWindowFactory;

public class NestedTabbedPanesDemo implements IJhromeDemo
{
	@Override
	public void start( )
	{
		JhromeWindowFactory windowFactory = new JhromeWindowFactory( );
		IJhromeWindow jhromeWindow = windowFactory.createWindow( );
		Window window = jhromeWindow.getWindow( );
		
		JhromeTabbedPane innerTabbedPane = new JhromeTabbedPane( );
		innerTabbedPane.setBorder( new EmptyBorder( 30 , 5 , 5 , 5 ) );
		final IJhromeTab innerTab1 = jhromeWindow.getTabbedPane( ).getTabFactory( ).createTab( "Inner Tabbed Pane" );
		innerTabbedPane.addTab( innerTab1 );
		innerTabbedPane.setSelectedTab( innerTab1 );
		
		innerTabbedPane.setDnDPolicy( new IJhromeTabDnDPolicy( )
		{
			@Override
			public boolean isTearAwayAllowed( JhromeTabbedPane tabbedPane , IJhromeTab tab )
			{
				return tab != innerTab1;
			}
			
			@Override
			public boolean isSnapInAllowed( JhromeTabbedPane tabbedPane , IJhromeTab tab )
			{
				return true;
			}
		} );
		
		final IJhromeTab outerTab1 = jhromeWindow.getTabbedPane( ).getTabFactory( ).createTab( "Outer Tabbed Pane" );
		outerTab1.setContent( innerTabbedPane );
		jhromeWindow.getTabbedPane( ).addTab( outerTab1 );
		jhromeWindow.getTabbedPane( ).setSelectedTab( outerTab1 );
		
		jhromeWindow.getTabbedPane( ).setDnDPolicy( new IJhromeTabDnDPolicy( )
		{
			@Override
			public boolean isTearAwayAllowed( JhromeTabbedPane tabbedPane , IJhromeTab tab )
			{
				return tab != outerTab1;
			}
			
			@Override
			public boolean isSnapInAllowed( JhromeTabbedPane tabbedPane , IJhromeTab tab )
			{
				return true;
			}
		} );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
