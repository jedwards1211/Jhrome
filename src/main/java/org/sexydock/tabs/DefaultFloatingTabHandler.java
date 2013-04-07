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

import javax.swing.JTabbedPane;

import org.sexydock.SwingUtils;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

import com.sun.awt.AWTUtilities;

public class DefaultFloatingTabHandler implements IFloatingTabHandler
{
	private Window	dragImageWindow	= null;
	private Image	dragImage		= null;
	
	private int		xOffs;
	private int		yOffs;
	
	public void initialize( Tab draggedTab , Point grabPoint )
	{
		JTabbedPane tabbedPane = SwingUtils.getJTabbedPaneAncestor( draggedTab );
		JhromeTabbedPaneUI tabbedPaneUI = SwingUtils.getJTabbedPaneAncestorUI( draggedTab );
		
		if( tabbedPaneUI != null )
		{
			dragImage = tabbedPaneUI.createDragImage( draggedTab );
			switch( tabbedPane.getTabPlacement( ) )
			{
				case JTabbedPane.TOP:
					xOffs = -grabPoint.x;
					yOffs = 10;
					break;
				case JTabbedPane.BOTTOM:
					xOffs = -grabPoint.x;
					yOffs = -dragImage.getHeight( null ) - 10;
					break;
				case JTabbedPane.LEFT:
					xOffs = 10;
					yOffs = -grabPoint.y;
					break;
				case JTabbedPane.RIGHT:
					xOffs = -dragImage.getWidth( null ) - 10;
					yOffs = -grabPoint.y;
					break;
			}
		}
	}
	
	@SuppressWarnings( "serial" )
	@Override
	public void onFloatingBegin( Tab draggedTab , Point grabPoint )
	{
		initialize( draggedTab , grabPoint );
		
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
	public void onFloatingTabDragged( DragSourceDragEvent dsde , Tab draggedTab , double grabX )
	{
		if( dragImageWindow != null )
		{
			Point p = new Point( dsde.getX( ) + xOffs , dsde.getY( ) + yOffs );
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
