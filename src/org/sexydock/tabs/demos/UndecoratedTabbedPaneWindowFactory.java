
package org.sexydock.tabs.demos;

import org.sexydock.tabs.DefaultTabbedPaneWindow;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.ITabbedPaneWindowFactory;

import com.sun.awt.AWTUtilities;

/**
 * The default implementation of {@link ITabbedPaneWindowFactory}.
 * 
 * @author andy.edwards
 */
public class UndecoratedTabbedPaneWindowFactory implements ITabbedPaneWindowFactory
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
		frame.setUndecorated( true );
		AWTUtilities.setWindowOpaque( frame , false );
		frame.getTabbedPane( ).setWindowFactory( this );
		frame.getTabbedPane( ).getNewTabButton( ).setVisible( showNewTabButton );
		frame.setDefaultCloseOperation( DefaultTabbedPaneWindow.DISPOSE_ON_CLOSE );
		return frame;
	}
}
