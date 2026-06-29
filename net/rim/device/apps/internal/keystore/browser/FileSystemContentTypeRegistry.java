package net.rim.device.apps.internal.keystore.browser;

import java.io.InputStream;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.proxy.Proxy;

public class FileSystemContentTypeRegistry implements FileSystemListener {
   private FileSystemContentTypeListener[] _listeners;
   private FileConnection _fc;
   private long _baseDirLastModified;
   private String[] _ignoreFileList;
   private long[] _ignoreFileListLastModified;
   private int FOLDER_LAST_MODIFIED = 0;
   private int FILE_NAME = 1;
   private int FILE_LAST_MODIFIED = 2;
   private int END_METADATA = 3;
   public static final long ID = -4900565683240895068L;
   public static final String BASE_DIR = "file:///SDCard/blackberry/certificates/";

   private FileSystemContentTypeRegistry() {
      Proxy p = Proxy.getInstance();
      p.addFileSystemListener(this);
   }

   public static FileSystemContentTypeRegistry getInstance() {
      FileSystemContentTypeRegistry app = (FileSystemContentTypeRegistry)ApplicationRegistry.getApplicationRegistry().getOrWaitFor(-4900565683240895068L);
      if (app == null) {
         app = new FileSystemContentTypeRegistry();
         ApplicationRegistry.getApplicationRegistry().put(-4900565683240895068L, app);
      }

      return app;
   }

   public static void libMain(String[] args) {
      getInstance();
   }

   public void addFileSystemContentTypeListener(FileSystemContentTypeListener listener) {
      if (this._listeners == null) {
         this._listeners = new FileSystemContentTypeListener[]{listener};
      } else {
         Arrays.add(this._listeners, listener);
      }
   }

   public void removeFileSystemContentTypeListener(FileSystemContentTypeListener listener) {
      if (this._listeners != null) {
         Arrays.remove(this._listeners, listener);
      }
   }

   public void removeAllFileSystemContentTypeListeners() {
      this._listeners = null;
   }

