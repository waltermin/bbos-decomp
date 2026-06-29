package net.rim.device.api.crypto.encoder;

import java.io.DataInputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.crypto.pgp.PGPPrivateKey;
import net.rim.device.api.io.SharedInputStream;
import net.rim.device.internal.crypto.pgp.PGPPrivateKeyPacket;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.vm.Array;

final class KeyStore_RIM_PrivateKeyDecoderPGP extends KeyStore_PrivateKeyDecoder {
   @Override
   public final PrivateKey decodeKey(DataInputStream encodedKey, CryptoSystem cryptoSystem, String keyAlgorithm) {
      SharedInputStream input = SharedInputStream.getSharedInputStream(encodedKey);
      int numPackets = 0;
      PGPPrivateKeyPacket[] packets = new PGPPrivateKeyPacket[0];

      try {
         int[] values = new int[2];

         try {
            while (true) {
               PGPUtilities.readTagAndLength(input, values);
               if (values[0] != 5 && values[0] != 7) {
                  throw new Object();
               }

               byte[] encoding = new byte[values[1]];
               int bytesRead = input.read(encoding);
               if (bytesRead != values[1]) {
                  throw new Object();
               }

               PGPPrivateKeyPacket packet = new PGPPrivateKeyPacket(values[0], encoding);
               Array.resize(packets, numPackets + 1);
               packets[numPackets] = packet;
               numPackets++;
            }
         } finally {
            return new PGPPrivateKey(packets);
         }
      } catch (PGPEncodingException e) {
         throw new Object();
      }
   }

   @Override
   public final String getEncodingAlgorithm() {
      return PGPUtilities.ENCODING_ALGORITHM;
   }

   @Override
   public final String[] getKeyAlgorithms() {
      return new String[]{"6"};
   }
}
