package net.rim.device.internal.ipc;

import net.rim.device.api.system.Application;

class IPCInvokerStrong extends IPCInvoker {
   private Object _listener;

   IPCInvokerStrong(Application app, Object listener) {
      this(app, listener, null);
   }

   IPCInvokerStrong(Application app, Object listener, IPCRunnable runnable) {
      super(app, runnable);
      this._listener = listener;
   }

   @Override
   public Object getListener() {
      return this._listener;
   }
}
