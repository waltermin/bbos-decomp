package net.rim.device.internal.media;

import net.rim.vm.WeakReference;

class PlayerImpl$CleanUpRunnable implements Runnable {
   WeakReference _wr;

   public PlayerImpl$CleanUpRunnable(PlayerImpl player) {
      this._wr = new WeakReference(player);
   }

   @Override
   public void run() {
      Object player = this._wr.get();
      if (player instanceof PlayerImpl) {
         ((PlayerImpl)player).cleanUp();
      }
   }
}
