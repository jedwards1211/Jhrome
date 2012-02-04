
package org.sexydock.tabs.demos;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.sexydock.tabs.DefaultTab;
import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITabbedPaneWindow;

public class LabelReplacementDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		DefaultTabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
		ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
		final Window window = tabbedPaneWindow.getWindow( );
		
		DefaultTab tab1 = new DefaultTab( "Tab 1" );
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
		
		tabbedPaneWindow.getTabbedPane( ).addTab( tab1 );
		tabbedPaneWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
