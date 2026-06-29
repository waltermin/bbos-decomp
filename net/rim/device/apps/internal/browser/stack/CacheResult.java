package net.rim.device.apps.internal.browser.stack;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.browser.multipart.MultipartConverter;
import net.rim.device.apps.internal.browser.util.DateCache;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.vm.Array;
import net.rim.vm.Persistable;

public final class CacheResult implements Persistable, EncryptableProvider {
   private Object _urlWithoutFragmentEncoding;
   private Object _cachedData;
   private Object _transcodedData;
   private CacheResult _parentCacheResult;
   private String _transcodedContentType;
   private HttpHeaders _responseHeaders;
   private int _status;
   private String _exceptionString;
   private String _exceptionDetail;
   private String _responseMessage;
   private long _timestamp;
   private long _expiration;
   private long _lastModified;
   private int _flags;
   private int _packet;
   private int _offset;
   private int _length;
   private static final long DEFAULT_EXPIRY = 43200000L;
   private static final int CACHE_RESULT_VERSION = 1;
   private static final int CACHE_DATA_VERSION = 1;
   private static final int FLAG_POST_METHOD = 1;
   private static final int FLAG_SECURED = 2;
   private static final int FLAG_DIRTY = 4;
   private static final int FLAG_LOCAL_CONTENT = 8;
   private static final int FLAG_MULTIPART_ROOT_NODE = 16;
   private static final int FLAG_VERIFIED_CHILD_REF = 32;
   private static final int FLAG_CACHED = 64;

   public CacheResult(String urlWithoutFragment, byte[] data, HttpHeaders responseHeaders, int status) {
      this(urlWithoutFragment, data, responseHeaders, status, true);
   }

   public CacheResult(String urlWithoutFragment, byte[] data, HttpHeaders responseHeaders, int status, boolean groupData) {
      this._timestamp = System.currentTimeMillis();
      if (urlWithoutFragment != null && urlWithoutFragment.indexOf(63) != -1) {
         this._expiration = 0;
      } else {
         this._expiration = this._timestamp + 43200000;
      }

      this._urlWithoutFragmentEncoding = urlWithoutFragment;
      if (data != null) {
         this._cachedData = new Pipe(data, data.length, groupData);
      }

      this.setResponseHeaders(responseHeaders);
      this._status = status;
   }

   public CacheResult(String urlWithoutFragment, int packet, int offset, int length, CacheResult parentCacheResult, HttpHeaders responseHeaders, int status) {
      this(urlWithoutFragment, null, responseHeaders, status);
      this._packet = packet;
      this._offset = offset;
      this._length = length;
      this._parentCacheResult = parentCacheResult;
   }

   public CacheResult(Exception e, String msg, String detail) {
      this("", null, new HttpHeaders(), 400);
      if (e != null) {
         this._exceptionString = e.toString();
         this._exceptionDetail = detail;
      }

      if (msg != null) {
         this._exceptionString = msg;
         this._exceptionDetail = detail;
      }
   }

   public CacheResult(String urlWithoutFragment) {
      this._urlWithoutFragmentEncoding = PersistentContent.encode(urlWithoutFragment, false, true);
      this._timestamp = System.currentTimeMillis();
      if (urlWithoutFragment != null && urlWithoutFragment.indexOf(63) != -1) {
         this._expiration = 0;
      } else {
         this._expiration = this._timestamp + 43200000;
      }
   }

   public final String getExceptionString() {
      return this._exceptionString;
   }

   public final void setExceptionString(String exceptionString) {
      this._exceptionString = exceptionString;
   }

   public final String getExceptionDetail() {
      return this._exceptionDetail;
   }

   public final void addExceptionDetail(String exceptionDetail) {
      if (this._exceptionDetail == null) {
         this._exceptionDetail = exceptionDetail;
      } else {
         this._exceptionDetail = this._exceptionDetail + '\n' + exceptionDetail;
      }
   }

   final void removeParentReference() {
      byte[] data = this.getDataAsArray();
      this.setData(data, data.length);
      this._parentCacheResult = null;
      this._packet = 0;
      this._offset = 0;
      this._length = 0;
   }

   public final void setResponseMessage(String responseMessage) {
      this._responseMessage = responseMessage;
   }

   public final String getResponseMessage() {
      return this._responseMessage;
   }

   public final void setResponseHeaders(HttpHeaders responseHeaders) {
      this._responseHeaders = responseHeaders;
      if (responseHeaders != null) {
         this.recalculateLastModified();
         this.recalculateExpiry(responseHeaders.getPropertyValue(HeaderParser.EXPIRES));
         this.setRootNode(MultipartConverter.getMultipartType(responseHeaders.getPropertyValue("content-type")) != 0);
      }
   }

