package net.rim.device.internal.io.file;

public class MetaDataFileInfo {
   private String _fileName;
   private String _path;
   private Object[] _metaData;

   MetaDataFileInfo() {
   }

   void setFileName(String fileName) {
      this._fileName = fileName;
   }

   void setPath(String path) {
      this._path = path;
   }

   void setMetaData(Object[] metaData) {
      this._metaData = metaData;
   }

   public String getFileName() {
      return this._fileName;
   }

   public String getPath() {
      return this._path;
   }

   public Object getMetaData(int metaDataType) {
      if (this._metaData == null) {
         MetaDataFile db = MetaDataFile.getOrCreate(this._path);
         if (db != null) {
            this._metaData = db.getOrCreateMetadata(this._fileName, false);
         }
      }

      return this._metaData != null && 0 <= metaDataType && metaDataType < this._metaData.length ? this._metaData[metaDataType] : null;
   }
}
