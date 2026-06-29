package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;

public final class RIMMessagingIncomingMessage extends RIMMessagingMessage {
   private int _bodyLength = 0;

   public final int getBodyLength() {
      return this._bodyLength;
   }

   public final byte getRecipientType() {
      byte[] bytes = super._headerParameters.getFirst((byte)-16);
      return bytes != null && bytes.length > 0 ? bytes[0] : 1;
   }

   public final boolean isReplyProhibited() {
      return super._headerParameters.has((byte)-12);
   }

   public final boolean isMarkedAsRead() {
      byte[] bytes = super._headerParameters.getFirst((byte)-4);
      return bytes != null ? bytes.length > 0 && (bytes[0] & 1) != 0 : false;
   }

   public final boolean isTransmitted() {
      byte[] bytes = super._headerParameters.getFirst((byte)-3);
      return bytes != null && bytes.length > 0 && (bytes[0] & 1) != 0;
   }

   public final boolean isSynchronized() {
      byte[] bytes = super._headerParameters.getFirst((byte)-3);
      return bytes != null && bytes.length > 0 && (bytes[0] & 2) != 0;
   }

   public final boolean isNotificationDisabled() {
      byte[] bytes = super._headerParameters.getFirst((byte)-3);
      return bytes != null && bytes.length > 0 && (bytes[0] & 4) != 0;
   }