   public final void updateCacheResult(HttpHeaders responseHeaders) {
      this._timestamp = System.currentTimeMillis();
      String urlWithoutFragment = this.getURLWithoutFragment();
      if (urlWithoutFragment != null && urlWithoutFragment.indexOf(63) != -1) {
         this._expiration = 0;
      } else {
         this._expiration = this._timestamp + 43200000;
      }

      this._lastModified = 0;
      this._responseHeaders.setProperties(responseHeaders);
      this.setResponseHeaders(this._responseHeaders);
   }

   public final String getURLWithoutFragment() {
      return PersistentContent.decodeString(this._urlWithoutFragmentEncoding);
   }

   public final void setURLWithoutFragment(String urlWithoutFragment) {
      this._urlWithoutFragmentEncoding = PersistentContent.encode(urlWithoutFragment, false, true);
   }

   public final Pipe getData() {
      if (!(this._cachedData instanceof Pipe)) {
         if (!(this._cachedData instanceof CacheResult$EncryptedPipe)) {
            Pipe result = this.getTranscodedData();
            if (result != null) {
               return result;
            } else {
               return this._parentCacheResult != null ? this._parentCacheResult.getData() : null;
            }
         } else {
            return ((CacheResult$EncryptedPipe)this._cachedData).getDecryptedPipe();
         }
      } else {
         return (Pipe)this._cachedData;
      }
   }

   final boolean isDataClosed() {
      return !(this._cachedData instanceof Pipe) ? true : ((Pipe)this._cachedData).isClosed();
   }

   public final Pipe getTranscodedData() {
      if (!(this._transcodedData instanceof Pipe)) {
         return !(this._transcodedData instanceof CacheResult$EncryptedPipe) ? null : ((CacheResult$EncryptedPipe)this._transcodedData).getDecryptedPipe();
      } else {
         return (Pipe)this._transcodedData;
      }
   }

   public final String getTranscodedContentType() {
      return this._transcodedContentType;
   }

   public final boolean hasUnecryptedPipe() {
      return this._cachedData instanceof Pipe;
   }

   public final CacheResult getParentCacheResult() {
      return this._parentCacheResult;
   }

   public final int getDataLength() {
      if (this._parentCacheResult != null) {
         return this._length;
      } else if (!(this._cachedData instanceof Pipe)) {
         return !(this._cachedData instanceof CacheResult$EncryptedPipe) ? 0 : ((CacheResult$EncryptedPipe)this._cachedData).getLength();
      } else {
         return ((Pipe)this._cachedData).getLength();
      }
   }

   public final byte[] getDataAsArray() {
      if (this._parentCacheResult != null) {
         InputStream in = this.getStream();
         if (in != null) {
            try {
               byte[] result = new byte[this._length];
               in.read(result);
               in.close();
               return result;
            } finally {
               ;
            }
         }
      } else {
         Pipe ptr = this.getData();
         if (ptr != null) {
            return ptr.toArray();
         }
      }

      return null;
   }

   public final int getSize() {
      return this.getDataLength();
   }

   public final InputStream getStream() {
      Pipe pipe = this.getData();
      if (pipe != null) {
         return this._parentCacheResult != null ? pipe.getInputStream(this._packet, this._offset, this._length) : pipe.getInputStream();
      } else {
         return null;
      }
   }

   public final void setTranscodedData(Pipe data, String contentType) {
      if ((this._flags & 16) == 0) {
         this._cachedData = data;
         if (this._responseHeaders != null) {
            this._responseHeaders.setProperty("content-type", contentType);
            if (this._responseHeaders.getPropertyValue("Content-Encoding") != null) {
               this._responseHeaders.setProperty("Content-Encoding", "identity");
               return;
            }
         }
      } else {
         this._transcodedData = data;
         this._transcodedContentType = contentType;
      }
   }

   public final void setData(Pipe data) {
      this._cachedData = data;
   }

   public final void setData(byte[] data, int length) {
      this.setData(new Pipe(data, length));
   }

   public final HttpHeaders getResponseHeaders() {
      return this._responseHeaders;
   }

   public final int getStatus() {
      return this._status;
   }

   public final void setStatus(int status) {
      this._status = status;
   }

   public final long getTimestamp() {
      return this._timestamp;
   }

   public final void setTimestamp(long timestamp) {
      this._timestamp = timestamp;
   }

   public final boolean isPostMethod() {
      return (this._flags & 1) != 0;
   }

