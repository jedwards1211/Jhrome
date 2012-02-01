
package org.jhrome.event;

import org.jhrome.ITab;
import org.jhrome.TabbedPane;

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