   @Override
   public final void read(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEConverterRegistry.getDefaultConverter ()Lnet/rim/device/apps/api/utility/serialization/Converter;
      // 003: astore 2
      // 004: aconst_null
      // 005: astore 3
      // 006: aconst_null
      // 007: astore 4
      // 009: aconst_null
      // 00a: astore 5
      // 00c: aconst_null
      // 00d: astore 6
      // 00f: aload 1
      // 010: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 013: pop
      // 014: aload 0
      // 015: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMessage._headerParameters Lnet/rim/device/apps/api/transmission/rim/CMIMEParameters;
      // 018: aload 1
      // 019: bipush 0
      // 01a: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEParameters.read (Lnet/rim/device/api/util/DataBuffer;B)I
      // 01d: pop
      // 01e: aload 1
      // 01f: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 022: ifne 02f
      // 025: aload 0
      // 026: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMessage._bodyParameters Lnet/rim/device/apps/api/transmission/rim/CMIMEParameters;
      // 029: aload 1
      // 02a: bipush 0
      // 02b: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEParameters.read (Lnet/rim/device/api/util/DataBuffer;B)I
      // 02e: pop
      // 02f: aload 0
      // 030: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMessage._bodyParameters Lnet/rim/device/apps/api/transmission/rim/CMIMEParameters;
      // 033: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEParameters.isEmpty ()Z
      // 036: ifeq 03a
      // 039: return
      // 03a: aload 0
      // 03b: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMessage._bodyParameters Lnet/rim/device/apps/api/transmission/rim/CMIMEParameters;
      // 03e: bipush 1
      // 03f: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEParameters.getFirst (B)[B
      // 042: astore 7
      // 044: new net/rim/device/apps/api/transmission/rim/CMIMEContentType
      // 047: dup
      // 048: aload 7
      // 04a: invokespecial net/rim/device/apps/api/transmission/rim/CMIMEContentType.<init> ([B)V
      // 04d: astore 8
      // 04f: aload 8
      // 051: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.isTypeOpaque ()Z
      // 054: ifne 05a
      // 057: goto 166
      // 05a: aload 0
      // 05b: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMessage._bodyParameters Lnet/rim/device/apps/api/transmission/rim/CMIMEParameters;
      // 05e: astore 4
      // 060: aload 4
      // 062: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEConverterRegistry.getDefaultConverter (Lnet/rim/device/apps/api/transmission/Parameters;)Lnet/rim/device/apps/api/utility/serialization/Converter;
      // 065: astore 3
      // 066: aload 1
      // 067: invokevirtual net/rim/device/api/util/DataBuffer.readByteArray ()[B
      // 06a: astore 6
      // 06c: aload 0
      // 06d: aload 6
      // 06f: ifnull 078
      // 072: aload 6
      // 074: arraylength
      // 075: goto 07a
      // 078: bipush -1
      // 07a: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 07d: aconst_null
      // 07e: astore 5
      // 080: aload 7
      // 082: ifnull 105
      // 085: aload 7
      // 087: arraylength
      // 088: ifle 105
      // 08b: aload 7
      // 08d: bipush 0
      // 08e: baload
      // 08f: bipush 8
      // 091: if_icmpne 105
      // 094: aload 0
      // 095: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 098: ifle 105
      // 09b: aload 8
      // 09d: new java/lang/Object
      // 0a0: dup
      // 0a1: ldc_w ";charset="
      // 0a4: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0a7: aload 6
      // 0a9: bipush 0
      // 0aa: baload
      // 0ab: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.getEncoding (B)Ljava/lang/String;
      // 0ae: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b1: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0b4: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.updateContentType (Ljava/lang/String;)V
      // 0b7: aload 0
      // 0b8: aload 0
      // 0b9: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 0bc: bipush 1
      // 0bd: isub
      // 0be: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 0c1: aload 0
      // 0c2: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 0c5: ifle 105
      // 0c8: aload 0
      // 0c9: aload 6
      // 0cb: bipush 0
      // 0cc: baload
      // 0cd: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setEncoding (B)V
      // 0d0: aload 6
      // 0d2: bipush 0
      // 0d3: baload
      // 0d4: sipush 128
      // 0d7: iand
      // 0d8: sipush 128
      // 0db: if_icmpne 105
      // 0de: aload 6
      // 0e0: bipush 0
      // 0e1: aload 0
      // 0e2: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 0e5: bipush 1
      // 0e6: iadd
      // 0e7: invokestatic net/rim/device/internal/i18n/UnicodeServiceUtilities.detectFutureData ([BII)I
      // 0ea: istore 9
      // 0ec: iload 9
      // 0ee: ifle 105
      // 0f1: iload 9
      // 0f3: aload 0
      // 0f4: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 0f7: if_icmpgt 105
      // 0fa: aload 0
      // 0fb: aload 0
      // 0fc: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 0ff: iload 9
      // 101: isub
      // 102: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 105: aload 3
      // 106: aload 6
      // 108: aload 4
      // 10a: invokeinterface net/rim/device/apps/api/utility/serialization/Converter.convert ([BLjava/lang/Object;)Ljava/lang/Object; 3
      // 10f: astore 5
      // 111: goto 12a
      // 114: astore 9
      // 116: ldc2_w 3020044433160143544
      // 119: ldc_w 1382118245
      // 11c: bipush 2
      // 11e: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 121: pop
      // 122: goto 12a
      // 125: astore 9
      // 127: aconst_null
      // 128: astore 5
      // 12a: aload 5
      // 12c: ifnonnull 13b
      // 12f: aload 2
      // 130: aload 6
      // 132: aload 4
      // 134: invokeinterface net/rim/device/apps/api/utility/serialization/Converter.convert ([BLjava/lang/Object;)Ljava/lang/Object; 3
      // 139: astore 5
      // 13b: aload 8
      // 13d: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.isTextType ()Z
      // 140: ifeq 158
      // 143: aload 0
      // 144: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.hasText ()Z
      // 147: ifne 158
      // 14a: aload 0
      // 14b: aload 5
      // 14d: aload 4
      // 14f: aload 8
      // 151: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.getFullType ()Ljava/lang/String;
      // 154: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setText (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V
      // 157: return
      // 158: aload 0
      // 159: aload 5
      // 15b: aload 4
      // 15d: aload 8
      // 15f: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.getFullType ()Ljava/lang/String;
      // 162: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.addAttachment (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V
      // 165: return
      // 166: aload 0
      // 167: aload 1
      // 168: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 16b: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 16e: bipush 0
      // 16f: istore 9
      // 171: aload 1
      // 172: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 175: ifeq 17b
      // 178: goto 2c5
      // 17b: new net/rim/device/apps/api/transmission/Parameters
      // 17e: dup
      // 17f: bipush 5
      // 181: bipush 5
      // 183: invokespecial net/rim/device/apps/api/transmission/Parameters.<init> (II)V
      // 186: astore 4
      // 188: aload 4
      // 18a: aload 1
      // 18b: bipush 0
      // 18c: invokevirtual net/rim/device/apps/api/transmission/Parameters.read (Lnet/rim/device/api/util/DataBuffer;B)I
      // 18f: pop
      // 190: aload 4
      // 192: bipush 1
      // 193: invokevirtual net/rim/device/apps/api/transmission/Parameters.getFirst (B)[B
      // 196: astore 7
      // 198: aload 7
      // 19a: ifnonnull 1a0
      // 19d: goto 2c5
      // 1a0: aload 7
      // 1a2: arraylength
      // 1a3: ifgt 1a9
      // 1a6: goto 2c5
      // 1a9: aload 8
      // 1ab: aload 7
      // 1ad: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.setBytes ([B)V
      // 1b0: aload 4
      // 1b2: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEConverterRegistry.getDefaultConverter (Lnet/rim/device/apps/api/transmission/Parameters;)Lnet/rim/device/apps/api/utility/serialization/Converter;
      // 1b5: astore 3
      // 1b6: aload 8
      // 1b8: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.isTextType ()Z
      // 1bb: ifeq 1c9
      // 1be: aload 0
      // 1bf: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.hasText ()Z
      // 1c2: ifne 1c9
      // 1c5: bipush 1
      // 1c6: goto 1ca
      // 1c9: bipush 0
      // 1ca: istore 9
      // 1cc: aload 1
      // 1cd: invokevirtual net/rim/device/api/util/DataBuffer.readByteArray ()[B
      // 1d0: astore 6
      // 1d2: iload 9
      // 1d4: ifeq 1e8
      // 1d7: aload 0
      // 1d8: aload 6
      // 1da: ifnull 1e3
      // 1dd: aload 6
      // 1df: arraylength
      // 1e0: goto 1e5
      // 1e3: bipush -1
      // 1e5: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 1e8: aconst_null
      // 1e9: astore 5
      // 1eb: aload 7
      // 1ed: bipush 0
      // 1ee: baload
      // 1ef: bipush 8
      // 1f1: if_icmpne 26a
      // 1f4: aload 0
      // 1f5: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 1f8: ifle 26a
      // 1fb: aload 8
      // 1fd: new java/lang/Object
      // 200: dup
      // 201: ldc_w ";charset="
      // 204: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 207: aload 6
      // 209: bipush 0
      // 20a: baload
      // 20b: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.getEncoding (B)Ljava/lang/String;
      // 20e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 211: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 214: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.updateContentType (Ljava/lang/String;)V
      // 217: iload 9
      // 219: ifeq 26a
      // 21c: aload 0
      // 21d: aload 0
      // 21e: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 221: bipush 1
      // 222: isub
      // 223: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 226: aload 0
      // 227: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 22a: ifle 26a
      // 22d: aload 0
      // 22e: aload 6
      // 230: bipush 0
      // 231: baload
      // 232: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setEncoding (B)V
      // 235: aload 6
      // 237: bipush 0
      // 238: baload
      // 239: sipush 128
      // 23c: iand
      // 23d: sipush 128
      // 240: if_icmpne 26a
      // 243: aload 6
      // 245: bipush 0
      // 246: aload 0
      // 247: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 24a: bipush 1
      // 24b: iadd
      // 24c: invokestatic net/rim/device/internal/i18n/UnicodeServiceUtilities.detectFutureData ([BII)I
      // 24f: istore 10
      // 251: iload 10
      // 253: ifle 26a
      // 256: iload 10
      // 258: aload 0
      // 259: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 25c: if_icmpgt 26a
      // 25f: aload 0
      // 260: aload 0
      // 261: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 264: iload 10
      // 266: isub
      // 267: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingIncomingMessage._bodyLength I
      // 26a: aload 3
      // 26b: aload 6
      // 26d: aload 4
      // 26f: invokeinterface net/rim/device/apps/api/utility/serialization/Converter.convert ([BLjava/lang/Object;)Ljava/lang/Object; 3
      // 274: astore 5
      // 276: goto 28f
      // 279: astore 10
      // 27b: ldc2_w 3020044433160143544
      // 27e: ldc_w 1382118245
      // 281: bipush 2
      // 283: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 286: pop
      // 287: goto 28f
      // 28a: astore 10
      // 28c: aconst_null
      // 28d: astore 5
      // 28f: aload 5
      // 291: ifnonnull 2a0
      // 294: aload 2
      // 295: aload 6
      // 297: aload 4
      // 299: invokeinterface net/rim/device/apps/api/utility/serialization/Converter.convert ([BLjava/lang/Object;)Ljava/lang/Object; 3
      // 29e: astore 5
      // 2a0: iload 9
      // 2a2: ifeq 2b5
      // 2a5: aload 0
      // 2a6: aload 5
      // 2a8: aload 4
      // 2aa: aload 8
      // 2ac: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.getFullType ()Ljava/lang/String;
      // 2af: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setText (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V
      // 2b2: goto 171
      // 2b5: aload 0
      // 2b6: aload 5
      // 2b8: aload 4
      // 2ba: aload 8
      // 2bc: invokevirtual net/rim/device/apps/api/transmission/rim/CMIMEContentType.getFullType ()Ljava/lang/String;
      // 2bf: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.addAttachment (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V
      // 2c2: goto 171
      // 2c5: return
      // try (132 -> 137): 138 null
      // try (132 -> 137): 145 null
      // try (301 -> 306): 307 null
      // try (301 -> 306): 314 null
   }
}
