package net.rim.device.api.crypto.tls.tls10;

import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.ssl.TLSOptionStore;
import net.rim.vm.Array;

public class TLSCipherSuites {
   public static final int TLS_NULL_WITH_NULL_NULL;
   public static final int TLS_DH_anon_EXPORT_WITH_RC4_40_MD5;
   public static final int TLS_RSA_EXPORT_WITH_RC4_40_MD5;
   public static final int TLS_RSA_EXPORT_WITH_DES40_CBC_SHA;
   public static final int TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA;
   public static final int TLS_DH_anon_EXPORT_WITH_DES40_CBC_SHA;
   public static final int TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA;
   public static final int TLS_RSA_WITH_RC4_128_MD5;
   public static final int TLS_RSA_WITH_RC4_128_SHA;
   public static final int TLS_RSA_WITH_3DES_EDE_CBC_SHA;
   public static final int TLS_RSA_WITH_AES_128_CBC_SHA;
   public static final int TLS_RSA_WITH_AES_256_CBC_SHA;
   public static final int TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA;
   public static final int TLS_DHE_DSS_WITH_AES_128_CBC_SHA;
   public static final int TLS_DHE_DSS_WITH_AES_256_CBC_SHA;
   public static final int TLS_DH_anon_WITH_AES_128_CBC_SHA;
   public static final int TLS_DH_anon_WITH_AES_256_CBC_SHA;
   public static final int TLS_DH_anon_WITH_RC4_128_MD5;
   public static final int TLS_DH_anon_WITH_3DES_EDE_CBC_SHA;
   public static final int TLS_DH_anon_WITH_DES_CBC_SHA;
   public static final int TLS_DHE_DSS_WITH_DES_CBC_SHA;
   public static final int TLS_RSA_WITH_DES_CBC_SHA;
   public static final int TLS_ECDH_ECDSA_WITH_RC4_128_SHA;
   public static final int TLS_ECDH_ECDSA_WITH_DES_CBC_SHA;
   public static final int TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA;
   public static final int TLS_RSA_EXPORT_WITH_RC2_CBC_40_MD5;
   public static final int TLS_DH_DSS_WITH_DES_CBC_SHA;
   public static final int TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA;
   public static final int TLS_DH_DSS_WITH_AES_128_CBC_SHA;
   public static final int TLS_DH_DSS_WITH_AES_256_CBC_SHA;
   public static final int TLS_DH_RSA_WITH_AES_128_CBC_SHA;
   public static final int TLS_DH_RSA_WITH_DES_CBC_SHA;
   public static final int TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA;
   public static final int TLS_DH_RSA_WITH_AES_256_CBC_SHA;
   public static final int TLS_DHE_RSA_WITH_DES_CBC_SHA;
   public static final int TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA;
   public static final int TLS_DHE_RSA_WITH_AES_128_CBC_SHA;
   public static final int TLS_DHE_RSA_WITH_AES_256_CBC_SHA;
   public static final int TLS_ECDH_RSA_WITH_RC4_128_SHA;
   public static final int TLS_ECDH_RSA_WITH_DES_CBC_SHA;
   public static final int TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA;
   public static final int TLS_ECDH_RSA_WITH_AES_128_CBC_SHA;
   public static final int TLS_ECDH_RSA_WITH_AES_256_CBC_SHA;
   public static final int TLS_ECDH_RSA_EXPORT_WITH_RC4_40_SHA;
   public static final int TLS_ECDH_RSA_EXPORT_WITH_RC4_56_SHA;
   public static final int TLS_ECDH_anon_EXPORT_WITH_DES40_CBC_SHA;
   public static final int TLS_ECDH_anon_EXPORT_WITH_RC4_40_SHA;
   public static final int TLS_ECDH_anon_WITH_RC4_128_SHA;
   public static final int TLS_ECDH_anon_WITH_DES_CBC_SHA;
   public static final int TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA;
   public static final int TLS_RSA_WITH_IDEA_CBC_SHA;
   public static final int TLS_DH_DSS_EXPORT_WITH_DES40_CBC_SHA;
   public static final int TLS_DH_RSA_EXPORT_WITH_DES40_CBC_SHA;
   public static final int TLS_RSA_WITH_NULL_MD5;
   public static final int TLS_RSA_WITH_NULL_SHA;
   public static final int TLS_ECDH_ECDSA_WITH_NULL_SHA;
   public static final int TLS_ECDH_RSA_WITH_NULL_SHA;
   public static final int TLS_ECDH_anon_NULL_WITH_SHA;
   private static int[] _supportedExportCipherSuites = new int[]{3, 23, -804651005, 5, 4, 24, -804651004, 8};
   private static int[] _supportedFIPSExportCipherSuites = new int[]{8, 17, 20, 25, -804650992, 10, 47, 53, 19, 50, 56, 9, 18, 51, 57, 22};
   private static int[] _supportedNonExportCipherSuites = new int[]{5, 4, 24, -804651004, 8, 17, 20, 25, -804650992, 10, 47, 53};
   private static int[] _supportedFIPSNonExportCipherSuites = new int[]{
      10,
      47,
      53,
      19,
      50,
      56,
      9,
      18,
      51,
      57,
      22,
      21,
      27,
      52,
      58,
      26,
      51,
      51,
      4801362,
      5391186,
      5526098,
      -805044220,
      1414417475,
      -805044220,
      1381388883,
      -805044219,
      896,
      1711341569,
      16805145,
      1701539702,
      526976000,
      1527120108,
      134266886,
      2032535552,
      671613054,
      467227,
      1064314888,
      671613171,
      747480,
      493693704,
      709040454,
      -2107808151,
      1831536640,
      1125729821,
      1566992682,
      1701728130,
      1831536640,
      1125729821,
      1566992682,
      1970427010,
      721944603,
      424025453,
      1634561860,
      1697402369,
      1831536640,
      -400996835,
      -1972802493,
      1718168131,
      134251101,
      1817948203,
      1665206272,
      1650531591,
      1836008574,
      743309665
   };

