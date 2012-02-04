
package org.sexydock.tabs.event;

import org.sexydock.tabs.ITab;
import org.sexydock.tabs.TabbedPane;

public class TabRemovedEvent extends TabbedPaneEvent
{
	public TabRemovedEvent( TabbedPane tabbedPane , long timestamp , ITab removedTab , int removedIndex )
	{
		super( tabbedPane , timestamp );
		this.removedTab = removedTab;
		this.removedIndex = removedIndex;
	}
	
	public final ITab	removedTab;
	public final int		removedIndex;
}
