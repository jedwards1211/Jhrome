
package org.jhrome;

public interface IJhromeTabDnDPolicy
{
	boolean isTearAwayAllowed( JhromeTabbedPane tabbedPane , IJhromeTab tab );
	
	boolean isSnapInAllowed( JhromeTabbedPane tabbedPane , IJhromeTab tab );
}
