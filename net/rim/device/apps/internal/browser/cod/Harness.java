package net.rim.device.apps.internal.browser.cod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.tools.compiler.Host;
import net.rim.tools.jar.JarEntry;
import net.rim.tools.jar.JarInputStream;
import net.rim.tools.jar.Manifest;
import net.rim.vm.ClassInfo;

final class Harness implements Host, JARCompiler {
   private ByteArrayOutputStream _bout;
   private PrintStream _pout;
   private StringBuffer _diagnostics = new StringBuffer();
   private int _progressState;
   private String _name;
   private int _jarSize;
   private InputStream _jarFile;
   private Vector _outputStreams = new Vector();
   private ApplicationDownloadListener _listener;
   private int _resultCode;
   private String _error;
   private byte[] _signerCertEncoding;
   private static final boolean DEBUGCOD = false;

   public Harness() {
   }

   @Override
   public final String extractManifest(InputStream inputStream) {
      String result = "";
      JarInputStream jarStream = new JarInputStream(inputStream, false);
      Manifest manifest = jarStream.getManifest();
      if (manifest != null) {
         result = manifest.getString();
      } else {
         JarEntry entry = null;

         while ((entry = jarStream.getNextJarEntry()) != null) {
            String entryName = entry.getName();
            int nameLen = entryName.length();
            if (nameLen == 20 && entryName.regionMatches(true, 0, "META-INF/MANIFEST.MF", 0, nameLen)) {
               manifest = new Manifest(jarStream);
               result = manifest.getString();
               jarStream.closeEntry();
               break;
            }

            jarStream.closeEntry();
         }
      }

      jarStream.close();
      return result;
   }

