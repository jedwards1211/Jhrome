package org.sexydock.tabs.test;

import javax.swing.JButton;
import javax.swing.JTabbedPane;

import junit.framework.Assert;

import org.fest.swing.core.Robot;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.sexydock.SwingUtils;
import org.sexydock.tabs.Tab;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

public class SDTabbedPaneFixture extends JTabbedPaneFixture
{
	public SDTabbedPaneFixture( Robot robot , JTabbedPane target )
	{
		super( robot , target );
	}
	
	public TabFixture tabAt( final int index )
	{
		class R implements Runnable
		{
			TabFixture	result	= null;
			
			@Override
			public void run( )
			{
				JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) target.getUI( );
				result = new TabFixture( robot , ui.getTabAt( index ) );
			}
		}
		;
		
		R r = new R( );
		SwingUtils.doSwing( r );
		
		return r.result;
	}
	
	public TabFixture tabTitled( final String title )
	{
		class R implements Runnable
		{
			TabFixture	result	= null;
			
			@Override
			public void run( )
			{
				JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) target.getUI( );
				for( Tab tab : ui.getTabs( ) )
				{
					if( title.equals( tab.getTitle( ) ) )
					{
						result = new TabFixture( robot , tab );
					}
				}
				throw new ComponentLookupException( "Couldn't find a tab titled '" + title + "'" );
			}
		}
		;
		
		R r = new R( );
		SwingUtils.doSwing( r );
		
		return r.result;
	}
	
	public TabFixture tabNamed( final String name )
	{
		class R implements Runnable
		{
			TabFixture	result	= null;
			
			@Override
			public void run( )
			{
				JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) target.getUI( );
				for( Tab tab : ui.getTabs( ) )
				{
					if( name.equals( tab.getName( ) ) )
					{
						result = new TabFixture( robot , tab );
					}
				}
				throw new ComponentLookupException( "Couldn't find a tab titled '" + name + "'" );
			}
		}
		;
		
		R r = new R( );
		SwingUtils.doSwing( r );
		
		return r.result;
	}
	
	public void sanityCheck( )
	{
		Runnable tester = new Runnable( )
		{
			@Override
			public void run( )
			{
				JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) target.getUI( );
				
				Assert.assertEquals( target.getTabCount( ) , ui.getTabCount( ) );
				
				for( int i = 0 ; i < target.getTabCount( ) ; i++ )
				{
					Tab tab = ui.getTabAt( i );
					Assert.assertEquals( target.getTitleAt( i ) , tab.getTitle( ) );
					Assert.assertEquals( target.getIconAt( i ) , tab.getIcon( ) );
					Assert.assertEquals( target.getTabComponentAt( i ) , tab.getTabComponent( ) );
					Assert.assertEquals( target.getMnemonicAt( i ) , tab.getMnemonic( ) );
					Assert.assertEquals( target.isEnabledAt( i ) , tab.isEnabled( ) );
					Assert.assertEquals( target.getSelectedIndex( ) == i , tab.isSelected( ) );
					Assert.assertEquals( target.getComponentAt( i ) , tab.getContent( ) );
					Assert.assertEquals( target.getSelectedIndex( ) == i , target.getComponentAt( i ).isVisible( ) );
				}
			}
		};
		
		SwingUtils.doSwing( tester );
	}
	
	public void requireTabCount( final int expected )
	{
		SwingUtils.doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) target.getUI( );
				Assert.assertEquals( expected , target.getTabCount( ) );
				Assert.assertEquals( expected , ui.getTabCount( ) );
			}
		} );
	}
	
	public JButtonFixture newTabButton( )
	{
		class R implements Runnable
		{
			JButtonFixture	result	= null;
			
			@Override
			public void run( )
			{
				JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) target.getUI( );
				result = new JButtonFixture( robot , ui.getNewTabButton( ) );
			}
		}
		
		R r = new R( );
		SwingUtils.doSwing( r );
		
		return r.result;
	}
	
	public void waitForAnimationToFinish( ) throws InterruptedException
	{
		JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) target.getUI( );
		ui.waitForAnimationToFinish( );
	}
	
	public void finishAnimation( )
	{
		SwingUtils.doSwing( new Runnable( )
		{
			@Override
			public void run( )
			{
				JhromeTabbedPaneUI ui = ( JhromeTabbedPaneUI ) target.getUI( );
				ui.finishAnimation( );
			}
		} );
	}
}
