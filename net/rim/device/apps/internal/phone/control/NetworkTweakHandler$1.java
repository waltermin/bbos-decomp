package net.rim.device.apps.internal.phone.control;

class NetworkTweakHandler$1 implements Runnable {
   private final NetworkTweakHandler this$0;

   NetworkTweakHandler$1(NetworkTweakHandler _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      new NetworkTweakHandler$SIMFriendlyNameReader();
   }
}
