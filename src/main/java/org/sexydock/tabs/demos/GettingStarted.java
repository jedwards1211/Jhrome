package org.sexydock.tabs.demos;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.sexydock.tabs.DefaultTabDropFailureHandler;
import org.sexydock.tabs.DefaultTabsRemovedHandler;
import org.sexydock.tabs.ITabFactory;
import org.sexydock.tabs.ITabbedPaneDndPolicy;
import org.sexydock.tabs.ITabbedPaneWindow;
import org.sexydock.tabs.ITabbedPaneWindowFactory;
import org.sexydock.tabs.Tab;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

public class GettingStarted implements ISexyTabsDemo
{
	// NOTE: This guide demonstrates how to enable all the special Google Chrome-like behavior
	// in a step-by-step manner. However, in practice, a tabbed application should be structured a bit
	// differently. See NotepadDemo for a prime example of how to structure a tabbed application.
	
	@Override
	public void start( )
	{
		// To turn on Google Chrome-style tabs for all JTabbedPanes in an existing
		// application, simply put the following code in your application startup:
		
		UIManager.getDefaults( ).put( "TabbedPaneUI" , JhromeTabbedPaneUI.class.getName( ) );
		
		final JTabbedPane tabbedPane = new JTabbedPane( );
		
		// Or, just set the tabbed pane's UI directly:
		
		tabbedPane.setUI( new JhromeTabbedPaneUI( ) );
		
		// Now the tabbed pane will look like Google Chrome, but besides letting
		// you reorder its tabs, it won't let you do anything special beyond
		// BasicTabbedPaneUI behavior.
		
		// To turn on tab close buttons, do this:
		
		tabbedPane.putClientProperty( JhromeTabbedPaneUI.TAB_CLOSE_BUTTONS_VISIBLE , true );
		
		// But how to make the window close when the user closes the last tab? Use this:
		
		tabbedPane.addContainerListener( new DefaultTabsRemovedHandler( ) );
		
		// To turn on the new tab button, do this:
		
		tabbedPane.putClientProperty( JhromeTabbedPaneUI.NEW_TAB_BUTTON_VISIBLE , true );
		
		// Not so fast! The new tab button won't work yet. You have to define how the
		// content of new tabs is created. Here's how:
		
		class MyContent extends JPanel
		{
			public MyContent( )
			{
				setOpaque( false );
			}
		}
		;
		
		ITabFactory tabFactory = new ITabFactory( )
		{
			int	tabCount	= 1;
			
			@Override
			public Tab createTabWithContent( )
			{
				String title = "Tab " + ( tabCount++ );
				
				JPanel content = new MyContent( );
				
				// put your content here!
				content.setLayout( new FlowLayout( ) );
				JLabel contentLabel = new JLabel( title );
				contentLabel.setFont( new Font( "Arial" , Font.BOLD , 72 ) );
				content.add( contentLabel );
				
				return new Tab( title , content );
			}
			
			@Override
			public Tab createTab( )
			{
				return new Tab( );
			}
		};
		
		tabbedPane.putClientProperty( JhromeTabbedPaneUI.TAB_FACTORY , tabFactory );
		
		// Not too bad...now to make it possible to drag tabs out of the tabbed pane
		// and back in!
		
		tabbedPane.putClientProperty( JhromeTabbedPaneUI.DND_POLICY , new ITabbedPaneDndPolicy( )
		{
			@Override
			public boolean isTearAwayAllowed( JTabbedPane tabbedPane , Tab tab )
			{
				return true;
			}
			
			@Override
			public boolean isSnapInAllowed( JTabbedPane tabbedPane , Tab tab )
			{
				// This way tabs we don't recognize can't be dragged in!
				return tab.getContent( ) instanceof MyContent;
			}
		} );
		
		// Then: what if the user drags a tab and drops it outside the window?
		// Here's how to make that pop up a new window.
		
		// Implementing ITabbedPaneWindow is not necessary, but it allows you to
		// let Jhrome do some of the work for you.
		
		class MyFrame extends JFrame implements ITabbedPaneWindow
		{
			public MyFrame( JTabbedPane tabbedPane )
			{
				super( "Hello World!" );
				this.tabbedPane = tabbedPane;
				getContentPane( ).add( tabbedPane , BorderLayout.CENTER );
				setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
			}
			
			JTabbedPane	tabbedPane;
			
			@Override
			public JTabbedPane getTabbedPane( )
			{
				return tabbedPane;
			}
			
			@Override
			public Window getWindow( )
			{
				return this;
			}
		}
		
		// Create a window factory that knows how to set up a new window with
		// a tabbed pane in it.
		
		ITabbedPaneWindowFactory windowFactory = new ITabbedPaneWindowFactory( )
		{
			@Override
			public ITabbedPaneWindow createWindow( )
			{
				JTabbedPane newTabbedPane = new JTabbedPane( );
				newTabbedPane.setUI( new JhromeTabbedPaneUI( ) );
				
				// copy all of the special properties we set up on the first
				// tabbed pane. In practice the tabbed pane would be created
				// by the window (see NotepadDemo for example), and there would
				// be no need for this, but I did it to make the steps in this
				// guide linear.
				
				JhromeTabbedPaneUI.copySettings( tabbedPane , newTabbedPane );
				newTabbedPane.addContainerListener( new DefaultTabsRemovedHandler( ) );
				
				return new MyFrame( newTabbedPane );
			}
		};
		
		// Finally, install a tab drop failure handler that will use the window factory:
		
		tabbedPane.putClientProperty( JhromeTabbedPaneUI.TAB_DROP_FAILURE_HANDLER , new DefaultTabDropFailureHandler( windowFactory ) );
		
		// Almost ready...create the first tab with the tab factory and add it:
		
		Tab tab = tabFactory.createTabWithContent( );
		tabbedPane.addTab( tab.getTitle( ) , tab.getContent( ) );
		
		// All right! Time to show off what we've done!
		
		MyFrame frame = new MyFrame( tabbedPane );
		
		frame.getContentPane( ).add( tabbedPane , BorderLayout.CENTER );
		frame.setSize( 800 , 600 );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
}
