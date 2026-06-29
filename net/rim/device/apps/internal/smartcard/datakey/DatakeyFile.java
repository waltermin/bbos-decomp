package net.rim.device.apps.internal.smartcard.datakey;

class DatakeyFile {
   private DatakeyFileHeader _fileHeader;
   private byte[] _fileContents;

   public DatakeyFile(DatakeyFileHeader fileHeader) {
      this(fileHeader, null);
   }

   public DatakeyFile(DatakeyFileHeader fileHeader, byte[] fileContents) {
      if (fileHeader == null) {
         throw new Object();
      }

      this._fileHeader = fileHeader;
      this._fileContents = fileContents;
   }

   public byte[] getContents() {
      return this._fileContents;
   }

   public void setContents(byte[] fileContents) {
      this._fileContents = fileContents;
   }

   public int getFileType() {
      return this._fileHeader.getFileType();
   }

   public int getHighWaterMark() {
      return this._fileHeader.getHighWaterMark();
   }
}
