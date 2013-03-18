package org.sexydock.tabs.test;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class DoSwing
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
			catch( InvocationTargetException | InterruptedException e )
			{
				throw new RuntimeException( e );
			}
		}
	}
}
