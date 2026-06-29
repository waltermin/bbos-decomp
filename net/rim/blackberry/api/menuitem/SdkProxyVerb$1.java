package net.rim.blackberry.api.menuitem;

class SdkProxyVerb$1 implements Runnable {
   private final Object val$externalContext;
   private final SdkProxyVerb this$0;

   SdkProxyVerb$1(SdkProxyVerb _1, Object _2) {
      this.this$0 = _1;
      this.val$externalContext = _2;
   }

   @Override
   public void run() {
      this.this$0._ami.run(this.val$externalContext);
   }
}
