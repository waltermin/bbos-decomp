package net.rim.tid.data;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;

public class LearningDataManager {
   private final String[] ENCODE_RESTRICTED_DATA_TYPES = new String[]{"opt"};
   private PersistentObject _persistentObject;
   private StringBuffer _buffer = (StringBuffer)(new Object());
   private Hashtable _learningRecords;
   private PersistentObject _stateStore;
   private Boolean _isDataEncoded;
   private Hashtable _learningDataTable = (Hashtable)(new Object());
   public static final byte DEFAULT_TYPE = 0;
   public static final byte LEARNING_TYPE = 1;
   public static final byte IMOPTIONS_TYPE = 2;
   public static final byte FREQUENCY_TYPE = 3;
   public static final byte SPELL_CHECK_LEARNING_TYPE = 5;
   public static final byte SPELL_CHECK_PAIR_LEARNING_TYPE = 6;
   public static final String DEFAULT_TYPE_NAME = "def";
   public static final String LEARNING_TYPE_NAME = "lrn";
   public static final String IMOPTIONS_TYPE_NAME = "opt";
   public static final String FREQUENCY_TYPE_NAME = "frq";
   public static final String SPELL_CHECK_LEARNING_TYPE_NAME = "spl";
   public static final String SPELL_CHECK_PAIR_LEARNING_TYPE_NAME = "spp";
   public static final String SEPARATOR = "__";
   private static final long LEARNING_DATA_MANAGER_RECORD_STORE_ID = -8960920648966008846L;
   private static final long LEARNING_DATA_MANAGER_STATE_STORE_ID = 1711008448817031823L;
   private static final long REGISTRY_ID = 6388244635521793558L;
   private static LearningDataManager _instance;

   private LearningDataManager() {
      this._persistentObject = RIMPersistentStore.getPersistentObject(-8960920648966008846L);
      if (this._persistentObject.getContents() == null) {
         this._persistentObject.setContents(new Object(), 51);
         this._persistentObject.commit();
      }

      this._learningRecords = (Hashtable)this._persistentObject.getContents();
      this._stateStore = RIMPersistentStore.getPersistentObject(1711008448817031823L);
      if (this._stateStore.getContents() == null) {
         this._stateStore.setContents(Boolean.FALSE, 51);
         this._stateStore.commit();
      }

      this._isDataEncoded = (Boolean)this._stateStore.getContents();
   }

   public static void libMain(String[] args) {
      initialize();
   }

   private static void initialize() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (LearningDataManager)ar.getOrWaitFor(6388244635521793558L);
      if (_instance == null) {
         _instance = new LearningDataManager();
         ar.put(6388244635521793558L, _instance);
      }
   }

   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 9206737719270818227L) {
         if (!PersistentContent.isEncryptionEnabled()) {
            if (this._isDataEncoded) {
               decodeData();
            }

            Enumeration values = this._learningDataTable.elements();
            Enumeration keys = this._learningDataTable.keys();
            boolean commit = false;

            while (values.hasMoreElements()) {
               LearningData ld = (LearningData)values.nextElement();
               String key = (String)keys.nextElement();
               if (ld.getEncodedData() != null) {
                  this._learningRecords.put(key, ld.getData());
                  commit = true;
                  ld.setEncodedData(null);
               }
            }

            if (commit) {
               commit();
               return;
            }
         } else {
            this.encodeData(false);
         }
      }
   }

   public static Object getLock() {
      return _instance._persistentObject;
   }

   public static boolean isDataLocked() {
      return _instance._isDataEncoded;
   }

   public static Enumeration getStoredRecordsNames() {
      return _instance._learningRecords.keys();
   }

   public static LearningData getLearningData(String learningDataName) {
      return _instance.getLearningData0(learningDataName);
   }

   public synchronized LearningData getLearningData0(String learningDataName) {
      LearningData res = (LearningData)this._learningDataTable.get(learningDataName);
      if (res == null) {
         Object data = this._learningRecords.get(learningDataName);
         if (data != null) {
            Object encodedData = null;
            if (this._isDataEncoded && this.isEncodeEnabledDataType(learningDataName)) {
               encodedData = data;
               data = PersistentContent.decode(data);
            }

            res = new LearningData((byte[])data, encodedData);
            this._learningDataTable.put(learningDataName, res);
         }
      }

      return res;
   }

   private boolean isEncodeEnabledDataType(String dataName) {
      int separatorIndex = dataName.indexOf("__");

      int nextIndex;
      while ((nextIndex = dataName.indexOf("__", separatorIndex + 1)) != -1) {
         separatorIndex = nextIndex;
      }

      int suffixIndex = separatorIndex + 2;

      for (int i = 0; i < this.ENCODE_RESTRICTED_DATA_TYPES.length; i++) {
         if (dataName.length() - suffixIndex == this.ENCODE_RESTRICTED_DATA_TYPES[i].length() && dataName.endsWith(this.ENCODE_RESTRICTED_DATA_TYPES[i])) {
            return false;
         }
      }

      return true;
   }

   public static void setLearningData(String learningDataName, LearningData record) {
      _instance.setLearningData0(learningDataName, record, true);
   }

   public static void setLearningData(String learningDataName, LearningData record, boolean forceCommit) {
      _instance.setLearningData0(learningDataName, record, forceCommit);
   }

   private synchronized void setLearningData0(String learningDataName, LearningData record, boolean forceCommit) {
      if (record != null) {
         this._learningRecords.put(learningDataName, record.getData());
         this._learningDataTable.put(learningDataName, record);
         if (forceCommit) {
            PersistentObject.commit(this._learningRecords);
            this._stateStore.commit();
         }
      }
   }

   public static void commit() {
      _instance.commit0();
   }

   private void commit0() {
      this._persistentObject.commit();
      this._stateStore.commit();
   }

   public static byte getType(String name) {
      if (name.endsWith("frq")) {
         return 3;
      } else if (name.endsWith("lrn")) {
         return 1;
      } else if (name.endsWith("spp")) {
         return 6;
      } else if (name.endsWith("spl")) {
         return 5;
      } else if (name.endsWith("opt")) {
         return 2;
      } else {
         return (byte)(name.endsWith("def") ? 0 : -1);
      }
   }

   public static String getTypeName(byte dataType) {
      String res = null;
      switch (dataType) {
         case 0:
            res = "def";
         case -1:
         case 4:
            return res;
         case 1:
            return "lrn";
         case 2:
            return "opt";
         case 3:
         default:
            return "frq";
         case 5:
            return "spl";
         case 6:
            return "spp";
      }
   }

   public static String constructKey(String name, String locale, byte dataType) {
      return _instance.constructKey0(name, locale, dataType);
   }

   private synchronized String constructKey0(String name, String locale, byte dataType) {
      this._buffer.setLength(0);
      if (name != null) {
         this._buffer.append(name);
      }

      this._buffer.append("__");
      this._buffer.append(locale != null ? locale : "def");
      this._buffer.append("__");
      this._buffer.append(getTypeName(dataType));
      return this._buffer.toString();
   }

   public static void encodeData() {
      _instance.encodeData(true);
   }

   private synchronized void encodeData(boolean clean) {
   }

   public static void decodeData() {
      _instance.decodeData0();
   }

   private synchronized void decodeData0() {
   }

   public static void reencodeData() {
      _instance.reencodeData0();
   }

   private synchronized void reencodeData0() {
   }

   static {
      initialize();
   }
}
