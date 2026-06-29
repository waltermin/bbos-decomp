package net.rim.device.internal.ipc;

import net.rim.device.api.system.Application;
import net.rim.vm.WeakReference;

class IPCInvokerWeak extends IPCInvoker {
   private WeakReference _listenerReference;

   IPCInvokerWeak(Application app, Object listener) {
      this(app, listener, null);
   }

   IPCInvokerWeak(Application app, Object listener, IPCRunnable runnable) {
      super(app, runnable);
      this._listenerReference = new WeakReference(listener);
   }

   @Override
   public Object getListener() {
      return this._listenerReference == null ? null : this._listenerReference.get();
   }
}
