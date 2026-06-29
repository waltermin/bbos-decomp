package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.component.LabelField;

final class WeekController$LayoutLabelField extends LabelField {
   public WeekController$LayoutLabelField(Object text, long style) {
      super(text, style);
   }

   public final void forceUpdateLayout() {
      this.updateLayout();
   }
}