   @Override
   public void rootChanged(int param1, String param2) {
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
      // 000: bipush 0
      // 001: istore 3
      // 002: bipush 0
      // 003: istore 4
      // 005: bipush 1
      // 006: istore 5
      // 008: iload 1
      // 009: ifeq 00f
      // 00c: goto 2a7
      // 00f: aconst_null
      // 010: astore 8
      // 012: aconst_null
      // 013: astore 9
      // 015: new java/lang/Object
      // 018: dup
      // 019: ldc_w "file:///SDCard/blackberry/certificates/metaData_"
      // 01c: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 01f: invokestatic net/rim/device/api/system/DeviceInfo.getDeviceId ()I
      // 022: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 025: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 028: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 02b: checkcast java/lang/Object
      // 02e: astore 8
      // 030: aload 0
      // 031: ldc_w "file:///SDCard/blackberry/certificates/"
      // 034: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 037: checkcast java/lang/Object
      // 03a: putfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 03d: aload 8
      // 03f: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 044: ifeq 05c
      // 047: aload 0
      // 048: aload 8
      // 04a: invokespecial net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.readMetaData (Ljavax/microedition/io/file/FileConnection;)Z
      // 04d: ifeq 056
      // 050: bipush 1
      // 051: istore 4
      // 053: goto 05e
      // 056: bipush 1
      // 057: istore 4
      // 059: goto 05e
      // 05c: bipush 1
      // 05d: istore 3
      // 05e: iload 3
      // 05f: ifne 06a
      // 062: iload 4
      // 064: ifne 06a
      // 067: goto 1ed
      // 06a: new java/lang/Object
      // 06d: dup
      // 06e: ldc_w "file:///SDCard/blackberry/certificates/metaData_"
      // 071: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 074: invokestatic net/rim/device/api/system/DeviceInfo.getDeviceId ()I
      // 077: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 07a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 07d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 080: checkcast java/lang/Object
      // 083: astore 8
      // 085: iload 3
      // 086: ifeq 098
      // 089: aload 8
      // 08b: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 090: aload 8
      // 092: bipush 1
      // 093: invokeinterface javax/microedition/io/file/FileConnection.setHidden (Z)V 2
      // 098: new java/lang/Object
      // 09b: dup
      // 09c: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 09f: astore 10
      // 0a1: aload 0
      // 0a2: aload 0
      // 0a3: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 0a6: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 0ab: putfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._baseDirLastModified J
      // 0ae: aload 10
      // 0b0: aload 0
      // 0b1: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.FOLDER_LAST_MODIFIED I
      // 0b4: aload 0
      // 0b5: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._baseDirLastModified J
      // 0b8: invokestatic net/rim/device/api/synchronization/ConverterUtilities.writeLong (Lnet/rim/device/api/util/DataBuffer;IJ)V
      // 0bb: bipush 1
      // 0bc: istore 5
      // 0be: aload 0
      // 0bf: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 0c2: invokeinterface javax/microedition/io/file/FileConnection.list ()Ljava/util/Enumeration; 1
      // 0c7: astore 14
      // 0c9: aload 0
      // 0ca: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._listeners [Lnet/rim/device/apps/internal/keystore/browser/FileSystemContentTypeListener;
      // 0cd: ifnonnull 0d3
      // 0d0: goto 1d6
      // 0d3: aload 0
      // 0d4: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._listeners [Lnet/rim/device/apps/internal/keystore/browser/FileSystemContentTypeListener;
      // 0d7: arraylength
      // 0d8: istore 15
      // 0da: aload 14
      // 0dc: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0e1: ifne 0e7
      // 0e4: goto 1d6
      // 0e7: aload 14
      // 0e9: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0ee: checkcast java/lang/Object
      // 0f1: astore 6
      // 0f3: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0f6: aload 6
      // 0f8: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 0fb: aload 0
      // 0fc: aload 6
      // 0fe: invokespecial net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.isIgnored (Ljava/lang/String;)Z
      // 101: ifne 0da
      // 104: bipush 0
      // 105: istore 16
      // 107: iload 16
      // 109: iload 15
      // 10b: if_icmpge 0da
      // 10e: aload 0
      // 10f: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._listeners [Lnet/rim/device/apps/internal/keystore/browser/FileSystemContentTypeListener;
      // 112: iload 16
      // 114: aaload
      // 115: aload 6
      // 117: invokeinterface net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeListener.isTypeSupported (Ljava/lang/String;)Z 2
      // 11c: ifne 122
      // 11f: goto 1d0
      // 122: iload 5
      // 124: ifeq 183
      // 127: sipush 6097
      // 12a: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 12d: invokestatic net/rim/device/internal/ui/component/SimpleChoiceDialog.askYesNoQuestionOnBackground (Ljava/lang/String;)Z
      // 130: ifne 180
      // 133: aload 0
      // 134: aload 10
      // 136: invokespecial net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.writeIgnoredFileListToMetaData (Lnet/rim/device/api/util/DataBuffer;)V
      // 139: aload 10
      // 13b: aload 0
      // 13c: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.END_METADATA I
      // 13f: invokestatic net/rim/device/api/synchronization/ConverterUtilities.writeEmptyField (Lnet/rim/device/api/util/DataBuffer;I)V
      // 142: aload 0
      // 143: aload 8
      // 145: aload 10
      // 147: invokespecial net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.writeAndClose (Ljavax/microedition/io/file/FileConnection;Lnet/rim/device/api/util/DataBuffer;)V
      // 14a: aload 0
      // 14b: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 14e: ifnull 15f
      // 151: aload 0
      // 152: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 155: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 15a: goto 15f
      // 15d: astore 17
      // 15f: aload 8
      // 161: ifnull 170
      // 164: aload 8
      // 166: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 16b: goto 170
      // 16e: astore 17
      // 170: aload 9
      // 172: ifnull 17f
      // 175: aload 9
      // 177: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 17c: return
      // 17d: astore 17
      // 17f: return
      // 180: bipush 0
      // 181: istore 5
      // 183: aload 0
      // 184: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._listeners [Lnet/rim/device/apps/internal/keystore/browser/FileSystemContentTypeListener;
      // 187: iload 16
      // 189: aaload
      // 18a: aload 6
      // 18c: invokeinterface net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeListener.parseCertificatesKeys (Ljava/lang/String;)Z 2
      // 191: ifeq 1d0
      // 194: aload 10
      // 196: aload 0
      // 197: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.FILE_NAME I
      // 19a: aload 6
      // 19c: invokestatic net/rim/device/api/synchronization/ConverterUtilities.writeString (Lnet/rim/device/api/util/DataBuffer;ILjava/lang/String;)V
      // 19f: new java/lang/Object
      // 1a2: dup
      // 1a3: ldc_w "file:///SDCard/blackberry/certificates/"
      // 1a6: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 1a9: aload 6
      // 1ab: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1ae: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1b1: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 1b4: checkcast java/lang/Object
      // 1b7: astore 9
      // 1b9: aload 10
      // 1bb: aload 0
      // 1bc: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.FILE_LAST_MODIFIED I
      // 1bf: aload 9
      // 1c1: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 1c6: invokestatic net/rim/device/api/synchronization/ConverterUtilities.writeLong (Lnet/rim/device/api/util/DataBuffer;IJ)V
      // 1c9: aload 9
      // 1cb: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1d0: iinc 16 1
      // 1d3: goto 107
      // 1d6: aload 0
      // 1d7: aload 10
      // 1d9: invokespecial net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.writeIgnoredFileListToMetaData (Lnet/rim/device/api/util/DataBuffer;)V
      // 1dc: aload 10
      // 1de: aload 0
      // 1df: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.END_METADATA I
      // 1e2: invokestatic net/rim/device/api/synchronization/ConverterUtilities.writeEmptyField (Lnet/rim/device/api/util/DataBuffer;I)V
      // 1e5: aload 0
      // 1e6: aload 8
      // 1e8: aload 10
      // 1ea: invokespecial net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.writeAndClose (Ljavax/microedition/io/file/FileConnection;Lnet/rim/device/api/util/DataBuffer;)V
      // 1ed: aload 8
      // 1ef: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1f4: aload 0
      // 1f5: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 1f8: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1fd: aload 0
      // 1fe: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 201: ifnull 212
      // 204: aload 0
      // 205: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 208: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 20d: goto 212
      // 210: astore 10
      // 212: aload 8
      // 214: ifnull 223
      // 217: aload 8
      // 219: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 21e: goto 223
      // 221: astore 10
      // 223: aload 9
      // 225: ifnull 2b1
      // 228: aload 9
      // 22a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 22f: return
      // 230: astore 10
      // 232: return
      // 233: astore 10
      // 235: aload 0
      // 236: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 239: ifnull 24a
      // 23c: aload 0
      // 23d: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 240: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 245: goto 24a
      // 248: astore 10
      // 24a: aload 8
      // 24c: ifnull 25b
      // 24f: aload 8
      // 251: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 256: goto 25b
      // 259: astore 10
      // 25b: aload 9
      // 25d: ifnull 2b1
      // 260: aload 9
      // 262: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 267: return
      // 268: astore 10
      // 26a: return
      // 26b: astore 18
      // 26d: aload 0
      // 26e: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 271: ifnull 282
      // 274: aload 0
      // 275: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._fc Ljavax/microedition/io/file/FileConnection;
      // 278: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 27d: goto 282
      // 280: astore 19
      // 282: aload 8
      // 284: ifnull 293
      // 287: aload 8
      // 289: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 28e: goto 293
      // 291: astore 19
      // 293: aload 9
      // 295: ifnull 2a4
      // 298: aload 9
      // 29a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 29f: goto 2a4
      // 2a2: astore 19
      // 2a4: aload 18
      // 2a6: athrow
      // 2a7: iload 1
      // 2a8: bipush 1
      // 2a9: if_icmpne 2b1
      // 2ac: aload 0
      // 2ad: aconst_null
      // 2ae: putfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._ignoreFileList [Ljava/lang/String;
      // 2b1: return
      // try (139 -> 145): 146 null
      // try (147 -> 151): 152 null
      // try (153 -> 157): 158 null
      // try (210 -> 216): 217 null
      // try (218 -> 222): 223 null
      // try (224 -> 228): 229 null
      // try (13 -> 139): 231 null
      // try (160 -> 210): 231 null
      // try (232 -> 238): 239 null
      // try (240 -> 244): 245 null
      // try (246 -> 250): 251 null
      // try (13 -> 139): 253 null
      // try (160 -> 210): 253 null
      // try (231 -> 232): 253 null
      // try (254 -> 260): 261 null
      // try (262 -> 266): 267 null
      // try (268 -> 272): 273 null
      // try (253 -> 254): 253 null
   }

