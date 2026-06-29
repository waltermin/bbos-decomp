package net.rim.device.api.crypto.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.rim.device.api.crypto.RC2Key;
import net.rim.device.api.crypto.SymmetricKey;

final class KeyStore_RIM_SymmetricKeyEncoder3 extends SymmetricKeyEncoder {
   @Override
   protected final EncodedKey encodeKey(SymmetricKey key) {
      try {
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         DataOutputStream dataOut = new DataOutputStream(output);
         dataOut.writeInt(0);
         if (key.getAlgorithm().equals("RC2")) {
            RC2Key k = (RC2Key)key;
            dataOut.writeInt(6);
            dataOut.writeInt(k.getEffectiveBitLength());
         } else if (key.getAlgorithm().equals("Skipjack")) {
            dataOut.writeInt(8);
         } else {
            if (!key.getAlgorithm().equals("CAST128")) {
               throw new IllegalArgumentException();
            }

            dataOut.writeInt(3);
         }

         Utility.writeData(key.getData(), dataOut);
         dataOut.close();
         return new EncodedKey(output.toByteArray(), "KeyStore");
      } finally {
         throw new RuntimeException();
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
