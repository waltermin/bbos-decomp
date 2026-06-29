package net.rim.device.api.crypto.tls.wtls20;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.BlockEncryptorEngine;
import net.rim.device.api.crypto.CBCDecryptorEngine;
import net.rim.device.api.crypto.CBCEncryptorEngine;
import net.rim.device.api.crypto.DecryptorFactory;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.EncryptorFactory;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.NullDecryptor;
import net.rim.device.api.crypto.NullEncryptor;
import net.rim.device.api.crypto.NullMAC;
import net.rim.device.api.crypto.RC5DecryptorEngine;
import net.rim.device.api.crypto.RC5EncryptorEngine;
import net.rim.device.api.crypto.RC5Key;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.tls.TLSAlertException;
import net.rim.device.api.crypto.tls.TLSBlockFormatterEngine;
import net.rim.device.api.crypto.tls.TLSBlockUnformatterEngine;

final class WTLSCipherSuiteFactory {
   public static final WTLSConnectionState getConnectionState(int cipherSuite) throws TLSAlertException {
      WTLSConnectionState state = new WTLSConnectionState();
      switch (cipherSuite >> 8 & 0xFF) {
         case -1:
         case 7:
         case 8:
         case 9:
            throw new TLSAlertException((byte)3, (byte)40);
         case 0:
         default:
            state.setBulkCipherAlgorithm(null);
            state.setIsExportable(true);
            state.setKeySize(0);
            state.setCipherType((byte)0);
            state.setKeyMaterialLength(0);
            state.setIVSize(0);
            break;
         case 1:
            state.setBulkCipherAlgorithm("RC5");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(true);
            state.setKeySize(5);
            state.setKeyMaterialLength(16);
            break;
         case 2:
            state.setBulkCipherAlgorithm("RC5");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(true);
            state.setKeySize(7);
            state.setKeyMaterialLength(16);
            break;
         case 3:
            state.setBulkCipherAlgorithm("RC5");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(false);
            state.setKeySize(16);
            state.setKeyMaterialLength(16);
            break;
         case 4:
            state.setBulkCipherAlgorithm("DES");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(true);
            state.setKeySize(5);
            state.setKeyMaterialLength(8);
            break;
         case 5:
            state.setBulkCipherAlgorithm("DES");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(false);
            state.setKeySize(8);
            state.setKeyMaterialLength(8);
            break;
         case 6:
            state.setBulkCipherAlgorithm("TripleDES");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(false);
            state.setKeySize(24);
            state.setKeyMaterialLength(24);
            break;
         case 10:
            state.setBulkCipherAlgorithm("RC5");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(true);
            state.setKeySize(8);
            state.setKeyMaterialLength(16);
      }

      switch (cipherSuite & 0xFF) {
         case -1:
         case 4:
            throw new TLSAlertException((byte)3, (byte)40);
         case 0:
         default:
            state.setHashSize(0);
            state.setMACKeySize(0);
            state.setMacAlgorithm(null);
            return state;
         case 1:
            state.setHashSize(5);
            state.setMACKeySize(20);
            state.setMacAlgorithm("SHA1");
            return state;
         case 2:
            state.setHashSize(10);
            state.setMACKeySize(20);
            state.setMacAlgorithm("SHA1");
            return state;
         case 3:
            state.setHashSize(20);
            state.setMACKeySize(20);
            state.setMacAlgorithm("SHA1");
            return state;
         case 5:
            state.setHashSize(5);
            state.setMACKeySize(16);
            state.setMacAlgorithm("MD5");
            return state;
         case 6:
            state.setHashSize(10);
            state.setMACKeySize(16);
            state.setMacAlgorithm("MD5");
            return state;
         case 7:
            state.setHashSize(16);
            state.setMACKeySize(16);
            state.setMacAlgorithm("MD5");
            return state;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void updateReadConnectionState(
      WTLSConnectionState state, SymmetricKey cipherKey, SymmetricKey hmacKey, InitializationVector iv, InputStream input
   ) {
      try {
         String bulkAlgorithm = state.getBulkCipherAlgorithm();
         if (bulkAlgorithm == null) {
            NullDecryptor decryptor = new NullDecryptor(input);
            state.setDecryptor(decryptor);
         } else {
            BlockDecryptorEngine blockDecrypt;
            if (state.getIsExportable() && cipherKey instanceof RC5Key) {
               blockDecrypt = new CBCDecryptorEngine(new RC5DecryptorEngine((RC5Key)cipherKey, 8, 12), iv);
            } else {
               blockDecrypt = DecryptorFactory.getBlockDecryptorEngine(
                  cipherKey, ((StringBuffer)(new Object())).append(bulkAlgorithm).append("/CBC").toString(), iv
               );
            }

            state.setBlockDecryptor(blockDecrypt);
            state.setServerIV(iv.getData());
            state.setDecryptor(new BlockDecryptor(new TLSBlockUnformatterEngine(blockDecrypt), input));
         }

         String macAlgorithm = state.getMacAlgorithm();
         if (macAlgorithm == null) {
            NullMAC mac = new NullMAC();
            state.setServerMAC(mac);
         } else {
            HMAC hmac = (HMAC)(new Object((HMACKey)hmacKey, DigestFactory.getInstance(macAlgorithm)));
            state.setServerMAC(hmac);
         }
      } catch (Throwable var9) {
         throw new Object(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void updateWriteConnectionState(
      WTLSConnectionState state, SymmetricKey cipherKey, SymmetricKey hmacKey, InitializationVector iv, OutputStream output
   ) {
      try {
         String bulkAlgorithm = state.getBulkCipherAlgorithm();
         if (bulkAlgorithm == null) {
            NullEncryptor encryptor = new NullEncryptor(output);
            state.setEncryptor(encryptor);
         } else {
            BlockEncryptorEngine blockEncrypt;
            if (state.getIsExportable() && cipherKey instanceof RC5Key) {
               blockEncrypt = new CBCEncryptorEngine(new RC5EncryptorEngine((RC5Key)cipherKey, 8, 12), iv);
            } else {
               blockEncrypt = EncryptorFactory.getBlockEncryptorEngine(
                  cipherKey, ((StringBuffer)(new Object())).append(bulkAlgorithm).append("/CBC").toString(), iv
               );
            }

            state.setBlockEncryptor(blockEncrypt);
            state.setClientIV(iv.getData());
            state.setEncryptor(new BlockEncryptor(new TLSBlockFormatterEngine(blockEncrypt), output));
         }

         String macAlgorithm = state.getMacAlgorithm();
         if (macAlgorithm == null) {
            NullMAC mac = new NullMAC();
            state.setClientMAC(mac);
         } else {
            HMAC hmac = (HMAC)(new Object((HMACKey)hmacKey, DigestFactory.getInstance(macAlgorithm)));
            state.setClientMAC(hmac);
         }
      } catch (Throwable var9) {
         throw new Object(e);
      }
   }

   public static final String getCompressionAlgorithm(byte algorithm) {
      return null;
   }
}
