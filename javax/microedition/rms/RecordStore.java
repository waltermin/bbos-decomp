package javax.microedition.rms;

import net.rim.device.internal.rms.RecordStoreData;

public class RecordStore {
   RecordStoreData _recordStoreData;
   RecordEventGenerator _eventGenerator;
   private int _openCount;
   public static final int AUTHMODE_PRIVATE = 0;
   public static final int AUTHMODE_ANY = 1;
   static final int AUTHMODE_ANY_RO = 2;

   RecordStore(RecordStoreData recordStoreData) {
      this._recordStoreData = recordStoreData;
      this._eventGenerator = new RecordStore$RSRecordEventGenerator(this);
   }

   private static void validateRecordStoreName(String recordStoreName) throws RecordStoreException {
      if (recordStoreName == null) {
         throw new NullPointerException();
      }

      if (recordStoreName.length() > 32) {
         throw new RecordStoreException();
      }
   }

   public static void deleteRecordStore(String recordStoreName) {
      validateRecordStoreName(recordStoreName);
      RecordStoreManager.deleteRecordStore(recordStoreName);
   }

   public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) {
      if (recordStoreName.length() <= 32 && recordStoreName.length() != 0) {
         RecordStore recordStore = RecordStoreManager.getRecordStore(recordStoreName, createIfNecessary);
         synchronized (recordStore) {
            recordStore._openCount++;
            return recordStore;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary, int authmode, boolean writable) {
      RecordStore rs = openRecordStore(recordStoreName, createIfNecessary);
      rs.setMode(authmode, writable);
      return rs;
   }

   public static RecordStore openRecordStore(String recordStoreName, String vendorName, String suiteName) {
      if (vendorName != null && suiteName != null) {
         if (recordStoreName.length() <= 32 && recordStoreName.length() != 0) {
            RecordStore recordStore = RecordStoreManager.getRecordStore(recordStoreName, vendorName, suiteName);
            synchronized (recordStore) {
               if (!RecordStoreManager.checkOwner(recordStore) && recordStore._recordStoreData.getAuthMode() == 0) {
                  throw new SecurityException();
               }

               recordStore._openCount++;
               return recordStore;
            }
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException("vendorName and suiteName must be non null");
      }
   }

   public void setMode(int authmode, boolean writable) {
      synchronized (this) {
         if (!RecordStoreManager.checkOwner(this)) {
            throw new SecurityException();
         }

         if (authmode != 0 && authmode != 1) {
            throw new IllegalArgumentException();
         }

         if (authmode == 1 && !writable) {
            authmode = 2;
         }

         this._recordStoreData.setAuthMode(authmode);
      }
   }

   public void closeRecordStore() {
      this.mustBeOpen();
      synchronized (this) {
         this._openCount--;
         if (this._openCount == 0) {
            this._eventGenerator._listeners = null;
         }
      }
   }

   public static String[] listRecordStores() {
      return RecordStoreManager.getRecordStoreList();
   }

   boolean isOpen() {
      return this._openCount > 0;
   }

   private void mustBeOpen() throws RecordStoreNotOpenException {
      if (!this.isOpen()) {
         throw new RecordStoreNotOpenException(this._recordStoreData.getName());
      }
   }

   public String getName() {
      this.mustBeOpen();
      return this._recordStoreData.getName();
   }

   public int getVersion() {
      this.mustBeOpen();
      return this._recordStoreData.getVersion();
   }

   public int getNumRecords() {
      this.mustBeOpen();
      return this._recordStoreData.getNumRecords();
   }

   public int getSize() {
      this.mustBeOpen();
      return this._recordStoreData.getSize();
   }

   public int getSizeAvailable() {
      this.mustBeOpen();
      return this._recordStoreData.getSizeAvailable();
   }

   public long getLastModified() {
      this.mustBeOpen();
      return this._recordStoreData.getLastModified();
   }

   public int getNextRecordID() {
      this.mustBeOpen();
      return this._recordStoreData.getNextRecordID();
   }

   public int addRecord(byte[] data, int offset, int numBytes) {
      this.mustBeOpen();
      if (!this.checkWritable()) {
         throw new SecurityException();
      }

      int id = this._recordStoreData.addRecord(data, offset, numBytes);
      this._eventGenerator.notifyRecordAdded(this, id);
      return id;
   }

   public void addRecordListener(RecordListener listener) {
      this._eventGenerator.addRecordListener(listener);
   }

   public void removeRecordListener(RecordListener listener) {
      this._eventGenerator.removeRecordListener(listener);
   }

   public void deleteRecord(int recordId) {
      this.mustBeOpen();
      if (!this.checkWritable()) {
         throw new SecurityException();
      }

      this._recordStoreData.deleteRecord(recordId);
      this._eventGenerator.notifyRecordDeleted(this, recordId);
   }

   public int getRecordSize(int recordId) {
      this.mustBeOpen();
      return this._recordStoreData.getRecordSize(recordId);
   }

   public int getRecord(int recordId, byte[] buffer, int offset) {
      this.mustBeOpen();
      return this._recordStoreData.getRecord(recordId, buffer, offset);
   }

   public byte[] getRecord(int recordId) {
      this.mustBeOpen();
      return this._recordStoreData.getRecord(recordId);
   }

   byte[] getRecordReadOnly(int recordId) {
      return this._recordStoreData.getRecordReadOnly(recordId);
   }

   public void setRecord(int recordId, byte[] newData, int offset, int numBytes) {
      this.mustBeOpen();
      if (!this.checkWritable()) {
         throw new SecurityException();
      }

      this._recordStoreData.setRecord(recordId, newData, offset, numBytes);
      this._eventGenerator.notifyRecordChanged(this, recordId);
   }

   synchronized void loadRecordIDs(int[] recordIds) {
      this._recordStoreData.loadRecordIDs(recordIds);
   }

   public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated) {
      this.mustBeOpen();
      if (filter == null && comparator == null) {
         BaseRecordEnumeration enumeration = new AllRecordEnumeration(this, keepUpdated);
         return enumeration;
      }

      RecordEventGenerator enumeration = this._eventGenerator;
      if (filter != null) {
         enumeration = new FilterRecordEnumeration(this, enumeration, filter, keepUpdated);
      }

      if (comparator != null) {
         enumeration = new SortRecordEnumeration(this, enumeration, comparator, keepUpdated);
      }

      return (RecordEnumeration)enumeration;
   }

   private boolean checkWritable() {
      return RecordStoreManager.checkOwner(this) ? true : this._recordStoreData.getAuthMode() == 1;
   }
}
