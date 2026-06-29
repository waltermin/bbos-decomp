package net.rim.tid.awt.event;

import net.rim.tid.awt.Event;
import net.rim.tid.itie.IComponent;

public final class FocusEvent extends ComponentEvent {
   private int _appID;
   public static final int FOCUS_GAINED = 1004;
   public static final int FOCUS_LOST = 1005;

   public FocusEvent(IComponent source, int eventID, int eMask, int appID) {
      super(source, eventID, eMask | Event.FOCUS_EVENT_MASK);
      this._appID = appID;
   }

   public final void init(IComponent aSource, int aId, int appId) {
      super.init(aSource, aId);
      this._appID = appId;
   }

   public final int getApplicationId() {
      return this._appID;
   }
}
