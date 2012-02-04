
package org.sexydock.tabs.event;

import org.sexydock.tabs.ITab;
import org.sexydock.tabs.TabbedPane;

public class TabSelectedEvent extends TabbedPaneEvent
{
	public TabSelectedEvent( TabbedPane tabbedPane , long timestamp , ITab prevSelected , ITab newSelected )
	{
		super( tabbedPane , timestamp );
		this.prevSelected = prevSelected;
		this.newSelected = newSelected;
	}
	
	public final ITab	prevSelected;
	public final ITab	newSelected;
}
