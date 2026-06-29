package net.rim.device.api.system;

public class EventInjector$NavigationEvent extends EventInjector$Event {
   private boolean _fourWayFlagged;
   public static final int NAVIGATION_CLICK = 6914;
   public static final int NAVIGATION_UNCLICK = 6915;
   public static final int NAVIGATION_MOVEMENT = 6913;

   public EventInjector$NavigationEvent(int event, int dx, int dy, int status) {
      super(27, event, -(dy << 16) | dx & 65535, status | 536870912, 0, null, null);
      this._fourWayFlagged = (status & 536870912) != 0;
   }

   public void setAmount(int dx, int dy) {
      super._msg.setSubMessage(-(dy << 16) | dx & 65535);
   }

   public int getAmountX() {
      return (short)super._msg.getSubMessage();
   }

   public int getAmountY() {
      return -(super._msg.getSubMessage() >> 16);
   }

   @Override
   public int getStatus() {
      return this._fourWayFlagged ? super._msg.getData0() : super._msg.getData0() ^ 536870912;
   }
}
