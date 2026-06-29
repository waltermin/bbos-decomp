package net.rim.wica.runtime.script.internal;

class ScriptEngineImpl$ScriptWatchdog extends Thread {
   private boolean _watch;
   private boolean _ok;
   private boolean _kill;
   private final ScriptEngineImpl this$0;

   ScriptEngineImpl$ScriptWatchdog(ScriptEngineImpl this$0) {
      this.this$0 = this$0;
      this.start();
   }

   @Override
   public synchronized void run() {
      while (true) {
         label36:
         try {
            this.wait(this.this$0._monitor.getTimeoutValue());
         } finally {
            break label36;
         }

         if (this._kill) {
            return;
         }

         if (this._ok) {
            this._ok = false;
         } else if (this._watch) {
            this.this$0._monitor.scriptTimedOut();
         }
      }
   }

   private synchronized void kick(boolean watch) {
      this._watch = watch;
      this._ok = true;
      this.notifyAll();
   }

   private synchronized void kill() {
      this._kill = true;
      this.notifyAll();
   }
}
