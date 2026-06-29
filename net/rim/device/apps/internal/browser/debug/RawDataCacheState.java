package net.rim.device.apps.internal.browser.debug;

import java.util.Enumeration;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.stack.CacheNode;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;

final class RawDataCacheState extends CacheState {
   private RawDataCache _cache;

   public RawDataCacheState() {
      super(191);
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      this._cache = browser.getRawDataCache();
   }

   @Override
   final void handleDelete(Object obj) {
      if (obj instanceof CachedUrlDetail) {
         CachedUrlDetail details = (CachedUrlDetail)obj;
         this._cache.remove(details.getURL(), true);
         this.refreshOptions();
      }
   }

   @Override
   final void handleSave(Object param1) {
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
      // 000: aload 1
      // 001: dup
      // 002: instanceof net/rim/device/apps/internal/browser/debug/CachedUrlDetail
      // 005: ifne 00c
      // 008: pop
      // 009: goto 109
      // 00c: checkcast net/rim/device/apps/internal/browser/debug/CachedUrlDetail
      // 00f: astore 2
      // 010: aload 2
      // 011: invokevirtual net/rim/device/apps/internal/browser/debug/CachedUrlDetail.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 014: astore 3
      // 015: aload 3
      // 016: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getResponseHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 019: ldc_w "content-type"
      // 01c: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyValue (Ljava/lang/String;)Ljava/lang/String;
      // 01f: invokestatic net/rim/device/api/io/MIMETypeAssociations.getExtensionFromMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 022: astore 4
      // 024: aload 4
      // 026: ifnonnull 02e
      // 029: ldc_w ""
      // 02c: astore 4
      // 02e: new java/lang/Object
      // 031: dup
      // 032: ldc_w "/SDCard/"
      // 035: new java/lang/Object
      // 038: dup
      // 039: invokespecial java/lang/StringBuffer.<init> ()V
      // 03c: aload 3
      // 03d: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getURLWithoutFragment ()Ljava/lang/String;
      // 040: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 043: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 046: bipush 46
      // 048: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 04b: aload 4
      // 04d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 050: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 053: bipush 0
      // 054: ldc_w "Save data"
      // 057: invokespecial net/rim/device/apps/api/framework/file/FileDialog.<init> (Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V
      // 05a: astore 5
      // 05c: aload 5
      // 05e: invokevirtual net/rim/device/apps/api/framework/file/FileDialog.doModal ()I
      // 061: bipush -1
      // 063: if_icmpne 067
      // 066: return
      // 067: aconst_null
      // 068: astore 6
      // 06a: aconst_null
      // 06b: astore 7
      // 06d: aload 5
      // 06f: invokevirtual net/rim/device/apps/api/framework/file/FileDialog.getURL ()Ljava/lang/String;
      // 072: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 075: checkcast java/lang/Object
      // 078: astore 6
      // 07a: aload 6
      // 07c: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 081: ifeq 08b
      // 084: aload 6
      // 086: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 08b: aload 6
      // 08d: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 092: aload 6
      // 094: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 099: astore 7
      // 09b: aload 7
      // 09d: aload 3
      // 09e: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getDataAsArray ()[B
      // 0a1: invokevirtual java/io/OutputStream.write ([B)V
      // 0a4: aload 7
      // 0a6: ifnull 0b3
      // 0a9: aload 7
      // 0ab: invokevirtual java/io/OutputStream.close ()V
      // 0ae: goto 0b3
      // 0b1: astore 8
      // 0b3: aload 6
      // 0b5: ifnull 109
      // 0b8: aload 6
      // 0ba: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0bf: return
      // 0c0: astore 8
      // 0c2: return
      // 0c3: astore 8
      // 0c5: aload 7
      // 0c7: ifnull 0d4
      // 0ca: aload 7
      // 0cc: invokevirtual java/io/OutputStream.close ()V
      // 0cf: goto 0d4
      // 0d2: astore 8
      // 0d4: aload 6
      // 0d6: ifnull 109
      // 0d9: aload 6
      // 0db: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0e0: return
      // 0e1: astore 8
      // 0e3: return
      // 0e4: astore 9
      // 0e6: aload 7
      // 0e8: ifnull 0f5
      // 0eb: aload 7
      // 0ed: invokevirtual java/io/OutputStream.close ()V
      // 0f0: goto 0f5
      // 0f3: astore 10
      // 0f5: aload 6
      // 0f7: ifnull 106
      // 0fa: aload 6
      // 0fc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 101: goto 106
      // 104: astore 10
      // 106: aload 9
      // 108: athrow
      // 109: return
      // try (70 -> 72): 73 null
      // try (76 -> 78): 79 null
      // try (49 -> 68): 81 null
      // try (84 -> 86): 87 null
      // try (90 -> 92): 93 null
      // try (49 -> 68): 95 null
      // try (81 -> 82): 95 null
      // try (98 -> 100): 101 null
      // try (104 -> 106): 107 null
      // try (95 -> 96): 95 null
   }

   @Override
   final void handleExpire(Object obj) {
      if (obj instanceof CachedUrlDetail) {
         CachedUrlDetail details = (CachedUrlDetail)obj;
         CacheResult cacheResult = details.getCacheResult();
         long timestamp = System.currentTimeMillis();
         long expiration = cacheResult.getExpiration();
         if (timestamp < expiration) {
            cacheResult.setTimestamp(timestamp - (expiration - timestamp));
            cacheResult.setExpiration(0);
            this.refreshOptions();
         }
      }
   }

   @Override
   final void refreshOptions() {
      Enumeration e = null;
      synchronized (this._cache) {
         super._options = new CachedUrlDetail[this._cache.getLongTermCacheCount() + this._cache.getShortTermCacheCount()];
         int index = 0;
         e = this._cache.getShortTermCacheElements();

         while (e.hasMoreElements()) {
            CacheNode node = (CacheNode)e.nextElement();
            CacheResult cacheResult = node.getContents();
            super._options[index++] = new CachedUrlDetail(node.getUrl(), node.getCreationDate(), node.getExpiryDate(), null, cacheResult, false);
         }

         e = this._cache.getLongTermCacheElements();

         while (e.hasMoreElements()) {
            CacheNode node = (CacheNode)e.nextElement();
            CacheResult cacheResult = node.getContents();
            super._options[index++] = new CachedUrlDetail(node.getUrl(), node.getCreationDate(), node.getExpiryDate(), null, cacheResult, true);
         }
      }

      Arrays.sort(super._options, 0, super._options.length, new RawDataCacheState$CacheUrlComparator(null));
      super._fields.setSize(super._options.length);
   }
}
