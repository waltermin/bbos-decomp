package net.rim.tid.itie;

import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.vm.Array;
import net.rim.vm.Memory;
import net.rim.vm.WeakReference;

class SecureBufferRegistry implements MemoryCleanerListener {
   private WeakReference[] _buffer = new WeakReference[20];
   private int _firstNullCell = 0;
   private int _lastEmptyIndex = -1;
   private static final int DEFAULT_INC = 20;
   private static final int MAX_RETRACE_COUNT = 80;
   private static final int MAX_BUFFER_SIZE = 400;
   private static final int GC_TRIGGER = 250;

   SecureBufferRegistry() {
      MemoryCleanerManager.getInstance().addListener(this, true, false);
   }

   synchronized void registerBuffer(ISecureInputMethodBuffer buffer) {
      if (this._firstNullCell < this._buffer.length) {
         this._buffer[this._firstNullCell++] = new WeakReference(buffer);
      } else {
         if (this._lastEmptyIndex > 250) {
            Memory.fullGC();
            this._lastEmptyIndex = -1;
         }

         for (int i = this._lastEmptyIndex + 1; i < this._firstNullCell; i++) {
            Object obj = this._buffer[i].get();
            if (obj == null) {
               this._buffer[i].set(buffer);
               this._lastEmptyIndex = i;
               return;
            }
         }

         if (this._buffer.length + 20 <= 400) {
            Array.resize(this._buffer, this._buffer.length + 20);
            this._lastEmptyIndex = this._firstNullCell;
            this._buffer[this._firstNullCell++] = new WeakReference(buffer);
         }
      }
   }

   @Override
   public boolean cleanNow(int event) {
      return event == 10 ? this.runSecureClean() : false;
   }

   @Override
   public String getDescription() {
      return null;
   }

   private synchronized boolean runSecureClean() {
      boolean result = false;

      for (int i = 0; i < this._firstNullCell; i++) {
         ISecureInputMethodBuffer buffer = (ISecureInputMethodBuffer)this._buffer[i].get();
         if (buffer != null) {
            result |= buffer.runSecureClean();
         }
      }

      return result;
   }
}
