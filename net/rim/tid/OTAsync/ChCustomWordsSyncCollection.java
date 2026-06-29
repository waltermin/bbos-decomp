package net.rim.tid.OTAsync;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.proxy.Proxy;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.util.algorithm.Counter;

public class ChCustomWordsSyncCollection extends WordSyncCollection {
   public static final long REGISTRY_ID = -1037950266182389283L;
   private static final long PERSISTENCE_ID = 6205514177874464825L;
   private static final long CLOCKS_COUNT_PERSISTENCE_ID = -6401963580920179664L;
   private static final String LEARNING_READER_NAME = "net.rim.tid.im.conv.zh.repository.SlChLearningWriter";
   private static final String BIGRAMS_READER_NAME = "net.rim.tid.im.conv.zh.learning.SlChLearningBigramsWordlist";
   private static final String CURRENT_LOCALE = ((StringBuffer)(new Object("__")))
      .append(Locale.get(2053636096).getLanguage())
      .append("_CN_Pinyin_")
      .toString();
   private static ChCustomWordsSyncCollection _instance;

   private ChCustomWordsSyncCollection(String syncName) {
      super._syncName = syncName;
      SyncManager.getInstance().enableSynchronization(this);
      Proxy.getInstance().addRealtimeClockListener(this);
      PersistentContent.addListener(this);
   }

   public static ChCustomWordsSyncCollection getInstance() {
      return _instance;
   }

   @Override
   protected OTASyncableCustomWordsProvider getReader(String name) {
      OTASyncableCustomWordsProvider res = null;
      byte dataType = LearningDataManager.getType(name);
      if ((dataType == 3 || dataType == 1) && (name.indexOf(CURRENT_LOCALE) != -1 || name.indexOf("__zh_CN__") != -1)) {
         res = (OTASyncableCustomWordsProvider)super._readers.get(name);
         if (res == null) {
            label47:
            try {
               res = (OTASyncableCustomWordsProvider)Class.forName(
                     dataType == 1 ? "net.rim.tid.im.conv.zh.repository.SlChLearningWriter" : "net.rim.tid.im.conv.zh.learning.SlChLearningBigramsWordlist"
                  )
                  .newInstance();
            } finally {
               break label47;
            }

            if (res != null) {
               super._readers.put(name, res);
               res.init(dataType);
               if (!res.isDataStorageCreated()) {
                  res.createLearningData(name);
               }
            }
         }
      }

      return res;
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      boolean ret = false;
      if (object instanceof CustomWordSyncObject) {
         super._listeners.fireElementAdded(this, object);
         CustomWordSyncObject cw = (CustomWordSyncObject)object;
         if (!super._elements.containsKey(cw.getUID())) {
            OTASyncableCustomWordsProvider writer = this.getReader(cw.getKey());
            if (writer != null) {
               if (super._debugOutputEnabled) {
                  System.err.println(((StringBuffer)(new Object("addSyncObject: "))).append(cw.toString()).toString());
               }

               int res = writer.addWord(cw.getWord(), cw.getFrequency());
               if (res != 0) {
                  Proxy.getInstance().invokeLater(new WordSyncCollection$RecordRemover(this, this, cw));
                  long LOGWORTHY_REPORT_REQUEST = 2888237357036234703L;
                  RIMGlobalMessagePoster.postGlobalEvent(
                     LOGWORTHY_REPORT_REQUEST, 0, 0, ((StringBuffer)(new Object("TID-OTA:"))).append(cw.getRecord()).append("-").append(res).toString(), null
                  );
                  return ret;
               }

               ret = true;
            }
         }
      }

      return ret;
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof CustomWordSyncObject)) {
         return false;
      }

      CustomWordSyncObject entry = (CustomWordSyncObject)object;
      ConverterUtilities.writeByteArray(buffer, 1, entry.getRecord().getBytes());
      ConverterUtilities.convertInt(buffer, 2, entry.getFrequency(), 4);
      return true;
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int UID) {
      try {
         byte[] record = null;
         int freq = 0;
         data.rewind();
         if (ConverterUtilities.findType(data, 1, true)) {
            record = ConverterUtilities.readByteArray(data);
         }

         data.rewind();
         if (ConverterUtilities.findType(data, 2, true)) {
            freq = ConverterUtilities.readInt(data);
         }

         if (record != null) {
            return new CustomWordSyncObject((String)(new Object(record)), freq);
         }
      } finally {
         return null;
      }

      return null;
   }

   public void otaSyncFinishedForTest() {
      this.otaSyncFinished();
   }

   public void clearForTest(String dataName) {
      super._readers.clear();
      super._elements.clear();
      super.clear(dataName);
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (ChCustomWordsSyncCollection)ar.getOrWaitFor(-1037950266182389283L);
      if (_instance == null) {
         _instance = new ChCustomWordsSyncCollection("ChCustomWordsCollection");
         ar.put(-1037950266182389283L, _instance);
      }

      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(6205514177874464825L);
      synchronized (persistentObject) {
         if (persistentObject.getContents() == null) {
            persistentObject.setContents(new Object(), 51);
            persistentObject.commit();
         }
      }

      _instance.setElements((IntHashtable)persistentObject.getContents());
      _instance.setPersistentObject(persistentObject);
      PersistentObject clocksCountStore = RIMPersistentStore.getPersistentObject(-6401963580920179664L);
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
