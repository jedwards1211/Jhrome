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

import java.awt.Component;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.ListCellRenderer;

/**
 * The interface for a jhromeTab in a {@link TabbedPane}. {@code JhromeTabbedPane} uses this interface to interact with the jhromeTab and its content. This
 * allows you to use any {@link Component} for the jhromeTab, and make it behave as though it has a non-rectangular shape.
 */
public interface ITab
{
	
	/**
	 * @return the {@link Component} that represents the jhromeTab itself. This will not merely paint the jhromeTab, like a single {@link ListCellRenderer} is
	 *         used to paint many cells, but will actually be added to a {@link TabbedPane}'s Component hierarchy.
	 */
	public abstract Component getRenderer( );
	
	/**
	 * @return the {@link Component} that represents the jhromeTab's content. It will be added to a {@link TabbedPane}'s content panel whenever this jhromeTab
	 *         is selected. May be {@code null}.
	 */
	public abstract Component getContent( );
	
	/**
	 * Sets this jhromeTab's content.
	 * 
	 * @see #getContent()
	 */
	public abstract void setContent( Component content );
	
	/**
	 * @return this jhromeTab's close button. {@link TabbedPane} will automatically add a listener to this button if it is not {@code null}.
	 */
	public abstract JButton getCloseButton( );
	
	/**
	 * Specifies where the user can click and drag this jhromeTab. This way the jhromeTab component can behave as though it has a non-rectangular shape.
	 * 
	 * @param p
	 *            the point at which the user pressed the mouse, in the renderer's coordinate system.
	 * @return {@code true} if the user can drag the jhromeTab after dragging from {@code p}. NOTE: must return {@code false} for points over the close button
	 *         and other operable components, or the user will be able to drag the jhromeTab from these points!
	 */
	public abstract boolean isDraggableAt( Point p );
	
	/**
	 * Specifies where the user can click and select this jhromeTab. This way the jhromeTab component can behave as though it has a non-rectangular shape.
	 * 
	 * @param p
	 *            the point at which the user pressed the mouse, in the renderer's coordinate system.
	 * @return {@code true} if the user can select the jhromeTab by pressing the mouse at {@code p}. NOTE: must return {@code false} for points over the close
	 *         button and other operable components, or the user will be able to select the jhromeTab by clicking these components!
	 */
	public abstract boolean isSelectableAt( Point p );
	
	/**
	 * Specifies where the jhromeTab is "hoverable." If the user moves the mouse over the hoverable areas of this jhromeTab, {@link TabbedPane} will set it to
	 * the rollover state.
	 * 
	 * @param p
	 *            the point the user moved the mouse over, in the renderer's coordinate system.
	 * @return {@code true} if the jhromeTab should be set to the rollover state when the user moves the mouse over {@code p}.
	 */
	public abstract boolean isHoverableAt( Point p );
	
	/**
	 * @return whether the jhromeTab is selected.
	 */
	public abstract boolean isSelected( );
	
	/**
	 * Sets the jhromeTab selection state. The jhromeTab renderer should change its appearance accordingly.
	 */
	public abstract void setSelected( boolean selected );
	
	/**
	 * @return whether the jhromeTab is rolled over.
	 */
	public abstract boolean isRollover( );
	
	/**
	 * Sets the jhromeTab rollover state. The jhromeTab renderer should change its appearance accordingly.
	 */
	public abstract void setRollover( boolean rollover );
	
}
