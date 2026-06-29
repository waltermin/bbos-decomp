package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.ui.component.AutoTextEditField;

public final class YomiField extends AutoTextEditField {
   public YomiField() {
      this.setFilter(new YomiField$YomiFilter());
   }

   public YomiField(String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, style);
      this.setFilter(new YomiField$YomiFilter());
   }

   @Override
   public final boolean isFocusable() {
      return this.getInputContext().getActiveInputMethodID() == 512 ? super.isFocusable() : false;
   }

   @Override
   public final boolean isEditable() {
      return this.getInputContext().getActiveInputMethodID() == 512 ? super.isEditable() : false;
   }
}
