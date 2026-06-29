package com.fourthpass.wapstack.wsp;

final class WSPClientSession$WSPExecuteThread extends Thread {
   private WSPContextObject _object;
   private int _type;
   private final WSPClientSession this$0;

   public WSPClientSession$WSPExecuteThread(WSPClientSession _1, int type, WSPContextObject obj) {
      this.this$0 = _1;
      this._type = type;
      this._object = obj;
   }

   @Override
   public final void run() {
      boolean result = false;
      Object obj = null;

      label36:
      try {
         switch (this._type) {
            case -1:
               break;
            case 0:
            default:
               result = this.this$0.connectRequest(this._object.getHeaders(), this._object.getConnectCapabilities());
               break;
            case 1:
               result = this.this$0.resumeRequest(this._object.getResumeId(), this._object.getHeaders());
               break;
            case 2:
               WSPMethod method = this._object.getMethod();
               this.this$0.invokeMethod(method);
               obj = method.getResult(this._object.getHeaders());
         }
      } finally {
         break label36;
      }

      synchronized (this._object) {
         this._object.setCompleted();
         this._object.setResultObject(obj);
         this._object.setResult(result);
         this._object.notify();
      }
   }
}
