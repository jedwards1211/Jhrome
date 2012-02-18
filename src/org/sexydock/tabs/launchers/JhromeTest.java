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

package org.sexydock.tabs.launchers;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.SwingUtilities;

import org.sexydock.tabs.DefaultTab;
import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.ITabbedPaneWindowFactory;
import org.sexydock.tabs.TabbedPane;

public class JhromeTest
{
	public static void main( String[ ] args )
	{
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				ITabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
				ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
				Window window = tabbedPaneWindow.getWindow( );
				TabbedPane tabbedPane = tabbedPaneWindow.getTabbedPane( );
				
				for( int i = 0 ; i < 1 ; i++ )
				{
					DefaultTab defaultTab = new DefaultTab( "Tab " + i );
					tabbedPane.addTab( i , defaultTab );
					tabbedPane.setSelectedTab( defaultTab );
				}
				
				window.setSize( new Dimension( 800 , 600 ) );
				window.setLocationRelativeTo( null );
				window.setVisible( true );
			}
		} );
	}
}
