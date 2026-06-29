package net.rim.device.apps.internal.browser.util;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;

public final class PNGImageWriter extends Thread {
   private Bitmap _offscreen;
   private int _width;
   private int _height;
   private Graphics _graphics;
   private Manager _mgr;
   private String _url;
   private static final int CHUNK_OVERHEAD = 12;
   private static final int RIMPNG_IHDR_SIZE = 25;
   private static final int RIMPNG_IDAT_SIZE = 12;
   private static final int RIMPNG_IEND_SIZE = 12;
   private static final byte[] RIMPNG_SIG = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
   private static final byte[] CHUNK_IHDR = new byte[]{73, 72, 68, 82};
   private static final byte[] CHUNK_IDAT = new byte[]{73, 68, 65, 84};
   private static final byte[] CHUNK_IEND = new byte[]{73, 69, 78, 68};

   public PNGImageWriter(Manager mgr, String url) {
      this._width = mgr.getVirtualWidth();
      this._height = mgr.getVirtualHeight();
      this._offscreen = new Bitmap(this._width, Math.min(this._height, 240));
      this._graphics = new Graphics(this._offscreen);
      this._mgr = mgr;
      this._url = url;
   }

   @Override
   public final void run() {
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
      // 000: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 003: astore 1
      // 004: new net/rim/device/internal/ui/component/ProgressDialog
      // 007: dup
      // 008: ldc_w "Saving file"
      // 00b: invokespecial net/rim/device/internal/ui/component/ProgressDialog.<init> (Ljava/lang/String;)V
      // 00e: astore 2
      // 00f: aload 1
      // 010: new net/rim/device/apps/internal/browser/util/PNGImageWriter$1
      // 013: dup
      // 014: aload 0
      // 015: aload 2
      // 016: invokespecial net/rim/device/apps/internal/browser/util/PNGImageWriter$1.<init> (Lnet/rim/device/apps/internal/browser/util/PNGImageWriter;Lnet/rim/device/internal/ui/component/ProgressDialog;)V
      // 019: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 01c: aconst_null
      // 01d: astore 3
      // 01e: aconst_null
      // 01f: astore 4
      // 021: aconst_null
      // 022: astore 5
      // 024: aload 0
      // 025: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._url Ljava/lang/String;
      // 028: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 02b: checkcast javax/microedition/io/file/FileConnection
      // 02e: astore 3
      // 02f: aload 3
      // 030: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 035: ifeq 03e
      // 038: aload 3
      // 039: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 03e: aload 3
      // 03f: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 044: new net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream
      // 047: dup
      // 048: aload 3
      // 049: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 04e: invokespecial net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.<init> (Ljava/io/OutputStream;)V
      // 051: astore 6
      // 053: aload 6
      // 055: astore 4
      // 057: aload 6
      // 059: getstatic net/rim/device/apps/internal/browser/util/PNGImageWriter.RIMPNG_SIG [B
      // 05c: invokevirtual java/io/OutputStream.write ([B)V
      // 05f: aload 6
      // 061: bipush 13
      // 063: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 066: aload 6
      // 068: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.clearCRC ()V
      // 06b: aload 6
      // 06d: getstatic net/rim/device/apps/internal/browser/util/PNGImageWriter.CHUNK_IHDR [B
      // 070: invokevirtual java/io/OutputStream.write ([B)V
      // 073: aload 6
      // 075: aload 0
      // 076: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._width I
      // 079: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 07c: aload 6
      // 07e: aload 0
      // 07f: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._height I
      // 082: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 085: aload 6
      // 087: bipush 8
      // 089: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.write (I)V
      // 08c: aload 6
      // 08e: bipush 2
      // 090: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.write (I)V
      // 093: aload 6
      // 095: bipush 0
      // 096: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.write (I)V
      // 099: aload 6
      // 09b: bipush 0
      // 09c: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.write (I)V
      // 09f: aload 6
      // 0a1: bipush 0
      // 0a2: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.write (I)V
      // 0a5: aload 6
      // 0a7: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.writeCRC ()V
      // 0aa: aload 6
      // 0ac: bipush 0
      // 0ad: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 0b0: aload 6
      // 0b2: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.clearCRC ()V
      // 0b5: aload 6
      // 0b7: getstatic net/rim/device/apps/internal/browser/util/PNGImageWriter.CHUNK_IDAT [B
      // 0ba: invokevirtual java/io/OutputStream.write ([B)V
      // 0bd: new net/rim/device/api/compress/ZLibOutputStream
      // 0c0: dup
      // 0c1: aload 4
      // 0c3: bipush 0
      // 0c4: bipush 14
      // 0c6: bipush 6
      // 0c8: invokespecial net/rim/device/api/compress/ZLibOutputStream.<init> (Ljava/io/OutputStream;ZII)V
      // 0cb: astore 5
      // 0cd: aload 0
      // 0ce: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._offscreen Lnet/rim/device/api/system/Bitmap;
      // 0d1: invokevirtual net/rim/device/api/system/Bitmap.getHeight ()I
      // 0d4: istore 7
      // 0d6: bipush 0
      // 0d7: istore 8
      // 0d9: aload 0
      // 0da: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._width I
      // 0dd: newarray 10
      // 0df: astore 9
      // 0e1: aload 0
      // 0e2: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._width I
      // 0e5: bipush 3
      // 0e7: imul
      // 0e8: newarray 8
      // 0ea: astore 10
      // 0ec: bipush 0
      // 0ed: istore 11
      // 0ef: new net/rim/device/apps/internal/browser/util/PNGImageWriter$UpdateProgress
      // 0f2: dup
      // 0f3: aload 2
      // 0f4: invokespecial net/rim/device/apps/internal/browser/util/PNGImageWriter$UpdateProgress.<init> (Lnet/rim/device/internal/ui/component/ProgressDialog;)V
      // 0f7: astore 12
      // 0f9: iload 8
      // 0fb: aload 0
      // 0fc: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._height I
      // 0ff: if_icmplt 105
      // 102: goto 1de
      // 105: aload 0
      // 106: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._graphics Lnet/rim/device/api/ui/Graphics;
      // 109: bipush 0
      // 10a: bipush 0
      // 10b: aload 0
      // 10c: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._width I
      // 10f: iload 7
      // 111: bipush 0
      // 112: iload 8
      // 114: ineg
      // 115: invokevirtual net/rim/device/api/ui/Graphics.pushContext (IIIIII)Z
      // 118: pop
      // 119: aload 0
      // 11a: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._graphics Lnet/rim/device/api/ui/Graphics;
      // 11d: invokevirtual net/rim/device/api/ui/Graphics.clear ()V
      // 120: aload 0
      // 121: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._graphics Lnet/rim/device/api/ui/Graphics;
      // 124: aload 0
      // 125: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._mgr Lnet/rim/device/api/ui/Manager;
      // 128: invokestatic net/rim/device/api/ui/PNGImageWriterManagerHack.printContent (Lnet/rim/device/api/ui/Graphics;Lnet/rim/device/api/ui/Manager;)V
      // 12b: aload 0
      // 12c: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._graphics Lnet/rim/device/api/ui/Graphics;
      // 12f: invokevirtual net/rim/device/api/ui/Graphics.popContext ()V
      // 132: bipush 0
      // 133: istore 13
      // 135: iload 13
      // 137: iload 7
      // 139: if_icmpge 0f9
      // 13c: iload 8
      // 13e: aload 0
      // 13f: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._height I
      // 142: if_icmpge 0f9
      // 145: aload 0
      // 146: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._offscreen Lnet/rim/device/api/system/Bitmap;
      // 149: aload 9
      // 14b: bipush 0
      // 14c: aload 9
      // 14e: arraylength
      // 14f: bipush 0
      // 150: iload 13
      // 152: aload 0
      // 153: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._width I
      // 156: bipush 1
      // 157: invokevirtual net/rim/device/api/system/Bitmap.getARGB ([IIIIIII)V
      // 15a: bipush 0
      // 15b: istore 14
      // 15d: bipush 0
      // 15e: istore 15
      // 160: iload 15
      // 162: aload 0
      // 163: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._width I
      // 166: if_icmpge 19f
      // 169: aload 10
      // 16b: iload 14
      // 16d: iinc 14 1
      // 170: aload 9
      // 172: iload 15
      // 174: iaload
      // 175: bipush 16
      // 177: ishr
      // 178: i2b
      // 179: bastore
      // 17a: aload 10
      // 17c: iload 14
      // 17e: iinc 14 1
      // 181: aload 9
      // 183: iload 15
      // 185: iaload
      // 186: bipush 8
      // 188: ishr
      // 189: i2b
      // 18a: bastore
      // 18b: aload 10
      // 18d: iload 14
      // 18f: iinc 14 1
      // 192: aload 9
      // 194: iload 15
      // 196: iaload
      // 197: i2b
      // 198: bastore
      // 199: iinc 15 1
      // 19c: goto 160
      // 19f: aload 5
      // 1a1: bipush 0
      // 1a2: invokevirtual net/rim/device/api/compress/ZLibOutputStream.write (I)V
      // 1a5: aload 5
      // 1a7: aload 10
      // 1a9: invokevirtual java/io/OutputStream.write ([B)V
      // 1ac: aload 5
      // 1ae: invokevirtual net/rim/device/api/compress/ZLibOutputStream.flush ()V
      // 1b1: iinc 8 1
      // 1b4: iload 8
      // 1b6: bipush 100
      // 1b8: imul
      // 1b9: aload 0
      // 1ba: getfield net/rim/device/apps/internal/browser/util/PNGImageWriter._height I
      // 1bd: idiv
      // 1be: istore 15
      // 1c0: iload 11
      // 1c2: iload 15
      // 1c4: if_icmpeq 1d8
      // 1c7: iload 15
      // 1c9: istore 11
      // 1cb: aload 12
      // 1cd: iload 15
      // 1cf: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$UpdateProgress.setProgress (I)V
      // 1d2: aload 1
      // 1d3: aload 12
      // 1d5: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 1d8: iinc 13 1
      // 1db: goto 135
      // 1de: aload 6
      // 1e0: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.writeCRC ()V
      // 1e3: aload 6
      // 1e5: bipush 0
      // 1e6: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 1e9: aload 6
      // 1eb: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.clearCRC ()V
      // 1ee: aload 6
      // 1f0: getstatic net/rim/device/apps/internal/browser/util/PNGImageWriter.CHUNK_IEND [B
      // 1f3: invokevirtual java/io/OutputStream.write ([B)V
      // 1f6: aload 6
      // 1f8: invokevirtual net/rim/device/apps/internal/browser/util/PNGImageWriter$CRC32OutputStream.writeCRC ()V
      // 1fb: aload 4
      // 1fd: ifnull 20a
      // 200: aload 6
      // 202: invokevirtual java/io/DataOutputStream.close ()V
      // 205: goto 20a
      // 208: astore 13
      // 20a: new java/io/DataOutputStream
      // 20d: dup
      // 20e: aload 3
      // 20f: bipush 33
      // 211: i2l
      // 212: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream (J)Ljava/io/OutputStream; 3
      // 217: invokespecial java/io/DataOutputStream.<init> (Ljava/io/OutputStream;)V
      // 21a: astore 4
      // 21c: aload 4
      // 21e: aload 3
      // 21f: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 224: l2i
      // 225: bipush 57
      // 227: isub
      // 228: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 22b: aload 4
      // 22d: ifnull 23a
      // 230: aload 4
      // 232: invokevirtual java/io/DataOutputStream.close ()V
      // 235: goto 23a
      // 238: astore 6
      // 23a: aload 3
      // 23b: ifnull 249
      // 23e: aload 3
      // 23f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 244: goto 249
      // 247: astore 6
      // 249: aload 5
      // 24b: ifnull 2bf
      // 24e: aload 5
      // 250: invokevirtual net/rim/device/api/compress/ZLibOutputStream.close ()V
      // 253: goto 2bf
      // 256: astore 6
      // 258: goto 2bf
      // 25b: astore 6
      // 25d: aload 4
      // 25f: ifnull 26c
      // 262: aload 4
      // 264: invokevirtual java/io/DataOutputStream.close ()V
      // 267: goto 26c
      // 26a: astore 6
      // 26c: aload 3
      // 26d: ifnull 27b
      // 270: aload 3
      // 271: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 276: goto 27b
      // 279: astore 6
      // 27b: aload 5
      // 27d: ifnull 2bf
      // 280: aload 5
      // 282: invokevirtual net/rim/device/api/compress/ZLibOutputStream.close ()V
      // 285: goto 2bf
      // 288: astore 6
      // 28a: goto 2bf
      // 28d: astore 16
      // 28f: aload 4
      // 291: ifnull 29e
      // 294: aload 4
      // 296: invokevirtual java/io/DataOutputStream.close ()V
      // 299: goto 29e
      // 29c: astore 17
      // 29e: aload 3
      // 29f: ifnull 2ad
      // 2a2: aload 3
      // 2a3: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2a8: goto 2ad
      // 2ab: astore 17
      // 2ad: aload 5
      // 2af: ifnull 2bc
      // 2b2: aload 5
      // 2b4: invokevirtual net/rim/device/api/compress/ZLibOutputStream.close ()V
      // 2b7: goto 2bc
      // 2ba: astore 17
      // 2bc: aload 16
      // 2be: athrow
      // 2bf: aload 1
      // 2c0: new net/rim/device/apps/internal/browser/util/PNGImageWriter$2
      // 2c3: dup
      // 2c4: aload 0
      // 2c5: aload 2
      // 2c6: invokespecial net/rim/device/apps/internal/browser/util/PNGImageWriter$2.<init> (Lnet/rim/device/apps/internal/browser/util/PNGImageWriter;Lnet/rim/device/internal/ui/component/ProgressDialog;)V
      // 2c9: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 2cc: return
      // try (245 -> 247): 248 null
      // try (266 -> 268): 269 null
      // try (272 -> 274): 275 null
      // try (278 -> 280): 281 null
      // try (20 -> 264): 283 null
      // try (286 -> 288): 289 null
      // try (292 -> 294): 295 null
      // try (298 -> 300): 301 null
      // try (20 -> 264): 303 null
      // try (283 -> 284): 303 null
      // try (306 -> 308): 309 null
      // try (312 -> 314): 315 null
      // try (318 -> 320): 321 null
      // try (303 -> 304): 303 null
   }
}
