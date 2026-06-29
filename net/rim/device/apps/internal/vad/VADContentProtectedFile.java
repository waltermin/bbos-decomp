package net.rim.device.apps.internal.vad;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.internal.vad.VADNatives;

final class VADContentProtectedFile extends VADPersistentFile {
   private byte[][] _plaintext;
   private static final boolean DEBUG_TIMINGS = false;

   VADContentProtectedFile(VADEngineManager manager, int handle) {
      super(manager, handle);
      super._length = PersistentContent.getLength(super._data[0]);
      if (this._plaintext == null) {
         this._plaintext = new byte[1][];
      }
   }

   @Override
   final void reset() {
      super.reset();
      if (this._plaintext == null) {
         this._plaintext = new byte[1][];
      }

      this._plaintext[0] = new byte[0];
   }

   @Override
   final boolean open(String name) {
      if (!super.open(name)) {
         return false;
      }

      byte[] data = PersistentContent.decodeByteArray(super._data[0]);
      if (data == null || data.length == 0) {
         data = new byte[0];
      }

      this._plaintext[0] = data;
      return true;
   }

   @Override
   final void close() {
      super.close();
      this._plaintext[0] = null;
   }

   @Override
   final void commit() {
      boolean encrypt = VADEngineManager.getEncryptFlag();
      super._data[0] = PersistentContent.encode(this._plaintext[0], encrypt, encrypt);
      super.commit();
   }

   @Override
   final void write(byte[] newData, int offset) {
      super._length = VADDataFile.writeToBuffer(newData, offset, this._plaintext[0]);
      super._dirty = true;
   }

   @Override
   final void read(int location, int offset, int count) {
      VADNatives.writeData(location, this._plaintext, offset, count);
   }

   final void reCrypt(boolean encrypt) {
      super._data[0] = PersistentContent.reEncode(super._data[0], encrypt, encrypt);
   }
}
