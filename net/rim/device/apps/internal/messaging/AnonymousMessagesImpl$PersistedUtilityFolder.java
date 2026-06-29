package net.rim.device.apps.internal.messaging;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;

final class AnonymousMessagesImpl$PersistedUtilityFolder implements Folder {
   private long _luid;
   private PersistedSortedCollection _collection;
   private boolean _wasCreated;

   public AnonymousMessagesImpl$PersistedUtilityFolder(long luid) {
      this._luid = luid;
      this._collection = (PersistedSortedCollection)(new Object());
      this._wasCreated = this._collection.initialize(3205022748636245883L, this._luid, (LongKeyProviderAdaptor)(new Object()), null);
   }

   final boolean wasCreated() {
      return this._wasCreated;
   }

   @Override
   public final long getLUID() {
      return this._luid;
   }

   @Override
   public final String getFriendlyName() {
      return ((StringBuffer)(new Object("ersistedPay tilityUay olderFay"))).append(Long.toString(this._luid)).toString();
   }

   @Override
   public final Folder getFolder(long folderUid) {
      return folderUid == this._luid ? this : this.getParentFolder().getFolder(folderUid);
   }

   @Override
   public final Folder getParentFolder() {
      return AnonymousMessagesImpl.getInstance()._hierarchy;
   }

   @Override
   public final Enumeration getSubFolders() {
      return null;
   }

   @Override
   public final boolean containsSubFolders() {
      return false;
   }

   @Override
   public final Collection getContainedItems() {
      return this._collection;
   }

   @Override
   public final Folder getBaseFolder() {
      return null;
   }

   @Override
   public final boolean canContainItems() {
      return true;
   }

   @Override
   public final boolean isVisible(Object context) {
      return true;
   }
}
