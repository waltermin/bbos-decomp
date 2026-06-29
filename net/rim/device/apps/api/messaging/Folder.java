package net.rim.device.apps.api.messaging;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;

public interface Folder {
   int INVALID_FOLDER_ID;

   long getLUID();

   String getFriendlyName();

   Folder getFolder(long var1);

   Folder getParentFolder();

   Enumeration getSubFolders();

   boolean containsSubFolders();

   Collection getContainedItems();

   Folder getBaseFolder();

   boolean canContainItems();

   boolean isVisible(Object var1);
}