   public final void setPostMethod(boolean postMethod) {
      this._flags &= -2;
      if (postMethod) {
         this._flags |= 1;
      }
   }

   public final boolean getSecured() {
      return (this._flags & 2) != 0;
   }

   public final void setSecured(boolean value) {
      this._flags &= -3;
      if (value) {
         this._flags |= 2;
      }
   }

   public final boolean isDirty() {
      return (this._flags & 4) != 0;
   }

   public final void setDirty(boolean dirty) {
      this._flags &= -5;
      if (dirty) {
         this._flags |= 4;
      }
   }

   final boolean isLocalContent() {
      return (this._flags & 8) != 0;
   }

   final void setLocalContent(boolean value) {
      this._flags &= -9;
      if (value) {
         this._flags |= 8;
      }
   }

   final boolean isCached() {
      return (this._flags & 64) != 0;
   }

   final void setCached(boolean value) {
      this._flags &= -65;
      if (value) {
         this._flags |= 64;
      }
   }

   final void setRootNode(boolean value) {
      this._flags &= -17;
      if (value) {
         this._flags |= 16;
      }
   }

   public final void setVerifiedChildRef(boolean value) {
      this._flags &= -33;
      if (value) {
         this._flags |= 32;
      }
   }

   public final boolean isVerifiedChildRef() {
      return (this._flags & 32) != 0;
   }

   final boolean isChildNode() {
      return this._parentCacheResult != null;
   }

   public final boolean writeCacheResult(SyncBuffer syncBuffer) {
      try {
         DataBuffer dataBuffer = new DataBuffer(false);
         dataBuffer.setLength(0);
         if (this._urlWithoutFragmentEncoding == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(this.getURLWithoutFragment());
         }

         dataBuffer.writeBoolean(false);
         if (this._responseHeaders == null) {
            dataBuffer.writeCompressedInt(0);
         } else {
            int size = this._responseHeaders.size();
            dataBuffer.writeCompressedInt(size);

            for (int i = 0; i < size; i++) {
               dataBuffer.writeUTF(this._responseHeaders.getPropertyKey(i));
               dataBuffer.writeUTF(this._responseHeaders.getPropertyValue(i));
            }
         }

         if (this._responseMessage == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(this._responseMessage);
         }

         dataBuffer.writeCompressedInt(this._status);
         if (this._exceptionString == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(this._exceptionString);
         }

         if (this._exceptionDetail == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(this._exceptionDetail);
         }

         dataBuffer.writeCompressedLong(this._timestamp);
         boolean postMethod = this.isPostMethod();
         dataBuffer.writeBoolean(postMethod);
         if (postMethod) {
            dataBuffer.writeUTF("");
         }

         dataBuffer.writeBoolean(this.getSecured());
         dataBuffer.writeCompressedInt(1);
         syncBuffer.addBytes(19, dataBuffer.toArray());
         if (this._cachedData != null) {
            byte[] data = this.getData().toArray();
            int fragmentOffset = 0;
            int length = data.length;

            while (length > 0) {
               int fragmentSize;
               if (length > 65520) {
                  fragmentSize = 65520;
               } else {
                  fragmentSize = length;
               }

               dataBuffer.setLength(0);
               dataBuffer.writeByteArray(data, fragmentOffset, fragmentSize);
               dataBuffer.writeCompressedInt(1);
               syncBuffer.addBytes(23, dataBuffer.toArray());
               length -= fragmentSize;
               fragmentOffset += fragmentSize;
            }
         }
      } finally {
         return true;
      }

      return true;
   }

   public static final byte[] readCacheData(SyncBuffer syncBuffer) {
      int fieldType = syncBuffer.getFieldType();
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();

      try {
         dataBuffer.readShort();
         dataBuffer.readByte();
      } finally {
         ;
      }

      byte[] data = null;
      if (fieldType == 15 || fieldType == 23) {
         data = dataBuffer.readByteArray();
      }

      if (fieldType == 23) {
         dataBuffer.readCompressedInt();
      }

      return data;
   }

