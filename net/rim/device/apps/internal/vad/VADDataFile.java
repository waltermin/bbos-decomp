package net.rim.device.apps.internal.vad;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.internal.vad.VADNatives;
import net.rim.vm.Array;

class VADDataFile extends VADFile {
   protected Object[] _data;
   protected int _length;

   VADDataFile(Object[] data) {
      if (data != null && data[0] != null) {
         this._data = data;

         for (int i = data.length - 1; i >= 0; i--) {
            this._length = this._length + PersistentContent.getLength(data[i]);
         }
      } else {
         this.reset();
      }
   }

   void reset() {
      this._data = new Object[1];
      this._data[0] = new byte[0];
      this._length = 0;
   }

   @Override
   boolean open(String name) {
      return true;
   }

   @Override
   void close() {
   }

   @Override
   boolean exists() {
      return this._length != 0;
   }

   @Override
   int size() {
      return this._length;
   }

   protected static int writeToBuffer(byte[] data, int offset, byte[] buffer) {
      int dataLength = data.length;
      int bufferLength = buffer.length;
      if (offset + dataLength >= bufferLength) {
         Array.resize(buffer, offset + dataLength + 1);
      }

      System.arraycopy(data, 0, buffer, offset, dataLength);
      return buffer.length;
   }

   @Override
   void write(byte[] data, int offset) {
      this._length = writeToBuffer(data, offset, (byte[])this._data[0]);
   }

   @Override
   void read(int location, int offset, int count) {
      VADNatives.writeData(location, this._data, offset, count);
   }

   @Override
   void delete() {
      this.reset();
   }

   @Override
   void copy(VADFile file) {
      if (file instanceof VADDataFile) {
         VADDataFile f = (VADDataFile)file;
         this._data = f._data;
         this._length = f._length;
      }
   }
}
