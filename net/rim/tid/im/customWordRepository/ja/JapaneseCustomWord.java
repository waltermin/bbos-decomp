package net.rim.tid.im.customWordRepository.ja;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;

public class JapaneseCustomWord implements Persistable, SyncObject {
   private Object _reading;
   private Object _candidate;
   private int _partOfSpeech;
   private static final int READING_ID = 1;
   private static final int CANDIDATE_ID = 2;
   private static final int POS_ID = 3;
   public static final int AWNN_POS_MEISI = 0;
   public static final int AWNN_POS_JINMEI = 1;
   public static final int AWNN_POS_CHIMEI = 2;
   public static final int AWNN_POS_KIGOU = 3;

   public String getCandidate() {
      return PersistentContent.decodeString(this._candidate);
   }

   public int getPartOfSpeech() {
      return this._partOfSpeech;
   }

   public String getReading() {
      return PersistentContent.decodeString(this._reading);
   }

   @Override
   public int getUID() {
      return this.hashCode();
   }

   public synchronized void setPartOfSpeech(int partOfSpeech) {
      this._partOfSpeech = partOfSpeech;
   }

   public synchronized void setReading(String reading) {
      this._reading = PersistentContent.encode(reading);
   }

   public synchronized void setCandidate(String candidate) {
      this._candidate = PersistentContent.encode(candidate);
   }

   public void reEncode() {
      this._reading = PersistentContent.reEncode(this._reading);
      this._candidate = PersistentContent.reEncode(this._candidate);
   }

   public synchronized boolean write(DataBuffer buffer, int version) {
      ConverterUtilities.writeStringSmart(buffer, 1, this.getReading());
      ConverterUtilities.writeStringSmart(buffer, 2, this.getCandidate());
      ConverterUtilities.writeInt(buffer, 3, this._partOfSpeech);
      return true;
   }

   public JapaneseCustomWord(DataBuffer data, int version) {
      this.read(data, version);
   }

   @Override
   public String toString() {
      return this.getCandidate();
   }

   public JapaneseCustomWord(String reading, String candidate, int partOfSpeech) {
      this.setReading(reading);
      this.setCandidate(candidate);
      this._partOfSpeech = partOfSpeech;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof JapaneseCustomWord)) {
         return false;
      }

      JapaneseCustomWord word = (JapaneseCustomWord)obj;
      return this.getReading().equals(word.getReading()) && this.getCandidate().equals(word.getCandidate()) && this._partOfSpeech == word.getPartOfSpeech();
   }

   @Override
   public synchronized int hashCode() {
      return this.getReading().hashCode() + this.getCandidate().hashCode() + this._partOfSpeech;
   }

   private void read(DataBuffer data, int version) {
      data.rewind();
      if (ConverterUtilities.findType(data, 1, true)) {
         this.setReading(ConverterUtilities.readString(data, true));
      }

      data.rewind();
      if (ConverterUtilities.findType(data, 2, true)) {
         this.setCandidate(ConverterUtilities.readString(data, true));
      }

      data.rewind();
      if (ConverterUtilities.findType(data, 3, true)) {
         this._partOfSpeech = ConverterUtilities.readInt(data, true);
      }
   }
}
