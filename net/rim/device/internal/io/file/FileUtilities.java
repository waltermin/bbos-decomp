package net.rim.device.internal.io.file;

import java.io.InputStream;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.file.PosixFileConnection;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.cldc.io.utility.URIEncoder;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.FilenameValidator;

public final class FileUtilities {
   public static String ENCRYPTED_MEDIA_EXTENSION = PosixFileConnection.ENCRYPTED_MEDIA_EXTENSION;
   private static final char DIRECTORY_NAME_SEPERATOR = '/';
   private static String FILE_COLON_SLASH_SLASH = "file://";
   private static String FILE_SYSTEM_STORE_ROOT_STR = "/store/";
   private static String USER_STORE_ROOT_STR = "/store/home/user/";
   private static String FILE_SYSTEM_SDCARD_ROOT_STR = "/SDCard/";
   private static final long[] MULTIPLIER = new long[]{
      1024L,
      1048576L,
      1073741824L,
      7784890369L,
      12079857665L,
      29259726849L,
      -3455386809805045760L,
      -3054176912083292366L,
      20621692766257203L,
      23734410189882194L,
      -3455386809805045760L,
      -2918606221006897090L,
      -3455386809805045760L,
      -3297167379286550693L,
      -3455386809805045760L,
      6063360555319689575L,
      -3455386805510078464L,
      7207871974803693937L,
      3596208183088439728L,
      -3455386801215111168L,
      -7464003439710973532L,
      -8040378802380461050L,
      -1438311245835636745L,
      -3455386809805045760L
   };
   private static final int RINGTONE_SIZE_THRESHOLD = 614400;
   private static ResourceBundle _fileSystemRb = ResourceBundle.getBundle(-538091128501376688L, "net.rim.device.internal.resource.FileSystem");

   private FileUtilities() {
   }

   public static final boolean ensureDirectoryExists(String param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 1
      // 02: aload 0
      // 03: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 06: checkcast java/lang/Object
      // 09: astore 1
      // 0a: aload 1
      // 0b: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 10: ifne 6e
      // 13: aload 1
      // 14: invokeinterface javax/microedition/io/file/FileConnection.getPath ()Ljava/lang/String; 1
      // 19: astore 2
      // 1a: aload 2
      // 1b: bipush 47
      // 1d: invokevirtual java/lang/String.lastIndexOf (I)I
      // 20: aload 2
      // 21: bipush 47
      // 23: bipush 1
      // 24: invokevirtual java/lang/String.indexOf (II)I
      // 27: if_icmpeq 56
      // 2a: new java/lang/Object
      // 2d: dup
      // 2e: invokespecial java/lang/StringBuffer.<init> ()V
      // 31: getstatic net/rim/device/internal/io/file/FileUtilities.FILE_COLON_SLASH_SLASH Ljava/lang/String;
      // 34: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 37: aload 2
      // 38: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3b: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 3e: invokestatic net/rim/device/internal/io/file/FileUtilities.ensureDirectoryExists (Ljava/lang/String;)Z
      // 41: ifne 56
      // 44: bipush 0
      // 45: istore 3
      // 46: aload 1
      // 47: ifnull 54
      // 4a: aload 1
      // 4b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 50: iload 3
      // 51: ireturn
      // 52: astore 4
      // 54: iload 3
      // 55: ireturn
      // 56: aload 1
      // 57: invokeinterface javax/microedition/io/file/FileConnection.mkdir ()V 1
      // 5c: bipush 1
      // 5d: istore 3
      // 5e: aload 1
      // 5f: ifnull 6c
      // 62: aload 1
      // 63: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 68: iload 3
      // 69: ireturn
      // 6a: astore 4
      // 6c: iload 3
      // 6d: ireturn
      // 6e: bipush 1
      // 6f: istore 2
      // 70: aload 1
      // 71: ifnull 7d
      // 74: aload 1
      // 75: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 7a: iload 2
      // 7b: ireturn
      // 7c: astore 3
      // 7d: iload 2
      // 7e: ireturn
      // 7f: astore 2
      // 80: aload 1
      // 81: ifnull b3
      // 84: aload 1
      // 85: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 8a: bipush 0
      // 8b: ireturn
      // 8c: astore 2
      // 8d: bipush 0
      // 8e: ireturn
      // 8f: astore 2
      // 90: aload 1
      // 91: ifnull b3
      // 94: aload 1
      // 95: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 9a: bipush 0
      // 9b: ireturn
      // 9c: astore 2
      // 9d: bipush 0
      // 9e: ireturn
      // 9f: astore 5
      // a1: aload 1
      // a2: ifnull b0
      // a5: aload 1
      // a6: invokeinterface javax/microedition/io/Connection.close ()V 1
      // ab: goto b0
      // ae: astore 6
      // b0: aload 5
      // b2: athrow
      // b3: bipush 0
      // b4: ireturn
      // try (34 -> 36): 38 null
      // try (47 -> 49): 51 null
      // try (58 -> 60): 62 null
      // try (2 -> 32): 65 null
      // try (41 -> 45): 65 null
      // try (54 -> 56): 65 null
      // try (68 -> 70): 72 null
      // try (2 -> 32): 75 null
      // try (41 -> 45): 75 null
      // try (54 -> 56): 75 null
      // try (78 -> 80): 82 null
      // try (2 -> 32): 85 null
      // try (41 -> 45): 85 null
      // try (54 -> 56): 85 null
      // try (65 -> 66): 85 null
      // try (75 -> 76): 85 null
      // try (88 -> 90): 91 null
      // try (85 -> 86): 85 null
   }

   public static final boolean checkFileExists(String param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 1
      // 02: aload 0
      // 03: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 06: checkcast java/lang/Object
      // 09: astore 1
      // 0a: aload 1
      // 0b: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 10: istore 2
      // 11: aload 1
      // 12: ifnull 1e
      // 15: aload 1
      // 16: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1b: iload 2
      // 1c: ireturn
      // 1d: astore 3
      // 1e: iload 2
      // 1f: ireturn
      // 20: astore 2
      // 21: aload 1
      // 22: ifnull 54
      // 25: aload 1
      // 26: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2b: bipush 0
      // 2c: ireturn
      // 2d: astore 2
      // 2e: bipush 0
      // 2f: ireturn
      // 30: astore 2
      // 31: aload 1
      // 32: ifnull 54
      // 35: aload 1
      // 36: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3b: bipush 0
      // 3c: ireturn
      // 3d: astore 2
      // 3e: bipush 0
      // 3f: ireturn
      // 40: astore 4
      // 42: aload 1
      // 43: ifnull 51
      // 46: aload 1
      // 47: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4c: goto 51
      // 4f: astore 5
      // 51: aload 4
      // 53: athrow
      // 54: bipush 0
      // 55: ireturn
      // try (11 -> 13): 15 null
      // try (2 -> 9): 18 null
      // try (21 -> 23): 25 null
      // try (2 -> 9): 28 null
      // try (31 -> 33): 35 null
      // try (2 -> 9): 38 null
      // try (18 -> 19): 38 null
      // try (28 -> 29): 38 null
      // try (41 -> 43): 44 null
      // try (38 -> 39): 38 null
   }

   public static final void moveFile(String srcFilename, String dstFilename) {
      copyFile(srcFilename, dstFilename, true, null, false);
   }

   public static final void moveFile(String srcFilename, String dstFilename, CopyProgressCallback progress, boolean force) {
      copyFile(srcFilename, dstFilename, true, progress, force);
   }

