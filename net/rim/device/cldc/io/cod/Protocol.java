package net.rim.device.cldc.io.cod;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.Array;

public final class Protocol implements ConnectionBaseInterface, HttpConnection {
   private Pipe _pipe;
   private String _type;
   private String _file;
   private String _name;
   private int _responseCode;
   private boolean _gzipped;

   @Override
   public final void close() {
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: Constructor net/rim/device/internal/browser/util/Pipe.<init>([[BZ)V not found
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExprUtil.getSyntheticParametersMask(ExprUtil.java:49)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:982)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:462)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.AssignmentExprent.toJava(AssignmentExprent.java:161)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:238)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:238)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:238)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:251)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1351)
      //
      // Bytecode:
      // 000: bipush 1
      // 001: invokestatic net/rim/device/api/system/ControlledAccess.verifyRRISignatures (Z)Z
      // 004: pop
      // 005: aload 1
      // 006: bipush 59
      // 008: invokevirtual java/lang/String.indexOf (I)I
      // 00b: istore 4
      // 00d: iload 4
      // 00f: bipush -1
      // 011: if_icmpeq 01c
      // 014: aload 1
      // 015: bipush 0
      // 016: iload 4
      // 018: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 01b: astore 1
      // 01c: aload 0
      // 01d: aload 1
      // 01e: putfield net/rim/device/cldc/io/cod/Protocol._name Ljava/lang/String;
      // 021: new java/lang/Object
      // 024: dup
      // 025: aload 1
      // 026: ldc_w "/"
      // 029: invokespecial net/rim/device/api/util/StringTokenizer.<init> (Ljava/lang/String;Ljava/lang/String;)V
      // 02c: astore 5
      // 02e: aload 5
      // 030: invokevirtual net/rim/device/api/util/StringTokenizer.nextToken ()Ljava/lang/String;
      // 033: astore 6
      // 035: aload 0
      // 036: aload 5
      // 038: ldc_w ";?#"
      // 03b: invokevirtual net/rim/device/api/util/StringTokenizer.nextToken (Ljava/lang/String;)Ljava/lang/String;
      // 03e: putfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 041: aload 6
      // 043: ifnull 05a
      // 046: aload 0
      // 047: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 04a: ifnull 05a
      // 04d: aload 0
      // 04e: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 051: ldc_w "/"
      // 054: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 057: ifeq 073
      // 05a: aload 0
      // 05b: sipush 404
      // 05e: putfield net/rim/device/cldc/io/cod/Protocol._responseCode I
      // 061: aload 0
      // 062: new java/lang/Object
      // 065: dup
      // 066: bipush 0
      // 067: newarray 8
      // 069: bipush 0
      // 06a: bipush 0
      // 06b: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([BIZ)V
      // 06e: putfield net/rim/device/cldc/io/cod/Protocol._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 071: aload 0
      // 072: areturn
      // 073: aload 6
      // 075: ifnull 07b
      // 078: goto 0f7
      // 07b: aload 0
      // 07c: ldc_w "text/html"
      // 07f: putfield net/rim/device/cldc/io/cod/Protocol._type Ljava/lang/String;
      // 082: new java/lang/Object
      // 085: dup
      // 086: ldc_w "<html><body>"
      // 089: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 08c: astore 7
      // 08e: invokestatic net/rim/device/api/system/CodeModuleManager.getModuleHandles ()[I
      // 091: astore 8
      // 093: aload 8
      // 095: arraylength
      // 096: istore 9
      // 098: bipush 0
      // 099: istore 10
      // 09b: iload 10
      // 09d: iload 9
      // 09f: if_icmpge 0d1
      // 0a2: aload 8
      // 0a4: iload 10
      // 0a6: iaload
      // 0a7: invokestatic net/rim/device/api/system/CodeModuleManager.getModuleName (I)Ljava/lang/String;
      // 0aa: astore 11
      // 0ac: aload 7
      // 0ae: ldc_w "<a href='"
      // 0b1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b4: aload 11
      // 0b6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b9: ldc_w "'>"
      // 0bc: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0bf: aload 11
      // 0c1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0c4: ldc_w "</a><br/>"
      // 0c7: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0ca: pop
      // 0cb: iinc 10 1
      // 0ce: goto 09b
      // 0d1: aload 7
      // 0d3: ldc_w "</body></html>"
      // 0d6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0d9: pop
      // 0da: aload 7
      // 0dc: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0df: invokevirtual java/lang/String.getBytes ()[B
      // 0e2: astore 10
      // 0e4: aload 0
      // 0e5: new java/lang/Object
      // 0e8: dup
      // 0e9: aload 10
      // 0eb: aload 10
      // 0ed: arraylength
      // 0ee: bipush 0
      // 0ef: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([BIZ)V
      // 0f2: putfield net/rim/device/cldc/io/cod/Protocol._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 0f5: aload 0
      // 0f6: areturn
      // 0f7: aload 0
      // 0f8: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 0fb: ifnull 10e
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 102: ldc_w "/"
      // 105: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 108: ifne 10e
      // 10b: goto 1ae
      // 10e: aload 0
      // 10f: ldc_w "text/html"
      // 112: putfield net/rim/device/cldc/io/cod/Protocol._type Ljava/lang/String;
      // 115: new java/lang/Object
      // 118: dup
      // 119: ldc_w "<html><body>"
      // 11c: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 11f: astore 7
      // 121: aload 6
      // 123: invokestatic net/rim/device/resources/Resource$Internal.getResourceClass (Ljava/lang/String;)Lnet/rim/device/resources/Resource;
      // 126: astore 8
      // 128: aload 8
      // 12a: invokevirtual net/rim/device/resources/Resource.getResourceKeys ()Ljava/util/Enumeration;
      // 12d: astore 9
      // 12f: aload 9
      // 131: ifnull 188
      // 134: aload 9
      // 136: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 13b: ifeq 188
      // 13e: aload 9
      // 140: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 145: checkcast java/lang/Object
      // 148: astore 10
      // 14a: aload 10
      // 14c: bipush 46
      // 14e: invokevirtual java/lang/String.lastIndexOf (I)I
      // 151: istore 11
      // 153: iload 11
      // 155: bipush -1
      // 157: if_icmpeq 134
      // 15a: aload 10
      // 15c: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 15f: astore 12
      // 161: aload 12
      // 163: ifnull 134
      // 166: aload 7
      // 168: ldc_w "<a href='"
      // 16b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 16e: aload 10
      // 170: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 173: ldc_w "'>"
      // 176: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 179: aload 10
      // 17b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 17e: ldc_w "</a><br/>"
      // 181: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 184: pop
      // 185: goto 134
      // 188: aload 7
      // 18a: ldc_w "</body></html>"
      // 18d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 190: pop
      // 191: aload 7
      // 193: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 196: invokevirtual java/lang/String.getBytes ()[B
      // 199: astore 10
      // 19b: aload 0
      // 19c: new java/lang/Object
      // 19f: dup
      // 1a0: aload 10
      // 1a2: aload 10
      // 1a4: arraylength
      // 1a5: bipush 0
      // 1a6: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([BIZ)V
      // 1a9: putfield net/rim/device/cldc/io/cod/Protocol._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 1ac: aload 0
      // 1ad: areturn
      // 1ae: aload 0
      // 1af: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 1b2: ldc_w "/"
      // 1b5: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 1b8: ifeq 1c7
      // 1bb: aload 0
      // 1bc: aload 0
      // 1bd: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 1c0: bipush 1
      // 1c1: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 1c4: putfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 1c7: aload 6
      // 1c9: invokestatic net/rim/device/resources/Resource$Internal.getResourceClass (Ljava/lang/String;)Lnet/rim/device/resources/Resource;
      // 1cc: astore 7
      // 1ce: aload 7
      // 1d0: ifnonnull 1d6
      // 1d3: goto 287
      // 1d6: aload 0
      // 1d7: aload 6
      // 1d9: aload 0
      // 1da: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 1dd: invokespecial net/rim/device/cldc/io/cod/Protocol.getResourceSegments (Ljava/lang/String;Ljava/lang/String;)[[[B
      // 1e0: astore 8
      // 1e2: aload 8
      // 1e4: ifnonnull 214
      // 1e7: aload 0
      // 1e8: aload 6
      // 1ea: new java/lang/Object
      // 1ed: dup
      // 1ee: invokespecial java/lang/StringBuffer.<init> ()V
      // 1f1: aload 0
      // 1f2: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 1f5: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1f8: ldc_w ".gz"
      // 1fb: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1fe: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 201: invokespecial net/rim/device/cldc/io/cod/Protocol.getResourceSegments (Ljava/lang/String;Ljava/lang/String;)[[[B
      // 204: astore 8
      // 206: aload 0
      // 207: aload 8
      // 209: ifnull 210
      // 20c: bipush 1
      // 20d: goto 211
      // 210: bipush 0
      // 211: putfield net/rim/device/cldc/io/cod/Protocol._gzipped Z
      // 214: aload 8
      // 216: ifnull 22a
      // 219: aload 0
      // 21a: new java/lang/Object
      // 21d: dup
      // 21e: aload 8
      // 220: bipush 0
      // 221: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([[BZ)V
      // 224: putfield net/rim/device/cldc/io/cod/Protocol._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 227: goto 287
      // 22a: aload 7
      // 22c: aload 0
      // 22d: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 230: invokevirtual net/rim/device/resources/Resource.getResource (Ljava/lang/String;)[B
      // 233: astore 9
      // 235: aload 9
      // 237: ifnull 24e
      // 23a: aload 0
      // 23b: new java/lang/Object
      // 23e: dup
      // 23f: aload 9
      // 241: aload 9
      // 243: arraylength
      // 244: bipush 0
      // 245: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([BIZ)V
      // 248: putfield net/rim/device/cldc/io/cod/Protocol._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 24b: goto 287
      // 24e: aload 7
      // 250: new java/lang/Object
      // 253: dup
      // 254: invokespecial java/lang/StringBuffer.<init> ()V
      // 257: aload 0
      // 258: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 25b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 25e: ldc_w ".gz"
      // 261: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 264: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 267: invokevirtual net/rim/device/resources/Resource.getResource (Ljava/lang/String;)[B
      // 26a: astore 9
      // 26c: aload 9
      // 26e: ifnull 287
      // 271: aload 0
      // 272: new java/lang/Object
      // 275: dup
      // 276: aload 9
      // 278: aload 9
      // 27a: arraylength
      // 27b: bipush 0
      // 27c: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([BIZ)V
      // 27f: putfield net/rim/device/cldc/io/cod/Protocol._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 282: aload 0
      // 283: bipush 1
      // 284: putfield net/rim/device/cldc/io/cod/Protocol._gzipped Z
      // 287: aload 0
      // 288: getfield net/rim/device/cldc/io/cod/Protocol._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 28b: ifnonnull 2a7
      // 28e: aload 0
      // 28f: sipush 404
      // 292: putfield net/rim/device/cldc/io/cod/Protocol._responseCode I
      // 295: aload 0
      // 296: new java/lang/Object
      // 299: dup
      // 29a: bipush 0
      // 29b: newarray 8
      // 29d: bipush 0
      // 29e: bipush 0
      // 29f: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([BIZ)V
      // 2a2: putfield net/rim/device/cldc/io/cod/Protocol._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 2a5: aload 0
      // 2a6: areturn
      // 2a7: aload 0
      // 2a8: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 2ab: bipush 46
      // 2ad: invokevirtual java/lang/String.lastIndexOf (I)I
      // 2b0: istore 8
      // 2b2: iload 8
      // 2b4: bipush -1
      // 2b6: if_icmpne 2c2
      // 2b9: aload 0
      // 2ba: sipush 400
      // 2bd: putfield net/rim/device/cldc/io/cod/Protocol._responseCode I
      // 2c0: aload 0
      // 2c1: areturn
      // 2c2: aload 0
      // 2c3: aload 0
      // 2c4: getfield net/rim/device/cldc/io/cod/Protocol._file Ljava/lang/String;
      // 2c7: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 2ca: putfield net/rim/device/cldc/io/cod/Protocol._type Ljava/lang/String;
      // 2cd: aload 0
      // 2ce: sipush 200
      // 2d1: putfield net/rim/device/cldc/io/cod/Protocol._responseCode I
      // 2d4: aload 0
      // 2d5: areturn
   }

   @Override
   public final String getType() {
      return this._type;
   }

   @Override
   public final String getEncoding() {
      return null;
   }

   @Override
   public final long getLength() {
      return this._gzipped ? -1 : this._pipe.getLength();
   }

   @Override
   public final InputStream openInputStream() {
      InputStream is = this._pipe.getInputStream();
      return (InputStream)(this._gzipped ? new Object(is) : is);
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return (DataInputStream)(new Object(this.openInputStream()));
   }

   @Override
   public final OutputStream openOutputStream() {
      throw new Object("Read only");
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      throw new Object("Read only");
   }

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final String getURL() {
      return ((StringBuffer)(new Object("cod:"))).append(this._name).toString();
   }

   @Override
   public final String getProtocol() {
      return "cod";
   }

   @Override
   public final String getHost() {
      return "";
   }

   @Override
   public final String getFile() {
      return this._file;
   }

   @Override
   public final String getRef() {
      int fragmentIdentifier = this._name.indexOf(35);
      return fragmentIdentifier != -1 ? this._name.substring(fragmentIdentifier + 1) : null;
   }

   @Override
   public final String getQuery() {
      return null;
   }

   @Override
   public final int getPort() {
      return 80;
   }

   @Override
   public final String getRequestMethod() {
      return "GET";
   }

   @Override
   public final void setRequestMethod(String method) {
   }

   @Override
   public final String getRequestProperty(String key) {
      return null;
   }

   @Override
   public final void setRequestProperty(String key, String value) {
   }

   @Override
   public final int getResponseCode() {
      return this._responseCode;
   }

   @Override
   public final String getResponseMessage() {
      return this._responseCode == 200 ? "OK" : "Error";
   }

   @Override
   public final long getExpiration() {
      return 0;
   }

   @Override
   public final long getDate() {
      return 0;
   }

   @Override
   public final long getLastModified() {
      return 0;
   }

   @Override
   public final String getHeaderField(String name) {
      return null;
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      return def;
   }

   @Override
   public final long getHeaderFieldDate(String name, long def) {
      return def;
   }

   @Override
   public final String getHeaderField(int n) {
      return n == 0 ? this._type : null;
   }

   @Override
   public final String getHeaderFieldKey(int n) {
      return n == 0 && this._type != null ? "Content-Type" : null;
   }

   private final byte[][][] getResourceSegments(String module, String name) {
      int moduleHandle = CodeModuleManager.getModuleHandle(module);
      byte[][][] result = new byte[0][][];
      int offset = 0;
      StringBuffer nameBuffer = (StringBuffer)(new Object());

      while (true) {
         nameBuffer.setLength(0);
         nameBuffer.append('_').append('_').append(name).append('@').append(offset);
         byte[] more = Resource$Internal.getResource(nameBuffer.toString(), moduleHandle);
         if (more == null) {
            return (byte[][][])(result.length > 0 ? result : (byte[][])null);
         }

         Array.resize(result, result.length + 1);
         result[result.length - 1] = (byte[][])more;
         offset += more.length;
      }
   }
}
