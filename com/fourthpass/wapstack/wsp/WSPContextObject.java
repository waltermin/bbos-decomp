package com.fourthpass.wapstack.wsp;

public final class WSPContextObject {
   private boolean _completed;
   private boolean _result;
   private Object _object;
   private WSPHeaders _headers;
   private long _resumeId;
   private WSPCapabilities _capabilities;
   private WSPMethod _method;

   public final boolean getResult() {
      return this._result;
   }

   public final void setResult(boolean value) {
      this._result = value;
   }

   public final Object getResultObject() {
      return this._object;
   }

   public final void setResultObject(Object anObject) {
      this._object = anObject;
   }

   protected final long getResumeId() {
      return this._resumeId;
   }

   protected final WSPHeaders getHeaders() {
      return this._headers;
   }

   protected final WSPCapabilities getConnectCapabilities() {
      return this._capabilities;
   }

   protected final void setConnectObjects(WSPHeaders headers, WSPCapabilities cap) {
      this._capabilities = cap;
      this._headers = headers;
   }

   protected final void setResumeObjects(long id, WSPHeaders headers) {
      this._resumeId = id;
      this._headers = headers;
   }

   protected final void setMethodObjects(WSPMethod method, WSPHeaders resultHeaders) {
      this._method = method;
      this._headers = resultHeaders;
   }

   protected final WSPMethod getMethod() {
      return this._method;
   }

   public final boolean getCompleted() {
      return this._completed;
   }

   protected final void setCompleted() {
      this._completed = true;
   }
}