   public static final void copyFile(String srcFilename, String dstFilename) {
      copyFile(srcFilename, dstFilename, false, null, false);
   }

   public static final void copyFile(String srcFilename, String dstFilename, CopyProgressCallback progress, boolean force) {
      copyFile(srcFilename, dstFilename, false, progress, force);
   }

   private static final void copyFile(String param0, String param1, boolean param2, CopyProgressCallback param3, boolean param4) {
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
      // 001: ifnull 00f
      // 004: aload 1
      // 005: ifnull 00f
      // 008: aload 0
      // 009: invokestatic net/rim/device/internal/io/file/FileUtilities.isDirectory (Ljava/lang/String;)Z
      // 00c: ifeq 017
      // 00f: new java/lang/Object
      // 012: dup
      // 013: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 016: athrow
      // 017: aload 1
      // 018: invokestatic net/rim/device/internal/io/file/FileUtilities.isDirectory (Ljava/lang/String;)Z
      // 01b: ifeq 034
      // 01e: new java/lang/Object
      // 021: dup
      // 022: invokespecial java/lang/StringBuffer.<init> ()V
      // 025: aload 1
      // 026: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 029: aload 0
      // 02a: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 02d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 030: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 033: astore 1
      // 034: aload 1
      // 035: invokestatic net/rim/device/internal/io/file/FileUtilities.getFileNameAndStripEncryptionExt (Ljava/lang/String;)Ljava/lang/String;
      // 038: astore 1
      // 039: aconst_null
      // 03a: astore 5
      // 03c: aconst_null
      // 03d: astore 6
      // 03f: aconst_null
      // 040: astore 7
      // 042: aconst_null
      // 043: astore 8
      // 045: bipush 0
      // 046: istore 9
      // 048: bipush 0
      // 049: istore 10
      // 04b: bipush 0
      // 04c: istore 11
      // 04e: aload 0
      // 04f: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 052: checkcast java/lang/Object
      // 055: astore 5
      // 057: iload 4
      // 059: ifeq 071
      // 05c: aload 5
      // 05e: invokeinterface javax/microedition/io/file/FileConnection.canWrite ()Z 1
      // 063: ifne 071
      // 066: aload 5
      // 068: bipush 1
      // 069: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 06e: bipush 1
      // 06f: istore 11
      // 071: aload 1
      // 072: invokestatic net/rim/device/internal/io/file/FileUtilities.getRoot (Ljava/lang/String;)Ljava/lang/String;
      // 075: astore 12
      // 077: aload 1
      // 078: getstatic net/rim/device/internal/io/file/FileUtilities.FILE_COLON_SLASH_SLASH Ljava/lang/String;
      // 07b: invokevirtual java/lang/String.length ()I
      // 07e: aload 12
      // 080: invokevirtual java/lang/String.length ()I
      // 083: iadd
      // 084: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 087: astore 13
      // 089: aload 13
      // 08b: invokestatic net/rim/device/internal/io/file/FileSystemOptions.isExternalEncryptionRequired (Ljava/lang/String;)Z
      // 08e: aload 0
      // 08f: getstatic net/rim/device/internal/io/file/FileUtilities.ENCRYPTED_MEDIA_EXTENSION Ljava/lang/String;
      // 092: ldc_w 1701707776
      // 095: invokestatic net/rim/device/api/util/StringUtilities.endsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 098: if_icmpeq 09e
      // 09b: goto 215
      // 09e: iload 2
      // 09f: ifne 0a5
      // 0a2: goto 168
      // 0a5: aload 5
      // 0a7: instanceof java/lang/Object
      // 0aa: ifne 0b0
      // 0ad: goto 168
      // 0b0: aload 12
      // 0b2: aload 0
      // 0b3: invokestatic net/rim/device/internal/io/file/FileUtilities.getRoot (Ljava/lang/String;)Ljava/lang/String;
      // 0b6: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0b9: ifne 0bf
      // 0bc: goto 168
      // 0bf: aload 5
      // 0c1: checkcast java/lang/Object
      // 0c4: astore 14
      // 0c6: aload 14
      // 0c8: invokeinterface net/rim/device/internal/io/file/BaseFileConnection.getSupportFlags ()I 1
      // 0cd: bipush 1
      // 0ce: iand
      // 0cf: ifne 0d5
      // 0d2: goto 168
      // 0d5: aload 14
      // 0d7: aload 13
      // 0d9: invokeinterface net/rim/device/internal/io/file/BaseFileConnection.renameEx (Ljava/lang/String;)V 2
      // 0de: aload 7
      // 0e0: ifnull 0ed
      // 0e3: aload 7
      // 0e5: invokevirtual java/io/InputStream.close ()V
      // 0e8: goto 0ed
      // 0eb: astore 22
      // 0ed: aload 8
      // 0ef: ifnull 0fc
      // 0f2: aload 8
      // 0f4: invokevirtual java/io/OutputStream.close ()V
      // 0f7: goto 0fc
      // 0fa: astore 22
      // 0fc: iload 9
      // 0fe: ifne 117
      // 101: iload 10
      // 103: ifeq 117
      // 106: aload 6
      // 108: ifnull 117
      // 10b: aload 6
      // 10d: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 112: goto 117
      // 115: astore 22
      // 117: aload 5
      // 119: ifnull 13a
      // 11c: iload 11
      // 11e: ifeq 12e
      // 121: aload 5
      // 123: bipush 0
      // 124: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 129: goto 12e
      // 12c: astore 22
      // 12e: aload 5
      // 130: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 135: goto 13a
      // 138: astore 22
      // 13a: aload 6
      // 13c: ifnull 15d
      // 13f: iload 11
      // 141: ifeq 151
      // 144: aload 6
      // 146: bipush 0
      // 147: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 14c: goto 151
      // 14f: astore 22
      // 151: aload 6
      // 153: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 158: goto 15d
      // 15b: astore 22
      // 15d: aload 3
      // 15e: ifnull 167
      // 161: aload 3
      // 162: invokeinterface net/rim/device/internal/io/file/CopyProgressCallback.copyCompleted ()V 1
      // 167: return
      // 168: iload 2
      // 169: ifne 16f
      // 16c: goto 215
      // 16f: aload 1
      // 170: invokestatic net/rim/device/internal/io/file/FileUtilities.getPath (Ljava/lang/String;)Ljava/lang/String;
      // 173: aload 0
      // 174: invokestatic net/rim/device/internal/io/file/FileUtilities.getPath (Ljava/lang/String;)Ljava/lang/String;
      // 177: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 17a: ifne 180
      // 17d: goto 215
      // 180: aload 5
      // 182: aload 1
      // 183: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 186: invokeinterface javax/microedition/io/file/FileConnection.rename (Ljava/lang/String;)V 2
      // 18b: aload 7
      // 18d: ifnull 19a
      // 190: aload 7
      // 192: invokevirtual java/io/InputStream.close ()V
      // 195: goto 19a
      // 198: astore 22
      // 19a: aload 8
      // 19c: ifnull 1a9
      // 19f: aload 8
      // 1a1: invokevirtual java/io/OutputStream.close ()V
      // 1a4: goto 1a9
      // 1a7: astore 22
      // 1a9: iload 9
      // 1ab: ifne 1c4
      // 1ae: iload 10
      // 1b0: ifeq 1c4
      // 1b3: aload 6
      // 1b5: ifnull 1c4
      // 1b8: aload 6
      // 1ba: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 1bf: goto 1c4
      // 1c2: astore 22
      // 1c4: aload 5
      // 1c6: ifnull 1e7
      // 1c9: iload 11
      // 1cb: ifeq 1db
      // 1ce: aload 5
      // 1d0: bipush 0
      // 1d1: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 1d6: goto 1db
      // 1d9: astore 22
      // 1db: aload 5
      // 1dd: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1e2: goto 1e7
      // 1e5: astore 22
      // 1e7: aload 6
      // 1e9: ifnull 20a
      // 1ec: iload 11
      // 1ee: ifeq 1fe
      // 1f1: aload 6
      // 1f3: bipush 0
      // 1f4: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 1f9: goto 1fe
      // 1fc: astore 22
      // 1fe: aload 6
      // 200: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 205: goto 20a
      // 208: astore 22
      // 20a: aload 3
      // 20b: ifnull 214
      // 20e: aload 3
      // 20f: invokeinterface net/rim/device/internal/io/file/CopyProgressCallback.copyCompleted ()V 1
      // 214: return
      // 215: aload 1
      // 216: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 219: checkcast java/lang/Object
      // 21c: astore 6
      // 21e: aload 5
      // 220: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 225: lstore 14
      // 227: lload 14
      // 229: aload 6
      // 22b: invokestatic net/rim/device/internal/io/file/FileUtilities.checkSpaceAvailable (JLjavax/microedition/io/file/FileConnection;)V
      // 22e: aload 5
      // 230: instanceof java/lang/Object
      // 233: ifeq 26c
      // 236: aload 6
      // 238: instanceof java/lang/Object
      // 23b: ifeq 26c
      // 23e: aload 5
      // 240: checkcast java/lang/Object
      // 243: astore 16
      // 245: aload 6
      // 247: checkcast java/lang/Object
      // 24a: astore 17
      // 24c: aload 16
      // 24e: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentDRMForwardLocked ()Z 1
      // 253: ifeq 25d
      // 256: aload 17
      // 258: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.enableDRMForwardLock ()V 1
      // 25d: aload 17
      // 25f: aload 16
      // 261: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.getControlledAccess ()Lnet/rim/device/api/system/CodeSigningKey; 1
      // 266: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.setControlledAccess (Lnet/rim/device/api/system/CodeSigningKey;)Z 2
      // 26b: pop
      // 26c: aload 6
      // 26e: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 273: bipush 1
      // 274: istore 10
      // 276: aload 5
      // 278: invokeinterface javax/microedition/io/file/FileConnection.isHidden ()Z 1
      // 27d: ifeq 288
      // 280: aload 6
      // 282: bipush 1
      // 283: invokeinterface javax/microedition/io/file/FileConnection.setHidden (Z)V 2
      // 288: aload 5
      // 28a: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 28f: astore 7
      // 291: aload 6
      // 293: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 298: astore 8
      // 29a: aload 3
      // 29b: ifnull 2a4
      // 29e: aload 3
      // 29f: invokeinterface net/rim/device/internal/io/file/CopyProgressCallback.copyStarted ()V 1
      // 2a4: bipush 0
      // 2a5: i2l
      // 2a6: lstore 16
      // 2a8: sipush 16384
      // 2ab: newarray 8
      // 2ad: astore 18
      // 2af: aload 7
      // 2b1: aload 18
      // 2b3: invokevirtual java/io/InputStream.read ([B)I
      // 2b6: istore 19
      // 2b8: iload 19
      // 2ba: bipush -1
      // 2bc: if_icmpne 2c2
      // 2bf: goto 2e5
      // 2c2: aload 8
      // 2c4: aload 18
      // 2c6: bipush 0
      // 2c7: iload 19
      // 2c9: invokevirtual java/io/OutputStream.write ([BII)V
      // 2cc: lload 16
      // 2ce: iload 19
      // 2d0: i2l
      // 2d1: ladd
      // 2d2: lstore 16
      // 2d4: aload 3
      // 2d5: ifnull 2af
      // 2d8: aload 3
      // 2d9: lload 16
      // 2db: lload 14
      // 2dd: invokeinterface net/rim/device/internal/io/file/CopyProgressCallback.segmentCopied (JJ)V 5
      // 2e2: goto 2af
      // 2e5: aload 7
      // 2e7: invokevirtual java/io/InputStream.close ()V
      // 2ea: aconst_null
      // 2eb: astore 7
      // 2ed: aload 8
      // 2ef: invokevirtual java/io/OutputStream.close ()V
      // 2f2: aconst_null
      // 2f3: astore 8
      // 2f5: bipush 1
      // 2f6: istore 9
      // 2f8: iload 2
      // 2f9: ifeq 303
      // 2fc: aload 5
      // 2fe: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 303: aload 7
      // 305: ifnull 312
      // 308: aload 7
      // 30a: invokevirtual java/io/InputStream.close ()V
      // 30d: goto 312
      // 310: astore 22
      // 312: aload 8
      // 314: ifnull 321
      // 317: aload 8
      // 319: invokevirtual java/io/OutputStream.close ()V
      // 31c: goto 321
      // 31f: astore 22
      // 321: iload 9
      // 323: ifne 33c
      // 326: iload 10
      // 328: ifeq 33c
      // 32b: aload 6
      // 32d: ifnull 33c
      // 330: aload 6
      // 332: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 337: goto 33c
      // 33a: astore 22
      // 33c: aload 5
      // 33e: ifnull 35f
      // 341: iload 11
      // 343: ifeq 353
      // 346: aload 5
      // 348: bipush 0
      // 349: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 34e: goto 353
      // 351: astore 22
      // 353: aload 5
      // 355: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 35a: goto 35f
      // 35d: astore 22
      // 35f: aload 6
      // 361: ifnull 382
      // 364: iload 11
      // 366: ifeq 376
      // 369: aload 6
      // 36b: bipush 0
      // 36c: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 371: goto 376
      // 374: astore 22
      // 376: aload 6
      // 378: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 37d: goto 382
      // 380: astore 22
      // 382: aload 3
      // 383: ifnonnull 389
      // 386: goto 4ad
      // 389: aload 3
      // 38a: invokeinterface net/rim/device/internal/io/file/CopyProgressCallback.copyCompleted ()V 1
      // 38f: return
      // 390: astore 12
      // 392: aload 7
      // 394: ifnull 3a1
      // 397: aload 7
      // 399: invokevirtual java/io/InputStream.close ()V
      // 39c: goto 3a1
      // 39f: astore 22
      // 3a1: aload 8
      // 3a3: ifnull 3b0
      // 3a6: aload 8
      // 3a8: invokevirtual java/io/OutputStream.close ()V
      // 3ab: goto 3b0
      // 3ae: astore 22
      // 3b0: iload 9
      // 3b2: ifne 3cb
      // 3b5: iload 10
      // 3b7: ifeq 3cb
      // 3ba: aload 6
      // 3bc: ifnull 3cb
      // 3bf: aload 6
      // 3c1: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 3c6: goto 3cb
      // 3c9: astore 22
      // 3cb: aload 5
      // 3cd: ifnull 3ee
      // 3d0: iload 11
      // 3d2: ifeq 3e2
      // 3d5: aload 5
      // 3d7: bipush 0
      // 3d8: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 3dd: goto 3e2
      // 3e0: astore 22
      // 3e2: aload 5
      // 3e4: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3e9: goto 3ee
      // 3ec: astore 22
      // 3ee: aload 6
      // 3f0: ifnull 411
      // 3f3: iload 11
      // 3f5: ifeq 405
      // 3f8: aload 6
      // 3fa: bipush 0
      // 3fb: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 400: goto 405
      // 403: astore 22
      // 405: aload 6
      // 407: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 40c: goto 411
      // 40f: astore 22
      // 411: aload 3
      // 412: ifnonnull 418
      // 415: goto 4ad
      // 418: aload 3
      // 419: invokeinterface net/rim/device/internal/io/file/CopyProgressCallback.copyCompleted ()V 1
      // 41e: return
      // 41f: astore 20
      // 421: aload 7
      // 423: ifnull 430
      // 426: aload 7
      // 428: invokevirtual java/io/InputStream.close ()V
      // 42b: goto 430
      // 42e: astore 22
      // 430: aload 8
      // 432: ifnull 43f
      // 435: aload 8
      // 437: invokevirtual java/io/OutputStream.close ()V
      // 43a: goto 43f
      // 43d: astore 22
      // 43f: iload 9
      // 441: ifne 45a
      // 444: iload 10
      // 446: ifeq 45a
      // 449: aload 6
      // 44b: ifnull 45a
      // 44e: aload 6
      // 450: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 455: goto 45a
      // 458: astore 22
      // 45a: aload 5
      // 45c: ifnull 47d
      // 45f: iload 11
      // 461: ifeq 471
      // 464: aload 5
      // 466: bipush 0
      // 467: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 46c: goto 471
      // 46f: astore 22
      // 471: aload 5
      // 473: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 478: goto 47d
      // 47b: astore 22
      // 47d: aload 6
      // 47f: ifnull 4a0
      // 482: iload 11
      // 484: ifeq 494
      // 487: aload 6
      // 489: bipush 0
      // 48a: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 48f: goto 494
      // 492: astore 22
      // 494: aload 6
      // 496: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 49b: goto 4a0
      // 49e: astore 22
      // 4a0: aload 3
      // 4a1: ifnull 4aa
      // 4a4: aload 3
      // 4a5: invokeinterface net/rim/device/internal/io/file/CopyProgressCallback.copyCompleted ()V 1
      // 4aa: aload 20
      // 4ac: athrow
      // 4ad: return
      // try (41 -> 99): 370 null
      // try (152 -> 166): 370 null
      // try (219 -> 316): 370 null
      // try (41 -> 99): 425 null
      // try (152 -> 166): 425 null
      // try (219 -> 316): 425 null
      // try (370 -> 371): 425 null
      // try (425 -> 426): 425 null
      // try (428 -> 430): 431 null
      // try (373 -> 375): 376 null
      // try (318 -> 320): 321 null
      // try (168 -> 170): 171 null
      // try (101 -> 103): 104 null
      // try (434 -> 436): 437 null
      // try (379 -> 381): 382 null
      // try (324 -> 326): 327 null
      // try (174 -> 176): 177 null
      // try (107 -> 109): 110 null
      // try (444 -> 446): 447 null
      // try (389 -> 391): 392 null
      // try (334 -> 336): 337 null
      // try (184 -> 186): 187 null
      // try (117 -> 119): 120 null
      // try (450 -> 455): 456 null
      // try (395 -> 400): 401 null
      // try (340 -> 345): 346 null
      // try (190 -> 195): 196 null
      // try (123 -> 128): 129 null
      // try (457 -> 459): 460 null
      // try (402 -> 404): 405 null
      // try (347 -> 349): 350 null
      // try (197 -> 199): 200 null
      // try (130 -> 132): 133 null
      // try (463 -> 468): 469 null
      // try (408 -> 413): 414 null
      // try (353 -> 358): 359 null
      // try (203 -> 208): 209 null
      // try (136 -> 141): 142 null
      // try (470 -> 472): 473 null
      // try (415 -> 417): 418 null
      // try (360 -> 362): 363 null
      // try (210 -> 212): 213 null
      // try (143 -> 145): 146 null
   }

