package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Application;

class BackgroundDialog$DialogDisplayRunnable$WaitForRunnableModalEventThread$1 extends Thread {
   private final BackgroundDialog$DialogDisplayRunnable$WaitForRunnableModalEventThread this$0;

   BackgroundDialog$DialogDisplayRunnable$WaitForRunnableModalEventThread$1(BackgroundDialog$DialogDisplayRunnable$WaitForRunnableModalEventThread _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._runnable.run();
      this.this$0._runnableFinished = true;
      Application.getApplication().invokeLater(new BackgroundDialog$DialogDisplayRunnable$WaitForRunnableModalEventThread$1$1(this));
   }
}
