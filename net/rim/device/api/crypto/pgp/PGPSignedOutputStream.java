package net.rim.device.api.crypto.pgp;

import java.io.OutputStream;
import java.util.Vector;
import net.rim.device.api.crypto.DSAPrivateKey;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.crypto.SignatureSigner;
import net.rim.device.internal.crypto.pgp.PGPSignatureSubPacket;
import net.rim.device.internal.crypto.pgp.PGPUtilities;

public final class PGPSignedOutputStream extends PGPOutputStream {
   private Vector _signedSubPackets;
   private Vector _unsignedSubPackets;
   private OutputStream _headerStream;
   private OutputStream _internalStream;
   private OutputStream _dataStream;
   private PGPArmorEncoder _clearOut;
   private PrivateKey _privateKey;
   private Digest _digest;
   private byte[] _keyID;
   private byte[] _signedTime;
   private int _signatureType;
   private int _publicKeyAlgorithm;
   private int _hashAlgorithm;
   private boolean _isClearSigned;
   private SignatureSigner _signer;
   private int _version = 3;
   public static final int VERSION_THREE = 3;
   public static final int VERSION_FOUR = 4;
   private static final byte[] CRLF = new byte[]{13, 10};
   private static final int CRLF_LENGTH = CRLF.length;
   private static final int MAX_CLEAR_SIGNED_LINE_LENGTH = 76;

   public PGPSignedOutputStream(OutputStream out, int signatureType, PrivateKey privateKey, byte[] keyID) {
      this(out, signatureType, privateKey, keyID, (Digest)(new Object()), 3, 4);
   }

   public PGPSignedOutputStream(OutputStream out, int signatureType, PrivateKey privateKey, byte[] keyID, Digest digest) {
      this(out, signatureType, privateKey, keyID, digest, 3, 4);
   }

   public PGPSignedOutputStream(OutputStream out, int signatureType, PrivateKey privateKey, byte[] keyID, Digest digest, int tagFormat) {
      this(out, signatureType, privateKey, keyID, digest, 3, tagFormat);
   }

