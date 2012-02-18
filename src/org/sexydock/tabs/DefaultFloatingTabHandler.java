/*
Copyright 2012 James Edwards

This file is part of Jhrome.

Jhrome is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Jhrome is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Jhrome.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sexydock.tabs;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.dnd.DragSourceDragEvent;

import com.sun.awt.AWTUtilities;

public class DefaultFloatingTabHandler implements IFloatingTabHandler
{
	private Window	dragImageWindow	= null;
	private Image	dragImage		= null;
	
	public void initialize( ITab draggedTab )
	{
		TabbedPane tabbedPane = TabbedPane.getTabbedPaneAncestor( draggedTab.getRenderer( ) );
		
		if( tabbedPane != null )
		{
			dragImage = tabbedPane.createDragImage( draggedTab );
		}
	}
	
	@SuppressWarnings( "serial" )
	@Override
	public void onFloatingBegin( ITab draggedTab )
	{
		initialize( draggedTab );
		
		if( dragImage != null )
		{
			if( dragImageWindow == null )
			{
				dragImageWindow = new Window( null )
				{
					@Override
					public void paint( Graphics g )
					{
						Graphics2D g2 = ( Graphics2D ) g;
						
						if( dragImage != null )
						{
							g2.drawImage( dragImage , 0 , 0 , null );
						}
					}
				};
				
				AWTUtilities.setWindowOpaque( dragImageWindow , false );
			}
			
			dragImageWindow.setSize( dragImage.getWidth( null ) , dragImage.getHeight( null ) );
			dragImageWindow.setAlwaysOnTop( true );
		}
	}
	
	@Override
	public void onFloatingTabDragged( DragSourceDragEvent dsde , ITab draggedTab , double grabX )
	{
		if( dragImageWindow != null )
		{
			Point p = new Point( dsde.getX( ) - ( int ) ( grabX * draggedTab.getRenderer( ).getWidth( ) ) , dsde.getY( ) + 10 );
			dragImageWindow.setLocation( p );
			dragImageWindow.setVisible( true );
		}
	}
	
	@Override
	public void onFloatingEnd( )
	{
		if( dragImageWindow != null )
		{
			dragImageWindow.dispose( );
			dragImageWindow = null;
		}
		dragImage = null;
	}
}
