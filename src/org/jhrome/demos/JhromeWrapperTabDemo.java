
package org.jhrome.demos;

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

import org.jhrome.IJhromeWindow;
import org.jhrome.JhromeWindowFactory;
import org.jhrome.JhromeWrapperTab;

public class JhromeWrapperTabDemo implements IJhromeDemo
{
	@Override
	public void start( )
	{
		JhromeWindowFactory windowFactory = new JhromeWindowFactory( );
		IJhromeWindow jhromeWindow = windowFactory.createWindow( );
		final Window window = jhromeWindow.getWindow( );
		
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
		
		JhromeWrapperTab tab1 = new JhromeWrapperTab( renderer , null );
		jhromeWindow.getTabbedPane( ).addTab( tab1 );
		jhromeWindow.getTabbedPane( ).setSelectedTab( tab1 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
