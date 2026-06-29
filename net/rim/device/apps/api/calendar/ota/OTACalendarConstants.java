package net.rim.device.apps.api.calendar.ota;

public interface OTACalendarConstants extends OTAEvents {
   String RIM_CALENDAR_APPT_UPDATE = "net.rim.RIMCalendarApptUpdate";
   String RIM_CALENDAR_APPT_DELETE = "net.rim.RIMCalendarApptDelete";
   String RIM_CALENDAR_MEETING_REQUEST = "net.rim.RIMCalendarMeetingRequest";
   String RIM_CALENDAR_MEETING_RESPONSE = "net.rim.RIMCalendarMeetingResponse";
   String RIM_CALENDAR_MEETING_CANCEL = "net.rim.RIMCalendarMeetingCancel";
   String RIM_CALENDAR_CONFIG = "net.rim.RIMCalendarConfig";
   String RIM_CALENDAR_SLOW_SYNC = "net.rim.RIMCalendarSlowSync";
   String RIM_CALENDAR_FOLDER_MANAGEMENT = "net.rim.RIMCalendarFolderManagement";
   int TYPE_MEETING_REQUEST = 1;
   int TYPE_MEETING_RESPONSE = 2;
   int TYPE_MEETING_CANCELATION = 3;
}
