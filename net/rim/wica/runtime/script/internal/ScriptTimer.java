package net.rim.wica.runtime.script.internal;

import java.util.Vector;

public final class ScriptTimer {
   private ScriptEngineImpl _engine;
   private ScriptTimer$TimerThread _timerThread;
   private Vector _queue;
   private int _timerId;
   private boolean _shutdown;

   ScriptTimer(ScriptEngineImpl engine) {
      this._engine = engine;
      this._queue = (Vector)(new Object(2));
   }

   public final int setTimeout(Object action, long timeout, long[] args, boolean repeat) {
      synchronized (this._queue) {
         ScriptTimer$TimerElement element = new ScriptTimer$TimerElement();
         element._timerId = ++this._timerId;
         element._action = action;
         element._args = args;
         element._repeat = repeat;
         element._repeatTime = timeout;
         element._timeToExecuteAt = System.currentTimeMillis() + timeout;
         element._engine = this._engine;
         this._queue.addElement(element);
         this._queue.notify();
         if (this._timerThread == null) {
            this._shutdown = false;
            this._timerThread = new ScriptTimer$TimerThread(this);
            this._timerThread.start();
         }

         return element._timerId;
      }
   }

   public final void clearTimeout(int timerId) {
      synchronized (this._queue) {
         int count = this._queue.size();

         for (int i = 0; i < count; i++) {
            ScriptTimer$TimerElement element = (ScriptTimer$TimerElement)this._queue.elementAt(i);
            if (element._timerId == timerId) {
               this._queue.removeElementAt(i);
               return;
            }
         }
      }
   }

   final void dispose() {
      synchronized (this._queue) {
         this._shutdown = true;
         this._queue.notify();
      }

      this._timerThread = null;
   }
}
