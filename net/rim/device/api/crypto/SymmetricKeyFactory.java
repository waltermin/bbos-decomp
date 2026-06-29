package net.rim.device.api.crypto;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;

public class SymmetricKeyFactory {
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(5563321887427573875L);

   protected SymmetricKeyFactory() {
   }

   public static SymmetricKey getInstance(String algorithm, byte[] data, int offset, int length) {
      if (algorithm != null && data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         return privateGetInstance(algorithm, data, offset, length);
      } else {
         throw new Object();
      }
   }

   private static SymmetricKey privateGetInstance(String algorithm, byte[] data, int offset, int length) {
      algorithm = RIMFactoryUtilities.getLeftMostSubAlgorithm(algorithm);
      String checkAlgorithm = algorithm;

      do {
         String baseAlgorithm = RIMFactoryUtilities.getBaseAlgorithm(checkAlgorithm);
         SymmetricKeyFactory factory = (SymmetricKeyFactory)_hashtable.get(baseAlgorithm);
         if (factory != null) {
            try {
               int keyBitLength = factory.getDefaultKeyLength(baseAlgorithm);

               label70:
               try {
                  keyBitLength = RIMFactoryUtilities.getKeyBitLength(checkAlgorithm, keyBitLength);
               } finally {
                  break label70;
               }

               int keyByteLength = keyBitLength >>> 3;
               if (data == null) {
                  data = RandomSource.getBytes(keyByteLength);
                  length = data.length;
               }

               if (length < keyByteLength) {
                  throw new Object();
               } else {
                  return factory.create(baseAlgorithm, data, offset, keyBitLength);
               }
            } finally {
               throw new Object();
            }
         }

         checkAlgorithm = RIMFactoryUtilities.stripBaseAlgorithm(checkAlgorithm);
      } while (checkAlgorithm != null);

      throw new Object(algorithm);
   }

   public static SymmetricKey getInstance(String algorithm, byte[] data, int offset) {
      return getInstance(algorithm, data, offset, data == null ? 0 : data.length - offset);
   }

   public static SymmetricKey getInstance(String algorithm) {
      return privateGetInstance(algorithm, null, 0, 0);
   }

   public static void register(SymmetricKeyFactory factory) {
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

   public static Enumeration getAlgorithms() {
      return _hashtable.keys();
   }

   protected String[] getFactoryAlgorithms() {
      throw null;
   }

   protected SymmetricKey create(String _1, byte[] _2, int _3, int _4) {
      throw null;
   }

   protected int getDefaultKeyLength(String _1) {
      throw null;
   }
}
