
package org.sexydock.tabs.event;

import org.sexydock.tabs.ITab;
import org.sexydock.tabs.TabbedPane;

public class TabSelectedEvent extends TabbedPaneEvent
{
	public TabSelectedEvent( TabbedPane tabbedPane , long timestamp , ITab prevSelected , int prevSelectedIndex , ITab newSelected , int newSelectedIndex )
	{
		super( tabbedPane , timestamp );
		this.prevSelected = prevSelected;
		this.prevSelectedIndex = prevSelectedIndex;
		this.newSelected = newSelected;
		this.newSelectedIndex = newSelectedIndex;
	}
	
	public final ITab	prevSelected;
	public final int	prevSelectedIndex;
	public final ITab	newSelected;
	public final int	newSelectedIndex;
	
	public ITab getPrevSelected( )
	{
		return prevSelected;
	}
	
	public ITab getNewSelected( )
	{
		return newSelected;
	}
	
	public int getPrevSelectedIndex( )
	{
		return prevSelectedIndex;
	}
	
	public int getNewSelectedIndex( )
	{
		return newSelectedIndex;
	}
	
	@Override
	public String toString( )
	{
		return String.format( "%s[tabbedPane: %s, timestamp: %d, prevSelected: %s, prevSelectedIndex: %d, newIndex: %d]" , getClass( ).getName( ) , tabbedPane , timestamp , prevSelected , prevSelectedIndex , newSelected , newSelectedIndex );
	}
}
