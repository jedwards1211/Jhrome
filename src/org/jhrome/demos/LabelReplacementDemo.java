
package org.jhrome.demos;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.jhrome.IJhromeWindow;
import org.jhrome.JhromeTab;
import org.jhrome.JhromeWindowFactory;

public class LabelReplacementDemo implements IJhromeDemo
{
	@Override
	public void start( )
	{
		JhromeWindowFactory windowFactory = new JhromeWindowFactory( );
		IJhromeWindow jhromeWindow = windowFactory.createWindow( );
		final Window window = jhromeWindow.getWindow( );
		
		JhromeTab tab1 = new JhromeTab( "Tab 1" );
		JButton button = new JButton( "Click Me!" );
		tab1.setOverrideLabel( button );
		button.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JOptionPane.showMessageDialog( window , "Pretty cool huh?" );
			}
		} );
		
		jhromeWindow.getTabbedPane( ).addTab( tab1 );
		jhromeWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
