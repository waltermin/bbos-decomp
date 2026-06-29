package net.rim.tid.OTAsync;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.proxy.Proxy;
import net.rim.tid.data.LearningData;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.util.algorithm.Counter;

public class WordSyncCollection
   implements CollectionEventSource,
   SyncCollection,
   PersistentContentListener,
   RealtimeClockListener,
   Runnable,
   SyncConverter,
   OTASyncCapable,
   OTASyncListener {
   protected CollectionListenerManager _listeners = (CollectionListenerManager)(new Object());
   protected boolean _debugOutputEnabled;
   protected TestListener _testListener;
   protected IntHashtable _cache = (IntHashtable)(new Object());
   protected IntHashtable _elements;
   protected WordSyncCollection$Cleaner _cleaner = new WordSyncCollection$Cleaner(this);
   protected boolean _cacheWasCleaned;
   protected Hashtable _readers = (Hashtable)(new Object());
   protected boolean _cpLocked;
   protected SLInputMethod _currentIM;
   protected PersistentObject _persistentObject;
   protected PersistentObject _clocksCountStore;
   protected Counter _clocksCount;
   protected String _syncName = "";
   protected String _otaPrefix = "";
   protected int _syncVersion = 0;
   protected static final int WORD_ID;
   protected static final int FREQ_ID;
   protected static final int TIME_OUT;
   protected static final int SHORT_TIME_OUT;
   protected static final int IDLE_TIME_OUT;

   protected OTASyncableCustomWordsProvider getReader(String _1) {
      throw null;
   }

   public void setClocksCount(Counter clocksCount) {
      this._clocksCount = clocksCount;
   }

   public void setClocksCountStore(PersistentObject clocksCountStore) {
      this._clocksCountStore = clocksCountStore;
   }

   public void setPersistentObject(PersistentObject persistentObject) {
      this._persistentObject = persistentObject;
   }

   public void setElements(IntHashtable elements) {
      this._elements = elements;
   }

   protected void startNextRun(int timeout) {
      this._clocksCount.set(timeout);
      this._clocksCountStore.commit();
      Proxy.getInstance().addRealtimeClockListener(this);
   }

   public void addTestListener(TestListener listener) {
      this._testListener = listener;
   }

   public void toggleDebugOutputState() {
      this._debugOutputEnabled = !this._debugOutputEnabled;
   }

   public boolean isDebugOutputEnabled() {
      return this._debugOutputEnabled;
   }

   public void setInputMethod(SLInputMethod inputMethod) {
      this._currentIM = inputMethod;
   }

   public boolean isQuickDeleteSupported() {
      return SyncManager.getInstance().isCollectionResetSupported();
   }

   public void remove(CustomWordsSyncManager data) {
      if (data != null) {
         Proxy.getInstance().invokeLater(new WordSyncCollection$Remover(this, this, data.elements()));
      }
   }

   protected void cleanCache() {
      if (this._debugOutputEnabled) {
         System.err.println(((StringBuffer)(new Object("cleanCache: - "))).append(this._cache.size()).toString());
      }

      synchronized (this._cache) {
         this._cache.clear();
         this._cacheWasCleaned = true;
      }
   }

   public void update(CustomWordSyncObject element) {
      if (element != null) {
         if (element.needImmediateSynch()) {
            switch (element.getType()) {
               case 0:
                  return;
               case 1:
               default:
                  synchronized (this._cache) {
                     this._cache.put(element.getUID(), element);
                  }

                  this._listeners.fireElementAdded(this, element);
                  this._cleaner.start();
                  return;
               case 2:
                  this._listeners.fireElementRemoved(this, element);
            }
         } else {
            synchronized (this._elements) {
               this._elements.put(element.getUID(), element);
            }

            if (this._debugOutputEnabled) {
               System.err.println(((StringBuffer)(new Object("update: "))).append(element.toString()).toString());
            }
         }
      }
   }

   protected void otaSyncFinished() {
      Enumeration en = this._readers.elements();
      boolean notifyIM = false;
      synchronized (LearningDataManager.getLock()) {
         while (en.hasMoreElements()) {
            OTASyncableCustomWordsProvider reader = (OTASyncableCustomWordsProvider)en.nextElement();
            if (reader.isDataStorageCreated()) {
               LearningData data = LearningDataManager.getLearningData(reader.getDataStorageName());
               if (data == null) {
                  data = new LearningData(reader.getData());
                  LearningDataManager.setLearningData(reader.getDataStorageName(), data);
                  notifyIM = true;
               } else {
                  if (data.isDataLocked()) {
                     notifyIM = false;
                     break;
                  }

                  switch (reader.getType()) {
                     case 1:
                     case 5:
                        OTASyncableCustomWordsProvider tmp = null;

                        label89:
                        try {
                           tmp = (OTASyncableCustomWordsProvider)reader.getClass().newInstance();
                        } finally {
                           break label89;
                        }

                        if (tmp != null) {
                           tmp.init(reader.getType());
                           if (tmp.loadLearningData(data.getData())) {
                              this.mergeData(reader, tmp);
                              data.setData(reader.getData());
                              LearningDataManager.setLearningData(reader.getDataStorageName(), data);
                              notifyIM = true;
                           }
                        }
                        break;
                     case 3:
                        data.setData(reader.getData());
                        LearningDataManager.setLearningData(reader.getDataStorageName(), data);
                        notifyIM = true;
                  }
               }
            }
         }

         if (notifyIM && this._currentIM != null) {
            this._currentIM.actionPerformed(this, 109, null);
         }
      }

      this._readers.clear();
      if (this._debugOutputEnabled) {
         System.err.println("merging data has finished!");
      }
   }

   public void clear(String dataName) {
      synchronized (this._cache) {
         this._cache.clear();
      }

      boolean fireReset = false;
      synchronized (this._elements) {
         Enumeration en = this._elements.elements();

         while (en.hasMoreElements()) {
            CustomWordSyncObject cw = (CustomWordSyncObject)en.nextElement();
            if (cw.getRecord().endsWith(dataName)) {
               this._elements.remove(cw.getUID());
               fireReset = true;
            }
         }
      }

      if (fireReset) {
         this._listeners.fireReset(this);
      }
   }

   protected void fillCache(SyncObject[] data) {
      synchronized (this._cache) {
         this._cleaner.cancel();

         for (int i = data.length - 1; i >= 0; i--) {
            this._cache.put(data[i].getUID(), data[i]);
         }

         if (this._debugOutputEnabled) {
            System.err.println(((StringBuffer)(new Object("fillCache: - "))).append(this._cache.size()).toString());
         }

         this._cacheWasCleaned = false;
         this._cleaner.start();
      }
   }

   @Override
   public void otaSyncOperationStarted(SyncCollection syncCollection, int type) {
   }

   @Override
   public void otaSyncOperationStopped(SyncCollection syncCollection, int type) {
      if (type == 1 && SyncManager.getInstance().isSyncCompleted(this)) {
         this.otaSyncFinished();
      }
   }

   @Override
   public void beginTransaction() {
   }

   @Override
   public void persistentContentStateChanged(int state) {
      switch (state) {
         case 1:
            this._cpLocked = false;
         case 0:
            return;
         case 2:
         default:
            this._cpLocked = true;
            this.contentProtect(0);
            this.cleanCache();
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      this.contentProtect(2);
   }

   @Override
   public void endTransaction() {
      this.otaSyncFinished();
   }

   @Override
   public synchronized int getSyncObjectCount() {
      int count = 0;
      Enumeration en = LearningDataManager.getStoredRecordsNames();

      while (en.hasMoreElements()) {
         String name = (String)en.nextElement();
         OTASyncableCustomWordsProvider reader = this.getReader(name);
         if (reader != null) {
            LearningData data = LearningDataManager.getLearningData(name);
            if (data != null) {
               if (data.isDataLocked()) {
                  return count;
               }

               synchronized (LearningDataManager.getLock()) {
                  if (reader.loadLearningData(data.getData())) {
                     count += reader.getWordsCount();
                  }
               }
            }
         }
      }

      return count;
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
   public synchronized SyncObject[] getSyncObjects() {
      SyncObject[] res = null;
      Enumeration en = LearningDataManager.getStoredRecordsNames();
      CustomWordsSyncManager sm = new CustomWordsSyncManager();

      while (en.hasMoreElements()) {
         String name = (String)en.nextElement();
         OTASyncableCustomWordsProvider reader = this.getReader(name);
         if (reader != null) {
            LearningData data = LearningDataManager.getLearningData(name);
            if (data != null) {
               if (data.isDataLocked()) {
                  break;
               }

               synchronized (LearningDataManager.getLock()) {
                  if (reader.loadLearningData(data.getData())) {
                     reader.getWords(sm);
                  }
               }
            }
         }
      }

      res = sm.getSyncObjects();
      if (sm.size() > 0 && this._cache.isEmpty()) {
         this.fillCache(res);
      }

      return res;
   }

   @Override
   public int getSyncVersion() {
      return this._syncVersion;
   }

   @Override
   public String getSyncName() {
      return this._syncName;
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
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
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return true;
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      return true;
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      return false;
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      CustomWordSyncObject res = null;
      synchronized (this._cache) {
         res = (CustomWordSyncObject)this._cache.get(uid);
      }

      if (this._debugOutputEnabled) {
         System.err.println(((StringBuffer)(new Object("getSyncObject: - "))).append(uid).toString());
      }

      if (res == null && this._cacheWasCleaned) {
         this.getSyncObjects();
         synchronized (this._cache) {
            res = (CustomWordSyncObject)this._cache.get(uid);
         }
      }

      if (this._debugOutputEnabled && res != null) {
         System.err.println(((StringBuffer)(new Object("found: "))).append(res.toString()).toString());
      }

      return res;
   }

   @Override
   public synchronized SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof CustomWordSyncObject)) {
         return false;
      }

      CustomWordSyncObject entry = (CustomWordSyncObject)object;
      ConverterUtilities.writeStringSmart(buffer, 1, entry.getRecord());
      ConverterUtilities.convertInt(buffer, 2, entry.getFrequency(), 4);
      if (this._debugOutputEnabled) {
         System.err.println(((StringBuffer)(new Object("convert to server: - "))).append(entry.toString()).toString());
      }

      return true;
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int UID) {
      String record = null;
      int freq = 0;

      try {
         data.rewind();
         if (ConverterUtilities.findType(data, 1, true)) {
            record = ConverterUtilities.readString(data, true);
         }

         data.rewind();
         if (ConverterUtilities.findType(data, 2, true)) {
            freq = ConverterUtilities.readInt(data);
         }

         CustomWordSyncObject res = new CustomWordSyncObject(record, freq);
         if (this._debugOutputEnabled) {
            System.err.println(((StringBuffer)(new Object("convert from server: - "))).append(res.toString()).toString());
         }

         return res;
      } finally {
         ;
      }
   }

   @Override
   public SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public void clockUpdated() {
      if (this._clocksCount.decrement() <= 0) {
         Proxy.getInstance().removeRealtimeClockListener(this);
         if (DeviceInfo.getIdleTime() >= 300) {
            Proxy.getInstance().submitRunnable(this);
            return;
         }

         this.startNextRun(5);
      }
   }

   @Override
   public void run() {
      if (!this._cpLocked) {
         synchronized (this._cache) {
            this._cleaner.cancel();
            this._cache.clear();
            synchronized (this._elements) {
               Enumeration en = this._elements.elements();

               while (en.hasMoreElements()) {
                  CustomWordSyncObject cw = (CustomWordSyncObject)en.nextElement();
                  switch (cw.getType()) {
                     case 0:
                        break;
                     case 1:
                        this._cache.put(cw.getUID(), cw);
                        this._listeners.fireElementAdded(this, cw);
                        if (this._debugOutputEnabled) {
                           System.err.println(((StringBuffer)(new Object("elementAdded: - "))).append(cw.toString()).toString());
                        }
                        break;
                     case 2:
                     default:
                        this._listeners.fireElementRemoved(this, cw);
                        if (this._debugOutputEnabled) {
                           System.err.println(((StringBuffer)(new Object("elementRemoved: - "))).append(cw.toString()).toString());
                        }
                        break;
                     case 3:
                        this._cache.put(cw.getUID(), cw);
                        this._listeners.fireElementRemoved(this, cw);
                        this._listeners.fireElementAdded(this, cw);
                        if (this._debugOutputEnabled) {
                           System.err.println(((StringBuffer)(new Object("elementUpdated: - "))).append(cw.toString()).toString());
                        }
                  }
               }

               this._elements.clear();
            }

            this._persistentObject.commit();
            this._cleaner.start();
         }
      }

      this.startNextRun(480);
   }

   private void contentProtect(int action) {
      synchronized (this._elements) {
         Enumeration en = this._elements.elements();

         while (en.hasMoreElements()) {
            CustomWordSyncObject so = (CustomWordSyncObject)en.nextElement();
            switch (action) {
               case 0:
                  so.encode();
                  break;
               case 2:
                  so.reEncode();
            }
         }
      }
   }

   private void mergeData(OTASyncableCustomWordsProvider otaData, OTASyncableCustomWordsProvider deviceData) {
      CustomWordsSyncManager sm = new CustomWordsSyncManager();
      deviceData.getWords(sm);
      if (this._debugOutputEnabled) {
         System.err
            .println(
               ((StringBuffer)(new Object("mergeData - server: ")))
                  .append(otaData.getDataStorageName())
                  .append(" device: ")
                  .append(deviceData.getDataStorageName())
                  .toString()
            );
      }

      SyncObject[] words = sm.getSyncObjects();
      if (words != null) {
         for (int i = words.length - 1; i >= 0; i--) {
            CustomWordSyncObject cw = (CustomWordSyncObject)words[i];
            otaData.addWord(cw.getWord(), cw.getFrequency());
         }
      }
   }
}