   public static final CacheResult readCacheResult(SyncBuffer syncBuffer) {
      int fieldType = syncBuffer.getFieldType();
      int position = syncBuffer.getPosition();
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();

      try {
         dataBuffer.readShort();
         dataBuffer.readByte();
      } finally {
         ;
      }

      String urlWithoutFragment = null;
      if (dataBuffer.readBoolean()) {
         urlWithoutFragment = dataBuffer.readUTF();
      }

      byte[] data = null;
      if (fieldType >= 9) {
         if (dataBuffer.readBoolean()) {
            data = dataBuffer.readByteArray();
         }
      } else if (dataBuffer.readBoolean()) {
         data = dataBuffer.readByteArray();
      }

      int numResponseHeaders = dataBuffer.readCompressedInt();
      HttpHeaders responseHeaders = null;
      if (numResponseHeaders > 0) {
         responseHeaders = new HttpHeaders();

         for (int i = 0; i < numResponseHeaders; i++) {
            String key = dataBuffer.readUTF();
            String value = dataBuffer.readUTF();
            responseHeaders.addProperty(key, value);
         }
      }

      String responseMessage = null;
      if (fieldType >= 7 && dataBuffer.readBoolean()) {
         responseMessage = dataBuffer.readUTF();
      }

      int status = dataBuffer.readCompressedInt();
      String exceptionString = null;
      if (dataBuffer.readBoolean()) {
         exceptionString = dataBuffer.readUTF();
      }

      String exceptionDetail = null;
      long timestamp = System.currentTimeMillis();
      if (fieldType >= 7) {
         if (dataBuffer.readBoolean()) {
            exceptionDetail = dataBuffer.readUTF();
         }

         timestamp = dataBuffer.readCompressedLong();
      }

      boolean postMethod = false;
      if (fieldType >= 10) {
         postMethod = dataBuffer.readBoolean();
         if (postMethod) {
            dataBuffer.readUTF();
         }
      }

      boolean secured = false;
      if (fieldType >= 13) {
         secured = dataBuffer.readBoolean();
      }

      if (fieldType == 19) {
         dataBuffer.readCompressedInt();
      }

      syncBuffer.setPosition(position);
      syncBuffer.skipField();
      if (data == null) {
         while (!syncBuffer.isEmpty() && (syncBuffer.getFieldType() == 15 || syncBuffer.getFieldType() == 23)) {
            position = syncBuffer.getPosition();
            byte[] chunk = null;

            try {
               chunk = readCacheData(syncBuffer);
            } finally {
               ;
            }

            if (chunk != null) {
               if (data == null) {
                  data = chunk;
               } else {
                  int offset = data.length;
                  int length = chunk.length;
                  Array.resize(data, offset + length);
                  ByteArrayInputStream bais = new ByteArrayInputStream(chunk);
                  bais.read(data, offset, length);
               }
            }

            syncBuffer.setPosition(position);
            syncBuffer.skipField();
         }
      }

      CacheResult cacheResult = new CacheResult(urlWithoutFragment, data, responseHeaders, status);
      cacheResult.setResponseMessage(responseMessage);
      cacheResult.setExceptionString(exceptionString);
      cacheResult.addExceptionDetail(exceptionDetail);
      cacheResult.setTimestamp(timestamp);
      cacheResult.setPostMethod(postMethod);
      cacheResult.setSecured(secured);
      return cacheResult;
   }

   public final void setExpiration(long expiration) {
      this._expiration = expiration;
      this.recalculateExpiry(null);
   }

   public final void setExpiration(String date) {
      if (this._responseHeaders != null && date != null) {
         this._responseHeaders.setProperty(HeaderParser.EXPIRES, date);
      }

      this.recalculateExpiry(date);
   }

   public final void recalculateLastModified() {
      if (this._responseHeaders != null) {
         this._lastModified = DateCache.parse(this._responseHeaders.getPropertyValue("Last-Modified"));
      } else {
         this._lastModified = 0;
      }
   }

   public final long getLastModified() {
      return this._lastModified;
   }

   public final void recalculateExpiry() {
      this.recalculateExpiry(null);
   }

