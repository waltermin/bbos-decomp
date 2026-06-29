package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public final class AttachAppointmentVerb extends Verb implements ConditionalVerb {
   private MMSPresentationModel _presentation;

   public AttachAppointmentVerb(MMSPresentationModel presentation) {
      super(16864031);
      this._presentation = presentation;
   }

   @Override
   public final String toString() {
      return MMSResources.getString(93);
   }

   @Override
   public final boolean canInvoke(Object parameter) {
      return true;
   }

   @Override
   public final Object invoke(Object context) {
      MMSSendVCalVerb sendVCalVerb = MMSUtilities.getSendAsVCalVerb();
      if (sendVCalVerb != null) {
         sendVCalVerb.setPresentationModel(this._presentation);
      }

      String[] args = new Object[1];
      args[0] = "closeable";
      CalendarProxy.startCalendar(args);
      return null;
   }
}
