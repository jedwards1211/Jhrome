
package org.jhrome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

@SuppressWarnings( "serial" )
public class JhromeTab extends JComponent implements IJhromeTab
{
	JLabel						label;
	JButton						closeButton;
	CompoundBorder				compoundBorder;
	
	JhromeTabBorder				outerBorder;
	JhromeTabBorderAttributes	selectedAttributes		= JhromeTabBorderAttributes.SELECTED_BORDER.clone( );
	JhromeTabBorderAttributes	rolloverAttributes		= JhromeTabBorderAttributes.UNSELECTED_ROLLOVER_BORDER.clone( );
	JhromeTabBorderAttributes	normalAttributes		= JhromeTabBorderAttributes.UNSELECTED_BORDER.clone( );
	
	EmptyBorder					innerBorder;
	Component					content;
	
	boolean						selected;
	boolean						rollover;
	
	float						highlight				= 0f;
	
	float						highlightSpeed			= 0.1f;
	
	javax.swing.Timer			highlightTimer;
	Color						selectedLabelColor		= Color.BLACK;
	Color						unselectedLabelColor	= new Color( 80 , 80 , 80 );
	
	public JhromeTab( String title )
	{
		innerBorder = new EmptyBorder( 5 , 5 , 5 , 0 );
		outerBorder = new JhromeTabBorder( );
		outerBorder.attrs.copyAttributes( normalAttributes );
		compoundBorder = new CompoundBorder( outerBorder , innerBorder );
		setBorder( compoundBorder );
		setLayout( new BorderLayout( ) );
		
		label = new JLabel( title );
		label.setFont( label.getFont( ).deriveFont( Font.PLAIN ) );
		label.setForeground( unselectedLabelColor );
		add( label , BorderLayout.CENTER );
		
		closeButton = new JButton( );
		closeButton.setUI( new BasicButtonUI( ) );
		closeButton.setBorderPainted( false );
		closeButton.setContentAreaFilled( false );
		closeButton.setOpaque( false );
		closeButton.setIcon( JhromeTabCloseButtonIcons.getJhromeNormalIcon( ) );
		closeButton.setRolloverIcon( JhromeTabCloseButtonIcons.getJhromeRolloverIcon( ) );
		closeButton.setPressedIcon( JhromeTabCloseButtonIcons.getJhromePressedIcon( ) );
		closeButton.setPreferredSize( new Dimension( closeButton.getIcon( ).getIconWidth( ) + 1 , closeButton.getIcon( ).getIconHeight( ) + 1 ) );
		add( closeButton , BorderLayout.EAST );
		
		setOpaque( false );
		
		highlightTimer = new javax.swing.Timer( 30 , new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				onHighlightTimerEvent( e );
			}
		} );
		
		JPanel content = new JPanel( );
		content.setOpaque( false );
		content.setLayout( new FlowLayout( ) );
		JLabel contentLabel = new JLabel( label.getText( ) );
		contentLabel.setFont( contentLabel.getFont( ).deriveFont( 72f ) );
		content.add( contentLabel );
		
		this.content = content;
	}
	
	protected void onHighlightTimerEvent( ActionEvent e )
	{
		float targetHighlight = rollover ? 1f : 0f;
		if( highlight != targetHighlight )
		{
			highlight = animate( highlight , targetHighlight );
		}
		else
		{
			highlightTimer.stop( );
		}
		updateBorder( );
		repaint( );
	}
	
	protected void updateBorder( )
	{
		if( selected )
		{
			outerBorder.attrs.copyAttributes( selectedAttributes );
		}
		else
		{
			outerBorder.attrs.copyAttributes( rolloverAttributes );
			outerBorder.attrs.interpolateColors( normalAttributes , rolloverAttributes , highlight );
		}
	}
	
	protected float animate( float current , float target )
	{
		if( current < target )
		{
			current = Math.min( target , current + highlightSpeed );
		}
		else if( current > target )
		{
			current = Math.max( target , current - highlightSpeed );
		}
		return current;
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
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#getRenderer()
	 */
	@Override
	public Component getRenderer( )
	{
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#getContent()
	 */
	@Override
	public Component getContent( )
	{
		return content;
	}
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#getCloseButton()
	 */
	@Override
	public JButton getCloseButton( )
	{
		return closeButton;
	}
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#isDraggableAt(java.awt.Point)
	 */
	@Override
	public boolean isDraggableAt( Point p )
	{
		return isHoverableAt( p ) && !closeButton.contains( SwingUtilities.convertPoint( this , p , closeButton ) );
	}
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#isSelectableAt(java.awt.Point)
	 */
	@Override
	public boolean isSelectableAt( Point p )
	{
		return isDraggableAt( p );
	}
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#isHoverableAt(java.awt.Point)
	 */
	@Override
	public boolean isHoverableAt( Point p )
	{
		return outerBorder.contains( p );
	}
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#isSelected()
	 */
	@Override
	public boolean isSelected( )
	{
		return selected;
	}
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#setSelected(boolean)
	 */
	@Override
	public void setSelected( boolean selected )
	{
		if( this.selected != selected )
		{
			this.selected = selected;
			label.setForeground( selected ? selectedLabelColor : unselectedLabelColor );
			highlightTimer.start( );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#isRollover()
	 */
	@Override
	public boolean isRollover( )
	{
		return rollover;
	}
	
	/* (non-Javadoc)
	 * @see org.jhrome.IJhromeTab#setRollover(boolean)
	 */
	@Override
	public void setRollover( boolean rollover )
	{
		if( this.rollover != rollover )
		{
			this.rollover = rollover;
			highlightTimer.start( );
		}
	}
}
