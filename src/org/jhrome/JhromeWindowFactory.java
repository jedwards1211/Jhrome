
package org.jhrome;

public class JhromeWindowFactory implements IJhromeWindowFactory
{
	private static int	windowCounter		= 1;
	
	public boolean		showNewTabButton	= true;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeWindowFactory#createWindow()
	 */
	@Override
	public IJhromeWindow createWindow( )
	{
		JhromeWindow frame = new JhromeWindow( "Jhrome! " + ( windowCounter++ ) );
		frame.getTabbedPane( ).setWindowFactory( this );
		frame.getTabbedPane( ).getNewTabButton( ).setVisible( showNewTabButton );
		frame.setDefaultCloseOperation( JhromeWindow.DISPOSE_ON_CLOSE );
		return frame;
	}
}
