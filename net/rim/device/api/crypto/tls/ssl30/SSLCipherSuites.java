package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.ssl.TLSOptionStore;
import net.rim.vm.Array;

public final class SSLCipherSuites {
   public static final int SSL_NULL_WITH_NULL_NULL;
   public static final int SSL_RSA_EXPORT_WITH_RC4_40_MD5;
   public static final int SSL_DH_anon_EXPORT_WITH_RC4_40_MD5;
   public static final int SSL_RSA_EXPORT_WITH_DES40_CBC_SHA;
   public static final int SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA;
   public static final int SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA;
   public static final int SSL_DH_RSA_EXPORT_WITH_DES40_CBC_SHA;
   public static final int SSL_RSA_WITH_RC4_128_MD5;
   public static final int SSL_RSA_WITH_RC4_128_SHA;
   public static final int SSL_RSA_WITH_3DES_EDE_CBC_SHA;
   public static final int SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA;
   public static final int SSL_DH_anon_WITH_RC4_128_MD5;
   public static final int SSL_DH_anon_WITH_3DES_EDE_CBC_SHA;
   public static final int SSL_RSA_WITH_DES_CBC_SHA;
   public static final int SSL_DHE_DSS_WITH_DES_CBC_SHA;
   public static final int SSL_DH_anon_WITH_DES_CBC_SHA;
   public static final int SSL_DHE_RSA_WITH_DES_CBC_SHA;
   public static final int SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA;
   public static final int SSL_DH_DSS_WITH_DES_CBC_SHA;
   public static final int SSL_DH_DSS_WITH_3DES_EDE_CBC_SHA;
   public static final int SSL_DH_RSA_WITH_DES_CBC_SHA;
   public static final int SSL_DH_RSA_WITH_3DES_EDE_CBC_SHA;
   public static final int SSL_FORTEZZA_KEA_WITH_NULL_SHA;
   public static final int SSL_FORTEZZA_KEA_WITH_FORTEZZA_CBC_SHA;
   public static final int SSL_FORTEZZA_KEA_WITH_RC4_128_SHA;
   public static final int SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5;
   public static final int SSL_RSA_WITH_IDEA_CBC_SHA;
   public static final int SSL_DH_DSS_EXPORT_WITH_DES40_CBC_SHA;
   public static final int SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA;
   public static final int SSL_RSA_WITH_NULL_MD5;
   public static final int SSL_RSA_WITH_NULL_SHA;
   private static int[] _supportedExportCipherSuites = new int[]{3, 14, 23, -804651005, 4, 5, 24, -804651005, 8, 17, 25, -804651000};
   private static int[] _supportedFIPSExportCipherSuites = new int[]{8, 17, 25, -804651000, 10, 19, 9, 18, 22, 21, 27, 26};
   private static int[] _supportedNonExportCipherSuites = new int[]{4, 5, 24, -804651005, 8, 17, 25, -804651000, 10, 19, 9, 18};
   private static int[] _supportedFIPSNonExportCipherSuites = new int[]{
      10,
      19,
      9,
      18,
      22,
      21,
      27,
      26,
      -805043656,
      872579632,
      -1593736656,
      -1392373758,
      1162772070,
      1867996926,
      1578735676,
      221298909,
      -2044065530,
      234325576,
      84017409,
      828321792,
      101265419,
      100947203,
      1398080019,
      506470449,
      72680198,
      1377243914,
      1142964563,
      543257697,
      1969448275,
      2037672306,
      1850286124,
      774975075
   };

   private SSLCipherSuites() {
   }

   public static final int[] getSupportedCipherSuites() {
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

   public static final int[] getSupportedExportCipherSuites() {
      return Arrays.copy(_supportedExportCipherSuites);
   }

   public static final int[] getSupportedNonExportCipherSuites() {
      return Arrays.copy(_supportedNonExportCipherSuites);
   }

   public static final int[] getSupportedFIPSExportCipherSuites() {
      return Arrays.copy(_supportedFIPSExportCipherSuites);
   }

   public static final int[] getSupportedFIPSNonExportCipherSuites() {
      return Arrays.copy(_supportedFIPSNonExportCipherSuites);
   }

   public static final void addCipherSuite(int cipherSuite, int priority) {
      if (notSupported(cipherSuite)) {
         throw new Object();
      }

      removeCipherSuite(cipherSuite);
      SSLCipherSuiteStorage storage = SSLCipherSuiteStorage.getInstance();
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

   public static final void removeCipherSuite(int cipherSuite) {
      SSLCipherSuiteStorage storage = SSLCipherSuiteStorage.getInstance();
      int[] vector = storage.getItems();
      int vectorLength = vector.length;

      for (int i = 0; i < vectorLength; i++) {
         if (vector[i] == cipherSuite) {
            for (int j = i + 1; j < vectorLength; i++) {
               vector[i] = vector[j];
               j++;
            }

            Array.resize(vector, vectorLength - 1);
            break;
         }
      }

      storage.setItems(vector);
   }

   public static final void removeAll() {
      SSLCipherSuiteStorage storage = SSLCipherSuiteStorage.getInstance();
      storage.setItems(new int[0]);
   }

   public static final void prioritize(int higher, int lower) {
      SSLCipherSuiteStorage storage = SSLCipherSuiteStorage.getInstance();
      int[] vector = storage.getItems();
      if (higher != lower) {
         int searchHigher = -1;
         int searchLower = -1;
         int vectorLength = vector.length;

         for (int i = 0; i < vectorLength; i++) {
            if (vector[i] == higher) {
               searchHigher = i;
            } else if (vector[i] == lower) {
               searchLower = i;
            }
         }

         if (searchHigher != -1 && searchLower != -1) {
            if (searchHigher >= searchLower) {
               for (int i = vectorLength - 1; i > searchLower; i--) {
                  vector[i] = vector[i - 1];
               }

               vector[searchLower] = higher;
               storage.setItems(vector);
            }
         }
      }
   }

   private static final void verifyPriorities() {
      SSLCipherSuiteStorage storage = SSLCipherSuiteStorage.getInstance();
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

   public static final int[] getPriority() {
      verifyPriorities();
      SSLCipherSuiteStorage storage = SSLCipherSuiteStorage.getInstance();
      return Arrays.copy(storage.getItems());
   }

   public static final int[] getDefaultCipherSuites() {
      return getSupportedCipherSuites();
   }

   public static final boolean notSupported(int cipherSuite) {
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

   public static final boolean export(int cipherSuite) {
      return TLSUtilities.findCipherSuite(cipherSuite, _supportedExportCipherSuites)
         ? true
         : TLSUtilities.findCipherSuite(cipherSuite, _supportedFIPSExportCipherSuites);
   }
}
