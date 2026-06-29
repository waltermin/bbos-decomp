package net.rim.device.internal.io.file;

import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Player;
import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.media.MetaDataObject;
import net.rim.device.api.media.control.BinaryMetaDataControl;
import net.rim.device.api.system.EncodedImage;

final class MediaMetaDataProvider extends MetaDataProvider {
   public MediaMetaDataProvider() {
   }

   @Override
   public final Object[] getMetaData(FileConnection param1, EncodedImage param2, byte[] param3, int param4, int param5, int param6, Object[] param7) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 8
      // 003: aload 1
      // 004: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 009: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 00c: astore 8
      // 00e: aload 8
      // 010: invokeinterface javax/microedition/media/Player.realize ()V 1
      // 015: bipush 0
      // 016: istore 9
      // 018: aload 8
      // 01a: ldc_w "javax.microedition.media.control.MetaDataControl"
      // 01d: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 022: checkcast java/lang/Object
      // 025: astore 10
      // 027: aload 7
      // 029: ifnonnull 033
      // 02c: iload 6
      // 02e: anewarray 49
      // 031: astore 7
      // 033: aload 8
      // 035: invokeinterface javax/microedition/media/Player.getDuration ()J 1
      // 03a: lstore 11
      // 03c: lload 11
      // 03e: bipush -1
      // 040: i2l
      // 041: lcmp
      // 042: ifeq 056
      // 045: bipush 1
      // 046: istore 9
      // 048: aload 7
      // 04a: bipush 2
      // 04c: new java/lang/Object
      // 04f: dup
      // 050: lload 11
      // 052: invokespecial java/lang/Long.<init> (J)V
      // 055: aastore
      // 056: aload 10
      // 058: ifnull 0b4
      // 05b: iload 9
      // 05d: aload 0
      // 05e: aload 10
      // 060: ldc_w "author"
      // 063: bipush 3
      // 065: aload 7
      // 067: invokespecial net/rim/device/internal/io/file/MediaMetaDataProvider.queryMetaData (Ljavax/microedition/media/control/MetaDataControl;Ljava/lang/String;I[Ljava/lang/Object;)Z
      // 06a: ior
      // 06b: istore 9
      // 06d: iload 9
      // 06f: aload 0
      // 070: aload 10
      // 072: ldc_w "title"
      // 075: bipush 1
      // 076: aload 7
      // 078: invokespecial net/rim/device/internal/io/file/MediaMetaDataProvider.queryMetaData (Ljavax/microedition/media/control/MetaDataControl;Ljava/lang/String;I[Ljava/lang/Object;)Z
      // 07b: ior
      // 07c: istore 9
      // 07e: iload 9
      // 080: aload 0
      // 081: aload 10
      // 083: ldc_w "album"
      // 086: bipush 4
      // 088: aload 7
      // 08a: invokespecial net/rim/device/internal/io/file/MediaMetaDataProvider.queryMetaData (Ljavax/microedition/media/control/MetaDataControl;Ljava/lang/String;I[Ljava/lang/Object;)Z
      // 08d: ior
      // 08e: istore 9
      // 090: iload 9
      // 092: aload 0
      // 093: aload 10
      // 095: ldc_w "genre"
      // 098: bipush 5
      // 09a: aload 7
      // 09c: invokespecial net/rim/device/internal/io/file/MediaMetaDataProvider.queryMetaData (Ljavax/microedition/media/control/MetaDataControl;Ljava/lang/String;I[Ljava/lang/Object;)Z
      // 09f: ior
      // 0a0: istore 9
      // 0a2: iload 9
      // 0a4: aload 0
      // 0a5: aload 10
      // 0a7: ldc_w "track number"
      // 0aa: bipush 6
      // 0ac: aload 7
      // 0ae: invokespecial net/rim/device/internal/io/file/MediaMetaDataProvider.queryMetaData (Ljavax/microedition/media/control/MetaDataControl;Ljava/lang/String;I[Ljava/lang/Object;)Z
      // 0b1: ior
      // 0b2: istore 9
      // 0b4: iload 9
      // 0b6: ifeq 0f9
      // 0b9: aload 0
      // 0ba: aload 8
      // 0bc: invokespecial net/rim/device/internal/io/file/MediaMetaDataProvider.extractAlbumArt (Ljavax/microedition/media/Player;)Lnet/rim/device/api/system/EncodedImage;
      // 0bf: astore 13
      // 0c1: aload 13
      // 0c3: ifnull 0e6
      // 0c6: ldc_w "image/jpeg"
      // 0c9: invokestatic net/rim/device/internal/io/file/MetaDataProvider.getProvider (Ljava/lang/String;)Lnet/rim/device/internal/io/file/MetaDataProvider;
      // 0cc: astore 14
      // 0ce: aload 14
      // 0d0: ifnull 0e6
      // 0d3: aload 14
      // 0d5: aconst_null
      // 0d6: aload 13
      // 0d8: aload 3
      // 0d9: bipush 35
      // 0db: bipush 35
      // 0dd: iload 6
      // 0df: aload 7
      // 0e1: invokevirtual net/rim/device/internal/io/file/MetaDataProvider.getMetaData (Ljavax/microedition/io/file/FileConnection;Lnet/rim/device/api/system/EncodedImage;[BIII[Ljava/lang/Object;)[Ljava/lang/Object;
      // 0e4: astore 7
      // 0e6: aload 7
      // 0e8: astore 14
      // 0ea: aload 8
      // 0ec: ifnull 0f6
      // 0ef: aload 8
      // 0f1: invokeinterface javax/microedition/media/Player.close ()V 1
      // 0f6: aload 14
      // 0f8: areturn
      // 0f9: aload 8
      // 0fb: ifnull 138
      // 0fe: aload 8
      // 100: invokeinterface javax/microedition/media/Player.close ()V 1
      // 105: aconst_null
      // 106: areturn
      // 107: astore 9
      // 109: aload 8
      // 10b: ifnull 138
      // 10e: aload 8
      // 110: invokeinterface javax/microedition/media/Player.close ()V 1
      // 115: aconst_null
      // 116: areturn
      // 117: astore 9
      // 119: aload 8
      // 11b: ifnull 138
      // 11e: aload 8
      // 120: invokeinterface javax/microedition/media/Player.close ()V 1
      // 125: aconst_null
      // 126: areturn
      // 127: astore 15
      // 129: aload 8
      // 12b: ifnull 135
      // 12e: aload 8
      // 130: invokeinterface javax/microedition/media/Player.close ()V 1
      // 135: aload 15
      // 137: athrow
      // 138: aconst_null
      // 139: areturn
      // try (2 -> 109): 121 null
      // try (2 -> 109): 128 null
      // try (2 -> 109): 135 null
      // try (121 -> 122): 135 null
      // try (128 -> 129): 135 null
      // try (135 -> 136): 135 null
   }

   private final boolean queryMetaData(MetaDataControl metaData, String keyName, int keyIndex, Object[] result) {
      try {
         String value = metaData.getKeyValue(keyName);
         if (value != null) {
            result[keyIndex] = value;
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   private final EncodedImage extractAlbumArt(Player player) {
      BinaryMetaDataControl binaryMetaData = null;
      binaryMetaData = (BinaryMetaDataControl)player.getControl("net.rim.device.api.media.control.BinaryMetaDataControl");
      if (binaryMetaData != null) {
         MetaDataObject[] metaDataObjects = binaryMetaData.getMetaDataObjects();

         for (int i = 0; i < metaDataObjects.length; i++) {
            if (metaDataObjects[i] != null && metaDataObjects[i].getPictureType() != -1) {
               byte[] data = metaDataObjects[i].getData();
               if (data != null) {
                  try {
                     EncodedImage albumArt = EncodedImage.createEncodedImage(data, 0, data.length);
                     if (albumArt != null) {
                        int imageWidth = albumArt.getWidth();
                        int imageHeight = albumArt.getHeight();
                        if (imageWidth >= 5 && imageHeight >= 5) {
                           if (albumArt != null) {
                              return albumArt;
                           }
                        } else {
                           EncodedImage var12 = null;
                        }
                     }
                  } finally {
                     continue;
                  }
               }
            }
         }
      }

      return null;
   }
}
