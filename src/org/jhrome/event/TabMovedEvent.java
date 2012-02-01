
package org.jhrome.event;

import org.jhrome.ITab;
import org.jhrome.TabbedPane;

public class TabMovedEvent extends TabbedPaneEvent
{
	public TabMovedEvent( TabbedPane tabbedPane , long timestamp , ITab movedTab , int prevIndex , int newIndex )
	{
		super( tabbedPane , timestamp );
		this.movedTab = movedTab;
		this.prevIndex = prevIndex;
		this.newIndex = newIndex;
	}
	
	public final ITab	movedTab;
	public final int		prevIndex;
	public final int		newIndex;
}
