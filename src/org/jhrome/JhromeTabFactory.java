
package org.jhrome;

public class JhromeTabFactory implements IJhromeTabFactory
{
	@Override
	public IJhromeTab createTab( String title )
	{
		return new JhromeTab( title );
	}
}
