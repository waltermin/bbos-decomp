package net.rim.device.api.crypto;

import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;

public class EncryptorFactory {
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(5724636610518248458L);

   protected EncryptorFactory() {
   }

   public static EncryptorOutputStream getEncryptorOutputStream(Key key, OutputStream stream) {
      if (key == null) {
         throw new IllegalArgumentException();
      } else {
         return getEncryptorOutputStream(key, stream, key.getAlgorithm(), null);
      }
   }

   public static EncryptorOutputStream getEncryptorOutputStream(Key key, OutputStream stream, String algorithm) {
      return getEncryptorOutputStream(key, stream, algorithm, null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static EncryptorOutputStream getEncryptorOutputStream(Key key, OutputStream stream, String algorithm, InitializationVector iv) throws NoSuchAlgorithmException {
      if (key == null) {
         throw new IllegalArgumentException();
      }

      if (algorithm == null) {
         algorithm = key.getAlgorithm();
      }

      if (algorithm.equals("RSA")) {
         algorithm = "RSA/PKCS1";
      }

      String subAlgorithm = RIMFactoryUtilities.getRightMostSubAlgorithm(algorithm);
      String nextAlgorithm = RIMFactoryUtilities.stripRightMostSubAlgorithm(algorithm);
      EncryptorFactory factory = (EncryptorFactory)_hashtable.get(nextAlgorithm == null ? RIMFactoryUtilities.getBaseAlgorithm(subAlgorithm) : subAlgorithm);
      if (factory == null) {
         throw new NoSuchAlgorithmException(algorithm);
      }

      boolean var10 = false /* VF: Semaphore variable */;

      Object object;
      try {
         var10 = true;
         object = factory.create(subAlgorithm, nextAlgorithm, key, stream, iv);
         var10 = false;
      } finally {
         if (var10) {
            throw new IllegalArgumentException();
         }
      }

      if (!(object instanceof EncryptorOutputStream)) {
         if (object instanceof BlockEncryptorEngine) {
            return new BlockEncryptor((BlockEncryptorEngine)object, stream);
         } else if (object instanceof BlockFormatterEngine) {
            return new BlockEncryptor((BlockFormatterEngine)object, stream);
         } else if (object instanceof PseudoRandomSource) {
            return new PRNGEncryptor((PseudoRandomSource)object, stream);
         } else {
            throw new NoSuchAlgorithmException(algorithm);
         }
      } else {
         return (EncryptorOutputStream)object;
      }
   }

   public static BlockEncryptorEngine getBlockEncryptorEngine(Key key) {
      if (key == null) {
         throw new IllegalArgumentException();
      } else {
         return getBlockEncryptorEngine(key, key.getAlgorithm(), null);
      }
   }

   public static BlockEncryptorEngine getBlockEncryptorEngine(Key key, String algorithm) {
      return getBlockEncryptorEngine(key, algorithm, null);
   }

   public static BlockEncryptorEngine getBlockEncryptorEngine(Key param0, String param1, InitializationVector param2) throws NoSuchAlgorithmException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: ifnonnull 0c
      // 04: new java/lang/IllegalArgumentException
      // 07: dup
      // 08: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0b: athrow
      // 0c: aload 1
      // 0d: ifnonnull 17
      // 10: aload 0
      // 11: invokeinterface net/rim/device/api/crypto/Key.getAlgorithm ()Ljava/lang/String; 1
      // 16: astore 1
      // 17: aload 1
      // 18: invokestatic net/rim/device/api/crypto/RIMFactoryUtilities.getRightMostSubAlgorithm (Ljava/lang/String;)Ljava/lang/String;
      // 1b: astore 3
      // 1c: aload 1
      // 1d: invokestatic net/rim/device/api/crypto/RIMFactoryUtilities.stripRightMostSubAlgorithm (Ljava/lang/String;)Ljava/lang/String;
      // 20: astore 4
      // 22: getstatic net/rim/device/api/crypto/EncryptorFactory._hashtable Ljava/util/Hashtable;
      // 25: aload 4
      // 27: ifnonnull 31
      // 2a: aload 3
      // 2b: invokestatic net/rim/device/api/crypto/RIMFactoryUtilities.getBaseAlgorithm (Ljava/lang/String;)Ljava/lang/String;
      // 2e: goto 32
      // 31: aload 3
      // 32: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 35: checkcast net/rim/device/api/crypto/EncryptorFactory
      // 38: astore 5
      // 3a: aload 5
      // 3c: ifnonnull 48
      // 3f: new net/rim/device/api/crypto/NoSuchAlgorithmException
      // 42: dup
      // 43: aload 1
      // 44: invokespecial net/rim/device/api/crypto/NoSuchAlgorithmException.<init> (Ljava/lang/String;)V
      // 47: athrow
      // 48: aload 5
      // 4a: aload 3
      // 4b: aload 4
      // 4d: aload 0
      // 4e: aconst_null
      // 4f: aload 2
      // 50: invokevirtual net/rim/device/api/crypto/EncryptorFactory.create (Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/api/crypto/Key;Ljava/io/OutputStream;Lnet/rim/device/api/crypto/InitializationVector;)Ljava/lang/Object;
      // 53: checkcast net/rim/device/api/crypto/BlockEncryptorEngine
      // 56: areturn
      // 57: astore 6
      // 59: new java/lang/IllegalArgumentException
      // 5c: dup
      // 5d: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 60: athrow
      // 61: astore 6
      // 63: new java/lang/IllegalArgumentException
      // 66: dup
      // 67: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 6a: athrow
      // try (34 -> 42): 43 null
      // try (34 -> 42): 48 null
   }

   public static void register(EncryptorFactory factory) {
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

   protected Object create(String _1, String _2, Key _3, OutputStream _4, InitializationVector _5) {
      throw null;
   }
}
