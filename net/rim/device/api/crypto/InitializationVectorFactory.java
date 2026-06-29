package net.rim.device.api.crypto;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.ToIntHashtable;

public class InitializationVectorFactory {
   private static final long HASHTABLE_ID = -2089495853761416774L;
   private static ToIntHashtable _hashtable;

   protected InitializationVectorFactory() {
   }

   public static InitializationVector getInstance(String algorithm, byte[] data, int offset, int maxLength) {
      if (algorithm != null && data != null && offset >= 0 && maxLength >= 0 && data.length - maxLength >= offset) {
         return privateGetInstance(algorithm, data, offset, maxLength);
      } else {
         throw new Object();
      }
   }

   private static InitializationVector privateGetInstance(String algorithm, byte[] data, int offset, int maxLength) {
      algorithm = RIMFactoryUtilities.getLeftMostSubAlgorithm(algorithm);
      String baseAlgorithm = RIMFactoryUtilities.getBaseAlgorithm(algorithm);
      int blockBitLength = _hashtable.get(baseAlgorithm);

      label42:
      try {
         blockBitLength = RIMFactoryUtilities.getBlockBitLength(baseAlgorithm, blockBitLength);
      } finally {
         break label42;
      }

      if (blockBitLength < 0) {
         throw new Object(algorithm);
      }

      int blockByteLength = blockBitLength >>> 3;
      if (data == null) {
         data = RandomSource.getBytes(blockByteLength);
         offset = 0;
      } else if (maxLength < blockByteLength) {
         throw new Object(algorithm);
      }

      if (offset < 0) {
         throw new Object();
      } else {
         return new InitializationVector(data, offset, blockByteLength);
      }
   }

   public static InitializationVector getInstance(String algorithm, byte[] data, int offset) {
      return getInstance(algorithm, data, offset, data == null ? 0 : data.length - offset);
   }

   public static InitializationVector getInstance(String algorithm) {
      if (algorithm == null) {
         throw new Object();
      } else {
         return privateGetInstance(algorithm, null, 0, 0);
      }
   }

   public static Enumeration getAlgorithms() {
      return _hashtable.keys();
   }

   public static void register(String algorithm, int defaultLength) {
      if (algorithm != null && defaultLength > 0) {
         if (_hashtable.get(algorithm) == -1) {
            _hashtable.put(algorithm, defaultLength);
         }
      } else {
         throw new Object();
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      _hashtable = (ToIntHashtable)registry.getOrWaitFor(-2089495853761416774L);
      if (_hashtable == null) {
         _hashtable = (ToIntHashtable)(new Object());
         registry.put(-2089495853761416774L, _hashtable);
      }
   }
}
