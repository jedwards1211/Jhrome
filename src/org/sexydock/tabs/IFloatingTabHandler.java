
package org.sexydock.tabs;

import java.awt.dnd.DragSourceDragEvent;

public interface IFloatingTabHandler
{
	void onFloatingBegin( ITab draggedTab );
	
	void onFloatingTabDragged( DragSourceDragEvent dsde , ITab draggedTab , double grabX );
	
	void onFloatingEnd( );
}
