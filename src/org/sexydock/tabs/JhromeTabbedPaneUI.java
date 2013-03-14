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

package org.sexydock.tabs;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;

import org.omg.CORBA.BooleanHolder;
import org.sexydock.tabs.event.ITabbedPaneListener;
import org.sexydock.tabs.event.TabAddedEvent;
import org.sexydock.tabs.event.TabMovedEvent;
import org.sexydock.tabs.event.TabRemovedEvent;
import org.sexydock.tabs.event.TabSelectedEvent;
import org.sexydock.tabs.event.TabbedPaneEvent;
import org.sexydock.tabs.event.TabsClearedEvent;
import org.sexydock.tabs.jhrome.JhromeContentPanelBorder;
import org.sexydock.tabs.jhrome.JhromeNewTabButtonUI;

/**
 * {@link JhromeTabbedPaneUI} is a Google Chrome-like tabbed pane, providing animated tab layout, drag and drop capabilities, and a new tab button. All Google
 * Chrome behavior is provided by default. Tabs can be dragged around within {@code TabbedPane} and rearranged, they can be "torn away" or dragged out and
 * opened in new windows, and they can be dragged from one window to another. If all the tabs in a {@code TabbedPane} are closed or torn away, the window
 * containing that {@code TabbedPane} is disposed. When a tab is torn away, a ghosted drag image window showing the tab and its contents will appear and follow
 * the mouse cursor until the tab is dragged over another {@code TabbedPane} or dropped.<br />
 * <br />
 * 
 * Animation includes tabs expanding when added, contracting when removed, jumping around when being reordered, contracting simultaneously when there is not
 * enough room, and expanding simultaneously when there is room again and the mouse is no longer on top of the tab zone.<br />
 * <br />
 * 
 * {@code TabbedPane} is designed to allow you to customize the look and behavior as much as possible. The following interfaces help with customization:
 * <ul>
 * <li>{@link Tab} provides an interface to tab renderers/content. You can use literally any {@link Component} (or combination of {@code Component}s) in a tab
 * renderer by providing them through an {@code Tab} implementation.
 * <li>{@link ITabFactory} creates new tabs when the new tab button is clicked. By providing your own tab factory you can use any kind of tabs you like with
 * {@code TabbedPane}.</li>
 * <li>{@link ITabbedPaneDnDPolicy} controls whether tabs may be torn away or snapped back in (by default both are always allowed). By providing your own DnD
 * policy you can create arbitrarily complex behavior, preventing only certain tabs from being torn away, certain tabs from being snapped in, only at certain
 * times, etc.</li>
 * <li>{@link IFloatingTabHandler} determines what happens when a tab is torn away from this {@code TabbedPane} and is "floating."
 * {@link DefaultFloatingTabHandler} shows a ghost drag image of the tab in another window, but you may create different behavior by providing your own
 * {@link IFloatingTabHandler}.
 * <li>{@link ITabDropFailureHandler} determines what to do when a tab drop fails (i.e. it is dropped on the desktop or another application that rejects the
 * drop). The {@link DefaultTabDropFailureHandler} creates a new window for the tab, but you can do something else by providing your own
 * {@link ITabDropFailureHandler}.
 * </ul>
 * 
 * Also, {@code TabbedPane} allows you to get its new tab button and content pane so that you can modify them arbitrarily.<br />
 * <br />
 * 
 * Since the layout is animated, when you remove a tab from {@code TabbedPane} (using {@link #removeTabInternal(Tab)}), it makes it start contracting and
 * doesn't actually remove the tab renderer component until it is done contracting. However, all public methods that involve the tab list behave as if tabs are
 * removed immediately. For example, if you remove a tab, it will no longer show up in {@link #getTabs()}, even while the tab renderer component is still a
 * child of this {@code TabbedPane} and contracting. If you add the tab back before it is done contracting, it will jump to the new position and expand back to
 * full size.
 * 
 * @author andy.edwards
 */
@SuppressWarnings( "serial" )
public class JhromeTabbedPaneUI extends TabbedPaneUI
{
	public JhromeTabbedPaneUI( )
	{
	}
	
	private Map<Component, TabInfo>	contentMap	= new HashMap<Component, TabInfo>( );
	
	private List<TabInfo>			tabs		= new ArrayList<TabInfo>( );
	
	private JTabbedPane				tabbedPane;
	private JLayeredPane			tabLayeredPane;
	
	private static class TabInfo
	{
		ActionListener	closeButtonHandler;
		Tab				tab;
		Dimension		prefSize;
		
		/**
		 * Whether the tab is being removed (contracting until its width reaches zero, when it will be completely removed)
		 */
		boolean			isBeingRemoved;
		
		/**
		 * The tab's target x position in virtual coordinate space. It will be scaled down to produce the actual target x position.
		 */
		int				vTargetX;
		/**
		 * The tab's target width in virtual coordinate space. This does not include the overlap area -- it is the distance to the virtual target x position of
		 * the next tab. To get the actual target width, this value will be scaled down and the overlap amount will be added.
		 */
		int				vTargetWidth;
		
		/**
		 * The tab's target bounds in actual coordinate space. Not valid for tabs that are being removed.
		 */
		Rectangle		targetBounds	= new Rectangle( );
		
		/**
		 * The tab's current x position in virtual coordinate space. It will be scaled down to produce the actual current x position.
		 */
		int				vCurrentX;
		/**
		 * The tab's current width in virtual coordinate space. This does not include the overlap area -- it is the distance to the virtual current x position
		 * of the next tab. To get the actual current width, this value will be scaled down and the overlap amount will be added.
		 */
		int				vCurrentWidth;
		
		/**
		 * Whether the tab is being dragged.
		 */
		boolean			isBeingDragged;
		
		/**
		 * The x position of the dragging mouse cursor in actual coordinate space.
		 */
		int				dragX;
		/**
		 * The relative x position at which the tab was grabbed, as a proportion of its width (0.0 = left side, 0.5 = middle, 1.0 = right side). This way if the
		 * tab width changes while it's being dragged, the layout manager can still give it a reasonable position relative to the mouse cursor.
		 */
		double			grabX;
	}
	
	private int							overlap						= 13;
	
	private double						animFactor					= 0.7;
	
	private javax.swing.Timer			animTimer;
	
	private TabLayoutManager			layout;
	
	private boolean						useUniformWidth				= true;
	private int							maxUniformWidth				= 300;
	
	private boolean						mouseOverTopZone			= true;
	
	private MouseManager				mouseOverManager;
	
	private TabInfo						selectedTab					= null;
	
	private Component					currentContent				= null;
	
	/**
	 * How many pixels the content panel overlaps the tabs. This is necessary with the Google Chrome appearance to make the selected tab and the content panel
	 * look like a contiguous object
	 */
	private int							contentPanelOverlap			= 1;
	private Border						defaultContentPanelBorder	= new JhromeContentPanelBorder( );
	private Border						contentPanelBorder			= defaultContentPanelBorder;
	private Rectangle					contentPanelBounds			= new Rectangle( );
	
	private int							tabMargin					= 2;
	
	private JPanel						rightButtonsPanel;
	
	private JButton						newTabButton;
	
	private ActionListener				newTabButtonListener;
	
	private DragHandler					dragHandler;
	private DropHandler					dropHandler;
	
	private ITabDropFailureHandler		tabDropFailureHandler		= new DefaultTabDropFailureHandler( );
	
	private IFloatingTabHandler			floatingTabHandler			= new DefaultFloatingTabHandler( );
	
	private ITabFactory					tabFactory					= new DefaultTabFactory( );
	
	private Rectangle					topZone						= new Rectangle( );
	private Rectangle					tabZone						= new Rectangle( );
	
	private int							extraDropZoneSpace			= 25;
	private Rectangle					dropZone					= new Rectangle( );
	
	private ITabbedPaneDnDPolicy		dndPolicy					= null;
	
	private List<ITabbedPaneListener>	tabListeners				= new ArrayList<ITabbedPaneListener>( );
	
