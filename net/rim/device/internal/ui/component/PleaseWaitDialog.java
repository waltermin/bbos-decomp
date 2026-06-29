package net.rim.device.internal.ui.component;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.vm.Process;

public class PleaseWaitDialog extends PopupScreen {
   private boolean _displayed;
   private PleaseWaitWorkerThread _workerThread;
   private Throwable _throwable;
   private RichTextField _messageField;
   private Process _workerThreadProcess;
   ResourceBundleFamily _rb = ResourceBundle.getBundle(3711053710409943671L, "net.rim.device.internal.resource.UI");

   public PleaseWaitDialog(PleaseWaitWorkerThread workerThread) {
      this(null, workerThread, null);
   }

   public PleaseWaitDialog(PleaseWaitWorkerThread workerThread, Process workerThreadProcess) {
      this(null, workerThread, workerThreadProcess);
   }

   public PleaseWaitDialog(String message, PleaseWaitWorkerThread workerThread) {
      this(message, workerThread, null);
   }

   public PleaseWaitDialog(String message, PleaseWaitWorkerThread workerThread, Process workerThreadProcess) {
      super(new DialogFieldManager());
      if (message == null) {
         message = this._rb.getString(27);
      }

      this._messageField = new RichTextField(message, 36028797018963968L);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage(this._messageField);
      dfm.setIcon(new BitmapField(Bitmap.getPredefinedBitmap(3)));
      this._throwable = null;
      this._workerThread = workerThread;
      this._workerThreadProcess = workerThreadProcess;
      this._workerThread.setDialog(this);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         synchronized (this) {
            this.notify();
            this._displayed = true;
         }
      }
   }

   synchronized void waitForDialog() {
      if (!this._displayed) {
         try {
            this.wait();
         } catch (InterruptedException var2) {
         }
      }

      this._displayed = false;
   }

   public void display() {
      Application app = Application.getApplication();
      if (!app.hasEventThread()) {
         throw new IllegalStateException("App has no event thread");
      }

      if (app.isEventThread()) {
         if (this._workerThreadProcess != null) {
            this._workerThreadProcess.addThread(this._workerThread);
         } else {
            this._workerThread.start();
         }

         Ui.getUiEngine().pushModalScreen(this);
      } else {
         try {
            this._workerThread.doWork();
         } catch (Throwable t) {
            this.setThrowable(t);
         }
      }
   }

   public void setMessage(String message) {
      this._messageField.setText(message);
   }

   void setThrowable(Throwable t) {
      this._throwable = t;
   }

   public Throwable getThrowable() {
      return this._throwable;
   }
}
