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

import java.awt.Point;

import javax.swing.plaf.ComponentUI;

public abstract class DefaultTabUI extends ComponentUI
{
	public abstract boolean isDraggableAt( DefaultTab defaultTab , Point p );
	
	public abstract boolean isSelectableAt( DefaultTab defaultTab , Point p );
	
	public abstract boolean isHoverableAt( DefaultTab defaultTab , Point p );
}
