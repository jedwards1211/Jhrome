
package org.sexydock.tabs.event;

import java.util.List;

import org.sexydock.tabs.ITab;
import org.sexydock.tabs.TabbedPane;

public class TabsClearedEvent extends TabbedPaneEvent
{
	public TabsClearedEvent( TabbedPane tabbedPane , long timestamp , List<ITab> removedTabs )
	{
		super( tabbedPane , timestamp );
		this.removedTabs = removedTabs;
	}
	
	public final List<ITab>	removedTabs;
	
	public List<ITab> getRemovedTabs( )
	{
		return removedTabs;
	}
	
	@Override
	public String toString( )
	{
		return String.format( "%s[tabbedPane: %s, timestamp: %d]" , getClass( ).getName( ) , tabbedPane , timestamp );
	}
}
