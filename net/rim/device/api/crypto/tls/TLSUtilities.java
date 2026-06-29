package net.rim.device.api.crypto.tls;

import net.rim.device.api.crypto.PseudoRandomSource;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.SymmetricKeyFactory;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public final class TLSUtilities {
   private static final boolean DEBUG;

   public static final boolean domainNameMatches(String recordProtocolDomainName, String certificateDomainName) {
      if (certificateDomainName != null && certificateDomainName.length() != 0) {
         return certificateDomainName.charAt(0) == '*'
            ? StringUtilities.endsWithIgnoreCase(recordProtocolDomainName, certificateDomainName.substring(1), 1701707776)
            : StringUtilities.strEqualIgnoreCase(recordProtocolDomainName, certificateDomainName, 1701707776);
      } else {
         return false;
      }
   }

   public static final boolean findCipherSuite(int cipherSuite, int[] cipherSuiteArray) {
      int length = cipherSuiteArray.length;

      for (int i = 0; i < length; i++) {
         if (cipherSuiteArray[i] == cipherSuite) {
            return true;
         }
      }

      return false;
   }

   public static final int[] concatenate(int[][][] arrays) {
      int[] finalResult = new int[0];
      int length = arrays.length;

      for (int i = 0; i < length; i++) {
         int finalResultLength = finalResult.length;
         int arrayLength = arrays[i].length;
         Array.resize(finalResult, finalResultLength + arrayLength);
         System.arraycopy(arrays[i], 0, finalResult, finalResultLength, arrayLength);
      }

      return finalResult;
   }

   public static final SymmetricKey getKey(ConnectionState state, PseudoRandomSource prf, byte[] keyData, boolean checkExport) {
      prf.xorBytes(keyData);
      if (checkExport && state.getIsExportable()) {
         return null;
      }

      int keyLength = state.getKeySize();
      if (!checkExport) {
         keyLength = state.getKeyMaterialLength();
      }

      return SymmetricKeyFactory.getInstance(
         ((StringBuffer)(new Object())).append(state.getBulkCipherAlgorithm()).append('_').append(keyLength << 3).toString(), keyData, 0, keyData.length
      );
   }

   public static final int readIntegerThreeBytes(DataBuffer buffer) {
      return buffer.readUnsignedByte() << 16 | buffer.readUnsignedByte() << 8 | buffer.readUnsignedByte();
   }

   public static final int readIntegerTwoBytes(DataBuffer buffer) {
      return buffer.readUnsignedByte() << 8 | buffer.readUnsignedByte();
   }

   public static final void writeIntegerTwoBytes(DataBuffer buffer, int integer) {
      buffer.write(integer >>> 8);
      buffer.write(integer);
   }

   public static final void writeIntegerThreeBytes(DataBuffer buffer, int integer) {
      buffer.write(integer >>> 16);
      buffer.write(integer >>> 8);
      buffer.write(integer);
   }

   public static final void sendAlertAndThrowException(AlertProtocolMethods alertProtocol, byte description) {
      alertProtocol.sendAlertMessage((byte)3, description);
      throw new TLSAlertException((byte)3, description);
   }

   public static final void sendAlertAndCancel(AlertProtocolMethods alertProtocol, byte description) {
      alertProtocol.sendAlertMessage((byte)3, description);
      throw new Object(new TLSAlertException((byte)3, description));
   }

   public static final void printCertificate(Certificate certificate) {
   }
}
