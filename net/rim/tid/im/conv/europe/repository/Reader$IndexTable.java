package net.rim.tid.im.conv.europe.repository;

import java.io.DataInputStream;

final class Reader$IndexTable {
   int[] offsets;
   short[] ids;

   public final void read(DataInputStream aDis) {
      int length = aDis.readShort();
      this.offsets = new int[length];
      this.ids = new short[length];

      for (int i = 0; i < length; i++) {
         this.ids[i] = aDis.readShort();
         this.offsets[i] = aDis.readInt();
      }
   }

   public final int getTableOffset(int id) {
      return this.offsets[id];
   }
}
