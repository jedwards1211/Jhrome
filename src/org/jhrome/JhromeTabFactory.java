
package org.jhrome;

public class JhromeTabFactory implements IJhromeTabFactory
{
	private static int	tabCounter	= 1;
	
	@Override
	public IJhromeTab createTab( )
	{
		return createTab( "Tab " + ( tabCounter++ ) );
	}
	
	@Override
	public IJhromeTab createTab( String title )
	{
		return new JhromeTab( title );
	}
}
