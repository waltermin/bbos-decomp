package net.rim.device.api.crypto.pgp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.LineReader;
import net.rim.device.api.io.SharedInputStream;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC24;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.MultiMap;
import net.rim.device.internal.crypto.pgp.PGPPacket;
import net.rim.device.internal.crypto.pgp.PGPPacketParser;
import net.rim.device.internal.crypto.pgp.PGPPrivateKeyPacket;
import net.rim.device.internal.crypto.pgp.PGPUtilities;

final class PGPInternalArmorDecoder extends InputStream {
   private MultiMap _headers;
   private KeyStore _keyStore;
   private boolean _displayUI;
   private SharedInputStream _input;
   private SharedInputStream _plaintextStream;
   private PGPDashEscapedInputStream _clearSignedDataStream;
   private Base64InputStream _base64Stream;
   private Base64InputStream _crcStream;
   private InputStream _innerStream;
   private int _numKeys;
   private PGPCertificate[] _certificates;
   private PGPPrivateKey[] _privateKeys;
   private boolean _isKeyBlock;
   private boolean _isCRCFound;
   private boolean _isCRCChecked;
   private boolean _isCRCValid;
   private int _crc;
   private boolean _wereProcessingErrors;
   private boolean _hasUnsupportedOperation;
   private int _initialStreamPosition;
   private static final byte[] CRLF = new byte[]{13, 10};
   private static final byte[] CRC_MARKER = new byte[]{61};
   private static final int CRC_LENGTH = 4;
   private static final int DECODED_CRC_LENGTH = 3;
   private static final int TEMP_DATA_LENGTH = 128;

   public PGPInternalArmorDecoder(SharedInputStream input) {
      this(input, null, true);
   }