   @Override
   public final Vector compile(String param1, String param2, InputStream param3, int param4, ApplicationDownloadListener param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: putfield net/rim/device/apps/internal/browser/cod/Harness._name Ljava/lang/String;
      // 005: aload 0
      // 006: aload 3
      // 007: putfield net/rim/device/apps/internal/browser/cod/Harness._jarFile Ljava/io/InputStream;
      // 00a: aload 0
      // 00b: iload 4
      // 00d: putfield net/rim/device/apps/internal/browser/cod/Harness._jarSize I
      // 010: aload 0
      // 011: aload 5
      // 013: putfield net/rim/device/apps/internal/browser/cod/Harness._listener Lnet/rim/device/apps/internal/browser/cod/ApplicationDownloadListener;
      // 016: aconst_null
      // 017: astore 6
      // 019: bipush 1
      // 01a: istore 7
      // 01c: new net/rim/tools/compiler/util/CompilerProperties
      // 01f: dup
      // 020: invokespecial net/rim/tools/compiler/util/CompilerProperties.<init> ()V
      // 023: astore 8
      // 025: aload 8
      // 027: ldc_w "quiet"
      // 02a: ldc_w "1"
      // 02d: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 030: pop
      // 031: aload 8
      // 033: ldc_w "midlet"
      // 036: ldc_w "1"
      // 039: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 03c: pop
      // 03d: new java/util/Vector
      // 040: dup
      // 041: invokespecial java/util/Vector.<init> ()V
      // 044: astore 9
      // 046: aload 8
      // 048: ldc_w "rapc_jarFiles"
      // 04b: aload 9
      // 04d: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 050: pop
      // 051: aload 8
      // 053: ldc_w "rapc_resourceBinaries"
      // 056: new java/util/Vector
      // 059: dup
      // 05a: invokespecial java/util/Vector.<init> ()V
      // 05d: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 060: pop
      // 061: aload 8
      // 063: ldc_w "jadString"
      // 066: aload 2
      // 067: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 06a: pop
      // 06b: aload 9
      // 06d: new java/lang/StringBuffer
      // 070: dup
      // 071: invokespecial java/lang/StringBuffer.<init> ()V
      // 074: aload 0
      // 075: getfield net/rim/device/apps/internal/browser/cod/Harness._name Ljava/lang/String;
      // 078: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 07b: ldc_w ".jar"
      // 07e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 081: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 084: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 087: aload 8
      // 089: ldc_w "codename"
      // 08c: aload 0
      // 08d: getfield net/rim/device/apps/internal/browser/cod/Harness._name Ljava/lang/String;
      // 090: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 093: pop
      // 094: aload 0
      // 095: aload 8
      // 097: invokestatic net/rim/tools/compiler/Compiler.compile (Ljava/lang/Object;Lnet/rim/tools/compiler/util/CompilerProperties;)Lnet/rim/tools/compiler/Compiler;
      // 09a: astore 6
      // 09c: aload 0
      // 09d: getfield net/rim/device/apps/internal/browser/cod/Harness._pout Ljava/io/PrintStream;
      // 0a0: ifnonnull 0a6
      // 0a3: goto 16e
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/apps/internal/browser/cod/Harness._pout Ljava/io/PrintStream;
      // 0aa: invokevirtual java/io/PrintStream.close ()V
      // 0ad: aload 0
      // 0ae: aconst_null
      // 0af: putfield net/rim/device/apps/internal/browser/cod/Harness._pout Ljava/io/PrintStream;
      // 0b2: aload 0
      // 0b3: getfield net/rim/device/apps/internal/browser/cod/Harness._bout Ljava/io/ByteArrayOutputStream;
      // 0b6: invokevirtual java/io/ByteArrayOutputStream.toString ()Ljava/lang/String;
      // 0b9: astore 10
      // 0bb: aload 0
      // 0bc: aconst_null
      // 0bd: putfield net/rim/device/apps/internal/browser/cod/Harness._bout Ljava/io/ByteArrayOutputStream;
      // 0c0: aload 10
      // 0c2: ifnonnull 0c8
      // 0c5: goto 16e
      // 0c8: aload 10
      // 0ca: invokevirtual java/lang/String.length ()I
      // 0cd: ifgt 0d3
      // 0d0: goto 16e
      // 0d3: aload 0
      // 0d4: getfield net/rim/device/apps/internal/browser/cod/Harness._diagnostics Ljava/lang/StringBuffer;
      // 0d7: aload 10
      // 0d9: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0dc: pop
      // 0dd: aload 0
      // 0de: getfield net/rim/device/apps/internal/browser/cod/Harness._diagnostics Ljava/lang/StringBuffer;
      // 0e1: bipush 10
      // 0e3: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0e6: pop
      // 0e7: goto 16e
      // 0ea: astore 8
      // 0ec: aload 8
      // 0ee: invokevirtual net/rim/tools/compiler/util/CompileException.getMessage ()Ljava/lang/String;
      // 0f1: astore 9
      // 0f3: aload 9
      // 0f5: ifnull 11a
      // 0f8: aload 9
      // 0fa: invokevirtual java/lang/String.length ()I
      // 0fd: ifle 11a
      // 100: aload 0
      // 101: getfield net/rim/device/apps/internal/browser/cod/Harness._diagnostics Ljava/lang/StringBuffer;
      // 104: aload 9
      // 106: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 109: pop
      // 10a: aload 0
      // 10b: getfield net/rim/device/apps/internal/browser/cod/Harness._diagnostics Ljava/lang/StringBuffer;
      // 10e: bipush 10
      // 110: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 113: pop
      // 114: aload 0
      // 115: aload 9
      // 117: putfield net/rim/device/apps/internal/browser/cod/Harness._error Ljava/lang/String;
      // 11a: bipush 0
      // 11b: istore 7
      // 11d: aload 0
      // 11e: aload 8
      // 120: invokevirtual net/rim/tools/compiler/util/CompileException.getResultCode ()I
      // 123: putfield net/rim/device/apps/internal/browser/cod/Harness._resultCode I
      // 126: goto 16e
      // 129: astore 8
      // 12b: aload 8
      // 12d: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 130: astore 9
      // 132: aload 9
      // 134: ifnull 159
      // 137: aload 9
      // 139: invokevirtual java/lang/String.length ()I
      // 13c: ifle 159
      // 13f: aload 0
      // 140: getfield net/rim/device/apps/internal/browser/cod/Harness._diagnostics Ljava/lang/StringBuffer;
      // 143: aload 9
      // 145: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 148: pop
      // 149: aload 0
      // 14a: getfield net/rim/device/apps/internal/browser/cod/Harness._diagnostics Ljava/lang/StringBuffer;
      // 14d: bipush 10
      // 14f: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 152: pop
      // 153: aload 0
      // 154: aload 9
      // 156: putfield net/rim/device/apps/internal/browser/cod/Harness._error Ljava/lang/String;
      // 159: bipush 0
      // 15a: istore 7
      // 15c: goto 16e
      // 15f: astore 8
      // 161: aload 0
      // 162: sipush 588
      // 165: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 168: putfield net/rim/device/apps/internal/browser/cod/Harness._error Ljava/lang/String;
      // 16b: bipush 0
      // 16c: istore 7
      // 16e: iload 7
      // 170: ifne 17b
      // 173: aload 0
      // 174: getfield net/rim/device/apps/internal/browser/cod/Harness._outputStreams Ljava/util/Vector;
      // 177: bipush 0
      // 178: invokevirtual java/util/Vector.setSize (I)V
      // 17b: aload 0
      // 17c: getfield net/rim/device/apps/internal/browser/cod/Harness._outputStreams Ljava/util/Vector;
      // 17f: invokevirtual java/util/Vector.size ()I
      // 182: istore 8
      // 184: bipush 0
      // 185: istore 9
      // 187: iload 9
      // 189: iload 8
      // 18b: if_icmpge 1b4
      // 18e: aload 0
      // 18f: getfield net/rim/device/apps/internal/browser/cod/Harness._outputStreams Ljava/util/Vector;
      // 192: iload 9
      // 194: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 197: checkcast java/io/ByteArrayOutputStream
      // 19a: astore 10
      // 19c: aload 10
      // 19e: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 1a1: astore 11
      // 1a3: aload 0
      // 1a4: getfield net/rim/device/apps/internal/browser/cod/Harness._outputStreams Ljava/util/Vector;
      // 1a7: aload 11
      // 1a9: iload 9
      // 1ab: invokevirtual java/util/Vector.setElementAt (Ljava/lang/Object;I)V
      // 1ae: iinc 9 1
      // 1b1: goto 187
      // 1b4: aload 6
      // 1b6: ifnull 1ca
      // 1b9: aload 6
      // 1bb: invokevirtual net/rim/tools/compiler/Compiler.getRequiresSigning ()Z
      // 1be: ifeq 1ca
      // 1c1: aload 6
      // 1c3: aload 0
      // 1c4: getfield net/rim/device/apps/internal/browser/cod/Harness._outputStreams Ljava/util/Vector;
      // 1c7: invokevirtual net/rim/tools/compiler/Compiler.performSigning (Ljava/util/Vector;)V
      // 1ca: aload 6
      // 1cc: ifnull 1d8
      // 1cf: aload 0
      // 1d0: aload 6
      // 1d2: invokevirtual net/rim/tools/compiler/Compiler.getSignerCertEncoding ()[B
      // 1d5: putfield net/rim/device/apps/internal/browser/cod/Harness._signerCertEncoding [B
      // 1d8: aload 0
      // 1d9: getfield net/rim/device/apps/internal/browser/cod/Harness._outputStreams Ljava/util/Vector;
      // 1dc: areturn
      // try (16 -> 106): 107 null
      // try (16 -> 106): 136 null
      // try (16 -> 106): 161 null
   }

