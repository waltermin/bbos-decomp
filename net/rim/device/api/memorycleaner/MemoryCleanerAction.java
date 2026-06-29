package net.rim.device.api.memorycleaner;

import net.rim.vm.WeakReference;

public final class MemoryCleanerAction {
   private Object _listener;

   public MemoryCleanerAction(Object listener) {
      this._listener = listener;
   }

   public final MemoryCleanerListener getListener() {
      return !(this._listener instanceof WeakReference) ? (MemoryCleanerListener)this._listener : (MemoryCleanerListener)((WeakReference)this._listener).get();
   }

   public final boolean doAction(int event) {
      MemoryCleanerListener listener = this.getListener();
      if (listener != null) {
         try {
            return listener.cleanNow(event);
         } catch (Throwable t) {
            return true;
         }
      } else {
         return false;
      }
   }

   public final boolean doAction() {
      return this.doAction(4);
   }

   public final String getDescription() {
      MemoryCleanerListener listener = this.getListener();
      if (listener != null) {
         try {
            return listener.getDescription();
         } catch (Throwable var3) {
         }
      }

      return "";
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof MemoryCleanerAction)) {
         return false;
      }

      MemoryCleanerAction memoryCleanerAction = (MemoryCleanerAction)other;
      return this.getListener().equals(memoryCleanerAction.getListener());
   }
}
