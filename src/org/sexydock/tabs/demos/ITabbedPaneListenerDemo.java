
package org.sexydock.tabs.demos;

import java.awt.Window;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.sexydock.tabs.DefaultTab;
import org.sexydock.tabs.DefaultTabbedPaneWindow;
import org.sexydock.tabs.ITab;
import org.sexydock.tabs.ITabbedPaneDnDPolicy;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.TabbedPane;
import org.sexydock.tabs.event.ITabbedPaneListener;
import org.sexydock.tabs.event.TabAddedEvent;
import org.sexydock.tabs.event.TabMovedEvent;
import org.sexydock.tabs.event.TabRemovedEvent;
import org.sexydock.tabs.event.TabSelectedEvent;
import org.sexydock.tabs.event.TabbedPaneEvent;
import org.sexydock.tabs.event.TabsClearedEvent;

public class ITabbedPaneListenerDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		ITabbedPaneWindow tabbedPaneWindow = new DefaultTabbedPaneWindow( "ITabbedPaneListener Demo" );
		Window window = tabbedPaneWindow.getWindow( );
		
		final JTextArea messageArea = new JTextArea( );
		final JScrollPane messageScroller = new JScrollPane( messageArea );
		
		ITabbedPaneListener listener = new ITabbedPaneListener( )
		{
			StringBuffer	text	= new StringBuffer( );
			
			@Override
			public void onEvent( TabbedPaneEvent event )
			{
				text.append( formatEvent( event ) ).append( '\n' );
				messageArea.setText( text.toString( ) );
				messageScroller.getVerticalScrollBar( ).setValue( messageScroller.getVerticalScrollBar( ).getMaximum( ) );
			}
		};
		tabbedPaneWindow.getTabbedPane( ).addTabbedPaneListener( listener );
		final DefaultTab listenerTab = new DefaultTab( "Listener" , messageScroller );
		listenerTab.getCloseButton( ).setVisible( false );
		tabbedPaneWindow.getTabbedPane( ).addTab( listenerTab );
		tabbedPaneWindow.getTabbedPane( ).setSelectedTab( listenerTab );
		tabbedPaneWindow.getTabbedPane( ).setDnDPolicy( new ITabbedPaneDnDPolicy( )
		{
			@Override
			public boolean isTearAwayAllowed( TabbedPane tabbedPane , ITab tab )
			{
				return tab != listenerTab;
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
	
	private static String formatEvent( TabbedPaneEvent event )
	{
		if( event instanceof TabAddedEvent )
		{
			TabAddedEvent e = ( TabAddedEvent ) event;
			return "Added tab '" + getTabTitle( e.getAddedTab( ) ) + "' at index " + e.getInsertIndex( );
		}
		else if( event instanceof TabRemovedEvent )
		{
			TabRemovedEvent e = ( TabRemovedEvent ) event;
			return "Removed tab '" + getTabTitle( e.getRemovedTab( ) ) + "' at index " + e.getRemovedIndex( );
		}
		else if( event instanceof TabMovedEvent )
		{
			TabMovedEvent e = ( TabMovedEvent ) event;
			return "Moved tab '" + getTabTitle( e.getMovedTab( ) ) + "' from index " + e.getPrevIndex( ) + " to index " + e.getNewIndex( );
		}
		else if( event instanceof TabSelectedEvent )
		{
			TabSelectedEvent e = ( TabSelectedEvent ) event;
			return "Selected tab '" + getTabTitle( e.getNewSelected( ) ) + "' at index " + e.getNewSelectedIndex( );
		}
		else if( event instanceof TabsClearedEvent )
		{
			return "Cleared all tabs";
		}
		return "";
	}
	
	private static String getTabTitle( ITab tab )
	{
		if( tab == null )
		{
			return "none";
		}
		return ( ( DefaultTab ) tab ).getLabel( ).getText( );
	}
}
