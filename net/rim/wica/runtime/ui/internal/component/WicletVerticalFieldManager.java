package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.ui.View;

final class WicletVerticalFieldManager extends VerticalFieldManager implements View {
   private ScreenContext _context;
   private UIContainer _model;
   private byte _visibility;

   WicletVerticalFieldManager(ScreenContext context, UIContainer model, int row, long style) {
      super(1155173304420532224L);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      ComponentHelper.buildLayout(this._context, this, model, row, style);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int styleId = this._model.getStyle();
      if (styleId != -1) {
         ThemeAttributeSet attributes = this._context.getStyleFactory().getStyle(styleId);
         this.setThemeAttributeSet(attributes);
      }

      super.applyFont();
   }

   @Override
   public final boolean isFocusable() {
      return this._visibility == 0 ? super.isFocusable() : false;
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._visibility != 1) {
         super.sublayout(width, height);
      } else {
         this.setVirtualExtent(0, 0);
         this.setExtent(0, 0);
      }
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._visibility == 0) {
         super.subpaint(graphics);
      }
   }

   @Override
   public final UIComponent getModel() {
      return this._model;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._model = (UIContainer)model;
   }

   @Override
   public final void setVisibility(byte visibility) {
      if (visibility != this._visibility) {
         this._visibility = visibility;
         if (this._visibility != 0 && this.getLeafFieldWithFocus() != null) {
            this.onUnfocus();
         }

         if (this._visibility != 2) {
            this.updateLayout();
            return;
         }

         this.invalidate();
      }
   }

   @Override
   protected final boolean incrementalLayout(int index, int added, int deleted) {
      return this._context.getSuspendLayout() ? true : super.incrementalLayout(index, added, deleted);
   }

   @Override
   public final void update(int row) {
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
      ComponentHelper.updateLayout(this._context, this, this._model, row);
      this.updateLayout();
   }
}
