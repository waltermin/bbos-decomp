package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

final class NetworkOptionsItem$ActiveNetworkField extends HorizontalFieldManager {
   private LabelField _valueField;
   private final NetworkOptionsItem this$0;

   public NetworkOptionsItem$ActiveNetworkField(NetworkOptionsItem _1) {
      this.this$0 = _1;
      this.add((Field)(new Object(((StringBuffer)(new Object())).append(_1._rb.getString(900)).append(": ").toString())));
      this._valueField = (LabelField)(new Object(null, 18014398509481984L));
      this.add(this._valueField);
   }

   public final void setText(String text) {
      this._valueField.setText(text);
   }

   @Override
   protected final void sublayout(int width, int height) {
      Field leftField = this.getField(0);
      int widthLeft = Math.min(width, leftField.getPreferredWidth());
      Field rightField = this.getField(1);
      int widthRight = Math.min(width, rightField.getPreferredWidth());
      this.layoutChild(leftField, widthLeft, height);
      this.layoutChild(rightField, widthRight, height);
      widthLeft = leftField.getWidth();
      widthRight = rightField.getWidth();
      boolean isSingleLine = widthLeft + widthRight <= width;
      int actualHeight;
      if (isSingleLine) {
         actualHeight = Math.max(leftField.getHeight(), rightField.getHeight());
      } else {
         actualHeight = leftField.getHeight() + rightField.getHeight();
      }

      int actualWidth = width;
      height = Math.min(actualHeight, height);
      this.setExtent(width, height);
      this.setVirtualExtent(actualWidth, actualHeight);
      this.setPositionChild(leftField, 0, 0);
      if (isSingleLine) {
         this.setPositionChild(rightField, width - widthRight, 0);
      } else {
         this.setPositionChild(rightField, width - widthRight, leftField.getHeight());
      }
   }
}
