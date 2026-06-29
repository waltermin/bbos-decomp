package net.rim.wica.runtime.comm.internal;

class OutgoingRequestProcessor$1 implements Runnable {
   private final OutgoingRequestProcessor this$0;

   OutgoingRequestProcessor$1(OutgoingRequestProcessor this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      this.this$0._deferredQueues.clear();
   }
}
