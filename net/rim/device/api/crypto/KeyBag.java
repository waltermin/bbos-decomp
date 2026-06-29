package net.rim.device.api.crypto;

public class KeyBag extends SafeBag {
   protected PrivateKey _key;

   public KeyBag(byte[] data, byte[] attributes, PKCS12ContentInfo parent) {
      super(data, attributes, parent);
   }

   @Override
   protected void parse() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/SafeBag._parsed Z
      // 04: ifeq 08
      // 07: return
      // 08: new java/lang/Object
      // 0b: dup
      // 0c: aload 0
      // 0d: getfield net/rim/device/api/crypto/SafeBag._bagData [B
      // 10: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 13: astore 1
      // 14: aload 0
      // 15: aload 1
      // 16: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 19: ldc_w "PKCS8"
      // 1c: invokestatic net/rim/device/api/crypto/encoder/PrivateKeyDecoder.decode ([BLjava/lang/String;)Lnet/rim/device/api/crypto/PrivateKey;
      // 1f: putfield net/rim/device/api/crypto/KeyBag._key Lnet/rim/device/api/crypto/PrivateKey;
      // 22: aload 0
      // 23: getfield net/rim/device/api/crypto/SafeBag._bagAttributes [B
      // 26: ifnull 73
      // 29: new java/lang/Object
      // 2c: dup
      // 2d: aload 0
      // 2e: getfield net/rim/device/api/crypto/SafeBag._bagAttributes [B
      // 31: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 34: astore 2
      // 35: aload 2
      // 36: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 39: istore 3
      // 3a: iload 3
      // 3b: bipush -1
      // 3d: if_icmpeq 73
      // 40: iload 3
      // 41: bipush 16
      // 43: if_icmpne 63
      // 46: aload 2
      // 47: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 4a: aload 0
      // 4b: getfield net/rim/device/api/crypto/SafeBag._attributes Ljava/util/Vector;
      // 4e: new net/rim/device/api/crypto/PKCS12Attribute
      // 51: dup
      // 52: aload 2
      // 53: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 56: aload 2
      // 57: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 5a: invokespecial net/rim/device/api/crypto/PKCS12Attribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[B)V
      // 5d: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 60: goto 6b
      // 63: new net/rim/device/api/crypto/PKCS12ParsingException
      // 66: dup
      // 67: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // 6a: athrow
      // 6b: aload 2
      // 6c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 6f: istore 3
      // 70: goto 3a
      // 73: aload 0
      // 74: bipush 1
      // 75: putfield net/rim/device/api/crypto/SafeBag._parsed Z
      // 78: return
      // 79: astore 1
      // 7a: return
      // 7b: astore 1
      // 7c: return
      // 7d: astore 1
      // 7e: return
      // 7f: astore 1
      // 80: return
      // 81: astore 1
      // 82: return
      // 83: astore 1
      // 84: return
      // 85: astore 1
      // 86: return
      // 87: astore 1
      // 88: new net/rim/device/api/crypto/PKCS12ParsingException
      // 8b: dup
      // 8c: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // 8f: athrow
      // try (4 -> 58): 59 null
      // try (4 -> 58): 61 null
      // try (4 -> 58): 63 null
      // try (4 -> 58): 65 null
      // try (4 -> 58): 67 null
      // try (4 -> 58): 69 null
      // try (4 -> 58): 71 null
      // try (4 -> 58): 73 null
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      return null;
   }

   public PrivateKey getPrivateKey() {
      this.parse();
      return this._key;
   }
}
