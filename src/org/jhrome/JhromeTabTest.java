
package org.jhrome;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

public class JhromeTabTest
{
	public static void main( String[ ] args )
	{
		JFrame frame = new JFrame( );
		Container content = frame.getContentPane( );
		content.setLayout( new GridLayout( 1 , 1 , 5 , 5 ) );
		
		final JhromeTab tab = new JhromeTab( "Default Tab" );
		content.add( tab );
		
		MouseAdapter adapter = new MouseAdapter( )
		{
			
			@Override
			public void mousePressed( MouseEvent e )
			{
				if( tab.isSelectableAt( e.getPoint( ) ) )
				{
					tab.setSelected( !tab.isSelected( ) );
				}
			}
			
			@Override
			public void mouseMoved( MouseEvent e )
			{
				tab.setRollover( tab.isHoverableAt( e.getPoint( ) ) );
			}
			
			@Override
			public void mouseEntered( MouseEvent e )
			{
				tab.setRollover( tab.isHoverableAt( e.getPoint( ) ) );
			}
			
			@Override
			public void mouseExited( MouseEvent e )
			{
				tab.setRollover( tab.isHoverableAt( e.getPoint( ) ) );
			}
		};
		
		tab.addMouseListener( adapter );
		tab.addMouseMotionListener( adapter );
		
		frame.pack( );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
}
