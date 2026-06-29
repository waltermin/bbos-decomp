package net.rim.device.internal.ipc;

class IPCFutureResult {
   protected Object _result;
   protected IPCFutureResult$SemaphoreBoolean _set = new IPCFutureResult$SemaphoreBoolean(false);

   synchronized void clear() {
      synchronized (this._set) {
         if (this._set.getValue()) {
            this._set.notifyAll();
         }

         this._set.setValue(false);
         this._result = null;
      }
   }

   synchronized void setResult(Object result) {
      synchronized (this._set) {
         if (!this._set.getValue()) {
            this._result = result;
            this._set.setValue(true);
            this._set.notifyAll();
         }
      }
   }

   Object getResult() {
      synchronized (this._set) {
         if (!this._set.getValue()) {
            try {
               this._set.wait();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }

         return this._result;
      }
   }
}
