package net.rim.device.cldc.io.proxyhttp.compression.coders;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.util.StringUtilitiesInternal;

public final class TextCoder implements Coder {
   private static final long ID = -4701931071511368799L;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final String decode(InputStream ins, int firstChar) {
      StringBuffer scratch = StringUtilitiesInternal.getScratchBuffer();
      synchronized (scratch) {
         boolean var11 = false /* VF: Semaphore variable */;

         String var6;
         try {
            var11 = true;
            scratch.setLength(0);
            int currentByte = firstChar;

            while (currentByte != 0) {
               switch (currentByte) {
                  case -1:
                     throw new Object();
                  default:
                     scratch.append((char)currentByte);
                  case 127:
                     currentByte = ins.read();
               }
            }

            var6 = scratch.toString();
            var11 = false;
         } finally {
            if (var11) {
               scratch.setLength(0);
            }
         }

         scratch.setLength(0);
         return var6;
      }
   }

   @Override
   public final String decode(InputStream ins) {
      return this.decode(ins, ins.read());
   }

   @Override
   public final void encode(String decoded, OutputStream outs) {
      int length;
      if (decoded != null && (length = decoded.length()) > 0) {
         int firstChar = decoded.charAt(0);
         if (firstChar <= 31 || firstChar >= 128) {
            outs.write(127);
         }

         for (int i = 0; i < length; i++) {
            outs.write(decoded.charAt(i));
         }
      }

      outs.write(0);
   }

   public static final TextCoder getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      TextCoder coder = (TextCoder)registry.getOrWaitFor(-4701931071511368799L);
      if (coder == null) {
         coder = new TextCoder();
         registry.put(-4701931071511368799L, coder);
      }

      return coder;
   }
}