   public static final void saveInputStream(InputStream in, String fileURL) {
      saveInputStream(in, fileURL, false);
   }

   public static final void saveInputStream(InputStream in, String fileURL, boolean overwrite) {
      saveInputStream(in, fileURL, overwrite, false);
   }

   public static final String getDisplayName(String fileURL) {
      int startIndex = 0;
      if (fileURL.startsWith(FILE_COLON_SLASH_SLASH)) {
         startIndex = FILE_COLON_SLASH_SLASH.length();
      }

      String prefix = null;
      if (fileURL.regionMatches(true, startIndex, FILE_SYSTEM_STORE_ROOT_STR, 0, FILE_SYSTEM_STORE_ROOT_STR.length())) {
         startIndex += FILE_SYSTEM_STORE_ROOT_STR.length();
         prefix = _fileSystemRb.getString(14);
      } else if (fileURL.regionMatches(true, startIndex, FILE_SYSTEM_SDCARD_ROOT_STR, 0, FILE_SYSTEM_SDCARD_ROOT_STR.length())) {
         startIndex += FILE_SYSTEM_SDCARD_ROOT_STR.length();
         prefix = _fileSystemRb.getString(13);
      } else {
         prefix = "";
      }

      return ((StringBuffer)(new Object())).append(prefix).append(URIDecoder.decode(fileURL.substring(startIndex), "UTF-8", false)).toString();
   }

