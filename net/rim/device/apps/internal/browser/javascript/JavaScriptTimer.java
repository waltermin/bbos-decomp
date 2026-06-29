package net.rim.device.apps.internal.browser.javascript;

import java.util.Vector;

final class JavaScriptTimer {
   private boolean _shutdown;
   private JavaScriptEngine _scriptEngine;
   private int _timerId;
   private Vector _waitQueue;
   private JavaScriptTimer$ExecuteThread _currentThread;

   public JavaScriptTimer(JavaScriptEngine engine) {
      this._scriptEngine = engine;
      this._waitQueue = (Vector)(new Object());
   }

   public final void shutdown() {
      synchronized (this._waitQueue) {
         this._shutdown = true;
         this._waitQueue.notify();
      }
   }

   public final int enque(String method, int timeout, boolean repeat) {
      synchronized (this._waitQueue) {
         JavaScriptTimer$QueueElement element = new JavaScriptTimer$QueueElement();
         element._timerId = ++this._timerId;
         element._action = method;
         element._repeat = repeat;
         element._repeatTime = timeout;
         element._timeToExecuteAt = System.currentTimeMillis() + timeout;
         this._waitQueue.addElement(element);
         this._waitQueue.notify();
         if (this._currentThread == null) {
            this._shutdown = false;
            this._currentThread = new JavaScriptTimer$ExecuteThread(this);
            this._currentThread.start();
         }

         return element._timerId;
      }
   }

   public final void deque(int timerId) {
      synchronized (this._waitQueue) {
         int count = this._waitQueue.size();

         for (int i = 0; i < count; i++) {
            JavaScriptTimer$QueueElement element = (JavaScriptTimer$QueueElement)this._waitQueue.elementAt(i);
            if (element._timerId == timerId) {
               this._waitQueue.removeElementAt(i);
               return;
            }
         }
      }
   }
}