   private void writeAndClose(FileConnection param1, DataBuffer param2) {
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
      // 01: astore 3
      // 02: aload 1
      // 03: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 08: astore 3
      // 09: aload 3
      // 0a: ifnonnull 24
      // 0d: aload 3
      // 0e: ifnull 1a
      // 11: aload 3
      // 12: invokevirtual java/io/OutputStream.close ()V
      // 15: goto 1a
      // 18: astore 4
      // 1a: aload 1
      // 1b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 20: return
      // 21: astore 4
      // 23: return
      // 24: aload 2
      // 25: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 28: astore 4
      // 2a: aload 3
      // 2b: aload 4
      // 2d: invokevirtual java/io/OutputStream.write ([B)V
      // 30: aload 3
      // 31: invokevirtual java/io/OutputStream.flush ()V
      // 34: aload 1
      // 35: aload 4
      // 37: arraylength
      // 38: i2l
      // 39: invokeinterface javax/microedition/io/file/FileConnection.truncate (J)V 3
      // 3e: aload 3
      // 3f: ifnull 4b
      // 42: aload 3
      // 43: invokevirtual java/io/OutputStream.close ()V
      // 46: goto 4b
      // 49: astore 4
      // 4b: aload 1
      // 4c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 51: return
      // 52: astore 4
      // 54: return
      // 55: astore 4
      // 57: aload 3
      // 58: ifnull 64
      // 5b: aload 3
      // 5c: invokevirtual java/io/OutputStream.close ()V
      // 5f: goto 64
      // 62: astore 4
      // 64: aload 1
      // 65: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 6a: return
      // 6b: astore 4
      // 6d: return
      // 6e: astore 5
      // 70: aload 3
      // 71: ifnull 7d
      // 74: aload 3
      // 75: invokevirtual java/io/OutputStream.close ()V
      // 78: goto 7d
      // 7b: astore 6
      // 7d: aload 1
      // 7e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 83: goto 88
      // 86: astore 6
      // 88: aload 5
      // 8a: athrow
      // try (7 -> 11): 12 null
      // try (13 -> 15): 16 null
      // try (31 -> 35): 36 null
      // try (37 -> 39): 40 null
      // try (2 -> 7): 42 null
      // try (18 -> 31): 42 null
      // try (43 -> 47): 48 null
      // try (49 -> 51): 52 null
      // try (2 -> 7): 54 null
      // try (18 -> 31): 54 null
      // try (42 -> 43): 54 null
      // try (55 -> 59): 60 null
      // try (61 -> 63): 64 null
      // try (54 -> 55): 54 null
   }

