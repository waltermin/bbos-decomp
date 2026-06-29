package net.rim.device.cldc.io.file;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.util.StringMatch;

public final class Protocol implements ConnectionBaseInterface {
   private static StringMatch RELATIVE_PATHS = new StringMatch(new String[]{"/../", "/./", "../", "./"}, false, false);
   private static String SLASH_DOT_DOT = "/..";
   private static String SLASH_DOT = "/.";

   @Override
   public final int getProperties(String name) {
      return 256;
   }

   @Override
   public final Connection openPrim(String param1, int param2, boolean param3) throws FileIOException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ldc_w "UTF-8"
      // 04: bipush 0
      // 05: bipush 1
      // 06: invokestatic net/rim/device/cldc/io/utility/URIDecoder.decode (Ljava/lang/String;Ljava/lang/String;ZZ)Ljava/lang/String;
      // 09: astore 1
      // 0a: aload 1
      // 0b: ifnonnull 19
      // 0e: new java/lang/IllegalArgumentException
      // 11: dup
      // 12: ldc_w "Invalid escape sequence, URI must be encoded"
      // 15: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 18: athrow
      // 19: aload 0
      // 1a: aload 1
      // 1b: invokespecial net/rim/device/cldc/io/file/Protocol.verifyPathIsNonRelative (Ljava/lang/String;)V
      // 1e: aload 1
      // 1f: ldc_w "//"
      // 22: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 25: ifne 2b
      // 28: goto c1
      // 2b: aload 1
      // 2c: bipush 47
      // 2e: bipush 2
      // 30: invokevirtual java/lang/String.indexOf (II)I
      // 33: istore 4
      // 35: iload 4
      // 37: bipush -1
      // 39: if_icmpne 47
      // 3c: new net/rim/device/api/io/file/FileIOException
      // 3f: dup
      // 40: sipush 1003
      // 43: invokespecial net/rim/device/api/io/file/FileIOException.<init> (I)V
      // 46: athrow
      // 47: aload 1
      // 48: bipush 47
      // 4a: iload 4
      // 4c: bipush 1
      // 4d: iadd
      // 4e: invokevirtual java/lang/String.indexOf (II)I
      // 51: istore 6
      // 53: iload 6
      // 55: bipush -1
      // 57: if_icmpne 79
      // 5a: new java/lang/StringBuffer
      // 5d: dup
      // 5e: invokespecial java/lang/StringBuffer.<init> ()V
      // 61: aload 1
      // 62: iload 4
      // 64: bipush 1
      // 65: iadd
      // 66: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 69: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 6c: bipush 47
      // 6e: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 71: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 74: astore 5
      // 76: goto 87
      // 79: aload 1
      // 7a: iload 4
      // 7c: bipush 1
      // 7d: iadd
      // 7e: iload 6
      // 80: bipush 1
      // 81: iadd
      // 82: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 85: astore 5
      // 87: aload 5
      // 89: invokestatic net/rim/device/internal/io/file/FileSystem.getRoot (Ljava/lang/String;)Ljava/lang/Class;
      // 8c: astore 7
      // 8e: aload 7
      // 90: ifnull b6
      // 93: aload 7
      // 95: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 98: checkcast net/rim/device/internal/io/file/BaseFileConnection
      // 9b: astore 8
      // 9d: aload 8
      // 9f: aload 1
      // a0: iload 2
      // a1: iload 3
      // a2: invokeinterface net/rim/device/internal/io/file/BaseFileConnection.init (Ljava/lang/String;IZ)V 4
      // a7: aload 8
      // a9: areturn
      // aa: astore 8
      // ac: goto b6
      // af: astore 8
      // b1: goto b6
      // b4: astore 8
      // b6: new net/rim/device/api/io/file/FileIOException
      // b9: dup
      // ba: sipush 1003
      // bd: invokespecial net/rim/device/api/io/file/FileIOException.<init> (I)V
      // c0: athrow
      // c1: new net/rim/device/api/io/file/FileIOException
      // c4: dup
      // c5: sipush 1003
      // c8: invokespecial net/rim/device/api/io/file/FileIOException.<init> (I)V
      // cb: athrow
      // try (72 -> 82): 83 null
      // try (72 -> 82): 85 null
      // try (72 -> 82): 87 null
   }

   private final void verifyPathIsNonRelative(String name) {
      synchronized (RELATIVE_PATHS) {
         if (RELATIVE_PATHS.indexOf(name) != -1) {
            throw new IllegalArgumentException("Relative path");
         } else if (name.endsWith(SLASH_DOT_DOT) || name.endsWith(SLASH_DOT)) {
            throw new IllegalArgumentException("Relative path");
         }
      }
   }
}
