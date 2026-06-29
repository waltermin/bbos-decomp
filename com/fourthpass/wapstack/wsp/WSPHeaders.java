package com.fourthpass.wapstack.wsp;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public final class WSPHeaders {
   private byte[] _attrList;
   private HttpHeaders _headers;
   private boolean _postData;

   public WSPHeaders() {
   }

   public WSPHeaders(HttpHeaders headers, Object postData) {
      this._postData = postData != null;
      this._headers = headers;
   }

   public WSPHeaders(byte[] attrList) {
      this._attrList = attrList;
   }

   public final int getLength() {
      if (this._attrList == null) {
         this.encodeHeaders();
      }

      return this._attrList == null ? 0 : this._attrList.length;
   }

   public final byte[] getAttributeList() {
      if (this._attrList == null) {
         this.encodeHeaders();
      }

      return this._attrList;
   }

   public final void setAttributeList(byte[] attrList) {
      this._attrList = attrList;
      this._headers = null;
   }

   public final HttpHeaders getHeaders() {
      if (this._headers == null) {
         this._headers = (HttpHeaders)(new Object());
         WSPHeaderDecoder oldHeaderDecoder = (WSPHeaderDecoder)(new Object(this._headers));
         oldHeaderDecoder.decode(this._attrList, false);
      }

      return this._headers;
   }

   public final void setHeaders(HttpHeaders headers) {
      this._headers = headers;
      this._attrList = null;
   }

   private final void encodeHeaders() {
      WSPHeaderEncoder headerEncoder = new WSPHeaderEncoder();
      int contentTypeIndex = -1;
      if (this._postData) {
         int i = 0;

         while (true) {
            String key = this._headers.getPropertyKey(i);
            if (key == null) {
               break;
            }

            if (StringUtilities.strEqualIgnoreCase(key, "content-type", 1701707776)) {
               contentTypeIndex = i;
               break;
            }

            i++;
         }

         String contentType = null;
         if (contentTypeIndex == -1) {
            contentType = "application/x-www-form-urlencoded";
         } else {
            contentType = this._headers.getPropertyValue(contentTypeIndex);
         }

         headerEncoder.encode("content-type", contentType);
      }

      int i = 0;

      while (true) {
         if (i == contentTypeIndex) {
            i++;
         }

         String key = this._headers.getPropertyKey(i);
         if (key == null) {
            byte[] encodedHeaders = headerEncoder.getEncodedHeader();
            if (this._postData) {
               System.arraycopy(encodedHeaders, 1, encodedHeaders, 0, encodedHeaders.length - 1);
               Array.resize(encodedHeaders, encodedHeaders.length - 1);
            }

            this._attrList = encodedHeaders;
            return;
         }

         headerEncoder.encode(key, this._headers.getPropertyValue(i));
         i++;
      }
   }
}
