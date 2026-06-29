package net.rim.tid.im.conv.europe.repository;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class LearningHeader {
   protected byte[] _data;
   protected int _headerSize = -1;
   protected int _versionMajor = 1;
   protected int _versionMinor = 1;
   protected int _fileSize;
   protected int _fileSizeOffset = 10;
   protected byte _maxNestingLevel = 2;
   protected int _maxNestingLevelOffset = this._fileSizeOffset + 4;
   protected String _locale;
   protected boolean _trimmable = true;
   protected int _trimmableOffset = this._maxNestingLevelOffset + 1;

   public void init(byte[] aData) {
      this._data = aData;
   }

   public void read(DataInputStream aData) {
      aData.mark(Integer.MAX_VALUE);
      aData.skip(4);
      this.readHelper(aData);
      aData.reset();
      aData.skip(this._headerSize);
   }

   protected void readHelper(DataInputStream aData) {
      this._headerSize = aData.readShort();
      this._versionMajor = aData.readShort();
      this._versionMinor = aData.readShort();
      this._fileSize = aData.readInt();
      this._maxNestingLevel = aData.readByte();
      this._trimmable = aData.readByte() == 1;
      String locale_str = aData.readUTF();
      String lang = this._locale;
      int pos = this._locale.indexOf(95);
      if (pos != -1) {
         lang = this._locale.substring(0, pos);
      }

      if (!locale_str.startsWith(lang)) {
         System.err
            .println(((StringBuffer)(new Object("Warning: locale "))).append(this._locale).append(" uses wordlist of locale ").append(locale_str).toString());
      }
   }

   public void write(DataOutputStream dos, LearningGlobalAlphabet aAlphabet) {
      int signature = 1397507668;
      dos.writeInt(signature);
      if (this._headerSize == -1) {
         this.computeInitialSizes(aAlphabet);
      }

      dos.writeShort(this._headerSize);
      this.writeHelper(dos);
   }

   protected void writeHelper(DataOutputStream aDos) {
      aDos.writeShort(this._versionMajor);
      aDos.writeShort(this._versionMinor);
      aDos.writeInt(this._fileSize);
      aDos.writeByte(this._maxNestingLevel);
      aDos.writeByte(this._trimmable ? 1 : 0);
      aDos.writeUTF(this._locale);
   }

   public void reset(LearningGlobalAlphabet aAlphabet) {
      if (aAlphabet == null) {
         this._headerSize = -1;
      } else {
         this.computeInitialSizes(aAlphabet);
         this._maxNestingLevel = 2;
      }
   }

   protected void computeInitialSizes(LearningGlobalAlphabet aAlphabet) {
      ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object());
      DataOutputStream dos1 = (DataOutputStream)(new Object(baos));
      this.writeHelper(dos1);
      this._headerSize = baos.size() + 6;
      this._fileSize = this._headerSize + aAlphabet.size() + 1;
   }

   public int getHeaderSize() {
      return this._headerSize;
   }

   public int getFileSize() {
      return this._fileSize;
   }

   public void growFileSize(int aVal) {
      this._fileSize += aVal;
   }

   public void update() {
      this._data[this._fileSizeOffset] = (byte)(this._fileSize >> 24);
      this._data[this._fileSizeOffset + 1] = (byte)(this._fileSize >> 16);
      this._data[this._fileSizeOffset + 2] = (byte)(this._fileSize >> 8);
      this._data[this._fileSizeOffset + 3] = (byte)this._fileSize;
   }

   public void updateNestingLevel(int aLevel) {
      this._maxNestingLevel = (byte)Math.max(this._maxNestingLevel, aLevel);
      this._data[this._maxNestingLevelOffset] = this._maxNestingLevel;
   }

   public int getVersionMajor() {
      return this._versionMajor;
   }

   public int getVersionMinor() {
      return this._versionMinor;
   }

   public byte getMaxNestingLevel() {
      return this._maxNestingLevel;
   }

   public String getLocaleStr() {
      return this._locale;
   }

   public void setLocaleStr(String aLocale) {
      this._locale = aLocale;
      this._headerSize = -1;
   }

   public boolean isTrimmable() {
      return this._trimmable;
   }

   public void setTrimmable(boolean aVal) {
      this._trimmable = aVal;
      if (this._data != null) {
         this._data[this._trimmableOffset] = (byte)(this._trimmable ? 1 : 0);
      }
   }

   public byte getTimeStamp(byte aPriority) {
      return 0;
   }

   public byte getTimeStamp(boolean aAdvance) {
      return 0;
   }

   public int getLeastTimeStamp() {
      return 0;
   }

   public boolean isChanged() {
      return this._data[this._fileSizeOffset + 3] != (byte)this._fileSize;
   }
}
