package net.rim.device.apps.internal.browser.core;

final class BrowserImpl$NavigationRequest implements Runnable {
   private int _requestType;
   private Object _context;
   private final BrowserImpl this$0;

   BrowserImpl$NavigationRequest(BrowserImpl _1, int task, Object context) {
      this.this$0 = _1;
      this._context = context;
      this._requestType = task;
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
      // 000: aconst_null
      // 001: astore 1
      // 002: aload 0
      // 003: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._requestType I
      // 006: tableswitch 66 0 12 1045 66 153 187 221 277 326 376 393 121 834 976 1012
      // 048: aload 0
      // 049: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 04c: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 04f: new net/rim/device/apps/internal/browser/core/MenuShower
      // 052: dup
      // 053: aload 0
      // 054: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 057: aload 0
      // 058: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._context Ljava/lang/Object;
      // 05b: dup
      // 05c: instanceof java/lang/Integer
      // 05f: ifne 066
      // 062: pop
      // 063: goto 06f
      // 066: checkcast java/lang/Integer
      // 069: invokevirtual java/lang/Integer.intValue ()I
      // 06c: goto 070
      // 06f: bipush 0
      // 070: aconst_null
      // 071: invokespecial net/rim/device/apps/internal/browser/core/MenuShower.<init> (Lnet/rim/device/apps/internal/browser/core/BrowserImpl;ILnet/rim/device/apps/api/framework/verb/Verb;)V
      // 074: astore 1
      // 075: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 078: aload 1
      // 079: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 07c: goto 41b
      // 07f: aload 0
      // 080: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 083: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 086: new net/rim/device/apps/internal/browser/core/MenuShower
      // 089: dup
      // 08a: aload 0
      // 08b: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 08e: bipush 0
      // 08f: aconst_null
      // 090: bipush 1
      // 091: invokespecial net/rim/device/apps/internal/browser/core/MenuShower.<init> (Lnet/rim/device/apps/internal/browser/core/BrowserImpl;ILnet/rim/device/apps/api/framework/verb/Verb;Z)V
      // 094: astore 1
      // 095: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 098: aload 1
      // 099: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 09c: goto 41b
      // 09f: aload 0
      // 0a0: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0a3: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0aa: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.followLinkUnderCursor ()V
      // 0ad: aload 0
      // 0ae: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0b1: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 0b4: goto 41b
      // 0b7: astore 2
      // 0b8: aload 0
      // 0b9: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0bc: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 0bf: aload 2
      // 0c0: athrow
      // 0c1: aload 0
      // 0c2: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0c5: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 0c8: aload 0
      // 0c9: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0cc: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.closeBrowserInternal ()V
      // 0cf: aload 0
      // 0d0: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0d3: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 0d6: goto 41b
      // 0d9: astore 3
      // 0da: aload 0
      // 0db: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0de: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 0e1: aload 3
      // 0e2: athrow
      // 0e3: aload 0
      // 0e4: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._context Ljava/lang/Object;
      // 0e7: dup
      // 0e8: instanceof net/rim/device/apps/internal/browser/stack/FetchRequest
      // 0eb: ifne 0f2
      // 0ee: pop
      // 0ef: goto 41b
      // 0f2: checkcast net/rim/device/apps/internal/browser/stack/FetchRequest
      // 0f5: astore 2
      // 0f6: aload 0
      // 0f7: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0fa: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 0fd: aload 0
      // 0fe: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 101: aload 2
      // 102: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.initiateFetchRequestInternal (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)V
      // 105: aload 0
      // 106: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 109: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 10c: goto 41b
      // 10f: astore 4
      // 111: aload 0
      // 112: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 115: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 118: aload 4
      // 11a: athrow
      // 11b: aload 0
      // 11c: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._context Ljava/lang/Object;
      // 11f: instanceof net/rim/device/apps/internal/browser/stack/FetchRequest
      // 122: ifne 128
      // 125: goto 41b
      // 128: aload 0
      // 129: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 12c: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 12f: aload 0
      // 130: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 133: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.abortCurrentRequest ()V
      // 136: aload 0
      // 137: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 13a: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 13d: goto 41b
      // 140: astore 5
      // 142: aload 0
      // 143: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 146: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 149: aload 5
      // 14b: athrow
      // 14c: aload 0
      // 14d: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._context Ljava/lang/Object;
      // 150: dup
      // 151: instanceof net/rim/device/apps/api/framework/verb/Verb
      // 154: ifne 15b
      // 157: pop
      // 158: goto 41b
      // 15b: checkcast net/rim/device/apps/api/framework/verb/Verb
      // 15e: astore 2
      // 15f: aload 0
      // 160: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 163: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 166: new net/rim/device/apps/internal/browser/core/MenuShower
      // 169: dup
      // 16a: aload 0
      // 16b: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 16e: bipush 0
      // 16f: aload 2
      // 170: invokespecial net/rim/device/apps/internal/browser/core/MenuShower.<init> (Lnet/rim/device/apps/internal/browser/core/BrowserImpl;ILnet/rim/device/apps/api/framework/verb/Verb;)V
      // 173: astore 1
      // 174: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 177: aload 1
      // 178: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 17b: goto 41b
      // 17e: aload 0
      // 17f: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 182: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 185: aload 0
      // 186: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 189: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.pushDebugScreen ()V
      // 18c: goto 41b
      // 18f: aload 0
      // 190: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 193: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 196: aload 0
      // 197: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 19a: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserExecutionState ()I
      // 19d: istore 2
      // 19e: aconst_null
      // 19f: astore 3
      // 1a0: iload 2
      // 1a1: bipush 5
      // 1a3: if_icmpeq 1fe
      // 1a6: iload 2
      // 1a7: ifeq 1fe
      // 1aa: aload 0
      // 1ab: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 1ae: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._verbRepository Lnet/rim/device/apps/internal/browser/core/BrowserVerbRepository;
      // 1b1: bipush 16
      // 1b3: aload 0
      // 1b4: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 1b7: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getVerbMask ()I
      // 1ba: invokevirtual net/rim/device/apps/internal/browser/core/BrowserVerbRepository.getVerb (II)Lnet/rim/device/apps/internal/browser/verbs/BrowserVerb;
      // 1bd: astore 3
      // 1be: aload 3
      // 1bf: ifnonnull 1c5
      // 1c2: goto 332
      // 1c5: aload 3
      // 1c6: aconst_null
      // 1c7: invokevirtual net/rim/device/apps/api/framework/verb/Verb.invoke (Ljava/lang/Object;)Ljava/lang/Object;
      // 1ca: pop
      // 1cb: aload 0
      // 1cc: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 1cf: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getCurrentPage ()Lnet/rim/device/apps/internal/browser/page/Page;
      // 1d2: instanceof net/rim/device/apps/internal/browser/page/SplashPage
      // 1d5: ifne 1db
      // 1d8: goto 332
      // 1db: aload 0
      // 1dc: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 1df: aload 0
      // 1e0: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._context Ljava/lang/Object;
      // 1e3: dup
      // 1e4: instanceof java/lang/Boolean
      // 1e7: ifne 1ee
      // 1ea: pop
      // 1eb: goto 1f7
      // 1ee: checkcast java/lang/Boolean
      // 1f1: invokevirtual java/lang/Boolean.booleanValue ()Z
      // 1f4: goto 1f8
      // 1f7: bipush 0
      // 1f8: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.handleEscapeOnLastPage (Z)V
      // 1fb: goto 332
      // 1fe: aload 0
      // 1ff: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 202: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 205: ifnull 263
      // 208: aload 0
      // 209: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 20c: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 20f: invokevirtual net/rim/device/apps/internal/browser/page/Page.getBrowserContent ()Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 212: ifnull 263
      // 215: aload 0
      // 216: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 219: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 21c: invokevirtual net/rim/device/apps/internal/browser/page/Page.getBrowserContent ()Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 21f: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.getContentManager ()Lnet/rim/device/api/ui/Manager;
      // 222: astore 4
      // 224: aload 4
      // 226: dup
      // 227: instanceof net/rim/device/apps/internal/browser/ui/TextFlowManager
      // 22a: ifne 231
      // 22d: pop
      // 22e: goto 263
      // 231: checkcast net/rim/device/apps/internal/browser/ui/TextFlowManager
      // 234: astore 5
      // 236: aload 5
      // 238: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.canZoomOut ()Z
      // 23b: ifeq 263
      // 23e: invokestatic net/rim/device/api/system/Application.getEventLock ()Ljava/lang/Object;
      // 241: dup
      // 242: astore 6
      // 244: monitorenter
      // 245: aload 5
      // 247: ldc_w 2147483647
      // 24a: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.adjustZoom (I)V
      // 24d: aload 6
      // 24f: monitorexit
      // 250: goto 25b
      // 253: astore 7
      // 255: aload 6
      // 257: monitorexit
      // 258: aload 7
      // 25a: athrow
      // 25b: aload 0
      // 25c: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 25f: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 262: return
      // 263: aload 0
      // 264: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 267: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 26a: ifnull 278
      // 26d: aload 0
      // 26e: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 271: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 274: invokevirtual net/rim/device/apps/internal/browser/page/Page.getPrevVerb ()Lnet/rim/device/apps/api/framework/verb/Verb;
      // 277: astore 3
      // 278: aload 0
      // 279: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 27c: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentSession Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 27f: ifnull 292
      // 282: aload 0
      // 283: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 286: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentSession Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 289: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.getHistory ()Lnet/rim/device/apps/internal/browser/history/History;
      // 28c: invokevirtual net/rim/device/apps/internal/browser/history/History.canGoBack ()Z
      // 28f: goto 293
      // 292: bipush 0
      // 293: istore 4
      // 295: aload 3
      // 296: ifnonnull 2e0
      // 299: iload 4
      // 29b: ifeq 2b8
      // 29e: aload 0
      // 29f: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 2a2: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._verbRepository Lnet/rim/device/apps/internal/browser/core/BrowserVerbRepository;
      // 2a5: bipush 4
      // 2a7: aload 0
      // 2a8: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 2ab: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getVerbMask ()I
      // 2ae: invokevirtual net/rim/device/apps/internal/browser/core/BrowserVerbRepository.getVerb (II)Lnet/rim/device/apps/internal/browser/verbs/BrowserVerb;
      // 2b1: checkcast net/rim/device/apps/internal/browser/verbs/HistoryBackVerb
      // 2b4: astore 3
      // 2b5: goto 2e0
      // 2b8: aload 0
      // 2b9: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 2bc: aload 0
      // 2bd: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._context Ljava/lang/Object;
      // 2c0: dup
      // 2c1: instanceof java/lang/Boolean
      // 2c4: ifne 2cb
      // 2c7: pop
      // 2c8: goto 2d4
      // 2cb: checkcast java/lang/Boolean
      // 2ce: invokevirtual java/lang/Boolean.booleanValue ()Z
      // 2d1: goto 2d5
      // 2d4: bipush 0
      // 2d5: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.handleEscapeOnLastPage (Z)V
      // 2d8: aload 0
      // 2d9: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 2dc: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 2df: return
      // 2e0: aload 3
      // 2e1: ifnull 332
      // 2e4: aload 0
      // 2e5: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 2e8: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 2eb: ifnull 332
      // 2ee: aload 0
      // 2ef: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 2f2: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 2f5: invokevirtual net/rim/device/apps/internal/browser/page/Page.savesContext ()Z
      // 2f8: ifne 32a
      // 2fb: aload 0
      // 2fc: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 2ff: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 302: invokevirtual net/rim/device/apps/internal/browser/page/Page.isModified ()Z
      // 305: ifeq 32a
      // 308: new net/rim/device/apps/internal/browser/core/ConfirmLeavePageRunnable
      // 30b: dup
      // 30c: invokespecial net/rim/device/apps/internal/browser/core/ConfirmLeavePageRunnable.<init> ()V
      // 30f: astore 5
      // 311: aload 0
      // 312: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 315: aload 5
      // 317: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 31a: aload 5
      // 31c: invokevirtual net/rim/device/apps/internal/browser/core/ConfirmLeavePageRunnable.getContinue ()Z
      // 31f: ifne 32a
      // 322: aload 0
      // 323: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 326: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 329: return
      // 32a: aload 3
      // 32b: getstatic java/lang/Boolean.TRUE Ljava/lang/Boolean;
      // 32e: invokevirtual net/rim/device/apps/api/framework/verb/Verb.invoke (Ljava/lang/Object;)Ljava/lang/Object;
      // 331: pop
      // 332: aload 0
      // 333: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 336: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 339: goto 41b
      // 33c: astore 8
      // 33e: aload 0
      // 33f: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 342: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 345: aload 8
      // 347: athrow
      // 348: aload 0
      // 349: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._context Ljava/lang/Object;
      // 34c: instanceof java/lang/Integer
      // 34f: ifne 355
      // 352: goto 41b
      // 355: aload 0
      // 356: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 359: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 35c: aload 0
      // 35d: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._context Ljava/lang/Object;
      // 360: checkcast java/lang/Integer
      // 363: invokevirtual java/lang/Integer.intValue ()I
      // 366: lookupswitch 79 4 1 62 6 42 7 52 9 72
      // 390: aload 0
      // 391: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 394: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.changeToLoadingNegotiatingState ()V
      // 397: goto 3b5
      // 39a: aload 0
      // 39b: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 39e: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.changeToLoadingSecuringState ()V
      // 3a1: goto 3b5
      // 3a4: aload 0
      // 3a5: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3a8: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.changeToLoadingSecuringFinishedState ()V
      // 3ab: goto 3b5
      // 3ae: aload 0
      // 3af: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3b2: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.changeToLoadingWaitingState ()V
      // 3b5: aload 0
      // 3b6: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3b9: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 3bc: goto 41b
      // 3bf: astore 2
      // 3c0: aload 0
      // 3c1: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3c4: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 3c7: goto 41b
      // 3ca: astore 9
      // 3cc: aload 0
      // 3cd: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3d0: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 3d3: aload 9
      // 3d5: athrow
      // 3d6: aload 0
      // 3d7: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3da: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBrowserLock ()V
      // 3dd: aload 0
      // 3de: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3e1: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.activateInternal ()V
      // 3e4: aload 0
      // 3e5: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3e8: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 3eb: goto 41b
      // 3ee: astore 10
      // 3f0: aload 0
      // 3f1: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3f4: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.releaseBrowserLock ()V
      // 3f7: aload 10
      // 3f9: athrow
      // 3fa: aload 0
      // 3fb: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest._context Ljava/lang/Object;
      // 3fe: dup
      // 3ff: instanceof java/lang/Runnable
      // 402: ifne 409
      // 405: pop
      // 406: goto 41b
      // 409: checkcast java/lang/Runnable
      // 40c: invokeinterface java/lang/Runnable.run ()V 1
      // 411: return
      // 412: astore 1
      // 413: aload 0
      // 414: getfield net/rim/device/apps/internal/browser/core/BrowserImpl$NavigationRequest.this$0 Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 417: aload 1
      // 418: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.handleException (Ljava/lang/Throwable;)V
      // 41b: return
      // try (49 -> 52): 56 null
      // try (56 -> 57): 56 null
      // try (65 -> 68): 72 null
      // try (72 -> 73): 72 null
      // try (90 -> 94): 98 null
      // try (98 -> 99): 98 null
      // try (112 -> 115): 119 null
      // try (119 -> 120): 119 null
      // try (237 -> 242): 243 null
      // try (243 -> 246): 243 null
      // try (159 -> 248): 345 null
      // try (252 -> 302): 345 null
      // try (306 -> 333): 345 null
      // try (337 -> 341): 345 null
      // try (345 -> 346): 345 null
      // try (359 -> 379): 383 null
      // try (359 -> 379): 388 null
      // try (383 -> 384): 388 null
      // try (388 -> 389): 388 null
      // try (397 -> 400): 404 null
      // try (404 -> 405): 404 null
      // try (0 -> 251): 420 null
      // try (252 -> 305): 420 null
      // try (306 -> 336): 420 null
      // try (337 -> 419): 420 null
   }
}