   public static final String getDisplayBaseName(String pathURL) {
      int startIndex = 0;
      int endIndex = pathURL.length();
      String path = pathURL;
      if (pathURL.startsWith(FILE_COLON_SLASH_SLASH)) {
         startIndex = FILE_COLON_SLASH_SLASH.length();
         path = pathURL.substring(startIndex, endIndex);
         endIndex = path.length();
      }

      if (isDirectory(path)) {
         if (path.equals(FILE_SYSTEM_SDCARD_ROOT_STR)) {
            return _fileSystemRb.getString(13);
         }

         if (path.equals(USER_STORE_ROOT_STR)) {
            return _fileSystemRb.getString(14);
         }

         endIndex--;
      }

      startIndex = path.lastIndexOf(47, endIndex - 1);
      if (startIndex < 0) {
         startIndex = 0;
      } else {
         startIndex++;
      }

      if (StringUtilities.endsWithIgnoreCase(path, ENCRYPTED_MEDIA_EXTENSION, 1701707776)) {
         endIndex -= ENCRYPTED_MEDIA_EXTENSION.length();
      }

      int extensionIndex = path.lastIndexOf(46, endIndex - 1);
      if (extensionIndex > startIndex) {
         endIndex = extensionIndex;
      }

      return URIDecoder.decode(path.substring(startIndex, endIndex), "UTF-8", false);
   }

   public static final void checkSpaceAvailable(long newSize, FileConnection fc) {
      if (newSize >= fc.availableSize()) {
         throw new Object(9);
      }

      if (fc.getPath().startsWith(FILE_SYSTEM_STORE_ROOT_STR) && newSize > FileSystemOptions.getContentStoreMaxFileSize()) {
         throw new Object(1008);
      }
   }

