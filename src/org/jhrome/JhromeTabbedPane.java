
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

import com.sun.awt.AWTUtilities;

@SuppressWarnings( "serial" )
public class JhromeTabbedPane extends JLayeredPane
{
	public JhromeTabbedPane( )
	{
		checkEDT( );
		init( );
	}
	
	private static int		tabCounter	= 1;
	
	private List<TabInfo>	tabs		= new ArrayList<TabInfo>( );
	
	private static class TabInfo
	{
		ActionListener	closeButtonHandler;
		IJhromeTab		tab;
		Dimension		prefSize;
		boolean			removing;
		
		int				targetX;
		int				targetWidth;
		Rectangle		targetBounds	= new Rectangle( );
		
		int				animX;
		int				animWidth;
		
		boolean			dragging;
		int				dragX;
		double			grabX;
	}
	
	private int						overlap				= 13;
	
	private double					animFactor			= 0.7;
	
	private javax.swing.Timer		animTimer;
	
	private TabLayoutManager		layout;
	
	private boolean					useUniformWidth		= true;
	private int						uniformWidth		= 300;
	
	private boolean					holdTabScale		= true;
	
	private MouseManager			mouseOverManager;
	
	private TabInfo					selectedTab			= null;
	
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
	
	private Rectangle				tabZone				= new Rectangle( );
	
	private int						extraDropZoneSpace	= 15;
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
										boolean newMouseOver = tabZone.contains( p );
										if( newMouseOver != holdTabScale )
										{
											holdTabScale = newMouseOver;
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
		
		mouseOverManager = new MouseManager( );
		mouseOverManager.install( this );
		
		contentPanel = new JPanel( );
		add( contentPanel );
		
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
				IJhromeTab newTab = tabFactory.createTab( "Tab " + ( tabCounter++ ) );
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
	
	public IJhromeTab getHoverableTabAt( Point p )
	{
		checkEDT( );
		
		for( int i = -1 ; i < tabs.size( ) ; i++ )
		{
			TabInfo info = i < 0 ? selectedTab : tabs.get( i );
			if( info == null || info.removing )
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
			if( info == null || info.removing )
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
			if( info == null || info.removing )
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
	
	public int getTabCount( )
	{
		checkEDT( );
		
		int count = 0;
		for( int i = 0 ; i < tabs.size( ) ; i++ )
		{
			if( !tabs.get( i ).removing )
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
			if( !info.removing )
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
		info.animWidth = expand ? 0 : info.prefSize.width;
		info.removing = false;
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
			info.animX = prev.animX + prev.animWidth - overlap;
		}
		tabs.add( index , info );
		add( tab.getRenderer( ) );
		
		invalidate( );
		validate( );
	}
	
	public void tabContentChanged( IJhromeTab tab )
	{
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
						if( !adjTab.removing )
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
							if( !adjTab.removing )
							{
								newSelectedTab = adjTab;
								break;
							}
						}
					}
					
					setSelectedTab( newSelectedTab );
				}
			}
			info.removing = true;
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
		
