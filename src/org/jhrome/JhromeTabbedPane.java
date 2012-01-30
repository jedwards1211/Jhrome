
package org.jhrome;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.omg.CORBA.BooleanHolder;

import com.sun.awt.AWTUtilities;

/**
 * {@link JhromeTabbedPane} is a Google Chrome-like tabbed pane, providing animated tab layout,
 * drag and drop capabilities, and a new tab button. All Google Chrome behavior is provided by default.
 * Tabs can be dragged around within {@code JhromeTabbedPane} and rearranged, they can be "torn away" or
 * dragged out and opened in new windows, and they can be dragged from one window to another. If all the tabs
 * in a {@code JhromeTabbedPane} are closed or torn away, the window containing that {@code JhromeTabbedPane} is disposed.
 * When a tab is torn away, a ghosted drag image window showing the tab and its contents will appear and follow the mouse cursor until
 * the tab is dragged over another {@code JhromeTabbedPane} or dropped.<br />
 * <br />
 * 
 * Animation includes tabs expanding when added, contracting when removed,
 * jumping around when being reordered, contracting simultaneously when there is not enough room, and expanding simultaneously when there is room again and the
 * mouse is no longer on top of the tab zone.<br />
 * <br />
 * 
 * {@code JhromeTabbedPane} is designed to allow you to customize the look and behavior as much as possible. The following
 * interfaces help with customization:
 * <ul>
 * <li>{@link IJhromeTab} provides an interface to tab renderers/content. You can use literally any {@link Component} (or combination of {@code Component}s) in
 * a tab renderer by providing them through an {@code IJhromeTab} implementation.
 * <li>{@link IJhromeTabFactory} creates new tabs when the new tab button is clicked. By providing your own tab factory you can use any kind of tabs you like
 * with {@code JhromeTabbedPane}.</li>
 * <li>{@link IJhromeWindowFactory} creates new windows for tabs that are torn away into their own windows. By providing your own window factory you can use any
 * kind of windows you like with {@code JhromeTabbedPane}.</li>
 * <li>{@link IJhromeWindow} allows one {@code JhromeTabbedPane} to add a tab to the {@code JhromeTabbedPane} of a new window created when the tab is torn away.
 * You can lay out a {@code JhromeTabbedPane} in a Window any way you like, and {@code JhromeTabbedPane} will still be able to use it.
 * <li>{@link IJhromeTabDnDPolicy} controls whether tabs may be torn away or snapped back in. By providing your own DnD policy you can create arbitrarily
 * complex behavior, preventing only certain tabs from being torn away, certain tabs from being snapped in, only at certain times, etc.</li>
 * </ul>
 * 
 * Also, {@code JhromeTabbedPane} allows you to get its new tab button and content pane so that you can modify them arbitrarily.<br />
 * <br />
 * 
 * Since the layout is animated, when you remove a tab from {@code JhromeTabbedPane} (using {@link #removeTab(IJhromeTab)}), it makes it start contracting
 * and doesn't actually remove the tab renderer component until it is done contracting. However, all public methods that involve the tab list behave as if
 * tabs are removed immediately. For example, if you remove a tab, it will no longer show up in {@link #getTabs()}, even while the tab renderer component is
 * still a child of this {@code JhromeTabbedPane} and contracting. If you add the tab back before it is done contracting, it will jump to the new position
 * and expand back to full size.
 * 
 * @author andy.edwards
 */
@SuppressWarnings( "serial" )
public class JhromeTabbedPane extends JLayeredPane
{
	public JhromeTabbedPane( )
	{
		checkEDT( );
		init( );
	}
	
	private List<TabInfo>	tabs	= new ArrayList<TabInfo>( );
	
	private static class TabInfo
	{
		ActionListener	closeButtonHandler;
		IJhromeTab		tab;
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
		 * The relative x position at which the tab was grabbed, as a proportion of its width (0.0 = left side, 0.5 = middle, 1.0 = right side).
		 * This way if the tab width changes while it's being dragged, the layout manager can still give it a reasonable position relative to the mouse cursor.
		 */
		double			grabX;
	}
	
	private int						overlap				= 13;
	