   public PGPSignedOutputStream(OutputStream out, int signatureType, PrivateKey privateKey, byte[] keyID, Digest digest, int signatureVersion, int tagFormat) {
      super(out, tagFormat);
      this._privateKey = privateKey;
      this._signatureType = signatureType;
      if ((this._signatureType == 0 || this._signatureType == 1) && (signatureVersion == 3 || signatureVersion == 4)) {
         this._version = signatureVersion;
         this._keyID = keyID;
         this._digest = digest;
         if (out instanceof PGPArmorEncoder && ((PGPArmorEncoder)out).isClearSigned()) {
            this._isClearSigned = true;
         }

         if (this._isClearSigned) {
            if (!(out instanceof PGPArmorEncoder)) {
               throw new Object();
            }

            this._clearOut = (PGPArmorEncoder)out;
            this._clearOut.writeClearSignedHeader(new Object[]{digest.getAlgorithm()});
         }

         this._signedSubPackets = (Vector)(new Object());
         this._unsignedSubPackets = (Vector)(new Object());
         this._headerStream = super._out.getOutputStream();
         this._internalStream = super._out.getOutputStream();
         if (!this._isClearSigned) {
            this._dataStream = super._out.getOutputStream();
         }

         if (this._privateKey instanceof Object) {
            this._signer = (SignatureSigner)(new Object((DSAPrivateKey)this._privateKey, this._digest));
            this._publicKeyAlgorithm = 17;
         } else {
            if (!(this._privateKey instanceof Object)) {
               throw new Object();
            }

            this._signer = (SignatureSigner)(new Object((RSAPrivateKey)this._privateKey, this._digest, true));
            this._publicKeyAlgorithm = 1;
         }

         this._hashAlgorithm = PGPUtilities.digestStringToConstant(this._digest.getAlgorithm());
         int signedTime = (int)(System.currentTimeMillis() / 1000);
         this._signedTime = new byte[]{(byte)(signedTime >> 24), (byte)(signedTime >> 16), (byte)(signedTime >> 8), (byte)signedTime};
         if (this._version == 4) {
            this._signedSubPackets.addElement(new PGPSignatureSubPacket(16, this._keyID));
            this._signedSubPackets.addElement(new PGPSignatureSubPacket(2, this._signedTime));
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final void write(byte[] param1, int param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnull 014
      // 004: iload 2
      // 005: iflt 014
      // 008: iload 3
      // 009: iflt 014
      // 00c: aload 1
      // 00d: arraylength
      // 00e: iload 3
      // 00f: isub
      // 010: iload 2
      // 011: if_icmpge 01c
      // 014: new java/lang/Object
      // 017: dup
      // 018: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 01b: athrow
      // 01c: aload 0
      // 01d: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._isClearSigned Z
      // 020: ifne 026
      // 023: goto 163
      // 026: aload 0
      // 027: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signatureType I
      // 02a: bipush 1
      // 02b: if_icmpeq 031
      // 02e: goto 151
      // 031: new java/lang/Object
      // 034: dup
      // 035: aload 1
      // 036: iload 2
      // 037: iload 3
      // 038: invokespecial java/io/ByteArrayInputStream.<init> ([BII)V
      // 03b: astore 4
      // 03d: new java/lang/Object
      // 040: dup
      // 041: aload 4
      // 043: invokespecial net/rim/device/api/io/LineReader.<init> (Ljava/io/InputStream;)V
      // 046: astore 5
      // 048: aload 5
      // 04a: invokevirtual net/rim/device/api/io/LineReader.readLine ()[B
      // 04d: astore 6
      // 04f: bipush 0
      // 050: istore 7
      // 052: aload 6
      // 054: arraylength
      // 055: istore 8
      // 057: iload 8
      // 059: ifne 079
      // 05c: aload 0
      // 05d: getstatic net/rim/device/api/crypto/pgp/PGPSignedOutputStream.CRLF [B
      // 060: bipush 0
      // 061: getstatic net/rim/device/api/crypto/pgp/PGPSignedOutputStream.CRLF [B
      // 064: arraylength
      // 065: invokevirtual net/rim/device/api/crypto/pgp/PGPSignedOutputStream.update ([BII)V
      // 068: aload 0
      // 069: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._clearOut Lnet/rim/device/api/crypto/pgp/PGPArmorEncoder;
      // 06c: getstatic net/rim/device/api/crypto/pgp/PGPSignedOutputStream.CRLF [B
      // 06f: bipush 0
      // 070: getstatic net/rim/device/api/crypto/pgp/PGPSignedOutputStream.CRLF_LENGTH I
      // 073: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorEncoder.writeClearSignedData ([BII)V
      // 076: goto 048
      // 079: iload 7
      // 07b: iload 8
      // 07d: if_icmpge 048
      // 080: iload 8
      // 082: iload 7
      // 084: bipush 76
      // 086: iadd
      // 087: invokestatic java/lang/Math.min (II)I
      // 08a: istore 9
      // 08c: iload 9
      // 08e: istore 11
      // 090: iload 11
      // 092: iload 7
      // 094: if_icmplt 0b0
      // 097: iload 11
      // 099: iload 8
      // 09b: if_icmpge 0b0
      // 09e: aload 0
      // 09f: aload 6
      // 0a1: iload 11
      // 0a3: baload
      // 0a4: invokespecial net/rim/device/api/crypto/pgp/PGPSignedOutputStream.isWhitespace (B)Z
      // 0a7: ifne 0b0
      // 0aa: iinc 11 -1
      // 0ad: goto 090
      // 0b0: iload 11
      // 0b2: iload 7
      // 0b4: if_icmpge 0be
      // 0b7: iload 9
      // 0b9: istore 10
      // 0bb: goto 0f1
      // 0be: iload 11
      // 0c0: bipush 1
      // 0c1: isub
      // 0c2: istore 12
      // 0c4: iload 12
      // 0c6: iload 7
      // 0c8: if_icmplt 0dd
      // 0cb: aload 0
      // 0cc: aload 6
      // 0ce: iload 12
      // 0d0: baload
      // 0d1: invokespecial net/rim/device/api/crypto/pgp/PGPSignedOutputStream.isWhitespace (B)Z
      // 0d4: ifeq 0dd
      // 0d7: iinc 12 -1
      // 0da: goto 0c4
      // 0dd: iload 12
      // 0df: iload 7
      // 0e1: if_icmpge 0eb
      // 0e4: iload 7
      // 0e6: istore 10
      // 0e8: goto 0f1
      // 0eb: iload 12
      // 0ed: bipush 1
      // 0ee: iadd
      // 0ef: istore 10
      // 0f1: aload 0
      // 0f2: aload 6
      // 0f4: iload 7
      // 0f6: iload 10
      // 0f8: iload 7
      // 0fa: isub
      // 0fb: invokevirtual net/rim/device/api/crypto/pgp/PGPSignedOutputStream.update ([BII)V
      // 0fe: aload 0
      // 0ff: getstatic net/rim/device/api/crypto/pgp/PGPSignedOutputStream.CRLF [B
      // 102: bipush 0
      // 103: getstatic net/rim/device/api/crypto/pgp/PGPSignedOutputStream.CRLF [B
      // 106: arraylength
      // 107: invokevirtual net/rim/device/api/crypto/pgp/PGPSignedOutputStream.update ([BII)V
      // 10a: aload 0
      // 10b: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._clearOut Lnet/rim/device/api/crypto/pgp/PGPArmorEncoder;
      // 10e: aload 6
      // 110: iload 7
      // 112: iload 10
      // 114: iload 7
      // 116: isub
      // 117: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorEncoder.writeClearSignedData ([BII)V
      // 11a: aload 0
      // 11b: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._clearOut Lnet/rim/device/api/crypto/pgp/PGPArmorEncoder;
      // 11e: getstatic net/rim/device/api/crypto/pgp/PGPSignedOutputStream.CRLF [B
      // 121: bipush 0
      // 122: getstatic net/rim/device/api/crypto/pgp/PGPSignedOutputStream.CRLF_LENGTH I
      // 125: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorEncoder.writeClearSignedData ([BII)V
      // 128: iload 10
      // 12a: istore 7
      // 12c: iload 7
      // 12e: iload 8
      // 130: if_icmplt 136
      // 133: goto 079
      // 136: aload 0
      // 137: aload 6
      // 139: iload 7
      // 13b: baload
      // 13c: invokespecial net/rim/device/api/crypto/pgp/PGPSignedOutputStream.isWhitespace (B)Z
      // 13f: ifne 145
      // 142: goto 079
      // 145: iinc 7 1
      // 148: goto 12c
      // 14b: astore 6
      // 14d: return
      // 14e: astore 6
      // 150: return
      // 151: aload 0
      // 152: aload 1
      // 153: iload 2
      // 154: iload 3
      // 155: invokevirtual net/rim/device/api/crypto/pgp/PGPSignedOutputStream.update ([BII)V
      // 158: aload 0
      // 159: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._clearOut Lnet/rim/device/api/crypto/pgp/PGPArmorEncoder;
      // 15c: aload 1
      // 15d: iload 2
      // 15e: iload 3
      // 15f: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorEncoder.writeClearSignedData ([BII)V
      // 162: return
      // 163: aload 0
      // 164: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._dataStream Ljava/io/OutputStream;
      // 167: aload 1
      // 168: iload 2
      // 169: iload 3
      // 16a: invokevirtual java/io/OutputStream.write ([BII)V
      // 16d: return
      // try (37 -> 158): 158 null
      // try (37 -> 158): 160 null
   }

   private final boolean isWhitespace(byte b) {
      return b == 32 || b == 9;
   }

   @Override
   final void update(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         this._signer.update(data, offset, length);
         if (super._pgpOut != null) {
            super._pgpOut.update(data, offset, length);
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final void close() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 1
      // 002: aload 0
      // 003: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._dataStream Ljava/io/OutputStream;
      // 006: ifnull 010
      // 009: aload 0
      // 00a: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._dataStream Ljava/io/OutputStream;
      // 00d: invokevirtual java/io/OutputStream.close ()V
      // 010: aload 0
      // 011: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._version I
      // 014: bipush 3
      // 016: if_icmpne 09e
      // 019: bipush 3
      // 01b: newarray 8
      // 01d: dup
      // 01e: bipush 0
      // 01f: aload 0
      // 020: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._version I
      // 023: i2b
      // 024: bastore
      // 025: dup
      // 026: bipush 1
      // 027: bipush 5
      // 029: bastore
      // 02a: dup
      // 02b: bipush 2
      // 02d: aload 0
      // 02e: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signatureType I
      // 031: i2b
      // 032: bastore
      // 033: astore 2
      // 034: aload 0
      // 035: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 038: aload 0
      // 039: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signatureType I
      // 03c: i2b
      // 03d: invokeinterface net/rim/device/api/crypto/SignatureSigner.update (I)V 2
      // 042: aload 0
      // 043: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 046: aload 2
      // 047: invokevirtual java/io/OutputStream.write ([B)V
      // 04a: aload 0
      // 04b: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 04e: aload 0
      // 04f: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signedTime [B
      // 052: invokeinterface net/rim/device/api/crypto/SignatureSigner.update ([B)V 2
      // 057: aload 0
      // 058: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 05b: aload 0
      // 05c: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signedTime [B
      // 05f: invokevirtual java/io/OutputStream.write ([B)V
      // 062: iload 1
      // 063: aload 2
      // 064: arraylength
      // 065: aload 0
      // 066: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signedTime [B
      // 069: arraylength
      // 06a: iadd
      // 06b: iadd
      // 06c: istore 1
      // 06d: aload 0
      // 06e: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 071: aload 0
      // 072: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._keyID [B
      // 075: invokevirtual java/io/OutputStream.write ([B)V
      // 078: aload 0
      // 079: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 07c: aload 0
      // 07d: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._publicKeyAlgorithm I
      // 080: i2b
      // 081: invokevirtual java/io/OutputStream.write (I)V
      // 084: aload 0
      // 085: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 088: aload 0
      // 089: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._hashAlgorithm I
      // 08c: i2b
      // 08d: invokevirtual java/io/OutputStream.write (I)V
      // 090: iload 1
      // 091: aload 0
      // 092: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._keyID [B
      // 095: arraylength
      // 096: bipush 2
      // 098: iadd
      // 099: iadd
      // 09a: istore 1
      // 09b: goto 252
      // 09e: aload 0
      // 09f: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._version I
      // 0a2: bipush 4
      // 0a4: if_icmpeq 0aa
      // 0a7: goto 243
      // 0aa: bipush 4
      // 0ac: newarray 8
      // 0ae: dup
      // 0af: bipush 0
      // 0b0: aload 0
      // 0b1: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._version I
      // 0b4: i2b
      // 0b5: bastore
      // 0b6: dup
      // 0b7: bipush 1
      // 0b8: aload 0
      // 0b9: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signatureType I
      // 0bc: i2b
      // 0bd: bastore
      // 0be: dup
      // 0bf: bipush 2
      // 0c1: aload 0
      // 0c2: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._publicKeyAlgorithm I
      // 0c5: i2b
      // 0c6: bastore
      // 0c7: dup
      // 0c8: bipush 3
      // 0ca: aload 0
      // 0cb: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._hashAlgorithm I
      // 0ce: i2b
      // 0cf: bastore
      // 0d0: astore 2
      // 0d1: aload 0
      // 0d2: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 0d5: aload 2
      // 0d6: invokeinterface net/rim/device/api/crypto/SignatureSigner.update ([B)V 2
      // 0db: aload 0
      // 0dc: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 0df: aload 2
      // 0e0: invokevirtual java/io/OutputStream.write ([B)V
      // 0e3: iload 1
      // 0e4: aload 2
      // 0e5: arraylength
      // 0e6: iadd
      // 0e7: istore 1
      // 0e8: new java/lang/Object
      // 0eb: dup
      // 0ec: invokespecial net/rim/device/api/io/NoCopyByteArrayOutputStream.<init> ()V
      // 0ef: astore 3
      // 0f0: bipush 0
      // 0f1: istore 4
      // 0f3: aload 0
      // 0f4: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signedSubPackets Ljava/util/Vector;
      // 0f7: invokevirtual java/util/Vector.size ()I
      // 0fa: istore 5
      // 0fc: bipush 0
      // 0fd: istore 6
      // 0ff: iload 6
      // 101: iload 5
      // 103: if_icmpge 122
      // 106: iload 4
      // 108: aload 0
      // 109: aload 3
      // 10a: aload 0
      // 10b: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signedSubPackets Ljava/util/Vector;
      // 10e: iload 6
      // 110: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 113: checkcast net/rim/device/internal/crypto/pgp/PGPSignatureSubPacket
      // 116: invokespecial net/rim/device/api/crypto/pgp/PGPSignedOutputStream.writeSignatureSubPacket (Ljava/io/OutputStream;Lnet/rim/device/internal/crypto/pgp/PGPSignatureSubPacket;)I
      // 119: iadd
      // 11a: istore 4
      // 11c: iinc 6 1
      // 11f: goto 0ff
      // 122: aload 3
      // 123: invokevirtual java/io/ByteArrayOutputStream.close ()V
      // 126: aload 0
      // 127: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 12a: iload 4
      // 12c: bipush 8
      // 12e: ishr
      // 12f: i2b
      // 130: invokeinterface net/rim/device/api/crypto/SignatureSigner.update (I)V 2
      // 135: aload 0
      // 136: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 139: iload 4
      // 13b: i2b
      // 13c: invokeinterface net/rim/device/api/crypto/SignatureSigner.update (I)V 2
      // 141: aload 0
      // 142: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 145: aload 3
      // 146: invokevirtual net/rim/device/api/io/NoCopyByteArrayOutputStream.getByteArray ()[B
      // 149: bipush 0
      // 14a: aload 3
      // 14b: invokevirtual java/io/ByteArrayOutputStream.size ()I
      // 14e: invokeinterface net/rim/device/api/crypto/SignatureSigner.update ([BII)V 4
      // 153: iload 1
      // 154: bipush 2
      // 156: iload 4
      // 158: iadd
      // 159: iadd
      // 15a: istore 1
      // 15b: aload 0
      // 15c: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 15f: iload 4
      // 161: bipush 8
      // 163: ishr
      // 164: i2b
      // 165: invokevirtual java/io/OutputStream.write (I)V
      // 168: aload 0
      // 169: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 16c: iload 4
      // 16e: i2b
      // 16f: invokevirtual java/io/OutputStream.write (I)V
      // 172: aload 0
      // 173: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 176: aload 3
      // 177: invokevirtual net/rim/device/api/io/NoCopyByteArrayOutputStream.getByteArray ()[B
      // 17a: bipush 0
      // 17b: aload 3
      // 17c: invokevirtual java/io/ByteArrayOutputStream.size ()I
      // 17f: invokevirtual java/io/OutputStream.write ([BII)V
      // 182: iload 4
      // 184: bipush 2
      // 186: iadd
      // 187: bipush 4
      // 189: iadd
      // 18a: istore 6
      // 18c: bipush 6
      // 18e: newarray 8
      // 190: dup
      // 191: bipush 0
      // 192: aload 0
      // 193: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._version I
      // 196: i2b
      // 197: bastore
      // 198: dup
      // 199: bipush 1
      // 19a: bipush -1
      // 19c: bastore
      // 19d: dup
      // 19e: bipush 2
      // 1a0: iload 6
      // 1a2: bipush 24
      // 1a4: ishr
      // 1a5: i2b
      // 1a6: bastore
      // 1a7: dup
      // 1a8: bipush 3
      // 1aa: iload 6
      // 1ac: bipush 16
      // 1ae: ishr
      // 1af: i2b
      // 1b0: bastore
      // 1b1: dup
      // 1b2: bipush 4
      // 1b4: iload 6
      // 1b6: bipush 8
      // 1b8: ishr
      // 1b9: i2b
      // 1ba: bastore
      // 1bb: dup
      // 1bc: bipush 5
      // 1be: iload 6
      // 1c0: i2b
      // 1c1: bastore
      // 1c2: astore 7
      // 1c4: aload 0
      // 1c5: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 1c8: aload 7
      // 1ca: bipush 0
      // 1cb: aload 7
      // 1cd: arraylength
      // 1ce: invokeinterface net/rim/device/api/crypto/SignatureSigner.update ([BII)V 4
      // 1d3: new java/lang/Object
      // 1d6: dup
      // 1d7: invokespecial net/rim/device/api/io/NoCopyByteArrayOutputStream.<init> ()V
      // 1da: astore 3
      // 1db: bipush 0
      // 1dc: istore 4
      // 1de: aload 0
      // 1df: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._unsignedSubPackets Ljava/util/Vector;
      // 1e2: invokevirtual java/util/Vector.size ()I
      // 1e5: istore 5
      // 1e7: bipush 0
      // 1e8: istore 8
      // 1ea: iload 8
      // 1ec: iload 5
      // 1ee: if_icmpge 20d
      // 1f1: iload 4
      // 1f3: aload 0
      // 1f4: aload 3
      // 1f5: aload 0
      // 1f6: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._unsignedSubPackets Ljava/util/Vector;
      // 1f9: iload 8
      // 1fb: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 1fe: checkcast net/rim/device/internal/crypto/pgp/PGPSignatureSubPacket
      // 201: invokespecial net/rim/device/api/crypto/pgp/PGPSignedOutputStream.writeSignatureSubPacket (Ljava/io/OutputStream;Lnet/rim/device/internal/crypto/pgp/PGPSignatureSubPacket;)I
      // 204: iadd
      // 205: istore 4
      // 207: iinc 8 1
      // 20a: goto 1ea
      // 20d: aload 3
      // 20e: invokevirtual java/io/ByteArrayOutputStream.close ()V
      // 211: iload 1
      // 212: bipush 2
      // 214: iload 4
      // 216: iadd
      // 217: iadd
      // 218: istore 1
      // 219: aload 0
      // 21a: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 21d: iload 4
      // 21f: bipush 8
      // 221: ishr
      // 222: i2b
      // 223: invokevirtual java/io/OutputStream.write (I)V
      // 226: aload 0
      // 227: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 22a: iload 4
      // 22c: i2b
      // 22d: invokevirtual java/io/OutputStream.write (I)V
      // 230: aload 0
      // 231: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 234: aload 3
      // 235: invokevirtual net/rim/device/api/io/NoCopyByteArrayOutputStream.getByteArray ()[B
      // 238: bipush 0
      // 239: aload 3
      // 23a: invokevirtual java/io/ByteArrayOutputStream.size ()I
      // 23d: invokevirtual java/io/OutputStream.write ([BII)V
      // 240: goto 252
      // 243: new java/lang/Object
      // 246: dup
      // 247: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 24a: dup
      // 24b: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> ()V
      // 24e: invokespecial net/rim/device/api/crypto/CryptoIOException.<init> (Lnet/rim/device/api/crypto/CryptoException;)V
      // 251: athrow
      // 252: aload 0
      // 253: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 256: dup
      // 257: instanceof java/lang/Object
      // 25a: ifne 261
      // 25d: pop
      // 25e: goto 2ba
      // 261: checkcast java/lang/Object
      // 264: astore 2
      // 265: aload 2
      // 266: invokevirtual net/rim/device/api/crypto/DSASignatureSigner.getRLength ()I
      // 269: newarray 8
      // 26b: astore 3
      // 26c: aload 2
      // 26d: invokevirtual net/rim/device/api/crypto/DSASignatureSigner.getSLength ()I
      // 270: newarray 8
      // 272: astore 4
      // 274: aload 2
      // 275: aload 3
      // 276: bipush 0
      // 277: aload 4
      // 279: bipush 0
      // 27a: invokevirtual net/rim/device/api/crypto/DSASignatureSigner.sign ([BI[BI)V
      // 27d: aload 0
      // 27e: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._digest Lnet/rim/device/api/crypto/Digest;
      // 281: bipush 0
      // 282: invokeinterface net/rim/device/api/crypto/Digest.getDigest (Z)[B 2
      // 287: astore 5
      // 289: aload 0
      // 28a: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 28d: aload 5
      // 28f: bipush 0
      // 290: bipush 2
      // 292: invokevirtual java/io/OutputStream.write ([BII)V
      // 295: iinc 1 2
      // 298: aload 0
      // 299: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 29c: aload 3
      // 29d: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.writeMPI (Ljava/io/OutputStream;[B)I
      // 2a0: istore 6
      // 2a2: iload 1
      // 2a3: iload 6
      // 2a5: iadd
      // 2a6: istore 1
      // 2a7: aload 0
      // 2a8: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 2ab: aload 4
      // 2ad: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.writeMPI (Ljava/io/OutputStream;[B)I
      // 2b0: istore 7
      // 2b2: iload 1
      // 2b3: iload 7
      // 2b5: iadd
      // 2b6: istore 1
      // 2b7: goto 30f
      // 2ba: aload 0
      // 2bb: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 2be: dup
      // 2bf: instanceof java/lang/Object
      // 2c2: ifne 2c9
      // 2c5: pop
      // 2c6: goto 307
      // 2c9: checkcast java/lang/Object
      // 2cc: astore 2
      // 2cd: aload 2
      // 2ce: invokevirtual net/rim/device/api/crypto/PKCS1SignatureSigner.getLength ()I
      // 2d1: newarray 8
      // 2d3: astore 3
      // 2d4: aload 2
      // 2d5: aload 3
      // 2d6: bipush 0
      // 2d7: invokevirtual net/rim/device/api/crypto/PKCS1SignatureSigner.sign ([BI)V
      // 2da: aload 0
      // 2db: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._digest Lnet/rim/device/api/crypto/Digest;
      // 2de: bipush 0
      // 2df: invokeinterface net/rim/device/api/crypto/Digest.getDigest (Z)[B 2
      // 2e4: astore 4
      // 2e6: aload 0
      // 2e7: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 2ea: aload 4
      // 2ec: bipush 0
      // 2ed: bipush 2
      // 2ef: invokevirtual java/io/OutputStream.write ([BII)V
      // 2f2: iinc 1 2
      // 2f5: aload 0
      // 2f6: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 2f9: aload 3
      // 2fa: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.writeMPI (Ljava/io/OutputStream;[B)I
      // 2fd: istore 5
      // 2ff: iload 1
      // 300: iload 5
      // 302: iadd
      // 303: istore 1
      // 304: goto 30f
      // 307: new java/lang/Object
      // 30a: dup
      // 30b: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> ()V
      // 30e: athrow
      // 30f: aload 0
      // 310: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._internalStream Ljava/io/OutputStream;
      // 313: invokevirtual java/io/OutputStream.close ()V
      // 316: aload 0
      // 317: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._headerStream Ljava/io/OutputStream;
      // 31a: bipush 2
      // 31c: iload 1
      // 31d: aload 0
      // 31e: getfield net/rim/device/api/crypto/pgp/PGPOutputStream._tagFormat I
      // 321: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.writeTagAndLength (Ljava/io/OutputStream;III)V
      // 324: aload 0
      // 325: getfield net/rim/device/api/crypto/pgp/PGPSignedOutputStream._headerStream Ljava/io/OutputStream;
      // 328: invokevirtual java/io/OutputStream.close ()V
      // 32b: aload 0
      // 32c: getfield net/rim/device/api/crypto/pgp/PGPOutputStream._out Lnet/rim/device/api/io/SharedOutputStream;
      // 32f: invokevirtual net/rim/device/api/io/SharedOutputStream.close ()V
      // 332: return
      // 333: astore 1
      // 334: new java/lang/Object
      // 337: dup
      // 338: aload 1
      // 339: invokespecial net/rim/device/api/crypto/CryptoIOException.<init> (Lnet/rim/device/api/crypto/CryptoException;)V
      // 33c: athrow
      // 33d: astore 1
      // 33e: new java/lang/Object
      // 341: dup
      // 342: aload 1
      // 343: invokespecial net/rim/device/api/crypto/CryptoIOException.<init> (Lnet/rim/device/api/crypto/CryptoException;)V
      // 346: athrow
      // try (0 -> 436): 437 null
      // try (0 -> 436): 443 null
   }

   private final int writeSignatureSubPacket(OutputStream out, PGPSignatureSubPacket subPacket) {
      int length = 1 + subPacket.getEncoding().length;
      int totalLength = length;
      if (length >> 16 != 0 || length - 8383 > 0) {
         out.write(255);
         out.write(length >> 24);
         out.write(length >> 16);
         out.write(length >> 8);
         out.write(length);
         totalLength += 5;
      } else if (length - 192 > 0) {
         out.write((length - 192 >> 8) + 192);
         out.write(length - 192);
         totalLength += 2;
      } else {
         out.write(length);
         totalLength++;
      }

      out.write(subPacket.getTag());
      out.write(subPacket.getEncoding());
      return totalLength;
   }
}
