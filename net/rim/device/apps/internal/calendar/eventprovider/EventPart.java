package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.TimeZone;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventInstance;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfoProvider;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MultiServiceEvent;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.pim.TimeBasedObject;
import net.rim.device.internal.ui.IconCollection;

class EventPart
   extends EventBase
   implements RIMModel,
   EventInstance,
   Duration,
   HotKeyProvider,
   VerbProvider,
   UniqueIDProvider,
   TimeBasedObject,
   MultiServiceEvent,
   MeetingInfoProvider,
   ActionProvider {
   long _handle;
   EventImpl _event;

   public long getStartDate(TimeZone tz) {
      return this.getStart(tz);
   }

   @Override
   public Event getEventInstance() {
      return EventUtilities.createEventInstanceFromRecurrence(this._event, this._handle);
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      Event event = this._event;
      long handle = this._handle;
      DeleteEventVerb deleteEventVerb = new DeleteEventVerb(event, handle);
      CICALConfiguration cicalConfig = CalendarServiceManager.getInstance().getCICALConfiguration(event);
      if (event.isMeeting() && event.getMeetingInfo().meetingCanBeModified() && cicalConfig.isMeetingSyncEnabled()) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         DeleteEventVerb$DeleteEventVerbListener listener = (DeleteEventVerb$DeleteEventVerbListener)ar.waitFor(8503322669810003080L);
         deleteEventVerb.setDeleteEventVerbListener(listener);
      }

      return this.getVerbs(context, verbs, deleteEventVerb, new EditEventVerb(event, handle));
   }

   @Override
   public Object invokeHotkey(Object context, int hotkeyID) {
      if (hotkeyID != 127 && Keypad.getAltedChar((char)hotkeyID) != 127) {
         return null;
      }

      Event e = this._event;
      e = (Event)ObjectGroup.expandGroup(e);
      long handle = this._handle;
      Verb[] defaultVerbs = new Verb[2];
      DeleteEventVerb deleteVerb = new DeleteEventVerb(e, handle);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      DeleteEventVerb$DeleteEventVerbListener listener = (DeleteEventVerb$DeleteEventVerbListener)ar.waitFor(8503322669810003080L);
      deleteVerb.setDeleteEventVerbListener(listener);
      defaultVerbs[1] = deleteVerb;
      this.checkForVerbOverrides(context, defaultVerbs);
      return deleteVerb.invoke(null);
   }

   @Override
   public long getLUID(Object context) {
      return this._event.getLUID();
   }

   @Override
   public boolean perform(long actionId, Object context) {
      boolean returnValue = false;
      if (actionId == 6099736323056465049L) {
         EditEventVerb editVerb = new EditEventVerb(this._event, this._handle);
         editVerb.invoke(context);
         returnValue = true;
      }

      return returnValue;
   }

   @Override
   public long getCalendarServiceID() {
      return this._event.getCalendarServiceID();
   }

   @Override
   public long getCalendarFolderID() {
      return this._event.getCalendarFolderID();
   }

   @Override
   public int getColour() {
      return this._event.getColour();
   }

   @Override
   public boolean isMeeting() {
      return this._event.isMeeting();
   }

   @Override
   public MeetingInfo getMeetingInfo() {
      return this._event.getMeetingInfo();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof EventPart)) {
         return false;
      }

      EventPart ep = (EventPart)obj;
      return this._handle == ep._handle && this._event.equals(ep._event);
   }

   @Override
   public long getStart(TimeZone tz) {
      Event e = this._event;
      return e.getStartFromHandle(this._handle, tz);
   }

   @Override
   public long getDuration(TimeZone tz) {
      Event e = this._event;
      return e.getDurationFromHandle(this._handle, tz);
   }

   @Override
   public boolean isAllDay() {
      Event e = this._event;
      return e.getAllDayFlag();
   }

   @Override
   public String getStringForField(long field) {
      return this._event.getStringForField(field, this._handle);
   }

   @Override
   public String getStringForField(long field, long data) {
      return this._event.getStringForField(field, this._handle);
   }

   @Override
   public int getIconsForField(long field, IconCollection[] icons, int[][] indices) {
      return this._event.getIconsForField(field, icons, indices);
   }

   @Override
   public byte getProperties() {
      return this._event.getPropertiesForEvent();
   }
}
