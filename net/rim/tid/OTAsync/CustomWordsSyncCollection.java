package net.rim.tid.OTAsync;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.proxy.Proxy;
import net.rim.tid.data.LearningData;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.europe.repository.checker.AmbigiousWordsChecker;
import net.rim.tid.im.util.algorithm.Counter;
import net.rim.tid.util.Utils;

public final class CustomWordsSyncCollection extends WordSyncCollection {
   protected AmbigiousWordsChecker _ambigiousWordChecker;
   private final String[] DEPRICATED_DATA_TYPES = new String[]{"frq"};
   public static final long REGISTRY_ID;
   private static final long PERSISTENCE_ID;
   private static final long CLOCKS_COUNT_PERSISTENCE_ID;
   private static final String CURRENT_LOCALE = Locale.get(2053636096).getLanguage();
   private static CustomWordsSyncCollection _instance;

   private CustomWordsSyncCollection(String syncName, String otaPrefix) {
      super._syncName = syncName;
      super._otaPrefix = otaPrefix;
      SyncManager.getInstance().enableSynchronization(this);
      Proxy.getInstance().addRealtimeClockListener(this);
      PersistentContent.addListener(this);
   }

   public final void setRestrictedWordsChecker(AmbigiousWordsChecker checker) {
      this._ambigiousWordChecker = checker;
   }

   public static final CustomWordsSyncCollection getInstance() {
      return _instance;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (object instanceof CustomWordSyncObject) {
         super._listeners.fireElementAdded(this, object);
         CustomWordSyncObject cw = (CustomWordSyncObject)object;
         if (!super._elements.containsKey(cw.getUID())) {
            OTASyncableCustomWordsProvider reader = this.getReader(cw.getKey());
            if (reader == null) {
               if (super._debugOutputEnabled) {
                  System.err.println(((StringBuffer)(new Object("no reader found, remove object: "))).append(cw.toString()).toString());
               }

               super._listeners.fireElementRemoved(this, cw);
            } else {
               if (!reader.isDataStorageCreated()) {
                  reader.createLearningData(cw.getKey());
                  if (reader.getType() == 3) {
                     this.cleanFreqRecords(reader);
                  }
               }

               if (super._debugOutputEnabled) {
                  System.err.println(((StringBuffer)(new Object("addSyncObject: "))).append(cw.toString()).toString());
               }

               String word = cw.getWord();
               if (this._ambigiousWordChecker != null) {
                  char[] chars = word.toCharArray();
                  if (this._ambigiousWordChecker.containsWord(Utils.hashCode(chars, 0, chars.length, true))) {
                     return true;
                  }
               }

               int res = reader.addWord(cw.getWord(), cw.getFrequency());
               if (res != 0) {
                  Proxy.getInstance().invokeLater(new WordSyncCollection$RecordRemover(this, this, cw));
                  long LOGWORTHY_REPORT_REQUEST = 2888237357036234703L;
                  RIMGlobalMessagePoster.postGlobalEvent(
                     LOGWORTHY_REPORT_REQUEST, 0, 0, ((StringBuffer)(new Object("TID-OTA:"))).append(cw.getRecord()).append("-").append(res).toString(), null
                  );
                  return true;
               }
            }
         }
      }

      return true;
   }

   private final void cleanFreqRecords(OTASyncableCustomWordsProvider serverData) {
      OTASyncableCustomWordsProvider deviceData = null;
      LearningData data = LearningDataManager.getLearningData(serverData.getDataStorageName());
      if (data != null) {
         if (data.isDataLocked()) {
            return;
         }

         label68:
         try {
            deviceData = (OTASyncableCustomWordsProvider)serverData.getClass().newInstance();
         } finally {
            break label68;
         }

         if (deviceData != null) {
            CustomWordsSyncManager sm = new CustomWordsSyncManager();
            synchronized (LearningDataManager.getLock()) {
               deviceData.init(serverData.getType());
               if (deviceData.loadLearningData(data.getData())) {
                  deviceData.getWords(sm);
               }
            }

            if (sm.size() > 0) {
               SyncObject[] objects = sm.getSyncObjects();

               for (int i = objects.length - 1; i >= 0; i--) {
                  super._cache.remove(objects[i].getUID());
               }
            }
         }
      }
   }

   @Override
   protected final OTASyncableCustomWordsProvider getReader(String name) {
      if (name.indexOf(CURRENT_LOCALE) != -1) {
         return null;
      }

      for (int i = 0; i < this.DEPRICATED_DATA_TYPES.length; i++) {
         if (name.endsWith(this.DEPRICATED_DATA_TYPES[i])) {
            return null;
         }
      }

      OTASyncableCustomWordsProvider res = null;
      byte dataType = LearningDataManager.getType(name);
      switch (dataType) {
         case 1:
         case 3:
         case 5:
         case 6:
         default:
            res = (OTASyncableCustomWordsProvider)super._readers.get(name);
            if (res == null) {
               label48:
               try {
                  StringBuffer tmp = (StringBuffer)(new Object(super._otaPrefix));
                  tmp.append(name);
                  res = (OTASyncableCustomWordsProvider)Class.forName(tmp.toString()).newInstance();
               } finally {
                  break label48;
               }

               if (res != null) {
                  super._readers.put(name, res);
                  res.init(LearningDataManager.getType(name));
               }
            }
         case 0:
         case 2:
         case 4:
            return res;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (CustomWordsSyncCollection)ar.getOrWaitFor(2311779573471752100L);
      if (_instance == null) {
         _instance = new CustomWordsSyncCollection("CustomWordsCollection", "net.rim.tid.im.conv.repository.OTAReaders.OTAReader");
         ar.put(2311779573471752100L, _instance);
      }

      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-8522499434933243818L);
      synchronized (persistentObject) {
         if (persistentObject.getContents() == null) {
            persistentObject.setContents(new Object(), 51);
            persistentObject.commit();
         }
      }

      _instance.setElements((IntHashtable)persistentObject.getContents());
      _instance.setPersistentObject(persistentObject);
      PersistentObject clocksCountStore = RIMPersistentStore.getPersistentObject(-6865884521018630058L);
      synchronized (clocksCountStore) {
         if (clocksCountStore.getContents() == null) {
            clocksCountStore.setContents(new Counter(480), 51);
            clocksCountStore.commit();
         }
      }

      Counter clocksCount = (Counter)clocksCountStore.getContents();
      _instance.setClocksCount(clocksCount);
      _instance.setClocksCountStore(clocksCountStore);
   }
}
