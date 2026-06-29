package net.rim.device.api.ui.container;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UserInputEventListener;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.HorizontalFieldManager3;

class Tooltip$TooltipScreen extends Screen implements UserInputEventListener {
   private Tooltip _tooltip;
   private final Tooltip this$0;

   public void setTooltip(Tooltip tooltip) {
      this._tooltip = tooltip;
      HorizontalFieldManager hfm = (HorizontalFieldManager)this.getDelegate();
      if (hfm.getFieldCount() != 0) {
         hfm.delete(hfm.getField(0));
      }

      hfm.add(tooltip._content);
   }

   @Override
   public void onUserInput(int device, int flags) {
      this.this$0.dismiss();
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         Ui.getUiEngine().addUserInputEventListener(this);
         this.this$0._popScreenRunnable.init();
      } else {
         Ui.getUiEngine().removeUserInputEventListener(this);
      }
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   public Tooltip$TooltipScreen(Tooltip _1) {
      super(new HorizontalFieldManager3());
      this.this$0 = _1;
      this.setTag(Tooltip.TAG);
      this.setAcceptsInput(false);
   }

   @Override
   protected void sublayout(int width, int height) {
      width -= this.getMarginLeft() + this.getMarginRight();
      height -= this.getMarginTop() + this.getMarginBottom();
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
      XYRect fmExtent = this.getDelegate().getExtent();
      this.setExtent(fmExtent.width, fmExtent.height);
      XYPoint point = this._tooltip.getPosition();
      this.setPosition(point.x, point.y);
   }
}
