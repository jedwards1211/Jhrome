
package org.sexydock.tabs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.dnd.DragSourceDropEvent;

import javax.swing.SwingUtilities;

public class DefaultTabDropFailureHandler implements ITabDropFailureHandler
{
	public DefaultTabDropFailureHandler( )
	{
		this( new DefaultTabbedPaneWindowFactory( ) );
	}
	
	public DefaultTabDropFailureHandler( ITabbedPaneWindowFactory windowFactory )
	{
		this.windowFactory = windowFactory;
	}
	
	final ITabbedPaneWindowFactory	windowFactory;
	
	@Override
	public void onDropFailure( DragSourceDropEvent dsde , ITab draggedTab , Dimension dragSourceWindowSize )
	{
		ITabbedPaneWindow newJhromeWindow = windowFactory.createWindow( );
		Window newWindow = newJhromeWindow.getWindow( );
		TabbedPane tabbedPane = newJhromeWindow.getTabbedPane( );
		
		tabbedPane.addTab( tabbedPane.getTabCount( ) , draggedTab , false );
		tabbedPane.setSelectedTab( draggedTab );
		tabbedPane.finishAnimation( );
		
		if( dragSourceWindowSize != null )
		{
			newWindow.setSize( dragSourceWindowSize );
		}
		else
		{
			newWindow.pack( );
		}
		
		newWindow.setLocation( dsde.getLocation( ) );
		newWindow.setVisible( true );
		
		newWindow.toFront( );
		newWindow.requestFocus( );
		
		Point loc = newWindow.getLocation( );
		Component renderer = draggedTab.getRenderer( );
		Point tabPos = new Point( renderer.getWidth( ) / 2 , renderer.getHeight( ) / 2 );
		SwingUtilities.convertPointToScreen( tabPos , renderer );
		
		loc.x += dsde.getX( ) - tabPos.x;
		loc.y += dsde.getY( ) - tabPos.y;
		newWindow.setLocation( loc );
	}
}
