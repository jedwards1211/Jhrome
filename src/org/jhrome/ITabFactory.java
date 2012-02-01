
package org.jhrome;

/**
 * An interface for creating {@link ITab}s. {@link TabbedPane} uses this interface
 * to create new tabs whenever its new jhromeTab button is pressed. This way you can control what jhromeTab is created.
 * 
 * @author andy.edwards
 */
public interface ITabFactory
{
	/**
	 * @return a new {IJhromeTab}.
	 */
	ITab createTab( );
	
	/**
	 * @return a new {IJhromeTab} with the given title.
	 */
	ITab createTab( String title );
}
