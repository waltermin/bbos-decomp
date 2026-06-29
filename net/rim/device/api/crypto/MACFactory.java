package net.rim.device.api.crypto;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;

public class MACFactory {
   private static Hashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(7919661442840010148L);

   protected MACFactory() {
   }

   public static MAC getInstance(SymmetricKey key) {
      String algorithm = key != null ? key.getAlgorithm() : "Null";
      return getInstance(algorithm, key);
   }

   public static MAC getInstance(String param0, SymmetricKey param1) throws NoSuchAlgorithmException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 10
      // 04: aload 0
      // 05: ifnonnull 10
      // 08: new java/lang/IllegalArgumentException
      // 0b: dup
      // 0c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0f: athrow
      // 10: aload 0
      // 11: ifnonnull 1e
      // 14: aload 1
      // 15: invokeinterface net/rim/device/api/crypto/Key.getAlgorithm ()Ljava/lang/String; 1
      // 1a: astore 0
      // 1b: goto 44
      // 1e: aload 0
      // 1f: bipush 0
      // 20: invokevirtual java/lang/String.charAt (I)C
      // 23: bipush 47
      // 25: if_icmpne 44
      // 28: aload 1
      // 29: ifnull 44
      // 2c: new java/lang/StringBuffer
      // 2f: dup
      // 30: invokespecial java/lang/StringBuffer.<init> ()V
      // 33: aload 1
      // 34: invokeinterface net/rim/device/api/crypto/Key.getAlgorithm ()Ljava/lang/String; 1
      // 39: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3c: aload 0
      // 3d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 40: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 43: astore 0
      // 44: aload 0
      // 45: invokestatic net/rim/device/api/crypto/RIMFactoryUtilities.getLeftMostSubAlgorithm (Ljava/lang/String;)Ljava/lang/String;
      // 48: astore 2
      // 49: aload 0
      // 4a: invokestatic net/rim/device/api/crypto/RIMFactoryUtilities.stripLeftMostSubAlgorithm (Ljava/lang/String;)Ljava/lang/String;
      // 4d: astore 3
      // 4e: getstatic net/rim/device/api/crypto/MACFactory._hashtable Ljava/util/Hashtable;
      // 51: aload 2
      // 52: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 55: checkcast net/rim/device/api/crypto/MACFactory
      // 58: astore 4
      // 5a: aload 4
      // 5c: ifnonnull 75
      // 5f: aload 3
      // 60: ifnonnull 75
      // 63: ldc_w "CBCMAC"
      // 66: astore 2
      // 67: aload 0
      // 68: astore 3
      // 69: getstatic net/rim/device/api/crypto/MACFactory._hashtable Ljava/util/Hashtable;
      // 6c: aload 2
      // 6d: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 70: checkcast net/rim/device/api/crypto/MACFactory
      // 73: astore 4
      // 75: aload 4
      // 77: ifnull 9c
      // 7a: aload 4
      // 7c: aload 2
      // 7d: aload 3
      // 7e: aload 1
      // 7f: invokevirtual net/rim/device/api/crypto/MACFactory.create (Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/api/crypto/SymmetricKey;)Lnet/rim/device/api/crypto/MAC;
      // 82: areturn
      // 83: astore 5
      // 85: goto 9c
      // 88: astore 5
      // 8a: aload 5
      // 8c: athrow
      // 8d: astore 5
      // 8f: new java/lang/IllegalArgumentException
      // 92: dup
      // 93: aload 5
      // 95: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 98: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 9b: athrow
      // 9c: new net/rim/device/api/crypto/NoSuchAlgorithmException
      // 9f: dup
      // a0: aload 0
      // a1: invokespecial net/rim/device/api/crypto/NoSuchAlgorithmException.<init> (Ljava/lang/String;)V
      // a4: athrow
      // try (57 -> 62): 63 null
      // try (57 -> 62): 65 null
      // try (57 -> 62): 68 null
   }

   public static void register(MACFactory factory) {
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

   protected MAC create(String _1, String _2, SymmetricKey _3) {
      throw null;
   }
}
