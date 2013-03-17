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

import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

/**
 * The default implementation of {@link ITabbedPaneWindowFactory}.
 * 
 * @author andy.edwards
 */
public class DefaultTabbedPaneWindowFactory implements ITabbedPaneWindowFactory
{
	private static int	windowCounter		= 1;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeWindowFactory#createWindow()
	 */
	@Override
	public ITabbedPaneWindow createWindow( )
	{
		DefaultTabbedPaneWindow frame = new DefaultTabbedPaneWindow( "Jhrome! " + ( windowCounter++ ) );
		if( frame.getTabbedPane( ).getUI( ) instanceof JhromeTabbedPaneUI )
		{
			JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) frame.getTabbedPane( ).getUI( );
			ui.setWindowFactory( this );
		}
		frame.setDefaultCloseOperation( DefaultTabbedPaneWindow.DISPOSE_ON_CLOSE );
		return frame;
	}
}
