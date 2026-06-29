package net.rim.tid.awt.event;

import net.rim.tid.awt.Event;
import net.rim.tid.itie.IComponent;

public class NavigationEvent extends ComponentEvent {
   private int _dxMagnitude;
   private int _dyMagnitude;
   private int _status;
   private int _eventConsumptionId;
   public static final int COMPOSED_TEXT_NOT_CHANGED = 0;
   public static final int COMPOSED_TEXT_CHANGED = 1;
   public static final int CARET_POSITION_CHANGED = 2;
   public static final int LOOKUP_POSITION_CHANGED = 3;
   public static final int TRACK_BALL_ID = 1;
   public static final int THUMB_WHEEL_ID = 2;

   public NavigationEvent(IComponent source, int id) {
      super(source, id, Event.NAVIGATION_EVENT_MASK);
   }

   public void init(IComponent aSource, int aId, int dxMagnitude, int dyMagnitude, int status) {
      super.init(aSource, aId);
      this._dxMagnitude = dxMagnitude;
      this._dyMagnitude = dyMagnitude;
      this._status = status;
   }

   public int getDxMagnitude() {
      return this._dxMagnitude;
   }

   public int getDyMagnitude() {
      return this._dyMagnitude;
   }

   public boolean isLetterKeyHeld() {
      return (this._status & 8) != 0 && (this._status & 3) == 0;
   }

   public boolean isAltHeld() {
      return (this._status & 1) != 0;
   }

   public boolean isShiftHeld() {
      return (this._status & 2) != 0;
   }

   public boolean isKeyHeld() {
      return (this._status & 8) != 0;
   }

   public int getEventConsumptionId() {
      return this._eventConsumptionId;
   }

   public void setEventConsumptionId(int consumptionId) {
      this._eventConsumptionId = consumptionId;
   }
}
