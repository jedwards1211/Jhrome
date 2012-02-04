
package org.sexydock.tabs;

/**
 * Controls the drag and drop policy of a {@link TabbedPane}. This includes whether
 * tabs may be torn away from the tabbed pane or snapped in.
 * 
 * @author andy.edwards
 * 
 */
public interface ITabbedPaneDnDPolicy
{
	/**
	 * Controls whether a jhromeTab may be "torn away" from a {@code JhromeTabbedPane} (if it can be
	 * removed by being dragged out of the tabbed pane).
	 * 
	 * @param tabbedPane
	 *            the {@code JhromeTabbedPane} the user is dragging the jhromeTab out of.
	 * @param tab
	 *            the {@code IJhromeTab} the user is dragging.
	 * @return {@code true} if {@code jhromeTab} may be torn away from {@code tabbedPane}.
	 */
	boolean isTearAwayAllowed( TabbedPane tabbedPane , ITab tab );
	
	/**
	 * Controls whether a jhromeTab may be "snapped in" to a {@code JhromeTabbedPane} (if it can be
	 * added by being dragged over the tabbed pane).
	 * 
	 * @param tabbedPane
	 *            the {@code JhromeTabbedPane} the user is dragging the jhromeTab over.
	 * @param tab
	 *            the {@code IJhromeTab} the user is dragging.
	 * @return {@code true} if {@code jhromeTab} may be snapped into {@code tabbedPane}.
	 */
	boolean isSnapInAllowed( TabbedPane tabbedPane , ITab tab );
}
