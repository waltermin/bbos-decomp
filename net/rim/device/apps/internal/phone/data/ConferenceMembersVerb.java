package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class ConferenceMembersVerb extends Verb {
   private PhoneCallModelImpl _callLog;

   ConferenceMembersVerb(PhoneCallModelImpl callLog, int ordering, int resourceId) {
      super(ordering, PhoneResources.getResourceBundle(), resourceId);
      this._callLog = callLog;
   }

   @Override
   public final Object invoke(Object parameter) {
      Screen screen = new ConferenceMembersScreen(this._callLog);
      UiApplication.getUiApplication().pushModalScreen(screen);
      return null;
   }
}
