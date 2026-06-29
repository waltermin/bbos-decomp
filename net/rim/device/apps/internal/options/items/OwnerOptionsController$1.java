package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.AutoTextEditField;

final class OwnerOptionsController$1 extends AutoTextEditField {
   private final OwnerOptionsController this$0;

   OwnerOptionsController$1(OwnerOptionsController _1, String x0, String x1, int x2, long x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   protected final void layout(int width, int height) {
      super.layout(width, height);
      int minHeight = this.getFont().getHeight() * 2;
      if (this.getHeight() < minHeight) {
         this.setExtent(this.getWidth(), minHeight);
      }
   }
}
