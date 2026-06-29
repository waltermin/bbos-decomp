package net.rim.device.apps.internal.calendar.viewer;

import java.util.Vector;

final class AgendaField$DoUIRunnable implements Runnable {
   Object _object;
   long _desiredTime;
   Vector _events;
   byte _loadType;
   private final AgendaField this$0;

   AgendaField$DoUIRunnable(AgendaField _1, long desiredTime, Object object, Vector events, byte loadType) {
      this.this$0 = _1;
      this._desiredTime = desiredTime;
      this._object = object;
      this._events = events;
      this._loadType = loadType;
   }

   @Override
   public final void run() {
      this.this$0.loadAgendaUI(this._desiredTime, this._loadType, this._object, this._events);
   }
}
