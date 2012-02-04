
package org.sexydock.tabs;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

/**
 * The default implementation of {@link ITabbedPaneWindow}.
 * 
 * @author andy.edwards
 */
@SuppressWarnings( "serial" )
public class DefaultTabbedPaneWindow extends JFrame implements ITabbedPaneWindow
{
	
	public DefaultTabbedPaneWindow( ) throws HeadlessException
	{
		super( );
		init( );
	}
	
	public DefaultTabbedPaneWindow( GraphicsConfiguration gc )
	{
		super( gc );
		init( );
	}
	
	public DefaultTabbedPaneWindow( String title , GraphicsConfiguration gc )
	{
		super( title , gc );
		init( );
	}
	
	public DefaultTabbedPaneWindow( String title ) throws HeadlessException
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
	
	TabbedPane	tabbedPane;
	
	private void init( )
	{
		tabbedPane = new TabbedPane( );
		tabbedPane.setBorder( new EmptyBorder( 3 , 3 , 3 , 3 ) );
		getContentPane( ).add( tabbedPane , BorderLayout.CENTER );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeWindow#getTabbedPane()
	 */
	@Override
	public TabbedPane getTabbedPane( )
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
