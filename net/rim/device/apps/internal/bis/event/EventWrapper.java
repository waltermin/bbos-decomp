package net.rim.device.apps.internal.bis.event;

import net.rim.device.apps.internal.bis.api.ui.Event;

public final class EventWrapper extends Event {
   private Event _delegateEvent;

   public EventWrapper() {
   }

   public EventWrapper(int rbID, Event delegateEvent) {
      super(rbID);
      this._delegateEvent = delegateEvent;
   }

   public final Event getEvent() {
      return this._delegateEvent;
   }

   public final void setEvent(Event delegateEvent) {
      this._delegateEvent = delegateEvent;
   }
}
