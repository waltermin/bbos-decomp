package net.rim.device.apps.internal.browser.pme.form;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.internal.browser.stack.FormData;

public final class PMETextInput extends Manager implements FormField {
   private String _name;
   private String _initialValue;
   private int _rows = -1;
   private int _columns = -1;
   private EditField _edit;

   PMETextInput(String name, String label, String initialValue, int maxLength, long style, long entryStyle, int rows, int columns) {
      super(style);
      this._rows = rows;
      this._columns = columns;
      this._edit = new EditField(label, initialValue, maxLength, entryStyle);
      this._initialValue = initialValue;
      this._name = name;
      this.add(this._edit);
   }

   @Override
   public final void setChangeListener(FieldChangeListener listener) {
      this._edit.setChangeListener(listener);
   }

   @Override
   public final void setFocusListener(FocusChangeListener listener) {
      this._edit.setFocusListener(listener);
   }

   @Override
   protected final boolean isScrollCopyable() {
      return false;
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._columns > 0) {
         width = Math.min(width, this.getPreferredWidth());
      }

      if (this._rows > 0) {
         height = Math.min(height, this.getPreferredHeight());
      }

      width = Math.max(0, width);
      height = Math.max(0, height);
      int contentWidth = 0;
      int contentHeight = 0;
      if (this.getFieldCount() > 0) {
         Field field = this.getField(0);
         int childWidth = (this.getStyle() & 1125899906842624L) == 0 ? width : 1073741823;
         int childHeight = (this.getStyle() & 281474976710656L) == 0 ? height : 1073741823;
         this.layoutChild(field, childWidth, childHeight);
         contentWidth = field.getWidth();
         contentHeight = field.getHeight();
      }

      this.setExtent(width, height);
      this.setVirtualExtent(Math.max(width, contentWidth), Math.max(height, contentHeight));
   }

   @Override
   public final int getPreferredHeight() {
      return this._rows < 0 ? this.getHeight() : this.getFont().getHeight() * this._rows;
   }

   @Override
   public final int getPreferredWidth() {
      return this._columns < 0 ? this.getWidth() : this.getFont().getBounds((char)109) * this._columns;
   }

   @Override
   public final void submit(FormData formData, Object buffer) {
      formData.append(buffer, this._name, this._edit.getText());
   }

   @Override
   public final void reset() {
      this._edit.setText(this._initialValue);
      if ((this.getStyle() & 1125899906842624L) != 0) {
         this.setHorizontalScroll(0);
      }
   }
}
