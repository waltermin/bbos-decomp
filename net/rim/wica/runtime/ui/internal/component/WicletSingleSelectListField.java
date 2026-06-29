package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.wica.runtime.metadata.component.ui.control.ChoiceControl;
import net.rim.wica.runtime.ui.internal.ModelUpdater;

final class WicletSingleSelectListField extends ChoiceField implements ModelUpdater, FocusChangeListener {
   private WicletSingleSelectListField$WicletListField _listField;
   private long _style;

   WicletSingleSelectListField(ScreenContext context, ChoiceControl controller, int row, long style) {
   }

   @Override
   public final void focusChanged(Field field, int eventType) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   @Override
   protected final void addInternal(int fromIndex, int count) {
      this._listField = new WicletSingleSelectListField$WicletListField(this._style);
      this.update(fromIndex, count);
      this._listField.setFocusListener(this);
      this.add(this._listField);
   }

   @Override
   public final void update(int fromIndex, int count) {
      Vector values = this.getValues();
      Object[] itemsArray = new Object[values.size()];
      values.copyInto(itemsArray);
      Object[] items = new Object[count];
      System.arraycopy(itemsArray, fromIndex, items, 0, count);
      if (this._listField != null) {
         this._listField.set(items);
      }
   }

   @Override
   public final void setSelected(int index) {
      if (this._listField != null) {
         Field fieldWithFocus = this.getFieldWithFocus();
         if (index > -1 && this._listField != fieldWithFocus && fieldWithFocus != null) {
            this.delete(fieldWithFocus);
            this.insert(fieldWithFocus, 0);
         }

         this._listField.setSelectedIndex(index);
      }
   }

   @Override
   public final void updateModel() {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }
}
