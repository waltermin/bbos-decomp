package net.rim.tid.OTAsync;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.tid.data.LearningDataManager;

public class CustomWordSyncObject implements SyncObject, Persistable {
   private String _record;
   private Object _recordEncoding;
   private int _freq;
   private int _type;
   private int _uid = -1;
   private boolean _needImmediateSynch;
   public static final int TYPE_DEFAULT;
   public static final int TYPE_ADDED;
   public static final int TYPE_REMOVED;
   public static final int TYPE_UPDATED;
   private static StringBuffer _buffer = (StringBuffer)(new Object());

   public boolean needImmediateSynch() {
      return this._needImmediateSynch;
   }

   public boolean isUpdated() {
      return this._type == 3;
   }

   public boolean isRemoved() {
      return this._type == 2;
   }

   @Override
   public synchronized int getUID() {
      if (this._uid == -1) {
         String record = this.getRecord();
         if (record != null) {
            this._uid = record.hashCode();
         }
      }

      return this._uid;
   }

   public int getType() {
      return this._type;
   }

   public synchronized String getRecord() {
      String record = this._record;
      if (record == null && this._recordEncoding != null) {
         record = (String)PersistentContent.decode(this._recordEncoding);
      }

      return record;
   }

   public synchronized String getWord() {
      String record = this.getRecord();
      int index = record.indexOf("__");
      return index == -1 ? record : record.substring(0, index);
   }

   public synchronized String getKey() {
      String record = this.getRecord();
      int index = record.indexOf("__");
      return index == -1 ? record : record.substring(index);
   }

   public char getFrequency() {
      return (char)this._freq;
   }

   public synchronized void encode() {
      if (this._record != null) {
         this._recordEncoding = PersistentContent.encode(this._record, false, true);
         this._uid = this._record.hashCode();
         this._record = null;
      }
   }

   public synchronized void reEncode() {
      if (this._recordEncoding != null) {
         this._recordEncoding = PersistentContent.reEncode(this._recordEncoding, false, true);
      }
   }

   public CustomWordSyncObject(String record, int freq) {
      this._record = record;
      this._freq = freq;
      if (PersistentContent.isEncryptionEnabled()) {
         this.encode();
      }
   }

   public CustomWordSyncObject(int uid) {
      this._uid = uid;
   }

   public CustomWordSyncObject(byte[] word, int offset, int length, String localeAndType, int freq, int recordType, boolean immediateSynch) {
      _buffer.setLength(0);
      _buffer.append((String)(new Object(word, offset, length)));
      _buffer.append("__");
      _buffer.append(localeAndType);
      this._record = _buffer.toString();
      this._type = recordType;
      this._freq = freq;
      this._needImmediateSynch = immediateSynch;
      if (PersistentContent.isEncryptionEnabled()) {
         this.encode();
      }
   }

   public CustomWordSyncObject(char[] word, int offset, int length, String locale, byte type, int freq, int recordType, boolean immediateSynch) {
      _buffer.setLength(0);
      _buffer.append(word, offset, length);
      _buffer.append("__");
      _buffer.append(locale);
      _buffer.append("__");
      _buffer.append(LearningDataManager.getTypeName(type));
      this._record = _buffer.toString();
      this._type = recordType;
      this._freq = freq;
      this._needImmediateSynch = immediateSynch;
      if (PersistentContent.isEncryptionEnabled()) {
         this.encode();
      }
   }

   @Override
   public synchronized String toString() {
      String record = this.getRecord();
      StringBuffer buffer;
      if (record != null) {
         buffer = (StringBuffer)(new Object(record));
      } else {
         buffer = (StringBuffer)(new Object(((StringBuffer)(new Object("uid:"))).append(this._uid).toString()));
      }

      buffer.append(" - ");
      switch (this._type) {
         case -1:
            break;
         case 0:
         default:
            buffer.append("default");
            break;
         case 1:
            buffer.append("added");
            break;
         case 2:
            buffer.append("removed");
            break;
         case 3:
            buffer.append("updated");
      }

      return buffer.toString();
   }

   public CustomWordSyncObject(byte[] word, int offset, int length, String locale, byte type, int freq, int recordType, boolean immediateSynch) {
      _buffer.setLength(0);
      _buffer.append((String)(new Object(word, offset, length)));
      _buffer.append("__");
      _buffer.append(locale);
      _buffer.append("__");
      _buffer.append(LearningDataManager.getTypeName(type));
      this._record = _buffer.toString();
      this._type = recordType;
      this._freq = freq;
      this._needImmediateSynch = immediateSynch;
      if (PersistentContent.isEncryptionEnabled()) {
         this.encode();
      }
   }

   public CustomWordSyncObject(char[] word, int offset, int length, String localeAndType, int freq, int recordType) {
      _buffer.setLength(0);
      _buffer.append(word, offset, length);
      _buffer.append(localeAndType);
      this._record = _buffer.toString();
      this._type = recordType;
      this._freq = freq;
      if (PersistentContent.isEncryptionEnabled()) {
         this.encode();
      }
   }
}
