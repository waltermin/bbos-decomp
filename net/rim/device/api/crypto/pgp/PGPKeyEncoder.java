package net.rim.device.api.crypto.pgp;

import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;

public final class PGPKeyEncoder {
   private PGPKeyEncoder() {
   }

   public static final byte[] getEncoding(PGPCertificate certificate) {
      if (certificate == null) {
         throw new Object();
      } else {
         return getEncoding(certificate.getEncoding());
      }
   }

   public static final byte[] getEncoding(byte[] param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: ifnonnull 0c
      // 04: new java/lang/Object
      // 07: dup
      // 08: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0b: athrow
      // 0c: new net/rim/device/internal/crypto/pgp/PGPPacketParser
      // 0f: dup
      // 10: aload 0
      // 11: invokespecial net/rim/device/internal/crypto/pgp/PGPPacketParser.<init> ([B)V
      // 14: astore 1
      // 15: aload 1
      // 16: invokevirtual net/rim/device/internal/crypto/pgp/PGPPacketParser.getPackets ()[[Lnet/rim/device/internal/crypto/pgp/PGPPacket;
      // 19: astore 2
      // 1a: aload 2
      // 1b: ifnull 2a
      // 1e: aload 2
      // 1f: arraylength
      // 20: bipush 1
      // 21: if_icmplt 2a
      // 24: aload 2
      // 25: bipush 0
      // 26: aaload
      // 27: ifnonnull 32
      // 2a: new java/lang/Object
      // 2d: dup
      // 2e: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 31: athrow
      // 32: aload 2
      // 33: bipush 0
      // 34: aaload
      // 35: astore 3
      // 36: new java/lang/Object
      // 39: dup
      // 3a: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 3d: astore 4
      // 3f: aload 3
      // 40: ifnonnull 47
      // 43: bipush 0
      // 44: goto 49
      // 47: aload 3
      // 48: arraylength
      // 49: istore 5
      // 4b: bipush 0
      // 4c: istore 6
      // 4e: iload 6
      // 50: iload 5
      // 52: if_icmpge 86
      // 55: aload 3
      // 56: iload 6
      // 58: aaload
      // 59: instanceof net/rim/device/internal/crypto/pgp/PGPTrustPacket
      // 5c: ifeq 62
      // 5f: goto 80
      // 62: aload 3
      // 63: iload 6
      // 65: aaload
      // 66: invokevirtual net/rim/device/internal/crypto/pgp/PGPPacket.getEncoding ()[B
      // 69: astore 0
      // 6a: aload 4
      // 6c: aload 3
      // 6d: iload 6
      // 6f: aaload
      // 70: invokevirtual net/rim/device/internal/crypto/pgp/PGPPacket.getTag ()I
      // 73: aload 0
      // 74: arraylength
      // 75: bipush 4
      // 77: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.writeTagAndLength (Ljava/io/OutputStream;III)V
      // 7a: aload 4
      // 7c: aload 0
      // 7d: invokevirtual java/io/OutputStream.write ([B)V
      // 80: iinc 6 1
      // 83: goto 4e
      // 86: aload 4
      // 88: invokevirtual java/io/ByteArrayOutputStream.close ()V
      // 8b: aload 4
      // 8d: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 90: areturn
      // 91: astore 1
      // 92: goto 9a
      // 95: astore 1
      // 96: goto 9a
      // 99: astore 1
      // 9a: new java/lang/Object
      // 9d: dup
      // 9e: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // a1: athrow
      // try (6 -> 77): 78 net/rim/device/api/crypto/pgp/PGPEncodingException
      // try (6 -> 77): 80 null
      // try (6 -> 77): 82 null
   }
}
