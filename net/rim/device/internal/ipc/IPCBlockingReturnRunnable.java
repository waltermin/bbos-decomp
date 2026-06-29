package net.rim.device.internal.ipc;

public class IPCBlockingReturnRunnable extends IPCBaseRunnable {
   private IPCFutureResult _result = new IPCFutureResult();
   private boolean _success;
   private String _message;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      Object result = null;
      boolean var6 = false /* VF: Semaphore variable */;

      label30: {
         try {
            var6 = true;
            this._result.clear();
            result = this.doRun(this.getListener());
            this._success = true;
            var6 = false;
            break label30;
         } catch (Throwable t) {
            this.doLogging(t);
            this._success = false;
            var6 = false;
         } finally {
            if (var6) {
               this.setResult(result);
            }
         }

         this.setResult(result);
         return;
      }

      this.setResult(result);
   }

   protected Object doRun(Object _1) {
      throw null;
   }

   private void setResult(Object result) {
      this._result.setResult(result);
   }

   Object getResult() {
      return this._result.getResult();
   }

   boolean wasSuccessful() {
      return this._success;
   }

   String getMessage() {
      return this._message;
   }

   protected void setMessage(String message) {
      this._message = message;
   }

   IPCResult getIPCResult() {
      return new IPCResult(this.getResult(), this.wasSuccessful(), this.getMessage());
   }
}
