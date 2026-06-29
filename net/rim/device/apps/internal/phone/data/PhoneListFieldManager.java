package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

class PhoneListFieldManager extends VerticalFieldManager implements ListFieldCallback {
   protected ListField _listField;

   PhoneListFieldManager(long style, boolean allowMultiSelect) {
      super(style | 1152921504606846976L | 2305843009213693952L | 281474976710656L | 17592186044416L);
      this.setVerticalQuantization(-1);
      int listFieldStyle = 0;
      if (allowMultiSelect && !PhoneUtilities.isQwertyReducedKeyboard()) {
         listFieldStyle |= 2;
      }

      this._listField = new PhoneListFieldManager$1(this, 0, listFieldStyle);
      this.setEmptyString(this._listField);
      this._listField.setCallback(this);
      this.add(this._listField);
   }

   protected void setEmptyString(ListField listField) {
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      return (status & 131072) == 0 && Trackball.isSupported() ? 0 : super.moveFocus(amount, status, time);
   }

   @Override
   public void drawListRow(ListField _1, Graphics _2, int _3, int _4, int _5) {
      throw null;
   }

   @Override
   public Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }
}
