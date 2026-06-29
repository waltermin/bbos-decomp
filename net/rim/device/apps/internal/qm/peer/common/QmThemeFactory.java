package net.rim.device.apps.internal.qm.peer.common;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.internal.ui.Border;

public final class QmThemeFactory {
   private static Vector _listeners = (Vector)(new Object());

   private QmThemeFactory() {
   }

   public static final void setIMThemedBorderColor(Field field) {
      field.setBorder((Border)(new Object(1, 1, 1, 1)));
   }

   public static final AutoTextEditField createThemedAutoTextEditField(String label, String initialValue, int maxNumChars, long style) {
      return (AutoTextEditField)(new Object(label, initialValue, maxNumChars, style));
   }
}
