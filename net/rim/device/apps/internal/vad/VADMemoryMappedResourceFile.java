package net.rim.device.apps.internal.vad;

import net.rim.device.internal.vad.VADNatives;

final class VADMemoryMappedResourceFile extends VADResourceFile {
   private int _memoryMapLocation;

   VADMemoryMappedResourceFile(byte[][] data, int type) {
      super(data);
      this._memoryMapLocation = VADNatives.memoryMapData(type, super._data, super._length);
   }

   final int getMemoryMapLocation() {
      return this._memoryMapLocation;
   }
}
