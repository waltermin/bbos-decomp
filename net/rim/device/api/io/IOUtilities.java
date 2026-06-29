package net.rim.device.api.io;

import java.io.InputStream;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.util.StringUtilitiesInternal;
import net.rim.vm.Array;

public class IOUtilities {
   private IOUtilities() {
   }

   public static String getBindName(String name) {
      int length = name.length();
      StringBuffer buffer = StringUtilitiesInternal.getScratchBuffer();
      synchronized (buffer) {
         for (int lv = 0; lv < length; lv++) {
            buffer.append(CharacterUtilities.toLowerCase(CharacterUtilities.getOriginal(name.charAt(lv)), 1701707776));
         }

         String result = buffer.toString();
         buffer.setLength(0);
         return result;
      }
   }

   public static byte[] streamToBytes(InputStream stream) {
      return streamToBytes(stream, 1024);
   }

   public static byte[] streamToBytes(InputStream stream, int increment) {
      if (increment <= 0) {
         throw new IllegalArgumentException("Increment must be positive");
      }

      if (stream == null) {
         return null;
      }

      int available = stream.available();
      int offset = 0;
      byte[] bytes = new byte[Math.max(available, 256)];

      while (true) {
         int length = bytes.length - offset;
         if (length == 0) {
            length = increment;
            Array.resize(bytes, bytes.length + length);
         }

         int read = stream.read(bytes, offset, length);
         if (read < 0) {
            Array.resize(bytes, offset);
            return bytes;
         }

         offset += read;
      }
   }
}
