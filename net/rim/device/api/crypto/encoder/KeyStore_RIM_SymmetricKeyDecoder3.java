package net.rim.device.api.crypto.encoder;

import java.io.DataInputStream;
import net.rim.device.api.crypto.CAST128Key;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.RC2Key;
import net.rim.device.api.crypto.SkipjackKey;
import net.rim.device.api.crypto.SymmetricKey;

final class KeyStore_RIM_SymmetricKeyDecoder3 extends KeyStore_SymmetricKeyDecoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final SymmetricKey decodeKey(DataInputStream input, String algorithm) throws InvalidKeyEncodingException {
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         switch (algorithm.charAt(0)) {
            case '3':
               byte[] var9 = Utility.readData(input);
               return new CAST128Key(var9, 0);
            case '6':
               int effectiveBitLength = input.readInt();
               byte[] keyData = Utility.readData(input);
               return new RC2Key(keyData, effectiveBitLength);
            case '8':
               byte[] e = Utility.readData(input);
               return new SkipjackKey(e, 0);
            default:
               var6 = false;
         }
      } finally {
         if (var6) {
            throw new InvalidKeyEncodingException();
         }
      }

      throw new IllegalArgumentException();
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "KeyStore";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"3", "6", "8"};
   }
}
