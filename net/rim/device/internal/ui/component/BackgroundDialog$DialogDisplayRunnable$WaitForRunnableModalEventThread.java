package net.rim.device.internal.ui.component;

import net.rim.device.api.system.ModalEventThread;

final class BackgroundDialog$DialogDisplayRunnable$WaitForRunnableModalEventThread extends ModalEventThread {
   private Runnable _runnable;
   private boolean _runnableFinished;

   public BackgroundDialog$DialogDisplayRunnable$WaitForRunnableModalEventThread(Runnable runnable) {
      this._runnable = runnable;
   }

   @Override
   protected final boolean shouldExit() {
      return this._runnableFinished;
   }

   @Override
   public final void start() {
      new BackgroundDialog$DialogDisplayRunnable$WaitForRunnableModalEventThread$1(this).start();
      super.start();
   }
}
