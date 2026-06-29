package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;

public class MessagePropertiesDefaults {
   private PersistentObject _persistedDataHolder = RIMPersistentStore.getPersistentObject(8944909432115055420L);
   private MessagePropertiesDefaults$MessagePropertiesDefaultsPersistedData _persistedData = (MessagePropertiesDefaults$MessagePropertiesDefaultsPersistedData)this._persistedDataHolder
      .getContents();
   private MessagePropertiesDefaults$MessagePropertiesDefaultsSyncItem _syncItem;
   private static final long ID = -1345867410942591367L;
   private static MessagePropertiesDefaults _instance;
   private static final long PERSISTED_DATA = 8944909432115055420L;
   private static final long ENCODING_UID_PGP_UNIVERSAL_DEFAULT = -742709496102783169L;

   public static MessagePropertiesDefaults getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (MessagePropertiesDefaults)applicationRegistry.getOrWaitFor(-1345867410942591367L);
         if (_instance == null) {
            _instance = new MessagePropertiesDefaults();
            applicationRegistry.put(-1345867410942591367L, _instance);
         }
      }

      return _instance;
   }

   private MessagePropertiesDefaults() {
      if (this._persistedData == null) {
         this._persistedData = new MessagePropertiesDefaults$MessagePropertiesDefaultsPersistedData();
         this._persistedDataHolder.setContents(this._persistedData, 51);
         this._persistedDataHolder.commit();
      }

      this._syncItem = new MessagePropertiesDefaults$MessagePropertiesDefaultsSyncItem(this, null);
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(this._syncItem);
      }
   }

   public long getEncodingUID() {
      return this._persistedData._defaultEncodingUID;
   }

   public int getEncodingAction() {
      return this._persistedData._defaultEncodingAction;
   }

   public int getMessageClassification() {
      return this._persistedData._defaultMessageClassification;
   }

   public synchronized void setProperties(long defaultEncodingUID, int defaultEncodingAction, int defaultMessageClassification) {
      this._persistedData._defaultEncodingUID = defaultEncodingUID;
      this._persistedData._defaultEncodingAction = defaultEncodingAction;
      this._persistedData._defaultMessageClassification = defaultMessageClassification;
      this._persistedDataHolder.commit();
      this._syncItem.fireSyncItemUpdated();
   }

   private synchronized void resetProperties() {
      this._persistedData.reset();
      this._persistedDataHolder.commit();
      this._syncItem.fireSyncItemUpdated();
   }
}
