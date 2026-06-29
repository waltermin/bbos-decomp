package net.rim.device.apps.internal.browser.page;

import java.io.InputStream;
import javax.microedition.io.Connection;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;

public final class RenderThread extends Thread {
   private BrowserImpl _browser;
   private FetchRequest _fetchRequest;
   private Connection _connection;
   private InputStream _in;

   public RenderThread(BrowserImpl browser, FetchRequest fetchRequest) {
      this._browser = browser;
      this._fetchRequest = fetchRequest;
   }

   public final void setConnection(Connection conn) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setInput(InputStream in) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final InputStream getInput() {
      return this._in;
   }

   public final void abort() {
      if (this._in != null) {
         try {
            this._in.close();
         } finally {
            return;
         }
      }
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
      // 001: bipush 7
      // 003: invokevirtual java/lang/Thread.setPriority (I)V
      // 006: invokestatic net/rim/device/api/system/PersistentContent.getTicket ()Ljava/lang/Object;
      // 009: astore 1
      // 00a: aload 1
      // 00b: ifnonnull 03a
      // 00e: invokestatic net/rim/device/internal/ui/AnimationThread.resume ()V
      // 011: aload 0
      // 012: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 015: ifnull 023
      // 018: aload 0
      // 019: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 01c: invokevirtual java/io/InputStream.close ()V
      // 01f: goto 023
      // 022: astore 2
      // 023: aload 0
      // 024: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 027: ifnull 039
      // 02a: aload 0
      // 02b: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 02e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 033: goto 039
      // 036: astore 2
      // 037: return
      // 038: astore 2
      // 039: return
      // 03a: invokestatic net/rim/device/internal/ui/AnimationThread.pause ()V
      // 03d: aload 0
      // 03e: getfield net/rim/device/apps/internal/browser/page/RenderThread._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 041: bipush 1
      // 042: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setDirtyHandling (Z)V
      // 045: ldc2_w 1907089860548946979
      // 048: ldc_w 1381265952
      // 04b: bipush 5
      // 04d: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 050: pop
      // 051: bipush 1
      // 052: invokestatic net/rim/vm/Process.killProcessIfThisThreadDies (Z)V
      // 055: aload 0
      // 056: getfield net/rim/device/apps/internal/browser/page/RenderThread._browser Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 059: aload 0
      // 05a: getfield net/rim/device/apps/internal/browser/page/RenderThread._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 05d: aload 0
      // 05e: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.processFetchRequest (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;Lnet/rim/device/apps/internal/browser/page/RenderThread;)V
      // 061: aload 0
      // 062: invokespecial net/rim/device/apps/internal/browser/page/RenderThread.markItemAsClean ()V
      // 065: invokestatic net/rim/device/internal/ui/AnimationThread.resume ()V
      // 068: aload 0
      // 069: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 06c: ifnull 07a
      // 06f: aload 0
      // 070: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 073: invokevirtual java/io/InputStream.close ()V
      // 076: goto 07a
      // 079: astore 1
      // 07a: aload 0
      // 07b: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 07e: ifnonnull 084
      // 081: goto 140
      // 084: aload 0
      // 085: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 088: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 08d: goto 140
      // 090: astore 1
      // 091: return
      // 092: astore 1
      // 093: return
      // 094: astore 1
      // 095: aload 0
      // 096: invokespecial net/rim/device/apps/internal/browser/page/RenderThread.removeItemIfDirty ()V
      // 099: aload 0
      // 09a: getfield net/rim/device/apps/internal/browser/page/RenderThread._browser Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 09d: aload 0
      // 09e: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.abortRequest (Lnet/rim/device/apps/internal/browser/page/RenderThread;)V
      // 0a1: invokestatic net/rim/device/internal/ui/AnimationThread.resume ()V
      // 0a4: aload 0
      // 0a5: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 0a8: ifnull 0b6
      // 0ab: aload 0
      // 0ac: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 0af: invokevirtual java/io/InputStream.close ()V
      // 0b2: goto 0b6
      // 0b5: astore 1
      // 0b6: aload 0
      // 0b7: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 0ba: ifnull 140
      // 0bd: aload 0
      // 0be: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 0c1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0c6: goto 140
      // 0c9: astore 1
      // 0ca: return
      // 0cb: astore 1
      // 0cc: return
      // 0cd: astore 1
      // 0ce: aload 0
      // 0cf: invokespecial net/rim/device/apps/internal/browser/page/RenderThread.removeItemIfDirty ()V
      // 0d2: aload 0
      // 0d3: getfield net/rim/device/apps/internal/browser/page/RenderThread._browser Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0d6: aload 1
      // 0d7: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.handleException (Ljava/lang/Throwable;)V
      // 0da: aload 0
      // 0db: getfield net/rim/device/apps/internal/browser/page/RenderThread._browser Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0de: aload 0
      // 0df: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.abortRequest (Lnet/rim/device/apps/internal/browser/page/RenderThread;)V
      // 0e2: invokestatic net/rim/device/internal/ui/AnimationThread.resume ()V
      // 0e5: aload 0
      // 0e6: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 0e9: ifnull 0f7
      // 0ec: aload 0
      // 0ed: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 0f0: invokevirtual java/io/InputStream.close ()V
      // 0f3: goto 0f7
      // 0f6: astore 1
      // 0f7: aload 0
      // 0f8: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 0fb: ifnull 140
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 102: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 107: goto 140
      // 10a: astore 1
      // 10b: return
      // 10c: astore 1
      // 10d: return
      // 10e: astore 3
      // 10f: invokestatic net/rim/device/internal/ui/AnimationThread.resume ()V
      // 112: aload 0
      // 113: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 116: ifnull 125
      // 119: aload 0
      // 11a: getfield net/rim/device/apps/internal/browser/page/RenderThread._in Ljava/io/InputStream;
      // 11d: invokevirtual java/io/InputStream.close ()V
      // 120: goto 125
      // 123: astore 4
      // 125: aload 0
      // 126: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 129: ifnull 13e
      // 12c: aload 0
      // 12d: getfield net/rim/device/apps/internal/browser/page/RenderThread._connection Ljavax/microedition/io/Connection;
      // 130: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 135: goto 13e
      // 138: astore 4
      // 13a: aload 3
      // 13b: athrow
      // 13c: astore 4
      // 13e: aload 3
      // 13f: athrow
      // 140: return
      // try (11 -> 14): 15 null
      // try (19 -> 22): 23 null
      // try (8 -> 24): 25 null
      // try (51 -> 54): 55 null
      // try (60 -> 63): 64 null
      // try (48 -> 65): 66 null
      // try (0 -> 7): 68 null
      // try (27 -> 47): 68 null
      // try (79 -> 82): 83 null
      // try (87 -> 90): 91 null
      // try (76 -> 92): 93 null
      // try (0 -> 7): 95 null
      // try (27 -> 47): 95 null
      // try (110 -> 113): 114 null
      // try (118 -> 121): 122 null
      // try (107 -> 123): 124 null
      // try (0 -> 7): 126 null
      // try (27 -> 47): 126 null
      // try (68 -> 75): 126 null
      // try (95 -> 106): 126 null
      // try (131 -> 134): 135 null
      // try (139 -> 142): 143 null
      // try (128 -> 144): 146 null
      // try (126 -> 127): 126 null
   }

   private final void markItemAsClean() {
      ModelResult mr = this._fetchRequest.getModelResult();
      if (mr != null) {
         CacheResult cr = mr.getCacheResult();
         if (cr != null) {
            cr.setDirty(false);
         }
      }
   }

   private final void removeItemIfDirty() {
      ModelResult mr = this._fetchRequest.getModelResult();
      if (mr != null) {
         CacheResult cr = mr.getCacheResult();
         if (cr == null || cr.isDirty()) {
            this._browser.getRawDataCache().remove(mr.getURL(), true);
         }
      }
   }
}
