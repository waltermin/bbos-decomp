package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailEditorScreen$TogglePrependedDisclaimerModelFieldVerb extends Verb {
   private PrependedDisclaimerModelField _disclaimerField;

   EmailEditorScreen$TogglePrependedDisclaimerModelFieldVerb(PrependedDisclaimerModelField disclaimerField) {
      super(16978000);
      this._disclaimerField = disclaimerField;
   }

   @Override
   public final Object invoke(Object parameter) {
      this._disclaimerField.togglePrependDisclaimer();
      return null;
   }

   @Override
   public final String toString() {
      int resourceId;
      if (this._disclaimerField.prependDisclaimer()) {
         resourceId = 77;
      } else {
         resourceId = 78;
      }

      return EmailResources.getString(resourceId);
   }
}
