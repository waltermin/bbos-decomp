package net.rim.device.api.crypto.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.rim.device.api.crypto.RC2Key;
import net.rim.device.api.crypto.SymmetricKey;

final class KeyStore_RIM_SymmetricKeyEncoder3 extends SymmetricKeyEncoder {
   @Override
   protected final EncodedKey encodeKey(SymmetricKey key) {
      try {
         ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
         DataOutputStream dataOut = (DataOutputStream)(new Object(output));
         dataOut.writeInt(0);
         if (key.getAlgorithm().equals("RC2")) {
            RC2Key k = (RC2Key)key;
            dataOut.writeInt(6);
            dataOut.writeInt(k.getEffectiveBitLength());
         } else if (key.getAlgorithm().equals("Skipjack")) {
            dataOut.writeInt(8);
         } else {
            if (!key.getAlgorithm().equals("CAST128")) {
               throw new Object();
            }

            dataOut.writeInt(3);
         }

         Utility.writeData(key.getData(), dataOut);
         dataOut.close();
         return (EncodedKey)(new Object(output.toByteArray(), "KeyStore"));
      } finally {
         throw new Object();
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "KeyStore";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"RC2", "Skipjack", "CAST128"};
   }
}
