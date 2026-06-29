package net.rim.device.apps.internal.lbs;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.lbs.content.SingleLocationDocumentConverter;

final class LocationSyncObject implements LocationSyncable, Persistable {
   private String _label;
   private SimpleFolder _folderHeirarchy;
   private int _uid;
   private byte[] _data;

   @Override
   public final void setData(String label, byte[] data) {
      this._label = label;
      this._data = data;
   }

   @Override
   public final boolean load(DataBuffer buffer, int version) {
      try {
         if (ConverterUtilities.findType(buffer, 1)) {
            this._data = ConverterUtilities.readByteArray(buffer);
            SingleLocationDocumentConverter conv = SingleLocationDocumentConverter.getInstance();
            Location loc = conv.getLocation(this);
            this._label = loc._label;
            String foldersString = loc._folderHierarchy;
            if (foldersString == null) {
               foldersString = "";
            }

            this._folderHeirarchy = FavouritesManager.createFolderHeirarchies(foldersString);
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   @Override
   public final SimpleFolder getFolderHeirarchies() {
      return this._folderHeirarchy;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   @Override
   public final boolean save(DataBuffer data, int version) {
      ConverterUtilities.writeByteArray(data, 1, this._data);
      return true;
   }

   @Override
   public final String getLabel() {
      return this._label;
   }

   @Override
   public final byte[] getData() {
      return this._data;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      keyArray[index] = this._uid;
      return 1;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      keyArray[index] = this._uid;
      return 1;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getType() {
      return 1;
   }

   LocationSyncObject(int uid, String label, byte[] data, SimpleFolder folderHeirarchy) {
      this._label = label;
      this._uid = uid;
      this._data = data;
      if (!(folderHeirarchy instanceof FavouritesSimpleFolder)) {
         folderHeirarchy = FavouritesManager.getRootFolder();
      }

      this._folderHeirarchy = folderHeirarchy;
   }

   LocationSyncObject(int uid) {
      this._uid = uid;
   }

   @Override
   public final String toString() {
      return this._label;
   }

   @Override
   public final int hashCode() {
      return this._uid;
   }
}
