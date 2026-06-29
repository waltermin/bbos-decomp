package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.transmission.Parameters;

public final class RIMMessagingMoreMessage extends RIMMessagingTransmission {
   private Parameters _parameters = new Parameters(5, 8);
   private Parameters _nestedParameters;
   private byte _encodingCode = -1;
   private int _bodyLength;

   public final int getFolderIdentifier() {
      byte[] bytes = this._parameters.getFirst((byte)-9);
      return bytes != null ? CMIMEUtilities.decodeInteger(bytes) : 0;
   }

   public final int getLength() {
      return CMIMEUtilities.getGMEInteger((byte[][][])this._parameters.get((byte)48), 3);
   }

   public final int getBodyLength() {
      return this._bodyLength;
   }

   public final int getOffset() {
      return CMIMEUtilities.getGMEInteger((byte[][][])this._parameters.get((byte)48), 2);
   }

   public final int getPartIdentifier() {
      return CMIMEUtilities.getGMEInteger((byte[][][])this._parameters.get((byte)48), 1);
   }

   public final int getReferenceIdentifier() {
      return CMIMEUtilities.getGMEInteger((byte[][][])this._parameters.get((byte)48), 0);
   }

   public final Object getText() {
      try {
         return CMIMEUtilities.getTextObject(this._parameters.getFirst((byte)64), this._nestedParameters);
      } finally {
         EventLogger.logEvent(3020044433160143544L, 1382904677, 2);
         return null;
      }
   }

   public final byte getEncoding() {
      return this._encodingCode;
   }

   public final byte[] getRawBytes() {
      return this._parameters.getFirst((byte)64);
   }

   public final int getConvertedAvailableLength() {
      if (this._nestedParameters != null) {
         byte[] lengthBytes = this._nestedParameters.getFirst((byte)3);
         return lengthBytes != null ? CMIMEUtilities.decodeInteger(lengthBytes) : 0;
      } else {
         return 0;
      }
   }

   public final byte[] getObjectDescriptor() {
      return this._nestedParameters != null ? this._nestedParameters.getFirst((byte)2) : null;
   }

   public final byte[] getConversionContentType() {
      return this._nestedParameters != null ? this._nestedParameters.getFirst((byte)1) : null;
   }

