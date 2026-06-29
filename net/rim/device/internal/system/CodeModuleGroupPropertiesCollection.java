package net.rim.device.internal.system;

import java.io.EOFException;
import java.util.Enumeration;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.internal.proxy.Proxy;

public class CodeModuleGroupPropertiesCollection implements SyncCollection, SyncConverter, CollectionEventSource, OTASyncCapable, GlobalEventListener {
   private PersistentObject _persist;
   private IntHashtable _properties;
   private CodeModuleGroupPropertiesCollection$CheckGroupsThread _checkGroupsThread;
   private CollectionListenerManager _listeners = new CollectionListenerManager();
   private static final long PROPERTIES_ID;
   private static final int TYPE_PROPERTY_KEY;
   private static final int TYPE_PROPERTY_VALUE;
   private static Object _lock = new Object();

   private CodeModuleGroupPropertiesCollection() {
      this._persist = RIMPersistentStore.getPersistentObject(-1494190557092396307L);
      synchronized (this._persist) {
         if (this._persist.getContents() == null) {
            this._persist.setContents(new IntHashtable(), 51);
            this._persist.commit();
         }
      }

      this._properties = (IntHashtable)this._persist.getContents();
   }

   public static CodeModuleGroupPropertiesCollection getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      CodeModuleGroupPropertiesCollection properties = (CodeModuleGroupPropertiesCollection)ar.getOrWaitFor(-1494190557092396307L);
      if (properties == null) {
         properties = new CodeModuleGroupPropertiesCollection();
         ar.put(-1494190557092396307L, properties);
      }

      return properties;
   }

   public static void CodeModuleGroupPropertiesCollectionMain() {
      CodeModuleGroupPropertiesCollection instance = getInstance();

      try {
         CodeModuleGroup[] groups = CodeModuleGroupManager.loadAll();
         instance.checkGroupInformation(groups);
      } catch (Exception var2) {
      }

      Proxy.getInstance().addGlobalEventListener(instance);
      SyncManager sm = SyncManager.getInstance();
      if (sm != null) {
         sm.enableSynchronization(instance, true);
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 256826950193107649L || guid == -4232371946002803201L) {
         synchronized (_lock) {
            if (this._checkGroupsThread != null && this._checkGroupsThread.isAlive()) {
               this._checkGroupsThread.setFlag(guid);
            } else {
               this._checkGroupsThread = new CodeModuleGroupPropertiesCollection$CheckGroupsThread(null);
               this._checkGroupsThread.setFlag(guid);
               this._checkGroupsThread.start();
            }
         }
      }
   }

   public static int getGroupUID(String groupName) {
      return CRC32.update(-1, groupName.getBytes());
   }

   private void checkGroupInformation(CodeModuleGroup[] groups) {
      if (groups != null) {
         this.checkAddedGroupInformation(groups);
         this.checkDeletedGroupInformation(groups);
      }
   }

   private void checkAddedGroupInformation(CodeModuleGroup[] groups) {
      for (int i = 0; i < groups.length; i++) {
         int uid = getGroupUID(groups[i].getName());
         if (this._properties.get(uid) == null) {
            this.addSyncObject(this.createGroupProperties(groups[i]));
         }
      }
   }

   private void checkDeletedGroupInformation(CodeModuleGroup[] groups) {
      SyncObject[] objects = this.getSyncObjects();
      IntVector currentGroups = this.getCurrentModuleGroupUIDs(groups);
      if (currentGroups != null) {
         for (int i = 0; i < objects.length; i++) {
            if (!currentGroups.contains(objects[i].getUID())) {
               this.removeSyncObject(objects[i]);
            }
         }
      }
   }

   private CodeModuleGroupProperties createGroupProperties(CodeModuleGroup group) {
      int uid = getGroupUID(group.getName());
      CodeModuleGroupProperties properties = new CodeModuleGroupProperties(uid);
      Enumeration e = group.getPropertyNames();

      while (e.hasMoreElements()) {
         String key = (String)e.nextElement();
         properties.put(key, group.getProperty(key));
      }

      return properties;
   }

   private IntVector getCurrentModuleGroupUIDs(CodeModuleGroup[] groups) {
      IntVector uids = new IntVector(groups.length);

      for (int i = 0; i < groups.length; i++) {
         uids.addElement(getGroupUID(groups[i].getName()));
      }

      return uids;
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      if (object instanceof CodeModuleGroupProperties) {
         synchronized (this._properties) {
            this._properties.put(object.getUID(), object);
            this._persist.commit();
         }

         this._listeners.fireElementAdded(this, object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void beginTransaction() {
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public void endTransaction() {
   }

   @Override
   public SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public String getSyncName() {
      return "Code Module Group Properties";
   }

   @Override
   public String getSyncName(Locale locale) {
      return "Code Module Group Properties";
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      synchronized (this._properties) {
         return (SyncObject)this._properties.get(uid);
      }
   }

   @Override
   public int getSyncObjectCount() {
      return this._properties.size();
   }

   @Override
   public SyncObject[] getSyncObjects() {
      synchronized (this._properties) {
         SyncObject[] objects = new SyncObject[this.getSyncObjectCount()];
         int index = 0;
         Enumeration e = this._properties.elements();

         while (e.hasMoreElements()) {
            objects[index++] = (SyncObject)e.nextElement();
         }

         return objects;
      }
   }

   @Override
   public int getSyncVersion() {
      return 1;
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public boolean removeAllSyncObjects() {
      synchronized (this._properties) {
         this._properties.clear();
         this._persist.commit();
         return true;
      }
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      if (object instanceof CodeModuleGroupProperties) {
         synchronized (this._properties) {
            this._properties.remove(object.getUID());
            this._persist.commit();
         }

         this._listeners.fireElementRemoved(this, object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject instanceof CodeModuleGroupProperties && newObject instanceof CodeModuleGroupProperties) {
         synchronized (this._properties) {
            this._properties.remove(oldObject.getUID());
            this._properties.put(newObject.getUID(), newObject);
            this._persist.commit();
         }

         this._listeners.fireElementUpdated(this, oldObject, newObject);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int UID) {
      CodeModuleGroupProperties properties = new CodeModuleGroupProperties(UID);

      try {
         data.rewind();

         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(data);
            } catch (EOFException eofe) {
               return properties;
            }

            if (type == 1) {
               String key = ConverterUtilities.readString(data, true);
               type = ConverterUtilities.getType(data);
               if (type == 2) {
                  String value = ConverterUtilities.readString(data, true);
                  properties.put(key, value);
               }
            } else {
               ConverterUtilities.skipField(data);
            }
         }
      } catch (EOFException eofe) {
         return null;
      }
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer data, int version) {
      if (!(object instanceof CodeModuleGroupProperties)) {
         return false;
      }

      CodeModuleGroupProperties properties = (CodeModuleGroupProperties)object;
      Enumeration e = properties.keys();

      while (e.hasMoreElements()) {
         String key = (String)e.nextElement();
         String value = (String)properties.get(key);
         ConverterUtilities.writeStringSmart(data, 1, key);
         ConverterUtilities.writeStringSmart(data, 2, value);
      }

      return true;
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._listeners.removeCollectionListener(listener);
   }

   @Override
   public SyncCollectionSchema getSchema() {
      return null;
   }
}
