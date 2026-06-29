package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

class MeetingCommentsScreen$CancelCommentsVerb extends Verb {
   private final MeetingCommentsScreen this$0;

   MeetingCommentsScreen$CancelCommentsVerb(MeetingCommentsScreen _1) {
      super(268500992);
      this.this$0 = _1;
   }

   @Override
   public String toString() {
      return CommonResource.getString(19);
   }

   @Override
   public Object invoke(Object parameter) {
      if (!this.this$0._commentField.isDirty() || this.this$0.confirmCancel()) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      }

      return null;
   }
}
