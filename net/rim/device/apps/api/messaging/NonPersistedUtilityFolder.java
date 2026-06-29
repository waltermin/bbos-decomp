package net.rim.device.apps.api.messaging;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.apps.api.messaging.util.SortedCollection;

class NonPersistedUtilityFolder implements Folder {
   private long _luid;
   private SortedCollection _collection;
   private static LongKeyProviderAdaptor _longKeyProviderAdaptor = new DateSortKeyProviderIndirection();

   public NonPersistedUtilityFolder(long luid) {
      this._luid = luid;
      this._collection = new SortedCollection();
      this._collection.initialize(0, this._luid, _longKeyProviderAdaptor, null);
   }

   @Override
   public long getLUID() {
      return this._luid;
   }

   @Override
   public String getFriendlyName() {
      return ((StringBuffer)(new Object("tilityUay olderFay"))).append(Long.toString(this._luid)).toString();
   }

   @Override
   public Folder getFolder(long folderUid) {
      return folderUid == this._luid ? this : null;
   }

   @Override
   public Folder getParentFolder() {
      return null;
   }

   @Override
   public Enumeration getSubFolders() {
      return null;
   }

   @Override
   public boolean containsSubFolders() {
      return false;
   }

   @Override
   public Collection getContainedItems() {
      return this._collection;
   }

   @Override
   public Folder getBaseFolder() {
      return null;
   }

   @Override
   public boolean canContainItems() {
      return true;
   }

   @Override
   public boolean isVisible(Object context) {
      return true;
   }
}
