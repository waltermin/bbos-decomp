package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import java.util.Enumeration;

public interface MeetingInfo {
   long ID;
   byte CAPABILITIES_NONE;
   byte CAPABILITIES_MEETING_CAN_BE_MODIFIED;

   int getAttendeeCount();

   void addAttendee(Attendee var1);

   void removeAttendee(Attendee var1);

   void removeAttendees();

   Enumeration getAttendees();

   Attendee getAttendee(String var1);

   Attendee getOrganizer();

   boolean hasOrganizer();

   void setCapabilities(byte var1);

   byte getCapabilities();

   boolean meetingCanBeModified();
}
