package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.text.EmailAddressTextFilter;
import net.rim.device.api.ui.text.PhoneTextFilter;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.ui.text.URLTextFilter;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.EditControl;
import net.rim.wica.runtime.metadata.component.ui.control.TextAreaControl;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.Focusable;
import net.rim.wica.runtime.ui.internal.ModelUpdater;
import net.rim.wica.runtime.util.Util;

final class WicletTextEditField extends Manager implements FieldChangeListener, View, ModelUpdater, Focusable {
   private ScreenContext _context;
   private EditControl _model;
   private BasicEditField _edit;
   private byte _visibility;
   private boolean _valid;
   private static final int BORDER = 1;
   private static final int DOUBLE_BORDER = 2;
   private static final int PADDING = 1;
   private static final int DOUBLE_PADDING = 2;
   private static final char MANDATORY_MARKER_CHAR = '*';
   private static final int INVALID_MARKER_COLOR = 16711680;
   private static final int VALID_MARKER_COLOR = 32768;

   final boolean performDefaultAction() {
      this.updateModel();
      return this._model.getScreen().performDefaultAction();
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
      this._visibility = visibility;
      if (this._visibility != 0 && this.getLeafFieldWithFocus() != null) {
         this.onUnfocus();
      }

      if (this._visibility != 2) {
         this._context.setSuspendLayout(false);
         this.updateLayout();
         this._context.setSuspendLayout(true);
      } else {
         this.invalidate();
      }
   }

   @Override
   public final void update(int row) {
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
      String text = null;
      Object value = this._model.getValue();
      if (value != null) {
         if (this._model.isRepetition()) {
            text = ((Vector)value).elementAt(row).toString();
         } else {
            text = value.toString();
         }
      }

      this._edit.setEditable(!this._model.isReadOnly());
      if (this._visibility == 0) {
         this.setValidatedText(text);
      }
   }

   @Override
   public final void updateModel() {
      if (this._edit.isDirty()) {
         String text = this._edit.getText();
         if (!(this._model instanceof TextAreaControl)) {
            int editType = this._model.getEditType();
            if (editType == 2 || editType == 5) {
               text = Util.filterNumber(text);
            } else if (editType == 6) {
               text = Util.filterURL(text);
            }
         }

         this._model.setValue(text, true);
         this._edit.setDirty(false);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      boolean valid = this._valid;
      this._valid = this._model.validateText(this._edit.getText());
      if (this._valid != valid) {
         this.invalidate();
      }
   }

   private static final BasicEditField createBasicEditField(long style, TextFilter filter) {
      BasicEditField field = new WicletTextEditField$BasicEditFieldMF(style);
      if (filter != null) {
         field.setFilter(filter);
      }

      return field;
   }

   private final void setValidatedText(String text) {
      if (text == null) {
         this._edit.setText("");
      } else {
         try {
            this._edit.setText(text);
         } finally {
            this._edit.setText("");
            return;
         }
      }
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
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
   public final int getPreferredHeight() {
      return this.getField(0).getPreferredHeight() + 2 + 2;
   }

   @Override
   public final int getPreferredWidth() {
      return Graphics.getScreenWidth() / 3;
   }

   @Override
   public final boolean isFocusable() {
      return this._visibility == 0 ? super.isFocusable() : false;
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      if (!this._model.isReadOnly()) {
         this._edit.setCursorPosition(this._edit.getCursorPosition());
         this.invalidate();
      }
   }

   @Override
   protected final void onUnfocus() {
      super.onUnfocus();
      if (!this._model.isReadOnly()) {
         this.invalidate();
      }

      if (this.isVisible()) {
         this.updateModel();
         this._model.eventOccurred(0);
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._visibility != 1) {
         int xSpace = 0;
         if (this._model.isMandatory()) {
            Font currentFont = this.getFont();
            Font markerFont = currentFont.derive(1, currentFont.getHeight() >> 1);
            xSpace = markerFont.getAdvance('*') + 4;
         }

         int borderWidth = 0;
         if (this._model.getBorder()) {
            borderWidth = 4;
            this.layoutChild(this._edit, width - borderWidth - xSpace, height);
            this.setPositionChild(this._edit, 2 + xSpace, 2);
         } else {
            this.layoutChild(this._edit, width - xSpace, height);
            this.setPositionChild(this._edit, xSpace, 0);
         }

         this.setExtent(width, this._edit.getHeight() + borderWidth);
      } else {
         this.setVirtualExtent(0, 0);
         this.setExtent(0, 0);
      }
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._visibility == 0) {
         if (!this._model.isReadOnly()) {
            if (this._model.isMandatory()) {
               Font currentFont = graphics.getFont();
               Font markerFont = currentFont.derive(1, currentFont.getHeight() >> 1);
               int currentColor = graphics.getColor();
               int markerWidth = markerFont.getAdvance('*');
               graphics.setColor(this._valid ? 32768 : 16711680);
               graphics.setFont(markerFont);
               graphics.drawText('*', 2, 0, 53, markerWidth);
               graphics.setColor(currentColor);
               graphics.setFont(currentFont);
            }

            if (this.getFieldWithFocus() != null && this._model.getBorder()) {
               XYRect rect = this._edit.getExtent();
               int topLeftOffset = 2;
               int bottomRightOffset = 4;
               graphics.drawRect(rect.x - topLeftOffset, rect.y - topLeftOffset, rect.width + bottomRightOffset, rect.height + bottomRightOffset);
            }
         }

         super.subpaint(graphics);
      }
   }

   WicletTextEditField(ScreenContext context, EditControl model, int row, long style) {
      super(2251799813685248L | style);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      long fieldStyle = 4294967298L | style;
      String text = null;
      Object value = model.getValue();
      if (value != null) {
         if (!(value instanceof Vector)) {
            text = value.toString();
         } else {
            text = ((Vector)value).elementAt(row).toString();
         }
      }

      int editType = 0;
      if (model instanceof TextAreaControl) {
         this._edit = new WicletTextEditField$ActiveAutoTextEditFieldMF(null, text, 1000000, fieldStyle);
      } else {
         fieldStyle |= 2147483648L;
         editType = model.getEditType();
         switch (editType) {
            case 0:
            case 3:
            case 4:
               String format = model.getFormat();
               fieldStyle |= format == null ? 0 : 1074266112;
               this._edit = new WicletBasicEditField(null, text, fieldStyle, format, model);
               break;
            case 1:
               this._edit = new NumberEditField(fieldStyle | 1073741824);
               break;
            case 2:
            case 5:
               this._edit = createBasicEditField(fieldStyle, null);
               break;
            case 6:
            default:
               this._edit = createBasicEditField(fieldStyle, new URLTextFilter());
               break;
            case 7:
               this._edit = new WicletTextEditField$PasswordEditFieldMF(null, text, 1000000, fieldStyle);
               break;
            case 8:
               this._edit = createBasicEditField(fieldStyle | 8192 | 1073741824, new PhoneTextFilter());
               break;
            case 9:
               if ((fieldStyle & 36028797018963968L) != 0) {
                  this._edit = createBasicEditField(fieldStyle, new EmailAddressTextFilter());
               } else {
                  this._edit = new WicletTextEditField$EmailAddressEditFieldMF(null, text, 1000000);
               }
         }
      }

      this.setValidatedText(text);
      this._edit.setEditable(!model.isReadOnly());
      if (model.isMandatory()) {
         this._valid = model.validateText(this._edit.getText());
         this._edit.setChangeListener(this);
      }

      this.add(this._edit);
   }
}
