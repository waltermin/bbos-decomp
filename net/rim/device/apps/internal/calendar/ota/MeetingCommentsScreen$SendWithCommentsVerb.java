package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

class MeetingCommentsScreen$SendWithCommentsVerb extends Verb {
   private final MeetingCommentsScreen this$0;

   MeetingCommentsScreen$SendWithCommentsVerb(MeetingCommentsScreen _1) {
      super(332032);
      this.this$0 = _1;
   }

   @Override
   public String toString() {
      return CommonResources.getString(9150);
   }

   @Override
   public Object invoke(Object parameter) {
      this.this$0._sendSelected = true;
      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      return null;
   }
}