   private TLSCipherSuites() {
   }

   public static int[] getSupportedCipherSuites() {
      TLSOptionStore options = TLSOptionStore.getOptions();
      boolean restrictFIPS = options.restrictFIPSCipherSuites();
      boolean allowExport = options.allowExportCipherSuites();
      if (!allowExport) {
         return !restrictFIPS
            ? TLSUtilities.concatenate((int[][])(new int[][][]{(int[][])_supportedNonExportCipherSuites, (int[][])_supportedFIPSNonExportCipherSuites}))
            : Arrays.copy(_supportedFIPSNonExportCipherSuites);
      } else {
         return restrictFIPS
            ? TLSUtilities.concatenate((int[][])(new int[][][]{(int[][])_supportedFIPSNonExportCipherSuites, (int[][])_supportedFIPSExportCipherSuites}))
            : TLSUtilities.concatenate(
               (int[][])(new int[][][]{
                  (int[][])_supportedNonExportCipherSuites,
                  (int[][])_supportedFIPSNonExportCipherSuites,
                  (int[][])_supportedFIPSExportCipherSuites,
                  (int[][])_supportedExportCipherSuites
               })
            );
      }
   }

   public static int[] getSupportedExportCipherSuites() {
      return Arrays.copy(_supportedExportCipherSuites);
   }

   public static int[] getSupportedNonExportCipherSuites() {
      return Arrays.copy(_supportedNonExportCipherSuites);
   }

   public static int[] getSupportedFIPSExportCipherSuites() {
      return Arrays.copy(_supportedFIPSExportCipherSuites);
   }

   public static int[] getSupportedFIPSNonExportCipherSuites() {
      return Arrays.copy(_supportedFIPSNonExportCipherSuites);
   }

   public static void addCipherSuite(int cipherSuite, int priority) {
      if (notSupported(cipherSuite)) {
         throw new Object();
      }

      removeCipherSuite(cipherSuite);
      TLSCipherSuiteStorage storage = TLSCipherSuiteStorage.getInstance();
      int[] vector = storage.getItems();
      if (priority >= 0 && priority <= vector.length) {
         Array.resize(vector, vector.length + 1);

         for (int i = vector.length - 1; i > priority; i--) {
            vector[i] = vector[i - 1];
         }

         vector[priority] = cipherSuite;
      } else {
         Array.resize(vector, vector.length + 1);
         vector[vector.length - 1] = cipherSuite;
      }

      storage.setItems(vector);
   }