	private double					animFactor			= 0.7;
	
	private javax.swing.Timer		animTimer;
	
	private TabLayoutManager		layout;
	
	private boolean					useUniformWidth		= true;
	private int						maxUniformWidth		= 300;
	
	private boolean					mouseOverTopZone	= true;
	
	private MouseManager			mouseOverManager;
	
	private TabInfo					selectedTab			= null;
	
	/**
	 * How many pixels the content panel overlaps the tabs. This is necessary with the
	 * Google Chrome appearance to make the selected tab and the content panel look like
	 * a contiguous object
	 */
	private int						contentPanelOverlap	= 1;
	
	private JPanel					contentPanel;
	
	private int						tabMargin			= 2;
	
	private JPanel					rightButtonsPanel;
	
	private JButton					newTabButton;
	
	private ActionListener			newTabButtonListener;
	
	private DragHandler				dragHandler;
	private DropHandler				dropHandler;
	
	private IJhromeWindowFactory	windowFactory		= new JhromeWindowFactory( );
	
	private IJhromeTabFactory		tabFactory			= new JhromeTabFactory( );
	
	private Rectangle				topZone				= new Rectangle( );
	private Rectangle				tabZone				= new Rectangle( );
	
	private int						extraDropZoneSpace	= 25;
	private Rectangle				dropZone			= new Rectangle( );
	
	private IJhromeTabDnDPolicy		dndPolicy			= null;
	
	private class MouseManager extends RecursiveListener
	{
		MouseAdapter	adapter	= new MouseAdapter( )
								{
									private void updateHoldTabScale( MouseEvent e )
									{
										Point p = e.getPoint( );
										p = SwingUtilities.convertPoint( ( Component ) e.getSource( ) , p , JhromeTabbedPane.this );
										boolean newMouseOver = JhromeUtils.contains( topZone , p );
										if( newMouseOver != mouseOverTopZone )
										{
											mouseOverTopZone = newMouseOver;
											invalidate( );
											validate( );
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
										
										Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , JhromeTabbedPane.this );
										IJhromeTab tab = getHoverableTabAt( p );
										for( TabInfo info : tabs )
										{
											info.tab.setRollover( info.tab == tab );
										}
									}
									
									@Override
									public void mousePressed( MouseEvent e )
									{
										Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , JhromeTabbedPane.this );
										IJhromeTab tab = getSelectableTabAt( p );
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
		
		contentPanel = new JPanel( );
		add( contentPanel );
		
		mouseOverManager = new MouseManager( );
		mouseOverManager.addExcludedComponent( contentPanel );
		mouseOverManager.install( this );
		
		contentPanel.setOpaque( false );
		contentPanel.setLayout( new BorderLayout( ) );
		contentPanel.setBorder( new JhromeContentPanelBorder( ) );
		contentPanel.setBackground( JhromeTabBorderAttributes.SELECTED_BORDER.bottomColor );
		
		newTabButton = new JButton( JhromeNewTabButtonUI.createNewTabButtonIcon( ) );
		
		newTabButtonListener = new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( tabFactory == null )
				{
					return;
				}
				IJhromeTab newTab = tabFactory.createTab( );
				addTab( getTabCount( ) , newTab );
				setSelectedTab( newTab );
			}
		};
		
		newTabButton.addActionListener( newTabButtonListener );
		newTabButton.setUI( new JhromeNewTabButtonUI( ) );
		
		rightButtonsPanel = new JPanel( );
		rightButtonsPanel.setLayout( new GridBagLayout( ) );
		rightButtonsPanel.setOpaque( false );
		
		GridBagConstraints gbc = new GridBagConstraints( );
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets( 1 , 2 , 0 , 0 );
		rightButtonsPanel.add( newTabButton , gbc );
		add( rightButtonsPanel );
		