	private Handler						handler						= new Handler( );
	
	private String						newTabButtonUiClassId		= "JhromeNewTabButtonUI";
	
	private class MouseManager extends RecursiveListener
	{
		MouseAdapter	adapter	= new MouseAdapter( )
								{
									private void updateHoldTabScale( MouseEvent e )
									{
										Point p = e.getPoint( );
										p = SwingUtilities.convertPoint( ( Component ) e.getSource( ) , p , tabbedPane );
										boolean newMouseOver = Utils.contains( topZone , p );
										if( newMouseOver != mouseOverTopZone )
										{
											mouseOverTopZone = newMouseOver;
											tabbedPane.invalidate( );
											tabbedPane.validate( );
										}
									}
									
									@Override
									public void mouseEntered( MouseEvent e )
									{
										updateHoldTabScale( e );
									}
									
									@Override
									public void mouseExited( MouseEvent e )
									{
										updateHoldTabScale( e );
									}
									
									@Override
									public void mouseMoved( MouseEvent e )
									{
										updateHoldTabScale( e );
										
										Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , tabbedPane );
										Tab tab = getHoverableTabAt( p );
										for( TabInfo info : tabs )
										{
											info.tab.setRollover( info.tab == tab );
										}
									}
									
									@Override
									public void mousePressed( MouseEvent e )
									{
										Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , tabbedPane );
										Tab tab = getSelectableTabAt( p );
										if( tab != null )
										{
											tabbedPane.setSelectedComponent( tab.getContent( ) );
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
	
	@Override
	public void installUI( JComponent c )
	{
		checkEDT( );
		tabbedPane = ( JTabbedPane ) c;
		init( );
	}
	
	@Override
	public void uninstallUI( JComponent c )
	{
		if( c != tabbedPane )
		{
			throw new IllegalArgumentException( "Cannot uninstall from a different component than this was installed on" );
		}
		checkEDT( );
		dispose( );
	}
	
	private class TabLayeredPane extends JLayeredPane implements UIResource
	{
		
	}
	
	private void init( )
	{
		animTimer = new Timer( 15 , new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( tabbedPane != null )
				{
					tabbedPane.invalidate( );
					tabbedPane.validate( );
				}
			}
		} );
		layout = new TabLayoutManager( );
		tabbedPane.setLayout( layout );
		tabLayeredPane = new TabLayeredPane( );
		tabLayeredPane.setLayout( null );
		tabbedPane.add( tabLayeredPane );
		
		tabbedPane.invalidate( );
		tabbedPane.validate( );
		
		mouseOverManager = new MouseManager( );
		mouseOverManager.addExcludedComponent( tabbedPane );
		mouseOverManager.install( tabbedPane );
		
		newTabButton = new JButton( );
		
