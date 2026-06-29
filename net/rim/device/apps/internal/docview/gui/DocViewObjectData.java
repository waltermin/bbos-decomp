package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.vm.Array;

class DocViewObjectData {
   private IntHashtable _contentsHash;
   private byte[] _contents;
   private int _id = -1;

   protected final void addData(byte[] data) {
      if (data != null && data.length > 0) {
         if (this._contents == null) {
            this._contents = data;
            return;
         }

         addBytes(this._contents, data);
      }
   }

   protected final byte[] getContents() {
      this.incrementalAddData();
      byte[] retValue = null;
      if (this._contentsHash != null && !this._contentsHash.isEmpty()) {
         int size = this._contentsHash.size();
         int[] keys = new int[size];
         this._contentsHash.keysToArray(keys);
         Arrays.sort(keys, 0, size);
         retValue = new byte[0];

         for (int i = 0; i < keys.length; i++) {
            addBytes(retValue, (byte[])this._contentsHash.get(keys[i]));
         }

         Object var5 = null;
      }

      return retValue;
   }

   void setID(int chunkIndex) {
      if (chunkIndex < 0) {
         throw new Object("Chunk indeces should be positive numbers!");
      }

      this._id = chunkIndex;
   }

   void endParsing() {
      if (this._id != -1) {
         this.incrementalAddData();
         this._id = -1;
      }
   }

   private void incrementalAddData() {
      if (this._id != -1) {
         if (this._contents != null && this._contents.length > 0) {
            if (this._contentsHash == null) {
               this._contentsHash = (IntHashtable)(new Object(1));
            }

            if (this._contentsHash.containsKey(this._id)) {
               addBytes((byte[])this._contentsHash.get(this._id), this._contents);
            } else {
               this._contentsHash.put(this._id, this._contents);
            }
         }

         this._contents = null;
      }
   }

   private static final void addBytes(byte[] array, byte[] add) {
      int crtSize = array.length;

      try {
         Array.resize(array, crtSize + add.length);
         System.arraycopy(add, 0, array, crtSize, add.length);
      } finally {
         return;
      }
   }
}
