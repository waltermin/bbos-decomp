package net.rim.tid.im.ui;

import net.rim.device.api.system.Application;
import net.rim.vm.WeakReference;

class TextFieldLookup$Invoker implements Runnable {
   private int _timeout;
   private int _id;
   private WeakReference _applicationWR;
   private final TextFieldLookup this$0;
   public static final short LOOKUP_TIMEOUT = 100;

   TextFieldLookup$Invoker(TextFieldLookup _1) {
      this.this$0 = _1;
      this._id = -1;
      this._applicationWR = new WeakReference(null);
   }

   public void start() {
      if (this._id == -1) {
         this._timeout = 100;
         Application application = Application.getApplication();
         this._applicationWR.set(application);
         this._id = application.invokeLaterInternal(this, this._timeout, false);
         if (this._id == -1) {
            this.run();
         }
      }
   }

   public void cancel() {
      if (this._id != -1) {
         Application application = (Application)this._applicationWR.get();
         if (application != null) {
            application.cancelInvokeLater(this._id);
            this._applicationWR.set(null);
         }

         this._id = -1;
      }
   }

   @Override
   public void run() {
      this._id = -1;
      this.this$0.updateViewFromModel();
   }
}
