
package org.jhrome;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.HashSet;

public abstract class RecursiveListener
{
	private ContainerListener	containerListener	= new HierarchyChangeListener( );
	
	private HashSet<Component>	excluded			= new HashSet<Component>( );
	
	public void addExcludedComponent( Component c )
	{
		excluded.add( c );
	}
	
	public void removeExcludedComponent( Component c )
	{
		excluded.remove( c );
	}
	
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
		if( excluded.contains( c ) )
		{
			return;
		}
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