   @Override
   public final int getResultCode() {
      return this._resultCode;
   }

   @Override
   public final String getError() {
      return this._error;
   }

   @Override
   public final byte[] getSignerCertEncoding() {
      return this._signerCertEncoding;
   }

   @Override
   public final PrintStream openDiagnose() {
      if (this._pout == null) {
         this._bout = new ByteArrayOutputStream();
         this._pout = new PrintStream(this._bout);
      }

      return this._pout;
   }

   @Override
   public final InputStream openInput(String name) throws IOException {
      if (name.endsWith(".jar")) {
         return this._jarFile;
      } else {
         throw new IOException("file not found: " + name);
      }
   }

   @Override
   public final OutputStream openOutput(String name) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      this._outputStreams.addElement(out);
      return out;
   }

   @Override
   public final void advanceProgress(int progress) {
      if (progress >= 0) {
         Asserts.productionStateAssert(progress == this._progressState);
         this._progressState = progress + 1;
      }

      switch (progress) {
         case -2:
            Asserts.productionArgumentAssert(false);
         case -1:
            return;
         case 0:
         default:
            this._listener.progressUpdate(this._jarSize * 2, 1);
            return;
         case 1:
            this._listener.progressUpdate(this._jarSize * 3, 1);
            return;
         case 2:
            this._listener.progressUpdate(this._jarSize * 4, 1);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object getClassInfo(String classFullName) {
      Class clazz = null;

      try {
         return Class.forName(classFullName);
      } catch (Throwable var6) {
         String msg = e.getMessage();
         if (msg != null && msg.length() > 0) {
            this._diagnostics.append(msg);
            this._diagnostics.append('\n');
         }

         return clazz;
      }
   }

   @Override
   public final String getModuleName(Object classInfo) {
      Class clazz = (Class)classInfo;
      return ClassInfo.getModuleInfo(clazz, 0, true);
   }

   @Override
   public final String getModuleVersion(Object classInfo) {
      Class clazz = (Class)classInfo;
      return ClassInfo.getModuleInfo(clazz, 0, false);
   }

   @Override
   public final int getClassAttributes(Object classInfo) {
      Class clazz = (Class)classInfo;
      return ClassInfo.getClassFlags(clazz);
   }
}
