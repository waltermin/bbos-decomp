package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Application;

public class PleaseWaitWorkerThread extends Thread {
   private PleaseWaitWorkerThread$ScreenPopper _screenPopper = new PleaseWaitWorkerThread$ScreenPopper(this);
   private Application _application;
   public PleaseWaitDialog _pleaseWaitDialog;

   public void setDialog(PleaseWaitDialog pleaseWaitDialog) {
      this._pleaseWaitDialog = pleaseWaitDialog;
      this._application = Application.getApplication();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      boolean var5 = false /* VF: Semaphore variable */;

      label27: {
         try {
            var5 = true;
            this.doWork();
            var5 = false;
            break label27;
         } catch (Throwable t) {
            this.setThrowable(t);
            var5 = false;
         } finally {
            if (var5) {
               this._pleaseWaitDialog.waitForDialog();
               this._application.invokeLater(this._screenPopper);
            }
         }

         this._pleaseWaitDialog.waitForDialog();
         this._application.invokeLater(this._screenPopper);
         return;
      }

      this._pleaseWaitDialog.waitForDialog();
      this._application.invokeLater(this._screenPopper);
   }

   protected void doWork() {
      throw null;
   }

   protected void setThrowable(Throwable t) {
      this._pleaseWaitDialog.setThrowable(t);
   }
}
