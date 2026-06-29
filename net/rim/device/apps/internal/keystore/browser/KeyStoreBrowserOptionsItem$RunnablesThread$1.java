package net.rim.device.apps.internal.keystore.browser;

class KeyStoreBrowserOptionsItem$RunnablesThread$1 implements Runnable {
   private final KeyStoreBrowserOptionsItem$RunnablesThread this$1;

   KeyStoreBrowserOptionsItem$RunnablesThread$1(KeyStoreBrowserOptionsItem$RunnablesThread _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.this$0.loadCertificates(1);
   }
}
