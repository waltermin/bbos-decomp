package net.rim.device.apps.internal.browser.stack;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connection;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.internal.browser.bookmark.Bookmarks;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.internal.browser.util.Pipe;

public final class ResourceValidationThread extends Thread {
   private Vector _fetchItems = (Vector)(new Object());
   private Vector _validateItems = (Vector)(new Object());
   private Vector _bookmarkItems = (Vector)(new Object());
   private RawDataCache _cache;
   private BrowserConfigRecord _config;
   private boolean _running = true;
   private boolean _terminated;
   private long _expiryCheckTime;
   private static final long FAILURE_DATES_KEY = 7352072977602323789L;

   public ResourceValidationThread(BrowserConfigRecord config, RawDataCache cache, long expiryCheckTime) {
      this._cache = cache;
      this._config = config;
      this._expiryCheckTime = expiryCheckTime;
   }

   public final long addBookmarksToValidate(long lastAutoUpdateTime) {
      return !PersistentContent.isEncryptionEnabled() && !ITPolicy.getBoolean(30, 11, false)
         ? Bookmarks.addAutoUpdateItems(this._bookmarkItems, lastAutoUpdateTime)
         : Long.MAX_VALUE;
   }

   public final void addUrlsToValidate() {
      if (!PersistentContent.isEncryptionEnabled()) {
         Enumeration persistentItems = this._cache.getLongTermCacheElements();

         while (persistentItems.hasMoreElements()) {
            CacheResult cr = ((CacheNode)persistentItems.nextElement()).getContents();
            if (!cr.isPostMethod() && cr.getExpiration() < this._expiryCheckTime) {
               this._validateItems.addElement(cr.getURLWithoutFragment());
            }
         }
      }

      Enumeration transientItems = this._cache.getShortTermCacheElements();

      while (transientItems.hasMoreElements()) {
         CacheResult cr = ((CacheNode)transientItems.nextElement()).getContents();
         if (!cr.isPostMethod() && cr.getExpiration() < this._expiryCheckTime) {
            this._validateItems.addElement(cr.getURLWithoutFragment());
         }
      }
   }

