package net.rim.device.apps.internal.iota;

final class MMCProcessor$BufferRequestRunnable implements Runnable {
   private IOTARequest _iotaRequest;
   private final MMCProcessor this$0;

   public MMCProcessor$BufferRequestRunnable(MMCProcessor _1, IOTARequest iotaRequest) {
      this.this$0 = _1;
      this._iotaRequest = iotaRequest;
   }

   @Override
   public final void run() {
      this.this$0._buffer.addRequest(this._iotaRequest);
   }
}
