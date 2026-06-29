package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

class CalendarEventVerb extends Verb {
   private int _displayStringId;
   private ResourceBundle _rb;

   public CalendarEventVerb(int displayStringId, int ordering) {
      this(null, displayStringId, ordering);
   }

   public CalendarEventVerb(ResourceBundle rb, int displayStringId, int ordering) {
      super(ordering);
      this._rb = rb;
      this._displayStringId = displayStringId;
   }

   @Override
   public String toString() {
      if (this._rb == null) {
         String displayString = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar")
            .getString(this._displayStringId);
         return displayString;
      } else {
         return CommonResource.getString(this._displayStringId);
      }
   }
}
