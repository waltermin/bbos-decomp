package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.internal.ui.Background;

final class QmThemedEmailAddressEditField extends EmailAddressEditField {
   QmThemedEmailAddressEditField(String label, String initialValue) {
      super(label, initialValue);
      QmThemeFactory.setIMThemedBorderColor(this);
   }

   @Override
   protected final void paintBackground(Graphics graphics) {
      XYRect rect = graphics.getClippingRect();
      Background.createSolidBackground(16777215).draw(graphics, rect);
   }
}
