package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

final class CalendarViewController$SimpleNavField extends Field {
   private int _navBarWidth;
   private int _totalNavBarWidth;
   private final CalendarViewController this$0;
   private static final int ARROW_WIDTH;

   CalendarViewController$SimpleNavField(CalendarViewController _1) {
      this.this$0 = _1;
      this._navBarWidth = 3;
      this.computeLayout(this.getFont());
   }

   private final void computeLayout(Font font) {
      if (this.this$0._disableSimpleNav) {
         this._totalNavBarWidth = 0;
      } else {
         this._totalNavBarWidth = this._navBarWidth + 6 + 1;
      }
   }

   @Override
   public final void setFont(Font font) {
      super.setFont(font);
      this.computeLayout(this.getFont());
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(width, height);
   }

   @Override
   public final int getPreferredWidth() {
      return this._totalNavBarWidth;
   }

   @Override
   protected final void paint(Graphics g) {
      if (!this.this$0._disableSimpleNav) {
         int x = 0;
         int availableWidth = this.getWidth();
         g.drawText('◁', x, 2, 0, availableWidth);
         x += 3;
         g.drawText('▷', x + this._navBarWidth, 2, 0, availableWidth);
      }
   }
}
