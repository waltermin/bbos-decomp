package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class EditProfileVerb extends Verb {
   private Profile _profile;

   EditProfileVerb(Profile profile) {
      super(profile == null ? 626704 : 626720, CommonResource.getBundle(), profile == null ? 13 : 16);
      this._profile = profile;
   }

   @Override
   public final String toString() {
      if (this._profile == null) {
         ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         return resources.getString(231);
      } else {
         return super.toString();
      }
   }

   @Override
   public final Object invoke(Object context) {
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      boolean newProfile = false;
      if (this._profile == null) {
         newProfile = true;
         Profiles profiles = Profiles.getInstance();
         this._profile = profiles.createNewProfile(resources.getString(220), (byte)-1);
      }

      UiApplication.getUiApplication().pushScreen(new ProfileEditScreen(this._profile, newProfile));
      return null;
   }
}
