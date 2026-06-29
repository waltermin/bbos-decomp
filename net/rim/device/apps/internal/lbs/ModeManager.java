package net.rim.device.apps.internal.lbs;

public final class ModeManager {
   private AbstractMode[] _modes;
   private int _currentModeIx = 0;
   private AbstractMode _currentMode = null;
   private MapField _mapField = null;
   public static final int MODE_MAP = 0;
   public static final int MODE_GPS = 1;
   public static final int MODE_LOC_REVIEW = 2;
   public static final int MODE_ROUTE_REVIEW = 3;

   public ModeManager(MapField mapField) {
      this._mapField = mapField;
      this._modes = new AbstractMode[4];
      this._modes[1] = new GPSMode();
   }

   public final AbstractMode setCurrentMode(int mode) {
      switch (mode) {
         case 0:
         case 1:
         case 2:
         case 3:
         default:
            if (this._modes[mode] != null && this._modes[mode].isEnabled()) {
               this._currentModeIx = mode;
               this._currentMode = this._modes[this._currentModeIx];
               return this._currentMode;
            }
         case -1:
            return null;
      }
   }

   public final AbstractMode getMode(int mode) {
      switch (mode) {
         case -1:
            return null;
         case 0:
         case 1:
         case 2:
         case 3:
         default:
            return this._modes[mode];
      }
   }

   public final void enableMode(int mode, boolean enable) {
      switch (mode) {
         case 0:
         case 1:
         case 2:
         case 3:
         default:
            if (this._modes[mode] != null) {
               this._modes[mode].setEnabled(enable);
            }
         case -1:
      }
   }
}
