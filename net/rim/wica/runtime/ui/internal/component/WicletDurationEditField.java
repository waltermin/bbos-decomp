package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.util.IntVector;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.EditControl;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.Focusable;
import net.rim.wica.runtime.util.LongVector;
import net.rim.wica.runtime.util.Util;

final class WicletDurationEditField extends BaseDurationField implements View, FieldChangeListener, Focusable {
   private ScreenContext _context;
   private EditControl _model;
   private byte _visibility;

   WicletDurationEditField(ScreenContext context, EditControl model, int row, long style) {
      super(3, 1, 0, style);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      this.setEditable(!model.isReadOnly());
      this.initialize(row);
      this.setChangeListener(this);
   }

   @Override
   public final void onUnfocus() {
      super.onUnfocus();
      if (this.isVisible()) {
         this._model.eventOccurred(0);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this._model.setValue(new Object(this.getDuration()), true);
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      return super.moveFocus(amount, status, time);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int styleId = this._model.getStyle();
      if (styleId != -1) {
         ThemeAttributeSet attributes = this._context.getStyleFactory().getStyle(styleId);
         this.setThemeAttributeSet(attributes);
         this.setThemeAttributesSpecial(attributes, null);
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
      this._model = (EditControl)model;
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
      this.setChangeListener(null);
      this.initialize(row);
      this.setChangeListener(this);
   }

   private final void initialize(int row) {
      Object value = this._model.getValue();
      long duration = 0;
      if (value == null) {
         this._model.initializeToEmpty(new Object(duration));
      } else if (!(value instanceof Object)) {
         if (!(value instanceof LongVector)) {
            if (!(value instanceof Object)) {
               if (value instanceof Object) {
                  duration = this.parseDuration((String)value);
               } else if (!(value instanceof Object)) {
                  duration = value;
               } else {
                  duration = ((Integer)value).intValue();
               }
            } else {
               IntVector values = (IntVector)value;
               if (values.size() > 0) {
                  duration = values.elementAt(row);
               }
            }
         } else {
            LongVector values = (LongVector)value;
            if (values.size() > 0) {
               duration = values.elementAt(row);
            }
         }
      } else {
         Vector values = (Vector)value;
         if (values.size() > 0) {
            duration = this.parseDuration((String)values.elementAt(row));
         }
      }

      this.setDuration(duration);
   }

   private final long parseDuration(String strDuration) {
      return Util.isValidLong(strDuration) ? Long.parseLong(strDuration) : HttpDateParser.parse(strDuration);
   }
}
