package net.rim.device.apps.internal.docview.core;

final class UCSDocumentData {
   private UCSParser _ucsParser = new UCSParser();
   private ArznClassFactory _classFactory = new ArznClassFactory();
   private ArznParsingInfo _parsingData;
   byte _parseStatus = 5;

   UCSDocumentData(ArznParseCallback parseItf) {
      this._parsingData = new ArznParsingInfo(parseItf);
   }

   final synchronized void parseDocument(DocViewInputStream param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 07
      // 04: goto e1
      // 07: aload 0
      // 08: bipush 4
      // 0a: putfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parseStatus B
      // 0d: aload 0
      // 0e: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parsingData Lnet/rim/device/apps/internal/docview/core/ArznParsingInfo;
      // 11: invokevirtual net/rim/device/apps/internal/docview/core/ArznParsingInfo.reset ()V
      // 14: aload 0
      // 15: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._ucsParser Lnet/rim/device/apps/internal/docview/core/UCSParser;
      // 18: aload 1
      // 19: invokevirtual net/rim/device/apps/internal/docview/core/UCSParser.set (Lnet/rim/device/apps/internal/docview/core/DocViewInputStream;)Z
      // 1c: bipush 1
      // 1d: if_icmpne c3
      // 20: ldc_w 65535
      // 23: istore 2
      // 24: bipush 0
      // 25: istore 3
      // 26: aload 0
      // 27: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parsingData Lnet/rim/device/apps/internal/docview/core/ArznParsingInfo;
      // 2a: invokevirtual net/rim/device/apps/internal/docview/core/ArznParsingInfo.getStopFlag ()B
      // 2d: tableswitch 27 0 2 42 35 27
      // 48: new net/rim/device/apps/internal/docview/core/ArznStopParsingException
      // 4b: dup
      // 4c: invokespecial net/rim/device/apps/internal/docview/core/ArznStopParsingException.<init> ()V
      // 4f: athrow
      // 50: aload 0
      // 51: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._ucsParser Lnet/rim/device/apps/internal/docview/core/UCSParser;
      // 54: invokevirtual net/rim/device/apps/internal/docview/core/UCSParser.waitForNotify ()V
      // 57: aload 0
      // 58: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._ucsParser Lnet/rim/device/apps/internal/docview/core/UCSParser;
      // 5b: invokevirtual net/rim/device/apps/internal/docview/core/UCSParser.readNextCommand ()V
      // 5e: aload 0
      // 5f: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._ucsParser Lnet/rim/device/apps/internal/docview/core/UCSParser;
      // 62: invokevirtual net/rim/device/apps/internal/docview/core/UCSParser.getCurrentCommandCode ()I
      // 65: istore 2
      // 66: aload 0
      // 67: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._ucsParser Lnet/rim/device/apps/internal/docview/core/UCSParser;
      // 6a: invokevirtual net/rim/device/apps/internal/docview/core/UCSParser.getCurrentCommandSize ()I
      // 6d: istore 3
      // 6e: iload 2
      // 6f: bipush 67
      // 71: if_icmpne 7c
      // 74: aload 0
      // 75: bipush 0
      // 76: putfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parseStatus B
      // 79: goto e7
      // 7c: aload 0
      // 7d: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._classFactory Lnet/rim/device/apps/internal/docview/core/ArznClassFactory;
      // 80: aload 0
      // 81: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._ucsParser Lnet/rim/device/apps/internal/docview/core/UCSParser;
      // 84: aconst_null
      // 85: invokevirtual net/rim/device/apps/internal/docview/core/ArznClassFactory.getObject (Lnet/rim/device/apps/internal/docview/core/UCSParser;Lnet/rim/device/apps/internal/docview/core/ArznObject;)Lnet/rim/device/apps/internal/docview/core/ArznObject;
      // 88: astore 4
      // 8a: aload 4
      // 8c: ifnull a9
      // 8f: aload 4
      // 91: aload 0
      // 92: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parsingData Lnet/rim/device/apps/internal/docview/core/ArznParsingInfo;
      // 95: invokevirtual net/rim/device/apps/internal/docview/core/ArznObject.parse (Lnet/rim/device/apps/internal/docview/core/ArznParsingInfo;)V
      // 98: aload 0
      // 99: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._classFactory Lnet/rim/device/apps/internal/docview/core/ArznClassFactory;
      // 9c: aload 4
      // 9e: aload 4
      // a0: invokevirtual net/rim/device/apps/internal/docview/core/ArznObject.getObjectType ()I
      // a3: invokevirtual net/rim/device/apps/internal/docview/core/ArznClassFactory.putObject (Ljava/lang/Object;I)V
      // a6: goto b1
      // a9: aload 0
      // aa: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._ucsParser Lnet/rim/device/apps/internal/docview/core/UCSParser;
      // ad: iload 3
      // ae: invokevirtual net/rim/device/apps/internal/docview/core/UCSParser.jumpCursor (I)V
      // b1: aload 0
      // b2: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parsingData Lnet/rim/device/apps/internal/docview/core/ArznParsingInfo;
      // b5: aload 0
      // b6: getfield net/rim/device/apps/internal/docview/core/UCSDocumentData._ucsParser Lnet/rim/device/apps/internal/docview/core/UCSParser;
      // b9: iload 2
      // ba: invokevirtual net/rim/device/apps/internal/docview/core/UCSParser.getAtomicIdentifer (I)B
      // bd: invokevirtual net/rim/device/apps/internal/docview/core/ArznParsingInfo.endCommandCodeParsing (B)V
      // c0: goto 26
      // c3: aload 0
      // c4: bipush 1
      // c5: putfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parseStatus B
      // c8: return
      // c9: astore 2
      // ca: aload 0
      // cb: bipush 2
      // cd: putfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parseStatus B
      // d0: return
      // d1: astore 2
      // d2: aload 0
      // d3: bipush 2
      // d5: putfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parseStatus B
      // d8: return
      // d9: astore 2
      // da: aload 0
      // db: bipush 3
      // dd: putfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parseStatus B
      // e0: return
      // e1: aload 0
      // e2: bipush 2
      // e4: putfield net/rim/device/apps/internal/docview/core/UCSDocumentData._parseStatus B
      // e7: return
      // try (9 -> 83): 84 null
      // try (9 -> 83): 89 null
      // try (9 -> 83): 94 net/rim/device/apps/internal/docview/core/ArznStopParsingException
   }

   final int getCurrentParsePercentage() {
      try {
         return this._ucsParser.getCurrentParsingPercentage();
      } finally {
         ;
      }
   }

   final void resumeParsing() {
      this._ucsParser.resumeParsing();
   }

   final int getMoreAvailableBytes() {
      try {
         return this._ucsParser.getMoreAvailableBytes();
      } finally {
         ;
      }
   }
}
