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

import java.awt.Color;
import java.awt.Window;

import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.ITabbedPaneWindowFactory;
import org.sexydock.tabs.JhromeTabbedPaneUI;
import org.sexydock.tabs.Tab;
import org.sexydock.tabs.jhrome.JhromeTabUI;

public class ColoredTabDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		ITabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
		ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
		final Window window = tabbedPaneWindow.getWindow( );
		
		Tab tab1 = new Tab( "Tab 1" );
		JhromeTabUI ui = new JhromeTabUI( );
		ui.getSelectedAttributes( ).topColor = Color.RED;
		ui.getRolloverAttributes( ).topColor = Color.BLUE;
		tab1.setUI( ui );
		JhromeTabbedPaneUI tabbedPaneUI = ( JhromeTabbedPaneUI ) tabbedPaneWindow.getTabbedPane( ).getUI( );
		tabbedPaneUI.addTab( 0 , tab1 , false );
		tabbedPaneWindow.getTabbedPane( ).setSelectedIndex( 0 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
