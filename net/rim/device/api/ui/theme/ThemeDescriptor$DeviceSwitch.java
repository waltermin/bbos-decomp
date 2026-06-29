package net.rim.device.api.ui.theme;

import net.rim.device.api.ui.Trackball;

class ThemeDescriptor$DeviceSwitch {
   private ThemeDescriptor _trackWheelTheme;
   private ThemeDescriptor _rollerBallTheme;

   public ThemeDescriptor$DeviceSwitch(ThemeDescriptor trackWheelTheme, ThemeDescriptor rollerBallTheme) {
      this._trackWheelTheme = trackWheelTheme;
      this._rollerBallTheme = rollerBallTheme;
   }

   public ThemeDescriptor getThemeDescriptor() {
      return Trackball.isSupported() ? this._rollerBallTheme : this._trackWheelTheme;
   }
}
