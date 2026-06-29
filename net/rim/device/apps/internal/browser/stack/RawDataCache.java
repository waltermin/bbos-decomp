package net.rim.device.apps.internal.browser.stack;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.InputConnection;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserStateListener;
import net.rim.device.apps.internal.browser.multipart.MultipartUtilities;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.BrowserOptionsChangeListener;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.URLCache;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.cldc.io.utility.URIEncoder;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.browser.util.TimeLogger;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class RawDataCache implements BrowserStateListener, PersistentContentListener, BrowserOptionsChangeListener {
   private StackManager _stackManager = StackManager.getInstance();
   private LongTermCache _longTermCache;
   private ShortTermCache _shortTermCache;
   private PersistentObject _shortTermPersistentObject;
   private RawDataCache$ShortTermCommitThread _commitThread;
   private WeakReference[] _cacheListeners = new WeakReference[0];
   private static String IF_NONE_MATCH = "If-None-Match";
   private static String RIM_IF_NONE_MATCH = "x-rim-if-none-match";
   private static String IF_MODIFIED_SINCE = "If-Modified-Since";
   private static String LAST_MODIFIED = "Last-Modified";
   private static String CACHE_SCHEME = "cache:";
   private static final int ADDED_SHORT_TERM = 1;
   private static final int ADDED_LONG_TERM = 2;
   private static final long SHORT_TERM_RAW_DATA_CACHE_KEY = -4455003596529320285L;

   @Override
   public final void browserStateChanged(int newState) {
      if (newState == 0) {
         if (GeneralProperty.getCurrentPropertyAsBoolean(3)) {
            this.clearShortTermCache();
         }

         this.commit();
      }
   }

   public final void removeRawDataCacheListener(RawDataCacheListener listener) {
      synchronized (this._cacheListeners) {
         for (int i = this._cacheListeners.length - 1; i >= 0; i--) {
            WeakReference ref = this._cacheListeners[i];
            Object obj = ref.get();
            if (obj == null || obj == listener) {
               Arrays.removeAt(this._cacheListeners, i);
            }
         }
      }
   }

   public final void cleanup() {
      BrowserDaemonRegistry.removeBrowserStateListener(this);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final InputConnection get(FetchRequest fetchRequest) {
      ModelResult modelResult = fetchRequest.getModelResult();
      CacheResult cacheResult = null;
      int navigation = modelResult.getNavigation();
      boolean historyRequest = fetchRequest.isHistoryRequest();
      String urlWithoutFragment = this.canonicalizeUrl(modelResult.getURL());
      boolean cacheRequest = urlWithoutFragment.startsWith(CACHE_SCHEME);
      if ((cacheRequest || navigation != 3 && navigation != 5) && modelResult.getPostData() == null) {
         if (navigation != 4) {
            cacheResult = this.get(
               urlWithoutFragment,
               fetchRequest.getModelResult().getNavigation(),
               null,
               fetchRequest.getModelResult().getPostData(),
               fetchRequest.getModelResult().getRequestHeaders(),
               historyRequest,
               false,
               false
            );
         }

         if (cacheResult != null) {
            modelResult.setCacheResult(cacheResult);
            CachedHttpConnection cachedConn = new CachedHttpConnection(modelResult.getURL(), cacheResult, fetchRequest.getModelResult().getRequestHeaders());
            if (cacheResult.getSecured()) {
               fetchRequest.setSecurityInfo(new CachedSecurityInfo());
            }

            return cachedConn;
         }

         cacheResult = this.get(
            urlWithoutFragment,
            fetchRequest.getModelResult().getNavigation(),
            null,
            fetchRequest.getModelResult().getPostData(),
            fetchRequest.getModelResult().getRequestHeaders(),
            historyRequest,
            true,
            false
         );
      }

      CacheResult originalCacheResult = cacheResult;
      cacheResult = new CacheResult(urlWithoutFragment);
      if (originalCacheResult != null) {
         HttpHeaders headers = originalCacheResult.getResponseHeaders();
         String lastModified = headers.getPropertyValue(LAST_MODIFIED);
         if (lastModified != null) {
            modelResult.getRequestHeaders().setProperty(IF_MODIFIED_SINCE, lastModified);
         }

         addETags(headers, modelResult, "etag", IF_NONE_MATCH);
         addETags(headers, modelResult, "x-rim-etag", RIM_IF_NONE_MATCH);
      } else {
         HttpHeaders requestHeaders = modelResult.getRequestHeaders();
         requestHeaders.removeProperties(IF_NONE_MATCH);
         requestHeaders.removeProperties(RIM_IF_NONE_MATCH);
         requestHeaders.removeProperties(IF_MODIFIED_SINCE);
      }

      if (navigation == 3) {
         modelResult.getRequestHeaders().setProperty("cache-control", "no-cache");
      } else if (navigation == 4) {
         modelResult.getRequestHeaders().setProperty("cache-control", "max-age=0");
      }

      cacheResult.setPostMethod(modelResult.getPostData() != null);
      modelResult.setCacheResult(cacheResult);
      if (cacheRequest) {
         cacheResult.setStatus(400);
         cacheResult.setExceptionString(BrowserResources.getString(627));
         return null;
      }

      InputConnection conn = null;

      try {
         EventLogger.logEvent(1907089860548946979L, 1382183794, 5);
         if (TimeLogger._loggingEnabled) {
            TimeLogger.getInstance().startTimer(2, cacheResult.hashCode());
         }

         conn = this._stackManager.fetchPage(fetchRequest);
         if (TimeLogger._loggingEnabled) {
            TimeLogger.getInstance().stopTimer(2, cacheResult.hashCode());
         }
      } catch (Throwable var19) {
         EventLogger.logEvent(1907089860548946979L, e.toString().getBytes());
         return null;
      }

      int status = cacheResult.getStatus();
      if (status == 304 && originalCacheResult != null) {
         HttpHeaders newHeaders = cacheResult.getResponseHeaders();
         newHeaders.removeProperties("Content-Type");
         newHeaders.removeProperties("Content-Length");
         newHeaders.removeProperties("Content-Encoding");
         originalCacheResult.updateCacheResult(newHeaders);
         if ((fetchRequest.getFlags() & 4096) == 0) {
            cacheResult = originalCacheResult;
            modelResult.setCacheResult(cacheResult);

            label257:
            try {
               if (conn != null) {
                  conn.close();
               }
            } finally {
               break label257;
            }

            conn = new CachedHttpConnection(modelResult.getURL(), cacheResult, fetchRequest.getModelResult().getRequestHeaders());
            status = cacheResult.getStatus();
         }
      }

      if (!cacheResult.isLocalContent() && cacheResult.getExceptionString() == null && (status == 200 || status == 203 || status == 300 || status == 301)) {
         int flags = fetchRequest.getFlags();
         if ((flags & 8) == 0 && GeneralProperty.getCurrentPropertyAsBoolean(10) && urlWithoutFragment != null && urlWithoutFragment.startsWith("https")) {
            return conn;
         }

         Pipe data = cacheResult.getData();
         if ((flags & 16) != 0 && data != null && !data.isClosed()) {
            cacheResult.setDirty(true);
         }

         boolean askedLongTermAdd = (fetchRequest.getFlags() & 8) != 0;
         if (this.put(urlWithoutFragment, cacheResult, askedLongTermAdd, false, false) == 2 && askedLongTermAdd) {
            MultipartUtilities.addToCacheIfMultipart(cacheResult);
         }
      }

      return conn;
   }

   public final CacheResult get(String url, int navigation, String previousUrl, byte[] postData, HttpHeaders requestHeaders, boolean historyRequest) {
      return this.get(url, navigation, previousUrl, postData, requestHeaders, historyRequest, false, true);
   }

   public final CacheResult get(
      String url,
      int navigation,
      String previousUrl,
      byte[] postData,
      HttpHeaders requestHeaders,
      boolean historyRequest,
      boolean returnExpired,
      boolean canonicalizeUrl
   ) {
      if (postData != null && !historyRequest) {
         return null;
      }

      String urlWithoutFragment;
      if (canonicalizeUrl) {
         urlWithoutFragment = this.canonicalizeUrl(url);
      } else {
         urlWithoutFragment = url;
      }

      long key = 0;

      try {
         key = this.getKey(urlWithoutFragment);
      } finally {
         ;
      }

      CacheNode node = (CacheNode)this._longTermCache.get(key);
      if (node == null) {
         node = (CacheNode)this._shortTermCache.get(urlWithoutFragment);
      }

      if (node != null && !node.getContents().isDirty()) {
         CacheResult cacheResult = node.getContents();
         long expiryDate = node.getExpiryDate();
         boolean post = cacheResult.isPostMethod();
         boolean cacheRequest = urlWithoutFragment.startsWith(CACHE_SCHEME);
         long currentTime = System.currentTimeMillis();
         boolean transcoded = cacheResult.getResponseHeaders().getPropertyValue("x-rim-image-original-size") != null;
         String transcodeHeader = requestHeaders != null ? requestHeaders.getPropertyValue("x-rim-transcode-content") : null;
         boolean transcodeNoneRequest = transcodeHeader != null && StringUtilities.compareToIgnoreCase(transcodeHeader, "none", 1701707776) == 0;
         if ((cacheRequest || navigation != 3 && navigation != 5)
            && (!transcoded || !transcodeNoneRequest)
            && (!post || this.isIntraDeck(url, previousUrl) || postData != null && historyRequest)
            && (
               returnExpired
                  || expiryDate == -1
                  || expiryDate >= currentTime
                  || expiryDate < currentTime
                     && (
                        navigation != 2 && this.isIntraDeck(url, previousUrl)
                           || navigation == 2
                              && (
                                 !HeaderParser.containsDirective(node.getContents().getResponseHeaders(), HeaderParser.CACHE_DIRECTIVE_MUST_REVALIDATE)
                                    || !url.startsWith("https:")
                              )
                     )
            )) {
            if (!returnExpired) {
               node.nodeAccessed();
               this._shortTermCache.nodeAccessed(node);
               node.setAccessedDate(currentTime);
            }

            cacheResult.setCached(true);
            return cacheResult;
         }
      }

      return null;
   }

   public final CacheResult get(String url) {
      CacheNode node = this.getCacheNode(url);
      if (node != null && !node.getContents().isDirty()) {
         node.nodeAccessed();
         this._shortTermCache.nodeAccessed(node);
         node.setAccessedDate(System.currentTimeMillis());
         return node.getContents();
      } else {
         return null;
      }
   }

   public final Object getLock() {
      return this;
   }

   public final synchronized int put(String url, CacheResult obj, boolean longTerm) {
      return this.put(url, obj, longTerm, false, true);
   }

   public final synchronized int put(String url, CacheResult cacheResult, boolean longTerm, boolean makeAvailableOffline) {
      return this.put(url, cacheResult, longTerm, makeAvailableOffline, true);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void moveNodeToPeristentCache(CacheNode cacheNode) {
      if (cacheNode != null) {
         String url = cacheNode.getUrl();
         long key = 0;
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            key = this.getKey(url);
            var7 = false;
         } finally {
            if (var7) {
               return;
            }
         }

         this._longTermCache.put(key, cacheNode);
         this._shortTermCache.remove(url);
      }
   }

   public final synchronized void updateNode(String url, HttpHeaders headers) {
      CacheNode node = this.getCacheNode(url);
      if (node != null) {
         CacheResult cacheResult = node.getContents();
         cacheResult.updateCacheResult(headers);
         synchronized (this._cacheListeners) {
            for (int i = this._cacheListeners.length - 1; i >= 0; i--) {
               WeakReference ref = this._cacheListeners[i];
               Object object = ref.get();
               if (object == null) {
                  Arrays.removeAt(this._cacheListeners, i);
               } else {
                  ((RawDataCacheListener)object).cacheChanged(0, node);
               }
            }
         }
      }
   }

   public final synchronized int addNode(CacheNode obj, boolean longTerm) {
      String url = obj.getUrl();
      if (url == null) {
         return 0;
      }

      long key = 0;

      try {
         key = this.getKey(url);
      } finally {
         ;
      }

      CacheNode existingCacheNode = null;
      if (!longTerm && (existingCacheNode = (CacheNode)this._longTermCache.get(key)) == null) {
         CacheNode oldNode = (CacheNode)this._shortTermCache.remove(url);
         this.removeChildNodes(oldNode, false);
         this.purgeCacheIfFull();
         this._shortTermCache.put(url, obj);
         synchronized (this._cacheListeners) {
            for (int i = this._cacheListeners.length - 1; i >= 0; i--) {
               WeakReference ref = this._cacheListeners[i];
               Object object = ref.get();
               if (object == null) {
                  Arrays.removeAt(this._cacheListeners, i);
               } else {
                  ((RawDataCacheListener)object).cacheChanged(0, obj);
               }
            }

            return 1;
         }
      } else {
         if (existingCacheNode != null && existingCacheNode.getAvailableOffline()) {
            obj.setAvailableOffline(true);
         }

         CacheNode oldNode = (CacheNode)this._longTermCache.put(key, obj);
         this.removeChildNodes(oldNode, true);
         oldNode = (CacheNode)this._shortTermCache.remove(url);
         this.removeChildNodes(oldNode, false);
         return 2;
      }
   }

   public final void cacheItemChanged(String url, int sizeChanged) {
      this._shortTermCache.itemChanged(url, sizeChanged);
   }

   public final int getShortTermCacheSize() {
      return this.getShortTermCacheSize(false);
   }

   public final int getShortTermCacheSize(boolean validate) {
      if (validate) {
         this._shortTermCache.validateSize();
      }

      return this._shortTermCache.getTotalSize();
   }

   public final int getLongTermCacheSize() {
      return this.getLongTermCacheSize(true);
   }

   public final int getLongTermCacheSize(boolean withOffline) {
      int count = 0;
      Enumeration nodes = this._longTermCache.elements();

      while (nodes.hasMoreElements()) {
         CacheNode node = (CacheNode)nodes.nextElement();
         if (withOffline || !node.getAvailableOffline()) {
            CacheResult item = node.getContents();
            if (!item.isChildNode()) {
               count += item.getSize();
            }
         }
      }

      return count;
   }

   public final int getShortTermCacheCount() {
      return this._shortTermCache.size();
   }

   public final int getLongTermCacheCount() {
      return this._longTermCache.size();
   }

   public final boolean containsPushedContent() {
      Enumeration nodes = this._longTermCache.elements();

      while (nodes.hasMoreElements()) {
         if (!((CacheNode)nodes.nextElement()).getAvailableOffline()) {
            return true;
         }
      }

      return false;
   }

   public final CacheNode getCacheNode(String url) {
      url = this.canonicalizeUrl(url);
      CacheNode cacheNode = null;
      long key = 0;

      label26:
      try {
         key = this.getKey(url);
         cacheNode = (CacheNode)this._longTermCache.get(key);
      } finally {
         break label26;
      }

      if (cacheNode == null) {
         cacheNode = (CacheNode)this._shortTermCache.get(url);
      }

      return cacheNode;
   }

   public final CacheNode getLongTermCacheNode(String url) {
      url = this.canonicalizeUrl(url);
      CacheNode cacheNode = null;
      long key = 0;

      try {
         key = this.getKey(url);
         return (CacheNode)this._longTermCache.get(key);
      } finally {
         ;
      }
   }

   public final CacheNode getShortTermCacheNode(String url) {
      return (CacheNode)this._shortTermCache.get(this.canonicalizeUrl(url));
   }

   public final int getMaxCacheSize() {
      return GeneralProperty.getCurrentPropertyAsInt(31);
   }

   public final void addRawDataCacheListener(RawDataCacheListener listener) {
      synchronized (this._cacheListeners) {
         for (int i = this._cacheListeners.length - 1; i >= 0; i--) {
            if (this._cacheListeners[i].get() == null) {
               Arrays.removeAt(this._cacheListeners, i);
            }
         }

         Array.resize(this._cacheListeners, this._cacheListeners.length + 1);
         this._cacheListeners[this._cacheListeners.length - 1] = new WeakReference(listener);
      }
   }

   public final void remove(String uriString, boolean absolute) {
      this.remove(uriString, absolute, true);
   }

   public final synchronized void remove(String uriString, boolean absolute, boolean keepOfflineContent) {
      try {
         URI uri = new URI(URIDecoder.decode(this.removeFragmentIdentifier(uriString), "iso-8859-1"));
         long thisKey = this.getKey(uriString);
         LongEnumeration longKeys = this._longTermCache.keys();

         while (longKeys.hasMoreElements()) {
            long key = longKeys.nextElement();
            if (this.uriMatches(key, thisKey, absolute)) {
               if (keepOfflineContent) {
                  CacheNode cacheNode = (CacheNode)this._longTermCache.get(key);
                  if (cacheNode != null && cacheNode.getAvailableOffline()) {
                     continue;
                  }
               }

               CacheNode oldNode = (CacheNode)this._longTermCache.remove(key);
               this.removeChildNodes(oldNode, true);
            }
         }

         Enumeration keys = this._shortTermCache.keys();

         while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            URI tempURI = new URI(URIDecoder.decode(key, "iso-8859-1"));
            if (this.uriMatches(uri, tempURI, absolute)) {
               CacheNode oldNode = (CacheNode)this._shortTermCache.remove(key);
               this.removeChildNodes(oldNode, false);
            }
         }
      } finally {
         return;
      }
   }

   public final void pauseCommit() {
      if (this._commitThread != null) {
         this._commitThread.pauseCommit();
      }
   }

   public final void unPauseCommit() {
      if (this._commitThread != null) {
         this._commitThread.unPauseCommit();
      }
   }

   public final synchronized void commit() {
      this._longTermCache.commit();
   }

   public final synchronized void clearShortTermCacheIfNecessary(String transportCID, String transportUID) {
      if (!StringUtilities.strEqualIgnoreCase(this._shortTermCache._transportCID, transportCID, 1701707776)
         || !StringUtilities.strEqualIgnoreCase(this._shortTermCache._transportUID, transportUID, 1701707776)) {
         if (StringUtilities.strEqualIgnoreCase(this._shortTermCache._transportCID, BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)
            && StringUtilities.strEqualIgnoreCase(transportCID, BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
            this._shortTermCache._transportUID = transportUID;
         } else {
            this._shortTermCache._transportCID = transportCID;
            this._shortTermCache._transportUID = transportUID;
            this.clearShortTermCacheTransformedItems();
         }
      }
   }

   public final synchronized void clearShortTermCacheTransformedItems() {
      Enumeration keys = this._shortTermCache.keys();

      while (keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         CacheNode node = (CacheNode)this._shortTermCache.get(key);
         HttpHeaders headers = node.getContents().getResponseHeaders();
         if (headers != null) {
            String warningHeader = headers.getPropertyValue("Warning");
            if (warningHeader != null && warningHeader.startsWith("214")) {
               CacheNode oldNode = (CacheNode)this._shortTermCache.remove(key);
               this.removeChildNodes(oldNode, false);
            }
         }
      }
   }

   public final synchronized void clearShortTermCache() {
      this._shortTermCache.clear();
      synchronized (this._cacheListeners) {
         for (int i = this._cacheListeners.length - 1; i >= 0; i--) {
            WeakReference ref = this._cacheListeners[i];
            Object object = ref.get();
            if (object == null) {
               Arrays.removeAt(this._cacheListeners, i);
            } else {
               ((RawDataCacheListener)object).cacheChanged(2, null);
            }
         }
      }

      LongEnumeration keys = this._longTermCache.keys();

      while (keys.hasMoreElements()) {
         long key = keys.nextElement();
         CacheNode node = (CacheNode)this._longTermCache.get(key);
         if (!node.getAvailableOffline()) {
            try {
               long expiryDate = node.getExpiryDate();
               if (expiryDate != -1 && expiryDate < System.currentTimeMillis()) {
                  CacheNode oldNode = (CacheNode)this._longTermCache.remove(key);
                  this.removeChildNodes(oldNode, true);
               }
            } finally {
               continue;
            }
         }
      }
   }

   public final synchronized void clearLongTermCache(boolean keepOfflineContent) {
      if (keepOfflineContent) {
         LongEnumeration keys = this._longTermCache.keys();

         while (keys.hasMoreElements()) {
            long key = keys.nextElement();
            CacheNode node = (CacheNode)this._longTermCache.get(key);
            if (!node.getAvailableOffline()) {
               CacheNode oldNode = (CacheNode)this._longTermCache.remove(key);
               this.removeChildNodes(oldNode, true);
            }
         }
      } else {
         this._longTermCache.clear();
      }
   }

   public final synchronized void flushLongTermCache() {
      this._longTermCache.flushCache();
   }

   public final StackManager getStackManager() {
      return this._stackManager;
   }

   public final synchronized CacheNode removeExpiredShortTermNode() {
      if (this._shortTermCache.isEmpty()) {
         return null;
      }

      Enumeration keys = this._shortTermCache.keys();

      while (keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         CacheNode node = (CacheNode)this._shortTermCache.get(key);
         String url = node.getUrl();
         long expiryDate = node.getExpiryDate();
         if ((url == null || !url.startsWith(CACHE_SCHEME)) && expiryDate != -1 && expiryDate < System.currentTimeMillis()) {
            CacheNode oldNode = (CacheNode)this._shortTermCache.remove(key);
            this.removeChildNodes(oldNode, false);
            return node;
         }
      }

      return null;
   }

   public final synchronized CacheNode removeExpiredLongTermNode() {
      if (this._longTermCache.isEmpty()) {
         return null;
      }

      LongEnumeration keys = this._longTermCache.keys();

      while (keys.hasMoreElements()) {
         long key = keys.nextElement();
         CacheNode node = (CacheNode)this._longTermCache.get(key);

         try {
            String url = node.getUrl();
            long expiryDate = node.getExpiryDate();
            if ((url == null || !url.startsWith(CACHE_SCHEME)) && expiryDate != -1 && expiryDate < System.currentTimeMillis()) {
               CacheNode oldNode = (CacheNode)this._longTermCache.remove(key);
               this.removeChildNodes(oldNode, true);
               return node;
            }
         } finally {
            continue;
         }
      }

      return null;
   }

   public final synchronized CacheNode removeOldestShortTermNode() {
      if (this._shortTermCache.isEmpty()) {
         return null;
      }

      Enumeration keys = this._shortTermCache.keys();
      CacheNode min = null;
      String minKey = null;
      if (keys.hasMoreElements()) {
         minKey = (String)keys.nextElement();
         min = (CacheNode)this._shortTermCache.get(minKey);
      }

      while (keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         CacheNode node = (CacheNode)this._shortTermCache.get(key);
         if (node.getAccessedDate() < min.getAccessedDate()) {
            min = node;
            minKey = key;
         }
      }

      if (minKey != null) {
         CacheNode oldNode = (CacheNode)this._shortTermCache.remove(minKey);
         this.removeChildNodes(oldNode, true);
      }

      return min;
   }

   public final synchronized CacheNode removeOldestLongTermNode() {
      if (this._longTermCache.isEmpty()) {
         return null;
      }

      LongEnumeration keys = this._longTermCache.keys();
      CacheNode min = null;
      long minKey = -1;
      if (keys.hasMoreElements()) {
         minKey = keys.nextElement();
         min = (CacheNode)this._longTermCache.get(minKey);
      }

      while (keys.hasMoreElements()) {
         long key = keys.nextElement();
         CacheNode node = (CacheNode)this._longTermCache.get(key);
         if (node.getAccessedDate() < min.getAccessedDate()) {
            min = node;
            minKey = key;
         }
      }

      if (minKey != -1) {
         CacheNode oldNode = (CacheNode)this._longTermCache.remove(minKey);
         this.removeChildNodes(oldNode, true);
      }

      return min;
   }

   protected final boolean isIntraDeck(String url, String previousUrl) {
      if (url != null && previousUrl != null) {
         int start = url.indexOf(35);
         if (start == -1) {
            return false;
         }

         url = url.substring(0, start);
         start = previousUrl.indexOf(35);
         if (start != -1) {
            previousUrl = previousUrl.substring(0, start);
         }

         return url.equals(previousUrl);
      } else {
         return false;
      }
   }

   public final Enumeration getLongTermCacheElements() {
      return this._longTermCache.elements();
   }

   public final Enumeration getShortTermCacheElements() {
      return this._shortTermCache.elements();
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      RIMPersistentStore.destroyPersistentObject(-4455003596529320285L);
      this._shortTermCache.destroyPersistence();
      this._shortTermPersistentObject = null;
      this._longTermCache.persistentContentModeChanged(generation);
      if (this._commitThread != null) {
         this._commitThread.shutdown();
         this._commitThread = null;
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void optionsChanged(BitSet changedOptions) {
      if (changedOptions.isSet(31)) {
         this._shortTermCache.setMaxSize(GeneralProperty.getCurrentPropertyAsInt(31) * 1024);
         this._shortTermCache.purgeEntriesIfNecessary();
      }
   }

   private final synchronized int put(String url, CacheResult cacheResult, boolean longTerm, boolean makeAvailableOffline, boolean canonicalizeUrl) {
      int result = 0;
      if (!cacheResult.isLocalContent()) {
         CacheNode newNode = new CacheNode(canonicalizeUrl ? this.canonicalizeUrl(url) : url, cacheResult, makeAvailableOffline);
         result = this.addNode(newNode, longTerm);
         CacheResult parentCacheResult = cacheResult.getParentCacheResult();
         if (parentCacheResult != null) {
            CacheNode parentNode = this.getCacheNode(parentCacheResult.getURLWithoutFragment());
            if (parentNode != null) {
               parentNode.addChildReference(newNode);
            }
         }
      }

      return result;
   }

   private final synchronized void removeChildNodes(CacheNode node, boolean longTerm) {
      if (node != null) {
         CacheNode[] children = node.getChildReferences();
         Vector nodes = new Vector();
         if (children != null) {
            for (int i = children.length - 1; i >= 0; i--) {
               CacheNode oldNode = children[i];
               String url = oldNode.getUrl();
               if (url != null) {
                  if (longTerm) {
                     long key = 0;

                     try {
                        key = this.getKey(url);
                     } finally {
                        continue;
                     }

                     this._longTermCache.remove(key);
                  } else if (oldNode.getContents().isVerifiedChildRef()) {
                     CacheResult cacheResult = oldNode.getContents();
                     cacheResult.removeParentReference();
                     this._shortTermCache.promoteItem(oldNode);
                  } else {
                     this._shortTermCache.remove(url);
                     nodes.addElement(oldNode);
                  }
               }
            }
         }

         if (nodes.size() > 0) {
            synchronized (this._cacheListeners) {
               for (int i = this._cacheListeners.length - 1; i >= 0; i--) {
                  WeakReference ref = this._cacheListeners[i];
                  Object object = ref.get();
                  if (object == null) {
                     Arrays.removeAt(this._cacheListeners, i);
                  } else {
                     ((RawDataCacheListener)object).cacheChanged(1, nodes);
                  }
               }
            }
         }
      }
   }

   private final long getKey(String uriString) {
      StringBuffer buffer = new StringBuffer();
      URI uri = new URI(URIDecoder.decode(this.removeFragmentIdentifier(uriString), "iso-8859-1"));
      String scheme = uri.getScheme();
      if (scheme != null) {
         buffer.append(StringUtilities.toLowerCase(scheme, 1701707776));
      }

      buffer.append("://");
      String host = uri.getAuthority();
      if (host != null) {
         buffer.append(StringUtilities.toLowerCase(host, 1701707776));
         int portIndex = host.indexOf(58);
         if (portIndex == -1) {
            buffer.append(":80");
         }
      } else {
         buffer.append(":80");
      }

      String path = uri.getPath();
      String file = null;
      if (path != null) {
         int nameStart = path.lastIndexOf(47);
         if (nameStart != -1) {
            buffer.append(path.substring(0, nameStart));
            file = path.substring(nameStart + 1);
         } else {
            buffer.append(path);
         }
      }

      String query = uri.getQuery();
      if (query != null) {
         buffer.append('?');
         buffer.append(query);
      }

      int urlHash = HashCodeCalculator.getCRC32(buffer.toString().getBytes());
      int fileHash = 0;
      if (file != null && file.length() != 0) {
         fileHash = HashCodeCalculator.getCRC32(file.getBytes());
      }

      return UIDGenerator.makeLUID(urlHash, fileHash);
   }

   private final void purgeCacheIfFull() {
      Vector deletedNodes = this._shortTermCache.purgeEntriesIfNecessary();
      if (deletedNodes != null && deletedNodes.size() > 0) {
         synchronized (this._cacheListeners) {
            for (int i = this._cacheListeners.length - 1; i >= 0; i--) {
               WeakReference ref = this._cacheListeners[i];
               Object object = ref.get();
               if (object == null) {
                  Arrays.removeAt(this._cacheListeners, i);
               } else {
                  ((RawDataCacheListener)object).cacheChanged(1, deletedNodes);
               }
            }
         }
      }
   }

   private final String removeFragmentIdentifier(String uriReference) {
      int fragment = uriReference.indexOf(35);
      return fragment != -1 ? uriReference.substring(0, fragment) : uriReference;
   }

   private final String canonicalizeUrl(String uriReference) {
      uriReference = this.removeFragmentIdentifier(uriReference);
      uriReference = URLCache.lowercaseSchemeAndHostname(uriReference, true);
      return URIEncoder.encodeNonUSASCII(uriReference, true);
   }

   private static final void addETags(HttpHeaders headers, ModelResult modelResult, String tag, String matchTag) {
      Vector etags = headers.getPropertyValues(tag);
      if (etags != null && etags.size() > 0) {
         int size = etags.size();
         StringBuffer buff = new StringBuffer((String)etags.elementAt(0));

         for (int i = 1; i < size; i++) {
            buff.append(',');
            buff.append(' ');
            buff.append((String)etags.elementAt(i));
         }

         modelResult.getRequestHeaders().setProperty(matchTag, buff.toString());
      }
   }

   private final boolean uriMatches(long a1, long a2, boolean includeFile) {
      if (includeFile) {
         return a1 == a2;
      }

      long i1 = a1 >>> 32;
      long i2 = a2 >>> 32;
      return i1 == i2;
   }

   private final boolean uriMatches(URI a1, URI a2, boolean includeFile) {
      if (!StringUtilities.strEqualIgnoreCase(a1.getScheme(), a2.getScheme(), 1701707776)) {
         return false;
      }

      String host1 = a1.getAuthority();
      int port1 = 80;
      if (host1 != null) {
         int tempIndex = host1.indexOf(58);
         if (tempIndex != -1) {
            String tempHost1 = host1.substring(0, tempIndex);
            port1 = Integer.parseInt(host1.substring(tempIndex + 1));
            host1 = tempHost1;
         }
      }

      String host2 = a2.getAuthority();
      int port2 = 80;
      if (host2 != null) {
         int tempIndex = host2.indexOf(58);
         if (tempIndex != -1) {
            String tempHost2 = host2.substring(0, tempIndex);
            port2 = Integer.parseInt(host2.substring(tempIndex + 1));
            host2 = tempHost2;
         }
      }

      if (!StringUtilities.strEqualIgnoreCase(host1, host2, 1701707776)) {
         return false;
      }

      if (port1 != port2) {
         return false;
      }

      String path1 = a1.getPath();
      String path2 = a2.getPath();
      if (includeFile) {
         return StringUtilities.strEqual(path1, path2);
      }

      if (path1 == path2) {
         return true;
      }

      if (path1 != null && path2 != null) {
         if (StringUtilities.strEqual(path1, path2)) {
            return true;
         }

         int nameStart = path1.lastIndexOf(47);
         if (nameStart != -1) {
            path1 = path1.substring(0, nameStart);
         }

         int fromIndex = path2.length() - 1;
         nameStart = path2.lastIndexOf(47, fromIndex);
         if (nameStart == 0) {
            nameStart = 0;
         }

         while (fromIndex >= 0) {
            if (StringUtilities.strEqual(path1, path2.substring(0, nameStart))) {
               return true;
            }

            fromIndex = nameStart - 1;
            nameStart = path2.lastIndexOf(47, fromIndex);
         }

         return false;
      } else {
         return false;
      }
   }

   public RawDataCache() {
      this._stackManager.reinitialize();
      RIMPersistentStore.destroyPersistentObject(-4455003596529320285L);
      this._shortTermCache = new ShortTermCache(GeneralProperty.getCurrentPropertyAsInt(31) * 1024);
      this._longTermCache = new LongTermCache();
      BrowserDaemonRegistry.addBrowserStateListener(this);
      PersistentContent.addWeakListener(this);
      GeneralProperty.addListener(this);
   }
}