   @Override
   public final void read(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._parameters Lnet/rim/device/apps/api/transmission/Parameters;
      // 004: aload 1
      // 005: invokevirtual net/rim/device/apps/api/transmission/Parameters.read (Lnet/rim/device/api/util/DataBuffer;)I
      // 008: pop
      // 009: aload 0
      // 00a: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._parameters Lnet/rim/device/apps/api/transmission/Parameters;
      // 00d: bipush 112
      // 00f: invokevirtual net/rim/device/apps/api/transmission/Parameters.getFirst (B)[B
      // 012: astore 2
      // 013: aload 2
      // 014: ifnull 039
      // 017: aload 0
      // 018: new net/rim/device/apps/api/transmission/Parameters
      // 01b: dup
      // 01c: bipush 3
      // 01e: bipush 1
      // 01f: invokespecial net/rim/device/apps/api/transmission/Parameters.<init> (II)V
      // 022: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._nestedParameters Lnet/rim/device/apps/api/transmission/Parameters;
      // 025: aload 0
      // 026: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._nestedParameters Lnet/rim/device/apps/api/transmission/Parameters;
      // 029: new java/lang/Object
      // 02c: dup
      // 02d: aload 2
      // 02e: bipush 0
      // 02f: aload 2
      // 030: arraylength
      // 031: bipush 1
      // 032: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 035: invokevirtual net/rim/device/apps/api/transmission/Parameters.read (Lnet/rim/device/api/util/DataBuffer;)I
      // 038: pop
      // 039: aload 0
      // 03a: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._parameters Lnet/rim/device/apps/api/transmission/Parameters;
      // 03d: bipush 64
      // 03f: bipush 0
      // 040: invokevirtual net/rim/device/apps/api/transmission/Parameters.resolveInIndex (BI)I
      // 043: istore 3
      // 044: iload 3
      // 045: bipush -1
      // 047: if_icmpgt 04d
      // 04a: goto 151
      // 04d: aload 0
      // 04e: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._parameters Lnet/rim/device/apps/api/transmission/Parameters;
      // 051: invokevirtual net/rim/device/apps/api/transmission/Parameters.getDataBuffer ()Lnet/rim/device/api/util/DataBuffer;
      // 054: astore 4
      // 056: aload 4
      // 058: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 05b: istore 5
      // 05d: aload 4
      // 05f: iload 3
      // 060: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 063: aload 4
      // 065: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 068: pop
      // 069: aload 0
      // 06a: aload 4
      // 06c: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 06f: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 072: aload 0
      // 073: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 076: ifgt 07c
      // 079: goto 133
      // 07c: aload 0
      // 07d: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._nestedParameters Lnet/rim/device/apps/api/transmission/Parameters;
      // 080: ifnonnull 086
      // 083: goto 133
      // 086: aload 0
      // 087: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._nestedParameters Lnet/rim/device/apps/api/transmission/Parameters;
      // 08a: bipush 1
      // 08b: invokevirtual net/rim/device/apps/api/transmission/Parameters.getFirst (B)[B
      // 08e: astore 6
      // 090: aload 6
      // 092: ifnonnull 098
      // 095: goto 133
      // 098: aload 6
      // 09a: arraylength
      // 09b: ifle 133
      // 09e: aload 6
      // 0a0: bipush 0
      // 0a1: baload
      // 0a2: bipush 8
      // 0a4: if_icmpne 133
      // 0a7: aload 0
      // 0a8: aload 0
      // 0a9: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 0ac: bipush 1
      // 0ad: isub
      // 0ae: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 0b1: aload 0
      // 0b2: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 0b5: ifle 133
      // 0b8: aload 4
      // 0ba: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 0bd: astore 7
      // 0bf: aload 4
      // 0c1: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 0c4: istore 8
      // 0c6: aload 7
      // 0c8: iload 8
      // 0ca: baload
      // 0cb: istore 9
      // 0cd: aload 0
      // 0ce: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 0d1: ifle 111
      // 0d4: iload 9
      // 0d6: sipush 128
      // 0d9: iand
      // 0da: sipush 128
      // 0dd: if_icmpne 111
      // 0e0: iload 9
      // 0e2: sipush -129
      // 0e5: iand
      // 0e6: i2b
      // 0e7: istore 9
      // 0e9: aload 7
      // 0eb: iload 8
      // 0ed: aload 0
      // 0ee: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 0f1: bipush 1
      // 0f2: iadd
      // 0f3: invokestatic net/rim/device/internal/i18n/UnicodeServiceUtilities.detectFutureData ([BII)I
      // 0f6: istore 10
      // 0f8: iload 10
      // 0fa: ifle 111
      // 0fd: iload 10
      // 0ff: aload 0
      // 100: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 103: if_icmpgt 111
      // 106: aload 0
      // 107: aload 0
      // 108: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 10b: iload 10
      // 10d: isub
      // 10e: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._bodyLength I
      // 111: iload 9
      // 113: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.getEncoding (B)Ljava/lang/String;
      // 116: astore 10
      // 118: aload 10
      // 11a: ifnull 133
      // 11d: aload 10
      // 11f: invokevirtual java/lang/String.length ()I
      // 122: ifle 133
      // 125: aload 10
      // 127: invokestatic com/sun/cldc/i18n/Helper.isSupportedEncoding (Ljava/lang/String;)Z
      // 12a: ifeq 133
      // 12d: aload 0
      // 12e: iload 9
      // 130: putfield net/rim/device/apps/api/transmission/rim/RIMMessagingMoreMessage._encodingCode B
      // 133: aload 4
      // 135: iload 5
      // 137: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 13a: return
      // 13b: astore 6
      // 13d: aload 4
      // 13f: iload 5
      // 141: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 144: return
      // 145: astore 11
      // 147: aload 4
      // 149: iload 5
      // 14b: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 14e: aload 11
      // 150: athrow
      // 151: return
      // try (48 -> 148): 152 null
      // try (48 -> 148): 157 null
      // try (152 -> 153): 157 null
      // try (157 -> 158): 157 null
   }

   public final Parameters getParameters() {
      return this._parameters;
   }
}
