package net.rim.device.api.ui;

import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.DefaultKeyLayout;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.tid.im.layout.UILocaleKeyLayout;

public class KeypadUtil {
   public static final int MODE_UI_CURRENT_LOCALE = 0;
   public static final int MODE_EN_LOCALE = 1;

   private KeypadUtil() {
   }

   public static char getKeyChar(int keycode, int mode) {
      switch (mode) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            return UiInternal.map(keycode);
         case 1:
            return UiInternal.mapFromFallbackLayout(keycode);
      }
   }

   public static int getKeyCode(char ch, int status, int mode) {
      SLKeyLayout layout = null;
      switch (mode) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            layout = UILocaleKeyLayout.getUIKeyLayout();
            break;
         case 1:
            layout = DefaultKeyLayout.getDefaultKeyLayout();
      }

      return layout == null ? 0 : layout.getOriginalKeyCode(ch, SLKeyLayout.convertStatusToModifiers(status)) << 16 | status;
   }
}
