package net.rim.device.api.crypto.certificate;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.internal.synchronization.MinimalSyncCollection;

final class CertificateOptionsSyncCollection implements SyncCollection, CollectionEventSource, MinimalSyncCollection {
   private CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   private static CertificateOptionsSyncConverter _converter = new CertificateOptionsSyncConverter();

   public final void fireElementRemoved(Object element) {
      synchronized (this._collectionListenerManager) {
         this._collectionListenerManager.fireElementRemoved(this, element);
      }
   }

   public final void fireElementUpdated(Object oldElement, Object newElement) {
      synchronized (this._collectionListenerManager) {
         this._collectionListenerManager.fireElementUpdated(this, oldElement, newElement);
      }
   }

   public final void fireElementAdded(Object element) {
      synchronized (this._collectionListenerManager) {
         this._collectionListenerManager.fireElementAdded(this, element);
      }
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (object instanceof CertificateServerInfo) {
         CertificateServers.getInstance().addServer((CertificateServerInfo)object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject instanceof CertificateServerInfo && newObject instanceof CertificateServerInfo) {
         CertificateServerInfo oldServerInfo = (CertificateServerInfo)oldObject;
         CertificateServerInfo newServerInfo = (CertificateServerInfo)newObject;
         CertificateServers options = CertificateServers.getInstance();
         options.removeServer(oldServerInfo);
         options.addServer(newServerInfo);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (object instanceof CertificateServerInfo) {
         CertificateServers.getInstance().internalRemoveServer((CertificateServerInfo)object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      CertificateServers.getInstance().internalRemoveAllElements();
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      return CertificateServers.getInstance().getSyncObjects();
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return CertificateServers.getInstance().getSyncObject(uid);
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      if (!(object instanceof CertificateServerInfo)) {
         return false;
      }

      CertificateServerInfo serverInfo = (CertificateServerInfo)object;
      return serverInfo.getType() == 4;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncObjectCount() {
      CertificateServers options = CertificateServers.getInstance();
      return options.getServerSize(1) + options.getServerSize(2) + options.getServerSize(3) + options.getServerSize(4);
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Certificate Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return _converter;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }

   public CertificateOptionsSyncCollection() {
   }
}
