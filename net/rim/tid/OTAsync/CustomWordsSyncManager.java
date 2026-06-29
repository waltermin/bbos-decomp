package net.rim.tid.OTAsync;

import java.util.Vector;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.StringUtilities;

public class CustomWordsSyncManager {
   private Vector _entries = (Vector)(new Object());
   private int _type;
   private String _localeAndType;
   private StringBuffer _buffer = (StringBuffer)(new Object());

   public CustomWordsSyncManager(int type) {
      this._type = type;
   }

   public CustomWordsSyncManager() {
      this(0);
   }

   public void addWord(byte[] word, int offset, int length, int freq, String locale, byte type) {
      this._entries.addElement(new CustomWordSyncObject(word, offset, length, locale, type, freq, this._type, false));
   }

   public void addWord(byte[] word, int offset, int length, int freq, String localeAndType) {
      this._entries.addElement(new CustomWordSyncObject(word, offset, length, localeAndType, freq, this._type, false));
   }

   public void addWord(char[] word, int offset, int length, int freq, String locale, byte type) {
      this._entries.addElement(new CustomWordSyncObject(word, offset, length, locale, type, freq, this._type, false));
   }

   public void addWord(char[] word, int offset, int length, int freq) {
      this._entries.addElement(new CustomWordSyncObject(word, offset, length, this._localeAndType, freq, this._type));
   }

   public void addWord(char[] word, int offset, int length) {
      this._buffer.setLength(0);
      this._buffer.append(word, offset, length);
      this._buffer.append(this._localeAndType);
      this._entries.addElement(new CustomWordSyncObject(StringUtilities.computeHashCode(this._buffer)));
   }

   public void setLocaleAndType(String lat) {
      this._localeAndType = lat;
   }

   public int size() {
      return this._entries.size();
   }

   public Vector elements() {
      return this._entries;
   }

   public SyncObject[] getSyncObjects() {
      SyncObject[] res = new Object[this._entries.size()];
      if (this._entries.size() > 0) {
         this._entries.copyInto(res);
      }

      return res;
   }
}
