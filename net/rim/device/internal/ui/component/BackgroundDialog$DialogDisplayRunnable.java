package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.Ui;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Process;

class BackgroundDialog$DialogDisplayRunnable implements Runnable, PopupDialogClosedListener {
   private PopupDialog _dialog;
   private boolean _dialogOnDisplay;
   private boolean _forceRunInProxy;

   public PopupDialog getDialog() {
      throw null;
   }

   public void forceRunInProxy() {
      this._forceRunInProxy = true;
   }

   public final PopupDialog runInCorrectProcess() {
      Application application;
      try {
         application = Application.getApplication();
      } catch (IllegalStateException e) {
         application = null;
      }

      if (application == null) {
         Proxy.getInstance().invokeAndWait(this);
      } else if (!application.hasEventThread()) {
         boolean wasForeground = false;
         if (application.isForeground()) {
            wasForeground = true;
            application.requestBackground();
         }

         Proxy.getInstance().invokeAndWait(this);
         if (wasForeground) {
            application.requestForeground();
         }
      } else if (this._forceRunInProxy) {
         boolean isMIDlet = CodeModuleManager.isMidlet(Process.currentProcess().getModuleHandle());
         if (!isMIDlet && application.isEventThread()) {
            synchronized (application.getAppEventLock()) {
               application.startModalEventThread(
                  new BackgroundDialog$DialogDisplayRunnable$WaitForRunnableModalEventThread(new BackgroundDialog$DialogDisplayRunnable$1(this))
               );
            }
         } else {
            Proxy.getInstance().invokeAndWait(this);
         }
      } else {
         this.run();
      }

      return this._dialog;
   }

   @Override
   public void run() {
      this._dialog = this.getDialog();
      int pushGlobalFlags = 0;
      Application application = Application.getApplication();
      boolean isEventThread = application.isEventThread();
      if (isEventThread) {
         pushGlobalFlags |= 1;
      } else {
         this._dialog.setPopupDialogClosedListener(this);
         this._dialogOnDisplay = true;
      }

      synchronized (application.getAppEventLock()) {
         Ui.getUiEngine().pushGlobalScreen(this._dialog, this._dialog.getStatusPriority(), pushGlobalFlags);
      }

      if (!isEventThread) {
         synchronized (this) {
            while (this._dialogOnDisplay) {
               try {
                  this.wait();
               } catch (InterruptedException var7) {
               }
            }
         }
      }
   }

   @Override
   public final synchronized void dialogClosed(PopupDialog popupDialog, int choice) {
      this._dialogOnDisplay = false;
      this.notifyAll();
   }
}