   private void writeIgnoredFileListToMetaData(DataBuffer param1) {
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
      // 01: astore 2
      // 02: aload 0
      // 03: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._ignoreFileList [Ljava/lang/String;
      // 06: ifnull 59
      // 09: bipush 0
      // 0a: istore 3
      // 0b: iload 3
      // 0c: aload 0
      // 0d: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._ignoreFileList [Ljava/lang/String;
      // 10: arraylength
      // 11: if_icmpge 59
      // 14: aload 1
      // 15: aload 0
      // 16: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.FILE_NAME I
      // 19: aload 0
      // 1a: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._ignoreFileList [Ljava/lang/String;
      // 1d: iload 3
      // 1e: aaload
      // 1f: invokestatic net/rim/device/api/synchronization/ConverterUtilities.writeString (Lnet/rim/device/api/util/DataBuffer;ILjava/lang/String;)V
      // 22: new java/lang/Object
      // 25: dup
      // 26: ldc_w "file:///SDCard/blackberry/certificates/"
      // 29: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 2c: aload 0
      // 2d: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._ignoreFileList [Ljava/lang/String;
      // 30: iload 3
      // 31: aaload
      // 32: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 35: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 38: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 3b: checkcast java/lang/Object
      // 3e: astore 2
      // 3f: aload 1
      // 40: aload 0
      // 41: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry.FILE_LAST_MODIFIED I
      // 44: aload 2
      // 45: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 4a: invokestatic net/rim/device/api/synchronization/ConverterUtilities.writeLong (Lnet/rim/device/api/util/DataBuffer;IJ)V
      // 4d: aload 2
      // 4e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 53: iinc 3 1
      // 56: goto 0b
      // 59: aload 2
      // 5a: ifnull 88
      // 5d: aload 2
      // 5e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 63: return
      // 64: astore 3
      // 65: return
      // 66: astore 3
      // 67: aload 2
      // 68: ifnull 88
      // 6b: aload 2
      // 6c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 71: return
      // 72: astore 3
      // 73: return
      // 74: astore 4
      // 76: aload 2
      // 77: ifnull 85
      // 7a: aload 2
      // 7b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 80: goto 85
      // 83: astore 5
      // 85: aload 4
      // 87: athrow
      // 88: return
      // try (43 -> 47): 48 null
      // try (2 -> 43): 50 null
      // try (51 -> 55): 56 null
      // try (2 -> 43): 58 null
      // try (50 -> 51): 58 null
      // try (59 -> 63): 64 null
      // try (58 -> 59): 58 null
   }

