package net.rim.tid.util;

public class PinInfo {
   private String _word = "";
   private char[] _syllables = new char[8];
   private byte[] _tones = new byte[8];
   private byte[] _indexes = new byte[8];
   private int _length;
   private static final int MAX_LENGTH = 8;

   public PinInfo() {
   }

   public PinInfo(PinInfo info) {
      this.set(info);
   }

   public void getRecord(StringBuffer result) {
      for (int i = 0; i < this._length; i++) {
         result.append(this._syllables[i]);
         result.append((char)this._tones[i]);
         result.append(this._word.charAt(i));
      }
   }

   public int length() {
      return this._length;
   }

   public void setWord(String word) {
      this._word = word;
      this._length = word.length();
   }

   public String getWord() {
      return this._word;
   }

   public char[] getPinIds() {
      return this._syllables;
   }

   public byte[] getTones() {
      return this._tones;
   }

   public byte[] getCharsIndexes() {
      return this._indexes;
   }

   public void set(PinInfo info) {
      this._word = info._word;
      this._length = info._length;
      System.arraycopy(info._syllables, 0, this._syllables, 0, this._length);
      System.arraycopy(info._tones, 0, this._tones, 0, this._length);
      System.arraycopy(info._indexes, 0, this._indexes, 0, this._length);
   }

   public void setSingle(PinInfo info, int position) {
      this._length = 1;
      System.arraycopy(info._syllables, position, this._syllables, 0, this._length);
      System.arraycopy(info._tones, position, this._tones, 0, this._length);
      System.arraycopy(info._indexes, position, this._indexes, 0, this._length);
   }

   public void set(char[] syllables, byte[] tones, byte[] indexes, int length) {
      this._length = length;
      System.arraycopy(syllables, 0, this._syllables, 0, this._length);
      System.arraycopy(tones, 0, this._tones, 0, this._length);
      System.arraycopy(indexes, 0, this._indexes, 0, this._length);
   }

   public void setFR(char[] syllables, byte[] tones, byte[] indexes, int length) {
      System.arraycopy(syllables, 0, this._syllables, this._length, length);
      System.arraycopy(tones, 0, this._tones, this._length, length);
      System.arraycopy(indexes, 0, this._indexes, this._length, length);
      this._length += length;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof PinInfo)) {
         return false;
      }

      PinInfo info = (PinInfo)obj;
      return info._word.equals(this._word);
   }
}
