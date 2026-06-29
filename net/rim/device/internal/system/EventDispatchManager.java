package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.Message;

public final class EventDispatchManager {
   private EventDispatcher[] _dispatchers = new EventDispatcher[59];
   private static final long GUID;

   public static final EventDispatchManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      EventDispatchManager edm = (EventDispatchManager)ar.getOrWaitFor(-7708749290975591471L);
      if (edm == null) {
         edm = new EventDispatchManager();
         ar.put(-7708749290975591471L, edm);
      }

      return edm;
   }

   private EventDispatchManager() {
   }

   public final EventDispatcher getDispatcher(int device) {
      return this._dispatchers[device];
   }

   public final void setDispatcher(int device, EventDispatcher dispatcher) {
      if (this._dispatchers[device] != null) {
         throw new RuntimeException("Dispatcher already registered for " + device);
      }

      this._dispatchers[device] = dispatcher;
   }

   public final void dispatch(int device, Message message, Object[] listeners) {
      EventDispatcher ed = this._dispatchers[device];
      if (ed != null && listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            ed.dispatch(message, listeners[i]);
         }
      }
   }

   public final boolean notify(Message message) {
      int device = message.getDevice();
      return this._dispatchers[device] == null ? true : this._dispatchers[device].notify(message);
   }

   public final int getNotifyProcessId(Message message) {
      int device = message.getDevice();
      return this._dispatchers[device] == null ? -1 : this._dispatchers[device].getNotifyProcessId(message);
   }
}
