package net.rim.device.apps.internal.bis.api.io.http;

import java.util.Hashtable;

public final class HttpResponse {
   private int _responseCode;
   private byte[] _responsePayload;
   private String _responseContentType;
   private Hashtable _responsePropertiesMap;
   private String _location;

   HttpResponse(int responseCode, byte[] responsePayload, String responseContentType, Hashtable responsePropertiesMap, String resourceLocation) {
      this._responseCode = responseCode;
      this._responsePayload = responsePayload;
      this._responseContentType = responseContentType;
      this._responsePropertiesMap = responsePropertiesMap;
      this._location = resourceLocation;
   }

   public final int getHttpResponseCode() {
      return this._responseCode;
   }

   public final byte[] getResponsePayload() {
      return this._responsePayload;
   }

   public final String getResourceLocation() {
      return this._location;
   }

   public final String getResponseProperty(String key) {
      return (String)(this._responsePropertiesMap == null ? null : this._responsePropertiesMap.get(key));
   }
}
