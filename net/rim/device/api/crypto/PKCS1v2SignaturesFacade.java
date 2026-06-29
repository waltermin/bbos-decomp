package net.rim.device.api.crypto;

import net.rim.device.api.crypto.oid.OID;

class PKCS1v2SignaturesFacade {
   private static PKCS1v2SignaturesFacade _signaturesFacade;

   static boolean available() {
      return _signaturesFacade != null;
   }

   static byte[] sign(OID digestOid, Digest digest) {
      return _signaturesFacade != null ? _signaturesFacade.sign0(digestOid, digest) : null;
   }

   byte[] sign0(OID _1, Digest _2) {
      throw null;
   }

   static boolean verify(byte[] encodedMessage, int encodedMessageOffset, byte[] message, OID digestOid) {
      return _signaturesFacade != null ? _signaturesFacade.verify0(encodedMessage, encodedMessageOffset, message, digestOid) : false;
   }

   boolean verify0(byte[] _1, int _2, byte[] _3, OID _4) {
      throw null;
   }

   static {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w "net.rim.device.api.crypto.PKCS1v2SignaturesFacadeImpl"
      // 03: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 06: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 09: checkcast net/rim/device/api/crypto/PKCS1v2SignaturesFacade
      // 0c: putstatic net/rim/device/api/crypto/PKCS1v2SignaturesFacade._signaturesFacade Lnet/rim/device/api/crypto/PKCS1v2SignaturesFacade;
      // 0f: return
      // 10: astore 0
      // 11: return
      // 12: astore 0
      // 13: new java/lang/RuntimeException
      // 16: dup
      // 17: aload 0
      // 18: invokevirtual java/lang/InstantiationException.toString ()Ljava/lang/String;
      // 1b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1e: athrow
      // 1f: astore 0
      // 20: new java/lang/RuntimeException
      // 23: dup
      // 24: aload 0
      // 25: invokevirtual java/lang/IllegalAccessException.toString ()Ljava/lang/String;
      // 28: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 2b: athrow
      // try (0 -> 5): 6 null
      // try (0 -> 5): 8 null
      // try (0 -> 5): 15 null
   }
}
