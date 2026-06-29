package net.rim.device.internal.io.file;

import net.rim.device.api.io.http.HttpHeaders;

final class UpdateRequest {
   String _uri;
   HttpHeaders _headers;

   public UpdateRequest(String uri, HttpHeaders headers) {
      this._uri = uri;
      this._headers = headers;
   }
}
