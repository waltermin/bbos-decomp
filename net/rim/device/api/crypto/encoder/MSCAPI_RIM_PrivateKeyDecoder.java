package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHPrivateKey;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.DSAPrivateKey;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

final class MSCAPI_RIM_PrivateKeyDecoder extends MSCAPI_PrivateKeyDecoder {
   private static final byte[] DH4 = new byte[]{0, 68, 72, 52};
   private static final byte[] DH2 = new byte[]{0, 68, 72, 50};

   @Override
   public final PrivateKey decodeKey(InputStream input, String algorithm) throws InvalidKeyEncodingException, NoSuchAlgorithmException {
      switch (algorithm.charAt(0)) {
         case '1':
            byte[] header = CryptoUtilities.readByteArrayFromInputStream(input, 4);
            boolean version3 = true;
            if (!Arrays.equals(header, "DSS4".getBytes())) {
               if (!Arrays.equals(header, "DSS2".getBytes())) {
                  throw new InvalidKeyEncodingException();
               }

               version3 = false;
            }

            int bitlenP = CryptoUtilities.readIntegerLittleEndian(input);
            int bitlenQ = 160;
            int bitlenJ = 0;
            int bitlenX = 160;
            if (version3) {
               bitlenQ = CryptoUtilities.readIntegerLittleEndian(input);
               bitlenJ = CryptoUtilities.readIntegerLittleEndian(input);
               bitlenX = CryptoUtilities.readIntegerLittleEndian(input);
               int counter = CryptoUtilities.readIntegerLittleEndian(input);
               CryptoUtilities.readByteArrayFromInputStream(input, 20);
            }

            int bitlenY = version3 ? bitlenP : 0;
            byte[] p = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            byte[] q = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenQ + 7) / 8);
            byte[] g = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            byte[] j = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenJ + 7) / 8);
            byte[] y = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenY + 7) / 8);
            byte[] x = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenX + 7) / 8);
            if (!version3) {
               CryptoUtilities.readIntegerLittleEndian(input);
               CryptoUtilities.readByteArrayFromInputStream(input, 20);
            }

            CryptoUtilities.flipArray(p);
            CryptoUtilities.flipArray(g);
            CryptoUtilities.flipArray(q);
            DSACryptoSystem cryptoSystem = new DSACryptoSystem(p, q, g);
            return new DSAPrivateKey(cryptoSystem, CryptoUtilities.flipArray(x));
         case '2':
            CryptoUtilities.verifyKeyBytes(input, 82, 83, 65, 50);
            int bitlen = CryptoUtilities.readIntegerLittleEndian(input);
            int bytelen = (bitlen + 7) / 8;
            RSACryptoSystem cryptoSystem = new RSACryptoSystem(bytelen * 8);
            byte[] e = CryptoUtilities.readInteger4BigEndianAsByteArray(input);
            int i = 4;

            while (e[i - 1] == 0 && i > 0) {
               i--;
            }

            Array.resize(e, i);
            byte[] n = CryptoUtilities.readByteArrayFromInputStream(input, bytelen);
            byte[] p = CryptoUtilities.readByteArrayFromInputStream(input, bytelen / 2);
            byte[] q = CryptoUtilities.readByteArrayFromInputStream(input, bytelen / 2);
            byte[] dModPm1 = CryptoUtilities.readByteArrayFromInputStream(input, bytelen / 2);
            byte[] dModQm1 = CryptoUtilities.readByteArrayFromInputStream(input, bytelen / 2);
            byte[] qInvModP = CryptoUtilities.readByteArrayFromInputStream(input, bytelen / 2);
            byte[] d = CryptoUtilities.readByteArrayFromInputStream(input, bytelen);
            CryptoUtilities.flipArray(n);
            CryptoUtilities.flipArray(dModPm1);
            CryptoUtilities.flipArray(p);
            CryptoUtilities.flipArray(dModQm1);
            CryptoUtilities.flipArray(q);
            CryptoUtilities.flipArray(qInvModP);
            CryptoUtilities.flipArray(d);
            return new RSAPrivateKey(cryptoSystem, e, d, n, p, q, dModPm1, dModQm1, qInvModP);
         case '5':
            byte[] header = CryptoUtilities.readByteArrayFromInputStream(input, 4);
            boolean version3 = true;
            if (!Arrays.equals(header, DH4)) {
               if (!Arrays.equals(header, DH2)) {
                  throw new InvalidKeyEncodingException();
               }

               version3 = false;
            }

            int bitlenP = CryptoUtilities.readIntegerLittleEndian(input);
            int bitlenQ = 0;
            int bitlenJ = 0;
            int bitlenX = bitlenP;
            if (version3) {
               bitlenQ = CryptoUtilities.readIntegerLittleEndian(input);
               bitlenJ = CryptoUtilities.readIntegerLittleEndian(input);
               bitlenX = CryptoUtilities.readIntegerLittleEndian(input);
               int counter = CryptoUtilities.readIntegerLittleEndian(input);
               CryptoUtilities.readByteArrayFromInputStream(input, 20);
            }

            int bitlenY = version3 ? bitlenP : 0;
            byte[] p = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            byte[] q = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenQ + 7) / 8);
            byte[] g = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            byte[] j = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenJ + 7) / 8);
            byte[] y = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenY + 7) / 8);
            byte[] x = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenX + 7) / 8);
            DHCryptoSystem cryptoSystem;
            if (bitlenQ == 0) {
               CryptoUtilities.flipArray(p);
               CryptoUtilities.flipArray(g);
               cryptoSystem = new DHCryptoSystem(p, g);
            } else {
               CryptoUtilities.flipArray(p);
               CryptoUtilities.flipArray(g);
               CryptoUtilities.flipArray(q);
               cryptoSystem = new DHCryptoSystem(p, q, g);
            }

            return new DHPrivateKey(cryptoSystem, CryptoUtilities.flipArray(x));
         default:
            throw new NoSuchAlgorithmException(algorithm);
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "MSCAPI";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"1", "2", "5"};
   }
}
