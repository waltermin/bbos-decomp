package net.rim.device.api.crypto.encoder;

import java.io.DataInputStream;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.ARC4Key;
import net.rim.device.api.crypto.DESKey;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.RC5Key;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.TripleDESKey;

final class KeyStore_RIM_SymmetricKeyDecoder1 extends KeyStore_SymmetricKeyDecoder {
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final SymmetricKey decodeKey(DataInputStream input, String algorithm) {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         switch (algorithm.charAt(0)) {
            case '0':
            case '3':
            case '6':
            case '8':
               var5 = false;
               throw new Object();
            case '1':
            default:
               byte[] keyData = Utility.readData(input);
               return new AESKey(keyData, 0, keyData.length * 8);
            case '2':
               byte[] var10 = Utility.readData(input);
               return new ARC4Key(var10, 0, var10.length);
            case '4':
               byte[] var9 = Utility.readData(input);
               return new DESKey(var9, 0);
            case '5':
               byte[] var8 = Utility.readData(input);
               return (SymmetricKey)(new Object(var8, 0, var8.length));
            case '7':
               byte[] var7 = Utility.readData(input);
               return new RC5Key(var7, 0, var7.length * 8);
            case '9':
               byte[] e = Utility.readData(input);
               return new TripleDESKey(e, 0);
         }
      } finally {
         if (var5) {
            throw new InvalidKeyEncodingException();
         }
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "KeyStore";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"1", "2", "4", "5", "7", "9"};
   }
}
