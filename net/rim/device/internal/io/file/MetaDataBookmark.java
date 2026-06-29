package net.rim.device.internal.io.file;

public class MetaDataBookmark {
   private String _folderURL;
   private String _filename;
   private long _position;

   public MetaDataBookmark(String fileURL) {
      this._filename = FileUtilities.getName(fileURL);
      this._folderURL = fileURL.substring(0, fileURL.length() - this._filename.length());
   }

   public MetaDataBookmark(String folderURL, String filename) {
      this._folderURL = folderURL;
      this._filename = filename;
   }

   public long getPosition() {
      return this._position;
   }

   public void setPosition(long position) {
      this._position = position;
   }

   public boolean load() {
      MetaDataFile dataFile = MetaDataFile.getOrCreate(this._folderURL);
      if (dataFile != null) {
         Object[] metadata = dataFile.loadMetaData(this._filename);
         if (metadata != null && metadata[7] instanceof Long) {
            this._position = (Long)metadata[7];
            return true;
         }
      }

      return false;
   }

   public boolean save() {
      MetaDataFile dataFile = MetaDataFile.getOrCreate(this._folderURL);
      if (dataFile != null) {
         Object[] metaData = dataFile.saveMetaDataBookmark(this._filename, this._position);
         if (metaData != null) {
            FileIndexService indexService = FileIndexService.getService();
            if (indexService != null) {
               indexService.notifyMetaDataListeners(2, this._folderURL, this._filename, metaData);
            }

            return true;
         }
      }

      return false;
   }
}
