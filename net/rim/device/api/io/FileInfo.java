package net.rim.device.api.io;

public final class FileInfo {
   private long _fileSize;
   private long _lastModified;
   private String _fileName;
   private int _attributes;
   public static final int ATTRIBUTE_ARCHIVE = 1;
   public static final int ATTRIBUTE_READ_ONLY = 2;
   public static final int ATTRIBUTE_SYSTEM = 4;
   public static final int ATTRIBUTE_HIDDEN = 8;
   public static final int ATTRIBUTE_SET_MASK = 15;
   public static final int ATTRIBUTE_DIRECTORY = 16;
   public static final int ATTRIBUTE_VOLUME = 32;

   public FileInfo() {
   }

   public FileInfo(String filename, long fileSize, long lastModified, int attributes) {
      this._fileName = filename;
      this._fileSize = fileSize;
      this._lastModified = lastModified;
      this._attributes = attributes;
   }

   public final String getFileName() {
      return this._fileName;
   }

   public final long getFileSize() {
      return this._fileSize;
   }

   public final long getLastModified() {
      return this._lastModified;
   }

   public final int getAttributes() {
      return this._attributes;
   }
}
