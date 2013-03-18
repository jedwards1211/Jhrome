package org.sexydock.tabs.test;

import java.awt.Component;
import java.awt.Point;

import org.fest.swing.awt.AWT;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.ComponentFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.sexydock.tabs.BasicTabUI;
import org.sexydock.tabs.Tab;

public class TabFixture extends ComponentFixture<Tab>
{
	public TabFixture( Robot robot , Tab target )
	{
		super( robot , target );
	}
	
	public void click( )
	{
		robot.click( getClickComponent( ) );
	}
	
	public void dragTo( Point dest )
	{
		Component clickComp = getClickComponent( );
		robot.pressMouse( clickComp , AWT.visibleCenterOf( clickComp ) );
		robot.moveMouse( dest );
		robot.releaseMouse( MouseButton.LEFT_BUTTON );
	}
	
	public Component getClickComponent( )
	{
		class R implements Runnable
		{
			Component	result	= null;
			
			@Override
			public void run( )
			{
				if( target.getTabComponent( ) != null )
				{
					result = target.getTabComponent( );
					return;
				}
				if( target.getUI( ) instanceof BasicTabUI )
				{
					BasicTabUI ui = ( BasicTabUI ) target.getUI( );
					result = ui.getLabel( );
					return;
				}
				result = target;
			}
		}
		
		R r = new R( );
		DoSwing.doSwing( r );
		
		return r.result;
	}
	
	public JButtonFixture closeButton( )
	{
		class R implements Runnable
		{
			JButtonFixture	result	= null;
			
			@Override
			public void run( )
			{
				BasicTabUI ui = ( BasicTabUI ) target.getUI( );
				result = new JButtonFixture( robot , ui.getCloseButton( ) );
			}
		}
		
		R r = new R( );
		DoSwing.doSwing( r );
		
		return r.result;
	}
}
