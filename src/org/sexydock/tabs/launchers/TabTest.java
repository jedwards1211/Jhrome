/*
Copyright 2012 James Edwards

This file is part of Jhrome.

Jhrome is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Jhrome is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Jhrome.  If not, see <http://www.gnu.org/licenses/>.
 */

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
