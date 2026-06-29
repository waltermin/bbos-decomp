package net.rim.device.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.ui.UiOptionsRegistry;
import net.rim.vm.TraceBack;

public final class Trackball {
   public static final int SENSITIVITY_UNSET;
   public static final int SENSITIVITY_OFF;
   public static final int FILTER_DEFAULT;
   public static final int FILTER_XY_SNAP;
   public static final int FILTER_NO_TIME_WINDOW;
   public static final int FILTER_ACCELERATION;
   private static boolean _supported = isSupported0();
   private static int _sensitivityX = Integer.MAX_VALUE;
   private static int _sensitivityY = Integer.MAX_VALUE;
   private static int _filterMask = -1;

   private Trackball() {
   }

   public static final int getFilter() {
      return _filterMask;
   }

   public static final int getFilterForSystem() {
      return UiOptionsRegistry.getInstance().getInt(-1211370300138911215L);
   }

   public static final int getSensitivityIncrement() {
      return 10;
   }

   public static final int getSensitivityX() {
      return _sensitivityX;
   }

   public static final int getSensitivityXForSystem() {
      return UiOptionsRegistry.getInstance().getInt(4925806619770988503L);
   }

   public static final native int getSensitivityXFromOS();

   public static final int getSensitivityY() {
      return _sensitivityY;
   }

   public static final int getSensitivityYForSystem() {
      return UiOptionsRegistry.getInstance().getInt(1105523701474371332L);
   }

   public static final native int getSensitivityYFromOS();

   public static final void getStats(Trackball$Stats stats) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(2));
      getStats0(stats);
   }

   private static final native void getStats0(Trackball$Stats var0);

   public static final boolean isFeedbackAudibleForSystem() {
      return UiOptionsRegistry.getInstance().getBoolean(9173869926753706073L);
   }

   public static final boolean isSupported() {
      return _supported;
   }

   private static final native boolean isSupported0();

   public static final void resetStats() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(2));
      resetStats0();
   }

   private static final native void resetStats0();

   public static final void setFeedbackAudibleForSystem(boolean enabled) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(2));
      UiOptionsRegistry.getInstance().setBoolean(9173869926753706073L, enabled);
      AudioRouter.getInstance().enableInputFeedback(0, enabled);
   }

   public static final void setFilter(int filter) {
      setFilterInternal(filter);
      updateDeviceWithAppSettings();
   }

   static final void setFilterInternal(int filter) {
      if (filter != -1 && (filter & -8) != 0) {
         throw new IllegalArgumentException();
      }

      _filterMask = filter;
   }

   private static final native void setFilterMask0(int var0);

   public static final void setFilterForSystem(int filter) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(2));
      if (filter != -1 && (filter & -8) != 0) {
         throw new IllegalArgumentException();
      }

      UiOptionsRegistry.getInstance().setInt(-1211370300138911215L, filter);
      updateDeviceWithAppSettings();
   }

   public static final void setSensitivityX(int sensitivity) {
      setSensitivityXInternal(sensitivity);
      updateDeviceWithAppSettings();
   }

   static final void setSensitivityXInternal(int sensitivity) {
      if ((sensitivity < 0 || 100 < sensitivity) && sensitivity != Integer.MAX_VALUE) {
         throw new IllegalArgumentException();
      }

      _sensitivityX = sensitivity;
   }

   private static final native void setSensitivityX0(int var0);

   public static final void setSensitivityXForSystem(int sensitivity) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(2));
      if ((sensitivity < 0 || 100 < sensitivity) && sensitivity != Integer.MAX_VALUE) {
         throw new IllegalArgumentException();
      }

      UiOptionsRegistry.getInstance().setInt(4925806619770988503L, sensitivity);
      updateDeviceWithAppSettings();
   }

   public static final void setSensitivityY(int sensitivity) {
      setSensitivityYInternal(sensitivity);
      updateDeviceWithAppSettings();
   }

   static final void setSensitivityYInternal(int sensitivity) {
      if ((sensitivity < 0 || 100 < sensitivity) && sensitivity != Integer.MAX_VALUE) {
         throw new IllegalArgumentException();
      }

      _sensitivityY = sensitivity;
   }

   private static final native void setSensitivityY0(int var0);

   public static final void setSensitivityYForSystem(int sensitivity) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(2));
      if ((sensitivity < 0 || 100 < sensitivity) && sensitivity != Integer.MAX_VALUE) {
         throw new IllegalArgumentException();
      }

      UiOptionsRegistry.getInstance().setInt(1105523701474371332L, sensitivity);
      updateDeviceWithAppSettings();
   }

   static final void updateDeviceWithAppSettings() {
      if (isSupported() && Application.getApplication().isForeground()) {
         UiOptionsRegistry options = UiOptionsRegistry.getInstance();
         int sensitvityX = _sensitivityX != Integer.MAX_VALUE ? _sensitivityX : options.getInt(4925806619770988503L);
         int sensitvityY = _sensitivityY != Integer.MAX_VALUE ? _sensitivityY : options.getInt(1105523701474371332L);
         setSensitivityX0(sensitvityX);
         setSensitivityY0(sensitvityY);
         AudioRouter.getInstance().enableInputFeedback(0, options.getBoolean(9173869926753706073L));
         int filterMask = _filterMask != -1 ? _filterMask : options.getInt(-1211370300138911215L);
         if (filterMask == -1) {
            filterMask = 1;
         }

         setFilterMask0(filterMask);
      }
   }
}
