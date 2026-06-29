package net.rim.blackberry.api.menuitem;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;

final class ApplicationMenuItemRepository$SdkVerbFactory implements VerbFactory {
   private Verb[] _verbs;

   ApplicationMenuItemRepository$SdkVerbFactory(long id, ApplicationMenuItem ami, ApplicationDescriptor application) {
      Verb v = new SdkProxyVerb(ami, id, application);
      this._verbs = new Verb[1];
      this._verbs[0] = v;
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      return ApplicationManager.getApplicationManager().isSystemLocked() ? null : this._verbs;
   }
}
