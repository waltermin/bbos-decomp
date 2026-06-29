package net.rim.device.api.ui.component;

import net.rim.vm.Array;

class ThreadPool {
   private Runnable[] _list;
   private int _onePastLast;
   private int _first;
   private long _lastTimeUsed = System.currentTimeMillis();
   private int _threadCount;
   private static final long MAX_IDLE_TIME;
   private static final int LIST_INCR;
   private static ThreadPool _pool;

   private ThreadPool() {
      this._list = new Runnable[8];
      this._first = 0;
      this._onePastLast = 0;

      for (int idx = 0; idx < 2; idx++) {
         new ThreadPool$ThreadPoolRunner(this).start();
         this._threadCount++;
      }
   }

   public static void post(Runnable object) {
      ThreadPool pool = _pool;
      if (pool == null || pool._threadCount <= 0) {
         pool = new ThreadPool();
         _pool = pool;
      }

      pool.put(object);
   }

   private synchronized void put(Runnable object) {
      if (this._first == 0 && this._onePastLast == this._list.length - 1 || this._first == this._onePastLast + 1) {
         int oldLength = this._list.length;
         Array.resize(this._list, oldLength + 8);
         if (this._first > this._onePastLast) {
            System.arraycopy(this._list, this._first, this._list, this._first + 8, oldLength - this._first);
            int oldFirst = this._first;
            this._first += 8;

            for (int i = oldFirst; i < this._first; i++) {
               this._list[i] = null;
            }
         }
      }

      this._list[this._onePastLast] = object;
      this._onePastLast++;
      if (this._onePastLast >= this._list.length) {
         this._onePastLast = 0;
      }

      this._lastTimeUsed = System.currentTimeMillis();
      this.notify();
   }

   private synchronized Runnable get() {
      while (this._onePastLast == this._first) {
         try {
            long delta = this._lastTimeUsed + 60000 - System.currentTimeMillis();
            if (delta <= 0) {
               return null;
            }

            this.wait(delta);
         } catch (InterruptedException var3) {
         } catch (Exception var4) {
         }
      }

      Runnable object = this._list[this._first];
      this._list[this._first] = null;
      this._first++;
      if (this._first >= this._list.length) {
         this._first = 0;
      }

      this._lastTimeUsed = System.currentTimeMillis();
      return object;
   }

   static int access$110(ThreadPool x0) {
      return x0._threadCount--;
   }

   static ThreadPool access$202(ThreadPool x0) {
      _pool = x0;
      return x0;
   }
}
