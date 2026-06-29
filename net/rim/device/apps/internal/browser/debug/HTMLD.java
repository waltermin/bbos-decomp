package net.rim.device.apps.internal.browser.debug;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Vector;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntStack;
import net.rim.device.apps.internal.browser.markup.HTMLUtilities;
import net.rim.device.apps.internal.browser.stack.WAPInputStream;

public final class HTMLD implements Decompiler {
   private ByteArrayOutputStream _outBuf = (ByteArrayOutputStream)(new Object());
   private PrintStream _out = (PrintStream)(new Object(this._outBuf));
   private int _depth;
   private int _currentStringRef;
   private boolean _allTagsProvided;
   private IntStack _tagStack;
   private IntStack _skipPopCalls;

   @Override
   public final String decompile(byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnull 009
      // 004: aload 1
      // 005: arraylength
      // 006: ifne 00d
      // 009: ldc_w ""
      // 00c: areturn
      // 00d: bipush 0
      // 00e: istore 2
      // 00f: aload 0
      // 010: bipush 0
      // 011: putfield net/rim/device/apps/internal/browser/debug/HTMLD._depth I
      // 014: aload 0
      // 015: bipush -1
      // 017: putfield net/rim/device/apps/internal/browser/debug/HTMLD._currentStringRef I
      // 01a: aload 0
      // 01b: bipush 0
      // 01c: putfield net/rim/device/apps/internal/browser/debug/HTMLD._allTagsProvided Z
      // 01f: aconst_null
      // 020: astore 3
      // 021: aconst_null
      // 022: astore 4
      // 024: new java/lang/Object
      // 027: dup
      // 028: new java/lang/Object
      // 02b: dup
      // 02c: aload 1
      // 02d: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 030: invokespecial net/rim/device/apps/internal/browser/stack/WAPInputStream.<init> (Ljava/io/InputStream;)V
      // 033: astore 9
      // 035: new java/lang/Object
      // 038: dup
      // 039: invokespecial java/util/Vector.<init> ()V
      // 03c: astore 10
      // 03e: new java/lang/Object
      // 041: dup
      // 042: invokespecial java/util/Vector.<init> ()V
      // 045: astore 11
      // 047: aload 0
      // 048: new java/lang/Object
      // 04b: dup
      // 04c: invokespecial net/rim/device/api/util/IntStack.<init> ()V
      // 04f: putfield net/rim/device/apps/internal/browser/debug/HTMLD._tagStack Lnet/rim/device/api/util/IntStack;
      // 052: aload 0
      // 053: new java/lang/Object
      // 056: dup
      // 057: invokespecial net/rim/device/api/util/IntStack.<init> ()V
      // 05a: putfield net/rim/device/apps/internal/browser/debug/HTMLD._skipPopCalls Lnet/rim/device/api/util/IntStack;
      // 05d: aload 9
      // 05f: invokevirtual java/io/DataInputStream.available ()I
      // 062: istore 2
      // 063: aload 0
      // 064: getfield net/rim/device/apps/internal/browser/debug/HTMLD._outBuf Ljava/io/ByteArrayOutputStream;
      // 067: invokevirtual java/io/ByteArrayOutputStream.reset ()V
      // 06a: goto 089
      // 06d: astore 12
      // 06f: aload 0
      // 070: new java/lang/Object
      // 073: dup
      // 074: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 077: putfield net/rim/device/apps/internal/browser/debug/HTMLD._outBuf Ljava/io/ByteArrayOutputStream;
      // 07a: aload 0
      // 07b: new java/lang/Object
      // 07e: dup
      // 07f: aload 0
      // 080: getfield net/rim/device/apps/internal/browser/debug/HTMLD._outBuf Ljava/io/ByteArrayOutputStream;
      // 083: invokespecial java/io/PrintStream.<init> (Ljava/io/OutputStream;)V
      // 086: putfield net/rim/device/apps/internal/browser/debug/HTMLD._out Ljava/io/PrintStream;
      // 089: aload 9
      // 08b: invokevirtual java/io/DataInputStream.read ()I
      // 08e: istore 12
      // 090: iload 12
      // 092: bipush 16
      // 094: if_icmpeq 0d4
      // 097: aload 0
      // 098: getfield net/rim/device/apps/internal/browser/debug/HTMLD._out Ljava/io/PrintStream;
      // 09b: new java/lang/Object
      // 09e: dup
      // 09f: ldc_w "ERROR: Wrong version numbers (got "
      // 0a2: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0a5: iload 12
      // 0a7: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 0aa: ldc_w ", expected "
      // 0ad: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b0: bipush 16
      // 0b2: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 0b5: bipush 41
      // 0b7: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0ba: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0bd: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 0c0: aload 0
      // 0c1: iload 2
      // 0c2: aload 9
      // 0c4: invokespecial net/rim/device/apps/internal/browser/debug/HTMLD.crashBurn (ILnet/rim/device/apps/internal/browser/stack/WAPInputStream;)V
      // 0c7: aload 0
      // 0c8: aconst_null
      // 0c9: putfield net/rim/device/apps/internal/browser/debug/HTMLD._tagStack Lnet/rim/device/api/util/IntStack;
      // 0cc: aload 0
      // 0cd: getfield net/rim/device/apps/internal/browser/debug/HTMLD._outBuf Ljava/io/ByteArrayOutputStream;
      // 0d0: invokevirtual java/io/ByteArrayOutputStream.toString ()Ljava/lang/String;
      // 0d3: areturn
      // 0d4: aload 9
      // 0d6: invokevirtual java/io/DataInputStream.readUnsignedByte ()I
      // 0d9: dup
      // 0da: istore 8
      // 0dc: ifeq 123
      // 0df: iload 8
      // 0e1: sipush 255
      // 0e4: if_icmpne 0f6
      // 0e7: aload 9
      // 0e9: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArray ()[B
      // 0ec: pop
      // 0ed: aload 9
      // 0ef: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArray ()[B
      // 0f2: pop
      // 0f3: goto 0d4
      // 0f6: iload 8
      // 0f8: bipush 2
      // 0fa: if_icmpne 11a
      // 0fd: aload 9
      // 0ff: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 102: pop
      // 103: aload 9
      // 105: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 108: istore 13
      // 10a: iload 13
      // 10c: bipush 4
      // 10e: iand
      // 10f: ifeq 0d4
      // 112: aload 0
      // 113: bipush 1
      // 114: putfield net/rim/device/apps/internal/browser/debug/HTMLD._allTagsProvided Z
      // 117: goto 0d4
      // 11a: aload 9
      // 11c: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArray ()[B
      // 11f: pop
      // 120: goto 0d4
      // 123: aload 9
      // 125: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 128: pop
      // 129: aload 0
      // 12a: getfield net/rim/device/apps/internal/browser/debug/HTMLD._allTagsProvided Z
      // 12d: ifne 137
      // 130: aload 0
      // 131: ldc_w "<html>"
      // 134: invokespecial net/rim/device/apps/internal/browser/debug/HTMLD.writeBeginTag (Ljava/lang/String;)V
      // 137: aload 9
      // 139: invokevirtual java/io/DataInputStream.available ()I
      // 13c: ifgt 142
      // 13f: goto 2a1
      // 142: aload 9
      // 144: invokevirtual java/io/DataInputStream.read ()I
      // 147: istore 8
      // 149: iload 8
      // 14b: bipush 1
      // 14c: if_icmpne 1d8
      // 14f: aload 9
      // 151: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 154: istore 6
      // 156: aload 9
      // 158: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 15b: dup
      // 15c: istore 5
      // 15e: ifle 194
      // 161: iload 5
      // 163: newarray 8
      // 165: astore 7
      // 167: aload 9
      // 169: aload 7
      // 16b: invokevirtual java/io/DataInputStream.read ([B)I
      // 16e: pop
      // 16f: iload 6
      // 171: invokestatic net/rim/device/apps/internal/browser/markup/HTMLBinaryConstants.resolveStringEncoding (I)Ljava/lang/String;
      // 174: astore 3
      // 175: new java/lang/Object
      // 178: dup
      // 179: aload 7
      // 17b: aload 3
      // 17c: invokespecial java/lang/String.<init> ([BLjava/lang/String;)V
      // 17f: astore 4
      // 181: goto 1a4
      // 184: astore 13
      // 186: new java/lang/Object
      // 189: dup
      // 18a: aload 7
      // 18c: invokespecial java/lang/String.<init> ([B)V
      // 18f: astore 4
      // 191: goto 1a4
      // 194: iload 5
      // 196: ifne 1a1
      // 199: ldc_w ""
      // 19c: astore 4
      // 19e: goto 1a4
      // 1a1: aconst_null
      // 1a2: astore 4
      // 1a4: aload 10
      // 1a6: aload 4
      // 1a8: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 1ab: aconst_null
      // 1ac: astore 7
      // 1ae: aload 0
      // 1af: getfield net/rim/device/apps/internal/browser/debug/HTMLD._allTagsProvided Z
      // 1b2: ifne 1ce
      // 1b5: aload 9
      // 1b7: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 1ba: dup
      // 1bb: istore 5
      // 1bd: ifle 1ce
      // 1c0: iload 5
      // 1c2: newarray 8
      // 1c4: astore 7
      // 1c6: aload 9
      // 1c8: aload 7
      // 1ca: invokevirtual java/io/DataInputStream.read ([B)I
      // 1cd: pop
      // 1ce: aload 11
      // 1d0: aload 7
      // 1d2: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 1d5: goto 137
      // 1d8: iload 8
      // 1da: bipush 2
      // 1dc: if_icmpne 20e
      // 1df: aload 0
      // 1e0: getfield net/rim/device/apps/internal/browser/debug/HTMLD._currentStringRef I
      // 1e3: bipush -1
      // 1e5: if_icmpeq 1f0
      // 1e8: aload 0
      // 1e9: aload 10
      // 1eb: aload 11
      // 1ed: invokespecial net/rim/device/apps/internal/browser/debug/HTMLD.flushCurrentText (Ljava/util/Vector;Ljava/util/Vector;)V
      // 1f0: aload 0
      // 1f1: aload 9
      // 1f3: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readMBInt ()I
      // 1f6: putfield net/rim/device/apps/internal/browser/debug/HTMLD._currentStringRef I
      // 1f9: aload 0
      // 1fa: getfield net/rim/device/apps/internal/browser/debug/HTMLD._allTagsProvided Z
      // 1fd: ifne 203
      // 200: goto 137
      // 203: aload 0
      // 204: aload 10
      // 206: aload 11
      // 208: invokespecial net/rim/device/apps/internal/browser/debug/HTMLD.flushCurrentText (Ljava/util/Vector;Ljava/util/Vector;)V
      // 20b: goto 137
      // 20e: iload 8
      // 210: sipush 255
      // 213: if_icmplt 226
      // 216: aload 9
      // 218: invokevirtual java/io/DataInputStream.read ()I
      // 21b: pop
      // 21c: aload 9
      // 21e: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArray ()[B
      // 221: astore 7
      // 223: goto 137
      // 226: iload 8
      // 228: ifeq 239
      // 22b: iload 8
      // 22d: bipush 4
      // 22f: if_icmpeq 239
      // 232: iload 8
      // 234: bipush 5
      // 236: if_icmplt 296
      // 239: iload 8
      // 23b: ifne 286
      // 23e: aload 0
      // 23f: getfield net/rim/device/apps/internal/browser/debug/HTMLD._skipPopCalls Lnet/rim/device/api/util/IntStack;
      // 242: invokevirtual net/rim/device/api/util/IntVector.isEmpty ()Z
      // 245: ifne 264
      // 248: aload 0
      // 249: getfield net/rim/device/apps/internal/browser/debug/HTMLD._skipPopCalls Lnet/rim/device/api/util/IntStack;
      // 24c: invokevirtual net/rim/device/api/util/IntStack.peek ()I
      // 24f: aload 0
      // 250: getfield net/rim/device/apps/internal/browser/debug/HTMLD._tagStack Lnet/rim/device/api/util/IntStack;
      // 253: invokevirtual net/rim/device/api/util/IntVector.size ()I
      // 256: if_icmpne 264
      // 259: aload 0
      // 25a: getfield net/rim/device/apps/internal/browser/debug/HTMLD._skipPopCalls Lnet/rim/device/api/util/IntStack;
      // 25d: invokevirtual net/rim/device/api/util/IntStack.pop ()I
      // 260: pop
      // 261: goto 137
      // 264: aload 0
      // 265: getfield net/rim/device/apps/internal/browser/debug/HTMLD._tagStack Lnet/rim/device/api/util/IntStack;
      // 268: invokevirtual net/rim/device/api/util/IntVector.isEmpty ()Z
      // 26b: ifeq 271
      // 26e: goto 137
      // 271: aload 0
      // 272: aload 0
      // 273: getfield net/rim/device/apps/internal/browser/debug/HTMLD._tagStack Lnet/rim/device/api/util/IntStack;
      // 276: invokevirtual net/rim/device/api/util/IntStack.pop ()I
      // 279: bipush 0
      // 27a: aload 9
      // 27c: aload 10
      // 27e: aload 11
      // 280: invokespecial net/rim/device/apps/internal/browser/debug/HTMLD.processTag (IZLnet/rim/device/apps/internal/browser/stack/WAPInputStream;Ljava/util/Vector;Ljava/util/Vector;)V
      // 283: goto 137
      // 286: aload 0
      // 287: iload 8
      // 289: bipush 1
      // 28a: aload 9
      // 28c: aload 10
      // 28e: aload 11
      // 290: invokespecial net/rim/device/apps/internal/browser/debug/HTMLD.processTag (IZLnet/rim/device/apps/internal/browser/stack/WAPInputStream;Ljava/util/Vector;Ljava/util/Vector;)V
      // 293: goto 137
      // 296: new java/lang/Object
      // 299: dup
      // 29a: ldc_w "Bad token"
      // 29d: invokespecial java/lang/Exception.<init> (Ljava/lang/String;)V
      // 2a0: athrow
      // 2a1: aload 0
      // 2a2: aload 10
      // 2a4: aload 11
      // 2a6: invokespecial net/rim/device/apps/internal/browser/debug/HTMLD.flushCurrentText (Ljava/util/Vector;Ljava/util/Vector;)V
      // 2a9: goto 2fe
      // 2ac: astore 10
      // 2ae: aload 0
      // 2af: getfield net/rim/device/apps/internal/browser/debug/HTMLD._out Ljava/io/PrintStream;
      // 2b2: aload 10
      // 2b4: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 2b7: aload 0
      // 2b8: iload 2
      // 2b9: aload 9
      // 2bb: invokespecial net/rim/device/apps/internal/browser/debug/HTMLD.crashBurn (ILnet/rim/device/apps/internal/browser/stack/WAPInputStream;)V
      // 2be: aload 0
      // 2bf: aconst_null
      // 2c0: putfield net/rim/device/apps/internal/browser/debug/HTMLD._tagStack Lnet/rim/device/api/util/IntStack;
      // 2c3: aload 0
      // 2c4: getfield net/rim/device/apps/internal/browser/debug/HTMLD._outBuf Ljava/io/ByteArrayOutputStream;
      // 2c7: invokevirtual java/io/ByteArrayOutputStream.toString ()Ljava/lang/String;
      // 2ca: areturn
      // 2cb: astore 10
      // 2cd: aload 0
      // 2ce: new java/lang/Object
      // 2d1: dup
      // 2d2: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 2d5: putfield net/rim/device/apps/internal/browser/debug/HTMLD._outBuf Ljava/io/ByteArrayOutputStream;
      // 2d8: aload 0
      // 2d9: new java/lang/Object
      // 2dc: dup
      // 2dd: aload 0
      // 2de: getfield net/rim/device/apps/internal/browser/debug/HTMLD._outBuf Ljava/io/ByteArrayOutputStream;
      // 2e1: invokespecial java/io/PrintStream.<init> (Ljava/io/OutputStream;)V
      // 2e4: putfield net/rim/device/apps/internal/browser/debug/HTMLD._out Ljava/io/PrintStream;
      // 2e7: aload 0
      // 2e8: getfield net/rim/device/apps/internal/browser/debug/HTMLD._out Ljava/io/PrintStream;
      // 2eb: ldc_w "Page too large to prepare html for"
      // 2ee: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 2f1: aload 0
      // 2f2: aconst_null
      // 2f3: putfield net/rim/device/apps/internal/browser/debug/HTMLD._tagStack Lnet/rim/device/api/util/IntStack;
      // 2f6: aload 0
      // 2f7: getfield net/rim/device/apps/internal/browser/debug/HTMLD._outBuf Ljava/io/ByteArrayOutputStream;
      // 2fa: invokevirtual java/io/ByteArrayOutputStream.toString ()Ljava/lang/String;
      // 2fd: areturn
      // 2fe: aload 0
      // 2ff: getfield net/rim/device/apps/internal/browser/debug/HTMLD._allTagsProvided Z
      // 302: ifne 30c
      // 305: aload 0
      // 306: ldc_w "</html>"
      // 309: invokespecial net/rim/device/apps/internal/browser/debug/HTMLD.writeEndTag (Ljava/lang/String;)V
      // 30c: aload 0
      // 30d: aconst_null
      // 30e: putfield net/rim/device/apps/internal/browser/debug/HTMLD._tagStack Lnet/rim/device/api/util/IntStack;
      // 311: aload 0
      // 312: getfield net/rim/device/apps/internal/browser/debug/HTMLD._outBuf Ljava/io/ByteArrayOutputStream;
      // 315: invokevirtual java/io/ByteArrayOutputStream.toString ()Ljava/lang/String;
      // 318: areturn
      // try (51 -> 54): 55 null
      // try (171 -> 180): 181 null
      // try (30 -> 100): 312 null
      // try (101 -> 311): 312 null
      // try (30 -> 100): 328 null
      // try (101 -> 311): 328 null
   }

