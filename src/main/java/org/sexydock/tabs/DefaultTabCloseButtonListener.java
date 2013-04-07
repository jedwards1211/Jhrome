package org.sexydock.tabs;

import javax.swing.JTabbedPane;

public class DefaultTabCloseButtonListener implements ITabCloseButtonListener
{
	
	@Override
	public void tabCloseButtonPressed( JTabbedPane tabbedPane , int tabIndex )
	{
		tabbedPane.removeTabAt( tabIndex );
	}
}
