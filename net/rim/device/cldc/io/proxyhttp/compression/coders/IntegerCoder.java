package net.rim.device.cldc.io.proxyhttp.compression.coders;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.system.ApplicationRegistry;

public final class IntegerCoder implements Coder {
   private static final long ID = 5227182672208802877L;

   public final void encode(int decoded, OutputStream outs) {
      if (decoded >= 0 && decoded <= 127) {
         decoded += 128;
         outs.write(decoded);
      } else if (decoded >= 0 && decoded <= 255) {
         outs.write(1);
         outs.write(decoded);
      } else if (decoded >= 0 && decoded <= 65535) {
         outs.write(2);
         outs.write(decoded >> 8);
         outs.write(decoded);
      } else if (decoded >= 0 && decoded <= 16777215) {
         outs.write(3);
         outs.write(decoded >> 16);
         outs.write(decoded >> 8);
         outs.write(decoded);
      } else {
         outs.write(4);
         outs.write(decoded >> 24);
         outs.write(decoded >> 16);
         outs.write(decoded >> 8);
         outs.write(decoded);
      }
   }

   @Override
   public final void encode(String decoded, OutputStream outs) {
      try {
         int i = Integer.parseInt(decoded);
         this.encode(i, outs);
      } finally {
         TextCoder.getInstance().encode(decoded, outs);
         return;
      }
   }

   @Override
   public final String decode(InputStream ins) {
      int currentByte = ins.read();
      if (currentByte == -1) {
         throw new Object();
      }

      if (currentByte >= 128) {
         return Integer.toString(currentByte ^ 128);
      }

      if (currentByte <= 4) {
         int value = 0;
         int nextByte = 0;

         while (currentByte > 0) {
            value <<= 8;
            nextByte = ins.read();
            if (nextByte == -1) {
               throw new Object();
            }

            value += (byte)nextByte & 255;
            currentByte--;
         }

         return Integer.toString(value);
      } else {
         return TextCoder.getInstance().decode(ins, currentByte);
      }
   }

   public static final IntegerCoder getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      IntegerCoder coder = (IntegerCoder)registry.getOrWaitFor(5227182672208802877L);
      if (coder == null) {
         coder = new IntegerCoder();
         registry.put(5227182672208802877L, coder);
      }

      return coder;
   }
}
