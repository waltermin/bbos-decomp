package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.ListenerUtilities;

public class CalendarServiceListenerManager {
   private Object[] _listeners;
   private static final long ID;
   private static CalendarServiceListenerManager _instance;

   private CalendarServiceListenerManager() {
   }

   public static CalendarServiceListenerManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _instance = (CalendarServiceListenerManager)reg.getOrWaitFor(-5746713630489719022L);
            if (_instance == null) {
               _instance = new CalendarServiceListenerManager();
               reg.put(-5746713630489719022L, _instance);
            }
         }
      }

      return _instance;
   }

   public void addCalendarServiceListener(Object listener) {
      if (!(listener instanceof CalendarServiceListener)) {
         throw new Object();
      }

      synchronized (this) {
         this._listeners = ListenerUtilities.fastAddListener(this._listeners, listener);
      }
   }

   public void removeCalendarServiceListener(Object listener) {
      if (!(listener instanceof CalendarServiceListener)) {
         throw new Object();
      }

      synchronized (this) {
         this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
      }
   }

   public void fireCalendarServiceCreated(CalendarService calendarService) {
      if (this._listeners != null) {
         synchronized (this._listeners) {
            for (int i = 0; i < this._listeners.length; i++) {
               CalendarServiceListener listener = (CalendarServiceListener)this._listeners[i];
               if (listener != null) {
                  listener.calendarServiceCreated(calendarService);
               }
            }
         }
      }
   }

   public void fireCalendarServiceDestroyed(CalendarService calendarService) {
      if (this._listeners != null) {
         synchronized (this._listeners) {
            for (int i = 0; i < this._listeners.length; i++) {
               CalendarServiceListener listener = (CalendarServiceListener)this._listeners[i];
               if (listener != null) {
                  listener.calendarServiceDestroyed(calendarService);
               }
            }
         }
      }
   }
}
