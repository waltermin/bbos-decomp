package net.rim.device.api.browser.push;

final class WAPPushSource$WAP1xRunThread extends Thread {
   private String _bearerString;
   private final WAPPushSource this$0;

   public WAPPushSource$WAP1xRunThread(WAPPushSource _1, String bearerString) {
      this.this$0 = _1;
      this._bearerString = bearerString;
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 004: dup
      // 005: astore 1
      // 006: monitorenter
      // 007: aload 0
      // 008: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 00b: getfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 00e: ifne 036
      // 011: aload 1
      // 012: monitorexit
      // 013: aload 0
      // 014: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 017: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 01a: ifnull 035
      // 01d: aload 0
      // 01e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 021: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 024: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 029: goto 02d
      // 02c: astore 2
      // 02d: aload 0
      // 02e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 031: aconst_null
      // 032: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 035: return
      // 036: aload 1
      // 037: monitorexit
      // 038: goto 040
      // 03b: astore 3
      // 03c: aload 1
      // 03d: monitorexit
      // 03e: aload 3
      // 03f: athrow
      // 040: aload 0
      // 041: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 044: new java/lang/Object
      // 047: dup
      // 048: ldc_w "waphttp://:"
      // 04b: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 04e: aload 0
      // 04f: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 052: getfield net/rim/device/api/browser/push/PushSource._port I
      // 055: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 058: ldc_w ";wapbearer="
      // 05b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 05e: aload 0
      // 05f: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread._bearerString Ljava/lang/String;
      // 062: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 065: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 068: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 06b: checkcast java/lang/Object
      // 06e: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 071: aload 0
      // 072: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 075: getfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 078: ifne 07e
      // 07b: goto 130
      // 07e: aload 0
      // 07f: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 082: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 085: invokeinterface javax/microedition/io/StreamConnectionNotifier.acceptAndOpen ()Ljavax/microedition/io/StreamConnection; 1
      // 08a: checkcast java/lang/Object
      // 08d: astore 1
      // 08e: aload 1
      // 08f: ifnonnull 0bd
      // 092: aload 0
      // 093: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 096: bipush 0
      // 097: putfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 09a: aload 0
      // 09b: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 09e: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 0a1: ifnull 0bc
      // 0a4: aload 0
      // 0a5: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 0a8: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 0ab: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0b0: goto 0b4
      // 0b3: astore 2
      // 0b4: aload 0
      // 0b5: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 0b8: aconst_null
      // 0b9: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 0bc: return
      // 0bd: new java/lang/Object
      // 0c0: dup
      // 0c1: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 0c4: astore 2
      // 0c5: aconst_null
      // 0c6: astore 3
      // 0c7: bipush 0
      // 0c8: istore 4
      // 0ca: aload 1
      // 0cb: iload 4
      // 0cd: invokeinterface net/rim/device/api/io/http/HttpServerConnection.getHeaderFieldKey (I)Ljava/lang/String; 2
      // 0d2: dup
      // 0d3: astore 3
      // 0d4: ifnull 0ea
      // 0d7: aload 2
      // 0d8: aload 3
      // 0d9: aload 1
      // 0da: iload 4
      // 0dc: invokeinterface net/rim/device/api/io/http/HttpServerConnection.getHeaderField (I)Ljava/lang/String; 2
      // 0e1: invokevirtual net/rim/device/api/io/http/HttpHeaders.setProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 0e4: iinc 4 1
      // 0e7: goto 0ca
      // 0ea: aload 0
      // 0eb: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 0ee: getfield net/rim/device/api/browser/push/WAPPushSource._listener Lnet/rim/device/api/browser/push/Pushlet;
      // 0f1: aload 2
      // 0f2: aload 1
      // 0f3: invokeinterface javax/microedition/io/InputConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 0f8: checkcast java/lang/Object
      // 0fb: invokeinterface net/rim/device/api/browser/push/Pushlet.messageReceived (Lnet/rim/device/api/io/http/HttpHeaders;Lnet/rim/device/api/io/http/PushInputStream;)V 3
      // 100: aload 1
      // 101: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 106: aconst_null
      // 107: astore 1
      // 108: goto 071
      // 10b: astore 4
      // 10d: ldc2_w -1133226195824034738
      // 110: new java/lang/Object
      // 113: dup
      // 114: ldc_w "PPce\n"
      // 117: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 11a: aload 4
      // 11c: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // 11f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 122: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 125: invokevirtual java/lang/String.getBytes ()[B
      // 128: bipush 0
      // 129: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 12c: pop
      // 12d: goto 071
      // 130: aload 0
      // 131: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 134: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 137: ifnonnull 13d
      // 13a: goto 000
      // 13d: aload 0
      // 13e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 141: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 144: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 149: goto 14d
      // 14c: astore 1
      // 14d: aload 0
      // 14e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 151: aconst_null
      // 152: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 155: goto 000
      // 158: astore 1
      // 159: aload 0
      // 15a: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 15d: dup
      // 15e: astore 2
      // 15f: monitorenter
      // 160: aload 0
      // 161: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 164: bipush 0
      // 165: putfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 168: aload 2
      // 169: monitorexit
      // 16a: goto 174
      // 16d: astore 5
      // 16f: aload 2
      // 170: monitorexit
      // 171: aload 5
      // 173: athrow
      // 174: aload 0
      // 175: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 178: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 17b: ifnull 196
      // 17e: aload 0
      // 17f: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 182: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 185: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 18a: goto 18e
      // 18d: astore 2
      // 18e: aload 0
      // 18f: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 192: aconst_null
      // 193: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 196: return
      // 197: astore 1
      // 198: aload 0
      // 199: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 19c: dup
      // 19d: astore 2
      // 19e: monitorenter
      // 19f: aload 0
      // 1a0: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1a3: bipush 0
      // 1a4: putfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 1a7: aload 2
      // 1a8: monitorexit
      // 1a9: goto 1b3
      // 1ac: astore 6
      // 1ae: aload 2
      // 1af: monitorexit
      // 1b0: aload 6
      // 1b2: athrow
      // 1b3: aload 0
      // 1b4: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1b7: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 1ba: ifnull 1d5
      // 1bd: aload 0
      // 1be: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1c1: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 1c4: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1c9: goto 1cd
      // 1cc: astore 2
      // 1cd: aload 0
      // 1ce: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1d1: aconst_null
      // 1d2: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 1d5: return
      // 1d6: astore 1
      // 1d7: aload 0
      // 1d8: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1db: dup
      // 1dc: astore 2
      // 1dd: monitorenter
      // 1de: aload 0
      // 1df: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1e2: getfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 1e5: ifne 20d
      // 1e8: aload 2
      // 1e9: monitorexit
      // 1ea: aload 0
      // 1eb: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1ee: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 1f1: ifnull 20c
      // 1f4: aload 0
      // 1f5: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1f8: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 1fb: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 200: goto 204
      // 203: astore 3
      // 204: aload 0
      // 205: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 208: aconst_null
      // 209: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 20c: return
      // 20d: aload 2
      // 20e: monitorexit
      // 20f: goto 219
      // 212: astore 7
      // 214: aload 2
      // 215: monitorexit
      // 216: aload 7
      // 218: athrow
      // 219: ldc2_w -1133226195824034738
      // 21c: new java/lang/Object
      // 21f: dup
      // 220: ldc_w "PPex\n"
      // 223: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 226: aload 1
      // 227: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // 22a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 22d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 230: invokevirtual java/lang/String.getBytes ()[B
      // 233: bipush 0
      // 234: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 237: pop
      // 238: aload 0
      // 239: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 23c: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 23f: ifnonnull 245
      // 242: goto 000
      // 245: aload 0
      // 246: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 249: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 24c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 251: goto 255
      // 254: astore 1
      // 255: aload 0
      // 256: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 259: aconst_null
      // 25a: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 25d: goto 000
      // 260: astore 8
      // 262: aload 0
      // 263: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 266: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 269: ifnull 285
      // 26c: aload 0
      // 26d: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 270: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 273: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 278: goto 27d
      // 27b: astore 9
      // 27d: aload 0
      // 27e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP1xRunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 281: aconst_null
      // 282: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 285: aload 8
      // 287: athrow
      // try (15 -> 19): 20 null
      // try (5 -> 11): 29 null
      // try (26 -> 28): 29 null
      // try (29 -> 32): 29 null
      // try (74 -> 78): 79 null
      // try (115 -> 119): 120 null
      // try (140 -> 144): 145 null
      // try (0 -> 11): 151 null
      // try (26 -> 70): 151 null
      // try (85 -> 135): 151 null
      // try (157 -> 163): 164 null
      // try (164 -> 167): 164 null
      // try (173 -> 177): 178 null
      // try (0 -> 11): 184 null
      // try (26 -> 70): 184 null
      // try (85 -> 135): 184 null
      // try (190 -> 196): 197 null
      // try (197 -> 200): 197 null
      // try (206 -> 210): 211 null
      // try (0 -> 11): 217 null
      // try (26 -> 70): 217 null
      // try (85 -> 135): 217 null
      // try (233 -> 237): 238 null
      // try (223 -> 229): 247 null
      // try (244 -> 246): 247 null
      // try (247 -> 250): 247 null
      // try (270 -> 274): 275 null
      // try (0 -> 11): 281 null
      // try (26 -> 70): 281 null
      // try (85 -> 135): 281 null
      // try (151 -> 169): 281 null
      // try (184 -> 202): 281 null
      // try (217 -> 229): 281 null
      // try (244 -> 265): 281 null
      // try (286 -> 290): 291 null
      // try (281 -> 282): 281 null
   }
}
