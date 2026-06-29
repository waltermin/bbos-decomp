package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeAttributeSet;

final class MapletFooterProgressField$EmptyField extends Field {
   private final MapletFooterProgressField this$0;

   public MapletFooterProgressField$EmptyField(MapletFooterProgressField this$0) {
      this.this$0 = this$0;
      this.setTag(MapletFooterProgressField.TAG);
   }

   public final int getColor(int attrib) {
      return ThemeAttributeSet.getColor(this, attrib);
   }

   @Override
   protected final void layout(int width, int height) {
   }

   @Override
   protected final void paint(Graphics graphics) {
   }
}
