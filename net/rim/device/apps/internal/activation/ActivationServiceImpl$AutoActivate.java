package net.rim.device.apps.internal.activation;

final class ActivationServiceImpl$AutoActivate implements Runnable {
   String[] _args;

   ActivationServiceImpl$AutoActivate(String[] args) {
      this._args = args;
   }

   @Override
   public final void run() {
      ActivationApp.run(this._args);
   }
}
