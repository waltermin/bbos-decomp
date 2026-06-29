package net.rim.device.apps.internal.options.items;

final class ScreenKeyboardOptionsItem$KeyRepeatRate {
   private String _displayString;
   private int _repeatRate;

   public ScreenKeyboardOptionsItem$KeyRepeatRate(String displayString, int repeatRate) {
      this._displayString = displayString;
      this._repeatRate = repeatRate;
   }

   @Override
   public final String toString() {
      return this._displayString;
   }

   public final int getRepeatRate() {
      return this._repeatRate;
   }
}
