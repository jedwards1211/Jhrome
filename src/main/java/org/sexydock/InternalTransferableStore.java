package org.sexydock;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public final class InternalTransferableStore
{
	private String	storedUUID		= null;
	private Object	storedObject	= null;
	
	public final Transferable createTransferable( Object o )
	{
		String uuid = UUID.randomUUID( ).toString( );
		while( uuid.equals( storedUUID ) )
		{
			uuid = UUID.randomUUID( ).toString( );
		}
		storedUUID = uuid;
		storedObject = o;
		return new StringSelection( uuid );
	}
	
	public final Object getTransferableData( Transferable t ) throws UnsupportedFlavorException , IOException
	{
		String uuid = ( String ) t.getTransferData( DataFlavor.stringFlavor );
		return uuid.equals( storedUUID ) ? storedObject : null;
	}
	
	public final void cleanUp( Transferable t )
	{
		if( t.isDataFlavorSupported( DataFlavor.stringFlavor ) )
		{
			try
			{
				String uuid = ( String ) t.getTransferData( DataFlavor.stringFlavor );
				if( uuid.equals( storedUUID ) )
				{
					storedUUID = null;
					storedObject = null;
				}
			}
			catch( Exception ex )
			{
				
			}
		}
	}
	
	public static InternalTransferableStore getDefaultInstance( )
	{
		return DefaultInstanceHolder.INSTANCE;
	}
	
	private static class DefaultInstanceHolder
	{
		public static final InternalTransferableStore	INSTANCE	= new InternalTransferableStore( );
	}
}
