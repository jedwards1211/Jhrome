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

package org.sexydock.tabs.demos;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.sexydock.tabs.DefaultTabbedPaneWindow;
import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.Tab;
import org.sexydock.tabs.TestTabFactory;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

public class LabelReplacementDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		DefaultTabbedPaneWindow window = new DefaultTabbedPaneWindow( getClass( ).getSimpleName( ) );
		
		TestTabFactory tabFactory = new TestTabFactory( );
		window.getTabbedPane( ).putClientProperty( JhromeTabbedPaneUI.TAB_FACTORY , tabFactory );
		
		Tab tab1 = tabFactory.createTabWithContent( );
		window.getTabbedPane( ).addTab( tab1.getTitle( ) , tab1.getContent( ) );
		
		JButton button = new JButton( "Click Me!" );
		button.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( ( Component ) e.getSource( ) ) , "Pretty cool huh?" );
			}
		} );
		window.getTabbedPane( ).setTabComponentAt( 0 , button );
		
		window.getWindow( ).setSize( 800 , 600 );
		window.getWindow( ).setLocationRelativeTo( null );
		window.getWindow( ).setVisible( true );
	}
}
