package net.rim.wica.runtime.ui.internal.component;

import java.util.Date;
import java.util.Vector;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.EditControl;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.Focusable;
import net.rim.wica.runtime.util.LongVector;

final class WicletDateEditField extends DateField implements View, FieldChangeListener, Focusable {
   private ScreenContext _context;
   private EditControl _model;
   private byte _visibility;

   WicletDateEditField(ScreenContext context, EditControl model, int row, long style) {
      super("", 0, 54 | style);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      this.setEditable(!model.isReadOnly());
      String format = model.getFormat();
      if (format != null) {
         DateFormat df = (DateFormat)(new Object(format));
         int[] fields = df.getFields();
         if (fields != null && fields.length > 0) {
            this.setFormat(df);
         }
      } else if (model.getEditType() == 4) {
         this.setFormat(DateFormat.getInstance(6));
      }

      this.initialize(row);
      this.setChangeListener(this);
   }

   private final void initialize(int row) {
      Object value = this._model.getValue();
      long date = 0;
      if (value == null) {
         date = ((Date)(new Object())).getTime();
         this._model.initializeToEmpty(new Object(date));
      } else if (!(value instanceof Object)) {
         if (!(value instanceof LongVector)) {
            if (value instanceof Object) {
               date = this.parseDate((String)value);
            } else {
               date = value;
            }
         } else {
            LongVector values = (LongVector)value;
            if (values.size() > 0) {
               date = values.elementAt(row);
            } else {
               date = ((Date)(new Object())).getTime();
            }
         }
      } else {
         Vector values = (Vector)value;
         if (values.size() > 0) {
            date = this.parseDate((String)values.elementAt(row));
         } else {
            date = ((Date)(new Object())).getTime();
         }
      }

      this.setDate(date);
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

   @Override
   protected final void onUnfocus() {
      super.onUnfocus();
      if (this.isVisible()) {
         this._model.eventOccurred(0);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this._model.setValue(new Object(this.getDate()), true);
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      if (this.isEditable()) {
         super.drawFocus(graphics, on);
      } else {
         XYRect rect = (XYRect)(new Object());
         this.getFocusRect(rect);
         this.drawHighlightRegion(graphics, 1, on, rect.x, rect.y, rect.width, rect.height);
      }
   }

   private final long parseDate(String date) {
      long time = HttpDateParser.parse(date);
      return time == -1 && !Character.isDigit(date.charAt(0)) ? 0 : time;
   }
}
