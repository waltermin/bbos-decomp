package net.rim.device.api.servicebook.selector;

import java.util.Vector;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.OTAUpgrade;

public final class SRSelectorSyncCollection implements SyncCollection, SyncConverter, OTASyncCapable, CollectionEventSource {
   private CollectionListenerManager _collectionListenerManager = new CollectionListenerManager();
   private Vector _syncObjects;
   private SRSelector _srSel;
   private ServiceBook _sb;
   private SyncCollectionSchema _schema;
   public static final long SR_SELECTOR_SYNC_GUID = -1080498432380349415L;
   private static final int NAME_TAG = 1;
   private static final int GUID_TAG = 2;
   private static final int CID_TAG = 3;
   private static final int UID_TAG = 4;
   private static final int USER_SET_TAG = 5;
   private static final int[] KEY_FIELD_IDS = new int[]{2, 3, -804651003, 5, 6, 7, 8, 3};
   private static final int DEFAULT_RECORD_TYPE = 1;

   public final void elementAdded(SRSelectorData data) {
      SRAppSyncObject syncObj = new SRAppSyncObject(data, (int)data.guid);
      syncObj.setUid(this.findUid(data));
      this._collectionListenerManager.fireElementAdded(this, syncObj);
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
      SRAppSyncObject obj = (SRAppSyncObject)object;
      SRSelectorData data = obj.getData();
      if (data.name != null && data.cid != null) {
         if (this._syncObjects == null) {
            this._syncObjects = new Vector();
         }

         this._syncObjects.addElement(obj);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      Vector datas = this._srSel.getAppData();
      SyncObject[] objs = new SyncObject[datas.size()];

      for (int i = datas.size() - 1; i >= 0; i--) {
         SRSelectorData d = (SRSelectorData)datas.elementAt(i);
         SRAppSyncObject obj = new SRAppSyncObject(d, i + 1);
         obj.setUid(this.findUid(d));
         objs[i] = obj;
      }

      return objs;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public final int getSyncObjectCount() {
      return this._srSel.getAppData().size();
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return false;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      return false;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return OTAUpgrade.isOTASLInProgress() ? this.addSyncObject(newObject) : false;
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Default Service Selector";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
      this.restoreStopped();
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      SRAppSyncObject sync = (SRAppSyncObject)object;
      SRSelectorData data = sync.getData();
      ConverterUtilities.writeStringIntellisync(buffer, 1, data.name);
      ConverterUtilities.writeLong(buffer, 2, data.guid);
      ConverterUtilities.writeStringIntellisync(buffer, 3, data.cid);
      String uid;
      if ((uid = sync.getUid()) != null) {
         ConverterUtilities.writeStringIntellisync(buffer, 4, uid);
      }

      ConverterUtilities.convertInt(buffer, 5, data.userSet ? 1 : 0, 1);
      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int UID) {
      if (version != this.getSyncVersion()) {
         return null;
      }

      data.rewind();
      SRSelectorData selData = new SRSelectorData();
      SRAppSyncObject obj = new SRAppSyncObject(selData, UID);

      try {
         while (data.available() > 0) {
            switch (ConverterUtilities.getType(data)) {
               case 0:
                  break;
               case 1:
               default:
                  selData.name = ConverterUtilities.readString(data);
                  break;
               case 2:
                  selData.guid = ConverterUtilities.readLong(data);
                  break;
               case 3:
                  selData.cid = ConverterUtilities.readString(data);
                  break;
               case 4:
                  obj.setUid(ConverterUtilities.readString(data));
                  break;
               case 5:
                  selData.userSet = ConverterUtilities.readInt(data) == 1;
            }
         }

         return obj;
      } finally {
         ;
      }
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }

   private final String findUid(SRSelectorData data) {
      if (data.defaultId != -1) {
         ServiceRecord sr = this._sb.getRecordById(data.defaultId);
         if (sr != null) {
            return sr.getUid();
         }
      }

      return null;
   }

   private final void restoreStopped() {
      this._srSel.syncFinished(this._syncObjects);
      this._syncObjects = null;
   }

   public SRSelectorSyncCollection(SRSelector srSel, ServiceBook sb) {
      this._srSel = srSel;
      this._sb = sb;
      this._schema = new SyncCollectionSchema();
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
   }
}
