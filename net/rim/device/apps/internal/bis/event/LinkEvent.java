package net.rim.device.apps.internal.bis.event;

import net.rim.device.apps.internal.bis.api.ui.Event;

public final class LinkEvent extends Event {
   private int _link;

   public LinkEvent(int rbID) {
      super(rbID);
   }

   public LinkEvent(int rbID, int link) {
      super(rbID);
      this._link = link;
   }

   public final int getLink() {
      return this._link;
   }

   public final void setLink(int link) {
      this._link = link;
   }
}
