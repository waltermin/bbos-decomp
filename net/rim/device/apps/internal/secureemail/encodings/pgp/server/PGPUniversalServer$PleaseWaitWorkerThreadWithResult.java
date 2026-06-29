package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class PGPUniversalServer$PleaseWaitWorkerThreadWithResult extends PleaseWaitWorkerThread {
   private boolean _result;

   private PGPUniversalServer$PleaseWaitWorkerThreadWithResult() {
   }

   protected void setResultTrue() {
      this._result = true;
   }

   public boolean getResult() {
      return this._result;
   }
}
