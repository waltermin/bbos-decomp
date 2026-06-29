package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.internal.ui.Background;

final class QmThemedActiveAutoTextEditField extends ActiveAutoTextEditField {
   QmThemedActiveAutoTextEditField(String label, String initialValue) {
      super(label, initialValue);
      QmThemeFactory.setIMThemedBorderColor(this);
   }

   QmThemedActiveAutoTextEditField(String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, style);
      QmThemeFactory.setIMThemedBorderColor(this);
   }

   QmThemedActiveAutoTextEditField(String label, String initialValue, int maxNumChars, long style, StringPatternContainer patterns) {
      super(label, initialValue, maxNumChars, style, patterns);
      QmThemeFactory.setIMThemedBorderColor(this);
   }

   @Override
   protected final void paintBackground(Graphics graphics) {
      XYRect rect = graphics.getClippingRect();
      Background.createSolidBackground(16777215).draw(graphics, rect);
   }
}
