
package org.sexydock.tabs.event;

import org.sexydock.tabs.TabbedPane;

public class TabbedPaneEvent
{
	public TabbedPaneEvent( TabbedPane tabbedPane , long timestamp )
	{
		this.timestamp = timestamp;
		this.tabbedPane = tabbedPane;
	}
	
	public final TabbedPane	tabbedPane;
	public final long				timestamp;
}
