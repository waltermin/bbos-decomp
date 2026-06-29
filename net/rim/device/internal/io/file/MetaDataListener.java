package net.rim.device.internal.io.file;

public interface MetaDataListener {
   void metaDataFileUnavailable(String var1);

   void metaDataDeleted(String var1);

   void metaDataAdded(MetaDataFileInfo var1);
}
