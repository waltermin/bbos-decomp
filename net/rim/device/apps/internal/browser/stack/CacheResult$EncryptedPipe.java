package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.vm.Memory;
import net.rim.vm.Persistable;

final class CacheResult$EncryptedPipe implements Persistable {
   private Object[] _data;

   public CacheResult$EncryptedPipe(byte[][] data) {
      if (data != null) {
         int dataLength = data.length;
         this._data = new Object[dataLength];

         for (int i = 0; i < dataLength; i++) {
            this._data[i] = PersistentContent.encode(data[i], true, true);
            Memory.createGroup(this._data[i]);
         }

         Memory.createGroup(this._data);
      }
   }

   public final Pipe getDecryptedPipe() {
      if (this._data != null && this._data.length != 0) {
         int dataLength = this._data.length;
         if (dataLength == 1) {
            byte[] data = PersistentContent.decodeByteArray(this._data[0]);
            return new Pipe(data, data.length, false);
         }

         byte[][] data = new byte[dataLength][];

         for (int i = 0; i < dataLength; i++) {
            data[i] = PersistentContent.decodeByteArray(this._data[i]);
         }

         return new Pipe(data, false);
      } else {
         return new Pipe();
      }
   }

   public final int getLength() {
      if (this._data != null && this._data.length != 0) {
         int dataLength = this._data.length;
         if (dataLength == 1) {
            return PersistentContent.getLength(this._data[0]);
         }

         int totalLength = 0;

         for (int i = 0; i < dataLength; i++) {
            totalLength += PersistentContent.getLength(this._data[i]);
         }

         return totalLength;
      } else {
         return 0;
      }
   }
}
