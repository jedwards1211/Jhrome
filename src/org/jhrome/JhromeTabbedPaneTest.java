
package org.jhrome;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

public class JhromeTabbedPaneTest
{
	public static void main( String[ ] args )
	{
		JhromeTabbedPane tabbedPane = new JhromeTabbedPane( );
		tabbedPane.setBorder( new EmptyBorder( 10 , 10 , 10 , 10 ) );
		
		for( int i = 0 ; i < 8 ; i++ )
		{
			JhromeTab tab = new JhromeTab( "Tab " + i );
			tabbedPane.addTab( i , tab );
		}
		
		JFrame frame = new JFrame( );
		
		frame.getContentPane( ).add( tabbedPane , BorderLayout.CENTER );
		frame.setSize( new Dimension( 1000 , 100 ) );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
}
