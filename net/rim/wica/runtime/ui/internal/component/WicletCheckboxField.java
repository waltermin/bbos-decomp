package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.CheckboxControl;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.Focusable;

final class WicletCheckboxField extends CheckboxField implements View, FieldChangeListener, Focusable {
   private ScreenContext _context;
   private CheckboxControl _model;
   private byte _visibility;

   WicletCheckboxField(ScreenContext context, CheckboxControl model, int row, long style) {
      super(null, false, style | 2147483648L);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      this.setEditable(!model.isReadOnly());
      Object value = model.getValue();
      if (value == null) {
         this.setChecked(false);
         model.initializeToEmpty(false);
      } else if (!(value instanceof Object)) {
         this.setChecked(value);
      } else {
         Boolean rowValue = (Boolean)((Vector)value).elementAt(row);
         this.setChecked(rowValue);
      }

      this.setChangeListener(this);
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
      this._model = (CheckboxControl)model;
   }

   @Override
   public final void setVisibility(byte visibility) {
      if (visibility != this._visibility) {
         this._visibility = visibility;
         if (this._visibility != 0 && this.isFocus()) {
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
   public final void update(int row) {
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
      this.setEditable(!this._model.isReadOnly());
      Object value = this._model.getValue();
      if (value == null) {
         this.setChecked(false);
         this._model.initializeToEmpty(false);
      } else if (!(value instanceof Object)) {
         this.setChecked(value);
      } else {
         Boolean rowValue = (Boolean)((Vector)value).elementAt(row);
         this.setChecked(rowValue);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         this._model.setValue(this.getChecked(), true);
         this._model.eventOccurred(1);
      }
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}
