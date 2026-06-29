package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.SeparatorControl;
import net.rim.wica.runtime.ui.View;

final class WicletSeparatorField extends SeparatorField implements View {
   private ScreenContext _context;
   private SeparatorControl _model;
   private byte _visibility;

   WicletSeparatorField(ScreenContext context, SeparatorControl model, int row) {
      this.setTag(null);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
   }

   @Override
   public final int getPreferredWidth() {
      return Graphics.getScreenWidth() >> 1;
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int styleId = this._model.getStyle();
      if (styleId != -1) {
         ThemeAttributeSet attributes = this._context.getStyleFactory().getStyle(styleId);
         this.setThemeAttributesSpecial(attributes, null);
      }

      super.applyFont();
   }

   @Override
   protected final void layout(int width, int height) {
      if (this._visibility != 1) {
         if (this._model.isWhitespace()) {
            this.setExtent(width, Font.getDefaultHeight(0) >> 1);
         } else {
            super.layout(width, height);
         }
      } else {
         this.setExtent(0, 0);
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._visibility == 0 && !this._model.isWhitespace()) {
         super.paint(graphics);
      }
   }

   @Override
   public final UIComponent getModel() {
      return this._model;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._model = (SeparatorControl)model;
   }

   @Override
   public final void setVisibility(byte visibility) {
      if (visibility != this._visibility) {
         this._visibility = visibility;
         if (this._visibility != 2) {
            this.updateLayout();
            return;
         }

         this.invalidate();
      }
   }

   @Override
   public final void update(int row) {
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
   }
}
