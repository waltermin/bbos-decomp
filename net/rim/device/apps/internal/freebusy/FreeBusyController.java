package net.rim.device.apps.internal.freebusy;

import java.util.Date;
import java.util.Enumeration;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.ui.AppsMainScreen;

public class FreeBusyController extends AppsMainScreen {
   FreeBusyHeader _headerField;
   FreeBusyField _freeBusyDisplayField;
   Date _startDate;
   Date _endDate;
   Enumeration _attendees;
   VerticalFieldManager _vfm = new VerticalFieldManager(562949953421312L);

   public FreeBusyController(MeetingInfo meeting, long startDate, long endDate) {
      super(562949953617920L);
      this._freeBusyDisplayField = new FreeBusyField(meeting.getAttendees(), startDate, endDate, this);
      this._startDate = new Date(startDate);
      this._endDate = new Date(endDate);
      this._headerField = new FreeBusyHeader(startDate, this);
      this.initializeFields();
   }

   private void initializeFields() {
      this._vfm.add(this._headerField);
      this._vfm.add(this._freeBusyDisplayField);
      this._vfm.add(new MeetingField("Meeting: ", this._startDate.getTime(), this._endDate.getTime()));
      this._vfm.add(new SuggestedMeetingField("Suggested: ", this._startDate.getTime() + 3600000, this._endDate.getTime() + 3600000));
      this.add(this._vfm);
   }

   public void updateHighlightedDate(long newDate) {
      if (this._freeBusyDisplayField != null) {
         this._freeBusyDisplayField.setHighlightedDate(newDate);
      }
   }

   public void updateHeaderDate(long newDate) {
      if (this._headerField != null) {
         this._headerField.updateDate(newDate);
      }
   }
}
