
package org.jhrome.demos;

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
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jhrome.IJhromeTab;
import org.jhrome.IJhromeTabDnDPolicy;
import org.jhrome.IJhromeWindow;
import org.jhrome.JhromeTab;
import org.jhrome.JhromeTabbedPane;
import org.jhrome.JhromeWindowFactory;

public class JhromeDemos implements IJhromeDemo
{
	public static void main( String[ ] args )
	{
		new JhromeDemos( ).start( );
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
				final JList demoList = new JList( );
				final JScrollPane demoListScroller = new JScrollPane( demoList );
				demoListPanel.add( demoListScroller , BorderLayout.CENTER );
				
				final DefaultListModel demoListModel = new DefaultListModel( );
				try
				{
					demoListModel.addElement( new DemoItem( JhromeDemos.this , "JhromeDemos (This Program)" , read( getClass( ).getClassLoader( ).getResource( "org/jhrome/demos/JhromeDemos.java" ) ) ) );
					demoListModel.addElement( new DemoItem( new OutOfTheBoxDemo( ) , "Out of the Box Demo" , read( getClass( ).getClassLoader( ).getResource( "org/jhrome/demos/OutOfTheBoxDemo.java" ) ) ) );
					demoListModel.addElement( new DemoItem( new LabelReplacementDemo( ) , "Label Replacement Demo" , read( getClass( ).getClassLoader( ).getResource( "org/jhrome/demos/LabelReplacementDemo.java" ) ) ) );
					demoListModel.addElement( new DemoItem( new ColoredTabDemo( ) , "Colored Tab Demo" , read( getClass( ).getClassLoader( ).getResource( "org/jhrome/demos/ColoredTabDemo.java" ) ) ) );
					demoListModel.addElement( new DemoItem( new JhromeWrapperTabDemo( ) , "JhromeWrapperTab Demo" , read( getClass( ).getClassLoader( ).getResource( "org/jhrome/demos/JhromeWrapperTabDemo.java" ) ) ) );
					demoListModel.addElement( new DemoItem( new NonUniformTabWidthDemo( ) , "Non-Uniform Tab Width Demo" , read( getClass( ).getClassLoader( ).getResource( "org/jhrome/demos/NonUniformTabWidthDemo.java" ) ) ) );
					demoListModel.addElement( new DemoItem( new NoTearAwayDemo( ) , "No Tear Away Demo" , read( getClass( ).getClassLoader( ).getResource( "org/jhrome/demos/NoTearAwayDemo.java" ) ) ) );
					demoListModel.addElement( new DemoItem( new NoSnapInDemo( ) , "No Snap In Demo" , read( getClass( ).getClassLoader( ).getResource( "org/jhrome/demos/NoSnapInDemo.java" ) ) ) );
				}
				catch( Exception e1 )
				{
					e1.printStackTrace( );
					JOptionPane.showMessageDialog( null , new Object[ ] { "Failed to load demo source code!" , e1.getLocalizedMessage( ) } , "I/O Error" , JOptionPane.ERROR_MESSAGE );
					return;
				}
				
				demoList.setModel( demoListModel );
				
				final JhromeWindowFactory windowFactory = new JhromeWindowFactory( );
				windowFactory.showNewTabButton = false;
				final IJhromeWindow window = windowFactory.createWindow( );
				
				final JhromeTab demoSelectorTab = new JhromeTab( "Jhrome Demos" , demoListPanel );
				demoSelectorTab.getLabel( ).setFont( demoSelectorTab.getLabel( ).getFont( ).deriveFont( Font.BOLD ) );
				demoSelectorTab.getCloseButton( ).setVisible( false );
				
				window.getTabbedPane( ).addTab( demoSelectorTab );
				window.getTabbedPane( ).setSelectedTab( demoSelectorTab );
				window.getTabbedPane( ).setDnDPolicy( new IJhromeTabDnDPolicy( )
				{
					@Override
					public boolean isTearAwayAllowed( JhromeTabbedPane tabbedPane , IJhromeTab tab )
					{
						return tab != demoSelectorTab;
					}
					
					@Override
					public boolean isSnapInAllowed( JhromeTabbedPane tabbedPane , IJhromeTab tab )
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
							
							JhromeTabbedPane tabbedPane = JhromeTabbedPane.getJhromeTabbedPaneAncestor( demoList );
							if( tabbedPane != null )
							{
								JhromeTab demoTab = new JhromeTab( demoItem.name , createDemoPanel( demoItem.demo , demoItem.source ) );
								tabbedPane.addTab( demoTab );
								tabbedPane.setSelectedTab( demoTab );
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
		public DemoItem( IJhromeDemo demo , String name , String source )
		{
			super( );
			this.demo = demo;
			this.name = name;
			this.source = source;
		}
		
		IJhromeDemo	demo;
		String		name;
		String		source;
		
		@Override
		public String toString( )
		{
			return name;
		}
	}
	
	public static JPanel createDemoPanel( final IJhromeDemo demo , String source )
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
		sourceArea.setTransferHandler( null );
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
