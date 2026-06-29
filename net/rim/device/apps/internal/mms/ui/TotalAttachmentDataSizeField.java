package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.internal.mms.resources.MMSResources;

final class TotalAttachmentDataSizeField extends ObjectChoiceField {
   public TotalAttachmentDataSizeField(int size) {
      super(MMSResources.getString(99), getChoices(size), 0, 9007199254740992L);
   }

   private static final Object[] getChoices(int size) {
      int kb = size / 1024;
      int rem = size - kb * 1024;
      int dot = (10 * rem + 1023) / 1024;
      String choice = ((StringBuffer)(new Object())).append(Integer.toString(kb)).append('.').append(Integer.toString(dot)).append(" KB").toString();
      return new Object[]{choice};
   }
}
