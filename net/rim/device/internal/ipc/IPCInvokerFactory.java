package net.rim.device.internal.ipc;

import net.rim.device.api.system.Application;
import net.rim.device.internal.proxy.Proxy;

public class IPCInvokerFactory {
   private IPCInvokerFactory() {
   }

   public static IPCInvoker getIPCInvoker(Object listener) {
      return getIPCInvoker(null, listener, null);
   }

   public static IPCInvoker getIPCInvoker(Application app, Object listener) {
      return getIPCInvoker(app, listener, null);
   }

   public static IPCInvoker getIPCInvoker(Object listener, IPCRunnable runnable) {
      return getIPCInvoker(null, listener, runnable);
   }

   public static IPCInvoker getIPCInvoker(Application app, Object listener, IPCRunnable runnable) {
      Application application = app;
      if (application == null) {
         try {
            application = Application.getApplication();
         } catch (IllegalStateException e) {
            application = Proxy.getInstance();
         }
      }

      return new IPCInvokerWeak(application, listener, runnable);
   }

   public static IPCInvoker getStrongIPCInvoker(Object listener) {
      return getStrongIPCInvoker(null, listener, null);
   }

   public static IPCInvoker getStrongIPCInvoker(Application app, Object listener) {
      return getStrongIPCInvoker(app, listener, null);
   }

   public static IPCInvoker getStrongIPCInvoker(Object listener, IPCRunnable runnable) {
      return getStrongIPCInvoker(null, listener, runnable);
   }

   public static IPCInvoker getStrongIPCInvoker(Application app, Object listener, IPCRunnable runnable) {
      Application application = app;
      if (application == null) {
         try {
            application = Application.getApplication();
         } catch (IllegalStateException e) {
            application = Proxy.getInstance();
         }
      }

      return new IPCInvokerStrong(application, listener, runnable);
   }
}
