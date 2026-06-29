package net.rim.device.apps.internal.bis.event;

import net.rim.device.apps.internal.bis.api.ui.Event;

public final class BackEvent extends Event {
   public BackEvent() {
      super(164);
   }

   public BackEvent(int rbID) {
      super(rbID);
   }
}
