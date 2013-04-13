package org.sexydock.tabs;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DefaultWindowsClosedHandler extends WindowAdapter
{
	@Override
	public void windowClosed( WindowEvent e )
	{
		for( Window window : Window.getWindows( ) )
		{
			if( window.isDisplayable( ) )
			{
				return;
			}
		}
		
		for( Frame frame : Frame.getFrames( ) )
		{
			if( frame.isDisplayable( ) )
			{
				return;
			}
		}
		
		System.exit(0);
	}
}
