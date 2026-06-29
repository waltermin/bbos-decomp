package net.rim.blackberry.api.pim;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;

public class EventContainer implements Persistable, SyncCollection, SyncConverter {
   private Hashtable _fieldsModels;
   static final int EVENT_FIELDS_SYNC_VERSION;
   static final int EVENT_FIELDS_SYNC_SIGNATURE;
   private static final long EVENT_CONTAINER_STORE;
   private static final byte FIELD_END_OF_DATA;

   public void commit() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-5426718932587642162L);
      synchronized (persistentObject) {
         persistentObject.setContents(this, 51);
         persistentObject.commit();
      }
   }

   public boolean contains(int uid) {
      return this._fieldsModels == null ? false : this._fieldsModels.containsKey(new Object(uid));
   }

   public void removeSyncObject(int uid) {
      if (this._fieldsModels != null) {
         Integer key = (Integer)(new Object(uid));
         this._fieldsModels.remove(key);
      }
   }

   public EventFieldsModel getEventFieldsModel(int uid) {
      if (this._fieldsModels != null) {
         Integer key = (Integer)(new Object(uid));
         return (EventFieldsModel)this._fieldsModels.get(key);
      } else {
         return null;
      }
   }

   @Override
   public boolean removeAllSyncObjects() {
      this._fieldsModels = null;
      return true;
   }

   @Override
   public SyncObject[] getSyncObjects() {
      if (this._fieldsModels == null) {
         return new Object[0];
      }

      int length = this._fieldsModels.size();
      int index = 0;
      SyncObject[] result = new Object[length];
      Enumeration enumeration = this._fieldsModels.elements();

      while (enumeration.hasMoreElements()) {
         result[index++] = (SyncObject)enumeration.nextElement();
      }

      return result;
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      if (this._fieldsModels != null) {
         Enumeration e = this._fieldsModels.elements();

         while (e.hasMoreElements()) {
            SyncObject element = (SyncObject)e.nextElement();
            if (element.getUID() == uid) {
               return element;
            }
         }
      }

      return null;
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return !(object instanceof EventFieldsModel) ? false : ((EventFieldsModel)object).isDirty();
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
      if (object instanceof EventFieldsModel) {
         ((EventFieldsModel)object).setDirty(true);
      }
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
      if (object instanceof EventFieldsModel) {
         ((EventFieldsModel)object).setDirty(false);
      }
   }

   @Override
   public int getSyncObjectCount() {
      return this._fieldsModels == null ? 0 : this._fieldsModels.size();
   }

   @Override
   public int getSyncVersion() {
      return 1;
   }

   @Override
   public String getSyncName() {
      return "PIM Event Fields";
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public void beginTransaction() {
   }

   @Override
   public void endTransaction() {
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof EventFieldsModel)) {
         return false;
      }

      EventFieldsModel model = (EventFieldsModel)object;
      DataBuffer dataBuffer = (DataBuffer)(new Object(buffer.isBigEndian()));
      dataBuffer.writeInt(-12975410);
      dataBuffer.writeByteArray(model.getData());
      dataBuffer.writeByte(0);
      byte[] data = dataBuffer.toArray();
      buffer.writeShort(data.length);
      buffer.writeByte(0);
      buffer.write(data);
      return true;
   }

   @Override
   public SyncObject convert(DataBuffer buffer, int version, int UID) {
      EventFieldsModel model = null;

      try {
         buffer.readShort();
         buffer.readByte();
         int signature = buffer.readInt();
         if (signature != -12975410) {
            return null;
         }

         model = new EventFieldsModel(buffer.readByteArray());
         buffer.readByte();
      } finally {
         return model;
      }

      return model;
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      if (this._fieldsModels == null) {
         return false;
      } else {
         Integer key = (Integer)(new Object(object.getUID()));
         if (this._fieldsModels.containsKey(key)) {
            this._fieldsModels.remove(key);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      EventFieldsModel newModel = null;
      if (!(newObject instanceof EventFieldsModel)) {
         return false;
      } else {
         newModel = (EventFieldsModel)newObject;
         if (this._fieldsModels == null) {
            return false;
         } else {
            int keyInt = oldObject.getUID();
            Integer key = (Integer)(new Object(keyInt));
            if (this._fieldsModels.containsKey(key) && newModel.getUID() == keyInt) {
               newModel.setDirty(false);
               this._fieldsModels.put(key, newModel);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      EventFieldsModel model = null;
      if (!(object instanceof EventFieldsModel)) {
         return false;
      }

      model = (EventFieldsModel)object;
      if (this._fieldsModels == null) {
         this._fieldsModels = (Hashtable)(new Object());
      }

      Integer key = (Integer)(new Object(object.getUID()));
      if (this._fieldsModels.containsKey(key)) {
         return false;
      }

      model.setDirty(false);
      this._fieldsModels.put(key, model);
      return true;
   }

   private EventContainer() {
   }

   public static EventContainer getInstance() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-5426718932587642162L);
      synchronized (persistentObject) {
         if (persistentObject.getContents() == null) {
            persistentObject.setContents(new EventContainer(), 51);
            persistentObject.commit();
         }
      }

      return (EventContainer)persistentObject.getContents();
   }
}
