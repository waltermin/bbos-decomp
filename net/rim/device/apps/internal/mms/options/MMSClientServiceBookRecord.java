package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.TLEUtilities;

class MMSClientServiceBookRecord {
   private ServiceBook _sb;
   private ServiceRecord _sr;
   private IntIntHashtable _integerTable;
   private IntHashtable _stringTable;
   private static final String MMSCLIENTSB_NAME = "MMS Client";
   private static final String MMSCLIENTSB_CID = "MMS";
   private static final String MMSCLIENTSB_UID = "MMS0001";
   public static final int INTEGER_START = 0;
   public static final int STRING_START = 32;
   public static final int RESERVED_START = 64;

   public static MMSClientServiceBookRecord getInstance() {
      return getInstance(true);
   }

   public static MMSClientServiceBookRecord getInstanceNoCreate() {
      return getInstance(false);
   }

   public static MMSClientServiceBookRecord getInstance(boolean createIfMissing) {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid("MMS");
      if (records != null && records.length > 0) {
         return new MMSClientServiceBookRecord(sb, records[0]);
      } else if (!createIfMissing) {
         return null;
      } else {
         ServiceRecord sr = (ServiceRecord)(new Object());
         sr.setType(0);
         sr.setCid("MMS");
         sr.setUid("MMS0001");
         sr.setName("MMS Client");
         sr.setEncryptionMode(1);
         sr.setCompressionMode(1);
         sr = sb.addRecord(sr);
         sb.commit();
         if (sr == null) {
            throw new Object("Failed to add MMS client service book.");
         } else {
            return new MMSClientServiceBookRecord(sb, sr);
         }
      }
   }

   private MMSClientServiceBookRecord(ServiceBook sb, ServiceRecord sr) {
      this._sb = sb;
      this._sr = sr;
      byte[] data = sr.getApplicationData();
      if (data != null) {
         DataBuffer buf = (DataBuffer)(new Object());
         buf.write(data);
         buf.rewind();

         try {
            while (buf.available() > 0) {
               int key = buf.readByte();
               buf.setPosition(buf.getPosition() - 1);
               if (isStringKey(key)) {
                  this.setField(key, TLEUtilities.readStringField(buf, key, true));
               } else {
                  if (!isIntegerKey(key)) {
                     break;
                  }

                  this.setField(key, TLEUtilities.readIntegerField(buf, key));
               }
            }
         } finally {
            return;
         }
      }
   }

   public void save() {
      DataBuffer buf = (DataBuffer)(new Object());
      if (this._integerTable != null) {
         IntEnumeration keys = this._integerTable.keys();

         while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            int value = this._integerTable.get(key);
            TLEUtilities.writeIntegerField(buf, key, value, false);
         }
      }

      if (this._stringTable != null) {
         IntEnumeration keys = this._stringTable.keys();

         while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            String value = (String)this._stringTable.get(key);
            TLEUtilities.writeDataField(buf, key, value.getBytes());
         }
      }

      this._sr.setApplicationData(buf.toArray());
      this._sb.commit();
   }

   public boolean getBooleanField(int key) {
      return this.getBooleanField(key, false);
   }

   public boolean getBooleanField(int key, boolean missing) {
      int value = this.getIntegerField(key);
      return value == -1 ? missing : value == 1;
   }

   public void setField(int key, boolean value) {
      this.setField(key, value ? 1 : 0);
   }

   public int getIntegerField(int key) {
      if (!isIntegerKey(key)) {
         throw new Object();
      } else {
         return this._integerTable == null ? -1 : this._integerTable.get(key);
      }
   }

   public void setField(int key, int value) {
      if (!isIntegerKey(key)) {
         throw new Object();
      }

      if (this._integerTable == null) {
         this._integerTable = (IntIntHashtable)(new Object());
      }

      this._integerTable.put(key, value);
   }

   public String getStringField(int key) {
      if (!isStringKey(key)) {
         throw new Object();
      } else {
         return (String)(this._stringTable == null ? null : this._stringTable.get(key));
      }
   }

   public void setField(int key, String value) {
      if (!isStringKey(key)) {
         throw new Object();
      }

      if (value == null) {
         if (this._stringTable != null && this._stringTable.get(key) != null) {
            this._stringTable.remove(key);
            return;
         }
      } else {
         if (this._stringTable == null) {
            this._stringTable = (IntHashtable)(new Object());
         }

         this._stringTable.put(key, value);
      }
   }

   private static boolean isIntegerKey(int key) {
      return key >= 0 && key < 32;
   }

   private static boolean isStringKey(int key) {
      return key >= 32 && key < 64;
   }
}
