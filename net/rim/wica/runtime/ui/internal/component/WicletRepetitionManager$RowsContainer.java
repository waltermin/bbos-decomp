package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.wica.runtime.ui.internal.Focusable;

final class WicletRepetitionManager$RowsContainer extends VerticalFieldManager implements Focusable {
   private final WicletRepetitionManager this$0;

   public WicletRepetitionManager$RowsContainer(WicletRepetitionManager this$0) {
      super(4037758540914360320L | this$0._style);
      this.this$0 = this$0;
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public final void doUpdateLayout() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, maxHeight);
      this.setExtent(this.getWidth(), this.getVirtualHeight());
   }

   @Override
   protected final boolean incrementalLayout(int index, int added, int deleted) {
      boolean result = super.incrementalLayout(index, added, deleted);
      int height = this.getHeight();
      int virtualHeight = this.getVirtualHeight();
      return height != virtualHeight && (!this.this$0._isCollapsible || height <= virtualHeight || this.getFieldWithFocusIndex() == index) ? false : result;
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      super.applyFont();
   }
}
