package net.rim.device.api.crypto.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSAPublicKey;

final class WTLS_RIM_PublicKeyEncoder extends PublicKeyEncoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final EncodedKey encodeKey(PublicKey key, long flags) {
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         ByteArrayOutputStream e = new Object();
         DataOutputStream output = new Object((OutputStream)e);
         if (key.getAlgorithm().equals("RSA")) {
            RSAPublicKey k = (RSAPublicKey)key;
            ((DataOutputStream)output).writeByte(2);
            ((DataOutputStream)output).writeByte(0);
            byte[] ex = k.getE();
            ((DataOutputStream)output).writeShort(ex.length);
            ((OutputStream)output).write(ex);
            byte[] n = k.getN();
            ((DataOutputStream)output).writeShort(n.length);
            ((OutputStream)output).write(n);
            return new EncodedKey(((ByteArrayOutputStream)e).toByteArray(), "WTLS");
         }

         var10 = false;
      } finally {
         if (var10) {
            throw new Object();
         }
      }

      throw new Object();
   }

   @Override
   public final String getEncodingAlgorithm() {
      return "WTLS";
   }

   @Override
   public final String[] getKeyAlgorithms() {
      return new String[]{"RSA"};
   }
}
