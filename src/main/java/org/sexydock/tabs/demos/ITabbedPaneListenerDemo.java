/*
 Copyright 2012 James Edwards

 This file is part of Jhrome.

 Jhrome is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Jhrome is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with Jhrome.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sexydock.tabs.demos;

import java.awt.Window;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.sexydock.tabs.DefaultTabbedPaneWindow;
import org.sexydock.tabs.ITabbedPaneDndPolicy;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.Tab;
import org.sexydock.tabs.event.ITabbedPaneListener;
import org.sexydock.tabs.event.TabAddedEvent;
import org.sexydock.tabs.event.TabMovedEvent;
import org.sexydock.tabs.event.TabRemovedEvent;
import org.sexydock.tabs.event.TabSelectedEvent;
import org.sexydock.tabs.event.TabbedPaneEvent;
import org.sexydock.tabs.event.TabsClearedEvent;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

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
		JhromeTabbedPaneUI ui = (JhromeTabbedPaneUI) tabbedPaneWindow.getTabbedPane( ).getUI( );
		ui.addTabbedPaneListener( listener );
		tabbedPaneWindow.getTabbedPane( ).addTab( "Listener", messageScroller );
		tabbedPaneWindow.getTabbedPane( ).setSelectedComponent( messageScroller );
		final Tab listenerTab = ui.getTabAt( 0 );
		listenerTab.putClientProperty( "closeButtonVisible", false );
		ui.setDndPolicy( new ITabbedPaneDndPolicy( )
		{
			@Override
			public boolean isTearAwayAllowed( JTabbedPane tabbedPane , Tab tab )
			{
				return tab != listenerTab;
			}
			
			@Override
			public boolean isSnapInAllowed( JTabbedPane tabbedPane , Tab tab )
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
	
	private static String getTabTitle( Tab tab )
	{
		if( tab == null )
		{
			return "none";
		}
		return tab.getTitle( );
	}
}
