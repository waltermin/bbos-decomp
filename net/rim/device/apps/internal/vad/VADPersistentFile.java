package net.rim.device.apps.internal.vad;

class VADPersistentFile extends VADDataFile {
   protected boolean _dirty;
   protected VADEngineManager _manager;
   protected int _handle;

   VADPersistentFile(VADEngineManager manager, int handle) {
      super(manager.getFileData(handle));
      this._manager = manager;
      this._handle = handle;
   }

   void commit() {
      this._manager.writeFileData(this._handle, super._data);
      this._dirty = false;
   }

   @Override
   void close() {
      if (this._dirty) {
         this.commit();
      }
   }

   @Override
   void write(byte[] data, int offset) {
      super.write(data, offset);
      this._dirty = true;
   }

   @Override
   void delete() {
      super.delete();
      this.commit();
   }

   @Override
   void copy(VADFile file) {
      super.copy(file);
      this._dirty = true;
   }
}
