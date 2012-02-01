
package org.jhrome;

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
		return new DefaultTab( title );
	}
}
