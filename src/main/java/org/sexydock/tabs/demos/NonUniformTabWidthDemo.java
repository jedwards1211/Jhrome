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

package org.sexydock.tabs.demos;

import java.awt.Window;

import javax.swing.JPanel;

import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

public class NonUniformTabWidthDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		DefaultTabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( )
		{
			@Override
			public ITabbedPaneWindow createWindow( )
			{
				ITabbedPaneWindow window = super.createWindow( );
				JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) window.getTabbedPane( ).getUI( );
				ui.setUseUniformWidth( false );
				return window;
			}
		};
		ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
		Window window = tabbedPaneWindow.getWindow( );
		
		tabbedPaneWindow.getTabbedPane( ).addTab( "Short" , new JPanel( ) );
		tabbedPaneWindow.getTabbedPane( ).addTab( "Loooooooooooooooooooooooooooooooooooooong" , new JPanel( ) );
		tabbedPaneWindow.getTabbedPane( ).setSelectedIndex( 0 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
