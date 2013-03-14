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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import org.sexydock.tabs.BasicTabUI;
import org.sexydock.tabs.DefaultTabbedPaneWindowFactory;
import org.sexydock.tabs.ITabbedPaneDnDPolicy;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.JhromeTabbedPaneUI;
import org.sexydock.tabs.Tab;
import org.sexydock.tabs.jhrome.JhromeTabUI;

public class SexyTabsDemos implements ISexyTabsDemo
{
	public static void main( String[ ] args )
	{
		new SexyTabsDemos( ).start( );
	}
	
	private static String getSourcePath( Class<?> clazz )
	{
		return clazz.getName( ).replace( '.' , '/' ) + ".java";
	}
	
	@Override
	public void start( )
	{
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				final JPanel demoListPanel = new JPanel( new BorderLayout( ) );
				demoListPanel.setOpaque( false );
				final JList<DemoItem> demoList = new JList<DemoItem>( );
				final JScrollPane demoListScroller = new JScrollPane( demoList );
				demoListPanel.add( demoListScroller , BorderLayout.CENTER );
				
				final DefaultListModel<DemoItem> demoListModel = new DefaultListModel<DemoItem>( );
				try
				{
					demoListModel.addElement( new DemoItem( SexyTabsDemos.this , "SexyTabsDemos (This Program)" , read( getClass( ).getClassLoader( ).getResource( getSourcePath( SexyTabsDemos.class ) ) ) ) );
					demoListModel.addElement( new DemoItem( new OutOfTheBoxDemo( ) , "Out of the Box Demo" , read( getClass( ).getClassLoader( ).getResource( getSourcePath( OutOfTheBoxDemo.class ) ) ) ) );
					// demoListModel.addElement( new DemoItem( new LabelReplacementDemo( ) , "Label Replacement Demo" , read( getClass( ).getClassLoader(
					// ).getResource( getSourcePath( LabelReplacementDemo.class ) ) ) ) );
					demoListModel.addElement( new DemoItem( new ColoredTabDemo( ) , "Colored Tab Demo" , read( getClass( ).getClassLoader( ).getResource( getSourcePath( ColoredTabDemo.class ) ) ) ) );
					// demoListModel.addElement( new DemoItem( new ComponentWrapperTabDemo( ) , "ComponentWrapperTab Demo" , read( getClass( ).getClassLoader(
					// ).getResource( getSourcePath( ComponentWrapperTabDemo.class ) ) ) ) );
					// demoListModel.addElement( new DemoItem( new NonUniformTabWidthDemo( ) , "Non-Uniform Tab Width Demo" , read( getClass( ).getClassLoader(
					// ).getResource( getSourcePath( NonUniformTabWidthDemo.class ) ) ) ) );
					// demoListModel.addElement( new DemoItem( new NoTearAwayDemo( ) , "No Tear Away Demo" , read( getClass( ).getClassLoader( ).getResource(
					// getSourcePath( NoTearAwayDemo.class ) ) ) ) );
					// demoListModel.addElement( new DemoItem( new NoSnapInDemo( ) , "No Snap In Demo" , read( getClass( ).getClassLoader( ).getResource(
					// getSourcePath( NoSnapInDemo.class ) ) ) ) );
					// demoListModel.addElement( new DemoItem( new NestedTabbedPanesDemo( ) , "Nested Tabbed Panes Demo" , read( getClass( ).getClassLoader(
					// ).getResource( getSourcePath( NestedTabbedPanesDemo.class ) ) ) ) );
					// demoListModel.addElement( new DemoItem( new UndecoratedWindowDemo( ) , "Undecorated Window Demo" , read( getClass( ).getClassLoader(
					// ).getResource( getSourcePath( UndecoratedWindowDemo.class ) ) ) ) );
					// demoListModel.addElement( new DemoItem( new ITabbedPaneListenerDemo( ) , "ITabbedPaneListener Demo" , read( getClass( ).getClassLoader(
					// ).getResource( getSourcePath( ITabbedPaneListenerDemo.class ) ) ) ) );
					// demoListModel.addElement( new DemoItem( new SlowAnimationDemo( ) , "Slow Animation Demo" , read( getClass( ).getClassLoader(
					// ).getResource( getSourcePath( SlowAnimationDemo.class ) ) ) ) );
				}
				catch( Exception e1 )
				{
					e1.printStackTrace( );
					JOptionPane.showMessageDialog( null , new Object[ ] { "Failed to load demo source code!" , e1.getLocalizedMessage( ) } , "I/O Error" , JOptionPane.ERROR_MESSAGE );
					return;
				}
				
				demoList.setModel( demoListModel );
				
				final DefaultTabbedPaneWindowFactory windowFactory = new DefaultTabbedPaneWindowFactory( );
				final ITabbedPaneWindow window = windowFactory.createWindow( );
				
				window.getTabbedPane( ).putClientProperty( "newTabButtonVisible" , null );
				
				final Tab demoSelectorTab = new Tab( "SexyTabs Demos" , demoListPanel );
				JhromeTabUI demoSelectorTabUI = (JhromeTabUI) demoSelectorTab.getUI( );
				demoSelectorTabUI.getLabel( ).setFont( demoSelectorTabUI.getLabel( ).getFont( ).deriveFont( Font.BOLD ) );
				demoSelectorTab.putClientProperty( "closeButtonVisible" , false );
				
				JhromeTabbedPaneUI tabbedPaneUI = ( JhromeTabbedPaneUI ) window.getTabbedPane( ).getUI( );
				tabbedPaneUI.addTab( demoSelectorTab );
				tabbedPaneUI.setSelectedTab( demoSelectorTab );
				tabbedPaneUI.setDnDPolicy( new ITabbedPaneDnDPolicy( )
				{
					@Override
					public boolean isTearAwayAllowed( JTabbedPane tabbedPane , Tab tab )
					{
						return tab != demoSelectorTab;
					}
					
					@Override
					public boolean isSnapInAllowed( JTabbedPane tabbedPane , Tab tab )
					{
						return true;
					}
				} );
				
				window.getWindow( ).setSize( 800 , 600 );
				window.getWindow( ).setLocationRelativeTo( null );
				window.getWindow( ).setVisible( true );
				
				demoList.addListSelectionListener( new ListSelectionListener( )
				{
					@Override
					public void valueChanged( ListSelectionEvent e )
					{
						if( !e.getValueIsAdjusting( ) && demoList.getSelectedValue( ) != null && demoList.getSelectedValue( ) instanceof DemoItem )
						{
							DemoItem demoItem = ( DemoItem ) demoList.getSelectedValue( );
							
							JTabbedPane tabbedPane = JhromeTabbedPaneUI.getJTabbedPaneAncestor( demoList );
							if( tabbedPane != null )
							{
								tabbedPane.addTab( demoItem.name , createDemoPanel( demoItem.demo , demoItem.source ) );
								tabbedPane.setSelectedIndex( tabbedPane.getTabCount( ) - 1 );
							}
							demoList.clearSelection( );
						}
					}
				} );
			}
		} );
	}
	
	static class DemoItem
	{
		public DemoItem( ISexyTabsDemo demo , String name , String source )
		{
			super( );
			this.demo = demo;
			this.name = name;
			this.source = source;
		}
		
		ISexyTabsDemo	demo;
		String			name;
		String			source;
		
		@Override
		public String toString( )
		{
			return name;
		}
	}
	
	public static JPanel createDemoPanel( final ISexyTabsDemo demo , String source )
	{
		JPanel panel = new JPanel( new BorderLayout( ) );
		panel.setOpaque( false );
		
		JButton startButton = new JButton( "Launch" );
		startButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				demo.start( );
			}
		} );
		
		panel.add( startButton , BorderLayout.NORTH );
		
		JTextArea sourceArea = new JTextArea( );
		sourceArea.setFont( new Font( "Monospaced" , Font.PLAIN , 11 ) );
		sourceArea.setEditable( false );
		// sourceArea.setTransferHandler( null );
		sourceArea.setText( source );
		JScrollPane sourceAreaScroller = new JScrollPane( sourceArea );
		panel.add( sourceAreaScroller , BorderLayout.CENTER );
		
		return panel;
	}
	
	public static String read( URL url ) throws IOException
	{
		return read( url.openStream( ) );
	}
	
	public static String read( InputStream in ) throws IOException
	{
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		
		StringBuffer buffer = new StringBuffer( );
		String line;
		while( ( line = reader.readLine( ) ) != null )
		{
			buffer.append( line ).append( '\n' );
		}
		
		return buffer.toString( );
	}
}
