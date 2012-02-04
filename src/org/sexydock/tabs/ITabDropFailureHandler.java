
package org.sexydock.tabs;

import java.awt.Dimension;
import java.awt.dnd.DragSourceDropEvent;

public interface ITabDropFailureHandler
{
	void onDropFailure( DragSourceDropEvent dsde , ITab draggedTab , Dimension sourceWindowSize );
}
