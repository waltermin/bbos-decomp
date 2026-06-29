package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.resources.MMSResources;

final class PriorityChoiceField extends ObjectChoiceField {
   public PriorityChoiceField(String originalPriority) {
      super(MMSResources.getString(59), getPriorityLabels(), getInitialIndex(originalPriority));
   }

   public final String getPriority() {
      int value;
      switch (this.getSelectedIndex()) {
         case 0:
            value = 128;
            break;
         case 2:
            value = 130;
            break;
         default:
            value = 129;
      }

      return Integer.toString(value);
   }

   private static final Object[] getPriorityLabels() {
      return new Object[]{MMSResources.getString(61), MMSResources.getString(58), MMSResources.getString(60)};
   }

   private static final int getInitialIndex(String str) {
      int priority = MMSUtilities.parseInt(str, 129);
      switch (priority) {
         case 128:
            return 0;
         case 130:
            return 2;
         default:
            return 1;
      }
   }
}
