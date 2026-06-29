package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.browser.util.Asserts;

public final class CacheNode implements Persistable {
   private CacheNode _prev;
   private CacheNode _next;
   private Object _urlWithoutFragmentEncoding;
   private CacheResult _contents;
   private long _creationDate;
   private int _timesAccessed;
   private long _accessedDate;
   private boolean _makeAvailableOffline;
   private CacheNode[] _childNodes;

   public CacheNode(String urlWithoutFragment, CacheResult contents) {
      this(urlWithoutFragment, contents, false);
   }

   public CacheNode(String urlWithoutFragment, CacheResult contents, boolean makeAvailableOffline) {
      Asserts.productionArgumentAssert(contents != null);
      this._contents = contents;
      this._creationDate = System.currentTimeMillis();
      this._accessedDate = this._creationDate;
      this._urlWithoutFragmentEncoding = urlWithoutFragment;
      this._makeAvailableOffline = makeAvailableOffline;
      this._timesAccessed = 1;
   }

   public final void addChildReference(CacheNode node) {
      if (this._childNodes == null) {
         this._childNodes = new CacheNode[0];
      }

      Arrays.add(this._childNodes, node);
   }

   public final CacheNode[] getChildReferences() {
      return this._childNodes;
   }

   public final CacheResult getContents() {
      return this._contents;
   }

   public final String getUrl() {
      return PersistentContent.decodeString(this._urlWithoutFragmentEncoding);
   }

   public final long getCreationDate() {
      return this._creationDate;
   }

   public final void setCreationDate(long creationDate) {
      this._creationDate = creationDate;
   }

   public final void setUrl(String url) {
      this._urlWithoutFragmentEncoding = PersistentContent.encode(url, false, true);
   }

   public final CacheNode getNext() {
      return this._next;
   }

   public final CacheNode getPrev() {
      return this._prev;
   }

   public final void setPrev(CacheNode prev) {
      this._prev = prev;
   }

   public final void setNext(CacheNode next) {
      this._next = next;
   }

   public final int getTimesAccessed() {
      return this._timesAccessed;
   }

   public final void nodeAccessed() {
      this._timesAccessed++;
   }

   public final long getAccessedDate() {
      return this._accessedDate;
   }

   public final void setAccessedDate(long aTime) {
      this._accessedDate = aTime;
   }

   public final long getExpiryDate() {
      return this._contents.getExpiration();
   }

   public final boolean getAvailableOffline() {
      return this._makeAvailableOffline;
   }

   public final void setAvailableOffline(boolean availableOffline) {
      this._makeAvailableOffline = availableOffline;
   }

   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._urlWithoutFragmentEncoding, compress, encrypt)
         && (this._contents == null || this._contents.checkCrypt(compress, encrypt));
   }

   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._urlWithoutFragmentEncoding = PersistentContent.reEncode(this._urlWithoutFragmentEncoding, compress, encrypt);
      if (this._contents != null) {
         this._contents.reCrypt(compress, encrypt);
      }

      return null;
   }
}
