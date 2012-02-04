
package org.sexydock.tabs;

/**
 * The interface for creating {@link ITabbedPaneWindow}s. {@link TabbedPane} uses this interface to create a new {@code IJhromeWindow} whenever a jhromeTab is torn
 * away and needs to be displayed in a new window. This way you can control what window is created.
 * 
 * @author andy.edwards
 */
public interface ITabbedPaneWindowFactory
{
	
	/**
	 * @return a new {@code IJhromeWindow}.
	 */
	public abstract ITabbedPaneWindow createWindow( );
	
}
