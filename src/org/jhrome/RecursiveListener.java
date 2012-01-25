
package org.jhrome;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

public abstract class RecursiveListener
{
	private ContainerListener	containerListener	= new HierarchyChangeListener( );
	
	private class HierarchyChangeListener implements ContainerListener
	{
		@Override
		public void componentAdded( ContainerEvent e )
		{
			installRecursively( e.getChild( ) );
		}
		
		@Override
		public void componentRemoved( ContainerEvent e )
		{
			uninstallRecursively( e.getChild( ) );
		}
	}
	
	public void installRecursively( Component c )
	{
		install( c );
		if( c instanceof Container )
		{
			Container cont = ( Container ) c;
			cont.addContainerListener( containerListener );
			installRecursively( cont );
		}
	}
	
	public void uninstallRecursively( Component c )
	{
		uninstall( c );
		if( c instanceof Container )
		{
			Container cont = ( Container ) c;
			cont.removeContainerListener( containerListener );
			uninstallRecursively( cont );
		}
	}
	
	protected abstract void install( Component c );
	
	protected abstract void uninstall( Component c );
}
