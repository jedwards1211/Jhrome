
package org.sexydock.tabs;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The default implementation of {@link ITabFactory}.
 * 
 * @author andy.edwards
 */
public class DefaultTabFactory implements ITabFactory
{
	private static int	tabCounter	= 1;
	
	@Override
	public ITab createTab( )
	{
		return createTab( "Tab " + ( tabCounter++ ) );
	}
	
	@Override
	public ITab createTab( String title )
	{
		return new DefaultTab( title , createTabContent( title ) );
	}
	
	private static Component createTabContent( String title )
	{
		JPanel content = new JPanel( );
		content.setOpaque( false );
		content.setLayout( new FlowLayout( ) );
		JLabel contentLabel = new JLabel( title );
		contentLabel.setFont( contentLabel.getFont( ).deriveFont( 72f ) );
		content.add( contentLabel );
		return content;
	}
}
