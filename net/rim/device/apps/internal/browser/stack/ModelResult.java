package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.vm.Persistable;

public final class ModelResult implements Persistable, EncryptableProvider {
   private Object _urlEncoding;
   private int _renderingFlags;
   private HttpHeaders _requestHeaders;
   private byte[] _postData;
   private Object _context;
   private CacheResult _cacheResult;
   private String _label;
   private boolean _isHomePage;
   private String _configUid;
   private String _transportCID;
   private int _configType = -1;
   private String _method;

   public ModelResult(String url, int flags, HttpHeaders requestHeaders) {
      if (url == null) {
         this._urlEncoding = PersistentContent.encode("", true, true);
      } else {
         this._urlEncoding = PersistentContent.encode(url, true, true);
      }

      this._renderingFlags = flags | 32;
      this._requestHeaders = requestHeaders;
   }

   public final void setClearContext(boolean clearContext) {
      if (clearContext) {
         this._renderingFlags |= 256;
      } else {
         this._renderingFlags &= -257;
      }
   }

   public final void setLabel(String label) {
      this._label = label;
   }

   public final String getLabel() {
      return this._label;
   }

   public final synchronized void postponeSecondaryResources() {
      this._renderingFlags |= 512;
   }

   public final void setContext(Object context) {
      this._context = context;
   }

   public final void setCacheResult(CacheResult cacheResult) {
      this._cacheResult = cacheResult;
   }

   public final void clearHistoryFlags() {
      this._renderingFlags &= -24577;
   }

   public final void setNavigation(int navigation) {
      this._renderingFlags = this._renderingFlags & -8 | navigation & 7;
   }

   public final int getNavigation() {
      return this._renderingFlags & 7;
   }

   public final void setPostData(byte[] postData) {
      this._postData = postData;
   }

   public final void setMethod(String method) {
      this._method = method;
   }

   public final String getMethod() {
      return this._method;
   }

   public final void setURL(String url) {
      this._urlEncoding = PersistentContent.encode(url, true, true);
   }

   public final String getURL() {
      return PersistentContent.decodeString(this._urlEncoding);
   }

   public final int getRenderingFlags() {
      if (this._cacheResult != null && this._cacheResult.isCached()) {
         this._renderingFlags |= 4096;
      }

      return this._renderingFlags;
   }

   public final void setEmbedded(boolean embedded) {
      if (embedded) {
         this._renderingFlags |= 16;
      } else {
         this._renderingFlags &= -17;
      }
   }

   public final Object getContext() {
      return this._context;
   }

   public final HttpHeaders getRequestHeaders() {
      return this._requestHeaders;
   }

   public final void setRequestHeaders(HttpHeaders requestHeaders) {
      this._requestHeaders = requestHeaders;
   }

   public final byte[] getPostData() {
      return this._postData;
   }

   public final CacheResult getCacheResult() {
      return this._cacheResult;
   }

   public final boolean isHomePage() {
      return this._isHomePage;
   }

   public final void setHomePage(boolean isHomePage) {
      this._isHomePage = isHomePage;
   }

   public final String getConfigUID() {
      return this._configUid;
   }

   public final void setConfigUID(String uid) {
      this._configUid = uid;
   }

   public final String getTransportCID() {
      return this._transportCID;
   }

   public final void setTransportCID(String cid) {
      this._transportCID = cid;
   }

   public final int getConfigType() {
      return this._configType;
   }

   public final void setConfigType(int configType) {
      this._configType = configType;
   }

   public final ModelResult clone() {
      HttpHeaders newRequestHeaders = null;
      if (this._requestHeaders != null) {
         newRequestHeaders = (HttpHeaders)(new Object());
         int size = this._requestHeaders.size();

         for (int i = 0; i < size; i++) {
            newRequestHeaders.setProperty(this._requestHeaders.getPropertyKey(i), this._requestHeaders.getPropertyValue(i));
         }
      }

      ModelResult clone = new ModelResult(PersistentContent.decodeString(this._urlEncoding), this._renderingFlags, newRequestHeaders);
      clone._postData = this._postData;
      clone._isHomePage = this._isHomePage;
      clone._configUid = this._configUid;
      clone._transportCID = this._transportCID;
      clone._configType = this._configType;
      if (this._cacheResult != null) {
         clone._cacheResult = this._cacheResult;
      }

      return clone;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._urlEncoding, compress, encrypt)
         && (this._requestHeaders == null || this._requestHeaders.checkCrypt())
         && (this._cacheResult == null || this._cacheResult.checkCrypt(compress, encrypt));
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._urlEncoding = PersistentContent.reEncode(this._urlEncoding, compress, encrypt);
      if (this._requestHeaders != null) {
         this._requestHeaders.reCrypt();
      }

      if (this._cacheResult != null) {
         this._cacheResult.reCrypt(compress, encrypt);
      }

      return null;
   }
}
