package net.rim.device.internal.system;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.LED;
import net.rim.device.internal.proxy.Proxy;

class LEDEventProcessor extends Thread implements LEDConstants {
   private LEDEventProcessor$LEDEngineGuard _guard;
   private int _guardInvokeId;
   private LEDEventProcessor$EventStack _events;
   private LEDEngine _ledEngine;

   LEDEventProcessor(LEDEngine ledEngine) {
      this._ledEngine = ledEngine;
      this._events = new LEDEventProcessor$EventStack();
      this._guard = new LEDEventProcessor$LEDEngineGuard(this);
      this._guardInvokeId = -1;
   }

   void addEvent(long sourceIdLong, boolean holsteredBoolean, int groupInfo) {
      synchronized (this._events) {
         this._events.pushEvent(sourceIdLong, holsteredBoolean, groupInfo);
         this._events.notify();
      }
   }

   void removeEvents(long sourceIdLong, int groupInfo) {
      synchronized (this._events) {
         this._events.expireEvents(sourceIdLong, groupInfo);
         this._events.notify();
      }
   }

   boolean contains(long sourceIdLong) {
      synchronized (this._events) {
         return this._events.contains(sourceIdLong);
      }
   }

   void notifyLEDThread() {
      synchronized (this._events) {
         this._events.notify();
      }
   }

   Object getLockObject() {
      return this._events;
   }

   @Override
   public void run() {
      int ledState = 0;
      int lastLedState = 0;
      LEDEventProcessor$EventHolder eventOnTopOfStack = new LEDEventProcessor$EventHolder();
      Proxy proxy = Proxy.getInstance();
      synchronized (this._events) {
         while (true) {
            try {
               try {
                  this._events.wait();
               } catch (InterruptedException var14) {
               }

               int var17 = 0;
               boolean foundValidEvent = false;
               if (this._guardInvokeId != -1) {
                  try {
                     proxy.cancelInvokeLater(this._guardInvokeId);
                     this._guardInvokeId = -1;
                  } catch (IllegalArgumentException var13) {
                  }
               }

               if (!this._events.isEmpty() && !this._ledEngine.isLEDAccessLocked()) {
                  long currentTime = System.currentTimeMillis();

                  while (!this._events.isEmpty()) {
                     this._events.peekEvent(eventOnTopOfStack);
                     long eventExpirationTime;
                     if ((eventExpirationTime = eventOnTopOfStack.getExpirationDate()) > currentTime) {
                        foundValidEvent = true;
                        if (!LED.isPolychromatic()) {
                           this._ledEngine.setConfigurationInternal(0, 150, 2850, 1);
                        }

                        var17 = 2;
                        this._guardInvokeId = proxy.invokeLater(this._guard, eventExpirationTime - currentTime, false);
                        if (this._guardInvokeId == -1) {
                           EventLogger.logEvent(6390170866224596725L, 1348891500, 2);
                           var17 = 0;
                        }
                        break;
                     }

                     this._events.popEvent();
                  }

                  if (LED.isPolychromatic()) {
                     if (foundValidEvent) {
                        this._ledEngine.setFlag(1);
                     } else {
                        this._ledEngine.clearFlag(1);
                     }
                  }
               }

               if (var17 != lastLedState) {
                  if (!LED.isPolychromatic()) {
                     this._ledEngine.setStateInternal(0, var17);
                  }

                  lastLedState = var17;
               }
            } catch (Throwable t) {
               EventLogger.logEvent(6390170866224596725L, 1345414500, 2);
            }
         }
      }
   }
}
