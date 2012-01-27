
package org.jhrome;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

@SuppressWarnings( "serial" )
public class JhromeWindow extends JFrame implements IJhromeWindow
{
	
	public JhromeWindow( ) throws HeadlessException
	{
		super( );
		init( );
	}
	
	public JhromeWindow( GraphicsConfiguration gc )
	{
		super( gc );
		init( );
	}
	
	public JhromeWindow( String title , GraphicsConfiguration gc )
	{
		super( title , gc );
		init( );
	}
	
	public JhromeWindow( String title ) throws HeadlessException
	{
		super( title );
		init( );
	}
	
	@Override
	public void dispose( )
	{
		tabbedPane.dispose( );
		super.dispose( );
	}
	
	JhromeTabbedPane	tabbedPane;
	
	private void init( )
	{
		tabbedPane = new JhromeTabbedPane( );
		tabbedPane.setBorder( new EmptyBorder( 3 , 3 , 3 , 3 ) );
		getContentPane( ).add( tabbedPane , BorderLayout.CENTER );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeWindow#getTabbedPane()
	 */
	@Override
	public JhromeTabbedPane getTabbedPane( )
	{
		return tabbedPane;
	}
	
	@Override
	public Window getWindow( )
	{
		return this;
	}
	
	@Override
	public void disposeWindow( )
	{
		dispose( );
	}
}
