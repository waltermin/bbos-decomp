package net.rim.device.api.ui.component;

import net.rim.vm.Array;

class BackgroundScanThread extends Thread {
   private Runnable[] _list = new Runnable[8];
   private int _headIndex;
   private int _tailIndex;
   private static final long MAX_IDLE_TIME = 60000L;
   private static BackgroundScanThread _scanThread;

   private BackgroundScanThread() {
   }

   public static void post(Runnable object) {
      BackgroundScanThread scanThread = _scanThread;
      if (scanThread == null) {
         scanThread = new BackgroundScanThread();
         scanThread.start();
         _scanThread = scanThread;
      }

      scanThread.put(object);
   }

   private synchronized void expandBuffer(int increase) {
      int oldLength = this._list.length;
      Array.resize(this._list, oldLength + increase);
      if (this._headIndex < this._tailIndex) {
         System.arraycopy(this._list, this._tailIndex, this._list, this._tailIndex + increase, oldLength - this._tailIndex);
         this._tailIndex += increase;
      }
   }

   private synchronized void put(Runnable object) {
      if (this._tailIndex - this._headIndex == 1 || this._tailIndex == 0 && this._headIndex == this._list.length - 1) {
         this.expandBuffer(8);
      }

      this._list[this._headIndex] = object;
      this._headIndex++;
      if (this._headIndex >= this._list.length) {
         this._headIndex = 0;
      }

      this.notify();
   }

   private synchronized Runnable get() {
      Runnable object = null;
      long startTime = System.currentTimeMillis();

      while (this._headIndex == this._tailIndex) {
         try {
            long delta = startTime + 60000 - System.currentTimeMillis();
            if (delta <= 0) {
               return null;
            }

            this.wait(delta);
         } catch (InterruptedException var6) {
         } catch (Exception var7) {
         }
      }

      object = this._list[this._tailIndex];
      this._list[this._tailIndex] = null;
      this._tailIndex++;
      if (this._tailIndex >= this._list.length) {
         this._tailIndex = 0;
      }

      return object;
   }

   @Override
   public void run() {
      while (true) {
         Runnable r = this.get();
         if (r == null) {
            _scanThread = null;
            return;
         }

         r.run();
         Object var2 = null;
      }
   }
}
