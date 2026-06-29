package net.rim.device.internal.ui;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiEngine;

public class UiInternalListener {
   public static final int DEPRECATED_VERSION = 1073741824;

   protected UiInternalListener() {
   }

   public void onFocus(UiEngine uie, Screen previous, Screen screen) {
   }

   public void onPopScreen(UiEngine uie, Screen screen) {
   }

   public void onPushGlobalScreen(UiEngine uie, Screen screen, int priority, int flags) {
   }

   public void onPushScreen(UiEngine uie, Screen screen) {
   }

   public void onUpdateDisplay(UiEngine uie) {
   }

   public void onUserKey(UiEngine uie, Screen screen, int event, int key, int keycode, int time) {
   }

   public void onUserStylus(UiEngine uie, Screen screen, int event, int x, int y, int status, int time) {
   }

   public void onUserTrackball(UiEngine uie, Screen screen, int event, int dx, int dy, int status, int time) {
   }

   public void onUserTrackwheel(UiEngine uie, Screen screen, int event, int amount, int status, int time) {
   }
}
