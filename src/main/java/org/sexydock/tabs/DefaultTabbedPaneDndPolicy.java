package org.sexydock.tabs;

import javax.swing.JTabbedPane;

public class DefaultTabbedPaneDndPolicy implements ITabbedPaneDndPolicy
{
	public DefaultTabbedPaneDndPolicy( boolean isTearAwayAllowed , boolean isSnapInAllowed )
	{
		super( );
		this.isTearAwayAllowed = isTearAwayAllowed;
		this.isSnapInAllowed = isSnapInAllowed;
	}

	private boolean isTearAwayAllowed;
	private boolean isSnapInAllowed;
	
	@Override
	public boolean isTearAwayAllowed( JTabbedPane tabbedPane , Tab tab )
	{
		return isTearAwayAllowed;
	}
	
	@Override
	public boolean isSnapInAllowed( JTabbedPane tabbedPane , Tab tab )
	{
		return isSnapInAllowed;
	}

	public void setTearAwayAllowed( boolean isTearAwayAllowed )
	{
		this.isTearAwayAllowed = isTearAwayAllowed;
	}

	public void setSnapInAllowed( boolean isSnapInAllowed )
	{
		this.isSnapInAllowed = isSnapInAllowed;
	}
}
