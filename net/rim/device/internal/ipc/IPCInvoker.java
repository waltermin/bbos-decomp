package net.rim.device.internal.ipc;

import net.rim.device.api.system.Application;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.vm.WeakReference;

public class IPCInvoker {
   protected WeakReference _applicationReference;
   protected IPCBaseRunnable _runnable;

   protected IPCInvoker(Application app) {
      this(app, null);
   }

   protected IPCInvoker(Application app, IPCBaseRunnable runnable) {
      if (app == null) {
         throw new IllegalStateException();
      }

      ApplicationControl.assertIPCAllowed(true);
      this._applicationReference = new WeakReference(app);
      this._runnable = runnable;
   }

   public boolean invoke() {
      return this._runnable != null ? this.invoke(this._runnable) : false;
   }

   public boolean invoke(IPCBaseRunnable runnable) {
      Application application = (Application)this._applicationReference.get();
      if (this.isAlive()) {
         runnable.setListener(this.getListener());
         application.invokeLater(runnable);
         return true;
      } else {
         return false;
      }
   }

   public IPCResult invokeResultWait() {
      return this._runnable instanceof IPCBlockingReturnRunnable ? this.invokeResultWait((IPCBlockingReturnRunnable)this._runnable) : IPCResult.FAILED_RESULT;
   }

   public IPCResult invokeResultWait(IPCBlockingReturnRunnable runnable) {
      Application application = (Application)this._applicationReference.get();
      if (this.isAlive()) {
         runnable.setListener(this.getListener());
         application.invokeLater(runnable);
         return runnable.getIPCResult();
      } else {
         return IPCResult.FAILED_RESULT;
      }
   }

   public boolean isAlive() {
      Application application = (Application)this._applicationReference.get();
      return application != null && this.getListener() != null && application.isAlive();
   }

   public Application getApplication() {
      return (Application)this._applicationReference.get();
   }

   public Runnable getRunnable() {
      return this._runnable;
   }

   public void setRunnable(IPCRunnable runnable) {
      this._runnable = runnable;
   }

   public Object getListener() {
      throw null;
   }
}
