package net.rim.device.apps.internal.bis.event;

import net.rim.device.apps.internal.bis.api.ui.Event;

public final class CloseEvent extends Event {
   public CloseEvent() {
      super(15);
      this.setOnMenu(false);
   }

   public CloseEvent(int rbID) {
      super(rbID);
   }
}
