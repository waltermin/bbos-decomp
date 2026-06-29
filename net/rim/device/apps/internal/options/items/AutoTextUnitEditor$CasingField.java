package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class AutoTextUnitEditor$CasingField extends ObjectChoiceField {
   public AutoTextUnitEditor$CasingField(int initialValue) {
      super(OptionsResources.getString(305), AutoTextUnitEditor.CASING_FIELD_CHOICES);
      this.setCookie(this);
      this.setSelectedIndex(initialValue);
   }
}
