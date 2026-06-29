package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.LabelControl;
import net.rim.wica.runtime.ui.View;

final class WicletLabelField extends LabelField implements View {
   private ScreenContext _context;
   private LabelControl _model;
   private byte _visibility;

   WicletLabelField(ScreenContext context, LabelControl model, int row) {
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      this.setValue(row);
   }

   private final void setValue(int row) {
      Object value = this._model.getValue();
      if (value == null) {
         this.setText(null);
      } else if (value instanceof Object) {
         this.setText(((Vector)value).elementAt(row));
      } else {
         this.setText(value);
      }
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
   protected final void layout(int width, int height) {
      if (this._visibility != 1) {
         super.layout(width, height);
      } else {
         this.setExtent(0, 0);
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._visibility == 0) {
         super.paint(graphics);
      }
   }

   @Override
   public final UIComponent getModel() {
      return this._model;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._model = (LabelControl)model;
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
      this.setValue(row);
   }
}
