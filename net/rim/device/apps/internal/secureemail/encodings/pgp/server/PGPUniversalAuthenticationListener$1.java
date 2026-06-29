package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

class PGPUniversalAuthenticationListener$1 extends Thread {
   private final PGPUniversalAuthenticationListener this$0;

   PGPUniversalAuthenticationListener$1(PGPUniversalAuthenticationListener _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.verifyRequiredServerThread();
   }
}
