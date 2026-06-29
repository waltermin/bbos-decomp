package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class DeleteProfileVerb extends Verb {
   private Profile _profile;

   DeleteProfileVerb(Profile profile) {
      super(626752, CommonResource.getBundle(), 17);
      this._profile = profile;
   }

   @Override
   public final Object invoke(Object anObject) {
      if (ProfilesOptions.getOptions().getConfirmDelete()) {
         ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         if (Dialog.ask(2, resources.getString(202), 3) != 3) {
            return null;
         }
      }

      Profiles profiles = Profiles.getInstance();
      profiles.remove(this._profile, true);
      return null;
   }
}
