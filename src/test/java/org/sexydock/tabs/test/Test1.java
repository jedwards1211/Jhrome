package org.sexydock.tabs.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import junit.framework.Assert;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

public class Test1
{
	private JFrame				window;
	private JTabbedPane			tabbedPane;
	private JhromeTabbedPaneUI	tabbedPaneUI;
	private FrameFixture		framefix;
	private SDTabbedPaneFixture	tpfix;
	
	@Before
	public void setUp( )
	{
		doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				UIManager.getDefaults( ).put( "TabbedPaneUI" , JhromeTabbedPaneUI.class.getName( ) );
				
				window = new JFrame( );
				tabbedPane = new JTabbedPane( );
				tabbedPane.setBorder( new EmptyBorder( 5 , 5 , 5 , 5 ) );
				tabbedPaneUI = ( JhromeTabbedPaneUI ) tabbedPane.getUI( );
				System.out.println( UIManager.getUI( tabbedPane ) );
				window.getContentPane( ).add( tabbedPane , BorderLayout.CENTER );
				
				JPanel content = new JPanel( );
				content.setName( "Content" );
				
				tabbedPane.addTab( "Tab 1" , content );
				tabbedPane.putClientProperty( "newTabButtonVisible" , true );
				tabbedPane.putClientProperty( "tabCloseButtonsVisible" , true );
				
				window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
				window.setSize( new Dimension( 800 , 600 ) );
				window.setLocationRelativeTo( null );
				window.setVisible( true );
				
				framefix = new FrameFixture( window );
				framefix.robot.settings( ).idleTimeout( 1000 );
				tpfix = new SDTabbedPaneFixture( framefix.robot , tabbedPane );
			}
		} );
	}
	
	@Test
	public void testSetUp( )
	{
		doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				framefix.label( ).requireText( "Tab 1" );
			}
		} );
	}
	
	@Test
	public void testAddTabInCode( )
	{
		doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				tabbedPane.addTab( "Tab 2" , new JPanel( ) );
			}
		} );
		framefix.tabbedPane( ).selectTab( "Tab 2" );
		doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				Assert.assertEquals( 1 , tabbedPane.getSelectedIndex( ) );
			}
		} );
	}
	
	@Test
	public void testUserAddAndCloseTab( )
	{
		tpfix.finishAnimation( );
		tpfix.newTabButton( ).click( );
		tpfix.finishAnimation( );
		tpfix.sanityCheck( );
		tpfix.requireTabCount( 2 );
		tpfix.finishAnimation( );
		tpfix.tabAt( 0 ).closeButton( ).click( );
		tpfix.requireTabCount( 1 );
		tpfix.sanityCheck( );
	}
	
	@Test
	public void testChangeTabAttributesInCode( ) throws TimeoutException, InterruptedException
	{
		DoSwing.doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				tabbedPane.setTitleAt( 0 , "New Title" );
				tpfix.sanityCheck( );
				
				JPanel panel = new JPanel( );
				tabbedPane.setComponentAt( 0 , panel );
				tpfix.sanityCheck( );
				
				Icon icon = UIManager.getIcon( "OptionPane.warningIcon" );
				tabbedPane.setIconAt( 0 , icon );
			}
		} );
		tabbedPaneUI.waitForUpdate( 100 );
		DoSwing.doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				tpfix.sanityCheck( );
				
				tabbedPane.setMnemonicAt( 0 , KeyEvent.VK_N );
				tpfix.sanityCheck( );
				
				JButton button = new JButton( "Test" );
				tabbedPane.setTabComponentAt( 0 , button );
				tpfix.sanityCheck( );
				
				tabbedPane.setEnabledAt( 0 , false );
			}
		} );
		tabbedPaneUI.waitForUpdate( 100 );
		DoSwing.doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				tpfix.sanityCheck( );
			}
		} );
	}
	
	@After
	public void tearDown( )
	{
		doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				window.dispose( );
				framefix.cleanUp( );
			}
		} );
	}
	
	private void doSwing( Runnable r )
	{
		try
		{
			SwingUtilities.invokeAndWait( r );
		}
		catch( InvocationTargetException | InterruptedException e )
		{
			throw new RuntimeException( e );
		}
	}
}
