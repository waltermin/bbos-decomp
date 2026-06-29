package net.rim.device.apps.api.framework.verb;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.ui.ToggleableField;

public final class ToggleQualifiedFriendlyVerb extends Verb {
   private ToggleableField _field;

   public ToggleQualifiedFriendlyVerb(ToggleableField field, ResourceBundleFamily rb, int rbKey) {
      super(16859712, rb, rbKey);
      this._field = field;
   }

   @Override
   public final Object invoke(Object parameter) {
      this._field.toggleVisibleField();
      return null;
   }
}
