
package org.sexydock.tabs.demos;

import java.awt.Window;

import javax.swing.border.EmptyBorder;

import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITab;
import org.sexydock.tabs.ITabbedPaneDnDPolicy;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.TabbedPane;

public class NestedTabbedPanesDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		DefaultTabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
		ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
		Window window = tabbedPaneWindow.getWindow( );
		
		TabbedPane innerTabbedPane = new TabbedPane( );
		innerTabbedPane.setBorder( new EmptyBorder( 30 , 5 , 5 , 5 ) );
		final ITab innerTab1 = tabbedPaneWindow.getTabbedPane( ).getTabFactory( ).createTab( "Inner Tabbed Pane" );
		innerTabbedPane.addTab( innerTab1 );
		innerTabbedPane.setSelectedTab( innerTab1 );
		
		innerTabbedPane.setDnDPolicy( new ITabbedPaneDnDPolicy( )
		{
			@Override
			public boolean isTearAwayAllowed( TabbedPane tabbedPane , ITab tab )
			{
				return tab != innerTab1;
			}
			
			@Override
			public boolean isSnapInAllowed( TabbedPane tabbedPane , ITab tab )
			{
				return true;
			}
		} );
		
		final ITab outerTab1 = tabbedPaneWindow.getTabbedPane( ).getTabFactory( ).createTab( "Outer Tabbed Pane" );
		outerTab1.setContent( innerTabbedPane );
		tabbedPaneWindow.getTabbedPane( ).addTab( outerTab1 );
		tabbedPaneWindow.getTabbedPane( ).setSelectedTab( outerTab1 );
		
		tabbedPaneWindow.getTabbedPane( ).setDnDPolicy( new ITabbedPaneDnDPolicy( )
		{
			@Override
			public boolean isTearAwayAllowed( TabbedPane tabbedPane , ITab tab )
			{
				return tab != outerTab1;
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
