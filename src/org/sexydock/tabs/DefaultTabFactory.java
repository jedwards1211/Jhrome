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
