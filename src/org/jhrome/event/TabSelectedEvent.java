
package org.jhrome.event;

import org.jhrome.ITab;
import org.jhrome.TabbedPane;

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
