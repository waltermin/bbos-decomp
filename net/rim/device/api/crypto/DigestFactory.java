package net.rim.device.api.crypto;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public class DigestFactory {
   private static final String[] DIGESTS = new String[]{"MD2", "MD4", "MD5", "RIPEMD128", "RIPEMD160", "SHA1", "SHA224", "SHA256", "SHA384", "SHA512"};
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(-428654658655490112L);

   protected DigestFactory() {
   }

   public static Digest getInstance(String algorithm) throws NoSuchAlgorithmException {
      if (algorithm == null) {
         throw new IllegalArgumentException();
      }

      String checkAlgorithm = algorithm;

      do {
         String baseAlgorithm = RIMFactoryUtilities.getBaseAlgorithm(checkAlgorithm);
         DigestFactory factory = (DigestFactory)_hashtable.get(baseAlgorithm);
         if (factory != null) {
            try {
               return factory.create(baseAlgorithm);
            } catch (ClassCastException e) {
               throw new IllegalArgumentException();
            }
         }

         checkAlgorithm = RIMFactoryUtilities.stripBaseAlgorithm(checkAlgorithm);
      } while (checkAlgorithm != null);

      throw new NoSuchAlgorithmException(algorithm);
   }

   public static boolean isDigestWeakByPolicy(String algorithm) {
      int flag = Arrays.getIndex(DIGESTS, algorithm);
      if (flag == -1) {
         return false;
      }

      int policyMask = ITPolicy.getInteger(24, 83, 0);
      return (policyMask & 1 << flag) != 0;
   }

   public static void register(DigestFactory factory) {
      if (factory == null) {
         throw new IllegalArgumentException();
      }

      String[] algorithms = factory.getFactoryAlgorithms();

      for (int i = 0; i < algorithms.length; i++) {
         if (_hashtable.get(algorithms[i]) == null) {
            _hashtable.put(algorithms[i], factory);
         }
      }
   }

   public static Enumeration getAlgorithms() {
      return _hashtable.keys();
   }

   protected String[] getFactoryAlgorithms() {
      throw null;
   }

   protected Digest create(String _1) {
      throw null;
   }
}
