package net.rim.device.apps.internal.freebusy;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;

public class AttendeeList {
   private Hashtable _attendees = new Hashtable(10);

   AttendeeList(Enumeration attendees) {
      if (attendees != null) {
         while (attendees.hasMoreElements()) {
            Attendee nextAttendee = (Attendee)attendees.nextElement();
            this._attendees.put(nextAttendee.getAddress(), nextAttendee);
         }
      }
   }

   public void addAttendee(Attendee attendee) {
      this._attendees.put(attendee.getAddress(), attendee);
   }

   public Attendee removeAttendee(String emailAddress) {
      return (Attendee)this._attendees.remove(emailAddress);
   }

   public Attendee getAttendee(String emailAddress) {
      return (Attendee)this._attendees.get(emailAddress);
   }

   public Attendee[] getAttendees() {
      Enumeration e = this._attendees.elements();
      Attendee[] attendees = new Attendee[0];

      while (e.hasMoreElements()) {
         Object o = e.nextElement();
         if (o instanceof Attendee) {
            Arrays.add(attendees, (Attendee)o);
         }
      }

      return attendees;
   }

   public int size() {
      return this._attendees.size();
   }
}
