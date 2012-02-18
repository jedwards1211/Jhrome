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

/**
 * The default implementation of {@link ITabbedPaneWindowFactory}.
 * 
 * @author andy.edwards
 */
public class DefaultTabbedPaneWindowFactory implements ITabbedPaneWindowFactory
{
	private static int	windowCounter		= 1;
	
	public boolean		showNewTabButton	= true;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeWindowFactory#createWindow()
	 */
	@Override
	public ITabbedPaneWindow createWindow( )
	{
		DefaultTabbedPaneWindow frame = new DefaultTabbedPaneWindow( "Jhrome! " + ( windowCounter++ ) );
		frame.getTabbedPane( ).setWindowFactory( this );
		frame.getTabbedPane( ).getNewTabButton( ).setVisible( showNewTabButton );
		frame.setDefaultCloseOperation( DefaultTabbedPaneWindow.DISPOSE_ON_CLOSE );
		return frame;
	}
}