   public final void addUrlToFetch(String url) {
      if (!this._fetchItems.contains(url)) {
         this._fetchItems.addElement(url);
      }
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
      // 000: ldc2_w 1907089860548946979
      // 003: ldc_w 1381266292
      // 006: bipush 5
      // 008: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 00b: pop
      // 00c: invokestatic net/rim/device/api/system/PersistentContent.getTicket ()Ljava/lang/Object;
      // 00f: astore 1
      // 010: aload 1
      // 011: ifnonnull 026
      // 014: ldc2_w 1907089860548946979
      // 017: ldc_w 1381266547
      // 01a: bipush 5
      // 01c: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 01f: pop
      // 020: aload 0
      // 021: bipush 1
      // 022: putfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._terminated Z
      // 025: return
      // 026: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 029: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getStandardRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 02c: astore 2
      // 02d: aload 0
      // 02e: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._bookmarkItems Ljava/util/Vector;
      // 031: invokevirtual java/util/Vector.elements ()Ljava/util/Enumeration;
      // 034: astore 3
      // 035: aload 3
      // 036: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 03b: ifne 041
      // 03e: goto 240
      // 041: ldc2_w 7352072977602323789
      // 044: invokestatic net/rim/device/api/system/RIMPersistentStore.getPersistentObject (J)Lnet/rim/device/api/system/PersistentObject;
      // 047: astore 4
      // 049: aload 4
      // 04b: invokevirtual net/rim/device/api/system/PersistentObject.getContents ()Ljava/lang/Object;
      // 04e: astore 5
      // 050: aconst_null
      // 051: astore 6
      // 053: aload 5
      // 055: dup
      // 056: instanceof java/lang/Object
      // 059: ifne 060
      // 05c: pop
      // 05d: goto 068
      // 060: checkcast java/lang/Object
      // 063: astore 6
      // 065: goto 071
      // 068: new java/lang/Object
      // 06b: dup
      // 06c: invokespecial net/rim/device/api/util/LongIntHashtable.<init> ()V
      // 06f: astore 6
      // 071: invokestatic java/lang/System.currentTimeMillis ()J
      // 074: ldc_w 3600000
      // 077: i2l
      // 078: ldiv
      // 079: l2i
      // 07a: istore 7
      // 07c: aload 3
      // 07d: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 082: ifne 088
      // 085: goto 202
      // 088: aload 0
      // 089: aload 0
      // 08a: astore 8
      // 08c: monitorenter
      // 08d: aload 0
      // 08e: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._running Z
      // 091: ifne 0a9
      // 094: aload 8
      // 096: monitorexit
      // 097: ldc2_w 1907089860548946979
      // 09a: ldc_w 1381266547
      // 09d: bipush 5
      // 09f: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0a2: pop
      // 0a3: aload 0
      // 0a4: bipush 1
      // 0a5: putfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._terminated Z
      // 0a8: return
      // 0a9: aload 8
      // 0ab: monitorexit
      // 0ac: goto 0b7
      // 0af: astore 9
      // 0b1: aload 8
      // 0b3: monitorexit
      // 0b4: aload 9
      // 0b6: athrow
      // 0b7: aload 3
      // 0b8: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0bd: checkcast net/rim/device/apps/internal/browser/page/BrowserPageModel
      // 0c0: astore 8
      // 0c2: aload 8
      // 0c4: invokevirtual net/rim/device/apps/internal/browser/page/BrowserPageModel.getModelResult ()Lnet/rim/device/apps/internal/browser/stack/ModelResult;
      // 0c7: astore 9
      // 0c9: aload 9
      // 0cb: ifnull 07c
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._config Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 0d2: ifnull 118
      // 0d5: aload 9
      // 0d7: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getConfigUID ()Ljava/lang/String;
      // 0da: aload 0
      // 0db: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._config Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 0de: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getUid ()Ljava/lang/String;
      // 0e1: ldc_w 1701707776
      // 0e4: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 0e7: ifeq 118
      // 0ea: aload 9
      // 0ec: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getConfigType ()I
      // 0ef: aload 0
      // 0f0: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._config Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 0f3: bipush 12
      // 0f5: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsInt (I)I
      // 0f8: if_icmpne 118
      // 0fb: aload 9
      // 0fd: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getTransportCID ()Ljava/lang/String;
      // 100: aload 0
      // 101: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._config Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 104: bipush 3
      // 106: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsString (I)Ljava/lang/String;
      // 109: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;)Z
      // 10c: ifeq 118
      // 10f: aload 0
      // 110: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._config Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 113: astore 10
      // 115: goto 13a
      // 118: aload 9
      // 11a: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getTransportCID ()Ljava/lang/String;
      // 11d: astore 11
      // 11f: aload 11
      // 121: ifnonnull 129
      // 124: getstatic net/rim/device/apps/internal/browser/options/BrowserConfigRecord.IPPP_SERVICE_CID Ljava/lang/String;
      // 127: astore 11
      // 129: aload 9
      // 12b: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getConfigUID ()Ljava/lang/String;
      // 12e: aload 9
      // 130: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getConfigType ()I
      // 133: aload 11
      // 135: invokestatic net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getDecodedConfig (Ljava/lang/String;ILjava/lang/String;)Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 138: astore 10
      // 13a: aload 9
      // 13c: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getURL ()Ljava/lang/String;
      // 13f: astore 11
      // 141: new net/rim/device/apps/internal/browser/stack/ModelResult
      // 144: dup
      // 145: aload 11
      // 147: bipush 4
      // 149: aload 2
      // 14a: invokespecial net/rim/device/apps/internal/browser/stack/ModelResult.<init> (Ljava/lang/String;ILnet/rim/device/api/io/http/HttpHeaders;)V
      // 14d: astore 9
      // 14f: new net/rim/device/apps/internal/browser/stack/FetchRequest
      // 152: dup
      // 153: aload 9
      // 155: aload 10
      // 157: sipush 6144
      // 15a: invokespecial net/rim/device/apps/internal/browser/stack/FetchRequest.<init> (Lnet/rim/device/apps/internal/browser/stack/ModelResult;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;I)V
      // 15d: astore 12
      // 15f: aload 0
      // 160: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._cache Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 163: aload 12
      // 165: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.get (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)Ljavax/microedition/io/InputConnection;
      // 168: astore 13
      // 16a: aload 11
      // 16c: invokevirtual java/lang/String.getBytes ()[B
      // 16f: invokestatic net/rim/device/api/crypto/HashCodeCalculator.getDigest64 ([B)J
      // 172: lstore 14
      // 174: aload 0
      // 175: aload 13
      // 177: aload 12
      // 179: aload 9
      // 17b: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 17e: invokespecial net/rim/device/apps/internal/browser/stack/ResourceValidationThread.readAllDataAndClose (Ljavax/microedition/io/Connection;Lnet/rim/device/apps/internal/browser/stack/FetchRequest;Lnet/rim/device/apps/internal/browser/stack/CacheResult;)I
      // 181: lookupswitch 45 2 200 27 304 34
      // 19c: aload 8
      // 19e: bipush 3
      // 1a0: invokevirtual net/rim/device/apps/internal/browser/page/BrowserPageModel.changeStatus (I)V
      // 1a3: aload 6
      // 1a5: lload 14
      // 1a7: invokevirtual net/rim/device/api/util/LongIntHashtable.remove (J)I
      // 1aa: pop
      // 1ab: goto 07c
      // 1ae: aload 6
      // 1b0: lload 14
      // 1b2: invokevirtual net/rim/device/api/util/LongIntHashtable.containsKey (J)Z
      // 1b5: ifne 1c5
      // 1b8: aload 6
      // 1ba: lload 14
      // 1bc: iload 7
      // 1be: invokevirtual net/rim/device/api/util/LongIntHashtable.put (JI)I
      // 1c1: pop
      // 1c2: goto 07c
      // 1c5: aload 6
      // 1c7: lload 14
      // 1c9: invokevirtual net/rim/device/api/util/LongIntHashtable.get (J)I
      // 1cc: istore 16
      // 1ce: sipush 168
      // 1d1: istore 17
      // 1d3: getstatic net/rim/device/apps/internal/browser/bookmark/Bookmarks._debugAutoUpdate Z
      // 1d6: ifeq 1dd
      // 1d9: bipush 4
      // 1db: istore 17
      // 1dd: iload 16
      // 1df: iload 17
      // 1e1: iadd
      // 1e2: iload 7
      // 1e4: if_icmple 1ea
      // 1e7: goto 07c
      // 1ea: aload 6
      // 1ec: lload 14
      // 1ee: invokevirtual net/rim/device/api/util/LongIntHashtable.remove (J)I
      // 1f1: pop
      // 1f2: aload 8
      // 1f4: bipush 0
      // 1f5: invokevirtual net/rim/device/apps/internal/browser/page/BrowserPageModel.setUpdateFlags (B)V
      // 1f8: bipush 102
      // 1fa: aload 8
      // 1fc: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.broadCastEvent (ILjava/lang/Object;)V
      // 1ff: goto 07c
      // 202: aload 6
      // 204: invokevirtual net/rim/device/api/util/LongIntHashtable.keys ()Lnet/rim/device/api/util/LongEnumeration;
      // 207: astore 8
      // 209: aload 8
      // 20b: invokeinterface net/rim/device/api/util/LongEnumeration.hasMoreElements ()Z 1
      // 210: ifeq 237
      // 213: aload 8
      // 215: invokeinterface net/rim/device/api/util/LongEnumeration.nextElement ()J 1
      // 21a: lstore 9
      // 21c: aload 6
      // 21e: lload 9
      // 220: invokevirtual net/rim/device/api/util/LongIntHashtable.get (J)I
      // 223: sipush 240
      // 226: iadd
      // 227: iload 7
      // 229: if_icmpgt 209
      // 22c: aload 6
      // 22e: lload 9
      // 230: invokevirtual net/rim/device/api/util/LongIntHashtable.remove (J)I
      // 233: pop
      // 234: goto 209
      // 237: aload 4
      // 239: aload 6
      // 23b: bipush 51
      // 23d: invokevirtual net/rim/device/api/system/PersistentObject.setContents (Ljava/lang/Object;I)V
      // 240: aload 0
      // 241: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._validateItems Ljava/util/Vector;
      // 244: invokevirtual java/util/Vector.elements ()Ljava/util/Enumeration;
      // 247: astore 4
      // 249: aload 4
      // 24b: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 250: ifeq 2c8
      // 253: aload 0
      // 254: aload 0
      // 255: astore 5
      // 257: monitorenter
      // 258: aload 0
      // 259: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._running Z
      // 25c: ifne 274
      // 25f: aload 5
      // 261: monitorexit
      // 262: ldc2_w 1907089860548946979
      // 265: ldc_w 1381266547
      // 268: bipush 5
      // 26a: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 26d: pop
      // 26e: aload 0
      // 26f: bipush 1
      // 270: putfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._terminated Z
      // 273: return
      // 274: aload 5
      // 276: monitorexit
      // 277: goto 282
      // 27a: astore 18
      // 27c: aload 5
      // 27e: monitorexit
      // 27f: aload 18
      // 281: athrow
      // 282: aload 4
      // 284: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 289: checkcast java/lang/Object
      // 28c: astore 5
      // 28e: new net/rim/device/apps/internal/browser/stack/ModelResult
      // 291: dup
      // 292: aload 5
      // 294: bipush 4
      // 296: aload 2
      // 297: invokespecial net/rim/device/apps/internal/browser/stack/ModelResult.<init> (Ljava/lang/String;ILnet/rim/device/api/io/http/HttpHeaders;)V
      // 29a: astore 6
      // 29c: new net/rim/device/apps/internal/browser/stack/FetchRequest
      // 29f: dup
      // 2a0: aload 6
      // 2a2: aload 0
      // 2a3: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._config Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 2a6: bipush 0
      // 2a7: invokespecial net/rim/device/apps/internal/browser/stack/FetchRequest.<init> (Lnet/rim/device/apps/internal/browser/stack/ModelResult;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;I)V
      // 2aa: astore 7
      // 2ac: aload 0
      // 2ad: getfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._cache Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 2b0: aload 7
      // 2b2: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.get (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)Ljavax/microedition/io/InputConnection;
      // 2b5: astore 8
      // 2b7: aload 0
      // 2b8: aload 8
      // 2ba: aload 7
      // 2bc: aload 6
      // 2be: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 2c1: invokespecial net/rim/device/apps/internal/browser/stack/ResourceValidationThread.readAllDataAndClose (Ljavax/microedition/io/Connection;Lnet/rim/device/apps/internal/browser/stack/FetchRequest;Lnet/rim/device/apps/internal/browser/stack/CacheResult;)I
      // 2c4: pop
      // 2c5: goto 249
      // 2c8: ldc2_w 1907089860548946979
      // 2cb: ldc_w 1381266547
      // 2ce: bipush 5
      // 2d0: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 2d3: pop
      // 2d4: aload 0
      // 2d5: bipush 1
      // 2d6: putfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._terminated Z
      // 2d9: return
      // 2da: astore 1
      // 2db: ldc2_w 1907089860548946979
      // 2de: ldc_w 1381266547
      // 2e1: bipush 5
      // 2e3: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 2e6: pop
      // 2e7: aload 0
      // 2e8: bipush 1
      // 2e9: putfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._terminated Z
      // 2ec: return
      // 2ed: astore 19
      // 2ef: ldc2_w 1907089860548946979
      // 2f2: ldc_w 1381266547
      // 2f5: bipush 5
      // 2f7: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 2fa: pop
      // 2fb: aload 0
      // 2fc: bipush 1
      // 2fd: putfield net/rim/device/apps/internal/browser/stack/ResourceValidationThread._terminated Z
      // 300: aload 19
      // 302: athrow
      // try (64 -> 69): 81 null
      // try (78 -> 80): 81 null
      // try (81 -> 84): 81 null
      // try (253 -> 258): 270 null
      // try (267 -> 269): 270 null
      // try (270 -> 273): 270 null
      // try (0 -> 9): 316 null
      // try (18 -> 69): 316 null
      // try (78 -> 258): 316 null
      // try (267 -> 307): 316 null
      // try (0 -> 9): 326 null
      // try (18 -> 69): 326 null
      // try (78 -> 258): 326 null
      // try (267 -> 307): 326 null
      // try (316 -> 317): 326 null
      // try (326 -> 327): 326 null
   }

   private final int readAllDataAndClose(Connection conn, FetchRequest fr, CacheResult cr) {
      if (cr != null) {
         Pipe data = cr.getData();
         if (data != null) {
            data.waitUntilClosed();
         }

         if (conn != null) {
            try {
               conn.close();
            } finally {
               return cr.getStatus();
            }
         }

         return cr.getStatus();
      } else {
         return -1;
      }
   }

   public final void terminateValidating() {
      this._validateItems.removeAllElements();
   }

   public final void terminate() {
      synchronized (this) {
         this._running = false;
      }
   }

   public final boolean isTerminated() {
      return this._terminated;
   }
}
