package net.rim.plazmic.internal.mediaengine.io;

import net.rim.plazmic.mediaengine.MediaException;

public class PMEVersionReader implements FormatVersionReader {
   private byte[] data;
   private int offset = 0;
   public static final int PME_START_HEADER = -749712059;

   protected final int readInt() {
      if (this.data.length - this.offset < 4) {
         throw new Object();
      }

      int ch1 = this.data[this.offset++];
      int ch2 = this.data[this.offset++];
      int ch3 = this.data[this.offset++];
      int ch4 = this.data[this.offset++];
      return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized String getVersion(byte[] data, int offset) {
      int version = -1;
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         try {
            var10 = true;
            this.data = data;
            this.offset = offset;
            if (this.readInt() != -749712059) {
               throw new MediaException(3);
            }

            version = this.readInt();
            this.readInt();
            var10 = false;
         } finally {
            if (var10) {
               throw new MediaException(4);
            }
         }
      } finally {
         byte[] var13 = null;
      }

      return ((StringBuffer)(new Object()))
         .append((version & 0xFF000000) >>> 24)
         .append('.')
         .append((version & 0xFF0000) >>> 16)
         .append('.')
         .append((version & 0xFF00) >>> 8)
         .append('.')
         .append(version & 0xFF)
         .toString();
   }
}
