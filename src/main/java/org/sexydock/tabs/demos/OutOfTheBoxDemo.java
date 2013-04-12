package org.sexydock.tabs.demos;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

public class OutOfTheBoxDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		JFrame frame = new JFrame( getClass( ).getSimpleName( ) );
		
		JTabbedPane tabbedPane = new JTabbedPane( );
		tabbedPane.setUI( new JhromeTabbedPaneUI( ) );
		
		Font bigFont = new Font("Arial", Font.BOLD, 72);
		JLabel label1 = new JLabel("Tab 1");
		JLabel label2 = new JLabel("Tab 2");
		label1.setFont( bigFont );
		label2.setFont( bigFont );
		JPanel panel1 = new JPanel(new FlowLayout());
		JPanel panel2 = new JPanel(new FlowLayout());
		panel1.add(label1);
		panel2.add(label2);
		tabbedPane.addTab( "Tab 1" , panel1 );
		tabbedPane.addTab( "Tab 2" , panel2 );
		tabbedPane.addTab( "Tab 3" , new JPanel( ) );
		tabbedPane.setEnabledAt( 2 , false );
		tabbedPane.setSelectedIndex( 0 );
		
		frame.getContentPane( ).add( tabbedPane , BorderLayout.CENTER );
		frame.setSize( 800, 600 );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
}
