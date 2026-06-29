package net.rim.device.apps.api.ui;

final class TimeChoiceField$TimeChoice {
   private String _displayString;
   private long _time;

   public TimeChoiceField$TimeChoice(String displayString, long time) {
      this._displayString = displayString;
      this._time = time;
   }

   @Override
   public final String toString() {
      return this._displayString;
   }

   public final long getTime() {
      return this._time;
   }
}
