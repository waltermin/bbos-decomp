package net.rim.device.apps.internal.profiles;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class ProfileEditScreen$EditConfigurationVerb extends Verb {
   private Object _source;
   private final ProfileEditScreen this$0;

   ProfileEditScreen$EditConfigurationVerb(ProfileEditScreen _1, Object source) {
      super(524800, CommonResource.getBundle(), 16);
      this.this$0 = _1;
      this._source = source;
   }

   @Override
   public final Object invoke(Object anObject) {
      if (this.this$0._profile.isRemovable() && this.this$0._profile.getName().length() == 0) {
         this.this$0.setDirty(true);
      }

      UiApplication.getUiApplication().pushScreen(new ConfigurationEditScreen(this.this$0._profile, this._source));
      return null;
   }
}
