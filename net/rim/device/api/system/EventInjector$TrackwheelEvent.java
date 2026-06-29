package net.rim.device.api.system;

public class EventInjector$TrackwheelEvent extends EventInjector$Event {
   private boolean _isThumbRollUp;
   public static final int THUMB_CLICK;
   public static final int THUMB_UNCLICK;
   public static final int THUMB_ROLL_UP;
   public static final int THUMB_ROLL_DOWN;

   public EventInjector$TrackwheelEvent(int event, int amount, int status) {
      super(2, event == 518 ? 519 : event, event == 518 ? -amount : amount, status, 0, null, null);
      if (event == 518) {
         this._isThumbRollUp = true;
      }
   }

   @Override
   public int getEvent() {
      return this._isThumbRollUp ? 518 : super.getEvent();
   }

   @Override
   public void setEvent(int event) {
      if (event != this.getEvent()) {
         if (this._isThumbRollUp) {
            this._isThumbRollUp = false;
            this.setAmount(-this.getAmount());
         } else if (event == 518) {
            event = 519;
            this._isThumbRollUp = true;
            this.setAmount(-this.getAmount());
         }
      }

      super.setEvent(event);
   }

   public void setAmount(int amount) {
      if (this._isThumbRollUp) {
         amount = -amount;
      }

      super._msg.setSubMessage(amount);
   }

   public int getAmount() {
      int amount = super._msg.getSubMessage();
      return this._isThumbRollUp ? -amount : amount;
   }
}