   public PGPInternalArmorDecoder(SharedInputStream param1, KeyStore param2, boolean param3) throws PGPIncompleteKeyException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokespecial java/io/InputStream.<init> ()V
      // 004: aload 0
      // 005: bipush -1
      // 007: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._numKeys I
      // 00a: aload 0
      // 00b: bipush 0
      // 00c: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._hasUnsupportedOperation Z
      // 00f: aload 0
      // 010: new net/rim/device/api/util/MultiMap
      // 013: dup
      // 014: invokespecial net/rim/device/api/util/MultiMap.<init> ()V
      // 017: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._headers Lnet/rim/device/api/util/MultiMap;
      // 01a: aload 0
      // 01b: aload 1
      // 01c: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 01f: aload 0
      // 020: aload 2
      // 021: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 024: aload 0
      // 025: iload 3
      // 026: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._displayUI Z
      // 029: new net/rim/device/api/io/LineReader
      // 02c: dup
      // 02d: aload 0
      // 02e: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 031: invokespecial net/rim/device/api/io/LineReader.<init> (Ljava/io/InputStream;)V
      // 034: astore 5
      // 036: bipush 0
      // 037: istore 6
      // 039: aload 0
      // 03a: aload 5
      // 03c: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.findNonEmptyLine (Lnet/rim/device/api/io/LineReader;)[B
      // 03f: astore 7
      // 041: aload 0
      // 042: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 045: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 048: aload 5
      // 04a: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 04d: isub
      // 04e: aload 7
      // 050: arraylength
      // 051: isub
      // 052: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRLF [B
      // 055: arraylength
      // 056: isub
      // 057: istore 8
      // 059: aload 0
      // 05a: iload 8
      // 05c: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._initialStreamPosition I
      // 05f: aload 7
      // 061: bipush 0
      // 062: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_PGP_DASHES [B
      // 065: bipush 0
      // 066: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_PGP_DASHES [B
      // 069: arraylength
      // 06a: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 06d: ifeq 083
      // 070: aload 0
      // 071: aload 7
      // 073: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_PGP_DASHES [B
      // 076: arraylength
      // 077: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.verifyWhitespaceAfterOffset ([BI)V
      // 07a: aload 0
      // 07b: aload 5
      // 07d: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.readHeaders (Lnet/rim/device/api/io/LineReader;)V
      // 080: goto 22d
      // 083: aload 7
      // 085: bipush 0
      // 086: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_SIGNEDMESSAGE_DASHES [B
      // 089: bipush 0
      // 08a: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_SIGNEDMESSAGE_DASHES [B
      // 08d: arraylength
      // 08e: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 091: ifne 097
      // 094: goto 179
      // 097: aload 0
      // 098: aload 7
      // 09a: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_SIGNEDMESSAGE_DASHES [B
      // 09d: arraylength
      // 09e: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.verifyWhitespaceAfterOffset ([BI)V
      // 0a1: aload 0
      // 0a2: aload 5
      // 0a4: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.readHeaders (Lnet/rim/device/api/io/LineReader;)V
      // 0a7: bipush -1
      // 0a9: istore 9
      // 0ab: bipush -1
      // 0ad: istore 10
      // 0af: aload 0
      // 0b0: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 0b3: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 0b6: aload 5
      // 0b8: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 0bb: isub
      // 0bc: istore 9
      // 0be: aload 0
      // 0bf: aload 5
      // 0c1: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.findBeginMarker (Lnet/rim/device/api/io/LineReader;)[B
      // 0c4: astore 7
      // 0c6: aload 7
      // 0c8: bipush 0
      // 0c9: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_SIGNATURE_DASHES [B
      // 0cc: bipush 0
      // 0cd: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_SIGNATURE_DASHES [B
      // 0d0: arraylength
      // 0d1: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 0d4: ifne 0f1
      // 0d7: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 0da: dup
      // 0db: new java/lang/StringBuffer
      // 0de: dup
      // 0df: ldc_w "AIAH"
      // 0e2: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0e5: aload 7
      // 0e7: invokevirtual java/lang/StringBuffer.append (Ljava/lang/Object;)Ljava/lang/StringBuffer;
      // 0ea: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0ed: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // 0f0: athrow
      // 0f1: aload 0
      // 0f2: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 0f5: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 0f8: aload 5
      // 0fa: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 0fd: isub
      // 0fe: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRLF [B
      // 101: arraylength
      // 102: isub
      // 103: aload 7
      // 105: arraylength
      // 106: isub
      // 107: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRLF [B
      // 10a: arraylength
      // 10b: isub
      // 10c: istore 10
      // 10e: goto 125
      // 111: astore 11
      // 113: bipush 1
      // 114: istore 6
      // 116: aload 0
      // 117: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 11a: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 11d: aload 5
      // 11f: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 122: isub
      // 123: istore 10
      // 125: aload 0
      // 126: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 129: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 12c: istore 11
      // 12e: aload 0
      // 12f: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 132: iload 9
      // 134: invokevirtual net/rim/device/api/io/SharedInputStream.setCurrentPosition (I)V
      // 137: aload 0
      // 138: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 13b: invokevirtual net/rim/device/api/io/SharedInputStream.readInputStream ()Lnet/rim/device/api/io/SharedInputStream;
      // 13e: astore 4
      // 140: aload 4
      // 142: iload 10
      // 144: iload 9
      // 146: isub
      // 147: invokevirtual net/rim/device/api/io/SharedInputStream.setLength (I)V
      // 14a: aload 0
      // 14b: new net/rim/device/api/crypto/pgp/PGPDashEscapedInputStream
      // 14e: dup
      // 14f: aload 4
      // 151: invokespecial net/rim/device/api/crypto/pgp/PGPDashEscapedInputStream.<init> (Ljava/io/InputStream;)V
      // 154: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._clearSignedDataStream Lnet/rim/device/api/crypto/pgp/PGPDashEscapedInputStream;
      // 157: aload 0
      // 158: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 15b: iload 11
      // 15d: invokevirtual net/rim/device/api/io/SharedInputStream.setCurrentPosition (I)V
      // 160: iload 6
      // 162: ifeq 166
      // 165: return
      // 166: aload 0
      // 167: aload 7
      // 169: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_SIGNATURE_DASHES [B
      // 16c: arraylength
      // 16d: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.verifyWhitespaceAfterOffset ([BI)V
      // 170: aload 0
      // 171: aload 5
      // 173: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.readHeaders (Lnet/rim/device/api/io/LineReader;)V
      // 176: goto 22d
      // 179: aload 7
      // 17b: bipush 0
      // 17c: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_PUBLIC_KEY_DASHES [B
      // 17f: bipush 0
      // 180: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_PUBLIC_KEY_DASHES [B
      // 183: arraylength
      // 184: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 187: ifeq 1a2
      // 18a: aload 0
      // 18b: aload 7
      // 18d: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_PUBLIC_KEY_DASHES [B
      // 190: arraylength
      // 191: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.verifyWhitespaceAfterOffset ([BI)V
      // 194: aload 0
      // 195: aload 5
      // 197: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.readHeaders (Lnet/rim/device/api/io/LineReader;)V
      // 19a: aload 0
      // 19b: bipush 1
      // 19c: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._isKeyBlock Z
      // 19f: goto 22d
      // 1a2: aload 7
      // 1a4: bipush 0
      // 1a5: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_PRIVATE_KEY_DASHES [B
      // 1a8: bipush 0
      // 1a9: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_PRIVATE_KEY_DASHES [B
      // 1ac: arraylength
      // 1ad: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 1b0: ifeq 1cb
      // 1b3: aload 0
      // 1b4: aload 7
      // 1b6: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.BEGIN_PRIVATE_KEY_DASHES [B
      // 1b9: arraylength
      // 1ba: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.verifyWhitespaceAfterOffset ([BI)V
      // 1bd: aload 0
      // 1be: aload 5
      // 1c0: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.readHeaders (Lnet/rim/device/api/io/LineReader;)V
      // 1c3: aload 0
      // 1c4: bipush 1
      // 1c5: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._isKeyBlock Z
      // 1c8: goto 22d
      // 1cb: bipush -1
      // 1cd: istore 9
      // 1cf: aload 0
      // 1d0: aload 5
      // 1d2: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.findBeginMarker (Lnet/rim/device/api/io/LineReader;)[B
      // 1d5: astore 7
      // 1d7: aload 0
      // 1d8: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 1db: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 1de: aload 5
      // 1e0: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 1e3: isub
      // 1e4: aload 7
      // 1e6: arraylength
      // 1e7: isub
      // 1e8: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRLF [B
      // 1eb: arraylength
      // 1ec: isub
      // 1ed: istore 9
      // 1ef: goto 203
      // 1f2: astore 10
      // 1f4: aload 0
      // 1f5: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 1f8: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 1fb: aload 5
      // 1fd: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 200: isub
      // 201: istore 9
      // 203: aload 0
      // 204: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 207: iload 8
      // 209: invokevirtual net/rim/device/api/io/SharedInputStream.setCurrentPosition (I)V
      // 20c: aload 0
      // 20d: aload 0
      // 20e: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 211: invokevirtual net/rim/device/api/io/SharedInputStream.readInputStream ()Lnet/rim/device/api/io/SharedInputStream;
      // 214: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._plaintextStream Lnet/rim/device/api/io/SharedInputStream;
      // 217: aload 0
      // 218: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._plaintextStream Lnet/rim/device/api/io/SharedInputStream;
      // 21b: iload 9
      // 21d: iload 8
      // 21f: isub
      // 220: invokevirtual net/rim/device/api/io/SharedInputStream.setLength (I)V
      // 223: aload 0
      // 224: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 227: iload 9
      // 229: invokevirtual net/rim/device/api/io/SharedInputStream.setCurrentPosition (I)V
      // 22c: return
      // 22d: aload 0
      // 22e: aload 5
      // 230: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.findNonEmptyLine (Lnet/rim/device/api/io/LineReader;)[B
      // 233: astore 7
      // 235: aload 0
      // 236: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 239: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 23c: aload 5
      // 23e: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 241: isub
      // 242: aload 7
      // 244: arraylength
      // 245: isub
      // 246: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRLF [B
      // 249: arraylength
      // 24a: isub
      // 24b: istore 9
      // 24d: bipush -1
      // 24f: istore 10
      // 251: aconst_null
      // 252: astore 11
      // 254: aload 7
      // 256: arraylength
      // 257: ifne 265
      // 25a: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 25d: dup
      // 25e: ldc_w "Invalid new line"
      // 261: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // 264: athrow
      // 265: aload 7
      // 267: bipush 0
      // 268: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRC_MARKER [B
      // 26b: bipush 0
      // 26c: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRC_MARKER [B
      // 26f: arraylength
      // 270: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 273: ifeq 2bf
      // 276: aload 0
      // 277: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 27a: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 27d: aload 5
      // 27f: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 282: isub
      // 283: aload 7
      // 285: arraylength
      // 286: isub
      // 287: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRLF [B
      // 28a: arraylength
      // 28b: isub
      // 28c: istore 10
      // 28e: aload 0
      // 28f: aload 7
      // 291: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRC_MARKER [B
      // 294: arraylength
      // 295: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.readCRC ([BI)V
      // 298: aload 0
      // 299: aload 5
      // 29b: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.findNonEmptyLine (Lnet/rim/device/api/io/LineReader;)[B
      // 29e: astore 7
      // 2a0: aload 7
      // 2a2: bipush 0
      // 2a3: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.END [B
      // 2a6: bipush 0
      // 2a7: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.END [B
      // 2aa: arraylength
      // 2ab: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 2ae: ifeq 2b4
      // 2b1: goto 386
      // 2b4: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 2b7: dup
      // 2b8: ldc_w "AInC"
      // 2bb: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // 2be: athrow
      // 2bf: aload 7
      // 2c1: bipush 0
      // 2c2: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.END [B
      // 2c5: bipush 0
      // 2c6: getstatic net/rim/device/api/crypto/pgp/PGPArmorEncoder.END [B
      // 2c9: arraylength
      // 2ca: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 2cd: ifeq 33c
      // 2d0: aload 11
      // 2d2: ifnull 2fd
      // 2d5: aload 11
      // 2d7: arraylength
      // 2d8: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRC_MARKER [B
      // 2db: arraylength
      // 2dc: bipush 4
      // 2de: iadd
      // 2df: if_icmplt 2fd
      // 2e2: aload 11
      // 2e4: aload 11
      // 2e6: arraylength
      // 2e7: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRC_MARKER [B
      // 2ea: arraylength
      // 2eb: bipush 4
      // 2ed: iadd
      // 2ee: isub
      // 2ef: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRC_MARKER [B
      // 2f2: bipush 0
      // 2f3: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRC_MARKER [B
      // 2f6: arraylength
      // 2f7: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 2fa: ifne 308
      // 2fd: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 300: dup
      // 301: ldc_w "AInC"
      // 304: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // 307: athrow
      // 308: aload 0
      // 309: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 30c: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 30f: aload 5
      // 311: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 314: isub
      // 315: aload 7
      // 317: arraylength
      // 318: isub
      // 319: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRLF [B
      // 31c: arraylength
      // 31d: isub
      // 31e: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRC_MARKER [B
      // 321: arraylength
      // 322: isub
      // 323: bipush 4
      // 325: isub
      // 326: getstatic net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.CRLF [B
      // 329: arraylength
      // 32a: isub
      // 32b: istore 10
      // 32d: aload 0
      // 32e: aload 11
      // 330: aload 11
      // 332: arraylength
      // 333: bipush 4
      // 335: isub
      // 336: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.readCRC ([BI)V
      // 339: goto 386
      // 33c: aload 7
      // 33e: astore 11
      // 340: aload 5
      // 342: invokevirtual net/rim/device/api/io/LineReader.readLine ()[B
      // 345: astore 7
      // 347: goto 254
      // 34a: astore 11
      // 34c: aload 0
      // 34d: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._isKeyBlock Z
      // 350: ifeq 35f
      // 353: aload 0
      // 354: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.setProcessingErrorsFound ()V
      // 357: new net/rim/device/api/crypto/pgp/PGPIncompleteKeyException
      // 35a: dup
      // 35b: invokespecial net/rim/device/api/crypto/pgp/PGPIncompleteKeyException.<init> ()V
      // 35e: athrow
      // 35f: aload 0
      // 360: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 363: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 366: aload 5
      // 368: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 36b: isub
      // 36c: istore 10
      // 36e: goto 386
      // 371: astore 11
      // 373: aload 0
      // 374: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._isKeyBlock Z
      // 377: ifeq 386
      // 37a: aload 0
      // 37b: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.setProcessingErrorsFound ()V
      // 37e: new net/rim/device/api/crypto/pgp/PGPIncompleteKeyException
      // 381: dup
      // 382: invokespecial net/rim/device/api/crypto/pgp/PGPIncompleteKeyException.<init> ()V
      // 385: athrow
      // 386: aload 0
      // 387: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 38a: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 38d: aload 5
      // 38f: invokevirtual net/rim/device/api/io/LineReader.lengthUnreadData ()I
      // 392: isub
      // 393: istore 11
      // 395: aload 0
      // 396: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 399: iload 9
      // 39b: invokevirtual net/rim/device/api/io/SharedInputStream.setCurrentPosition (I)V
      // 39e: aload 0
      // 39f: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 3a2: invokevirtual net/rim/device/api/io/SharedInputStream.readInputStream ()Lnet/rim/device/api/io/SharedInputStream;
      // 3a5: astore 4
      // 3a7: aload 4
      // 3a9: iload 10
      // 3ab: iload 9
      // 3ad: isub
      // 3ae: invokevirtual net/rim/device/api/io/SharedInputStream.setLength (I)V
      // 3b1: aload 0
      // 3b2: new net/rim/device/api/io/Base64InputStream
      // 3b5: dup
      // 3b6: aload 4
      // 3b8: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 3bb: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._base64Stream Lnet/rim/device/api/io/Base64InputStream;
      // 3be: aload 0
      // 3bf: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._isCRCFound Z
      // 3c2: ifeq 3e5
      // 3c5: aload 0
      // 3c6: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 3c9: invokevirtual net/rim/device/api/io/SharedInputStream.readInputStream ()Lnet/rim/device/api/io/SharedInputStream;
      // 3cc: astore 4
      // 3ce: aload 4
      // 3d0: iload 10
      // 3d2: iload 9
      // 3d4: isub
      // 3d5: invokevirtual net/rim/device/api/io/SharedInputStream.setLength (I)V
      // 3d8: aload 0
      // 3d9: new net/rim/device/api/io/Base64InputStream
      // 3dc: dup
      // 3dd: aload 4
      // 3df: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 3e2: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._crcStream Lnet/rim/device/api/io/Base64InputStream;
      // 3e5: aload 0
      // 3e6: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._input Lnet/rim/device/api/io/SharedInputStream;
      // 3e9: iload 11
      // 3eb: invokevirtual net/rim/device/api/io/SharedInputStream.setCurrentPosition (I)V
      // 3ee: aload 0
      // 3ef: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._isKeyBlock Z
      // 3f2: ifeq 411
      // 3f5: aload 0
      // 3f6: aload 0
      // 3f7: getfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._base64Stream Lnet/rim/device/api/io/Base64InputStream;
      // 3fa: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.parseKeys (Lnet/rim/device/api/io/Base64InputStream;)V
      // 3fd: return
      // 3fe: astore 4
      // 400: aload 4
      // 402: athrow
      // 403: astore 4
      // 405: aload 0
      // 406: invokespecial net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder.setProcessingErrorsFound ()V
      // 409: return
      // 40a: astore 4
      // 40c: aload 0
      // 40d: bipush 1
      // 40e: putfield net/rim/device/api/crypto/pgp/PGPInternalArmorDecoder._hasUnsupportedOperation Z
      // 411: return
      // try (88 -> 134): 135 null
      // try (226 -> 243): 244 null
      // try (291 -> 416): 416 null
      // try (291 -> 416): 434 null
      // try (8 -> 174): 500 net/rim/device/api/crypto/pgp/PGPIncompleteKeyException
      // try (175 -> 271): 500 net/rim/device/api/crypto/pgp/PGPIncompleteKeyException
      // try (272 -> 499): 500 net/rim/device/api/crypto/pgp/PGPIncompleteKeyException
      // try (8 -> 174): 503 net/rim/device/api/crypto/pgp/PGPEncodingException
      // try (175 -> 271): 503 net/rim/device/api/crypto/pgp/PGPEncodingException
      // try (272 -> 499): 503 net/rim/device/api/crypto/pgp/PGPEncodingException
      // try (8 -> 174): 507 null
      // try (175 -> 271): 507 null
      // try (272 -> 499): 507 null
   }

   private final byte[] findBeginMarker(LineReader scanLine) {
      byte[] line;
      do {
         line = scanLine.readLine();
      } while (!PGPUtilities.startsWith(line, PGPArmorEncoder.BEGIN));

      return line;
   }

   private final void verifyWhitespaceAfterOffset(byte[] data, int offset) throws PGPEncodingException {
      for (int i = offset; i < data.length; i++) {
         if (!CharacterUtilities.isSpaceChar((char)(data[i] & 0xFF))) {
            throw new PGPEncodingException("ANWT");
         }
      }
   }

   private final byte[] findNonEmptyLine(LineReader scanLine) {
      byte[] line;
      do {
         line = scanLine.readLine();
      } while (line.length <= 0);

      return line;
   }

   private final void readCRC(byte[] data, int offset) {
      try {
         Base64InputStream crcStream = new Base64InputStream(new ByteArrayInputStream(data, offset, 4));
         byte[] crcBytes = new byte[3];
         int crcReadLength = crcStream.read(crcBytes);
         if (crcReadLength == 3) {
            for (int i = 2; i >= 0; i--) {
               this._crc <<= 8;
               this._crc = this._crc | crcBytes[i] & 255;
            }

            this._isCRCFound = true;
            return;
         }
      } finally {
         return;
      }
   }

   public final Enumeration getValues(String key) {
      return this._headers.elements(key);
   }

   public final Enumeration getValues() {
      return this._headers.elements();
   }

   public final InputStream getInnerStream() throws CryptoUnsupportedOperationException {
      if (this._hasUnsupportedOperation) {
         throw new CryptoUnsupportedOperationException();
      }

      if (!this._wereProcessingErrors) {
         if (this._innerStream == null) {
            if (this._plaintextStream != null) {
               this._innerStream = this._plaintextStream;
            } else if (this._base64Stream == null && this._clearSignedDataStream != null) {
               this._innerStream = new PGPSignedInputStream(this._clearSignedDataStream, null, null, null, true, true);
            } else if (this._base64Stream != null) {
               this._innerStream = PGPInputStream.getPGPInputStream(this._base64Stream, this._keyStore, this._displayUI);
               if (this._clearSignedDataStream != null && this._innerStream instanceof PGPSignedInputStream) {
                  ((PGPSignedInputStream)this._innerStream).setData(this._clearSignedDataStream);
               }
            }
         }
      } else {
         this._input.setCurrentPosition(this._initialStreamPosition);
         this._innerStream = this._input;
      }

      return this._innerStream;
   }

   public final boolean isCheckSumPresent() {
      return this._isCRCFound;
   }

   public final boolean isCheckSumValid() {
      if (!this.hasBase64Data()) {
         return true;
      }

      if (!this.isCheckSumPresent()) {
         return false;
      }

      if (this._isCRCChecked) {
         return this._isCRCValid;
      }

      this._isCRCChecked = true;
      int computedCRC = 13501623;
      byte[] tempData = new byte[128];

      while (true) {
         int tempReadLength = this._crcStream.read(tempData);
         if (tempReadLength < 0) {
            this._isCRCValid = computedCRC == this._crc;
            return this._isCRCValid;
         }

         computedCRC = CRC24.update(computedCRC, tempData, 0, tempReadLength);
      }
   }

   public final boolean hasBase64Data() {
      return this._base64Stream != null;
   }

   @Override
   public final int read() {
      try {
         InputStream innerStream = this.getInnerStream();
         if (innerStream != null) {
            return innerStream.read();
         }
      } finally {
         return -1;
      }

      return -1;
   }

   @Override
   public final int read(byte[] buffer) {
      return this.read(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      if (buffer != null && buffer.length - length >= offset && offset >= 0 && length >= 0) {
         try {
            InputStream innerStream = this.getInnerStream();
            if (innerStream != null) {
               return innerStream.read(buffer, offset, length);
            }
         } finally {
            return -1;
         }

         return -1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int available() {
      try {
         InputStream innerStream = this.getInnerStream();
         if (innerStream != null) {
            return innerStream.available();
         }
      } finally {
         return 0;
      }

      return 0;
   }

   @Override
   public final long skip(long n) {
      try {
         InputStream innerStream = this.getInnerStream();
         if (innerStream != null) {
            return innerStream.skip(n);
         }
      } finally {
         return 0;
      }

      return 0;
   }

   @Override
   public final void close() {
      label20:
      try {
         InputStream innerStream = this.getInnerStream();
         if (innerStream != null) {
            innerStream.close();
         }
      } finally {
         break label20;
      }

      this._input.close();
   }

   public final int numCertificates() {
      return this._isKeyBlock ? this._numKeys : 0;
   }

   public final PGPCertificate getCertificate(int index) {
      if (this._isKeyBlock) {
         this.validateIndex(this._certificates, index);
         return this._certificates[index];
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final String getCertificateLabel(int index) {
      return this.getCertificate(index).getSubjectFriendlyName();
   }

   public final boolean isPrivateKey(int index) {
      return this._isKeyBlock ? this.getPrivateKey(index) != null : false;
   }

   public final PGPPrivateKey getPrivateKey(int index) {
      if (this._isKeyBlock) {
         this.validateIndex(this._privateKeys, index);
         return this._privateKeys[index];
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void readHeaders(LineReader scanLine) throws PGPEncodingException {
      while (true) {
         byte[] line = scanLine.readLine();
         if (line.length == 0) {
            return;
         }

         int keyEnd = this.indexOf(line, 58, 0);
         if (keyEnd < 0) {
            throw new PGPEncodingException("A single empty line must follow the header definition");
         }

         String key = this.trimmedSubString(line, 0, keyEnd);
         int valueStart = keyEnd + 1;

         while (valueStart < line.length) {
            int valueEnd = this.indexOf(line, 44, valueStart);
            if (valueEnd < 0) {
               valueEnd = line.length;
            }

            String value = this.trimmedSubString(line, valueStart, valueEnd);
            this._headers.add(key, value);
            valueStart = valueEnd;
         }
      }
   }

   private final int indexOf(byte[] line, int element, int startPos) {
      if (startPos >= 0 && startPos < line.length) {
         for (int i = startPos; i < line.length; i++) {
            if (line[i] == element) {
               return i;
            }
         }

         return -1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final String trimmedSubString(byte[] line, int startIndex, int endIndex) {
      if (startIndex <= endIndex && startIndex >= 0 && endIndex <= line.length) {
         while (startIndex < endIndex && CharacterUtilities.isSpaceChar((char)(line[startIndex] & 0xFF))) {
            startIndex++;
         }

         while (endIndex > startIndex && CharacterUtilities.isSpaceChar((char)(line[endIndex - 1] & 0xFF))) {
            endIndex--;
         }

         return new String(line, startIndex, endIndex - startIndex);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void parseKeys(Base64InputStream keyStream) {
      PGPPacketParser parser = new PGPPacketParser(keyStream);
      PGPPacket[][] packets = parser.getPackets();
      byte[][] encodings = parser.getEncodings();
      this._numKeys = packets.length;
      this._certificates = new PGPCertificate[this._numKeys];
      this._privateKeys = new PGPPrivateKey[this._numKeys];

      for (int i = 0; i < this._numKeys; i++) {
         PGPPrivateKeyPacket[] privatePackets = this.extractPrivatePackets(packets[i]);
         this._certificates[i] = new PGPCertificate(encodings[i], packets[i]);
         if (privatePackets.length > 0) {
            this._privateKeys[i] = new PGPPrivateKey(privatePackets, this._certificates[i].getSubjectFriendlyName());
         }
      }
   }

   private final PGPPrivateKeyPacket[] extractPrivatePackets(PGPPacket[] packets) {
      PGPPrivateKeyPacket[] privatePackets = new PGPPrivateKeyPacket[0];
      int numPackets = packets.length;

      for (int i = 0; i < numPackets; i++) {
         PGPPacket var10000 = packets[i];
         if (packets[i] instanceof PGPPrivateKeyPacket) {
            PGPPrivateKeyPacket privateKeyPacket = (PGPPrivateKeyPacket)var10000;
            Arrays.add(privatePackets, privateKeyPacket);
            packets[i] = privateKeyPacket.getPublicKeyPacket();
         }
      }

      return privatePackets;
   }

   private final void validateIndex(Object[] array, int index) {
      if (index < 0 || index >= array.length) {
         throw new IllegalArgumentException();
      }
   }

   private final void setProcessingErrorsFound() {
      this._wereProcessingErrors = true;
      this._isKeyBlock = false;
      this._isCRCFound = false;
      this._isCRCChecked = false;
      this._isCRCValid = false;
      this._base64Stream = null;
      this._input.skip(this._input.available());
   }

   public final boolean wereProcessingErrorsFound() {
      return this._wereProcessingErrors;
   }
}