   private final void recalculateExpiry(String date) {
      if (date != null) {
         this._expiration = DateCache.parse(date);
      }

      if (this._responseHeaders != null) {
         if (date == null && this._lastModified != 0) {
            long freshnessLifetime = (this._timestamp - this._lastModified) / 10;
            if (freshnessLifetime > 43200000) {
               this._expiration = this._timestamp + freshnessLifetime;
            }
         }

         String cacheControlValue = this._responseHeaders.getPropertyValue(HeaderParser.CACHE_CONTROL);
         if (cacheControlValue != null) {
            cacheControlValue = StringUtilities.toLowerCase(cacheControlValue, 1701707776);
            boolean noCache = false;
            int cacheControlValueSize = cacheControlValue.length();
            int valueStart = 0;
            int valueEnd = cacheControlValue.indexOf(44, valueStart);
            if (valueEnd == -1) {
               valueEnd = cacheControlValueSize;
            }

            while (valueStart < cacheControlValueSize) {
               int equalsIndex = StringUtilities.indexOf(cacheControlValue, '=', valueStart, valueEnd);
               int directiveValueStart = -1;
               int directiveValueEnd = -1;
               int nextIndex = valueEnd;
               int directiveEnd;
               if (equalsIndex != -1) {
                  directiveEnd = equalsIndex;
                  if (equalsIndex + 1 < cacheControlValueSize && cacheControlValue.charAt(equalsIndex + 1) == '"') {
                     directiveValueStart = equalsIndex + 2;
                     if (directiveValueStart < cacheControlValueSize) {
                        directiveValueEnd = cacheControlValue.indexOf(34, directiveValueStart);
                     }

                     nextIndex = directiveValueEnd + 1;
                  } else {
                     directiveValueStart = equalsIndex + 1;
                     directiveValueEnd = valueEnd;
                  }
               } else {
                  directiveEnd = valueEnd;
               }

               if (directiveValueStart != -1 && directiveValueEnd == -1
                  || directiveValueStart > cacheControlValueSize
                  || directiveValueEnd > cacheControlValueSize) {
                  break;
               }

               if ((
                     HeaderParser.CACHE_DIRECTIVE_NO_CACHE.length() != directiveEnd - valueStart
                        || !StringUtilities.regionMatches(
                           cacheControlValue,
                           false,
                           valueStart,
                           HeaderParser.CACHE_DIRECTIVE_NO_CACHE,
                           0,
                           HeaderParser.CACHE_DIRECTIVE_NO_CACHE.length(),
                           1701707776
                        )
                  )
                  && (
                     HeaderParser.CACHE_DIRECTIVE_NO_STORE.length() != directiveEnd - valueStart
                        || !StringUtilities.regionMatches(
                           cacheControlValue,
                           false,
                           valueStart,
                           HeaderParser.CACHE_DIRECTIVE_NO_STORE,
                           0,
                           HeaderParser.CACHE_DIRECTIVE_NO_STORE.length(),
                           1701707776
                        )
                  )) {
                  if (HeaderParser.CACHE_DIRECTIVE_MAX_AGE.length() == directiveEnd - valueStart
                     && StringUtilities.regionMatches(
                        cacheControlValue,
                        false,
                        valueStart,
                        HeaderParser.CACHE_DIRECTIVE_MAX_AGE,
                        0,
                        HeaderParser.CACHE_DIRECTIVE_MAX_AGE.length(),
                        1701707776
                     )
                     && directiveValueStart != -1
                     && directiveValueEnd != -1) {
                     label164:
                     try {
                        this._expiration = Long.parseLong(cacheControlValue.substring(directiveValueStart, directiveValueEnd)) * 1000 + this._timestamp;
                     } finally {
                        break label164;
                     }
                  }
               } else if (directiveValueStart == -1 && directiveValueEnd == -1) {
                  noCache = true;
               }

               valueStart = nextIndex + 1;

               while (valueStart < cacheControlValueSize && CharacterUtilities.isSpaceChar(cacheControlValue.charAt(valueStart))) {
                  valueStart++;
               }

               valueEnd = cacheControlValue.indexOf(44, valueStart);
               if (valueEnd == -1) {
                  valueEnd = cacheControlValueSize;
               }
            }

            if (noCache) {
               this._expiration = 0;
            }
         }
      }
   }

   public final long getExpiration() {
      return this._expiration;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._urlWithoutFragmentEncoding, compress, encrypt)
         && (this._responseHeaders == null || this._responseHeaders.checkCrypt())
         && (this._cachedData == null || this._cachedData instanceof CacheResult$EncryptedPipe)
         && (this._transcodedData == null || this._transcodedData instanceof CacheResult$EncryptedPipe);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._urlWithoutFragmentEncoding = PersistentContent.reEncode(this._urlWithoutFragmentEncoding, compress, encrypt);
      if (encrypt && PersistentContent.isEncryptionEnabled()) {
         if (this._responseHeaders != null) {
            this._responseHeaders.reCrypt();
         }

         if (this._cachedData != null && !(this._cachedData instanceof CacheResult$EncryptedPipe)) {
            this._cachedData = new CacheResult$EncryptedPipe(((Pipe)this._cachedData).toSegmentedArray());
         }

         if (this._transcodedData != null && !(this._transcodedData instanceof CacheResult$EncryptedPipe)) {
            this._transcodedData = new CacheResult$EncryptedPipe(((Pipe)this._transcodedData).toSegmentedArray());
         }
      }

      return null;
   }
}