   public static void removeCipherSuite(int cipherSuite) {
      TLSCipherSuiteStorage storage = TLSCipherSuiteStorage.getInstance();
      int[] vector = storage.getItems();

      for (int i = 0; i < vector.length; i++) {
         if (vector[i] == cipherSuite) {
            for (int j = i + 1; j < vector.length; i++) {
               vector[i] = vector[j];
               j++;
            }

            Array.resize(vector, vector.length - 1);
            break;
         }
      }

      storage.setItems(vector);
   }

   public static void removeAll() {
      TLSCipherSuiteStorage storage = TLSCipherSuiteStorage.getInstance();
      storage.setItems(new int[0]);
   }

   public static void prioritize(int higher, int lower) {
      TLSCipherSuiteStorage storage = TLSCipherSuiteStorage.getInstance();
      int[] vector = storage.getItems();
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
               storage.setItems(vector);
            }
         }
      }
   }

   private static void verifyPriorities() {
      TLSCipherSuiteStorage storage = TLSCipherSuiteStorage.getInstance();
      int[] ciphersuites = storage.getItems();
      if (ciphersuites != null) {
         int length = ciphersuites.length;
         int insertionIndex = 0;

         for (int i = 0; i < length; i++) {
            if (!notSupported(ciphersuites[i])) {
               ciphersuites[insertionIndex++] = ciphersuites[i];
            }
         }

         if (insertionIndex != length) {
            Array.resize(ciphersuites, insertionIndex);
            storage.setItems(ciphersuites);
         }
      }
   }

   public static int[] getPriority() {
      verifyPriorities();
      TLSCipherSuiteStorage storage = TLSCipherSuiteStorage.getInstance();
      return Arrays.copy(storage.getItems());
   }

   public static int[] getDefaultCipherSuites() {
      return getSupportedCipherSuites();
   }

   public static boolean notSupported(int cipherSuite) {
      TLSOptionStore options = TLSOptionStore.getOptions();
      boolean restrictFIPS = options.restrictFIPSCipherSuites();
      boolean allowExport = options.allowExportCipherSuites();
      if (TLSUtilities.findCipherSuite(cipherSuite, _supportedFIPSNonExportCipherSuites)) {
         return false;
      }

      if (!allowExport) {
         if (!restrictFIPS && TLSUtilities.findCipherSuite(cipherSuite, _supportedNonExportCipherSuites)) {
            return false;
         }
      } else if (restrictFIPS) {
         if (TLSUtilities.findCipherSuite(cipherSuite, _supportedFIPSExportCipherSuites)) {
            return false;
         }
      } else {
         if (TLSUtilities.findCipherSuite(cipherSuite, _supportedFIPSExportCipherSuites)) {
            return false;
         }

         if (TLSUtilities.findCipherSuite(cipherSuite, _supportedNonExportCipherSuites)) {
            return false;
         }

         if (TLSUtilities.findCipherSuite(cipherSuite, _supportedExportCipherSuites)) {
            return false;
         }
      }

      return true;
   }

   public static boolean export(int cipherSuite) {
      return TLSUtilities.findCipherSuite(cipherSuite, _supportedExportCipherSuites)
         ? true
         : TLSUtilities.findCipherSuite(cipherSuite, _supportedFIPSExportCipherSuites);
   }

   public static String getPublicKeyAlgorithm(int cipherSuite) {
      switch (cipherSuite) {
         case 0:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 52:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 75:
         case 76:
            return null;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 14:
         case 15:
         case 16:
         case 20:
         case 21:
         case 22:
         case 47:
         case 49:
         case 51:
         case 53:
         case 55:
         case 57:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         default:
            return "RSA";
         case 11:
         case 12:
         case 13:
         case 17:
         case 18:
         case 19:
         case 48:
         case 50:
         case 54:
         case 56:
            return "DSA";
         case 71:
         case 72:
         case 73:
         case 74:
            return "EC";
      }
   }
}
