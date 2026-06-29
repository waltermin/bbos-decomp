package net.rim.device.apps.internal.vad;

class VADResourceFile extends VADDataFile {
   VADResourceFile(byte[][][] data) {
      super(data);
   }

   @Override
   void write(byte[] data, int offset) {
      throw new Object("read-only file");
   }

   @Override
   void delete() {
      throw new Object("read-only file");
   }

   byte[] getData() {
      byte[] data = new byte[super._length];
      int numSections = super._data.length;
      int offset = 0;

      for (int i = 0; i < numSections; i++) {
         byte[] b = (byte[])super._data[i];
         System.arraycopy(b, 0, data, offset, b.length);
         offset += b.length;
      }

      return data;
   }
}
