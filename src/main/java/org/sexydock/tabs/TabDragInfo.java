package org.sexydock.tabs;

import java.awt.Dimension;
import java.awt.Point;

public class TabDragInfo
{
	
	public TabDragInfo( Tab tab , Point grabPoint , double grabX , IFloatingTabHandler floatingTabHandler , Dimension sourceWindowSize )
	{
		super( );
		this.floatingTabHandler = floatingTabHandler;
		this.grabPoint = new Point( grabPoint );
		this.grabX = grabX;
		this.tab = tab;
		this.sourceWindowSize = sourceWindowSize;
	}
	
	public final IFloatingTabHandler	floatingTabHandler;
	private Point						grabPoint	= null;
	public final double					grabX;
	public final Tab					tab;
	public final Dimension				sourceWindowSize;
	
	public void setGrabPoint( Point p )
	{
		grabPoint = p == null ? null : new Point( p );
	}
	
	public Point getGrabPoint( )
	{
		return new Point( grabPoint );
	}
}