		dragHandler = new DragHandler( this , DnDConstants.ACTION_MOVE );
		dropHandler = new DropHandler( this );
	}
	
	public IJhromeTabFactory getTabFactory( )
	{
		return tabFactory;
	}
	
	public void setTabFactory( JhromeTabFactory tabFactory )
	{
		this.tabFactory = tabFactory;
	}
	
	public IJhromeWindowFactory getWindowFactory( )
	{
		return windowFactory;
	}
	
	public void setWindowFactory( JhromeWindowFactory windowFactory )
	{
		this.windowFactory = windowFactory;
	}
	
	public IJhromeTabDnDPolicy getDnDPolicy( )
	{
		return dndPolicy;
	}
	
	public void setDnDPolicy( IJhromeTabDnDPolicy dndPolicy )
	{
		this.dndPolicy = dndPolicy;
	}
	
	/**
	 * @return whether to keep all tabs the same width.
	 */
	public boolean isUseUniformWidth( )
	{
		return useUniformWidth;
	}
	
	/**
	 * Sets whether to keep all tabs the same with, rather than taking their preferred size
	 * into account.
	 */
	public void setUseUniformWidth( boolean useUniformWidth )
	{
		if( this.useUniformWidth != useUniformWidth )
		{
			this.useUniformWidth = useUniformWidth;
			invalidate( );
			validate( );
		}
	}
	
	public int getMaxUniformWidth( )
	{
		return maxUniformWidth;
	}
	
	public void setMaxUniformWidth( int maxUniformWidth )
	{
		if( this.maxUniformWidth != maxUniformWidth )
		{
			this.maxUniformWidth = maxUniformWidth;
			invalidate( );
			validate( );
		}
	}
	
	public IJhromeTab getHoverableTabAt( Point p )
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
			IJhromeTab tab = info.tab;
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
	
	public IJhromeTab getDraggableTabAt( Point p )
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
			IJhromeTab tab = info.tab;
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
	
	public IJhromeTab getSelectableTabAt( Point p )
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
			IJhromeTab tab = info.tab;
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
	
	private TabInfo getInfo( IJhromeTab tab )
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
	
	private int devirtualizeIndex( int index )
	{
		checkEDT( );
		
		int virtual = 0;
		
		int devirtualized;
		for( devirtualized = 0 ; devirtualized < tabs.size( ) ; devirtualized++ )
		{
			if( virtual == index )
			{
				break;
			}
			if( !tabs.get( devirtualized ).isBeingRemoved )
			{
				virtual++ ;
			}
		}
		return devirtualized;
	}
	
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
	
	public List<IJhromeTab> getTabs( )
	{
		checkEDT( );
		
		List<IJhromeTab> result = new ArrayList<IJhromeTab>( );
		for( TabInfo info : tabs )
		{
			if( !info.isBeingRemoved )
			{
				result.add( info.tab );
			}
		}
		return result;
	}
	
	public void addTab( final IJhromeTab tab )
	{
		addTab( getTabCount( ) , tab );
	}
	
	public void addTab( int index , final IJhromeTab tab )
	{
		addTab( index , tab , true );
	}
	
	public void addTab( int index , final IJhromeTab tab , boolean expand )
	{
		checkEDT( );
		
		TabInfo info = new TabInfo( );
		info.tab = tab;
		info.prefSize = tab.getRenderer( ).getPreferredSize( );
		info.vCurrentWidth = expand ? 0 : info.prefSize.width;
		info.isBeingRemoved = false;
		if( tab.getCloseButton( ) != null )
		{
			info.closeButtonHandler = new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					removeTab( tab );
					if( getTabCount( ) == 0 )
					{
						Window window = SwingUtilities.getWindowAncestor( JhromeTabbedPane.this );
						if( window != null )
						{
							window.dispose( );
						}
					}
				}
			};
			tab.getCloseButton( ).addActionListener( info.closeButtonHandler );
		}
		
		index = devirtualizeIndex( index );
		if( index > 0 )
		{
			TabInfo prev = tabs.get( index - 1 );
			info.vCurrentX = prev.vCurrentX + prev.vCurrentWidth - overlap;
		}
		tabs.add( index , info );
		add( tab.getRenderer( ) );
		
		invalidate( );
		validate( );
	}
	
	public void tabContentChanged( IJhromeTab tab )
	{
		checkEDT( );
		
		TabInfo info = getInfo( tab );
		if( info == selectedTab )
		{
			setContent( tab.getContent( ) );
		}
	}
	
	public void removeTab( IJhromeTab tab )
	{
		removeTab( tab , true );
	}
	
	private void removeTab( IJhromeTab tab , boolean startTimer )
	{
		checkEDT( );
		
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
					
					setSelectedTab( newSelectedTab );
				}
			}
			info.isBeingDragged = false;
			info.isBeingRemoved = true;
			invalidate( );
			validate( );
		}
	}
	
	public void removeTabImmediately( IJhromeTab tab )
	{
		removeTab( tab , false );
		actuallyRemoveTab( tab );
	}
	
	private void actuallyRemoveTab( IJhromeTab tab )
	{
		checkEDT( );
		
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
	
	public void removeAllTabs( )
	{
		checkEDT( );
		
		while( !tabs.isEmpty( ) )
		{
			removeTabImmediately( tabs.get( 0 ).tab );
		}
	}
	
	public void setSelectedTab( IJhromeTab tab )
	{
		checkEDT( );
		
		if( tab == null )
		{
			setSelectedTab( ( TabInfo ) null );
		}
		else
		{
			TabInfo info = getInfo( tab );
			if( info == null || info.isBeingRemoved )
			{
				throw new IllegalArgumentException( "tab must be a child of this " + getClass( ).getName( ) );
			}
			setSelectedTab( info );
		}
	}
	
	private void setSelectedTab( TabInfo info )
	{
		checkEDT( );
		
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
				setContent( selectedTab.tab.getContent( ) );
			}
			else
			{
				setContent( null );
			}
			
			invalidate( );
			validate( );
		}
	}
	
	private void setContent( Component content )
	{
		checkEDT( );
		
		contentPanel.removeAll( );
		if( content != null )
		{
			contentPanel.add( content , BorderLayout.CENTER );
		}
		invalidate( );
		validate( );
	}
	
	private void setDragState( IJhromeTab draggedTab , double grabX , int dragX )
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
			invalidate( );
			validate( );
		}
	}
	
	public JButton getNewTabButton( )
	{
		return newTabButton;
	}
	
	public JPanel getContentPanel( )
	{
		return contentPanel;
	}
	
	public void dispose( )
	{
		checkEDT( );
		
		removeAllTabs( );
		animTimer.stop( );
		mouseOverManager.uninstall( this );
		dragHandler.dispose( );
		dropHandler.dispose( );
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
		
		private int getInsertIndex( IJhromeTab tab , double grabX , int dragX )
		{
			checkEDT( );
			
			int targetWidth = useUniformWidth ? maxUniformWidth : tab.getRenderer( ).getPreferredSize( ).width;
			targetWidth *= widthScale;
			int vX = dragX - ( int ) ( grabX * targetWidth );
			vX = ( int ) ( ( vX - tabZone.x ) / widthScale );
			
			int index = 0;
			
			int vTargetX = 0;
			
			for( int virtualIndex = 0 ; virtualIndex < tabs.size( ) ; virtualIndex++ )
			{
				TabInfo info = tabs.get( virtualIndex );
				
				if( info.tab == tab || info.isBeingRemoved )
				{
					continue;
				}
				
				if( vX < vTargetX + info.vTargetWidth / 2 )
				{
					break;
				}
				
				vTargetX += info.vTargetWidth;
				index++ ;
			}
			
			return index;
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
			
			boolean reset = false;
			
			double animFactor = reset ? 0.0 : JhromeTabbedPane.this.animFactor;
			
			int parentWidth = getWidth( );
			int parentHeight = getHeight( );
			
			Insets insets = getInsets( );
			
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
			 * The target width of the tab zone, which is the total target width of all tabs, except those being removed, in virtual coordinate space.
			 * This does not include the overlap area of the last tab.
			 */
			int vTargetTabZoneWidth = 0;
			
			/**
			 * The current width of the tab zone, which is the total current width of all tabs, including those being removed, in virtual coordinate space.
			 * This does not include the overlap area of the last tab.
			 */
			int vCurrentTabZoneWidth = 0;
			
			int targetTabHeight = rightButtonsPanelPrefSize.height;
			
			boolean anyDragging = false;
			
			for( int i = 0 ; i < tabs.size( ) ; i++ )
			{
				TabInfo info = tabs.get( i );
				info.vTargetX = vTargetX;
				
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
			// if the sustained tab zone width must increase to reach the current, do it immediately, without animation; if it must shrink to reach the target, do it with animation,
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
				Dimension minSize = info.tab.getRenderer( ).getMinimumSize( );
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
					x = Math.max( topZone.x , Math.min( topZone.x + topZone.width - width , x ) );
					info.vCurrentX = ( int ) ( ( x - topZone.x ) / widthScale );
				}
				info.tab.getRenderer( ).setBounds( x , topZone.y , width , tabZone.height );
				info.targetBounds.setFrame( targetX , topZone.y , targetWidth , tabZone.height );
			}
			
			// lay out the content panel and right button panel
			contentPanel.setBounds( insets.left , tabZone.y + tabZone.height - contentPanelOverlap , availWidth , availHeight - tabZone.height + contentPanelOverlap );
			
			// animate the right buttons panel x position.  If it must increase to reach the target, do it immediately, without animation; 
			// If it must decrease, do it with animation.
			int vCurrentRightButtonsPanelX = ( int ) ( ( rightButtonsPanel.getX( ) - overlap / 2 - tabZone.x ) / widthScale );
			vCurrentRightButtonsPanelX = animateShrinkingOnly( vCurrentRightButtonsPanelX , vTargetRightButtonsPanelX , animFactor , animNeeded );
			int rightButtonsPanelX = tabZone.x + ( int ) ( vCurrentRightButtonsPanelX * widthScale ) + overlap / 2;
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
			
			setComponentZOrder( rightButtonsPanel , layer++ );
			
			if( selectedTab != null )
			{
				try
				{
					setComponentZOrder( selectedTab.tab.getRenderer( ) , layer++ );
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
				}
			}
			
			setComponentZOrder( contentPanel , layer++ );
			
			for( TabInfo info : tabs )
			{
				if( info == selectedTab )
				{
					continue;
				}
				setComponentZOrder( info.tab.getRenderer( ) , layer++ );
			}
			
			repaint( );
			
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
	
	private static class JhromeTransferable implements Transferable
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
	
	private class DragHandler implements DragSourceListener,DragSourceMotionListener,DragGestureListener
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
				dragImage = createDragImage( draggedTab );
				Window window = SwingUtilities.getWindowAncestor( JhromeTabbedPane.this );
				if( window != null )
				{
					dragSourceWindowSize = window.getSize( );
				}
				Point p = SwingUtilities.convertPoint( JhromeTabbedPane.this , dragOrigin , draggedTab.getRenderer( ) );
				grabX = p.x / ( double ) draggedTab.getRenderer( ).getWidth( );
				Transferable t = new JhromeTransferable( );
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
				JhromeTabbedPane draggedParent = getJhromeTabbedPaneAncestor( draggedTab.getRenderer( ) );
				if( draggedParent != null )
				{
					Point p = dsde.getLocation( );
					SwingUtilities.convertPointFromScreen( p , draggedParent );
					if( !JhromeUtils.contains( draggedParent.dropZone , p ) )
					{
						dragOut( dsde );
					}
				}
				
				moveDragImageWindow( new Point( dsde.getX( ) - ( int ) ( grabX * draggedTab.getRenderer( ).getWidth( ) ) , dsde.getY( ) + 10 ) );
			}
		}
		
		@Override
		public void dropActionChanged( DragSourceDragEvent dsde )
		{
		}
		
		@Override
		public void dragDropEnd( final DragSourceDropEvent dsde )
		{
			disposeDragImageWindow( );
			dragImage = null;
			
			JhromeTabbedPane draggedParent = getJhromeTabbedPaneAncestor( draggedTab.getRenderer( ) );
			
			if( draggedTab != null && windowFactory != null && draggedParent == null )
			{
				IJhromeWindow newJhromeWindow = windowFactory.createWindow( );
				Window newWindow = newJhromeWindow.getWindow( );
				JhromeTabbedPane tabbedPane = newJhromeWindow.getTabbedPane( );
				
				tabbedPane.addTab( draggedTab );
				tabbedPane.setSelectedTab( draggedTab );
				
				if( dragSourceWindowSize != null )
				{
					newWindow.setSize( dragSourceWindowSize );
				}
				else
				{
					newWindow.pack( );
				}
				
				newWindow.setLocation( dsde.getLocation( ) );
				newWindow.setVisible( true );
				
				newWindow.toFront( );
				newWindow.requestFocus( );
				
				Point loc = newWindow.getLocation( );
				Component renderer = draggedTab.getRenderer( );
				Point tabPos = new Point( renderer.getWidth( ) / 2 , renderer.getHeight( ) / 2 );
				SwingUtilities.convertPointToScreen( tabPos , renderer );
				
				loc.x += dsde.getX( ) - tabPos.x;
				loc.y += dsde.getY( ) - tabPos.y;
				newWindow.setLocation( loc );
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
		
		public void dispose( )
		{
			target.removeDropTargetListener( this );
			target.setComponent( null );
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
			JhromeTabbedPane.dragOver( dtde );
		}
		
		@Override
		public void dragExit( DropTargetEvent dte )
		{
			JhromeTabbedPane.dragOut( dte );
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
			
			if( draggedTab != null && JhromeUtils.contains( dropZone , dtde.getLocation( ) ) && isSnapInAllowed( draggedTab ) )
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
	
	private static Window		dragImageWindow			= null;
	
	private static Image		dragImage				= null;
	
	private static double		grabX					= 0;
	
	private static IJhromeTab	draggedTab				= null;
	
	private static List<Window>	deadWindows				= new ArrayList<Window>( );
	
	private static Dimension	dragSourceWindowSize	= null;
	
	private boolean isTearAwayAllowed( IJhromeTab tab )
	{
		return dndPolicy == null || dndPolicy.isTearAwayAllowed( this , tab );
	}
	
	private boolean isSnapInAllowed( IJhromeTab tab )
	{
		return dndPolicy == null || dndPolicy.isSnapInAllowed( this , tab );
	}
	
	private static void dragOut( DropTargetEvent dte )
	{
		if( draggedTab != null )
		{
			JhromeTabbedPane draggedParent = getJhromeTabbedPaneAncestor( draggedTab.getRenderer( ) );
			if( draggedParent != null && dte.getDropTargetContext( ).getComponent( ) == draggedParent && draggedParent.isTearAwayAllowed( draggedTab ) )
			{
				initDragImageWindow( );
				removeDraggedTabFromParent( );
			}
		}
	}
	
	private static void dragOut( DragSourceDragEvent dsde )
	{
		if( draggedTab != null )
		{
			JhromeTabbedPane draggedParent = getJhromeTabbedPaneAncestor( draggedTab.getRenderer( ) );
			if( draggedParent != null && dsde.getDragSourceContext( ).getComponent( ) == draggedParent && draggedParent.isTearAwayAllowed( draggedTab ) )
			{
				initDragImageWindow( );
				removeDraggedTabFromParent( );
			}
		}
	}
	
	private static void removeDraggedTabFromParent( )
	{
		JhromeTabbedPane draggedParent = getJhromeTabbedPaneAncestor( draggedTab.getRenderer( ) );
		draggedParent.setDragState( null , 0 , 0 );
		draggedParent.removeTabImmediately( draggedTab );
		if( draggedParent.getTabCount( ) == 0 )
		{
			Window window = SwingUtilities.getWindowAncestor( draggedParent );
			window.setVisible( false );
			deadWindows.add( window );
		}
		draggedParent = null;
	}
	
	private static void dragOver( DropTargetDragEvent dtde )
	{
		if( draggedTab != null )
		{
			JhromeTabbedPane tabbedPane = ( JhromeTabbedPane ) dtde.getDropTargetContext( ).getComponent( );
			
			Point p = dtde.getLocation( );
			if( !JhromeUtils.contains( tabbedPane.dropZone , p ) )
			{
				dragOut( dtde );
				return;
			}
			
			JhromeTabbedPane draggedParent = getJhromeTabbedPaneAncestor( draggedTab.getRenderer( ) );
			
			if( draggedParent != tabbedPane && ( draggedParent == null || draggedParent.isTearAwayAllowed( draggedTab ) ) && tabbedPane.isSnapInAllowed( draggedTab ) )
			{
				disposeDragImageWindow( );
				
				if( draggedParent != null )
				{
					removeDraggedTabFromParent( );
				}
				
				draggedParent = tabbedPane;
				
				Window ancestor = SwingUtilities.getWindowAncestor( tabbedPane );
				if( ancestor != null )
				{
					ancestor.toFront( );
					ancestor.requestFocus( );
				}
				
				int dragX = dtde.getLocation( ).x;
				
				int newIndex = tabbedPane.layout.getInsertIndex( draggedTab , grabX , dragX );
				tabbedPane.addTab( newIndex , draggedTab );
				
				tabbedPane.setDragState( draggedTab , grabX , dragX );
				tabbedPane.setSelectedTab( draggedTab );
			}
			else
			{
				TabInfo info = tabbedPane.getInfo( draggedTab );
				if( info != null )
				{
					int currentIndex = tabbedPane.tabs.indexOf( info );
					int dragX = dtde.getLocation( ).x;
					tabbedPane.setDragState( draggedTab , grabX , dragX );
					
					int newIndex = tabbedPane.layout.getInsertIndex( draggedTab , grabX , dragX );
					if( newIndex != currentIndex )
					{
						tabbedPane.tabs.remove( info );
						newIndex = Math.min( newIndex , tabbedPane.tabs.size( ) );
						tabbedPane.tabs.add( newIndex , info );
						
						tabbedPane.invalidate( );
						tabbedPane.validate( );
					}
				}
			}
		}
	}
	
	private Image createDragImage( IJhromeTab tab )
	{
		Component rend = tab.getRenderer( );
		Component cont = contentPanel;
		
		int width = rend.getWidth( );
		int height = rend.getHeight( );
		
		if( cont != null )
		{
			width = Math.max( width , cont.getWidth( ) );
			height += cont.getHeight( ) - contentPanelOverlap;
		}
		
		if( width == 0 || height == 0 )
		{
			return null;
		}
		
		BufferedImage image = new BufferedImage( width , height , BufferedImage.TYPE_INT_ARGB );
		
		Graphics2D g2 = ( Graphics2D ) image.getGraphics( );
		
		AffineTransform origXform = g2.getTransform( );
		
		if( cont != null )
		{
			g2.translate( 0 , rend.getHeight( ) - contentPanelOverlap );
			cont.paint( g2 );
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
	
	private static void initDragImageWindow( )
	{
		if( dragImageWindow == null )
		{
			dragImageWindow = new Window( null )
			{
				@Override
				public void paint( Graphics g )
				{
					Graphics2D g2 = ( Graphics2D ) g;
					
					if( dragImage != null )
					{
						g2.drawImage( dragImage , 0 , 0 , null );
					}
				}
			};
			
			AWTUtilities.setWindowOpaque( dragImageWindow , false );
		}
		
		if( dragImage != null )
		{
			dragImageWindow.setSize( dragImage.getWidth( null ) , dragImage.getHeight( null ) );
		}
		dragImageWindow.setAlwaysOnTop( true );
	}
	
	private static void moveDragImageWindow( Point p )
	{
		if( dragImageWindow != null )
		{
			dragImageWindow.setLocation( p );
			dragImageWindow.setVisible( true );
		}
	}
	
	private static void disposeDragImageWindow( )
	{
		if( dragImageWindow != null )
		{
			dragImageWindow.dispose( );
			dragImageWindow = null;
		}
	}
	
	private static void checkEDT( )
	{
		if( !SwingUtilities.isEventDispatchThread( ) )
		{
			throw new IllegalArgumentException( "Must be called on the AWT Event Dispatch Thread!" );
		}
	}
	
	public static JhromeTabbedPane getJhromeTabbedPaneAncestor( Component c )
	{
		while( c != null )
		{
			if( c instanceof JhromeTabbedPane )
			{
				return ( JhromeTabbedPane ) c;
			}
			c = c.getParent( );
		}
		return null;
	}
}
