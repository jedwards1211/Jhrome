
package org.jhrome;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The default implementation of {@link ITab}. Contains a title label and close button.
 * The label can be replaced with any {@link Component}, and the jhromeTab's look and feel can be customized with {@link TabUI}.
 * 
 * @author andy.edwards
 */
@SuppressWarnings( "serial" )
public class DefaultTab extends JComponent implements ITab
{
	JLabel		label;
	Component	overrideLabel;
	JButton		closeButton;
	
	Component	content;
	
	boolean		selected;
	boolean		rollover;
	
	public DefaultTab( String title )
	{
		setLayout( new BorderLayout( ) );
		
		label = new JLabel( title );
		add( label , BorderLayout.CENTER );
		
		closeButton = new JButton( );
		add( closeButton , BorderLayout.EAST );
		
		JPanel content = new JPanel( );
		content.setOpaque( false );
		content.setLayout( new FlowLayout( ) );
		JLabel contentLabel = new JLabel( label.getText( ) );
		contentLabel.setFont( contentLabel.getFont( ).deriveFont( 72f ) );
		content.add( contentLabel );
		
		this.content = content;
		
		setUI( new JhromeTabUI( ) );
	}
	
	public DefaultTab( String title , Component content )
	{
		setLayout( new BorderLayout( ) );
		
		label = new JLabel( title );
		add( label , BorderLayout.CENTER );
		
		closeButton = new JButton( );
		add( closeButton , BorderLayout.EAST );
		
		setUI( new JhromeTabUI( ) );
		
		this.content = content;
	}
	
	public void setTitle( String title )
	{
		label.setText( title );
	}
	
	public void setOverrideLabel( Component component )
	{
		if( overrideLabel != component )
		{
			if( overrideLabel != null )
			{
				remove( overrideLabel );
			}
			else
			{
				remove( label );
			}
			
			overrideLabel = component;
			
			if( component != null )
			{
				add( component , BorderLayout.CENTER );
			}
			else
			{
				add( label , BorderLayout.CENTER );
			}
		}
	}
	
	@Override
	public Dimension getMinimumSize( )
	{
		Dimension min = super.getMinimumSize( );
		if( min != null )
		{
			Insets insets = getInsets( );
			min.width = insets.left + insets.right;
		}
		return min;
	}
	
	@Override
	public void paint( Graphics g )
	{
		Dimension minSize = getMinimumSize( );
		if( minSize != null )
		{
			if( getWidth( ) < minSize.getWidth( ) )
			{
				return;
			}
		}
		super.paint( g );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#getRenderer()
	 */
	@Override
	public Component getRenderer( )
	{
		return this;
	}
	
	@Override
	public void setContent( Component content )
	{
		this.content = content;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#getContent()
	 */
	@Override
	public Component getContent( )
	{
		return content;
	}
	
	public JLabel getLabel( )
	{
		return label;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#getCloseButton()
	 */
	@Override
	public JButton getCloseButton( )
	{
		return closeButton;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isDraggableAt(java.awt.Point)
	 */
	@Override
	public boolean isDraggableAt( Point p )
	{
		if( ui != null && ui instanceof TabUI )
		{
			TabUI jui = ( TabUI ) ui;
			return jui.isDraggableAt( this , p );
		}
		return isHoverableAt( p ) && !closeButton.contains( SwingUtilities.convertPoint( this , p , closeButton ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isSelectableAt(java.awt.Point)
	 */
	@Override
	public boolean isSelectableAt( Point p )
	{
		if( ui != null && ui instanceof TabUI )
		{
			TabUI jui = ( TabUI ) ui;
			return jui.isDraggableAt( this , p );
		}
		return isDraggableAt( p );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isHoverableAt(java.awt.Point)
	 */
	@Override
	public boolean isHoverableAt( Point p )
	{
		if( ui != null && ui instanceof TabUI )
		{
			TabUI jui = ( TabUI ) ui;
			return jui.isDraggableAt( this , p );
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isSelected()
	 */
	@Override
	public boolean isSelected( )
	{
		return selected;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#setSelected(boolean)
	 */
	@Override
	public void setSelected( boolean selected )
	{
		if( this.selected != selected )
		{
			this.selected = selected;
			repaint( );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#isRollover()
	 */
	@Override
	public boolean isRollover( )
	{
		return rollover;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeTab#setRollover(boolean)
	 */
	@Override
	public void setRollover( boolean rollover )
	{
		if( this.rollover != rollover )
		{
			this.rollover = rollover;
			repaint( );
		}
	}
	
	public void setUI( TabUI ui )
	{
		super.setUI( ui );
	}
}
