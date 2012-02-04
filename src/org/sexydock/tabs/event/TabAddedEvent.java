
package org.sexydock.tabs.event;

import org.sexydock.tabs.ITab;
import org.sexydock.tabs.TabbedPane;

public class TabAddedEvent extends TabbedPaneEvent
{
	public TabAddedEvent( TabbedPane tabbedPane , long timestamp , ITab addedTab , int insertIndex )
	{
		super( tabbedPane , timestamp );
		this.addedTab = addedTab;
		this.insertIndex = insertIndex;
	}
	
	public final ITab	addedTab;
	public final int		insertIndex;
}
