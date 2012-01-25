
package org.jhrome;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class JhromeTabbedPane extends JLayeredPane
{
	public JhromeTabbedPane( )
	{
		init( );
	}
	
	private List<TabInfo>	tabs	= new ArrayList<TabInfo>( );
	
	private static class TabInfo
	{
		ActionListener	closeButtonHandler;
		JhromeTab		tab;
		Dimension		prefSize;
		boolean			removing;
		int				animX;
		int				animWidth;
	}
	
	private int					overlap			= 13;
	
	private double				animFactor		= 0.7;
	
	private javax.swing.Timer	animTimer;
	
	private TabLayoutManager	layout;
	
	private boolean				useUniformWidth	= true;
	private int					uniformWidth	= 300;
	
	private boolean				mouseOver		= true;
	
	private MouseManager		mouseOverManager;
	
	private TabInfo				selectedTab		= null;
	
	private class MouseManager extends RecursiveListener
	{
		MouseAdapter	adapter	= new MouseAdapter( )
								{
									private void handleEvent( MouseEvent e )
									{
										Point p = e.getPoint( );
										p = SwingUtilities.convertPoint( ( Component ) e.getSource( ) , p , JhromeTabbedPane.this );
										boolean newMouseOver = contains( p );
										if( newMouseOver != mouseOver )
										{
											mouseOver = newMouseOver;
											invalidate( );
											validate( );
										}
									}
									
									@Override
									public void mouseEntered( MouseEvent e )
									{
										handleEvent( e );
									}
									
									@Override
									public void mouseExited( MouseEvent e )
									{
										handleEvent( e );
									}
									
									@Override
									public void mouseMoved( MouseEvent e )
									{
										Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , JhromeTabbedPane.this );
										JhromeTab tab = getHoverableTabAt( p );
										for( TabInfo info : tabs )
										{
											info.tab.setRollover( info.tab == tab );
										}
									}
									
									@Override
									public void mousePressed( MouseEvent e )
									{
										Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , JhromeTabbedPane.this );
										JhromeTab tab = getSelectableTabAt( p );
										if( tab != null )
										{
											setSelectedTab( tab );
										}
									}
								};
		
		@Override
		protected void install( Component c )
		{
			c.addMouseListener( adapter );
			c.addMouseMotionListener( adapter );
		}
		
		@Override
		protected void uninstall( Component c )
		{
			c.removeMouseListener( adapter );
			c.removeMouseMotionListener( adapter );
		}
	}
	
	private void init( )
	{
		animTimer = new Timer( 15 , new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				invalidate( );
				validate( );
			}
		} );
		layout = new TabLayoutManager( );
		setLayout( layout );
		
		invalidate( );
		validate( );
		
		mouseOverManager = new MouseManager( );
		mouseOverManager.install( this );
	}
	
	private void setSelectedTab( TabInfo info )
	{
		if( selectedTab != info )
		{
			if( selectedTab != null )
			{
				selectedTab.tab.setSelected( false );
			}
			
			selectedTab = info;
			
			if( selectedTab != null )
			{
				selectedTab.tab.setSelected( true );
			}
			
			invalidate( );
			validate( );
		}
	}
	
	public void setSelectedTab( JhromeTab tab )
	{
		if( tab == null )
		{
			setSelectedTab( ( TabInfo ) null );
		}
		else
		{
			TabInfo info = getInfo( tab );
			if( info == null || info.removing )
			{
				throw new IllegalArgumentException( "tab must be a child of this " + getClass( ).getName( ) );
			}
			setSelectedTab( info );
		}
	}
	
	public JhromeTab getHoverableTabAt( Point p )
	{
		for( int i = -1 ; i < tabs.size( ) ; i++ )
		{
			TabInfo info = i < 0 ? selectedTab : tabs.get( i );
			if( info == null )
			{
				continue;
			}
			if( info == selectedTab && i >= 0 )
			{
				continue;
			}
			JhromeTab tab = info.tab;
			Component c = tab.getRenderer( );
			Point converted = SwingUtilities.convertPoint( this , p , c );
			if( c.contains( converted ) )
			{
				if( tab.isHoverableAt( converted ) )
				{
					return tab;
				}
			}
		}
		return null;
	}
	
	public JhromeTab getDraggableTabAt( Point p )
	{
		for( int i = -1 ; i < tabs.size( ) ; i++ )
		{
			TabInfo info = i < 0 ? selectedTab : tabs.get( i );
			if( info == null )
			{
				continue;
			}
			if( info == selectedTab && i >= 0 )
			{
				continue;
			}
			JhromeTab tab = info.tab;
			Component c = tab.getRenderer( );
			Point converted = SwingUtilities.convertPoint( this , p , c );
			if( c.contains( converted ) )
			{
				if( tab.isDraggableAt( converted ) )
				{
					return tab;
				}
			}
		}
		return null;
	}
	
	public JhromeTab getSelectableTabAt( Point p )
	{
		for( int i = -1 ; i < tabs.size( ) ; i++ )
		{
			TabInfo info = i < 0 ? selectedTab : tabs.get( i );
			if( info == null )
			{
				continue;
			}
			if( info == selectedTab && i >= 0 )
			{
				continue;
			}
			JhromeTab tab = info.tab;
			Component c = tab.getRenderer( );
			Point converted = SwingUtilities.convertPoint( this , p , c );
			if( c.contains( converted ) )
			{
				if( tab.isSelectableAt( converted ) )
				{
					return tab;
				}
			}
		}
		return null;
	}
	
	private TabInfo getInfo( JhromeTab tab )
	{
		for( TabInfo info : tabs )
		{
			if( info.tab == tab )
			{
				return info;
			}
		}
		return null;
	}
	
	public int devirtualizeIndex( int index )
	{
		int virtual = 0;
		
		int devirtualized;
		for( devirtualized = 0 ; devirtualized < tabs.size( ) ; devirtualized++ )
		{
			if( virtual == index )
			{
				break;
			}
			if( !tabs.get( devirtualized ).removing )
			{
				virtual++ ;
			}
		}
		return devirtualized;
	}
	
	public void addTab( int index , final JhromeTab tab )
	{
		TabInfo info = new TabInfo( );
		info.tab = tab;
		info.prefSize = tab.getRenderer( ).getPreferredSize( );
		info.animWidth = 0;
		info.removing = false;
		if( tab.getCloseButton( ) != null )
		{
			info.closeButtonHandler = new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					removeTab( tab );
				}
			};
			tab.getCloseButton( ).addActionListener( info.closeButtonHandler );
		}
		
		index = devirtualizeIndex( index );
		if( index > 0 )
		{
			TabInfo prev = tabs.get( index - 1 );
			info.animX = prev.animX + prev.animWidth - overlap;
		}
		tabs.add( index , info );
		add( tab.getRenderer( ) );
		
		animTimer.start( );
	}
	
	public void removeTab( JhromeTab tab )
	{
		TabInfo info = getInfo( tab );
		if( info != null )
		{
			if( info == selectedTab )
			{
				if( tabs.size( ) == 1 )
				{
					setSelectedTab( ( TabInfo ) null );
				}
				else
				{
					int index = tabs.indexOf( info );
					setSelectedTab( tabs.get( index < tabs.size( ) - 1 ? index + 1 : index - 1 ) );
				}
			}
			info.removing = true;
			animTimer.start( );
		}
	}
	
	private void actuallyRemoveTab( JhromeTab tab )
	{
		TabInfo info = getInfo( tab );
		if( info != null )
		{
			if( tab.getCloseButton( ) != null && info.closeButtonHandler != null )
			{
				tab.getCloseButton( ).removeActionListener( info.closeButtonHandler );
			}
			remove( tab.getRenderer( ) );
			if( tab.getContent( ) != null )
			{
				remove( tab.getContent( ) );
			}
			tabs.remove( info );
		}
	}
	
	public JButton getNewTabButton( )
	{
		return null;
	}
	
	private int animate( int value , int target )
	{
		int d = value - target;
		d *= animFactor;
		return d == 0 ? target : target + d;
	}
	
	private class TabLayoutManager implements LayoutManager
	{
		Dimension	lastSize		= null;
		
		int			animTotalWidth	= 0;
		
		@Override
		public void addLayoutComponent( String name , Component comp )
		{
		}
		
		@Override
		public void removeLayoutComponent( Component comp )
		{
		}
		
		@Override
		public Dimension preferredLayoutSize( Container parent )
		{
			return null;
		}
		
		@Override
		public Dimension minimumLayoutSize( Container parent )
		{
			return null;
		}
		
		@Override
		public void layoutContainer( Container parent )
		{
			Dimension size = getSize( );
			
			boolean reset = false;
			
			int width = getWidth( );
			int height = getHeight( );
			
			Insets insets = getInsets( );
			
			int availWidth = width - insets.left - insets.right;
			int availHeight = height - insets.top - insets.bottom;
			
			boolean animNeeded = false;
			
			int x = 0;
			
			int newTotalWidth = 0;
			
			for( int i = 0 ; i < tabs.size( ) ; i++ )
			{
				TabInfo info = tabs.get( i );
				
				int targetWidth = info.removing ? 0 : useUniformWidth ? uniformWidth : info.prefSize.width;
				
				if( info.animWidth != targetWidth )
				{
					if( reset )
					{
						info.animWidth = targetWidth;
					}
					else
					{
						animNeeded = true;
						info.animWidth = animate( info.animWidth , targetWidth );
					}
				}
				
				if( info.animX != x )
				{
					if( reset )
					{
						info.animX = x;
					}
					else
					{
						animNeeded = true;
						info.animX = animate( info.animX , x );
					}
				}
				
				newTotalWidth = Math.max( newTotalWidth , info.animX + info.animWidth );
				
				if( !info.removing )
				{
					x += targetWidth;
				}
			}
			
			if( newTotalWidth > animTotalWidth )
			{
				animTotalWidth = newTotalWidth;
			}
			else if( !mouseOver && animTotalWidth != newTotalWidth )
			{
				if( reset )
				{
					animTotalWidth = newTotalWidth;
				}
				else
				{
					animNeeded = true;
					animTotalWidth = animate( animTotalWidth , newTotalWidth );
				}
			}
			
			double widthRatio = animTotalWidth > availWidth ? availWidth / ( double ) animTotalWidth : 1.0;
			
			for( int i = 0 ; i < tabs.size( ) ; i++ )
			{
				TabInfo info = tabs.get( i );
				info.tab.getRenderer( ).setBounds( insets.left + ( int ) ( info.animX * widthRatio ) , height - insets.bottom - Math.min( availHeight , info.prefSize.height ) , ( int ) ( info.animWidth * widthRatio ) + ( i < tabs.size( ) - 1 ? overlap : 0 ) ,
						Math.min( availHeight , info.prefSize.height ) );
			}
			
			for( int i = tabs.size( ) - 1 ; i >= 0 ; i-- )
			{
				TabInfo info = tabs.get( i );
				if( info.removing && info.animWidth == 0 )
				{
					actuallyRemoveTab( info.tab );
				}
			}
			
			int layer = JLayeredPane.DEFAULT_LAYER;
			
			if( selectedTab != null )
			{
				setComponentZOrder( selectedTab.tab.getRenderer( ) , layer++ );
			}
			for( TabInfo info : tabs )
			{
				if( info == selectedTab )
				{
					continue;
				}
				setComponentZOrder( info.tab.getRenderer( ) , layer++ );
			}
			
			lastSize = size;
			
			if( animNeeded && !animTimer.isRunning( ) )
			{
				animTimer.start( );
			}
			else if( !animNeeded && animTimer.isRunning( ) )
			{
				animTimer.stop( );
			}
		}
	}
	
}
