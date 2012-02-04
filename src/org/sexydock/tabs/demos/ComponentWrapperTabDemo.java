
package org.sexydock.tabs.demos;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.sexydock.tabs.ComponentWrapperTab;
import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITabbedPaneWindow;

public class ComponentWrapperTabDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		DefaultTabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
		ITabbedPaneWindow tabbedPaneWindow = windowFactory.createWindow( );
		final Window window = tabbedPaneWindow.getWindow( );
		
		JButton button = new JButton( "Click Me!" );
		button.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JOptionPane.showMessageDialog( window , "Pretty cool huh?" );
			}
		} );
		
		JTextField textField = new JTextField( "Edit Me!" );
		textField.setTransferHandler( null );
		
		JPanel renderer = new JPanel( new GridLayout( 2 , 1 , 2 , 2 ) );
		renderer.setBorder( new CompoundBorder( new LineBorder( Color.BLACK ) , new EmptyBorder( 5 , 5 , 5 , 5 ) ) );
		renderer.add( button );
		renderer.add( textField );
		
		ComponentWrapperTab tab1 = new ComponentWrapperTab( renderer , null );
		tabbedPaneWindow.getTabbedPane( ).addTab( tab1 );
		tabbedPaneWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
