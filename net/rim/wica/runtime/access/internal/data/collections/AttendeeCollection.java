package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.AttendeeFactory;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.wica.common.builtindata.componentdefn.AttendeeCompDef;
import net.rim.wica.runtime.metadata.internal.WicletEx;

public class AttendeeCollection extends AccessInnerDataCollection {
   public AttendeeCollection(WicletEx wiclet) {
      super(wiclet, AttendeeCompDef.getInstance());
      this.initFieldHandlers();
   }

   private void initFieldHandlers() {
      super._objectFieldHandlers = new IntHashtable(1);
      super._objectFieldHandlers.put(1, new AttendeeCollection$AddressHandler(null));
      super._intFieldHandlers = new IntHashtable(1);
      super._intFieldHandlers.put(0, new AttendeeCollection$TypeHandler(null));
   }

   @Override
   public Object getDBItemFromHandle(long dataHandle) {
      return null;
   }

   @Override
   protected void loadItem(long dataHandle) {
   }

   public void saveAttendeeInEvent(long dataHandle, Event event) {
      int attendeeType = this.getIntFieldValue(dataHandle, 0);
      String attendeeAddress = (String)this.getObjectFieldValue(dataHandle, 1);
      if (event != null && attendeeAddress != null) {
         Attendee attendee = AttendeeFactory.createAttendeeFromRFC822(attendeeType, attendeeAddress);
         MeetingInfo meetingInfo = event.getMeetingInfo();
         meetingInfo.addAttendee(attendee);
      }
   }

   public void loadItemFromAttendee(long attendeeHandle, Attendee attendee) {
      if (attendee != null) {
         EmailAddressModel eam = (EmailAddressModel)attendee.getAddress();
         if (eam != null) {
            this.setObjectFieldValue(attendeeHandle, 1, eam.getAddress());
            this.setIntFieldValue(attendeeHandle, 0, attendee.getType());
            super._loadedItems.addElement(this.getHandle(attendeeHandle));
         }
      }
   }
}
