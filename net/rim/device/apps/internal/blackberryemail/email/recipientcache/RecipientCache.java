package net.rim.device.apps.internal.blackberryemail.email.recipientcache;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.utility.framework.RecryptableCollection;
import net.rim.device.apps.api.utility.framework.RecryptableCollectionUtilities;
import net.rim.device.apps.api.utility.general.LRUCache;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

public final class RecipientCache implements RecryptableCollection, PersistentContentListener {
   private LRUCache _cache;
   private RecipientCache$RecipientCacheSyncItem _syncItem;
   private PersistentObject _persist = RIMPersistentStore.getPersistentObject(-362925792369882738L);
   private static final long RECIPIENT_SEND_CACHE_PERSISTED = -362925792369882738L;
   private static final long RECIPIENT_SEND_CACHE_APP_REG = -8547700227378297055L;
   private static final int MAX_RECORDS = 128;
   public static final long DEFAULT_ENCODING_UID = -1L;
   public static final int DEFAULT_SERVICE_UID_HASH = -1;
   public static final int DEFAULT_SERVICE_USERID = -1;
   public static final int DEFAULT_FLAGS = 0;
   public static final int DEFAULT_CLASSIFICATION = -1;

   public static final synchronized RecipientCache getInstance() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      RecipientCache recipientCache = (RecipientCache)appRegistry.get(-8547700227378297055L);
      if (recipientCache == null) {
         recipientCache = new RecipientCache();
         appRegistry.put(-8547700227378297055L, recipientCache);
      }

      return recipientCache;
   }

   private RecipientCache() {
      synchronized (this._persist) {
         if (this._persist.getContents() == null) {
            this._persist.setContents(new LRUCache(128), 51);
            this._persist.commit();
         }
      }

      this._cache = (LRUCache)this._persist.getContents();
      this._syncItem = new RecipientCache$RecipientCacheSyncItem(this);
      SyncManager.getInstance().enableSynchronization(this._syncItem);
      PersistentContent.addListener(this);
   }

   private final void commit() {
      this._persist.commit();
      this._syncItem.fireSyncItemUpdated();
   }

   public final void add(RecipientCacheData data) {
      RecipientCacheData oldData = (RecipientCacheData)this._cache.get(data);
      if (oldData != null) {
         data.setFlag(oldData.getFlags() & -769);
      }

      this._cache.put(data, data);
      this.commit();
   }

   public final void add(String recipient, int serviceUserId, int serviceUIDHash, int messageClassification, long encodingUID, int flags) {
      RecipientCacheData data = new RecipientCacheData(recipient, serviceUserId, serviceUIDHash, messageClassification, encodingUID, flags);
      this.add(data);
   }

   public final void add(EmailMessageModel message, int serviceUserId, int serviceUIDHash, int messageClassification, long encodingUID, int flags) {
      ContextObject contextObject = new ContextObject(124);

      for (int j = message.size() - 1; j >= 0; j--) {
         Object submember = message.getAt(j);
         if (submember instanceof EmailHeaderModel) {
            EmailHeaderModel emailHeaderModel = (EmailHeaderModel)submember;
            String[] addressAndName = new String[2];
            if (emailHeaderModel.convert(contextObject, addressAndName)) {
               int maxAddressIndex = addressAndName.length - 2;

               for (int i = 0; i <= maxAddressIndex; i += 2) {
                  if (addressAndName[i] != null) {
                     this.add(addressAndName[i], serviceUserId, serviceUIDHash, messageClassification, encodingUID, flags);
                  }
               }
            }
         }
      }
   }

   public final void setFlag(String recipient, int flag) {
      RecipientCacheData data = this.get(recipient);
      if (data != null) {
         data.setFlag(flag);
         this.commit();
      } else {
         this.add(recipient, -1, -1, -1, -1, flag);
      }
   }

   public final synchronized RecipientCacheData get(String recipient) {
      return (RecipientCacheData)this._cache.get(recipient);
   }

   public final synchronized boolean remove(String recipient) {
      return this._cache.remove(recipient);
   }

   private final int size() {
      return this._cache.size();
   }

   private final void removeAll() {
      this._cache.clear();
   }

   @Override
   public final int getSize(Object cookie) {
      return this.size();
   }

   @Override
   public final Object getElementAt(int index, Object cookie) {
      return this._cache.getAt(index);
   }

   @Override
   public final void replaceElementAt(Object oldElement, Object newElement, int index, Object cookie) {
      this._cache.replaceAt(index, newElement);
   }

   @Override
   public final void updateListeners(Object oldElement, Object newElement, Object cookie) {
   }

   @Override
   public final void commit(Object cookie) {
      this.commit();
   }

   @Override
   public final void reCryptStarted(Object cookie) {
   }

   @Override
   public final void reCryptEnded(Object cookie) {
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      RecryptableCollectionUtilities.recrypt(this, this, null, generation);
   }
}
