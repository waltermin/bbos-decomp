package net.rim.device.api.crypto;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;

public class SignatureSignerFactory {
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(9005613039819088990L);

   protected SignatureSignerFactory() {
   }

   public static SignatureSigner getInstance(PrivateKey key, String algorithm) {
      if (key == null) {
         throw new Object();
      }

      if (algorithm == null) {
         algorithm = key.getAlgorithm();
      } else if (algorithm.charAt(0) == '/') {
         algorithm = ((StringBuffer)(new Object())).append(key.getAlgorithm()).append(algorithm).toString();
      }

      String signatureAlgorithm = RIMFactoryUtilities.getLeftMostSubAlgorithm(algorithm);
      String digestAlgorithm = RIMFactoryUtilities.stripLeftMostSubAlgorithm(algorithm);
      SignatureSignerFactory factory = (SignatureSignerFactory)_hashtable.get(signatureAlgorithm);
      if (factory == null) {
         throw new Object(algorithm);
      }

      try {
         return factory.createSignatureSigner(signatureAlgorithm, digestAlgorithm, key);
      } finally {
         throw new Object();
      }
   }

   public static void register(SignatureSignerFactory factory) {
      if (factory == null) {
         throw new Object();
      }

      String[] algorithms = factory.getFactoryAlgorithms();

      for (int i = 0; i < algorithms.length; i++) {
         if (_hashtable.get(algorithms[i]) == null) {
            _hashtable.put(algorithms[i], factory);
         }
      }
   }

   protected String[] getFactoryAlgorithms() {
      throw null;
   }

   public static Enumeration getAlgorithms() {
      return _hashtable.keys();
   }

   protected SignatureSigner createSignatureSigner(String _1, String _2, PrivateKey _3) {
      throw null;
   }
}
