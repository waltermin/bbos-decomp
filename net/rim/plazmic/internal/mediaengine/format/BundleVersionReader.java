package net.rim.plazmic.internal.mediaengine.format;

import net.rim.plazmic.internal.mediaengine.io.FormatVersionReader;

public class BundleVersionReader implements FormatVersionReader {
   private byte[] bundleData;
   private int bundleOffset = 0;
   static final int SUPPORTED_MAJOR1_VERSION;
   static final int SUPPORTED_MAJOR2_VERSION;
   static final int SUPPORTED_MINOR1_VERSION;
   static final int BUNDLE_START_HEADER;

   @Override
   public synchronized String getVersion(byte[] header, int offset) {
      this.bundleData = header;
      this.bundleOffset = offset;
      if (this.readInt() != -548385470) {
         throw new Object(3);
      } else {
         int major1Version = this.readUnsignedByte();
         int major2Version = this.readUnsignedByte();
         int minor1Version = this.readUnsignedByte();
         int minor2Version = this.readUnsignedByte();
         minor2Version = minor2Version;
         if (major1Version > 0 || major2Version > 1 || minor1Version > 6) {
            throw new Object(1);
         } else if (major1Version >= 0 && major2Version >= 1) {
            return ((StringBuffer)(new Object()))
               .append(major1Version)
               .append('.')
               .append(major2Version)
               .append('.')
               .append(minor2Version)
               .append('.')
               .append(minor1Version)
               .toString();
         } else {
            throw new Object(2);
         }
      }
   }

   private int readInt() {
      int ch1 = this.readUnsignedByte();
      int ch2 = this.readUnsignedByte();
      int ch3 = this.readUnsignedByte();
      int ch4 = this.readUnsignedByte();
      return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
   }

   private int readUnsignedByte() {
      if (this.bundleData.length - this.bundleOffset < 1) {
         throw new Object(4);
      } else {
         return this.bundleData[this.bundleOffset++] & 0xFF;
      }
   }
}
