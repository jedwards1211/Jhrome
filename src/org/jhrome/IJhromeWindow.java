
package org.jhrome;

import java.awt.Window;

public interface IJhromeWindow
{
	JhromeTabbedPane getTabbedPane( );
	
	Window getWindow( );
	
	void disposeWindow( );
}