   private final void flushCurrentText(Vector richTextStrings, Vector richTextProperties) {
      if (this._currentStringRef != -1) {
         byte[] propertiesData = (byte[])richTextProperties.elementAt(this._currentStringRef);
         if (propertiesData != null) {
            int regionCount = 0;
            DataBuffer properties = (DataBuffer)(new Object(propertiesData, 0, propertiesData.length, true));
            short var22;
            if (properties.readByte() == 1 && (var22 = properties.readShort()) > 0) {
               properties.readShort();
               int alignment = properties.readUnsignedByte();
               String parentPostfix = null;
               if (alignment != 10 && alignment != 8 && alignment == 9) {
                  this.writeBeginTag("<center>");
                  parentPostfix = "</center>";
               }

               String currentString = (String)richTextStrings.elementAt(this._currentStringRef);
               int[] attributes = new int[var22 + 1];
               int[] offsets = new int[var22 + 2];
               int[] anchors = new int[var22 + 1];
               int[] sizes = new int[var22 + 1];
               int[] foregroundColors = new int[var22 + 1];
               int[] backgroundColors = new int[var22 + 1];
               int[] fonts = new int[var22 + 1];
               int index = 0;

               for (int region = 0; region < var22; region++) {
                  index = properties.readCompressedInt();
                  int attribute = properties.readCompressedInt();
                  offsets[region] = index;
                  attributes[region] = attribute;
                  if ((attribute >> 3 & 1) != 0) {
                     anchors[region] = properties.readCompressedInt();
                  }

                  if ((attribute >> 4 & 1) != 0) {
                     sizes[region] = properties.readCompressedInt();
                  }

                  if ((attribute >> 9 & 1) != 0) {
                     foregroundColors[region] = properties.readCompressedInt();
                  }

                  if ((attribute >> 10 & 1) != 0) {
                     backgroundColors[region] = properties.readCompressedInt();
                  }

                  if ((attribute >> 11 & 1) != 0) {
                     fonts[region] = properties.readCompressedInt();
                  }
               }

               offsets[var22] = currentString.length();
               offsets[var22 + 1] = currentString.length();
               int currentAttribute = 0;
               int count = var22 + 1;

               for (int i = 0; i < count; i++) {
                  int nextAttribute = attributes[i] ^ currentAttribute;
                  if ((nextAttribute & 16) != 0 && (attributes[i] & 16) == 0) {
                     this.writeEndTag("</font>");
                  }

                  if ((nextAttribute & 256) != 0 && (attributes[i] & 256) == 0) {
                     this.writeEndTag("</sup>");
                  }

                  if ((nextAttribute & 128) != 0 && (attributes[i] & 128) == 0) {
                     this.writeEndTag("</sub>");
                  }

                  if ((nextAttribute & 64) != 0 && (attributes[i] & 64) == 0) {
                     this.writeEndTag("</tt>");
                  }

                  if ((nextAttribute & 32) != 0 && (attributes[i] & 32) == 0) {
                     this.writeEndTag("</strike>");
                  }

                  if ((nextAttribute & 8) != 0 && (attributes[i] & 8) == 0) {
                     this.writeEndTag("</a>");
                  }

                  if ((nextAttribute & 4) != 0 && (attributes[i] & 4) == 0) {
                     this.writeEndTag("</u>");
                  }

                  if ((nextAttribute & 2) != 0 && (attributes[i] & 2) == 0) {
                     this.writeEndTag("</i>");
                  }

                  if ((nextAttribute & 1) != 0 && (attributes[i] & 1) == 0) {
                     this.writeEndTag("</b>");
                  }

                  if ((nextAttribute & 1) != 0 && (attributes[i] & 1) != 0) {
                     this.writeBeginTag("<b>");
                  }

                  if ((nextAttribute & 2) != 0 && (attributes[i] & 2) != 0) {
                     this.writeBeginTag("<i>");
                  }

                  if ((nextAttribute & 4) != 0 && (attributes[i] & 4) != 0) {
                     this.writeBeginTag("<u>");
                  }

                  if ((nextAttribute & 8) != 0 && (attributes[i] & 8) != 0) {
                     this.writeBeginTag(
                        ((StringBuffer)(new Object("<a href=\""))).append((String)richTextStrings.elementAt(anchors[i])).append("\">").toString()
                     );
                  }

                  if ((nextAttribute & 32) != 0 && (attributes[i] & 32) != 0) {
                     this.writeBeginTag("<strike>");
                  }

                  if ((nextAttribute & 64) != 0 && (attributes[i] & 64) != 0) {
                     this.writeBeginTag("<tt>");
                  }

                  if ((nextAttribute & 128) != 0 && (attributes[i] & 128) != 0) {
                     this.writeBeginTag("<sub>");
                  }

                  if ((nextAttribute & 256) != 0 && (attributes[i] & 256) != 0) {
                     this.writeBeginTag("<sup>");
                  }

                  if ((nextAttribute & 16) != 0) {
                     if ((attributes[i] & 16) != 0) {
                        this.writeBeginTag(((StringBuffer)(new Object("<font size="))).append(sizes[i]).append('>').toString());
                     } else {
                        this.writeEndTag("</font>");
                     }
                  }

                  this.writeTag(currentString.substring(offsets[i], offsets[i + 1]));
                  currentAttribute = attributes[i];
               }

               if (parentPostfix != null) {
                  this.writeEndTag(parentPostfix);
               }
            }

            this._currentStringRef = -1;
            return;
         }

         this.writeTag((String)richTextStrings.elementAt(this._currentStringRef));
         this._currentStringRef = -1;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void crashBurn(int inSize, WAPInputStream in) {
      this._out.flush();
      this._out.println();

      try {
         this._out.println(((StringBuffer)(new Object("FATAL: Decompile halted at byte "))).append(inSize - in.available()).toString());
         in.close();
      } catch (Throwable var5) {
         this._out.println(e);
         return;
      }
   }

   private final String readAttributeValue(WAPInputStream in, Vector richTextStrings) {
      int properties = in.read();
      if ((properties & 1) == 1) {
         int tokenMask = properties & 14;
         if (tokenMask != 0) {
            int result = 0;
            switch (tokenMask) {
               case 8:
                  result |= in.readUnsignedByte();
                  result <<= 8;
               case 6:
                  result |= in.readUnsignedByte();
                  result <<= 8;
               case 4:
                  result |= in.readUnsignedByte();
                  result <<= 8;
               case 2:
                  result |= in.readUnsignedByte();
               default:
                  return Integer.toString(result);
            }
         } else {
            return (String)richTextStrings.elementAt(in.readMBInt());
         }
      } else {
         return "";
      }
   }

   private final void processTag(int tag, boolean isStartTag, WAPInputStream in, Vector richTextStrings, Vector richTextProperties) {
      if (tag == 17 || tag == 36 || tag == 50 || tag == 51 || tag == 87 || tag == 84 || tag == 86 || tag == 92) {
         this.flushCurrentText(richTextStrings, richTextProperties);
      }

      boolean hasAttributes = false;
      boolean hasContent = false;
      String currentTag = null;
      if (!isStartTag) {
         currentTag = HTMLUtilities.resolveTag(tag);
         if (tag == 91) {
            this.flushCurrentText(richTextStrings, richTextProperties);
         }

         this.writeEndTag(((StringBuffer)(new Object("</"))).append(currentTag).append('>').toString());
      } else {
         if (tag == 4) {
            int nameId = in.readMBInt();
            currentTag = (String)richTextStrings.elementAt(nameId);
         } else {
            currentTag = HTMLUtilities.resolveTag(tag);
         }

         int properties = in.read();
         hasAttributes = (properties & 1) == 1;
         hasContent = (properties & 2) >> 1 == 1;
         if ((properties & 4) >> 2 == 1) {
            in.readMBInt();
         }

         if (hasContent) {
            switch (tag) {
               case 10:
               case 12:
               case 13:
               case 18:
               case 24:
               case 37:
               case 46:
               case 50:
               case 51:
               case 53:
               case 58:
               case 61:
               case 69:
                  hasContent = false;
                  this._skipPopCalls.push(this._tagStack.size());
            }
         }

         String attributes = "";
         if (hasAttributes) {
            for (int currentByte = in.read(); currentByte != 0; currentByte = in.read()) {
               String name;
               if (currentByte == 4) {
                  name = (String)richTextStrings.elementAt(in.readMBInt());
               } else {
                  name = HTMLUtilities.resolveAttribute(currentByte);
               }

               if (name != null) {
                  attributes = ((StringBuffer)(new Object()))
                     .append(attributes)
                     .append(' ')
                     .append(name)
                     .append("=\"")
                     .append(this.readAttributeValue(in, richTextStrings))
                     .append('"')
                     .toString();
               } else {
                  name = HTMLUtilities.resolveAttributeValue(currentByte);
                  attributes = ((StringBuffer)(new Object())).append(attributes).append(' ').append(name).toString();
               }
            }
         }

         currentTag = ((StringBuffer)(new Object())).append('<').append(currentTag).append(attributes).toString();
         if (hasContent) {
            currentTag = ((StringBuffer)(new Object())).append(currentTag).append('>').toString();
            this._tagStack.push(tag);
            this.writeBeginTag(currentTag);
         } else {
            currentTag = ((StringBuffer)(new Object())).append(currentTag).append("/>").toString();
            this.writeTag(currentTag);
         }

         if (tag == 46) {
            this.flushCurrentText(richTextStrings, richTextProperties);
            return;
         }
      }
   }

   private final void writeBeginTag(String tag) {
      this.writeTag(tag);
      this._depth++;
   }

   private final void writeEndTag(String tag) {
      this._depth--;
      this.writeTag(tag);
   }

   private final void writeTag(String tag) {
      if (tag.length() != 0) {
         this._out.println();

         for (int i = 0; i < this._depth; i++) {
            this._out.print(' ');
         }

         int count = tag.length();

         for (int var6 = 0; var6 < count; var6++) {
            char currentChar = tag.charAt(var6);
            switch (currentChar) {
               case '\r':
                  if (var6 + 1 < count && tag.charAt(var6 + 1) == '\n') {
                     var6++;
                  }
               case '\n':
                  if (!this._allTagsProvided) {
                     this.writeTag("<br>");
                  }

                  if (var6 == count - 1) {
                     break;
                  }

                  this._out.println();

                  for (int j = 0; j < this._depth; j++) {
                     this._out.print(' ');
                  }
                  break;
               default:
                  this._out.print(currentChar);
            }
         }
      }
   }
}
