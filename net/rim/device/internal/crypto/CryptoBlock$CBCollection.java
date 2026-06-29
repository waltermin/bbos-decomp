package net.rim.device.internal.crypto;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.DataBuffer;

public final class CryptoBlock$CBCollection implements SyncCollection, SyncConverter, OTASyncPriorityProvider {
   PersistentObject _root;
   Object[] _key = new Object[1];
   private static final int CURRENT_VERSION = 1;

   CryptoBlock$CBCollection(PersistentObject root) {
      this._root = root;
   }

   private final boolean isByID() {
      return this._root == CryptoBlock._persistentKeysById;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      synchronized (CryptoBlock._persistentKeysById) {
         Hashtable h = (Hashtable)this._root.getContents();
         if (h != null) {
            SyncObject[] objs = new SyncObject[h.size()];
            int curr = 0;
            Enumeration e = h.elements();

            while (e.hasMoreElements()) {
               SyncObject so = (SyncObject)e.nextElement();
               objs[curr++] = so;
            }

            return objs;
         }
      }

      return new SyncObject[0];
   }

   private static final void clearHashtable(PersistentObject root) {
      Hashtable h = (Hashtable)root.getContents();
      if (h != null) {
         h.clear();
         root.commit();
      }
   }

   private final boolean okToInject() {
      int status = CryptoBlock$CryptoBlockKey.sameDeviceKey(this._key[0]);
      if (status == -1) {
         return false;
      }

      if (status == 0) {
         CryptoBlock$CryptoBlockKey.setDeviceKey(this._key[0], true);
         synchronized (CryptoBlock._persistentKeysById) {
            clearHashtable(CryptoBlock._persistentKeysByName);
            clearHashtable(CryptoBlock._persistentKeysById);
            return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      synchronized (CryptoBlock._persistentKeysById) {
         Hashtable h = (Hashtable)this._root.getContents();
         if (h != null && this.okToInject()) {
            h.put(((CryptoBlock$CryptoBlockKey)object).getHashKey(this.isByID()), object);
            this._root.commit();
         }

         return true;
      }
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final int getSyncObjectCount() {
      synchronized (CryptoBlock._persistentKeysById) {
         Hashtable h = (Hashtable)this._root.getContents();
         return h != null ? h.size() : 0;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      return true;
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Encrypted Device Keys" + (this.isByID() ? "(by id)" : "(by name)");
   }

   @Override
   public final String getSyncName(Locale locale) {
      return this.getSyncName();
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final void beginTransaction() {
      CryptoBlock$CryptoBlockKey.getDeviceKey();
   }

   @Override
   public final void endTransaction() {
      this._key[0] = null;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      return version != 1 ? false : CryptoBlock$CryptoBlockKey.convert(object, buffer);
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int UID) {
      if (version != 1) {
         return null;
      }

      this._key[0] = null;
      return CryptoBlock$CryptoBlockKey.convert(data, UID, this._key);
   }

   @Override
   public final int getSyncPriority() {
      return 0;
   }
}
