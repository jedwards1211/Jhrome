package org.sexydock.tabs;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.sexydock.tabs.jhrome.JhromeTabUI;

public class Tab extends JComponent
{
	private static final String	uiClassId			= TabUI.class.getName( );
	
	private static final long	serialVersionUID	= 5209596149902613716L;
	
	private String				title;
	private boolean				rollover;
	private boolean				selected;
	
	private Component			content;
	
	static
	{
		UIManager.getDefaults( ).put( uiClassId , JhromeTabUI.class.getName( ) );
	}
	
	public Tab( String title )
	{
		this( title , null );
	}
	
	public Tab( String title , Component content )
	{
		setTitle( title );
		setContent( content );
		
		updateUI( );
	}
	
	public boolean isRollover( )
	{
		return rollover;
	}
	
	public void setRollover( boolean rollover )
	{
		this.rollover = rollover;
	}
	
	public boolean isSelected( )
	{
		return selected;
	}
	
	public void setSelected( boolean selected )
	{
		this.selected = selected;
	}
	
	public TabUI getUI( )
	{
		return ( TabUI ) ui;
	}
	
	public String getTitle( )
	{
		return title;
	}
	
	public void setTitle( String title )
	{
		this.title = title;
	}
	
	public Component getRenderer( )
	{
		return this;
	}
	
	public Component getContent( )
	{
		return content;
	}
	
	public void setContent( Component tabContent )
	{
		this.content = tabContent;
		// TODO update tabbed pane if necessary
	}
	
	/**
	 * Specifies where the user can click and drag this jhromeTab. This way the jhromeTab component can behave as though it has a non-rectangular shape.
	 * 
	 * @param p
	 *            the point at which the user pressed the mouse, in the renderer's coordinate system.
	 * @return {@code true} if the user can drag the jhromeTab after dragging from {@code p}. NOTE: must return {@code false} for points over the close button
	 *         and other operable components, or the user will be able to drag the jhromeTab from these points!
	 */
	public boolean isDraggableAt( Point p )
	{
		return getUI( ).isDraggableAt( this , p );
	}
	
	/**
	 * Specifies where the user can click and select this jhromeTab. This way the jhromeTab component can behave as though it has a non-rectangular shape.
	 * 
	 * @param p
	 *            the point at which the user pressed the mouse, in the renderer's coordinate system.
	 * @return {@code true} if the user can select the jhromeTab by pressing the mouse at {@code p}. NOTE: must return {@code false} for points over the close
	 *         button and other operable components, or the user will be able to select the jhromeTab by clicking these components!
	 */
	public boolean isSelectableAt( Point p )
	{
		return getUI( ).isSelectableAt( this , p );
	}
	
	/**
	 * Specifies where the jhromeTab is "hoverable." If the user moves the mouse over the hoverable areas of this jhromeTab, {@link TabbedPane} will set it to
	 * the rollover state.
	 * 
	 * @param p
	 *            the point the user moved the mouse over, in the renderer's coordinate system.
	 * @return {@code true} if the jhromeTab should be set to the rollover state when the user moves the mouse over {@code p}.
	 */
	public boolean isHoverableAt( Point p )
	{
		return getUI( ).isHoverableAt( this , p );
	}
	
	@Override
	public String getUIClassID( )
	{
		return uiClassId;
	}
	
	@Override
	public void updateUI( )
	{
		setUI( ( TabUI ) UIManager.getUI( this ) );
	}
	
	public void setUI( TabUI ui )
	{
		super.setUI( ui );
	}
}