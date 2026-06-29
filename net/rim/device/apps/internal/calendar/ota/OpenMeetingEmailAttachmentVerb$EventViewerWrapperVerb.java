package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

final class OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb extends Verb {
   private Verb _innerVerb;
   private final OpenMeetingEmailAttachmentVerb this$0;

   OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb(OpenMeetingEmailAttachmentVerb _1, Verb innerVerb) {
      super(innerVerb.getOrdering());
      this.this$0 = _1;
      this._innerVerb = innerVerb;
   }

   @Override
   public final String toString() {
      return this._innerVerb.toString();
   }

   @Override
   public final Object invoke(Object parameter) {
      Object result = this._innerVerb.invoke(parameter);
      boolean closeViewer = false;
      if (result != null) {
         if (result instanceof ContextObject) {
            if (ContextObject.getFlag(result, 39)) {
               closeViewer = true;
            }
         } else {
            closeViewer = true;
         }
      }

      if (closeViewer) {
         this.this$0._viewer.closeViewer(false);
      }

      return result;
   }
}
