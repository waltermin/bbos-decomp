package net.rim.device.apps.internal.activation;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;

public final class ActivationEventQueue extends Thread {
   private boolean _run;
   private Vector _eventQueue;
   private boolean _queueIsEmpty;
   private static final long APPLICATION_REG_GUID = 33491840932755780L;

   public ActivationEventQueue() {
      ApplicationRegistry theApplicationRegistry = ApplicationRegistry.getApplicationRegistry();
      theApplicationRegistry.replace(33491840932755780L, this);
      this._run = true;
      this._eventQueue = new Vector(10);
      this._queueIsEmpty = true;
      this.start();
   }

   public final void addEvent(ActivationEventQueueCallback callback, long guid, int data0, int data1, Object object0, Object object1) {
      Object[] event = new Object[]{callback, new Long(guid), new Integer(data0), new Integer(data1), object0, object1};
      synchronized (this._eventQueue) {
         this._eventQueue.addElement(event);
         if (this._queueIsEmpty) {
            this._eventQueue.notify();
            this._queueIsEmpty = false;
         }
      }
   }

   public final void stop() {
      this._run = false;
      if (this._queueIsEmpty) {
         synchronized (this._eventQueue) {
            this._eventQueue.notify();
         }
      }
   }

   @Override
   public final void run() {
      while (this._run || !this._queueIsEmpty) {
         if (!this._eventQueue.isEmpty()) {
            Object[] event;
            synchronized (this._eventQueue) {
               event = (Object[])this._eventQueue.elementAt(0);
               this._eventQueue.removeElementAt(0);
            }

            ActivationEventQueueCallback callback = (ActivationEventQueueCallback)event[0];
            if (callback != null) {
               callback.onEventFromActivationEventQueue((Long)event[1], (Integer)event[2], (Integer)event[3], event[4], event[5]);
               ActivationEventQueueCallback var14 = null;
            }

            event = null;
         } else {
            synchronized (this._eventQueue) {
               this._queueIsEmpty = true;
               if (this._run) {
                  try {
                     this._eventQueue.wait();
                  } finally {
                     continue;
                  }
               }
            }
         }
      }

      ApplicationRegistry.getApplicationRegistry().remove(33491840932755780L);
   }
}