   public static final void saveInputStream(InputStream param0, String param1, boolean param2, boolean param3) {
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
      // 001: astore 4
      // 003: aconst_null
      // 004: astore 5
      // 006: bipush 0
      // 007: istore 6
      // 009: bipush 0
      // 00a: istore 7
      // 00c: aload 1
      // 00d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 010: checkcast java/lang/Object
      // 013: astore 5
      // 015: aload 0
      // 016: invokevirtual java/io/InputStream.available ()I
      // 019: istore 8
      // 01b: iload 8
      // 01d: i2l
      // 01e: aload 5
      // 020: invokestatic net/rim/device/internal/io/file/FileUtilities.checkSpaceAvailable (JLjavax/microedition/io/file/FileConnection;)V
      // 023: aload 5
      // 025: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 02a: ifeq 038
      // 02d: iload 2
      // 02e: ifeq 038
      // 031: aload 5
      // 033: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 038: iload 3
      // 039: ifeq 062
      // 03c: aload 5
      // 03e: dup
      // 03f: instanceof java/lang/Object
      // 042: ifne 049
      // 045: pop
      // 046: goto 062
      // 049: checkcast java/lang/Object
      // 04c: astore 9
      // 04e: aload 9
      // 050: bipush 51
      // 052: invokestatic net/rim/device/api/system/CodeSigningKey.getBuiltInKey (I)Lnet/rim/device/api/system/CodeSigningKey;
      // 055: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.setControlledAccess (Lnet/rim/device/api/system/CodeSigningKey;)Z 2
      // 05a: pop
      // 05b: aload 9
      // 05d: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.enableDRMForwardLock ()V 1
      // 062: aload 5
      // 064: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 069: bipush 1
      // 06a: istore 7
      // 06c: aload 5
      // 06e: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 073: astore 4
      // 075: sipush 16384
      // 078: newarray 8
      // 07a: astore 9
      // 07c: aload 0
      // 07d: aload 9
      // 07f: invokevirtual java/io/InputStream.read ([B)I
      // 082: istore 10
      // 084: iload 10
      // 086: bipush -1
      // 088: if_icmpne 08e
      // 08b: goto 09b
      // 08e: aload 4
      // 090: aload 9
      // 092: bipush 0
      // 093: iload 10
      // 095: invokevirtual java/io/OutputStream.write ([BII)V
      // 098: goto 07c
      // 09b: aload 4
      // 09d: invokevirtual java/io/OutputStream.close ()V
      // 0a0: aconst_null
      // 0a1: astore 4
      // 0a3: bipush 1
      // 0a4: istore 6
      // 0a6: aload 4
      // 0a8: ifnull 0b5
      // 0ab: aload 4
      // 0ad: invokevirtual java/io/OutputStream.close ()V
      // 0b0: goto 0b5
      // 0b3: astore 8
      // 0b5: iload 6
      // 0b7: ifne 0d0
      // 0ba: iload 7
      // 0bc: ifeq 0d0
      // 0bf: aload 5
      // 0c1: ifnull 0d0
      // 0c4: aload 5
      // 0c6: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 0cb: goto 0d0
      // 0ce: astore 8
      // 0d0: aload 5
      // 0d2: ifnull 15c
      // 0d5: aload 5
      // 0d7: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0dc: return
      // 0dd: astore 8
      // 0df: return
      // 0e0: astore 8
      // 0e2: aload 4
      // 0e4: ifnull 0f1
      // 0e7: aload 4
      // 0e9: invokevirtual java/io/OutputStream.close ()V
      // 0ec: goto 0f1
      // 0ef: astore 8
      // 0f1: iload 6
      // 0f3: ifne 10c
      // 0f6: iload 7
      // 0f8: ifeq 10c
      // 0fb: aload 5
      // 0fd: ifnull 10c
      // 100: aload 5
      // 102: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 107: goto 10c
      // 10a: astore 8
      // 10c: aload 5
      // 10e: ifnull 15c
      // 111: aload 5
      // 113: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 118: return
      // 119: astore 8
      // 11b: return
      // 11c: astore 11
      // 11e: aload 4
      // 120: ifnull 12d
      // 123: aload 4
      // 125: invokevirtual java/io/OutputStream.close ()V
      // 128: goto 12d
      // 12b: astore 12
      // 12d: iload 6
      // 12f: ifne 148
      // 132: iload 7
      // 134: ifeq 148
      // 137: aload 5
      // 139: ifnull 148
      // 13c: aload 5
      // 13e: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 143: goto 148
      // 146: astore 12
      // 148: aload 5
      // 14a: ifnull 159
      // 14d: aload 5
      // 14f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 154: goto 159
      // 157: astore 12
      // 159: aload 11
      // 15b: athrow
      // 15c: return
      // try (75 -> 77): 78 null
      // try (85 -> 87): 88 null
      // try (91 -> 93): 94 null
      // try (8 -> 73): 96 null
      // try (99 -> 101): 102 null
      // try (109 -> 111): 112 null
      // try (115 -> 117): 118 null
      // try (8 -> 73): 120 null
      // try (96 -> 97): 120 null
      // try (123 -> 125): 126 null
      // try (133 -> 135): 136 null
      // try (139 -> 141): 142 null
      // try (120 -> 121): 120 null
   }

   public static final EncodedImage getEncodedImage(String param0) {
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
      // 00: aconst_null
      // 01: astore 1
      // 02: aconst_null
      // 03: astore 2
      // 04: aconst_null
      // 05: astore 3
      // 06: aload 0
      // 07: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 0a: checkcast java/lang/Object
      // 0d: astore 1
      // 0e: aload 1
      // 0f: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 14: lstore 4
      // 16: lload 4
      // 18: bipush 0
      // 19: i2l
      // 1a: lcmp
      // 1b: ifle 37
      // 1e: aload 1
      // 1f: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 24: astore 2
      // 25: aload 2
      // 26: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 29: bipush 0
      // 2a: lload 4
      // 2c: l2i
      // 2d: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage ([BII)Lnet/rim/device/api/system/EncodedImage;
      // 30: astore 3
      // 31: aload 2
      // 32: invokevirtual java/io/InputStream.close ()V
      // 35: aconst_null
      // 36: astore 2
      // 37: aload 2
      // 38: ifnull 44
      // 3b: aload 2
      // 3c: invokevirtual java/io/InputStream.close ()V
      // 3f: goto 44
      // 42: astore 4
      // 44: aload 1
      // 45: ifnull 94
      // 48: aload 1
      // 49: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4e: aload 3
      // 4f: areturn
      // 50: astore 4
      // 52: aload 3
      // 53: areturn
      // 54: astore 4
      // 56: aload 2
      // 57: ifnull 63
      // 5a: aload 2
      // 5b: invokevirtual java/io/InputStream.close ()V
      // 5e: goto 63
      // 61: astore 4
      // 63: aload 1
      // 64: ifnull 94
      // 67: aload 1
      // 68: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 6d: aload 3
      // 6e: areturn
      // 6f: astore 4
      // 71: aload 3
      // 72: areturn
      // 73: astore 6
      // 75: aload 2
      // 76: ifnull 82
      // 79: aload 2
      // 7a: invokevirtual java/io/InputStream.close ()V
      // 7d: goto 82
      // 80: astore 7
      // 82: aload 1
      // 83: ifnull 91
      // 86: aload 1
      // 87: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 8c: goto 91
      // 8f: astore 7
      // 91: aload 6
      // 93: athrow
      // 94: aload 3
      // 95: areturn
      // try (34 -> 36): 37 null
      // try (40 -> 42): 44 null
      // try (6 -> 32): 47 null
      // try (50 -> 52): 53 null
      // try (56 -> 58): 60 null
      // try (6 -> 32): 63 null
      // try (47 -> 48): 63 null
      // try (66 -> 68): 69 null
      // try (72 -> 74): 75 null
      // try (63 -> 64): 63 null
   }

