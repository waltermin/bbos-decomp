package net.rim.device.api.crypto.cms;

import java.io.InputStream;
import net.rim.device.api.crypto.asn1.ASN1BitSet;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;

public final class EMSAcceptRequestInputStream extends CMSInputStream {
   private String[] _names;
   private String _displayName;
   private X509Certificate[] _certificates;
   private ASN1BitSet _flags;
   private int _version;
   private String _clientID;

   EMSAcceptRequestInputStream(InputStream param1) throws CMSParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: invokespecial net/rim/device/api/crypto/cms/CMSInputStream.<init> (Ljava/io/InputStream;)V
      // 005: new net/rim/device/api/crypto/asn1/ASN1InputStream
      // 008: dup
      // 009: aload 0
      // 00a: getfield net/rim/device/api/crypto/cms/CMSInputStream._input Ljava/io/InputStream;
      // 00d: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 010: astore 2
      // 011: aload 2
      // 012: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 015: astore 3
      // 016: aload 3
      // 017: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 01a: ifne 076
      // 01d: aload 3
      // 01e: bipush 2
      // 020: bipush 0
      // 021: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 024: astore 4
      // 026: aload 0
      // 027: bipush 0
      // 028: anewarray 58
      // 02b: putfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._names [Ljava/lang/String;
      // 02e: aload 4
      // 030: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 033: ifne 076
      // 036: aload 4
      // 038: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 03b: istore 5
      // 03d: iload 5
      // 03f: lookupswitch -17 1 1 17
      // 050: aload 0
      // 051: getfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._names [Ljava/lang/String;
      // 054: aload 0
      // 055: getfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._names [Ljava/lang/String;
      // 058: arraylength
      // 059: bipush 1
      // 05a: iadd
      // 05b: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 05e: aload 0
      // 05f: getfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._names [Ljava/lang/String;
      // 062: aload 0
      // 063: getfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._names [Ljava/lang/String;
      // 066: arraylength
      // 067: bipush 1
      // 068: isub
      // 069: aload 4
      // 06b: bipush 2
      // 06d: iload 5
      // 06f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readIA5String (II)Ljava/lang/String;
      // 072: aastore
      // 073: goto 02e
      // 076: aload 3
      // 077: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 07a: bipush 1
      // 07b: if_icmpne 089
      // 07e: aload 0
      // 07f: aload 3
      // 080: bipush 2
      // 082: bipush 1
      // 083: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readUTF8String (II)Ljava/lang/String;
      // 086: putfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._displayName Ljava/lang/String;
      // 089: aload 3
      // 08a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 08d: bipush 2
      // 08f: if_icmpne 0d2
      // 092: aload 3
      // 093: bipush 2
      // 095: bipush 2
      // 097: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 09a: astore 4
      // 09c: aload 0
      // 09d: bipush 0
      // 09e: anewarray 144
      // 0a1: putfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._certificates [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 0a4: aload 4
      // 0a6: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 0a9: ifne 0d2
      // 0ac: aload 0
      // 0ad: getfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._certificates [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 0b0: aload 0
      // 0b1: getfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._certificates [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 0b4: arraylength
      // 0b5: bipush 1
      // 0b6: iadd
      // 0b7: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 0ba: aload 0
      // 0bb: getfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._certificates [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 0be: aload 0
      // 0bf: getfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._certificates [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 0c2: arraylength
      // 0c3: bipush 1
      // 0c4: isub
      // 0c5: new net/rim/device/api/crypto/certificate/x509/X509Certificate
      // 0c8: dup
      // 0c9: aload 4
      // 0cb: invokespecial net/rim/device/api/crypto/certificate/x509/X509Certificate.<init> (Lnet/rim/device/api/crypto/asn1/ASN1InputStream;)V
      // 0ce: aastore
      // 0cf: goto 0a4
      // 0d2: aload 3
      // 0d3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 0d6: bipush 3
      // 0d8: if_icmpne 0e7
      // 0db: aload 0
      // 0dc: aload 3
      // 0dd: bipush 2
      // 0df: bipush 3
      // 0e1: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readBitString (II)Lnet/rim/device/api/crypto/asn1/ASN1BitSet;
      // 0e4: putfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._flags Lnet/rim/device/api/crypto/asn1/ASN1BitSet;
      // 0e7: aload 3
      // 0e8: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 0eb: bipush 4
      // 0ed: if_icmpne 0fc
      // 0f0: aload 0
      // 0f1: aload 3
      // 0f2: bipush 2
      // 0f4: bipush 4
      // 0f6: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger (II)I
      // 0f9: putfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._version I
      // 0fc: aload 3
      // 0fd: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 100: bipush 5
      // 102: if_icmpne 125
      // 105: aload 0
      // 106: aload 3
      // 107: bipush 2
      // 109: bipush 5
      // 10b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readUTF8String (II)Ljava/lang/String;
      // 10e: putfield net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream._clientID Ljava/lang/String;
      // 111: return
      // 112: astore 2
      // 113: new net/rim/device/api/crypto/cms/CMSParsingException
      // 116: dup
      // 117: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 11a: athrow
      // 11b: astore 2
      // 11c: new net/rim/device/api/crypto/cms/CMSParsingException
      // 11f: dup
      // 120: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 123: athrow
      // 124: astore 2
      // 125: return
      // try (3 -> 130): 131 null
      // try (3 -> 130): 136 null
      // try (3 -> 130): 141 null
   }

   public final String[] getNames() {
      return this._names;
   }

   public final String getDisplayName() {
      return this._displayName;
   }

   public final X509Certificate[] getCertificates() {
      return this._certificates;
   }

   public final ASN1BitSet getFlags() {
      return this._flags;
   }

   public final int getVersion() {
      return this._version;
   }

   public final String getClientID() {
      return this._clientID;
   }

   @Override
   public final boolean isEncrypted() {
      return false;
   }

   @Override
   public final boolean isSigned() {
      return false;
   }

   @Override
   public final int read(byte[] data, int offset, int length) {
      return -1;
   }

   @Override
   public final void setData(InputStream data) {
   }

   @Override
   public final boolean isContentComplete() {
      return true;
   }
}
