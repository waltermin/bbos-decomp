package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.wica.runtime.metadata.component.ui.control.ChoiceControl;

final class WicletRadioButtonListField extends ChoiceField implements FieldChangeListener {
   private RadioButtonGroup _group;

   WicletRadioButtonListField(ScreenContext context, ChoiceControl controller, int row, long style) {
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   @Override
   protected final void addInternal(int fromIndex, int count) {
      this._group = new RadioButtonGroup();

      for (int i = fromIndex; i < fromIndex + count; i++) {
         WicletRadioButtonListField$WicletRadioButtonField radio = new WicletRadioButtonListField$WicletRadioButtonField(this.getLabel(i), this._group, false);
         radio.setEditable(this.isEditable());
         this.add(radio);
      }

      this._group.setChangeListener(this);
   }

   @Override
   public final void update(int fromIndex, int count) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   @Override
   public final void setSelected(int index) {
      if (this._group != null) {
         this._group.setSelectedIndex(index);
      }
   }
}
