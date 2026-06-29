package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class ShowLocationDocumentVerb extends Verb {
   String _link;

   public ShowLocationDocumentVerb(String link) {
      super(1265702);
      this._link = link;
   }

   @Override
   public final String toString() {
      return LBSResources.getString(31);
   }

   final byte[] decode(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ldc_w "doc="
      // 04: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 07: bipush 4
      // 09: iadd
      // 0a: istore 2
      // 0b: aload 1
      // 0c: invokevirtual java/lang/String.length ()I
      // 0f: iload 2
      // 10: isub
      // 11: istore 3
      // 12: new java/lang/Object
      // 15: dup
      // 16: aload 1
      // 17: invokevirtual java/lang/String.getBytes ()[B
      // 1a: iload 2
      // 1b: iload 3
      // 1c: invokespecial java/io/ByteArrayInputStream.<init> ([BII)V
      // 1f: astore 4
      // 21: new java/lang/Object
      // 24: dup
      // 25: aload 4
      // 27: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 2a: astore 5
      // 2c: iload 3
      // 2d: newarray 8
      // 2f: astore 6
      // 31: aload 5
      // 33: aload 6
      // 35: invokevirtual net/rim/device/api/io/Base64InputStream.read ([B)I
      // 38: pop
      // 39: new java/lang/Object
      // 3c: dup
      // 3d: aload 6
      // 3f: bipush 0
      // 40: aload 6
      // 42: arraylength
      // 43: bipush 1
      // 44: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 47: astore 7
      // 49: new java/lang/Object
      // 4c: dup
      // 4d: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 50: astore 8
      // 52: aload 7
      // 54: aload 8
      // 56: invokestatic net/rim/device/internal/crypto/CryptoBlock.decode (Lnet/rim/device/api/util/DataBuffer;Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 59: pop
      // 5a: aload 8
      // 5c: invokevirtual net/rim/device/api/util/DataBuffer.toArray ()[B
      // 5f: areturn
      // 60: astore 6
      // 62: aconst_null
      // 63: areturn
      // 64: astore 6
      // 66: aconst_null
      // 67: areturn
      // try (24 -> 50): 51 null
      // try (24 -> 50): 54 null
   }

   @Override
   public final Object invoke(Object context) {
      byte[] data = this.decode(this._link);
      if (data != null) {
         LBSApplication.openDocument("application/vnd.rim.location", data);
      }

      return null;
   }
}
