package net.rim.device.internal.io.file;

import javax.microedition.io.file.FileConnection;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;

final class ImageThumbnailProvider extends MetaDataProvider {
   private Bitmap _bmp = (Bitmap)(new Object(112, 84));
   private Bitmap _artBmp;
   private Graphics _offscreen = (Graphics)(new Object(this._bmp));
   private Graphics _artOffscreen;

   public ImageThumbnailProvider() {
   }

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
      // 000: aload 2
      // 001: ifnull 007
      // 004: goto 0df
      // 007: new java/lang/Object
      // 00a: dup
      // 00b: invokespecial java/lang/StringBuffer.<init> ()V
      // 00e: aload 1
      // 00f: invokeinterface javax/microedition/io/file/FileConnection.getPath ()Ljava/lang/String; 1
      // 014: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 017: aload 1
      // 018: invokeinterface javax/microedition/io/file/FileConnection.getName ()Ljava/lang/String; 1
      // 01d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 020: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 023: astore 8
      // 025: aload 8
      // 027: invokestatic net/rim/device/internal/io/file/FileUtilities.isRemovable (Ljava/lang/String;)Z
      // 02a: ifeq 048
      // 02d: aload 8
      // 02f: invokestatic net/rim/device/internal/io/file/FileUtilities.isEncrypted (Ljava/lang/String;)Z
      // 032: ifne 048
      // 035: aload 8
      // 037: aload 8
      // 039: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 03c: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage (Ljava/lang/String;Ljava/lang/String;)Lnet/rim/device/api/system/EncodedImage;
      // 03f: astore 2
      // 040: goto 0df
      // 043: astore 9
      // 045: goto 0df
      // 048: aconst_null
      // 049: astore 9
      // 04b: aload 1
      // 04c: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 051: lstore 10
      // 053: lload 10
      // 055: bipush 0
      // 056: i2l
      // 057: lcmp
      // 058: ifle 065
      // 05b: lload 10
      // 05d: ldc_w 2097152
      // 060: i2l
      // 061: lcmp
      // 062: ifle 07a
      // 065: aconst_null
      // 066: astore 12
      // 068: aload 9
      // 06a: ifnull 077
      // 06d: aload 9
      // 06f: invokevirtual java/io/DataInputStream.close ()V
      // 072: goto 077
      // 075: astore 13
      // 077: aload 12
      // 079: areturn
      // 07a: aload 1
      // 07b: invokeinterface javax/microedition/io/file/FileConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 080: astore 9
      // 082: aload 3
      // 083: arraylength
      // 084: i2l
      // 085: lload 10
      // 087: lcmp
      // 088: ifge 092
      // 08b: aload 3
      // 08c: lload 10
      // 08e: l2i
      // 08f: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 092: aload 9
      // 094: aload 3
      // 095: bipush 0
      // 096: lload 10
      // 098: l2i
      // 099: invokevirtual java/io/DataInputStream.readFully ([BII)V
      // 09c: aload 3
      // 09d: bipush 0
      // 09e: lload 10
      // 0a0: l2i
      // 0a1: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage ([BII)Lnet/rim/device/api/system/EncodedImage;
      // 0a4: astore 2
      // 0a5: aload 9
      // 0a7: ifnull 0df
      // 0aa: aload 9
      // 0ac: invokevirtual java/io/DataInputStream.close ()V
      // 0af: goto 0df
      // 0b2: astore 10
      // 0b4: goto 0df
      // 0b7: astore 10
      // 0b9: aload 9
      // 0bb: ifnull 0df
      // 0be: aload 9
      // 0c0: invokevirtual java/io/DataInputStream.close ()V
      // 0c3: goto 0df
      // 0c6: astore 10
      // 0c8: goto 0df
      // 0cb: astore 14
      // 0cd: aload 9
      // 0cf: ifnull 0dc
      // 0d2: aload 9
      // 0d4: invokevirtual java/io/DataInputStream.close ()V
      // 0d7: goto 0dc
      // 0da: astore 15
      // 0dc: aload 14
      // 0de: athrow
      // 0df: aload 2
      // 0e0: ifnonnull 0e6
      // 0e3: goto 1c4
      // 0e6: aload 0
      // 0e7: getfield net/rim/device/internal/io/file/ImageThumbnailProvider._offscreen Lnet/rim/device/api/ui/Graphics;
      // 0ea: invokevirtual net/rim/device/api/ui/Graphics.clear ()V
      // 0ed: bipush 0
      // 0ee: istore 8
      // 0f0: aload 2
      // 0f1: invokevirtual net/rim/device/api/system/EncodedImage.getWidth ()I
      // 0f4: invokestatic net/rim/device/api/math/Fixed32.toFP (I)I
      // 0f7: iload 4
      // 0f9: invokestatic net/rim/device/api/math/Fixed32.toFP (I)I
      // 0fc: invokestatic net/rim/device/api/math/Fixed32.div (II)I
      // 0ff: istore 9
      // 101: aload 2
      // 102: invokevirtual net/rim/device/api/system/EncodedImage.getHeight ()I
      // 105: invokestatic net/rim/device/api/math/Fixed32.toFP (I)I
      // 108: iload 5
      // 10a: invokestatic net/rim/device/api/math/Fixed32.toFP (I)I
      // 10d: invokestatic net/rim/device/api/math/Fixed32.div (II)I
      // 110: istore 10
      // 112: iload 9
      // 114: iload 10
      // 116: invokestatic java/lang/Math.max (II)I
      // 119: istore 11
      // 11b: aload 2
      // 11c: iload 11
      // 11e: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 121: aload 2
      // 122: iload 11
      // 124: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 127: aload 1
      // 128: ifnull 13f
      // 12b: aload 0
      // 12c: getfield net/rim/device/internal/io/file/ImageThumbnailProvider._offscreen Lnet/rim/device/api/ui/Graphics;
      // 12f: bipush 0
      // 130: bipush 0
      // 131: iload 4
      // 133: iload 5
      // 135: aload 2
      // 136: bipush 0
      // 137: bipush 0
      // 138: bipush 0
      // 139: invokevirtual net/rim/device/api/ui/Graphics.drawImage (IIIILnet/rim/device/api/system/EncodedImage;III)V
      // 13c: goto 17c
      // 13f: aload 0
      // 140: getfield net/rim/device/internal/io/file/ImageThumbnailProvider._artBmp Lnet/rim/device/api/system/Bitmap;
      // 143: ifnonnull 164
      // 146: aload 0
      // 147: new java/lang/Object
      // 14a: dup
      // 14b: iload 4
      // 14d: iload 5
      // 14f: invokespecial net/rim/device/api/system/Bitmap.<init> (II)V
      // 152: putfield net/rim/device/internal/io/file/ImageThumbnailProvider._artBmp Lnet/rim/device/api/system/Bitmap;
      // 155: aload 0
      // 156: new java/lang/Object
      // 159: dup
      // 15a: aload 0
      // 15b: getfield net/rim/device/internal/io/file/ImageThumbnailProvider._artBmp Lnet/rim/device/api/system/Bitmap;
      // 15e: invokespecial net/rim/device/api/ui/Graphics.<init> (Lnet/rim/device/api/system/Bitmap;)V
      // 161: putfield net/rim/device/internal/io/file/ImageThumbnailProvider._artOffscreen Lnet/rim/device/api/ui/Graphics;
      // 164: aload 0
      // 165: getfield net/rim/device/internal/io/file/ImageThumbnailProvider._artOffscreen Lnet/rim/device/api/ui/Graphics;
      // 168: invokevirtual net/rim/device/api/ui/Graphics.clear ()V
      // 16b: aload 0
      // 16c: getfield net/rim/device/internal/io/file/ImageThumbnailProvider._artOffscreen Lnet/rim/device/api/ui/Graphics;
      // 16f: bipush 0
      // 170: bipush 0
      // 171: iload 4
      // 173: iload 5
      // 175: aload 2
      // 176: bipush 0
      // 177: bipush 0
      // 178: bipush 0
      // 179: invokevirtual net/rim/device/api/ui/Graphics.drawImage (IIIILnet/rim/device/api/system/EncodedImage;III)V
      // 17c: aload 7
      // 17e: ifnonnull 188
      // 181: iload 6
      // 183: anewarray 334
      // 186: astore 7
      // 188: aload 1
      // 189: ifnull 1a4
      // 18c: aload 7
      // 18e: bipush 0
      // 18f: aload 0
      // 190: getfield net/rim/device/internal/io/file/ImageThumbnailProvider._bmp Lnet/rim/device/api/system/Bitmap;
      // 193: bipush 0
      // 194: bipush 0
      // 195: aload 2
      // 196: invokevirtual net/rim/device/api/system/EncodedImage.getScaledWidth ()I
      // 199: aload 2
      // 19a: invokevirtual net/rim/device/api/system/EncodedImage.getScaledHeight ()I
      // 19d: invokestatic net/rim/device/api/system/PNGEncodedImage.encode (Lnet/rim/device/api/system/Bitmap;IIII)Lnet/rim/device/api/system/PNGEncodedImage;
      // 1a0: aastore
      // 1a1: goto 1b1
      // 1a4: aload 7
      // 1a6: bipush 0
      // 1a7: aload 0
      // 1a8: getfield net/rim/device/internal/io/file/ImageThumbnailProvider._artBmp Lnet/rim/device/api/system/Bitmap;
      // 1ab: bipush 70
      // 1ad: invokestatic net/rim/device/api/system/JPEGEncodedImage.encode (Lnet/rim/device/api/system/Bitmap;I)Lnet/rim/device/api/system/JPEGEncodedImage;
      // 1b0: aastore
      // 1b1: aload 7
      // 1b3: areturn
      // 1b4: astore 9
      // 1b6: iload 8
      // 1b8: ifne 1c4
      // 1bb: bipush 1
      // 1bc: istore 8
      // 1be: invokestatic net/rim/vm/Memory.maximizeContiguousRAM ()V
      // 1c1: goto 0f0
      // 1c4: aload 7
      // 1c6: areturn
      // try (20 -> 25): 26 null
      // try (47 -> 49): 50 null
      // try (80 -> 82): 83 null
      // try (30 -> 45): 85 null
      // try (53 -> 78): 85 null
      // try (88 -> 90): 91 null
      // try (30 -> 45): 93 null
      // try (53 -> 78): 93 null
      // try (85 -> 86): 93 null
      // try (96 -> 98): 99 null
      // try (93 -> 94): 93 null
      // try (110 -> 207): 208 null
   }
}
