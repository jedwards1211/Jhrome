
package org.jhrome.event;

import java.util.List;

import org.jhrome.ITab;
import org.jhrome.TabbedPane;

public class TabsClearedEvent extends TabbedPaneEvent
{
	public TabsClearedEvent( TabbedPane tabbedPane , long timestamp , List<ITab> removedTabs )
	{
		super( tabbedPane , timestamp );
		this.removedTabs = removedTabs;
	}
	
	public final List<ITab>	removedTabs;
}
