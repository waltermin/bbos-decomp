package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.service.ServiceObject;

class CICALConfigConverter$OTAConfigEvent implements ServiceObject {
   byte _result = -1;
   int _command;
   byte[] _conflictSettings;
   byte[] _capabilitySettings;
   byte[] _userSettings;
   CalendarService _calendarService;

   public void setUserSettings(byte[] settings) {
      this._userSettings = settings;
   }

   public int getCommand() {
      return this._command;
   }

   public boolean isValid() {
      return this._result == 0;
   }

   public void setResult(byte result) {
      this._result = result;
   }

   public byte getResult() {
      return this._result;
   }

   public byte[] getCapabilitySettings() {
      return this._capabilitySettings;
   }

   public void setCapabilitySettings(byte[] settings) {
      this._capabilitySettings = settings;
   }

   public byte[] getConflictSettings() {
      return this._conflictSettings;
   }

   public void setConflictSettings(byte[] settings) {
      this._conflictSettings = settings;
   }

   public byte[] getUserSettings() {
      return this._userSettings;
   }

   @Override
   public ServiceIdentifier getServiceIdentifier() {
      return this._calendarService;
   }

   CICALConfigConverter$OTAConfigEvent(int command, CalendarService calendarService) {
      this._calendarService = calendarService;
      this._command = command;
   }
}