   public static final void rename(String fullyQualifiedFilename, String newName) {
      rename(fullyQualifiedFilename, newName, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void rename(String fullyQualifiedFilename, String newName, boolean force) {
      FileConnection conn = null;
      boolean var16 = false /* VF: Semaphore variable */;

      try {
         var16 = true;
         conn = (FileConnection)Connector.open(makeFileURL(fullyQualifiedFilename));
         boolean writable = true;
         if (force && !conn.canWrite()) {
            conn.setWritable(true);
            writable = false;
         }

         if (isDirectory(fullyQualifiedFilename) && isDirectory(newName)) {
            newName = newName.substring(0, newName.length() - 1);
         }

         conn.rename(newName);
         if (!writable) {
            conn.setWritable(false);
            var16 = false;
         } else {
            var16 = false;
         }
      } finally {
         if (var16) {
            if (conn != null) {
               label120:
               try {
                  conn.close();
               } finally {
                  break label120;
               }
            }
         }
      }

      if (conn != null) {
         try {
            conn.close();
         } finally {
            return;
         }
      }
   }

   public static final void delete(String fullyQualifiedFilename) {
      delete(fullyQualifiedFilename, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void delete(String fullyQualifiedFilename, boolean force) {
      FileConnection conn = null;
      boolean var15 = false /* VF: Semaphore variable */;

      try {
         var15 = true;
         conn = (FileConnection)Connector.open(makeFileURL(fullyQualifiedFilename));
         if (conn.exists()) {
            if (conn.isDirectory()) {
               deleteDirectory(fullyQualifiedFilename, force);
               var15 = false;
            } else {
               if (force && !conn.canWrite()) {
                  conn.setWritable(true);
               }

               conn.delete();
               var15 = false;
            }
         } else {
            var15 = false;
         }
      } finally {
         if (var15) {
            if (conn != null) {
               label105:
               try {
                  conn.close();
               } finally {
                  break label105;
               }
            }
         }
      }

      if (conn != null) {
         try {
            conn.close();
         } finally {
            return;
         }
      }
   }

   public static final void deleteDirectory(String fullyQualifiedName) {
      deleteDirectory(fullyQualifiedName, true);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void deleteDirectory(String fullyQualifiedName, boolean deleteAllFiles) {
      FileConnection conn = null;
      boolean var18 = false /* VF: Semaphore variable */;

      try {
         var18 = true;
         conn = (FileConnection)Connector.open(makeFileURL(fullyQualifiedName));
         if (!conn.exists()) {
            var18 = false;
         } else {
            FileSystem.suspendCommitOnDelete();
            Enumeration elements = conn.list("*", true);
            boolean isEmpty = true;
            boolean thumbsDBPresent = false;

            while (elements.hasMoreElements() && isEmpty) {
               String file = (String)elements.nextElement();
               if (deleteAllFiles) {
                  delete(((StringBuffer)(new Object())).append(fullyQualifiedName).append(file).toString(), true);
               } else if (file.equals(MetaDataFile.THUMBS_DB_FILE)) {
                  thumbsDBPresent = true;
               } else {
                  isEmpty = false;
               }
            }

            if (!deleteAllFiles && isEmpty && thumbsDBPresent) {
               delete(((StringBuffer)(new Object())).append(fullyQualifiedName).append(MetaDataFile.THUMBS_DB_FILE).toString(), true);
            }

            if (!conn.canWrite()) {
               conn.setWritable(true);
            }

            FileSystem.resumeCommitOnDelete();
            conn.delete();
            var18 = false;
         }
      } finally {
         if (var18) {
            FileSystem.resumeCommitOnDelete();
            if (conn != null) {
               label160:
               try {
                  conn.close();
               } finally {
                  break label160;
               }
            }
         }
      }

      FileSystem.resumeCommitOnDelete();
      if (conn != null) {
         try {
            conn.close();
         } finally {
            return;
         }
      }
   }

   public static final String getPath(String filename) {
      if (filename.length() > 1) {
         int dirNamePos = filename.lastIndexOf(47, filename.length() - 2) + 1;
         int startPos = filename.startsWith(FILE_COLON_SLASH_SLASH) ? 7 : 0;
         return startPos == 0 && dirNamePos == filename.length() ? filename : filename.substring(startPos, dirNamePos);
      } else {
         return filename;
      }
   }

   public static final String getRoot(String filename) {
      if (filename.length() > 1) {
         int startIndex = 0;
         if (filename.startsWith(FILE_COLON_SLASH_SLASH)) {
            startIndex = FILE_COLON_SLASH_SLASH.length();
         }

         int dirNamePos = filename.indexOf(47, startIndex);
         if (dirNamePos != -1) {
            dirNamePos = filename.indexOf(47, dirNamePos + 1);
            if (dirNamePos != -1) {
               return filename.substring(startIndex, dirNamePos);
            }
         }
      }

      return filename;
   }

   public static final String getPathURL(String filename) {
      if (filename.length() > 1) {
         int dirNamePos = filename.lastIndexOf(47, filename.length() - 2) + 1;
         if (filename.length() != dirNamePos) {
            filename = filename.substring(0, dirNamePos);
         }

         if (!filename.startsWith(FILE_COLON_SLASH_SLASH)) {
            filename = ((StringBuffer)(new Object())).append(FILE_COLON_SLASH_SLASH).append(filename).toString();
         }
      }

      return filename;
   }

   public static final String getName(String filename) {
      if (filename.length() > 1) {
         int dirNamePos = filename.lastIndexOf(47, filename.length() - 2);
         return dirNamePos == -1 ? filename : filename.substring(dirNamePos + 1, filename.length());
      } else {
         return filename;
      }
   }

   public static final boolean isDirectory(String filename) {
      return filename.length() > 0 && filename.charAt(filename.length() - 1) == '/';
   }

   public static final boolean isRemovable(String path) {
      return path.startsWith(FILE_SYSTEM_SDCARD_ROOT_STR)
         || path.startsWith(((StringBuffer)(new Object())).append(FILE_COLON_SLASH_SLASH).append(FILE_SYSTEM_SDCARD_ROOT_STR).toString());
   }

   public static final String getDirectoryName(String path) {
      char dirSlash = '/';
      int dirNameEnd = path.lastIndexOf(dirSlash);
      int dirNameStart = path.substring(0, dirNameEnd).lastIndexOf(dirSlash);
      return path.substring(dirNameStart + 1, dirNameEnd + 1);
   }

   public static final String makeFileURL(String path, String filename) {
      StringBuffer fileURL = (StringBuffer)(new Object());
      if (!path.startsWith(FILE_COLON_SLASH_SLASH)) {
         fileURL.append(FILE_COLON_SLASH_SLASH);
      }

      return fileURL.append(path).append(filename).toString();
   }

   public static final String makeFileURL(String file) {
      return !file.startsWith(FILE_COLON_SLASH_SLASH) ? ((StringBuffer)(new Object())).append(FILE_COLON_SLASH_SLASH).append(file).toString() : file;
   }

   public static final boolean filenamesMatch(String str1, String str2) {
      if (StringUtilities.strEqualIgnoreCase(str1, str2)) {
         return true;
      } else {
         return str1 != null && str2 != null ? StringUtilities.strEqual(encodeString(str1.toLowerCase()), encodeString(str2.toLowerCase())) : false;
      }
   }

   public static final String makeValidFilename(String name) {
      if (name != null && !FilenameValidator.validateFilenameAndPath(name)) {
         StringBuffer sbuf = (StringBuffer)(new Object(name));
         int size = name.length();

         for (int i = 0; i < size; i++) {
            char nextChar = name.charAt(i);
            if (!FilenameValidator.validateChar(nextChar)) {
               nextChar = CharacterUtilities.getOriginal(nextChar);
               if (!FilenameValidator.validateChar(nextChar)) {
                  nextChar = '_';
               }

               sbuf.setCharAt(i, nextChar);
            }
         }

         return sbuf.toString();
      } else {
         return name;
      }
   }

   public static final String encodeString(String name) {
      StringBuffer output = null;
      int length = name.length();

      for (int i = 0; i < length; i++) {
         char character = name.charAt(i);
         switch (character) {
            case '!':
            case '$':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case '_':
               break;
            default:
               if (('0' > character || character > '9') && ('A' > character || character > 'Z') && ('a' > character || character > 'z')) {
                  if (output == null) {
                     output = (StringBuffer)(new Object(length + 16));
                     StringUtilities.append(output, name, 0, i);
                  }

                  URIEncoder.writeUTF8Char(output, character);
                  continue;
               }
         }

         if (output != null) {
            output.append(character);
         }
      }

      return output != null ? output.toString() : name;
   }

   public static final String sizeToString(long size) {
      return sizeToString(size, 1);
   }

   public static final String sizeToString(long size, int decimalPlaces) {
      StringBuffer fileSizeStr = (StringBuffer)(new Object());
      int sizeIndex = MULTIPLIER.length - 1;

      while (size < MULTIPLIER[sizeIndex] && sizeIndex != 0) {
         sizeIndex--;
      }

      long multiplier = MULTIPLIER[sizeIndex];
      if (decimalPlaces > 0) {
         long exponent = size / multiplier;
         fileSizeStr.append(Long.toString(exponent));
         fileSizeStr.append('.');
         long remainder = size - exponent * multiplier;
         int count = decimalPlaces;

         while (--count >= 0) {
            remainder *= 10;
         }

         remainder /= multiplier;
         StringUtilities.append(fileSizeStr, NumberUtilities.toString(remainder, 10, decimalPlaces), 0, decimalPlaces);
      } else {
         long exponent = (size + (multiplier >> 1)) / multiplier;
         if (exponent == 0 && size > 0) {
            exponent = 1;
         }

         fileSizeStr.append(Long.toString(exponent));
      }

      fileSizeStr.append(CommonResource.getStringArray(10182)[sizeIndex]);
      return fileSizeStr.toString();
   }

   public static final boolean isRoot(String path) {
      if (path.length() > 0 && path.charAt(0) == '/') {
         Enumeration roots = FileSystemRegistry.listRoots();
         path = path.substring(1);

         while (roots.hasMoreElements()) {
            String nextRoot = (String)roots.nextElement();
            if (nextRoot.equals(path)) {
               return true;
            }
         }
      }

      return false;
   }

   public static final boolean isSDCardMounted() {
      return isRoot(FILE_SYSTEM_SDCARD_ROOT_STR);
   }

   public static final int getDefaultWritableFileSystem(int param0) {
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
      // 00: invokestatic net/rim/device/internal/io/file/FileUtilities.isSDCardMounted ()Z
      // 03: ifne 09
      // 06: bipush 3
      // 08: ireturn
      // 09: iload 0
      // 0a: ifle 16
      // 0d: iload 0
      // 0e: i2l
      // 0f: invokestatic net/rim/device/internal/io/file/FileSystemOptions.getContentStoreMaxFileSize ()J
      // 12: lcmp
      // 13: ifle 18
      // 16: bipush 1
      // 17: ireturn
      // 18: aconst_null
      // 19: astore 1
      // 1a: new java/lang/Object
      // 1d: dup
      // 1e: invokespecial java/lang/StringBuffer.<init> ()V
      // 21: getstatic net/rim/device/internal/io/file/FileUtilities.FILE_COLON_SLASH_SLASH Ljava/lang/String;
      // 24: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 27: getstatic net/rim/device/internal/io/file/FileUtilities.FILE_SYSTEM_SDCARD_ROOT_STR Ljava/lang/String;
      // 2a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 30: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 33: checkcast java/lang/Object
      // 36: astore 1
      // 37: aload 1
      // 38: invokeinterface javax/microedition/io/file/FileConnection.availableSize ()J 1
      // 3d: iload 0
      // 3e: i2l
      // 3f: lcmp
      // 40: iflt 54
      // 43: bipush 1
      // 44: istore 2
      // 45: aload 1
      // 46: ifnull 52
      // 49: aload 1
      // 4a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4f: iload 2
      // 50: ireturn
      // 51: astore 3
      // 52: iload 2
      // 53: ireturn
      // 54: aload 1
      // 55: ifnull 8b
      // 58: aload 1
      // 59: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 5e: goto 8b
      // 61: astore 2
      // 62: goto 8b
      // 65: astore 2
      // 66: aload 1
      // 67: ifnull 8b
      // 6a: aload 1
      // 6b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 70: goto 8b
      // 73: astore 2
      // 74: goto 8b
      // 77: astore 4
      // 79: aload 1
      // 7a: ifnull 88
      // 7d: aload 1
      // 7e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 83: goto 88
      // 86: astore 5
      // 88: aload 4
      // 8a: athrow
      // 8b: new java/lang/Object
      // 8e: dup
      // 8f: invokespecial java/lang/StringBuffer.<init> ()V
      // 92: getstatic net/rim/device/internal/io/file/FileUtilities.FILE_COLON_SLASH_SLASH Ljava/lang/String;
      // 95: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 98: getstatic net/rim/device/internal/io/file/FileUtilities.FILE_SYSTEM_STORE_ROOT_STR Ljava/lang/String;
      // 9b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 9e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // a1: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // a4: checkcast java/lang/Object
      // a7: astore 1
      // a8: aload 1
      // a9: invokeinterface javax/microedition/io/file/FileConnection.availableSize ()J 1
      // ae: iload 0
      // af: i2l
      // b0: lcmp
      // b1: iflt c6
      // b4: bipush 3
      // b6: istore 2
      // b7: aload 1
      // b8: ifnull c4
      // bb: aload 1
      // bc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // c1: iload 2
      // c2: ireturn
      // c3: astore 3
      // c4: iload 2
      // c5: ireturn
      // c6: aload 1
      // c7: ifnull f9
      // ca: aload 1
      // cb: invokeinterface javax/microedition/io/Connection.close ()V 1
      // d0: bipush 1
      // d1: ireturn
      // d2: astore 2
      // d3: bipush 1
      // d4: ireturn
      // d5: astore 2
      // d6: aload 1
      // d7: ifnull f9
      // da: aload 1
      // db: invokeinterface javax/microedition/io/Connection.close ()V 1
      // e0: bipush 1
      // e1: ireturn
      // e2: astore 2
      // e3: bipush 1
      // e4: ireturn
      // e5: astore 6
      // e7: aload 1
      // e8: ifnull f6
      // eb: aload 1
      // ec: invokeinterface javax/microedition/io/Connection.close ()V 1
      // f1: goto f6
      // f4: astore 7
      // f6: aload 6
      // f8: athrow
      // f9: bipush 1
      // fa: ireturn
      // try (36 -> 38): 40 null
      // try (45 -> 47): 48 null
      // try (15 -> 34): 50 null
      // try (53 -> 55): 56 null
      // try (15 -> 34): 58 null
      // try (50 -> 51): 58 null
      // try (61 -> 63): 64 null
      // try (58 -> 59): 58 null
      // try (88 -> 90): 92 null
      // try (97 -> 99): 101 null
      // try (67 -> 86): 104 null
      // try (107 -> 109): 111 null
      // try (67 -> 86): 114 null
      // try (104 -> 105): 114 null
      // try (117 -> 119): 120 null
      // try (114 -> 115): 114 null
   }

   public static final String getDefaultPath(String filename) {
      return getDefaultPath(MIMETypeAssociations.getMediaType(filename));
   }

   public static final String getDefaultPathForMIMEType(String mimeType) {
      return getDefaultPathForMIMEType(mimeType, -1);
   }

   public static final String getDefaultPathForMIMEType(String mimeType, int spaceRequired) {
      if (mimeType.equals("audio/amr")) {
         String location = null;
         return isSDCardMounted() ? "file:///SDCard/BlackBerry/voicenotes/" : "file:///store/home/user/voicenotes/";
      } else {
         return FileIndexService.getCurrentMediaFolder(MIMETypeAssociations.getMediaTypeFromMIMEType(mimeType), spaceRequired);
      }
   }

   public static final String getDefaultWritablePathForMIMEType(String mimeType, int size) {
      return getDefaultWritablePath(MIMETypeAssociations.getMediaTypeFromMIMEType(mimeType), size);
   }

   public static final String getDefaultPath(int mediaType) {
      return FileIndexService.getCurrentMediaFolder(mediaType, -1);
   }

   private static final String getDefaultWritablePath(int mediaType, int size) {
      String path = FileIndexService.getCurrentMediaFolder(mediaType, size);
      if (StringUtilities.startsWithIgnoreCase(path, FILE_SYSTEM_SDCARD_ROOT_STR, 1701707776) && getDefaultWritableFileSystem(size) == 3) {
         path = FileIndexService.getDefaultMediaFolderForFileSystem(mediaType, 3, size);
      }

      return path;
   }

   public static final byte[] getData(String param0) {
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
      // 00: aconst_null
      // 01: astore 1
      // 02: aconst_null
      // 03: astore 2
      // 04: aconst_null
      // 05: astore 3
      // 06: aload 0
      // 07: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 0a: checkcast java/lang/Object
      // 0d: astore 1
      // 0e: aload 1
      // 0f: invokeinterface javax/microedition/io/file/FileConnection.canRead ()Z 1
      // 14: ifeq 29
      // 17: aload 1
      // 18: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 1d: astore 2
      // 1e: aload 2
      // 1f: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 22: astore 3
      // 23: aload 2
      // 24: invokevirtual java/io/InputStream.close ()V
      // 27: aconst_null
      // 28: astore 2
      // 29: aload 2
      // 2a: ifnull 36
      // 2d: aload 2
      // 2e: invokevirtual java/io/InputStream.close ()V
      // 31: goto 36
      // 34: astore 4
      // 36: aload 1
      // 37: ifnull 86
      // 3a: aload 1
      // 3b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 40: aload 3
      // 41: areturn
      // 42: astore 4
      // 44: aload 3
      // 45: areturn
      // 46: astore 4
      // 48: aload 2
      // 49: ifnull 55
      // 4c: aload 2
      // 4d: invokevirtual java/io/InputStream.close ()V
      // 50: goto 55
      // 53: astore 4
      // 55: aload 1
      // 56: ifnull 86
      // 59: aload 1
      // 5a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 5f: aload 3
      // 60: areturn
      // 61: astore 4
      // 63: aload 3
      // 64: areturn
      // 65: astore 5
      // 67: aload 2
      // 68: ifnull 74
      // 6b: aload 2
      // 6c: invokevirtual java/io/InputStream.close ()V
      // 6f: goto 74
      // 72: astore 6
      // 74: aload 1
      // 75: ifnull 83
      // 78: aload 1
      // 79: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 7e: goto 83
      // 81: astore 6
      // 83: aload 5
      // 85: athrow
      // 86: aload 3
      // 87: areturn
      // try (25 -> 27): 28 null
      // try (31 -> 33): 35 null
      // try (6 -> 23): 38 null
      // try (41 -> 43): 44 null
      // try (47 -> 49): 51 null
      // try (6 -> 23): 54 null
      // try (38 -> 39): 54 null
      // try (57 -> 59): 60 null
      // try (63 -> 65): 66 null
      // try (54 -> 55): 54 null
   }

   public static final void setHidden(String param0, boolean param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 2
      // 02: aload 0
      // 03: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 06: checkcast java/lang/Object
      // 09: astore 2
      // 0a: aload 2
      // 0b: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 10: ifeq 1a
      // 13: aload 2
      // 14: iload 1
      // 15: invokeinterface javax/microedition/io/file/FileConnection.setHidden (Z)V 2
      // 1a: aload 2
      // 1b: ifnull 57
      // 1e: aload 2
      // 1f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 24: return
      // 25: astore 3
      // 26: return
      // 27: astore 3
      // 28: aload 2
      // 29: ifnull 57
      // 2c: aload 2
      // 2d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 32: return
      // 33: astore 3
      // 34: return
      // 35: astore 3
      // 36: aload 2
      // 37: ifnull 57
      // 3a: aload 2
      // 3b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 40: return
      // 41: astore 3
      // 42: return
      // 43: astore 4
      // 45: aload 2
      // 46: ifnull 54
      // 49: aload 2
      // 4a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4f: goto 54
      // 52: astore 5
      // 54: aload 4
      // 56: athrow
      // 57: return
      // try (14 -> 16): 17 null
      // try (2 -> 12): 19 null
      // try (22 -> 24): 25 null
      // try (2 -> 12): 27 null
      // try (30 -> 32): 33 null
      // try (2 -> 12): 35 null
      // try (19 -> 20): 35 null
      // try (27 -> 28): 35 null
      // try (38 -> 40): 41 null
      // try (35 -> 36): 35 null
   }

   public static final boolean isEncrypted(String fileName) {
      return StringUtilities.endsWithIgnoreCase(fileName, ENCRYPTED_MEDIA_EXTENSION, 1701707776);
   }

   public static final String getFileNameAndStripEncryptionExt(String fileName) {
      if (fileName == null) {
         return null;
      } else {
         return StringUtilities.endsWithIgnoreCase(fileName, ENCRYPTED_MEDIA_EXTENSION, 1701707776)
            ? fileName.substring(0, fileName.length() - ENCRYPTED_MEDIA_EXTENSION.length())
            : fileName;
      }
   }

   public static final String getFileName(String fileURL) {
      if (fileURL != null && fileURL.startsWith(FILE_COLON_SLASH_SLASH)) {
         fileURL = fileURL.substring(7);
      }

      return fileURL;
   }

   public static final boolean isFileSchemeURI(String uri) {
      return StringUtilities.startsWithIgnoreCase(uri, FILE_COLON_SLASH_SLASH, 1701707776);
   }
}
