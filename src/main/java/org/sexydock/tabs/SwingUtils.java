package org.sexydock.tabs;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

public class SwingUtils
{
	public static void doSwing( Runnable r )
	{
		if( SwingUtilities.isEventDispatchThread( ) )
		{
			r.run( );
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait( r );
			}
			catch( InvocationTargetException e )
			{
				throw new RuntimeException( e );
			}
			catch( InterruptedException e )
			{
				throw new RuntimeException( e );
			}
		}
	}
	
	public static JTabbedPane getJTabbedPaneAncestor( Component c )
	{
		while( c != null )
		{
			if( c instanceof JTabbedPane )
			{
				return ( JTabbedPane ) c;
			}
			c = c.getParent( );
		}
		return null;
	}
	
	public static JhromeTabbedPaneUI getJTabbedPaneAncestorUI( Component c )
	{
		JTabbedPane tabbedPane = getJTabbedPaneAncestor( c );
		if( tabbedPane != null && tabbedPane.getUI( ) instanceof JhromeTabbedPaneUI )
		{
			return ( JhromeTabbedPaneUI ) tabbedPane.getUI( );
		}
		return null;
	}
}