   private boolean isIgnored(String param1) {
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
      // 00: bipush 0
      // 01: istore 2
      // 02: aload 0
      // 03: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._ignoreFileList [Ljava/lang/String;
      // 06: ifnonnull 0c
      // 09: goto b1
      // 0c: aload 0
      // 0d: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._ignoreFileList [Ljava/lang/String;
      // 10: arraylength
      // 11: istore 3
      // 12: aconst_null
      // 13: astore 4
      // 15: bipush 0
      // 16: istore 5
      // 18: iload 5
      // 1a: iload 3
      // 1b: if_icmpge b1
      // 1e: aload 0
      // 1f: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._ignoreFileList [Ljava/lang/String;
      // 22: iload 5
      // 24: aaload
      // 25: aload 1
      // 26: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 29: ifeq ab
      // 2c: new java/lang/Object
      // 2f: dup
      // 30: ldc_w "file:///SDCard/blackberry/certificates/"
      // 33: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 36: aload 1
      // 37: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 3d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 40: checkcast java/lang/Object
      // 43: astore 4
      // 45: aload 0
      // 46: getfield net/rim/device/apps/internal/keystore/browser/FileSystemContentTypeRegistry._ignoreFileListLastModified [J
      // 49: iload 5
      // 4b: laload
      // 4c: aload 4
      // 4e: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 53: lcmp
      // 54: ifne 6b
      // 57: bipush 1
      // 58: istore 2
      // 59: aload 4
      // 5b: ifnull b1
      // 5e: aload 4
      // 60: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 65: iload 2
      // 66: ireturn
      // 67: astore 6
      // 69: iload 2
      // 6a: ireturn
      // 6b: aload 4
      // 6d: ifnull ab
      // 70: aload 4
      // 72: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 77: goto ab
      // 7a: astore 6
      // 7c: goto ab
      // 7f: astore 6
      // 81: aload 4
      // 83: ifnull ab
      // 86: aload 4
      // 88: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 8d: goto ab
      // 90: astore 6
      // 92: goto ab
      // 95: astore 7
      // 97: aload 4
      // 99: ifnull a8
      // 9c: aload 4
      // 9e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // a3: goto a8
      // a6: astore 8
      // a8: aload 7
      // aa: athrow
      // ab: iinc 5 1
      // ae: goto 18
      // b1: iload 2
      // b2: ireturn
      // try (44 -> 48): 50 null
      // try (53 -> 57): 58 null
      // try (24 -> 44): 60 null
      // try (61 -> 65): 66 null
      // try (24 -> 44): 68 null
      // try (60 -> 61): 68 null
      // try (69 -> 73): 74 null
      // try (68 -> 69): 68 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean readMetaData(FileConnection metaData) {
      this._ignoreFileList = new Object[0];
      this._ignoreFileListLastModified = new long[0];
      String tempName = null;
      long tempTime = 0;
      boolean result = true;
      InputStream inStream = null;
      boolean var34 = false /* VF: Semaphore variable */;
      boolean var40 = false /* VF: Semaphore variable */;

      label277: {
         try {
            label275:
            try {
               var40 = true;
               var34 = true;
               inStream = metaData.openInputStream();
               int e = inStream.read();
               int size = inStream.available();
               byte[] data = new byte[size + 1];
               data[0] = (byte)e;
               inStream.read(data, 1, size);
               inStream.close();
               DataBuffer buffer = new Object(data, 0, data.length, true);
               int type = ConverterUtilities.getType((DataBuffer)buffer);
               if (type == this.FOLDER_LAST_MODIFIED) {
                  this._baseDirLastModified = ConverterUtilities.readLong((DataBuffer)buffer);
               }

               for (type = ConverterUtilities.getType((DataBuffer)buffer); type != this.END_METADATA; type = ConverterUtilities.getType((DataBuffer)buffer)) {
                  if (type == this.FILE_NAME) {
                     tempName = ConverterUtilities.readString((DataBuffer)buffer);
                  }

                  type = ConverterUtilities.getType((DataBuffer)buffer);
                  if (type == this.FILE_LAST_MODIFIED) {
                     tempTime = ConverterUtilities.readLong((DataBuffer)buffer);
                  }

                  if (tempName != null && tempTime != 0) {
                     Arrays.add(this._ignoreFileList, tempName);
                     Arrays.add(this._ignoreFileListLastModified, tempTime);
                     tempName = null;
                  }
               }

               var34 = false;
               var40 = false;
               break label277;
            } finally {
               if (var40) {
                  result = false;
                  var34 = false;
                  break label275;
               }
            }
         } finally {
            if (var34) {
               label259:
               try {
                  if (inStream != null) {
                     inStream.close();
                  }
               } finally {
                  break label259;
               }
            }
         }

         try {
            if (inStream != null) {
               inStream.close();
            }

            return result;
         } finally {
            return result;
         }
      }

      try {
         if (inStream != null) {
            inStream.close();
         }
      } finally {
         return result;
      }

      return result;
   }
}
