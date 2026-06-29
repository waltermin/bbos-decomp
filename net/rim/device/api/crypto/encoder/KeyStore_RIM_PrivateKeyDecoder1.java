package net.rim.device.api.crypto.encoder;

import java.io.DataInputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHPrivateKey;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.DSAPrivateKey;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSAPrivateKey;

final class KeyStore_RIM_PrivateKeyDecoder1 extends KeyStore_PrivateKeyDecoder {
   @Override
   public final PrivateKey decodeKey(DataInputStream input, CryptoSystem cryptoSystem, String algorithm) {
      switch (algorithm.charAt(0)) {
         case '1': {
            byte[] p = Utility.readData(input);
            byte[] g = Utility.readData(input);
            byte[] q = Utility.readData(input);
            DHCryptoSystem dhCryptoSystem;
            if (q.length == 0) {
               dhCryptoSystem = new DHCryptoSystem(p, g);
            } else {
               dhCryptoSystem = new DHCryptoSystem(p, q, g);
            }

            byte[] keyData = Utility.readData(input);
            return new DHPrivateKey(dhCryptoSystem, keyData);
         }
         case '2': {
            byte[] p = Utility.readData(input);
            byte[] q = Utility.readData(input);
            byte[] g = Utility.readData(input);
            DSACryptoSystem dsaCryptoSystem = new DSACryptoSystem(p, q, g);
            byte[] keyData = Utility.readData(input);
            return new DSAPrivateKey(dsaCryptoSystem, keyData);
         }
         case '5': {
            byte[] n = Utility.readData(input);
            byte[] e = Utility.readData(input);
            byte[] d = Utility.readData(input);
            byte[] p = Utility.readData(input);
            byte[] q = Utility.readData(input);
            byte[] dModPm1 = Utility.readData(input);
            byte[] dModQm1 = Utility.readData(input);
            byte[] qInvModP = Utility.readData(input);
            if (e.length > 0) {
               if (p.length > 0 && q.length > 0) {
                  RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem((p.length + q.length) * 8);
                  if (dModPm1.length > 0 && dModQm1.length > 0 && qInvModP.length > 0) {
                     if (d.length > 0 && n.length > 0) {
                        return new RSAPrivateKey(rsaCryptoSystem, e, d, n, p, q, dModPm1, dModQm1, qInvModP);
                     }

                     return new RSAPrivateKey(rsaCryptoSystem, e, p, q, dModPm1, dModQm1, qInvModP);
                  } else if (d.length > 0) {
                     return new RSAPrivateKey(rsaCryptoSystem, e, d, p, q);
                  }
               } else if (d.length > 0 && n.length > 0) {
                  RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem(n.length * 8);
                  return new RSAPrivateKey(rsaCryptoSystem, e, d, n);
               }
            }
         }
         default:
            throw new Object();
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "KeyStore";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"1", "2", "5"};
   }
}
