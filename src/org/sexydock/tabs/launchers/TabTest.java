
package org.sexydock.tabs.launchers;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import org.sexydock.tabs.DefaultTab;

public class TabTest
{
	public static void main( String[ ] args )
	{
		JFrame frame = new JFrame( );
		Container content = frame.getContentPane( );
		content.setLayout( new GridLayout( 1 , 1 , 5 , 5 ) );
		
		final DefaultTab defaultTab = new DefaultTab( "Default Tab" );
		content.add( defaultTab );
		
		MouseAdapter adapter = new MouseAdapter( )
		{
			
			@Override
			public void mousePressed( MouseEvent e )
			{
				if( defaultTab.isSelectableAt( e.getPoint( ) ) )
				{
					defaultTab.setSelected( !defaultTab.isSelected( ) );
				}
			}
			
			@Override
			public void mouseMoved( MouseEvent e )
			{
				defaultTab.setRollover( defaultTab.isHoverableAt( e.getPoint( ) ) );
			}
			
			@Override
			public void mouseEntered( MouseEvent e )
			{
				defaultTab.setRollover( defaultTab.isHoverableAt( e.getPoint( ) ) );
			}
			
			@Override
			public void mouseExited( MouseEvent e )
			{
				defaultTab.setRollover( defaultTab.isHoverableAt( e.getPoint( ) ) );
			}
		};
		
		defaultTab.addMouseListener( adapter );
		defaultTab.addMouseMotionListener( adapter );
		
		frame.pack( );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
}
