package net.rim.device.api.crypto.tls.wtls20;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class WTLSCipherSuites {
   public static final byte NULL = 0;
   public static final byte RC5_CBC_40 = 1;
   public static final byte RC5_CBC_56 = 2;
   public static final byte RC5_CBC = 3;
   public static final byte DES_CBC_40 = 4;
   public static final byte DES_CBC = 5;
   public static final byte TDES_CBC_EDE = 6;
   public static final byte IDEA_CBC_40 = 7;
   public static final byte IDEA_CBC_56 = 8;
   public static final byte IDEA_CBC = 9;
   public static final byte RC5_CBC_64 = 10;
   public static final byte IDEA_CBC_64 = 11;
   public static final byte SHA_0 = 0;
   public static final byte SHA_40 = 1;
   public static final byte SHA_80 = 2;
   public static final byte SHA = 3;
   public static final byte MD5_40 = 5;
   public static final byte MD5_80 = 6;
   public static final byte MD5 = 7;
   private static final byte[] _supportedExportEncryption = new byte[]{10, 2, 1, 4, 0};
   private static final byte[] _supportedNonExportEncryption = new byte[]{3};
   private static final byte[] _supportedFIPSNonExportEncryption = new byte[]{6, 5};
   private static final byte[] _supportedFIPSMACs = new byte[]{3, 7, 2, 1, 6, 5};
   private static final byte[][][] _supportedFIPSKeyEx = new byte[][][]{
      (byte[][])({8, 0}),
      (byte[][])({10, 0}),
      (byte[][])({9, 0}),
      (byte[][])({5, 0}),
      (byte[][])({7, 0}),
      (byte[][])({6, 0}),
      (byte[][])({2, 1}),
      (byte[][])({2, 2}),
      (byte[][])({4, 2}),
      (byte[][])({3, 1})
   };

   private WTLSCipherSuites() {
   }

   public static final byte[] getSupportedMACAlgorithms() {
      return Arrays.copy(_supportedFIPSMACs);
   }

   public static final byte[][][] getSupportedKeyExchangeAlgorithms() {
      int length = _supportedFIPSKeyEx.length;
      byte[][][] result = new byte[length][][];

      for (int i = 0; i < length; i++) {
         result[i] = (byte[][])Arrays.copy((byte[])_supportedFIPSKeyEx[i]);
      }

      return result;
   }

   public static final byte[] getSupportedEncryptionAlgorithms() {
      if (WTLSOptionStore.getOptions().restrictFIPSCipherSuites()) {
         return Arrays.copy(_supportedFIPSNonExportEncryption);
      }

      int fipsLength = _supportedFIPSNonExportEncryption.length;
      int nonExportLength = _supportedNonExportEncryption.length;
      int exportLength = _supportedExportEncryption.length;
      byte[] supported = new byte[fipsLength + nonExportLength + exportLength];
      System.arraycopy(_supportedFIPSNonExportEncryption, 0, supported, 0, fipsLength);
      System.arraycopy(_supportedNonExportEncryption, 0, supported, fipsLength, nonExportLength);
      System.arraycopy(_supportedExportEncryption, 0, supported, fipsLength + nonExportLength, exportLength);
      return supported;
   }

   public static final byte[] getSupportedExportEncryptionAlgorithms() {
      return WTLSOptionStore.getOptions().restrictFIPSCipherSuites() ? new byte[0] : Arrays.copy(_supportedExportEncryption);
   }

   public static final byte[] getSupportedNonExportEncryptionAlgorithms() {
      if (WTLSOptionStore.getOptions().restrictFIPSCipherSuites()) {
         return Arrays.copy(_supportedFIPSNonExportEncryption);
      }

      int fipsLength = _supportedFIPSNonExportEncryption.length;
      int nonFIPSLength = _supportedNonExportEncryption.length;
      byte[] supported = new byte[fipsLength + nonFIPSLength];
      System.arraycopy(_supportedFIPSNonExportEncryption, 0, supported, 0, fipsLength);
      System.arraycopy(_supportedNonExportEncryption, 0, supported, fipsLength, nonFIPSLength);
      return supported;
   }

   public static final void addEncryptionAlgorithm(byte algorithm, int priority) {
      removeEncryptionAlgorithm(algorithm);
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[] vector = storage.getEncryptionElements();
      if (notSupportedEncryptionAlgorithm(algorithm)) {
         throw new Object();
      }

      addAlgorithm(vector, algorithm, priority);
      storage.setEncryptionElements(vector);
   }

   public static final void removeEncryptionAlgorithm(byte algorithm) {
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[] vector = storage.getEncryptionElements();
      removeAlgorithm(vector, algorithm);
      storage.setEncryptionElements(vector);
   }

   public static final void addMACAlgorithm(byte algorithm, int priority) {
      removeMACAlgorithm(algorithm);
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[] vector = storage.getMACElements();
      if (notSupportedMACAlgorithm(algorithm)) {
         throw new Object();
      }

      addAlgorithm(vector, algorithm, priority);
      storage.setMACElements(vector);
   }

   public static final void removeMACAlgorithm(byte algorithm) {
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[] vector = storage.getMACElements();
      removeAlgorithm(vector, algorithm);
      storage.setMACElements(vector);
   }

   public static final void addKeyExchangeAlgorithm(byte[] algorithm, int priority) {
      removeKeyExchangeAlgorithm(algorithm);
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[][][] vector = (byte[][][])storage.getKeyExchangeElements();
      if (notSupportedKeyExchangeAlgorithm(algorithm)) {
         throw new Object();
      }

      if (priority >= 0 && priority <= vector.length) {
         Array.resize(vector, vector.length + 1);

         for (int i = vector.length - 1; i > priority; i--) {
            vector[i] = vector[i - 1];
         }

         vector[priority] = (byte[][])Arrays.copy(algorithm);
      } else {
         Array.resize(vector, vector.length + 1);
         vector[vector.length - 1] = (byte[][])Arrays.copy(algorithm);
      }

      storage.setKeyExchangeElements((byte[][])vector);
   }

   public static final void removeKeyExchangeAlgorithm(byte[] algorithm) {
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[][][] vector = (byte[][][])storage.getKeyExchangeElements();

      for (int i = 0; i < vector.length; i++) {
         if (Arrays.equals((byte[])vector[i], algorithm)) {
            for (int j = i + 1; j < vector.length; i++) {
               vector[i] = vector[j];
               j++;
            }

            Array.resize(vector, vector.length - 1);
            break;
         }
      }

      storage.setKeyExchangeElements((byte[][])vector);
   }

   private static final void addAlgorithm(byte[] vector, byte algorithm, int priority) {
      if (priority >= 0 && priority <= vector.length) {
         Array.resize(vector, vector.length + 1);

         for (int i = vector.length - 1; i > priority; i--) {
            vector[i] = vector[i - 1];
         }

         vector[priority] = algorithm;
      } else {
         Array.resize(vector, vector.length + 1);
         vector[vector.length - 1] = algorithm;
      }
   }

   private static final void removeAlgorithm(byte[] vector, byte algorithm) {
      for (int i = 0; i < vector.length; i++) {
         if (vector[i] == algorithm) {
            for (int j = i + 1; j < vector.length; i++) {
               vector[i] = vector[j];
               j++;
            }

            Array.resize(vector, vector.length - 1);
            return;
         }
      }
   }

   public static final void prioritizeEncryptionAlgorithms(byte higher, byte lower) {
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[] vector = storage.getEncryptionElements();
      prioritizeAlgorithms(vector, higher, lower);
      storage.setEncryptionElements(vector);
   }

   public static final void prioritizeMACAlgorithms(byte higher, byte lower) {
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[] vector = storage.getMACElements();
      prioritizeAlgorithms(vector, higher, lower);
      storage.setMACElements(vector);
   }

   private static final void prioritizeAlgorithms(byte[] vector, byte higher, byte lower) {
      if (higher != lower) {
         int searchHigher = -1;
         int searchLower = -1;

         for (int i = 0; i < vector.length; i++) {
            if (vector[i] == higher) {
               searchHigher = i;
            } else if (vector[i] == lower) {
               searchLower = i;
            }
         }

         if (searchHigher != -1 && searchLower != -1) {
            if (searchHigher >= searchLower) {
               for (int i = vector.length - 1; i > searchLower; i--) {
                  vector[i] = vector[i - 1];
               }

               vector[searchLower] = higher;
            }
         }
      }
   }

   public static final void prioritizeKeyExchangeAlgorithms(byte[] higher, byte[] lower) {
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[][][] vector = (byte[][][])storage.getKeyExchangeElements();
      if (higher != lower) {
         int searchHigher = -1;
         int searchLower = -1;

         for (int i = 0; i < vector.length; i++) {
            if (Arrays.equals((byte[])vector[i], higher)) {
               searchHigher = i;
            } else if (Arrays.equals((byte[])vector[i], lower)) {
               searchLower = i;
            }
         }

         if (searchHigher != -1 && searchLower != -1) {
            if (searchHigher >= searchLower) {
               for (int i = vector.length - 1; i > searchLower; i--) {
                  vector[i] = vector[i - 1];
               }

               vector[searchLower] = (byte[][])higher;
               storage.setKeyExchangeElements((byte[][])vector);
            }
         }
      }
   }

   public static final byte[] getEncryptionPriority() {
      verifyEncryptionPriorities();
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      return Arrays.copy(storage.getEncryptionElements());
   }

   public static final byte[] getMACPriority() {
      verifyMACPriorities();
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      return Arrays.copy(storage.getMACElements());
   }

   public static final byte[][][] getKeyExchangePriority() {
      verifyKeyExchangePriorities();
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[][][] data = (byte[][][])storage.getKeyExchangeElements();
      byte[][][] result = new byte[data.length][][];

      for (int i = 0; i < result.length; i++) {
         result[i] = (byte[][])Arrays.copy((byte[])data[i]);
      }

      return result;
   }

   private static final void verifyEncryptionPriorities() {
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[] data = storage.getEncryptionElements();
      if (data != null) {
         int length = data.length;
         int insertionIndex = 0;

         for (int i = 0; i < length; i++) {
            if (!notSupportedEncryptionAlgorithm(data[i])) {
               data[insertionIndex++] = data[i];
            }
         }

         if (insertionIndex != length) {
            Array.resize(data, insertionIndex);
            storage.setEncryptionElements(data);
         }
      }
   }

   private static final void verifyMACPriorities() {
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[] data = storage.getMACElements();
      if (data != null) {
         int length = data.length;
         int insertionIndex = 0;

         for (int i = 0; i < length; i++) {
            if (!notSupportedMACAlgorithm(data[i])) {
               data[insertionIndex++] = data[i];
            }
         }

         if (insertionIndex != length) {
            Array.resize(data, insertionIndex);
            storage.setMACElements(data);
         }
      }
   }

   private static final void verifyKeyExchangePriorities() {
      WTLSCipherSuiteStorage storage = WTLSCipherSuiteStorage.getInstance();
      byte[][][] data = (byte[][][])storage.getKeyExchangeElements();
      if (data != null) {
         int length = data.length;
         int insertionIndex = 0;

         for (int i = 0; i < length; i++) {
            if (!notSupportedKeyExchangeAlgorithm((byte[])data[i])) {
               data[insertionIndex++] = data[i];
            }
         }

         if (insertionIndex != length) {
            Array.resize(data, insertionIndex);
            storage.setKeyExchangeElements((byte[][])data);
         }
      }
   }

   public static final byte[] getDefaultEncryptionAlgorithms() {
      WTLSOptionStore options = WTLSOptionStore.getOptions();
      return !options.allowExportCipherSuites() ? getSupportedNonExportEncryptionAlgorithms() : getSupportedEncryptionAlgorithms();
   }

   public static final byte[] getDefaultMACAlgorithms() {
      return getSupportedMACAlgorithms();
   }

   public static final void removeAllMACAlgorithms() {
      WTLSCipherSuiteStorage.getInstance().setMACElements(new byte[0]);
   }

   public static final void removeAllKeyExchangeAlgorithms() {
      WTLSCipherSuiteStorage.getInstance().setKeyExchangeElements((byte[][])(new byte[0][][]));
   }

   public static final void removeAllEncryptionAlgorithms() {
      WTLSCipherSuiteStorage.getInstance().setEncryptionElements(new byte[0]);
   }

   public static final byte[][][] getDefaultKeyExchangeAlgorithms() {
      return getSupportedKeyExchangeAlgorithms();
   }

   private static final boolean notSupportedEncryptionAlgorithm(byte encryptionAlgorithm) {
      if (WTLSOptionStore.getOptions().restrictFIPSCipherSuites()) {
         return !findValueInArray(_supportedFIPSNonExportEncryption, encryptionAlgorithm);
      } else {
         return !WTLSOptionStore.getOptions().allowExportCipherSuites()
            ? !findValueInArray(_supportedNonExportEncryption, encryptionAlgorithm)
               && !findValueInArray(_supportedFIPSNonExportEncryption, encryptionAlgorithm)
            : !findValueInArray(getSupportedEncryptionAlgorithms(), encryptionAlgorithm);
      }
   }

   private static final boolean notSupportedMACAlgorithm(byte algorithm) {
      return !findValueInArray(_supportedFIPSMACs, algorithm);
   }

   private static final boolean notSupportedKeyExchangeAlgorithm(byte[] algorithm) {
      int size = _supportedFIPSKeyEx.length;

      for (int i = 0; i < size; i++) {
         if (Arrays.equals((byte[])_supportedFIPSKeyEx[i], algorithm)) {
            return false;
         }
      }

      return true;
   }

   public static final boolean export(byte cipher) {
      return findValueInArray(_supportedExportEncryption, cipher);
   }

   private static final boolean findValueInArray(byte[] array, byte value) {
      int size = array.length;

      for (int i = 0; i < size; i++) {
         if (value == array[i]) {
            return true;
         }
      }

      return false;
   }
}