		newTabButtonListener = new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( tabFactory == null )
				{
					return;
				}
				Tab newTab = tabFactory.createTab( );
				tabbedPane.addTab( newTab.getTitle( ) , newTab.getContent( ) );
				tabbedPane.setSelectedComponent( newTab.getContent( ) );
			}
		};
		
		newTabButton.addActionListener( newTabButtonListener );
		updateNewTabButtonVisible( );
		updateNewTabButtonUI( );
		updateContentPanelBorder( );
		
		rightButtonsPanel = new JPanel( );
		rightButtonsPanel.setLayout( new GridBagLayout( ) );
		rightButtonsPanel.setOpaque( false );
		
		GridBagConstraints gbc = new GridBagConstraints( );
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets( 1 , 2 , 0 , 0 );
		rightButtonsPanel.add( newTabButton , gbc );
		tabLayeredPane.add( rightButtonsPanel );
		
		dragHandler = new DragHandler( tabbedPane , DnDConstants.ACTION_MOVE );
		dropHandler = new DropHandler( tabbedPane );
		
		handler = new Handler( );
		tabbedPane.addContainerListener( handler );
		tabbedPane.getModel( ).addChangeListener( handler );
		tabbedPane.addPropertyChangeListener( handler );
	}
	
	private void updateNewTabButtonVisible( )
	{
		Object ntbvProp = tabbedPane.getClientProperty( "newTabButtonVisible" );
		if( ntbvProp != null && ntbvProp instanceof Boolean )
		{
			newTabButton.setVisible( ( Boolean ) ntbvProp );
		}
		else
		{
			newTabButton.setVisible( false );
		}
	}
	
	private void updateContentPanelBorder( )
	{
		Object cpbProp = tabbedPane.getClientProperty( "contentPanelBorder" );
		if( cpbProp != null && cpbProp instanceof Border )
		{
			contentPanelBorder = ( Border ) cpbProp;
		}
		else
		{
			contentPanelBorder = defaultContentPanelBorder;
		}
	}
	
	private void updateNewTabButtonUI( )
	{
		Object ntbuiProp = tabbedPane.getClientProperty( "newTabButtonUI" );
		if( ntbuiProp != null && ntbuiProp instanceof ButtonUI )
		{
			newTabButton.setUI( ( ButtonUI ) ntbuiProp );
		}
		else
		{
			newTabButton.setUI( new JhromeNewTabButtonUI( ) );
		}
	}
	
	public ITabFactory getTabFactory( )
	{
		return tabFactory;
	}
	
	public void setTabFactory( ITabFactory tabFactory )
	{
		this.tabFactory = tabFactory;
	}
	
	/**
	 * Sets the drop failure handler to a {@link DefaultTabDropFailureHandler} with the given window factory.
	 */
	public void setWindowFactory( ITabbedPaneWindowFactory windowFactory )
	{
		setDropFailureHandler( new DefaultTabDropFailureHandler( windowFactory ) );
	}
	
	public ITabDropFailureHandler getDropFailureHandler( )
	{
		return tabDropFailureHandler;
	}
	
	public void setDropFailureHandler( DefaultTabDropFailureHandler dropFailureHandler )
	{
		this.tabDropFailureHandler = dropFailureHandler;
	}
	
	public ITabbedPaneDnDPolicy getDnDPolicy( )
	{
		return dndPolicy;
	}
	
	public void setDnDPolicy( ITabbedPaneDnDPolicy dndPolicy )
	{
		this.dndPolicy = dndPolicy;
	}
	
	/**
	 * @return whether to make all tabs the same width. If uniform width is not used, the tabs' preferred widths will be used.
	 */
	public boolean isUseUniformWidth( )
	{
		return useUniformWidth;
	}
	
	/**
	 * Sets whether to make all tabs the same width. If uniform width is not used, the tabs' preferred widths will be used.
	 */
	public void setUseUniformWidth( boolean useUniformWidth )
	{
		if( this.useUniformWidth != useUniformWidth )
		{
			this.useUniformWidth = useUniformWidth;
			tabbedPane.invalidate( );
			tabbedPane.validate( );
		}
	}
	
	/**
	 * @return the width to make each tab if {@link #isUseUniformWidth()} is {@code true}. If there are too many tabs in this tabbed pane to fit at this width,
	 *         they will of course be squashed down.
	 */
	public int getMaxUniformWidth( )
	{
		return maxUniformWidth;
	}
	
	/**
	 * Sets the width to make each tab if {@link #isUseUniformWidth()} is {@code true}. If there are too many tabs in this tabbed pane to fit at this width,
	 * they will of course be squashed down.
	 */
	public void setMaxUniformWidth( int maxUniformWidth )
	{
		if( this.maxUniformWidth != maxUniformWidth )
		{
			this.maxUniformWidth = maxUniformWidth;
			tabbedPane.invalidate( );
			tabbedPane.validate( );
		}
	}
	
	/**
	 * @param p
	 *            a {@link Point} in this tabbed pane's coordinate system.
	 * 
	 * @return the hoverable tab under {@code p}, or {@code null} if no such tab exists.
	 * 
	 * @see Tab#isHoverableAt(Point)
	 */
	public Tab getHoverableTabAt( Point p )
	{
		checkEDT( );
		
		for( int i = -1 ; i < tabs.size( ) ; i++ )
		{
			TabInfo info = i < 0 ? selectedTab : tabs.get( i );
			if( info == null || info.isBeingRemoved )
			{
				continue;
			}
			if( info == selectedTab && i >= 0 )
			{
				continue;
			}
			Tab tab = info.tab;
			Component c = tab;
			Point converted = SwingUtilities.convertPoint( tabbedPane , p , c );
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
	
	/**
	 * @param p
	 *            a {@link Point} in this tabbed pane's coordinate system.
	 * 
	 * @return the draggable tab under {@code p}, or {@code null} if no such tab exists.
	 * 
	 * @see Tab#isDraggableAt(Point)
	 */
	public Tab getDraggableTabAt( Point p )
	{
		checkEDT( );
		
		for( int i = -1 ; i < tabs.size( ) ; i++ )
		{
			TabInfo info = i < 0 ? selectedTab : tabs.get( i );
			if( info == null || info.isBeingRemoved )
			{
				continue;
			}
			if( info == selectedTab && i >= 0 )
			{
				continue;
			}
			Tab tab = info.tab;
			Component c = tab;
			Point converted = SwingUtilities.convertPoint( tabbedPane , p , c );
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
	
	/**
	 * @param p
	 *            a {@link Point} in this tabbed pane's coordinate system.
	 * 
	 * @return the selectable tab under {@code p}, or {@code null} if no such tab exists.
	 * 
	 * @see Tab#isSelectableAt(Point)
	 */
	public Tab getSelectableTabAt( Point p )
	{
		checkEDT( );
		
		for( int i = -1 ; i < tabs.size( ) ; i++ )
		{
			TabInfo info = i < 0 ? selectedTab : tabs.get( i );
			if( info == null || info.isBeingRemoved )
			{
				continue;
			}
			if( info == selectedTab && i >= 0 )
			{
				continue;
			}
			Tab tab = info.tab;
			Component c = tab;
			Point converted = SwingUtilities.convertPoint( tabbedPane , p , c );
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
	
	private int getInfoIndex( Tab tab )
	{
		checkEDT( );
		for( int i = 0 ; i < tabs.size( ) ; i++ )
		{
			TabInfo info = tabs.get( i );
			if( info.tab == tab )
			{
				return i;
			}
		}
		return -1;
	}
	
	private TabInfo getInfo( Tab tab )
	{
		checkEDT( );
		
		for( TabInfo info : tabs )
		{
			if( info.tab == tab )
			{
				return info;
			}
		}
		return null;
	}
	
	private int actualizeIndex( int index )
	{
		checkEDT( );
		
		int virtual = 0;
		
		int actual;
		for( actual = 0 ; actual < tabs.size( ) ; actual++ )
		{
			if( virtual == index )
			{
				break;
			}
			if( !tabs.get( actual ).isBeingRemoved )
			{
				virtual++ ;
			}
		}
		return actual;
	}
	
	private int virtualizeIndex( int index )
	{
		checkEDT( );
		
		int virtual = 0;
		
		for( int actual = 0 ; actual < index ; actual++ )
		{
			if( !tabs.get( actual ).isBeingRemoved )
			{
				virtual++ ;
			}
		}
		return virtual;
	}
	
	/**
	 * @return the number of tabs in this tabbed pane.
	 */
	public int getTabCount( )
	{
		checkEDT( );
		
		int count = 0;
		for( int i = 0 ; i < tabs.size( ) ; i++ )
		{
			if( !tabs.get( i ).isBeingRemoved )
			{
				count++ ;
			}
		}
		return count;
	}
	
	public Tab getTabAt( int vIndex )
	{
		return tabs.get( actualizeIndex( vIndex ) ).tab;
	}
	
	/**
	 * @return a newly-constructed {@link List} of the tabs in this tabbed pane.
	 */
	public List<Tab> getTabs( )
	{
		checkEDT( );
		
		List<Tab> result = new ArrayList<Tab>( );
		for( TabInfo info : tabs )
		{
			if( !info.isBeingRemoved )
			{
				result.add( info.tab );
			}
		}
		return result;
	}
	
	/**
	 * Inserts a tab at a specific index in the tabbed pane. The new tab will not be selected.
	 * 
	 * @param vIndex
	 *            the index to add the tab at. Like {@link List#add(int, Object)}, this method will insert the new tab before the tab at {@code index}.
	 * @param tab
	 *            the tab to add.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code index} is less than 0 or greater than the tab count, or {@code tab} is already a member of this tabbed pane.
	 */
	private void addTabInternal( int vIndex , final Tab tab )
	{
		addTabInternal( vIndex , tab , true );
	}
	
	/**
	 * Inserts a tab at a specific index in the tabbed pane.
	 * 
	 * @param vIndex
	 *            the index to add the tab at. Like {@link List#add(int, Object)}, this method will insert the new tab before the tab at {@code index}.
	 * @param tab
	 *            the tab to add.
	 * @param expand
	 *            whether to animate the new tab expansion. If {@code false}, the new tab will immediately appear at full size.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code index} is less than 0 or greater than the tab count, or {@code tab} is already a member of this tabbed pane.
	 */
	private void addTabInternal( int vIndex , final Tab tab , boolean expand )
	{
		checkEDT( );
		
		int tabCount = getTabCount( );
		if( vIndex < 0 || vIndex > tabCount )
		{
			throw new IndexOutOfBoundsException( String.format( "Invalid insertion index: %d (tab count: %d)" , vIndex , tabCount ) );
		}
		TabInfo existing = getInfo( tab );
		
		if( existing != null )
		{
			if( existing.isBeingRemoved )
			{
				actuallyRemoveTab( tab );
			}
			else
			{
				throw new IllegalArgumentException( "Tab has already been added: " + tab );
			}
		}
		
		TabAddedEvent event = new TabAddedEvent( tabbedPane , System.currentTimeMillis( ) , tab , vIndex );
		
		int index = actualizeIndex( vIndex );
		
		TabInfo info = new TabInfo( );
		info.tab = tab;
		info.prefSize = tab.getPreferredSize( );
		info.vCurrentWidth = expand ? 0 : info.prefSize.width;
		info.isBeingRemoved = false;
		
		if( index > 0 )
		{
			TabInfo prev = tabs.get( index - 1 );
			info.vCurrentX = prev.vCurrentX + prev.vCurrentWidth - overlap;
		}
		tabs.add( index , info );
		contentMap.put( tab.getContent( ) , info );
		
		tabLayeredPane.add( tab );
		
		tabbedPane.invalidate( );
		tabbedPane.validate( );
		
		notifyTabbedPaneListeners( event );
	}
	
	public void addTab( int vIndex , Tab tab , boolean expand )
	{
		addTabInternal( vIndex , tab , expand );
		handler.disable = true;
		try
		{
			tabbedPane.insertTab( tab.getTitle( ) , tab.getIcon( ) , tab.getContent( ) , tab.getToolTipText( ) , vIndex );
			tabbedPane.setTabComponentAt( vIndex , tab.getTabComponent( ) );
		}
		finally
		{
			handler.disable = false;
		}
		setSelectedIndexInternal( tabbedPane.getSelectedIndex( ) );
	}
	
	/**
	 * Moves a tab from its current index to a new index.
	 * 
	 * @param tab
	 *            the tab to move.
	 * @param vNewIndex
	 *            the index to move the tab to. Like {@link List#add(int, Object)}, this method will insert {@code tab} before the tab at {@code newIndex}.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code newIndex} is less than 0, greater than the tab count, or {@code tab} is not a member of this tabbed pane.
	 */
	private void moveTabInternal( Tab tab , int vNewIndex )
	{
		int tabCount = getTabCount( );
		if( vNewIndex < 0 || vNewIndex > tabCount )
		{
			throw new IndexOutOfBoundsException( String.format( "Invalid new index: %d (tab count: %d)" , vNewIndex , tabCount ) );
		}
		
		int actualNewIndex = actualizeIndex( vNewIndex );
		int currentIndex = getInfoIndex( tab );
		
		if( currentIndex < 0 )
		{
			throw new IllegalArgumentException( "Tab is not a member of this tabbed pane: " + tab );
		}
		
		TabInfo info = tabs.get( currentIndex );
		
		if( info.isBeingRemoved )
		{
			throw new IllegalArgumentException( "Tab is not a member of this tabbed pane: " + tab );
		}
		
		if( actualNewIndex != currentIndex )
		{
			int vCurrentIndex = virtualizeIndex( currentIndex );
			
			TabMovedEvent event = new TabMovedEvent( tabbedPane , System.currentTimeMillis( ) , info.tab , vCurrentIndex , vNewIndex );
			
			actualNewIndex = Math.min( actualNewIndex , tabs.size( ) - 1 );
			tabs.remove( info );
			tabs.add( actualNewIndex , info );
			
			tabbedPane.invalidate( );
			tabbedPane.validate( );
			
			notifyTabbedPaneListeners( event );
		}
		
	}
	
	/**
	 * Moves the given tab with animation (if enabled). Attempting to do this by removing / adding back the tab in the {@link JTabbedPane} won't produce the
	 * same effect.
	 * 
	 * @param vCurrentIndex
	 *            the current index of the tab to move.
	 * @param vNewIndex
	 *            the index to move the tab to.
	 */
	public void moveTab( int vCurrentIndex , int vNewIndex )
	{
		TabInfo info = tabs.get( actualizeIndex( vCurrentIndex ) );
		
		handler.disable = true;
		try
		{
			int index = tabbedPane.indexOfComponent( info.tab.getContent( ) );
			if( index >= 0 )
			{
				tabbedPane.removeTabAt( index );
				tabbedPane.insertTab( info.tab.getTitle( ) , info.tab.getIcon( ) , info.tab.getContent( ) , info.tab.getToolTipText( ) , actualizeIndex( vNewIndex ) );
			}
		}
		finally
		{
			handler.disable = false;
		}
		moveTabInternal( info.tab , vNewIndex );
	}
	
	/**
	 * Removes a tab from this tabbed pane with animation. The layout will animate the tab renderer shrinking before it is actually removed from the component
	 * hierarchy. However, the tab will be immediately removed from the list of tabs in this tabbed pane, so {@link #getTabs()}, {@link #addTabInternal(Tab)},
	 * {@link #moveTabInternal(Tab, int)}, etc. will all behave as if it is no longer a member of this tabbed pane. If you want to remove the tab and its
	 * renderer component immediately, use {@link #removeTabImmediatelyInternal(Tab)}.
	 * 
	 * If the selected tab is removed, one of the adjacent tabs will be selected before it is removed. If it is the only tab, the selection will be cleared
	 * before it is removed.
	 * 
	 * {@code tab} the tab to remove. If {@code tab} is not a member of this tabbed pane, this method has no effect.
	 */
	private void removeTabInternal( Tab tab )
	{
		removeTabInternal( tab , true );
	}
	
	private void removeTabInternal( Tab tab , boolean startTimer )
	{
		checkEDT( );
		
		int removedIndex = getInfoIndex( tab );
		
		if( removedIndex >= 0 )
		{
			TabInfo info = tabs.get( removedIndex );
			if( info.isBeingRemoved )
			{
				return;
			}
			
			TabRemovedEvent event = new TabRemovedEvent( tabbedPane , System.currentTimeMillis( ) , tab , virtualizeIndex( removedIndex ) );
			
			if( info == selectedTab )
			{
				if( tabs.size( ) == 1 )
				{
					setSelectedTabInternal( ( TabInfo ) null );
				}
				else
				{
					int index = tabs.indexOf( info );
					
					// select the closest tab that is not being removed
					TabInfo newSelectedTab = null;
					
					for( int i = index + 1 ; i < tabs.size( ) ; i++ )
					{
						TabInfo adjTab = tabs.get( i );
						if( !adjTab.isBeingRemoved )
						{
							newSelectedTab = adjTab;
							break;
						}
					}
					if( newSelectedTab == null )
					{
						for( int i = index - 1 ; i >= 0 ; i-- )
						{
							TabInfo adjTab = tabs.get( i );
							if( !adjTab.isBeingRemoved )
							{
								newSelectedTab = adjTab;
								break;
							}
						}
					}
					
					setSelectedTabInternal( newSelectedTab );
				}
			}
			info.isBeingDragged = false;
			info.isBeingRemoved = true;
			tabbedPane.invalidate( );
			tabbedPane.validate( );
			
			notifyTabbedPaneListeners( event );
		}
	}
	
	/**
	 * Removes a tab from this tabbed pane without animation. This means the tab renderer will be removed from the component hierarchy immediately, unlike
	 * {@link #removeTabInternal(Tab)}.
	 * 
	 * If the selected tab is removed, one of the adjacent tabs will be selected before it is removed. If it is the only tab, the selection will be cleared
	 * before it is removed.
	 * 
	 * @param tab
	 *            the tab to remove. If {@code tab} is not a member of this tabbed pane, this method has no effect.
	 */
	private void removeTabImmediatelyInternal( Tab tab )
	{
		removeTabInternal( tab , false );
		actuallyRemoveTab( tab );
	}
	
	private void actuallyRemoveTab( Tab tab )
	{
		checkEDT( );
		
		TabInfo info = getInfo( tab );
		if( info != null )
		{
			// if( tab.getCloseButton( ) != null && info.closeButtonHandler != null )
			// {
			// tab.getCloseButton( ).removeActionListener( info.closeButtonHandler );
			// }
			tabLayeredPane.remove( tab );
			tabs.remove( info );
			contentMap.remove( info.tab.getContent( ) );
		}
	}
	
	/**
	 * Removes all tabs from this tabbed pane without animation. This method is equivalent to setting the selected tab to {@code null} and then removing all
	 * tabs one by one.
	 */
	private void removeAllTabsInternal( )
	{
		checkEDT( );
		
		long time = System.currentTimeMillis( );
		
		setSelectedTabInternal( ( TabInfo ) null );
		
		List<Tab> removedTabs = new ArrayList<Tab>( );
		
		while( !tabs.isEmpty( ) )
		{
			TabInfo info = tabs.get( 0 );
			removedTabs.add( info.tab );
			removeTabImmediatelyInternal( info.tab );
		}
		
		TabsClearedEvent event = new TabsClearedEvent( tabbedPane , time , Collections.unmodifiableList( removedTabs ) );
		
		notifyTabbedPaneListeners( event );
	}
	
	/**
	 * @return the selected tab, or {@code null} if no tab is selected.
	 */
	public Tab getSelectedTab( )
	{
		return selectedTab != null ? selectedTab.tab : null;
	}
	
	public void setSelectedTab( Tab tab )
	{
		int index = getInfoIndex( tab );
		if( index >= 0 )
		{
			index = virtualizeIndex( index );
		}
		setSelectedIndexInternal( index );
	}
	
	private void setSelectedIndexInternal( int vIndex )
	{
		if( vIndex < 0 )
		{
			setSelectedTabInternal( ( TabInfo ) null );
		}
		else
		{
			setSelectedTabInternal( tabs.get( actualizeIndex( vIndex ) ) );
		}
	}
	
	private void setSelectedTabInternal( TabInfo info )
	{
		checkEDT( );
		
		if( selectedTab != info )
		{
			int prevIndex = selectedTab != null ? virtualizeIndex( tabs.indexOf( selectedTab ) ) : -1;
			int newIndex = info != null ? virtualizeIndex( tabs.indexOf( info ) ) : -1;
			
			Tab prevTab = selectedTab != null ? selectedTab.tab : null;
			Tab newTab = info != null ? info.tab : null;
			
			TabSelectedEvent event = new TabSelectedEvent( tabbedPane , System.currentTimeMillis( ) , prevTab , prevIndex , newTab , newIndex );
			
			if( selectedTab != null )
			{
				selectedTab.tab.setSelected( false );
			}
			
			selectedTab = info;
			
			if( selectedTab != null )
			{
				selectedTab.tab.setSelected( true );
				setContentInternal( selectedTab.tab.getContent( ) );
			}
			else
			{
				setContentInternal( null );
			}
			
			tabbedPane.invalidate( );
			tabbedPane.validate( );
			
			notifyTabbedPaneListeners( event );
		}
	}
	
	private void setContentInternal( Component newContent )
	{
		checkEDT( );
		
		if( currentContent != null && currentContent != newContent && currentContent.getParent( ) == tabbedPane && currentContent.isVisible( ) )
		{
			
			currentContent.setVisible( false );
		}
		if( newContent != null && !newContent.isVisible( ) )
		{
			newContent.setVisible( true );
		}
		currentContent = newContent;
	}
	
	/**
	 * Notifies this {@code TabbedPane} that a tab's content has changed. If that tab is selected, its old content will be removed from this {@code TabbedPane}
	 * and replaced with the tab's new content.
	 * 
	 * @param tab
	 *            the tab whose content has changed. If {@code tab} is not a member of this tabbed pane, this method has no effect.
	 */
	public void tabContentChanged( Tab tab )
	{
		checkEDT( );
		
		TabInfo info = getInfo( tab );
		if( info == selectedTab )
		{
			setContentInternal( tab.getContent( ) );
		}
	}
	
	/**
	 * Immediately updates the tabs' bounds to their eventual position where animation is complete.
	 */
	public void finishAnimation( )
	{
		checkEDT( );
		
		layout.finishAnimation = true;
		tabbedPane.invalidate( );
		tabbedPane.validate( );
	}
	
	private void setDragState( Tab draggedTab , double grabX , int dragX )
	{
		checkEDT( );
		
		boolean validate = false;
		
		for( TabInfo info : tabs )
		{
			if( info.tab == draggedTab )
			{
				if( !info.isBeingDragged || info.grabX != grabX || info.dragX != dragX )
				{
					info.isBeingDragged = true;
					info.grabX = grabX;
					info.dragX = dragX;
					
					validate = true;
				}
			}
			else
			{
				if( info.isBeingDragged )
				{
					info.isBeingDragged = false;
					validate = true;
				}
			}
		}
		
		if( validate )
		{
			tabbedPane.invalidate( );
			tabbedPane.validate( );
		}
	}
	
	/**
	 * @return the new tab button. This tabbed pane manages its appearance and action by default. You are free to modify it however you wish, but behavior is
	 *         undefined in this case so be careful.
	 */
	public JButton getNewTabButton( )
	{
		return newTabButton;
	}
	
	/**
	 * Removes all tabs, stops animation and attempts to unregister all listeners that could prevent this TabbedPane from being finalized. However, I am not yet
	 * certain if this method eliminates all potential memory leaks (excluding references apart from internal TabbedPane code.
	 */
	public void dispose( )
	{
		checkEDT( );
		
		animTimer.stop( );
		
		tabbedPane.remove( tabLayeredPane );
		mouseOverManager.uninstall( tabbedPane );
		mouseOverManager.removeExcludedComponent( tabbedPane );
		
		tabbedPane.removeContainerListener( handler );
		tabbedPane.getModel( ).removeChangeListener( handler );
		tabbedPane.removePropertyChangeListener( handler );
		
		try
		{
			removeAllTabsInternal( );
		}
		catch( Exception e )
		{
			e.printStackTrace( );
		}
		
		dragHandler.dispose( );
		tabbedPane.setLayout( null );
		tabbedPane.setDropTarget( null );
		
		dragHandler = null;
		dropHandler = null;
		handler = null;
		mouseOverManager = null;
		layout = null;
		animTimer = null;
		tabLayeredPane = null;
		tabbedPane = null;
	}
	
	private int animate( int value , int target , double animFactor )
	{
		int d = value - target;
		d *= animFactor;
		return d == 0 ? target : target + d;
	}
	
	private int animate( int value , int target , double animFactor , BooleanHolder animNeeded )
	{
		int newValue = animate( value , target , animFactor );
		if( newValue != value )
		{
			animNeeded.value = true;
		}
		return newValue;
	}
	
	private int animateShrinkingOnly( int value , int target , double animFactor , BooleanHolder animNeeded )
	{
		int newValue = value < target ? target : animate( value , target , animFactor );
		if( newValue != value )
		{
			animNeeded.value = true;
		}
		return newValue;
	}
	
	private class TabLayoutManager implements LayoutManager
	{
		/**
		 * The sustained total width of the tab zone in virtual coordinate space. This does not include the overlap area of the last tab.
		 */
		private int		vSustainedTabZoneWidth	= 0;
		
		/**
		 * The current width scale.
		 */
		private double	widthScale				= 1.0;
		
		private boolean	finishAnimation			= false;
		
		private int getInsertIndex( Tab tab , double grabX , int dragX )
		{
			checkEDT( );
			
			int targetWidth = useUniformWidth ? maxUniformWidth : tab.getPreferredSize( ).width;
			targetWidth *= widthScale;
			int vX = dragX - ( int ) ( grabX * targetWidth );
			vX = ( int ) ( ( vX - tabZone.x ) / widthScale );
			
			int vIndex = 0;
			
			int vTargetX = 0;
			
			for( int index = 0 ; index < tabs.size( ) ; index++ )
			{
				TabInfo info = tabs.get( index );
				
				if( info.tab == tab || info.isBeingRemoved )
				{
					continue;
				}
				
				if( vX < vTargetX + info.vTargetWidth / 2 )
				{
					break;
				}
				
				vTargetX += info.vTargetWidth;
				vIndex++ ;
			}
			
			return vIndex;
		}
		
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
			checkEDT( );
			
			boolean reset = finishAnimation;
			finishAnimation = false;
			
			double animFactor = reset ? 0.0 : JhromeTabbedPaneUI.this.animFactor;
			
			int parentWidth = parent.getWidth( );
			int parentHeight = parent.getHeight( );
			
			Insets insets = parent.getInsets( );
			
			Dimension rightButtonsPanelPrefSize = rightButtonsPanel.getPreferredSize( );
			
			int availWidth = parentWidth - insets.left - insets.right;
			int availTopZoneWidth = availWidth - tabMargin * 2;
			int availTabZoneWidth = availTopZoneWidth - rightButtonsPanelPrefSize.width;
			int availHeight = parentHeight - insets.top - insets.bottom;
			
			/**
			 * Whether another animation step is needed after this one.
			 */
			BooleanHolder animNeeded = new BooleanHolder( false );
			
			/**
			 * The target x position of the next tab, in virtual coordinate space.
			 */
			int vTargetX = 0;
			
			/**
			 * The target width of the tab zone, which is the total target width of all tabs, except those being removed, in virtual coordinate space. This does
			 * not include the overlap area of the last tab.
			 */
			int vTargetTabZoneWidth = 0;
			
			/**
			 * The current width of the tab zone, which is the total current width of all tabs, including those being removed, in virtual coordinate space. This
			 * does not include the overlap area of the last tab.
			 */
			int vCurrentTabZoneWidth = 0;
			
			int targetTabHeight = rightButtonsPanelPrefSize.height;
			
			boolean anyDragging = false;
			
			for( int i = 0 ; i < tabs.size( ) ; i++ )
			{
				TabInfo info = tabs.get( i );
				info.vTargetX = vTargetX;
				
				info.prefSize = info.tab.getPreferredSize( );
				
				info.vTargetWidth = info.isBeingRemoved ? 0 : Math.max( 0 , useUniformWidth ? maxUniformWidth - overlap : info.prefSize.width - overlap );
				
				// animate the tab x position
				info.vCurrentX = animate( info.vCurrentX , vTargetX , animFactor , animNeeded );
				
				// animate the tab width
				info.vCurrentWidth = animate( info.vCurrentWidth , info.vTargetWidth , animFactor , animNeeded );
				
				if( info.isBeingDragged )
				{
					anyDragging = true;
				}
				
				if( !info.isBeingRemoved )
				{
					vTargetX += info.vTargetWidth;
					vTargetTabZoneWidth += info.vTargetWidth;
					targetTabHeight = Math.max( targetTabHeight , info.prefSize.height );
				}
				vCurrentTabZoneWidth += info.vCurrentWidth;
			}
			
			TabInfo lastInfo = tabs.isEmpty( ) ? null : tabs.get( tabs.size( ) - 1 );
			/**
			 * The target x position for the right buttons panel, in virtual coordinate space. The logic for this is tricky.
			 */
			int vTargetRightButtonsPanelX = lastInfo != null ? lastInfo.vCurrentX == lastInfo.vTargetX ? lastInfo.vCurrentX + lastInfo.vCurrentWidth : lastInfo.vTargetX + lastInfo.vCurrentWidth : 0;
			
			// Animate the tab zone (virtual) width.
			// if the sustained tab zone width must increase to reach the current, do it immediately, without animation; if it must shrink to reach the target,
			// do it with animation,
			// but only if the mouse is not over the top zone.
			if( reset || vCurrentTabZoneWidth > vSustainedTabZoneWidth )
			{
				vSustainedTabZoneWidth = vCurrentTabZoneWidth;
			}
			else if( !mouseOverTopZone && !anyDragging && vSustainedTabZoneWidth > vTargetTabZoneWidth )
			{
				animNeeded.value = true;
				vSustainedTabZoneWidth = animate( vSustainedTabZoneWidth , vTargetTabZoneWidth , animFactor );
			}
			
			// Compute necessary width scale to fit all tabs on screen
			widthScale = vSustainedTabZoneWidth > availTabZoneWidth - overlap ? ( availTabZoneWidth - overlap ) / ( double ) vSustainedTabZoneWidth : 1.0;
			
			// Adjust width scale as necessary so that no tab (except those being removed) is narrower than its minimum width
			double adjWidthScale = widthScale;
			for( int i = 0 ; i < tabs.size( ) ; i++ )
			{
				TabInfo info = tabs.get( i );
				if( info.isBeingRemoved )
				{
					continue;
				}
				Dimension minSize = info.tab.getMinimumSize( );
				if( minSize != null && info.vCurrentWidth >= minSize.width )
				{
					int targetWidth = useUniformWidth ? maxUniformWidth : info.prefSize.width;
					adjWidthScale = Math.max( adjWidthScale , minSize.width / ( double ) targetWidth );
				}
			}
			widthScale = adjWidthScale;
			
			int currentTabHeight = targetTabHeight;
			if( tabZone.height > 0 )
			{
				currentTabHeight = animate( tabZone.height , targetTabHeight , animFactor , animNeeded );
			}
			topZone.setFrame( insets.left + tabMargin , insets.top , availTopZoneWidth , currentTabHeight );
			tabZone.setFrame( insets.left + tabMargin , insets.top , availTabZoneWidth , topZone.height );
			dropZone.setFrame( insets.left + tabMargin , insets.top , availTopZoneWidth , Math.min( availHeight , tabZone.height + extraDropZoneSpace ) );
			
			tabLayeredPane.setBounds( 0 , 0 , tabbedPane.getWidth( ) , tabZone.y + tabZone.height );
			
			// now, lay out the tabs
			for( int i = 0 ; i < tabs.size( ) ; i++ )
			{
				TabInfo info = tabs.get( i );
				
				int x = topZone.x + ( int ) ( info.vCurrentX * widthScale );
				int targetX = topZone.x + ( int ) ( info.vTargetX * widthScale );
				int width = ( int ) ( info.vCurrentWidth * widthScale ) + overlap;
				int targetWidth = ( int ) ( info.vTargetWidth * widthScale ) + overlap;
				
				if( info.isBeingDragged )
				{
					x = info.dragX - ( int ) ( info.grabX * width );
					// if( i == tabs.size( ) - 1 && x > targetX )
					// {
					// x = x / 2 + targetX / 2;
					// }
					x = Math.max( topZone.x , Math.min( topZone.x + topZone.width - width , x ) );
					info.vCurrentX = ( int ) ( ( x - topZone.x ) / widthScale );
				}
				info.tab.setBounds( x , topZone.y , width , tabZone.height );
				info.targetBounds.setFrame( targetX , topZone.y , targetWidth , tabZone.height );
			}
			
			// lay out the content panel and right button panel
			contentPanelBounds.setBounds( insets.left , tabZone.y + tabZone.height - contentPanelOverlap , availWidth , availHeight - tabZone.height + contentPanelOverlap );
			Insets contentInsets = contentPanelBorder.getBorderInsets( tabbedPane );
			
			int contentX = contentPanelBounds.x + contentInsets.left;
			int contentW = contentPanelBounds.width - contentInsets.left - contentInsets.right;
			int contentY = contentPanelBounds.y + contentInsets.top;
			int contentH = contentPanelBounds.height - contentInsets.top - contentInsets.bottom;
			
			for( Component comp : tabbedPane.getComponents( ) )
			{
				if( comp != tabLayeredPane )
				{
					comp.setBounds( contentX , contentY , contentW , contentH );
				}
			}
			
			// animate the right buttons panel x position. If it must increase to reach the target, do it immediately, without animation;
			// If it must decrease, do it with animation.
			int vCurrentRightButtonsPanelX = ( int ) ( ( rightButtonsPanel.getX( ) - overlap / 2 - tabZone.x ) / widthScale );
			vCurrentRightButtonsPanelX = animateShrinkingOnly( vCurrentRightButtonsPanelX , vTargetRightButtonsPanelX , animFactor , animNeeded );
			int rightButtonsPanelX = tabZone.x + ( int ) ( vCurrentRightButtonsPanelX * widthScale ) + overlap / 2;
			
			// keep right buttons panel from getting pushed off the edge of the tabbed pane when minimum tab width is reached
			rightButtonsPanelX = Math.min( rightButtonsPanelX , topZone.x + topZone.width - rightButtonsPanelPrefSize.width );
			rightButtonsPanel.setBounds( rightButtonsPanelX , topZone.y , rightButtonsPanelPrefSize.width , topZone.height );
			
			for( int i = tabs.size( ) - 1 ; i >= 0 ; i-- )
			{
				TabInfo info = tabs.get( i );
				if( info.isBeingRemoved && info.vCurrentWidth == 0 )
				{
					actuallyRemoveTab( info.tab );
				}
			}
			
			int layer = JLayeredPane.DEFAULT_LAYER;
			
			tabLayeredPane.setComponentZOrder( rightButtonsPanel , layer++ );
			
			if( selectedTab != null )
			{
				try
				{
					tabLayeredPane.setComponentZOrder( selectedTab.tab , layer++ );
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
				}
			}
			
			// setComponentZOrder( contentPanel , layer++ );
			
			for( TabInfo info : tabs )
			{
				if( info == selectedTab )
				{
					continue;
				}
				tabLayeredPane.setComponentZOrder( info.tab , layer++ );
			}
			
			tabLayeredPane.repaint( );
			
			if( animNeeded.value )
			{
				animTimer.start( );
			}
			else
			{
				animTimer.stop( );
			}
		}
	}
	
	private static class DummyTransferable implements Transferable
	{
		@Override
		public DataFlavor[ ] getTransferDataFlavors( )
		{
			return new DataFlavor[ 0 ];
		}
		
		@Override
		public boolean isDataFlavorSupported( DataFlavor flavor )
		{
			return false;
		}
		
		@Override
		public Object getTransferData( DataFlavor flavor ) throws UnsupportedFlavorException , IOException
		{
			throw new UnsupportedFlavorException( flavor );
		}
	}
	
	private class DragHandler implements DragSourceListener , DragSourceMotionListener , DragGestureListener
	{
		DragSource				source;
		
		DragGestureRecognizer	dragGestureRecognizer;
		
		Point					dragOrigin;
		
		public DragHandler( Component comp , int actions )
		{
			source = new DragSource( );
			dragGestureRecognizer = source.createDefaultDragGestureRecognizer( comp , actions , this );
			source.addDragSourceMotionListener( this );
		}
		
		public void dispose( )
		{
			source.removeDragSourceListener( this );
			source.removeDragSourceMotionListener( this );
			dragGestureRecognizer.removeDragGestureListener( this );
			dragGestureRecognizer.setComponent( null );
		}
		
		@Override
		public void dragGestureRecognized( DragGestureEvent dge )
		{
			dragOrigin = dge.getDragOrigin( );
			
			draggedTab = getDraggableTabAt( dragOrigin );
			if( draggedTab != null )
			{
				dragFloatingTabHandler = floatingTabHandler;
				
				Window window = SwingUtilities.getWindowAncestor( tabbedPane );
				if( window != null )
				{
					dragSourceWindowSize = window.getSize( );
				}
				Point p = SwingUtilities.convertPoint( tabbedPane , dragOrigin , draggedTab );
				grabX = p.x / ( double ) draggedTab.getWidth( );
				Transferable t = new DummyTransferable( );
				source.startDrag( dge , Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) , t , this );
			}
		}
		
		@Override
		public void dragEnter( DragSourceDragEvent dsde )
		{
		}
		
		@Override
		public void dragExit( DragSourceEvent dse )
		{
		}
		
		@Override
		public void dragOver( DragSourceDragEvent dsde )
		{
		}
		
		@Override
		public void dragMouseMoved( DragSourceDragEvent dsde )
		{
			if( draggedTab != null )
			{
				JhromeTabbedPaneUI draggedParent = getTabbedPaneAncestorUI( draggedTab );
				if( draggedParent != null )
				{
					Point p = dsde.getLocation( );
					SwingUtilities.convertPointFromScreen( p , draggedParent.tabbedPane );
					if( !Utils.contains( draggedParent.dropZone , p ) )
					{
						dragOut( dsde.getDragSourceContext( ).getComponent( ) );
					}
				}
				
				if( dragFloatingTabHandler != null )
				{
					dragFloatingTabHandler.onFloatingTabDragged( dsde , draggedTab , grabX );
				}
			}
		}
		
		@Override
		public void dropActionChanged( DragSourceDragEvent dsde )
		{
		}
		
		@Override
		public void dragDropEnd( final DragSourceDropEvent dsde )
		{
			if( dragFloatingTabHandler != null )
			{
				dragFloatingTabHandler.onFloatingEnd( );
			}
			dragFloatingTabHandler = null;
			
			JhromeTabbedPaneUI draggedParent = getTabbedPaneAncestorUI( draggedTab );
			
			if( draggedTab != null && draggedParent == null && tabDropFailureHandler != null )
			{
				tabDropFailureHandler.onDropFailure( dsde , draggedTab , dragSourceWindowSize );
			}
			
			if( draggedParent != null )
			{
				draggedParent.setDragState( null , 0 , 0 );
			}
			
			draggedTab = null;
			draggedParent = null;
			
			for( final Window window : deadWindows )
			{
				SwingUtilities.invokeLater( new Runnable( )
				{
					@Override
					public void run( )
					{
						window.dispose( );
					}
				} );
			}
			
			deadWindows.clear( );
			
		}
	}
	
	private class DropHandler implements DropTargetListener
	{
		DropTarget	target;
		
		public DropHandler( Component comp )
		{
			target = new DropTarget( comp , this );
		}
		
		private void handleDrag( DropTargetDragEvent dtde )
		{
		}
		
		@Override
		public void dragEnter( DropTargetDragEvent dtde )
		{
			handleDrag( dtde );
		}
		
		@Override
		public void dragOver( DropTargetDragEvent dtde )
		{
			handleDrag( dtde );
			JhromeTabbedPaneUI.dragOver( dtde );
		}
		
		@Override
		public void dragExit( DropTargetEvent dte )
		{
			JhromeTabbedPaneUI.dragOut( dte.getDropTargetContext( ).getComponent( ) );
		}
		
		@Override
		public void dropActionChanged( DropTargetDragEvent dtde )
		{
			handleDrag( dtde );
		}
		
		@Override
		public void drop( DropTargetDropEvent dtde )
		{
			setDragState( null , 0 , 0 );
			
			if( draggedTab != null && Utils.contains( dropZone , dtde.getLocation( ) ) && isSnapInAllowed( draggedTab ) )
			{
				dtde.acceptDrop( dtde.getDropAction( ) );
			}
			else
			{
				dtde.rejectDrop( );
			}
			dtde.dropComplete( true );
		}
	}
	
	private static IFloatingTabHandler	dragFloatingTabHandler	= null;
	
	private static double				grabX					= 0;
	
	private static Tab					draggedTab				= null;
	
	private static List<Window>			deadWindows				= new ArrayList<Window>( );
	
	private static Dimension			dragSourceWindowSize	= null;
	
	private boolean isTearAwayAllowed( Tab tab )
	{
		return dndPolicy == null || dndPolicy.isTearAwayAllowed( tabbedPane , tab );
	}
	
	private boolean isSnapInAllowed( Tab tab )
	{
		return dndPolicy == null || dndPolicy.isSnapInAllowed( tabbedPane , tab );
	}
	
	private static void dragOut( Component dragOutComponent )
	{
		if( draggedTab != null )
		{
			JhromeTabbedPaneUI draggedParent = getTabbedPaneAncestorUI( draggedTab );
			if( draggedParent != null && dragOutComponent == draggedParent.tabbedPane && draggedParent.isTearAwayAllowed( draggedTab ) )
			{
				if( dragFloatingTabHandler != null )
				{
					dragFloatingTabHandler.onFloatingBegin( draggedTab );
				}
				removeDraggedTabFromParent( );
				// draggedParent.mouseOverTopZone = false;
			}
		}
	}
	
	private static void removeDraggedTabFromParent( )
	{
		JhromeTabbedPaneUI draggedParent = getTabbedPaneAncestorUI( draggedTab );
		draggedParent.setDragState( null , 0 , 0 );
		Component draggedTabComponent = draggedTab.getTabComponent( );
		draggedParent.removeTabImmediatelyInternal( draggedTab );
		int tabIndex = draggedParent.tabbedPane.indexOfComponent( draggedTab.getContent( ) );
		if( tabIndex < 0 )
		{
			return;
		}
		draggedParent.tabbedPane.removeTabAt( tabIndex );
		draggedTab.setTabComponent( draggedTabComponent );
		if( draggedParent.getTabCount( ) == 0 )
		{
			Window window = SwingUtilities.getWindowAncestor( draggedParent.tabbedPane );
			window.setVisible( false );
			deadWindows.add( window );
		}
		draggedParent = null;
	}
	
	private static void dragOver( DropTargetDragEvent dtde )
	{
		if( draggedTab != null )
		{
			JhromeTabbedPaneUI tabbedPaneUI = getTabbedPaneAncestorUI( dtde.getDropTargetContext( ).getComponent( ) );
			
			Point p = dtde.getLocation( );
			if( !Utils.contains( tabbedPaneUI.dropZone , p ) )
			{
				dragOut( dtde.getDropTargetContext( ).getComponent( ) );
				return;
			}
			
			JhromeTabbedPaneUI draggedParent = getTabbedPaneAncestorUI( draggedTab );
			
			if( draggedParent != tabbedPaneUI && ( draggedParent == null || draggedParent.isTearAwayAllowed( draggedTab ) ) && tabbedPaneUI.isSnapInAllowed( draggedTab ) )
			{
				if( dragFloatingTabHandler != null )
				{
					dragFloatingTabHandler.onFloatingEnd( );
				}
				
				if( draggedParent != null )
				{
					removeDraggedTabFromParent( );
				}
				
				draggedParent = tabbedPaneUI;
				
				// tabbedPane.mouseOverTopZone = true;
				
				Window ancestor = SwingUtilities.getWindowAncestor( tabbedPaneUI.tabbedPane );
				if( ancestor != null )
				{
					ancestor.toFront( );
					ancestor.requestFocus( );
				}
				
				int dragX = dtde.getLocation( ).x;
				
				int newIndex = tabbedPaneUI.layout.getInsertIndex( draggedTab , grabX , dragX );
				tabbedPaneUI.addTab( newIndex , draggedTab , false );
				tabbedPaneUI.tabbedPane.setSelectedIndex( newIndex );
				
				tabbedPaneUI.setDragState( draggedTab , grabX , dragX );
			}
			else
			{
				TabInfo info = tabbedPaneUI.getInfo( draggedTab );
				if( info != null )
				{
					int dragX = dtde.getLocation( ).x;
					tabbedPaneUI.setDragState( draggedTab , grabX , dragX );
					
					int newIndex = tabbedPaneUI.layout.getInsertIndex( draggedTab , grabX , dragX );
					int currentIndex = tabbedPaneUI.getInfoIndex( draggedTab );
					tabbedPaneUI.moveTab( currentIndex , newIndex );
					tabbedPaneUI.tabbedPane.setSelectedComponent( draggedTab.getContent( ) );
				}
			}
		}
	}
	
	public Image createDragImage( Tab tab )
	{
		Component rend = tab;
		
		int width = rend.getWidth( );
		int height = rend.getHeight( );
		
		width = Math.max( width , contentPanelBounds.width );
		height += contentPanelBounds.height - contentPanelOverlap;
		
		if( width == 0 || height == 0 )
		{
			return null;
		}
		
		BufferedImage image = new BufferedImage( width , height , BufferedImage.TYPE_INT_ARGB );
		
		Graphics2D g2 = ( Graphics2D ) image.getGraphics( );
		
		AffineTransform origXform = g2.getTransform( );
		
		g2.translate( 0 , rend.getHeight( ) - contentPanelOverlap );
		contentPanelBorder.paintBorder( tabbedPane , g2 , 0 , 0 , contentPanelBounds.width , contentPanelBounds.height );
		if( tab.getContent( ) != null )
		{
			Insets contentInsets = contentPanelBorder.getBorderInsets( tabbedPane );
			g2.translate( contentInsets.left , contentInsets.top );
			tab.getContent( ).paint( g2 );
		}
		
		g2.setTransform( origXform );
		g2.translate( tabMargin , 0 );
		rend.paint( g2 );
		
		BufferedImage rescaled = new BufferedImage( width * 3 / 4 , height * 3 / 4 , BufferedImage.TYPE_INT_ARGB );
		
		g2 = ( Graphics2D ) rescaled.getGraphics( );
		g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION , RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER , .5f ) );
		
		g2.drawImage( image , 0 , 0 , rescaled.getWidth( ) , rescaled.getHeight( ) , 0 , 0 , width , height , null );
		
		return rescaled;
	}
	
	private static void checkEDT( )
	{
		if( !SwingUtilities.isEventDispatchThread( ) )
		{
			throw new IllegalArgumentException( "Must be called on the AWT Event Dispatch Thread!" );
		}
	}
	
	public static JhromeTabbedPaneUI getTabbedPaneAncestorUI( Component c )
	{
		while( c != null )
		{
			if( c instanceof JTabbedPane )
			{
				return ( JhromeTabbedPaneUI ) ( ( JTabbedPane ) c ).getUI( );
			}
			c = c.getParent( );
		}
		return null;
	}
	
	public static JTabbedPane getJTabbedPaneAncestor( Component c )
	{
		while( c != null )
		{
			if( c instanceof JTabbedPane )
			{
				return ( JTabbedPane ) c;
			}
			c = c.getParent( );
		}
		return null;
	}
	
	private void notifyTabbedPaneListeners( TabbedPaneEvent event )
	{
		for( ITabbedPaneListener listener : tabListeners )
		{
			listener.onEvent( event );
		}
	}
	
	public void addTabbedPaneListener( ITabbedPaneListener listener )
	{
		tabListeners.add( listener );
	}
	
	public void removeTabbedPaneListener( ITabbedPaneListener listener )
	{
		tabListeners.remove( listener );
	}
	
	public void setAnimationFactor( double factor )
	{
		animFactor = factor;
	}
	
	@Override
	public void paint( Graphics g , JComponent c )
	{
		contentPanelBorder.paintBorder( c , g , contentPanelBounds.x , contentPanelBounds.y , contentPanelBounds.width , contentPanelBounds.height );
	}
	
	@Override
	public void update( Graphics g , JComponent c )
	{
		updateTabs( );
		super.update( g , c );
	}
	
	public int tabForCoordinate( JTabbedPane pane , int x , int y )
	{
		Tab tab = getHoverableTabAt( new Point( x , y ) );
		return virtualizeIndex( getInfoIndex( tab ) );
	}
	
	@Override
	public Rectangle getTabBounds( JTabbedPane pane , int vIndex )
	{
		TabInfo info = tabs.get( actualizeIndex( vIndex ) );
		return info != null ? info.tab.getBounds( ) : null;
	}
	
	@Override
	public int getTabRunCount( JTabbedPane pane )
	{
		return 1;
	}
	
	private void updateTabs( )
	{
		List<Tab> toRemove = new LinkedList<Tab>( );
		for( TabInfo info : tabs )
		{
			if( tabbedPane.indexOfComponent( info.tab.getContent( ) ) < 0 )
			{
				toRemove.add( info.tab );
			}
		}
		
		for( Tab tab : toRemove )
		{
			removeTabInternal( tab );
		}
		
		for( int i = 0 ; i < tabbedPane.getTabCount( ) ; i++ )
		{
			updateTab( i , true );
		}
	}
	
	private void updateTab( int vIndex , boolean createIfNecessary )
	{
		String title = tabbedPane.getTitleAt( vIndex );
		Icon icon = tabbedPane.getIconAt( vIndex );
		Component content = tabbedPane.getComponentAt( vIndex );
		Component tabComponent = tabbedPane.getTabComponentAt( vIndex );
		
		TabInfo info = contentMap.get( content );
		if( info != null )
		{
			info.tab.setTitle( title );
			info.tab.setIcon( icon );
			info.tab.setTabComponent( tabComponent );
			if( tabs.indexOf( info ) != vIndex )
			{
				moveTabInternal( info.tab , vIndex );
			}
		}
		else if( createIfNecessary )
		{
			Tab tab = tabFactory.createTab( title );
			tab.setIcon( icon );
			tab.setContent( content );
			addTabInternal( vIndex , tab );
		}
	}
	
	private class Handler implements PropertyChangeListener , ContainerListener , ChangeListener
	{
		boolean	disable	= false;
		
		@Override
		public void propertyChange( PropertyChangeEvent evt )
		{
			if( disable )
			{
				return;
			}
			if( "indexForTabComponent".equals( evt.getPropertyName( ) ) )
			{
				updateTabs( );
			}
			else if( "indexForTitle".equals( evt.getPropertyName( ) ) )
			{
				updateTab( ( Integer ) evt.getNewValue( ) , false );
			}
			else if( "newTabButtonVisible".equals( evt.getPropertyName( ) ) )
			{
				updateNewTabButtonVisible( );
			}
			else if( "newTabButtonUI".equals( evt.getPropertyName( ) ) )
			{
				updateNewTabButtonUI( );
			}
			else if( "tabCloseButtonsVisible".equals( evt.getPropertyName( ) ) )
			{
				tabbedPane.repaint( );
			}
			else if( "contentPanelBorder".equals( evt.getPropertyName( ) ) )
			{
				updateContentPanelBorder( );
				tabbedPane.repaint( );
			}
		}
		
		@Override
		public void stateChanged( ChangeEvent e )
		{
			if( disable )
			{
				return;
			}
			setSelectedTabInternal( contentMap.get( tabbedPane.getSelectedComponent( ) ) );
		}
		
		@Override
		public void componentAdded( ContainerEvent e )
		{
			if( disable )
			{
				return;
			}
			updateTabs( );
		}
		
		@Override
		public void componentRemoved( ContainerEvent e )
		{
			if( disable )
			{
				return;
			}
			updateTabs( );
		}
		
	}
	
	public void addTab( Tab tab )
	{
		addTab( getTabCount( ) , tab , true );
	}
}