		setSelectedTab( ( TabInfo ) null );
		removeAll( );
		tabs.clear( );
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
			if( info == null || info.removing )
			{
				throw new IllegalArgumentException( "tab must be a child of this " + getClass( ).getName( ) );
			}
			setSelectedTab( info );
		}
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
		boolean validate = false;
		
		for( TabInfo info : tabs )
		{
			if( info.tab == draggedTab )
			{
				if( !info.dragging || info.grabX != grabX || info.dragX != dragX )
				{
					info.dragging = true;
					info.grabX = grabX;
					info.dragX = dragX;
					
					validate = true;
				}
			}
			else
			{
				if( info.dragging )
				{
					info.dragging = false;
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
	
	private int getInsertIndex( int x )
	{
		int virtualIndex = 0;
		int closestIndex = -1;
		int closestDistance = 0;
		
		for( int i = 0 ; i < tabs.size( ) ; i++ )
		{
			TabInfo info = tabs.get( i );
			if( !info.removing )
			{
				if( x >= info.targetBounds.x && x <= info.targetBounds.x + info.targetBounds.width )
				{
					return virtualIndex;
				}
				else
				{
					int distance = Math.min( Math.abs( x - info.targetBounds.x ) , Math.abs( x - info.targetBounds.x + info.targetBounds.width ) );
					if( closestIndex < 0 || distance < closestDistance )
					{
						closestIndex = virtualIndex;
						closestDistance = distance;
					}
				}
				virtualIndex++ ;
			}
		}
		return Math.max( closestIndex , 0 );
	}
	
	public JButton getNewTabButton( )
	{
		return newTabButton;
	}
	
	public void dispose( )
	{
		checkEDT( );
		
		removeAllTabs( );
		mouseOverManager.uninstall( this );
	}
	
	private int animate( int value , int target )
	{
		int d = value - target;
		d *= animFactor;
		return d == 0 ? target : target + d;
	}
	
	private class TabLayoutManager implements LayoutManager
	{
		private int	animTotalWidth	= 0;
		
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
			boolean reset = false;
			
			int width = getWidth( );
			int height = getHeight( );
			
			Insets insets = getInsets( );
			
			Dimension rightButtonPanelPrefSize = rightButtonsPanel.getPreferredSize( );
			
			int availWidth = width - insets.left - insets.right;
			int availTabWidth = width - insets.left - insets.right - tabMargin - tabMargin - overlap - rightButtonPanelPrefSize.width;
			int availHeight = height - insets.top - insets.bottom;
			
			boolean animNeeded = false;
			
			int x = 0;
			
			int newTotalWidth = 0;
			
			int tabHeight = rightButtonPanelPrefSize.height;
			
			boolean anyDragging = false;
			
			for( int i = 0 ; i < tabs.size( ) ; i++ )
			{
				TabInfo info = tabs.get( i );
				info.targetX = x;
				
				int targetWidth = info.removing ? 0 : useUniformWidth ? uniformWidth : info.prefSize.width;
				info.targetWidth = targetWidth;
				
				tabHeight = Math.max( tabHeight , info.prefSize.height );
				
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
				
				newTotalWidth = Math.max( newTotalWidth , info.targetX + info.animWidth );
				if( !info.dragging )
				{
					newTotalWidth = Math.max( newTotalWidth , info.animX + info.animWidth );
				}
				else
				{
					anyDragging = true;
				}
				
				if( !info.removing )
				{
					x += targetWidth;
				}
			}
			
			if( newTotalWidth > animTotalWidth )
			{
				animTotalWidth = newTotalWidth;
			}
			else if( !holdTabScale && !anyDragging && animTotalWidth != newTotalWidth )
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
			
			double widthRatio = animTotalWidth > availTabWidth ? availTabWidth / ( double ) animTotalWidth : 1.0;
			
			double adjWidthRatio = widthRatio;
			for( int i = 0 ; i < tabs.size( ) ; i++ )
			{
				TabInfo info = tabs.get( i );
				if( info.removing )
				{
					continue;
				}
				Dimension minSize = info.tab.getRenderer( ).getMinimumSize( );
				if( minSize != null && info.animWidth >= minSize.width )
				{
					int targetWidth = useUniformWidth ? uniformWidth : info.prefSize.width;
					adjWidthRatio = Math.max( adjWidthRatio , minSize.width / ( double ) targetWidth );
				}
			}
			
			for( int i = 0 ; i < tabs.size( ) ; i++ )
			{
				TabInfo info = tabs.get( i );
				int tabX = insets.left + tabMargin + ( int ) ( info.animX * adjWidthRatio );
				int tabTargetX = insets.left + tabMargin + ( int ) ( info.targetX * adjWidthRatio );
				int tabW = ( int ) ( info.animWidth * adjWidthRatio ) + overlap;
				int tabTargetW = ( int ) ( info.targetWidth * adjWidthRatio ) + overlap;
				if( info.dragging )
				{
					tabX = info.dragX - ( int ) ( info.grabX * tabW );
					tabX = Math.max( tabX , insets.left + tabMargin );
					if( i == tabs.size( ) - 1 )
					{
						tabX = Math.min( tabX , tabTargetX );
					}
					info.animX = ( int ) ( ( tabX - insets.left - tabMargin ) / adjWidthRatio );
				}
				info.tab.getRenderer( ).setBounds( tabX , insets.top , tabW , tabHeight );
				info.targetBounds.setFrame( tabTargetX , insets.top , tabTargetW , tabHeight );
			}
			
			tabZone.setFrame( insets.left , insets.top , availWidth , tabHeight );
			dropZone.setFrame( insets.left , insets.top , availWidth , Math.min( availHeight , tabHeight + extraDropZoneSpace ) );
			
			contentPanel.setBounds( insets.left , insets.top + tabHeight - contentPanelOverlap , availWidth , availHeight - tabHeight + contentPanelOverlap );
			
			int rightButtonPanelX = Math.min( insets.left + availWidth - tabMargin - rightButtonPanelPrefSize.width , insets.left + tabMargin + ( int ) ( newTotalWidth * adjWidthRatio ) + overlap / 2 );
			rightButtonsPanel.setBounds( rightButtonPanelX , insets.top , rightButtonPanelPrefSize.width , tabHeight );
			
			for( int i = tabs.size( ) - 1 ; i >= 0 ; i-- )
			{
				TabInfo info = tabs.get( i );
				if( info.removing && info.animWidth == 0 )
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
			
			if( animNeeded )
			{
				animTimer.start( );
			}
			else if( !animNeeded )
			{
				animTimer.stop( );
			}
		}
	}
	
	private class JhromeTransferable implements Transferable
	{
		@Override
		public DataFlavor[ ] getTransferDataFlavors( )
		{
			return null;
		}
		
		@Override
		public boolean isDataFlavorSupported( DataFlavor flavor )
		{
			return false;
		}
		
		@Override
		public Object getTransferData( DataFlavor flavor ) throws UnsupportedFlavorException , IOException
		{
			return null;
		}
	}
	
	private class DragHandler implements DragSourceListener,DragSourceMotionListener,DragGestureListener
	{
		DragSource	source;
		
		Point		dragOrigin;
		
		public DragHandler( Component comp , int actions )
		{
			source = new DragSource( );
			source.createDefaultDragGestureRecognizer( comp , actions , this );
			source.addDragSourceMotionListener( this );
		}
		
		@Override
		public void dragGestureRecognized( DragGestureEvent dge )
		{
			dragOrigin = dge.getDragOrigin( );
			
			draggedTab = getDraggableTabAt( dragOrigin );
			if( draggedTab != null )
			{
				dragImage = createDragImage( draggedTab );
				draggedParent = JhromeTabbedPane.this;
				Window window = SwingUtilities.getWindowAncestor( draggedParent );
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
			if( draggedTab != null && draggedParent != null )
			{
				Point p = dsde.getLocation( );
				SwingUtilities.convertPointFromScreen( p , draggedParent );
				if( !draggedParent.contains( p ) )
				{
					dragOut( dsde );
				}
			}
			
			moveDragImageWindow( new Point( dsde.getX( ) + 10 , dsde.getY( ) + 10 ) );
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
			
			if( draggedTab != null && windowFactory != null && !dsde.getDropSuccess( ) && ( draggedParent == null || draggedParent.isTearAwayAllowed( draggedTab ) ) )
			{
				IJhromeWindow newJhromeWindow = windowFactory.createWindow( );
				Window newWindow = newJhromeWindow.getWindow( );
				JhromeTabbedPane tabbedPane = newJhromeWindow.getTabbedPane( );
				
				if( draggedParent != null )
				{
					removeDraggedTabFromParent( );
				}
				
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
			
			if( draggedTab != null )
			{
				dtde.acceptDrop( dtde.getDropAction( ) );
				dtde.dropComplete( true );
			}
			else
			{
				dtde.rejectDrop( );
			}
		}
	}
	
	private static Window			dragImageWindow			= null;
	
	private static Image			dragImage				= null;
	
	private static JhromeTabbedPane	draggedParent			= null;
	
	private static double			grabX					= 0;
	
	private static IJhromeTab		draggedTab				= null;
	
	private static List<Window>		deadWindows				= new ArrayList<Window>( );
	
	private static Dimension		dragSourceWindowSize	= null;
	
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
			if( draggedParent != null && dsde.getDragSourceContext( ).getComponent( ) == draggedParent && draggedParent.isTearAwayAllowed( draggedTab ) )
			{
				initDragImageWindow( );
				removeDraggedTabFromParent( );
			}
		}
	}
	
	private static void removeDraggedTabFromParent( )
	{
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
				
				int insertX = dragX;
				
				int newIndex = tabbedPane.devirtualizeIndex( tabbedPane.getInsertIndex( insertX ) );
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
					
					int insertX = dragX + ( int ) ( ( 0.5 - grabX ) * draggedTab.getRenderer( ).getWidth( ) );
					
					int newIndex = tabbedPane.devirtualizeIndex( tabbedPane.getInsertIndex( insertX ) );
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
	
	private static boolean isDragImageWindowVisible( )
	{
		return ( dragImageWindow != null && dragImageWindow.isShowing( ) );
	}
	
	@SuppressWarnings( { "serial" } )
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
