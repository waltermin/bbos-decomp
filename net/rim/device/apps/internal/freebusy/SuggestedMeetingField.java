package net.rim.device.apps.internal.freebusy;

class SuggestedMeetingField extends MeetingField {
   SuggestedMeetingField(String labelText, long startDate, long endDate) {
      super(labelText, startDate, endDate);
   }

   @Override
   public boolean isFocusable() {
      return true;
   }
}
