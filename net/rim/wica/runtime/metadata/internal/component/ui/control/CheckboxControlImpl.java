package net.rim.wica.runtime.metadata.internal.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.CheckboxControl;
import net.rim.wica.runtime.metadata.internal.component.ui.UIControlImpl;

public class CheckboxControlImpl extends UIControlImpl implements CheckboxControl {
   public CheckboxControlImpl(int id, UIContainer parent, int style, int bits, int x, int y, int initEventId, Object inValue, int changeEventId, int[] mapping) {
      super(id, 139, parent, style, bits, x, y, initEventId, inValue);
      super._valueType = 0;
      if (this.requireInValueArray()) {
         super._valueType |= 32768;
      }

      this.setEvent(1, changeEventId);
      this.setMapping(mapping);
   }

   @Override
   public void setValue(boolean value, boolean fromUi) {
      this.setValue(value ? Boolean.TRUE : Boolean.FALSE, fromUi);
   }

   @Override
   public void initializeToEmpty(boolean value) {
      this.initializeToEmpty(value ? Boolean.TRUE : Boolean.FALSE);
   }
}
