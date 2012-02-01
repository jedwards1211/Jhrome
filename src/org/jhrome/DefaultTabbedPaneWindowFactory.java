
package org.jhrome;

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
