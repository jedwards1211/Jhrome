
package org.jhrome.event;

import org.jhrome.ITab;
import org.jhrome.TabbedPane;

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
