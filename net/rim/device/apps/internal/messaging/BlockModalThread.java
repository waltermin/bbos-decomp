package net.rim.device.apps.internal.messaging;

import net.rim.device.api.system.ModalEventThread;

final class BlockModalThread extends ModalEventThread {
   SetFilterRunnable _sfr;

   public BlockModalThread(SetFilterRunnable sfr) {
      this._sfr = sfr;
   }

   @Override
   protected final boolean shouldExit() {
      return this._sfr.isDone();
   }
}
