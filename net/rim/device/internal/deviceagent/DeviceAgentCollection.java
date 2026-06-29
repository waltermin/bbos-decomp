package net.rim.device.internal.deviceagent;

import java.util.Enumeration;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncParametersProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.proxy.Proxy;

class DeviceAgentCollection
   implements DeviceAgentUid,
   SyncCollection,
   CollectionEventSource,
   OTASyncCapable,
   OTASyncParametersProvider,
   SyncConverter,
   GlobalEventListener {
   PersistentObject _persistDatabase;
   IntHashtable _database;
   CollectionListenerManager _listeners = new CollectionListenerManager();
   boolean _inTransaction;

   int getVersion() {
      return 1;
   }

   void commit() {
      if (!this._inTransaction) {
         this._persistDatabase.commit();
      }
   }

   boolean contains(Object element) {
      if (!(element instanceof DeviceAgentModel)) {
         return false;
      }

      DeviceAgentModel m = (DeviceAgentModel)element;
      return this._database.get(m.getUID()) != null;
   }

   void removeAll() {
      synchronized (this._database) {
         this._database.clear();
      }
   }

   void remove(Object entry) {
      if (entry instanceof DeviceAgentModel) {
         DeviceAgentModel m = (DeviceAgentModel)entry;
         synchronized (this._database) {
            this._database.remove(m.getUID());
         }
      }
   }

   int size() {
      return this._database.size();
   }

   Object get(int key) {
      return this._database.get(key);
   }

   @Override
   public String getDataSourceName() {
      return "DevMgmt";
   }

   @Override
   public String getUserSystemId() {
      return null;
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
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
   public boolean addSyncObject(SyncObject object) {
      if (object instanceof DeviceAgentModel) {
         this.add(object);
         DeviceAgentEventLogger.logEvent(1094992975, 5, String.valueOf(((DeviceAgentModel)object).getUID()).getBytes());
         this._listeners.fireElementAdded(this, object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject instanceof DeviceAgentModel && newObject instanceof DeviceAgentModel) {
         this.remove(oldObject);
         this.add(newObject);
         DeviceAgentEventLogger.logEvent(1431323727, 5, String.valueOf(((DeviceAgentModel)oldObject).getUID()).getBytes());
         this._listeners.fireElementUpdated(this, oldObject, newObject);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      if (object instanceof DeviceAgentModel) {
         this.remove(object);
         DeviceAgentEventLogger.logEvent(1145392207, 5, String.valueOf(((DeviceAgentModel)object).getUID()).getBytes());
         this._listeners.fireElementRemoved(this, object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean removeAllSyncObjects() {
      synchronized (this._database) {
         this.removeAll();
         return true;
      }
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public int getSyncObjectCount() {
      return this.size();
   }

   @Override
   public void beginTransaction() {
      this._inTransaction = true;
   }

   @Override
   public void endTransaction() {
      if (this._inTransaction) {
         this._inTransaction = false;
         this.commit();
         this._listeners.fireReset(this);
      }
   }

   @Override
   public SyncObject[] getSyncObjects() {
      synchronized (this._database) {
         int dest = 0;
         SyncObject[] objects = new SyncObject[this.getSyncObjectCount()];
         Enumeration enumeration = this._database.elements();

         while (enumeration.hasMoreElements()) {
            Object element = enumeration.nextElement();
            if (element instanceof SyncObject) {
               objects[dest++] = (SyncObject)element;
            }
         }

         return objects;
      }
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      synchronized (this._database) {
         return (SyncObject)this._database.get(uid);
      }
   }

   @Override
   public int getSyncVersion() {
      return 1;
   }

   @Override
   public boolean convert(SyncObject param1, DataBuffer param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 1
      // 01: dup
      // 02: instanceof net/rim/device/internal/deviceagent/DeviceAgentModel
      // 05: ifne 0c
      // 08: pop
      // 09: goto 5b
      // 0c: checkcast net/rim/device/internal/deviceagent/DeviceAgentModel
      // 0f: astore 4
      // 11: aload 4
      // 13: invokevirtual net/rim/device/internal/deviceagent/DeviceAgentModel.getData ()Lnet/rim/device/api/util/DataBuffer;
      // 16: astore 5
      // 18: aload 5
      // 1a: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 1d: istore 6
      // 1f: aload 5
      // 21: bipush 0
      // 22: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 25: aload 5
      // 27: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 2a: newarray 8
      // 2c: astore 7
      // 2e: aload 5
      // 30: aload 7
      // 32: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 35: aload 2
      // 36: aload 7
      // 38: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 3b: aload 5
      // 3d: iload 6
      // 3f: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 42: bipush 1
      // 43: ireturn
      // 44: astore 7
      // 46: aload 5
      // 48: iload 6
      // 4a: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 4d: bipush 1
      // 4e: ireturn
      // 4f: astore 8
      // 51: aload 5
      // 53: iload 6
      // 55: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 58: aload 8
      // 5a: athrow
      // 5b: bipush 0
      // 5c: ireturn
      // try (14 -> 27): 32 null
      // try (14 -> 27): 38 null
      // try (32 -> 33): 38 null
      // try (38 -> 39): 38 null
   }

   public DataBuffer getDeviceAgentInfo(int uid) {
      DeviceAgentModel model = (DeviceAgentModel)this.get(uid);
      return model.getData();
   }

   @Override
   public SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public SyncConverter getSyncConverter() {
      throw null;
   }

   @Override
   public String getSyncName() {
      throw null;
   }

   @Override
   public String getDatabaseName() {
      throw null;
   }

   @Override
   public SyncObject convert(DataBuffer _1, int _2, int _3) {
      throw null;
   }

   @Override
   public void eventOccurred(long _1, int _3, int _4, Object _5, Object _6) {
      throw null;
   }

   DeviceAgentCollection(long guid) {
      this._persistDatabase = RIMPersistentStore.getPersistentObject(guid);
      synchronized (this._persistDatabase) {
         if (this._persistDatabase.getContents() == null) {
            this._persistDatabase.setContents(new IntHashtable(), 51);
         }
      }

      this._database = (IntHashtable)this._persistDatabase.getContents();
      Proxy.getInstance().addGlobalEventListener(this);
   }

   private void add(Object entry) {
      if (entry instanceof DeviceAgentModel) {
         DeviceAgentModel m = (DeviceAgentModel)entry;
         synchronized (this._database) {
            this._database.put(m.getUID(), entry);
         }
      }
   }
}
