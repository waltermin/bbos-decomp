package net.rim.device.apps.internal.mms;

import java.util.Enumeration;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;
import net.rim.device.internal.io.file.MetaDataProvider;

final class MMSThumbnailProvider extends MetaDataProvider {
   @Override
   public final Object[] getMetaData(FileConnection param1, EncodedImage param2, byte[] param3, int param4, int param5, int param6, Object[] param7) {
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
      // 000: aconst_null
      // 001: astore 8
      // 003: aload 1
      // 004: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 009: lstore 9
      // 00b: lload 9
      // 00d: bipush 0
      // 00e: i2l
      // 00f: lcmp
      // 010: iflt 01d
      // 013: lload 9
      // 015: ldc_w 2097152
      // 018: i2l
      // 019: lcmp
      // 01a: ifle 032
      // 01d: aconst_null
      // 01e: astore 11
      // 020: aload 8
      // 022: ifnull 02f
      // 025: aload 8
      // 027: invokevirtual java/io/DataInputStream.close ()V
      // 02a: goto 02f
      // 02d: astore 12
      // 02f: aload 11
      // 031: areturn
      // 032: aload 1
      // 033: invokeinterface javax/microedition/io/file/FileConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 038: astore 8
      // 03a: aload 3
      // 03b: arraylength
      // 03c: i2l
      // 03d: lload 9
      // 03f: lcmp
      // 040: ifge 04a
      // 043: aload 3
      // 044: lload 9
      // 046: l2i
      // 047: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 04a: aload 8
      // 04c: aload 3
      // 04d: bipush 0
      // 04e: lload 9
      // 050: l2i
      // 051: invokevirtual java/io/DataInputStream.readFully ([BII)V
      // 054: goto 06e
      // 057: astore 11
      // 059: aconst_null
      // 05a: astore 12
      // 05c: aload 8
      // 05e: ifnull 06b
      // 061: aload 8
      // 063: invokevirtual java/io/DataInputStream.close ()V
      // 066: goto 06b
      // 069: astore 13
      // 06b: aload 12
      // 06d: areturn
      // 06e: aload 0
      // 06f: new net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit
      // 072: dup
      // 073: aload 3
      // 074: bipush 0
      // 075: lload 9
      // 077: l2i
      // 078: invokespecial net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit.<init> ([BII)V
      // 07b: invokespecial net/rim/device/apps/internal/mms/MMSThumbnailProvider.findLargestEncodedImage (Lnet/rim/device/apps/internal/mms/service/MMSProtocolDataUnit;)Lnet/rim/device/api/system/EncodedImage;
      // 07e: astore 11
      // 080: aload 11
      // 082: ifnonnull 088
      // 085: goto 113
      // 088: aload 11
      // 08a: invokevirtual net/rim/device/api/system/EncodedImage.getWidth ()I
      // 08d: invokestatic net/rim/device/api/math/Fixed32.toFP (I)I
      // 090: iload 4
      // 092: invokestatic net/rim/device/api/math/Fixed32.toFP (I)I
      // 095: invokestatic net/rim/device/api/math/Fixed32.div (II)I
      // 098: istore 12
      // 09a: aload 11
      // 09c: invokevirtual net/rim/device/api/system/EncodedImage.getHeight ()I
      // 09f: invokestatic net/rim/device/api/math/Fixed32.toFP (I)I
      // 0a2: iload 5
      // 0a4: invokestatic net/rim/device/api/math/Fixed32.toFP (I)I
      // 0a7: invokestatic net/rim/device/api/math/Fixed32.div (II)I
      // 0aa: istore 13
      // 0ac: iload 12
      // 0ae: iload 13
      // 0b0: invokestatic java/lang/Math.max (II)I
      // 0b3: istore 14
      // 0b5: aload 11
      // 0b7: iload 14
      // 0b9: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 0bc: aload 11
      // 0be: iload 14
      // 0c0: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 0c3: new net/rim/device/api/system/Bitmap
      // 0c6: dup
      // 0c7: iload 4
      // 0c9: iload 5
      // 0cb: invokespecial net/rim/device/api/system/Bitmap.<init> (II)V
      // 0ce: astore 15
      // 0d0: new net/rim/device/api/ui/Graphics
      // 0d3: dup
      // 0d4: aload 15
      // 0d6: invokespecial net/rim/device/api/ui/Graphics.<init> (Lnet/rim/device/api/system/Bitmap;)V
      // 0d9: astore 16
      // 0db: aload 16
      // 0dd: bipush 0
      // 0de: bipush 0
      // 0df: iload 4
      // 0e1: iload 5
      // 0e3: aload 11
      // 0e5: bipush 0
      // 0e6: bipush 0
      // 0e7: bipush 0
      // 0e8: invokevirtual net/rim/device/api/ui/Graphics.drawImage (IIIILnet/rim/device/api/system/EncodedImage;III)V
      // 0eb: iload 6
      // 0ed: anewarray 159
      // 0f0: astore 17
      // 0f2: aload 17
      // 0f4: bipush 0
      // 0f5: aload 15
      // 0f7: bipush 75
      // 0f9: invokestatic net/rim/device/api/system/JPEGEncodedImage.encode (Lnet/rim/device/api/system/Bitmap;I)Lnet/rim/device/api/system/JPEGEncodedImage;
      // 0fc: aastore
      // 0fd: aload 17
      // 0ff: astore 18
      // 101: aload 8
      // 103: ifnull 110
      // 106: aload 8
      // 108: invokevirtual java/io/DataInputStream.close ()V
      // 10b: goto 110
      // 10e: astore 19
      // 110: aload 18
      // 112: areturn
      // 113: aload 8
      // 115: ifnull 149
      // 118: aload 8
      // 11a: invokevirtual java/io/DataInputStream.close ()V
      // 11d: aconst_null
      // 11e: areturn
      // 11f: astore 9
      // 121: aconst_null
      // 122: areturn
      // 123: astore 9
      // 125: aload 8
      // 127: ifnull 149
      // 12a: aload 8
      // 12c: invokevirtual java/io/DataInputStream.close ()V
      // 12f: aconst_null
      // 130: areturn
      // 131: astore 9
      // 133: aconst_null
      // 134: areturn
      // 135: astore 20
      // 137: aload 8
      // 139: ifnull 146
      // 13c: aload 8
      // 13e: invokevirtual java/io/DataInputStream.close ()V
      // 141: goto 146
      // 144: astore 21
      // 146: aload 20
      // 148: athrow
      // 149: aconst_null
      // 14a: areturn
      // try (19 -> 21): 22 null
      // try (38 -> 44): 45 null
      // try (50 -> 52): 53 null
      // try (127 -> 129): 130 null
      // try (135 -> 137): 139 null
      // try (2 -> 17): 142 null
      // try (25 -> 48): 142 null
      // try (56 -> 125): 142 null
      // try (145 -> 147): 149 null
      // try (2 -> 17): 152 null
      // try (25 -> 48): 152 null
      // try (56 -> 125): 152 null
      // try (142 -> 143): 152 null
      // try (155 -> 157): 158 null
      // try (152 -> 153): 152 null
   }

   private final EncodedImage findLargestEncodedImage(MMSProtocolDataUnit pdu) {
      EncodedImage maxImage = null;
      Enumeration names = pdu.attachmentNames();
      if (names != null) {
         int maxArea = 0;

         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            MMSAttachment attachment = pdu.getAttachment(name);
            if (attachment != null && MMSUtilities.isImageType(attachment.getType())) {
               byte[] data = attachment.getData();
               EncodedImage image = EncodedImage.createEncodedImage(data, 0, data.length);
               int area = image.getHeight() * image.getWidth();
               if (area >= maxArea) {
                  maxImage = image;
                  maxArea = area;
               }
            }
         }
      }

      return maxImage;
   }
}
