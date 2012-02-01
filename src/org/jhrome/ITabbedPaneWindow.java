
package org.jhrome;

import java.awt.Window;

/**
 * The interface for a window containing a {@link TabbedPane}. {@code JhromeTabbedPane} uses this interface to move a jhromeTab that has been torn away into a
 * new window. This allows you to control the layout and attributes of the window besides its tabbed pane.
 * 
 * @author andy.edwards
 * 
 * @see ITabbedPaneWindowFactory
 */
public interface ITabbedPaneWindow
{
	/**
	 * @return the {@code JhromeTabbedPane} in the window.
	 */
	TabbedPane getTabbedPane( );
	
	/**
	 * @return the {@code Window} containing the tabbed pane.
	 */
	Window getWindow( );
	
	/**
	 * Disposes the window, tabbed pane, and any other associated resources.
	 */
	void disposeWindow( );
}
