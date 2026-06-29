package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.util.Factory;

class MeetingInfoFactory implements Factory {
   @Override
   public Object createInstance(Object initialData) {
      return new MeetingInfoModel();
   }
}
