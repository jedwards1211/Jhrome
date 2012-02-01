
package org.jhrome;

import java.awt.dnd.DragSourceDragEvent;

public interface IFloatingTabHandler
{
	void onFloatingBegin( ITab draggedTab );
	
	void onFloatingTabDragged( DragSourceDragEvent dsde , ITab draggedTab , double grabX );
	
	void onFloatingEnd( );
}
