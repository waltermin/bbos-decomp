package net.rim.device.internal.media.metadata;

import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.media.MetaDataObject;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public final class ID3v2Reader {
   public static final int ID3V2_HEADER_SIZE;
   private static final int ID3V2_SIZE_OFFSET;
   private static final int ID3V2_MAJOR_VERSION_OFFSET;
   private static final int ID3V2_MAX_SUPPORTED_VERSION;
   private static final int ID3V2_MIN_SUPPORTED_VERSION;
   private static final int ID3V2_UNSYNCHRONIZATION_FLAG_MASK;
   private static final int ID3V2_EXTENDED_HEADER_FLAG_MASK;
   private static final int ID3V2_2_COMPRESSION_FLAG_MASK;
   private static final int ID3V2_3_COMPRESSION_FLAG_MASK;
   private static final int ID3V2_4_COMPRESSION_FLAG_MASK;
   private static final int ID3V2_3_ENCRYPTION_FLAG_MASK;
   private static final int ID3V2_4_ENCRYPTION_FLAG_MASK;
   private static final int ID3V2_3_GROUPING_IDENTITY_FLAG_MASK;
   private static final int ID3V2_4_GROUPING_IDENTITY_FLAG_MASK;
   private static final int ID3V2_4_UNSYNCHRONIZATION_FLAG_MASK;
   private static final int ID3V2_4_DATA_LENGTH_INDICATOR_FLAG_MASK;
   private static final int ID3V2_ENCODING_ISO_8859_1;
   private static final int ID3V2_ENCODING_UTF16;
   private static final int ID3V2_ENCODING_UTF16BE;
   private static final int ID3V2_ENCODING_UTF8;
   private static final int FRAME_ID_BUF;
   private static final int FRAME_ID_CNT;
   private static final int FRAME_ID_COM;
   private static final int FRAME_ID_CRA;
   private static final int FRAME_ID_CRM;
   private static final int FRAME_ID_ETC;
   private static final int FRAME_ID_EQU;
   private static final int FRAME_ID_GEO;
   private static final int FRAME_ID_IPL;
   private static final int FRAME_ID_LNK;
   private static final int FRAME_ID_MCI;
   private static final int FRAME_ID_MLL;
   private static final int FRAME_ID_PIC;
   private static final int FRAME_ID_POP;
   private static final int FRAME_ID_REV;
   private static final int FRAME_ID_RVA;
   private static final int FRAME_ID_SLT;
   private static final int FRAME_ID_STC;
   private static final int FRAME_ID_TAL;
   private static final int FRAME_ID_TBP;
   private static final int FRAME_ID_TCM;
   private static final int FRAME_ID_TCO;
   private static final int FRAME_ID_TCR;
   private static final int FRAME_ID_TDA;
   private static final int FRAME_ID_TDY;
   private static final int FRAME_ID_TEN;
   private static final int FRAME_ID_TFT;
   private static final int FRAME_ID_TIM;
   private static final int FRAME_ID_TKE;
   private static final int FRAME_ID_TLA;
   private static final int FRAME_ID_TLE;
   private static final int FRAME_ID_TMT;
   private static final int FRAME_ID_TOA;
   private static final int FRAME_ID_TOF;
   private static final int FRAME_ID_TOL;
   private static final int FRAME_ID_TOR;
   private static final int FRAME_ID_TOT;
   private static final int FRAME_ID_TP1;
   private static final int FRAME_ID_TP2;
   private static final int FRAME_ID_TP3;
   private static final int FRAME_ID_TP4;
   private static final int FRAME_ID_TPA;
   private static final int FRAME_ID_TPB;
   private static final int FRAME_ID_TRC;
   private static final int FRAME_ID_TRD;
   private static final int FRAME_ID_TRK;
   private static final int FRAME_ID_TSI;
   private static final int FRAME_ID_TSS;
   private static final int FRAME_ID_TT1;
   private static final int FRAME_ID_TT2;
   private static final int FRAME_ID_TT3;
   private static final int FRAME_ID_TXT;
   private static final int FRAME_ID_TXX;
   private static final int FRAME_ID_TYE;
   private static final int FRAME_ID_UFI;
   private static final int FRAME_ID_ULT;
   private static final int FRAME_ID_WAF;
   private static final int FRAME_ID_WAR;
   private static final int FRAME_ID_WAS;
   private static final int FRAME_ID_WCM;
   private static final int FRAME_ID_WCP;
   private static final int FRAME_ID_WPB;
   private static final int FRAME_ID_WXX;
   private static final int FRAME_ID_AENC;
   private static final int FRAME_ID_APIC;
   private static final int FRAME_ID_COMM;
   private static final int FRAME_ID_COMR;
   private static final int FRAME_ID_ENCR;
   private static final int FRAME_ID_EQUA;
   private static final int FRAME_ID_ETCO;
   private static final int FRAME_ID_GEOB;
   private static final int FRAME_ID_GRID;
   private static final int FRAME_ID_IPLS;
   private static final int FRAME_ID_LINK;
   private static final int FRAME_ID_MCDI;
   private static final int FRAME_ID_MLLT;
   private static final int FRAME_ID_OWNE;
   private static final int FRAME_ID_PRIV;
   private static final int FRAME_ID_PCNT;
   private static final int FRAME_ID_POPM;
   private static final int FRAME_ID_POSS;
   private static final int FRAME_ID_RBUF;
   private static final int FRAME_ID_RVAD;
   private static final int FRAME_ID_RVRB;
   private static final int FRAME_ID_SYLT;
   private static final int FRAME_ID_SYTC;
   private static final int FRAME_ID_TALB;
   private static final int FRAME_ID_TBPM;
   private static final int FRAME_ID_TCOM;
   private static final int FRAME_ID_TCON;
   private static final int FRAME_ID_TCOP;
   private static final int FRAME_ID_TDAT;
   private static final int FRAME_ID_TDLY;
   private static final int FRAME_ID_TENC;
   private static final int FRAME_ID_TEXT;
   private static final int FRAME_ID_TFLT;
   private static final int FRAME_ID_TIME;
   private static final int FRAME_ID_TIT1;
   private static final int FRAME_ID_TIT2;
   private static final int FRAME_ID_TIT3;
   private static final int FRAME_ID_TKEY;
   private static final int FRAME_ID_TLAN;
   private static final int FRAME_ID_TLEN;
   private static final int FRAME_ID_TMED;
   private static final int FRAME_ID_TOAL;
   private static final int FRAME_ID_TOFN;
   private static final int FRAME_ID_TOLY;
   private static final int FRAME_ID_TOPE;
   private static final int FRAME_ID_TORY;
   private static final int FRAME_ID_TOWN;
   private static final int FRAME_ID_TPE1;
   private static final int FRAME_ID_TPE2;
   private static final int FRAME_ID_TPE3;
   private static final int FRAME_ID_TPE4;
   private static final int FRAME_ID_TPOS;
   private static final int FRAME_ID_TPUB;
   private static final int FRAME_ID_TRCK;
   private static final int FRAME_ID_TRDA;
   private static final int FRAME_ID_TRSN;
   private static final int FRAME_ID_TRSO;
   private static final int FRAME_ID_TSIZ;
   private static final int FRAME_ID_TSRC;
   private static final int FRAME_ID_TSSE;
   private static final int FRAME_ID_TYER;
   private static final int FRAME_ID_TXXX;
   private static final int FRAME_ID_UFID;
   private static final int FRAME_ID_USER;
   private static final int FRAME_ID_USLT;
   private static final int FRAME_ID_WCOM;
   private static final int FRAME_ID_WCOP;
   private static final int FRAME_ID_WOAF;
   private static final int FRAME_ID_WOAR;
   private static final int FRAME_ID_WOAS;
   private static final int FRAME_ID_WORS;
   private static final int FRAME_ID_WPAY;
   private static final int FRAME_ID_WPUB;
   private static final int FRAME_ID_WXXX;
   private static final int FRAME_ID_ASPI;
   private static final int FRAME_ID_EQU2;
   private static final int FRAME_ID_RVA2;
   private static final int FRAME_ID_SEEK;
   private static final int FRAME_ID_SIGN;
   private static final int FRAME_ID_TDEN;
   private static final int FRAME_ID_TDOR;
   private static final int FRAME_ID_TDRC;
   private static final int FRAME_ID_TDRL;
   private static final int FRAME_ID_TDTG;
   private static final int FRAME_ID_TIPL;
   private static final int FRAME_ID_TMCL;
   private static final int FRAME_ID_TMOO;
   private static final int FRAME_ID_TPRO;
   private static final int FRAME_ID_TSOA;
   private static final int FRAME_ID_TSOP;
   private static final int FRAME_ID_TSOT;
   private static final int FRAME_ID_TSST;

   private ID3v2Reader() {
   }

   public static final int getTagSize(byte[] tag) {
      if (tag.length >= 10 && tag[0] == 73 && tag[1] == 68 && tag[2] == 51) {
         return readSynchsafeInteger(tag, 6) + 10;
      } else {
         throw new Object();
      }
   }

   public static final MetaDataControl readTag(byte[] param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: arraylength
      // 002: bipush 10
      // 004: if_icmplt 020
      // 007: aload 0
      // 008: bipush 0
      // 009: baload
      // 00a: bipush 73
      // 00c: if_icmpne 020
      // 00f: aload 0
      // 010: bipush 1
      // 011: baload
      // 012: bipush 68
      // 014: if_icmpne 020
      // 017: aload 0
      // 018: bipush 2
      // 01a: baload
      // 01b: bipush 51
      // 01d: if_icmpeq 028
      // 020: new java/lang/Object
      // 023: dup
      // 024: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 027: athrow
      // 028: bipush 3
      // 02a: istore 1
      // 02b: aload 0
      // 02c: iload 1
      // 02d: iinc 1 1
      // 030: baload
      // 031: istore 2
      // 032: iload 2
      // 033: bipush -1
      // 035: if_icmpeq 043
      // 038: aload 0
      // 039: iload 1
      // 03a: iinc 1 1
      // 03d: baload
      // 03e: bipush -1
      // 040: if_icmpne 04b
      // 043: new java/lang/Object
      // 046: dup
      // 047: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 04a: athrow
      // 04b: iload 2
      // 04c: bipush 4
      // 04e: if_icmpgt 057
      // 051: iload 2
      // 052: bipush 2
      // 054: if_icmpge 059
      // 057: aconst_null
      // 058: areturn
      // 059: aload 0
      // 05a: iload 1
      // 05b: iinc 1 1
      // 05e: baload
      // 05f: sipush 255
      // 062: iand
      // 063: istore 3
      // 064: aload 0
      // 065: iload 1
      // 066: invokestatic net/rim/device/internal/media/metadata/ID3v2Reader.readSynchsafeInteger ([BI)I
      // 069: aload 0
      // 06a: arraylength
      // 06b: bipush 10
      // 06d: isub
      // 06e: invokestatic java/lang/Math.min (II)I
      // 071: istore 4
      // 073: iinc 1 4
      // 076: iload 2
      // 077: bipush 2
      // 079: if_icmpgt 085
      // 07c: iload 3
      // 07d: bipush 64
      // 07f: iand
      // 080: ifeq 085
      // 083: aconst_null
      // 084: areturn
      // 085: iload 3
      // 086: sipush 128
      // 089: iand
      // 08a: ifeq 0a5
      // 08d: iload 2
      // 08e: bipush 4
      // 090: if_icmpge 0a5
      // 093: aload 0
      // 094: bipush 10
      // 096: iload 4
      // 098: invokestatic net/rim/device/internal/media/metadata/ID3v2Reader.resynchronize ([BII)[B
      // 09b: astore 0
      // 09c: aload 0
      // 09d: arraylength
      // 09e: istore 4
      // 0a0: bipush 0
      // 0a1: istore 1
      // 0a2: goto 0a8
      // 0a5: iinc 4 10
      // 0a8: iload 2
      // 0a9: bipush 2
      // 0ab: if_icmple 12c
      // 0ae: iload 3
      // 0af: bipush 64
      // 0b1: iand
      // 0b2: ifeq 12c
      // 0b5: iload 2
      // 0b6: lookupswitch 81 1 3 18
      // 0c8: aload 0
      // 0c9: iload 1
      // 0ca: baload
      // 0cb: sipush 255
      // 0ce: iand
      // 0cf: bipush 24
      // 0d1: ishl
      // 0d2: aload 0
      // 0d3: iload 1
      // 0d4: bipush 1
      // 0d5: iadd
      // 0d6: baload
      // 0d7: sipush 255
      // 0da: iand
      // 0db: bipush 16
      // 0dd: ishl
      // 0de: ior
      // 0df: aload 0
      // 0e0: iload 1
      // 0e1: bipush 2
      // 0e3: iadd
      // 0e4: baload
      // 0e5: sipush 255
      // 0e8: iand
      // 0e9: bipush 8
      // 0eb: ishl
      // 0ec: ior
      // 0ed: aload 0
      // 0ee: iload 1
      // 0ef: bipush 3
      // 0f1: iadd
      // 0f2: baload
      // 0f3: sipush 255
      // 0f6: iand
      // 0f7: ior
      // 0f8: istore 5
      // 0fa: iload 5
      // 0fc: ifge 101
      // 0ff: aconst_null
      // 100: areturn
      // 101: iinc 1 4
      // 104: goto 120
      // 107: aload 0
      // 108: iload 1
      // 109: invokestatic net/rim/device/internal/media/metadata/ID3v2Reader.readSynchsafeInteger ([BI)I
      // 10c: istore 5
      // 10e: iload 5
      // 110: bipush 6
      // 112: if_icmpge 120
      // 115: new java/lang/Object
      // 118: dup
      // 119: ldc_w "ID3v2.4 extended header size less than minimum 6 bytes"
      // 11c: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 11f: athrow
      // 120: iload 1
      // 121: iload 5
      // 123: iadd
      // 124: istore 1
      // 125: goto 12c
      // 128: astore 5
      // 12a: aconst_null
      // 12b: areturn
      // 12c: new java/lang/Object
      // 12f: dup
      // 130: invokespecial net/rim/device/internal/media/metadata/MetaDataControlImpl.<init> ()V
      // 133: astore 5
      // 135: invokestatic net/rim/device/api/i18n/Locale.getDefault ()Lnet/rim/device/api/i18n/Locale;
      // 138: invokevirtual net/rim/device/api/i18n/Locale.getCode ()I
      // 13b: invokestatic com/sun/cldc/i18n/Helper.getSuggestedEncoding (I)Ljava/lang/String;
      // 13e: astore 6
      // 140: bipush 0
      // 141: istore 7
      // 143: iload 2
      // 144: bipush 2
      // 146: if_icmpgt 14e
      // 149: bipush 3
      // 14b: goto 150
      // 14e: bipush 4
      // 150: istore 9
      // 152: iload 1
      // 153: iload 4
      // 155: if_icmplt 15b
      // 158: goto 413
      // 15b: bipush 0
      // 15c: istore 8
      // 15e: bipush 0
      // 15f: istore 11
      // 161: iload 11
      // 163: iload 9
      // 165: if_icmpge 18f
      // 168: aload 0
      // 169: iload 1
      // 16a: baload
      // 16b: ifne 171
      // 16e: goto 413
      // 171: iload 8
      // 173: aload 0
      // 174: iload 1
      // 175: iinc 1 1
      // 178: baload
      // 179: sipush 255
      // 17c: iand
      // 17d: bipush 24
      // 17f: iload 11
      // 181: bipush 8
      // 183: imul
      // 184: isub
      // 185: ishl
      // 186: ior
      // 187: istore 8
      // 189: iinc 11 1
      // 18c: goto 161
      // 18f: bipush 0
      // 190: istore 11
      // 192: bipush 0
      // 193: istore 12
      // 195: bipush -1
      // 197: istore 10
      // 199: iload 2
      // 19a: tableswitch 26 1 3 279 26 68
      // 1b4: aload 0
      // 1b5: iload 1
      // 1b6: baload
      // 1b7: sipush 255
      // 1ba: iand
      // 1bb: bipush 16
      // 1bd: ishl
      // 1be: aload 0
      // 1bf: iload 1
      // 1c0: bipush 1
      // 1c1: iadd
      // 1c2: baload
      // 1c3: sipush 255
      // 1c6: iand
      // 1c7: bipush 8
      // 1c9: ishl
      // 1ca: ior
      // 1cb: aload 0
      // 1cc: iload 1
      // 1cd: bipush 2
      // 1cf: iadd
      // 1d0: baload
      // 1d1: sipush 255
      // 1d4: iand
      // 1d5: ior
      // 1d6: istore 7
      // 1d8: iinc 1 3
      // 1db: goto 33b
      // 1de: aload 0
      // 1df: iload 1
      // 1e0: baload
      // 1e1: sipush 255
      // 1e4: iand
      // 1e5: bipush 24
      // 1e7: ishl
      // 1e8: aload 0
      // 1e9: iload 1
      // 1ea: bipush 1
      // 1eb: iadd
      // 1ec: baload
      // 1ed: sipush 255
      // 1f0: iand
      // 1f1: bipush 16
      // 1f3: ishl
      // 1f4: ior
      // 1f5: aload 0
      // 1f6: iload 1
      // 1f7: bipush 2
      // 1f9: iadd
      // 1fa: baload
      // 1fb: sipush 255
      // 1fe: iand
      // 1ff: bipush 8
      // 201: ishl
      // 202: ior
      // 203: aload 0
      // 204: iload 1
      // 205: bipush 3
      // 207: iadd
      // 208: baload
      // 209: sipush 255
      // 20c: iand
      // 20d: ior
      // 20e: istore 7
      // 210: iload 7
      // 212: ifge 21d
      // 215: new java/lang/Object
      // 218: dup
      // 219: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 21c: athrow
      // 21d: iinc 1 5
      // 220: aload 0
      // 221: iload 1
      // 222: baload
      // 223: sipush 128
      // 226: iand
      // 227: ifeq 22e
      // 22a: bipush 1
      // 22b: goto 22f
      // 22e: bipush 0
      // 22f: istore 11
      // 231: aload 0
      // 232: iload 1
      // 233: baload
      // 234: bipush 32
      // 236: iand
      // 237: ifeq 23e
      // 23a: bipush 1
      // 23b: goto 23f
      // 23e: bipush 0
      // 23f: istore 13
      // 241: aload 0
      // 242: iload 1
      // 243: iinc 1 1
      // 246: baload
      // 247: bipush 64
      // 249: iand
      // 24a: ifeq 250
      // 24d: goto 404
      // 250: iload 11
      // 252: ifeq 2a0
      // 255: aload 0
      // 256: iload 1
      // 257: baload
      // 258: sipush 255
      // 25b: iand
      // 25c: bipush 24
      // 25e: ishl
      // 25f: aload 0
      // 260: iload 1
      // 261: bipush 1
      // 262: iadd
      // 263: baload
      // 264: sipush 255
      // 267: iand
      // 268: bipush 16
      // 26a: ishl
      // 26b: ior
      // 26c: aload 0
      // 26d: iload 1
      // 26e: bipush 2
      // 270: iadd
      // 271: baload
      // 272: sipush 255
      // 275: iand
      // 276: bipush 8
      // 278: ishl
      // 279: ior
      // 27a: aload 0
      // 27b: iload 1
      // 27c: bipush 3
      // 27e: iadd
      // 27f: baload
      // 280: sipush 255
      // 283: iand
      // 284: ior
      // 285: istore 10
      // 287: iload 10
      // 289: ifgt 28f
      // 28c: goto 404
      // 28f: iload 10
      // 291: ldc_w 1048576
      // 294: if_icmple 29a
      // 297: goto 404
      // 29a: iinc 1 4
      // 29d: iinc 7 -4
      // 2a0: iload 13
      // 2a2: ifne 2a8
      // 2a5: goto 33b
      // 2a8: iinc 1 1
      // 2ab: iinc 7 -1
      // 2ae: goto 33b
      // 2b1: aload 0
      // 2b2: iload 1
      // 2b3: invokestatic net/rim/device/internal/media/metadata/ID3v2Reader.readSynchsafeInteger ([BI)I
      // 2b6: istore 7
      // 2b8: iinc 1 5
      // 2bb: aload 0
      // 2bc: iload 1
      // 2bd: baload
      // 2be: bipush 8
      // 2c0: iand
      // 2c1: ifeq 2c8
      // 2c4: bipush 1
      // 2c5: goto 2c9
      // 2c8: bipush 0
      // 2c9: istore 11
      // 2cb: aload 0
      // 2cc: iload 1
      // 2cd: baload
      // 2ce: bipush 2
      // 2d0: iand
      // 2d1: ifeq 2d8
      // 2d4: bipush 1
      // 2d5: goto 2d9
      // 2d8: bipush 0
      // 2d9: istore 12
      // 2db: aload 0
      // 2dc: iload 1
      // 2dd: baload
      // 2de: bipush 1
      // 2df: iand
      // 2e0: ifeq 2e7
      // 2e3: bipush 1
      // 2e4: goto 2e8
      // 2e7: bipush 0
      // 2e8: istore 13
      // 2ea: aload 0
      // 2eb: iload 1
      // 2ec: baload
      // 2ed: bipush 4
      // 2ef: iand
      // 2f0: ifeq 2f9
      // 2f3: iinc 1 1
      // 2f6: goto 404
      // 2f9: aload 0
      // 2fa: iload 1
      // 2fb: iinc 1 1
      // 2fe: baload
      // 2ff: bipush 64
      // 301: iand
      // 302: ifeq 30b
      // 305: iinc 1 1
      // 308: iinc 7 -1
      // 30b: iload 13
      // 30d: ifeq 333
      // 310: aload 0
      // 311: iload 1
      // 312: invokestatic net/rim/device/internal/media/metadata/ID3v2Reader.readSynchsafeInteger ([BI)I
      // 315: istore 10
      // 317: iload 10
      // 319: ifgt 31f
      // 31c: goto 404
      // 31f: iload 10
      // 321: ldc_w 1048576
      // 324: if_icmple 32a
      // 327: goto 404
      // 32a: iinc 1 4
      // 32d: iinc 7 -4
      // 330: goto 33b
      // 333: iload 11
      // 335: ifeq 33b
      // 338: goto 404
      // 33b: iload 8
      // 33d: iload 2
      // 33e: invokestatic net/rim/device/internal/media/metadata/ID3v2Reader.getKeyForFrameID (II)Ljava/lang/String;
      // 341: astore 13
      // 343: aload 13
      // 345: ifnonnull 34b
      // 348: goto 404
      // 34b: aload 0
      // 34c: astore 14
      // 34e: iload 1
      // 34f: istore 15
      // 351: iload 7
      // 353: istore 16
      // 355: iload 12
      // 357: ifeq 36d
      // 35a: aload 14
      // 35c: iload 15
      // 35e: iload 16
      // 360: invokestatic net/rim/device/internal/media/metadata/ID3v2Reader.resynchronize ([BII)[B
      // 363: astore 14
      // 365: bipush 0
      // 366: istore 15
      // 368: aload 14
      // 36a: arraylength
      // 36b: istore 16
      // 36d: iload 11
      // 36f: ifeq 3f2
      // 372: iload 10
      // 374: ifgt 37a
      // 377: goto 404
      // 37a: iload 10
      // 37c: newarray 8
      // 37e: astore 17
      // 380: new java/lang/Object
      // 383: dup
      // 384: new java/lang/Object
      // 387: dup
      // 388: aload 14
      // 38a: iload 15
      // 38c: iload 16
      // 38e: invokespecial java/io/ByteArrayInputStream.<init> ([BII)V
      // 391: invokespecial net/rim/device/api/compress/ZLibInputStream.<init> (Ljava/io/InputStream;)V
      // 394: astore 18
      // 396: bipush 0
      // 397: istore 19
      // 399: iload 19
      // 39b: iload 10
      // 39d: if_icmpge 3c2
      // 3a0: aload 18
      // 3a2: aload 17
      // 3a4: iload 19
      // 3a6: iload 10
      // 3a8: iload 19
      // 3aa: isub
      // 3ab: invokevirtual java/io/InputStream.read ([BII)I
      // 3ae: istore 20
      // 3b0: iload 20
      // 3b2: ifgt 3b8
      // 3b5: goto 3c2
      // 3b8: iload 19
      // 3ba: iload 20
      // 3bc: iadd
      // 3bd: istore 19
      // 3bf: goto 399
      // 3c2: aload 18
      // 3c4: invokevirtual java/io/InputStream.close ()V
      // 3c7: goto 3d4
      // 3ca: astore 21
      // 3cc: aload 18
      // 3ce: invokevirtual java/io/InputStream.close ()V
      // 3d1: aload 21
      // 3d3: athrow
      // 3d4: iload 19
      // 3d6: iload 10
      // 3d8: if_icmpeq 3de
      // 3db: goto 404
      // 3de: aload 17
      // 3e0: astore 14
      // 3e2: bipush 0
      // 3e3: istore 15
      // 3e5: aload 14
      // 3e7: arraylength
      // 3e8: istore 16
      // 3ea: goto 3f2
      // 3ed: astore 17
      // 3ef: goto 404
      // 3f2: aload 14
      // 3f4: iload 15
      // 3f6: iload 16
      // 3f8: aload 5
      // 3fa: aload 13
      // 3fc: iload 8
      // 3fe: iload 2
      // 3ff: aload 6
      // 401: invokestatic net/rim/device/internal/media/metadata/ID3v2Reader.readFrameData ([BIILnet/rim/device/internal/media/metadata/MetaDataControlImpl;Ljava/lang/String;IILjava/lang/String;)V
      // 404: iload 1
      // 405: iload 7
      // 407: iadd
      // 408: istore 1
      // 409: goto 152
      // 40c: astore 7
      // 40e: goto 413
      // 411: astore 7
      // 413: aload 5
      // 415: invokevirtual net/rim/device/internal/media/metadata/MetaDataControlImpl.size ()I
      // 418: ifle 41e
      // 41b: aload 5
      // 41d: areturn
      // 41e: aconst_null
      // 41f: areturn
      // try (103 -> 144): 164 null
      // try (145 -> 163): 164 null
      // try (493 -> 512): 515 null
      // try (515 -> 516): 515 null
      // try (478 -> 523): 532 null
      // try (524 -> 531): 532 null
      // try (175 -> 548): 548 null
      // try (175 -> 548): 550 null
   }

   private static final byte[] resynchronize(byte[] data, int offset, int length) {
      length = Math.min(length, data.length - offset);
      byte[] resynchronizedData = new byte[length];
      int newIndex = 0;

      for (int oldIndex = offset; oldIndex < offset + length; oldIndex++) {
         if (data[oldIndex] != 0 || oldIndex <= offset || (data[oldIndex - 1] & 255) != 255) {
            resynchronizedData[newIndex++] = data[oldIndex];
         }
      }

      Array.resize(resynchronizedData, newIndex);
      return resynchronizedData;
   }

   private static final void readFrameData(
      byte[] frameData, int offset, int length, MetaDataControlImpl metaData, String key, int frameID, int version, String defaultEncoding
   ) {
      ID3v2Reader$TextStringDescriptor textStringDescriptor = null;
      MetaDataObject binaryObject = null;
      boolean readLanguage = false;
      switch (frameID) {
         case 1095780675:
         case 1195724544:
         case 1195724610:
         case 1346978560:
            binaryObject = (MetaDataObject)(new Object());
            break;
         case 1129270528:
         case 1129270605:
         case 1431522388:
            readLanguage = true;
         case 1415075840:
         case 1415075928:
         case 1465407488:
         case 1465407576:
            textStringDescriptor = new ID3v2Reader$TextStringDescriptor();
            break;
         case 1431520594:
            readLanguage = true;
      }

      String value = readTextString(
         frameData, offset, length, frameID >> 24 == 87 ? 0 : -1, textStringDescriptor, binaryObject, readLanguage, frameID, defaultEncoding
      );
      if (value != null && value.length() > 0) {
         if (textStringDescriptor != null && textStringDescriptor._description != null && textStringDescriptor._description.length() > 0) {
            if (metaData.containsKey(textStringDescriptor._description)) {
               return;
            }

            key = textStringDescriptor._description;
         }

         metaData.put(key, decodeTextString(value, frameID, version));
      } else {
         if (binaryObject != null) {
            metaData.addObject(binaryObject);
         }
      }
   }

   private static final String readTextString(
      byte[] tag,
      int offset,
      int frameSize,
      int encoding,
      ID3v2Reader$TextStringDescriptor textStringDescriptor,
      MetaDataObject binaryObject,
      boolean readLanguage,
      int frameID,
      String defaultEncoding
   ) {
      if (frameSize <= 0) {
         return null;
      }

      int secondStringEncoding = encoding;
      if (encoding < 0 || textStringDescriptor != null) {
         encoding = tag[offset++];
         if (secondStringEncoding < 0) {
            secondStringEncoding = encoding;
         }

         frameSize--;
      }

      if (binaryObject != null) {
         if (frameID != 1346978560) {
            encoding = 0;
         } else {
            if (frameSize < 4) {
               return null;
            }

            switch (tag[offset]) {
               case 45:
                  if ((char)tag[offset + 1] == '-' && (char)tag[offset + 2] == '>') {
                     binaryObject.setMIMEType("-->");
                  }
                  break;
               case 66:
               case 98:
                  if (Character.toUpperCase((char)tag[offset + 1]) == 'M' && Character.toUpperCase((char)tag[offset + 2]) == 'P') {
                     binaryObject.setMIMEType("image/bmp");
                  }
                  break;
               case 71:
               case 103:
                  if (Character.toUpperCase((char)tag[offset + 1]) == 'I' && Character.toUpperCase((char)tag[offset + 2]) == 'F') {
                     binaryObject.setMIMEType("image/gif");
                  }
                  break;
               case 74:
               case 106:
                  if (Character.toUpperCase((char)tag[offset + 1]) == 'P' && Character.toUpperCase((char)tag[offset + 2]) == 'G') {
                     binaryObject.setMIMEType("image/jpeg");
                  }
                  break;
               case 80:
               case 112:
                  if (Character.toUpperCase((char)tag[offset + 1]) == 'N' && Character.toUpperCase((char)tag[offset + 2]) == 'G') {
                     binaryObject.setMIMEType("image/png");
                  }
            }

            binaryObject.setPictureType(tag[offset + 3]);
            offset += 4;
            frameSize -= 4;
         }
      }

      if (readLanguage) {
         offset += 3;
         frameSize -= 3;
      }

      String encodingStr = null;
      int numStringsRead = 0;

      while (true) {
         switch (encoding) {
            case -1:
               return null;
            case 0:
            default:
               encodingStr = defaultEncoding;
               break;
            case 1:
               if (frameSize < 2) {
                  break;
               }

               if ((tag[offset] & 255) == 254 && (tag[offset + 1] & 255) == 255) {
                  encodingStr = "UTF-16BE";
                  offset += 2;
                  frameSize -= 2;
               } else if ((tag[offset] & 255) == 255 && (tag[offset + 1] & 255) == 254) {
                  encodingStr = "UTF-16LE";
                  offset += 2;
                  frameSize -= 2;
               } else if (encodingStr == null && (tag[offset] != 0 || tag[offset + 1] != 0)) {
                  return null;
               }
               break;
            case 2:
               encodingStr = "UTF-16BE";
               break;
            case 3:
               encodingStr = "UTF-8";
         }

         int startIndex = offset;

         int byteLength;
         for (byteLength = 0; frameSize-- > 0; byteLength++) {
            if (tag[offset++] == 0) {
               if (encoding != 1 && encoding != 2) {
                  break;
               }

               if ((byteLength & 1) == 0 && frameSize > 0 && tag[offset] == 0) {
                  offset++;
                  frameSize--;
                  break;
               }
            }
         }

         String str;
         if (byteLength > 0) {
            try {
               str = (String)(new Object(tag, startIndex, byteLength, encodingStr));
            } finally {
               ;
            }
         } else {
            str = "";
         }

         if (binaryObject == null) {
            if (textStringDescriptor == null || numStringsRead != 0) {
               return str;
            }

            textStringDescriptor._description = str;
            encoding = secondStringEncoding;
         } else {
            if (frameSize <= 0) {
               return null;
            }

            byte[] data;
            data = null;
            label235:
            switch (numStringsRead) {
               case -1:
                  return null;
               case 0:
               default:
                  switch (frameID) {
                     case 1095780675:
                        binaryObject.setPictureType(tag[offset++]);
                        frameSize--;
                     default:
                        if (str.length() > 0) {
                           binaryObject.setMIMEType(str);
                        } else if (frameID == 1095780675) {
                           binaryObject.setMIMEType("image/");
                        }

                        encoding = secondStringEncoding;
                        break label235;
                     case 1346978560:
                        if (str.length() > 0) {
                           binaryObject.setDescription(str);
                        }

                        data = new byte[frameSize];
                        break label235;
                  }
               case 1:
                  if (frameID == 1095780675) {
                     if (str.length() > 0) {
                        binaryObject.setDescription(str);
                     }

                     data = new byte[frameSize];
                  } else if (str.length() > 0) {
                     binaryObject.setFilename(str);
                  }
                  break;
               case 2:
                  if (str.length() > 0) {
                     binaryObject.setDescription(str);
                  }

                  data = new byte[frameSize];
            }

            if (data != null) {
               System.arraycopy(tag, offset, data, 0, data.length);
               if ((frameID == 1095780675 || frameID == 1346978560) && "-->".equals(binaryObject.getMIMEType())) {
                  binaryObject.setURL((String)(new Object(data)));
                  binaryObject.setMIMEType(null);
                  return null;
               }

               binaryObject.setData(data);
               return null;
            }
         }

         numStringsRead++;
      }
   }

   private static final String decodeTextString(String str, int frameID, int version) {
      switch (frameID) {
         case 1413697280:
         case 1413697358:
            if (str != null) {
               if (version >= 4) {
                  return resolveContentType(str);
               }

               int strLength = str.length();
               if (strLength > 1 && str.charAt(0) == '(') {
                  StringBuffer buffer = (StringBuffer)(new Object());

                  for (int i = 0; i < strLength; i++) {
                     if (str.charAt(i) != '(') {
                        if (buffer.length() > 0) {
                           buffer.append('-');
                        }

                        buffer.append(str.substring(i));
                        break;
                     }

                     if (i + 1 < strLength && str.charAt(i + 1) == '(') {
                        if (buffer.length() > 0) {
                           buffer.append(' ');
                        }

                        buffer.append(str.substring(i + 1));
                        break;
                     }

                     int closeParenthesis = str.indexOf(41, i + 1);
                     if (closeParenthesis <= i) {
                        if (buffer.length() > 0) {
                           buffer.append(' ');
                        }

                        buffer.append(str.substring(i));
                        break;
                     }

                     String resolvedType = resolveContentType(str.substring(i + 1, closeParenthesis));
                     if (resolvedType != null && !str.regionMatches(true, closeParenthesis + 1, resolvedType, 0, resolvedType.length())) {
                        if (buffer.length() > 0) {
                           buffer.append('-');
                        }

                        buffer.append(resolvedType);
                     }

                     i = closeParenthesis;
                  }

                  return buffer.toString();
               }
            }
            break;
         case 1413697360:
         case 1413698048:
            if (str != null && str.length() > 0 && str.indexOf(169) == -1 && !StringUtilities.startsWithIgnoreCase(str, "Copyright", 1701707776)) {
               return ((StringBuffer)(new Object("Copyright © "))).append(str).toString();
            }
            break;
         case 1414550095:
            if (str != null && str.length() > 0 && str.indexOf(8471) == -1 && !StringUtilities.startsWithIgnoreCase(str, "Produced", 1701707776)) {
               return ((StringBuffer)(new Object("Produced ℗ "))).append(str).toString();
            }
      }

      return str;
   }

   private static final String resolveContentType(String param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: ifnull 50
      // 04: aload 0
      // 05: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 08: invokestatic net/rim/device/internal/media/metadata/ID3v1Reader.genreCodeToString (I)Ljava/lang/String;
      // 0b: areturn
      // 0c: astore 1
      // 0d: goto 13
      // 10: astore 1
      // 11: aload 0
      // 12: areturn
      // 13: aload 0
      // 14: invokevirtual java/lang/String.length ()I
      // 17: bipush 2
      // 19: if_icmpne 50
      // 1c: aload 0
      // 1d: ldc_w "RX"
      // 20: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 23: ifeq 36
      // 26: ldc2_w 7712504258262123478
      // 29: ldc_w "net.rim.device.internal.resource.Genre"
      // 2c: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 2f: sipush 148
      // 32: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 35: areturn
      // 36: aload 0
      // 37: ldc_w "CR"
      // 3a: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 3d: ifeq 50
      // 40: ldc2_w 7712504258262123478
      // 43: ldc_w "net.rim.device.internal.resource.Genre"
      // 46: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 49: sipush 149
      // 4c: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 4f: areturn
      // 50: aload 0
      // 51: areturn
      // try (2 -> 5): 6 null
      // try (2 -> 5): 8 null
   }

   private static final int readSynchsafeInteger(byte[] tag, int offset) {
      if ((tag[offset] & 128) == 0 && (tag[offset + 1] & 128) == 0 && (tag[offset + 2] & 128) == 0 && (tag[offset + 3] & 128) == 0) {
         return tag[offset] << 21 | tag[offset + 1] << 14 | tag[offset + 2] << 7 | tag[offset + 3];
      } else {
         throw new Object();
      }
   }

   private static final String getKeyForFrameID(int frameID, int version) {
      if (version > 2) {
         switch (frameID) {
            case 1095780675:
               return "";
            case 1129270605:
               return "comment";
            case 1195724610:
               return "";
            case 1413565506:
               return "album";
            case 1413632077:
               return "BPM";
            case 1413697357:
               return "composer";
            case 1413697358:
               return "genre";
            case 1413697360:
               return "copyright";
            case 1413759316:
               if (version == 3) {
                  return "day-month";
               }
               break;
            case 1413762137:
               return "playlist delay";
            case 1413828163:
               return "encoder";
            case 1413830740:
               return "text writer";
            case 1413893204:
               return "file type";
            case 1414090053:
               if (version == 3) {
                  return "time";
               }
               break;
            case 1414091825:
               return "content group description";
            case 1414091826:
               return "title";
            case 1414091827:
               return "subtitle";
            case 1414219097:
               return "initial key";
            case 1414283598:
               return "language";
            case 1414284622:
               return "length";
            case 1414350148:
               return "media type";
            case 1414480204:
               return "original album";
            case 1414481486:
               return "original filename";
            case 1414483033:
               return "original text writer";
            case 1414484037:
               return "original artist";
            case 1414484569:
               if (version == 3) {
                  return "original release year";
               }
               break;
            case 1414485838:
               return "file owner";
            case 1414546737:
               return "author";
            case 1414546738:
               return "orchestra";
            case 1414546739:
               return "conductor";
            case 1414546740:
               return "remixed by";
            case 1414549331:
               return "part of a set";
            case 1414550850:
               return "publisher";
            case 1414677323:
               return "track number";
            case 1414677569:
               if (version == 3) {
                  return "recording dates";
               }
               break;
            case 1414681422:
               return "Internet radio station name";
            case 1414681423:
               return "Internet radio station owner";
            case 1414744410:
               if (version == 3) {
                  return "size";
               }
               break;
            case 1414746691:
               return "ISRC";
            case 1414746949:
               return "encoder settings";
            case 1415075928:
               return "";
            case 1415136594:
               if (version == 3) {
                  return "date";
               }
               break;
            case 1431520594:
               return "terms of use";
            case 1431522388:
               return "text transcription";
            case 1464029005:
               return "commercial information URL";
            case 1464029008:
               return "copyright URL";
            case 1464811846:
               return "official audio file URL";
            case 1464811858:
               return "official artist URL";
            case 1464811859:
               return "official audio source URL";
            case 1464816211:
               return "official Internet radio station URL";
            case 1464877401:
               return "payment URL";
            case 1464882498:
               return "publisher URL";
            case 1465407576:
               return "URL";
         }

         if (version >= 4) {
            switch (frameID) {
               case 1413760334:
                  return "encoding time";
               case 1413762898:
                  return "original release time";
               case 1413763651:
                  return "date";
               case 1413763660:
                  return "release time";
               case 1413764167:
                  return "tagging time";
               case 1414352719:
                  return "mood";
               case 1414550095:
                  return "produced notice";
               case 1414745921:
                  return "album sort order";
               case 1414745936:
                  return "performer sort order key";
               case 1414745940:
                  return "title sort order";
               case 1414746964:
                  return "set subtitle";
            }
         }
      } else {
         switch (frameID) {
            case 1129270528:
               return "comment";
            case 1195724544:
            case 1346978560:
               return "";
            case 1413565440:
               return "album";
            case 1413632000:
               return "BPM";
            case 1413696768:
               return "composer";
            case 1413697280:
               return "genre";
            case 1413698048:
               return "copyright";
            case 1413759232:
               return "day-month";
            case 1413765376:
               return "playlist delay";
            case 1413828096:
               return "encoder";
            case 1413895168:
               return "file type";
            case 1414089984:
               return "time";
            case 1414219008:
               return "initial key";
            case 1414283520:
               return "language";
            case 1414284544:
               return "length";
            case 1414353920:
               return "media type";
            case 1414480128:
               return "original artist";
            case 1414481408:
               return "original filename";
            case 1414482944:
               return "original text writer";
            case 1414484480:
               return "original release year";
            case 1414484992:
               return "original album";
            case 1414541568:
               return "author";
            case 1414541824:
               return "orchestra";
            case 1414542080:
               return "conductor";
            case 1414542336:
               return "remixed by";
            case 1414545664:
               return "part of a set";
            case 1414545920:
               return "publisher";
            case 1414677248:
               return "ISRC";
            case 1414677504:
               return "recording dates";
            case 1414679296:
               return "track number";
            case 1414744320:
               return "size";
            case 1414746880:
               return "encoder settings";
            case 1414803712:
               return "content group description";
            case 1414803968:
               return "title";
            case 1414804224:
               return "subtitle";
            case 1415074816:
               return "text writer";
            case 1415075840:
               return "";
            case 1415136512:
               return "date";
            case 1431065600:
               return "text transcription";
            case 1463895552:
               return "official audio file URL";
            case 1463898624:
               return "official artist URL";
            case 1463898880:
               return "official audio source URL";
            case 1464028416:
               return "commercial information URL";
            case 1464029184:
               return "copyright URL";
            case 1464877568:
               return "publisher URL";
            case 1465407488:
               return "URL";
         }
      }

      return null;
   }
}
