package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import net.rim.device.apps.api.pim.TimeBasedObject;

public interface BareEvent extends TimeBasedObject {
   byte FB_FREE = 0;
   byte FB_TENTATIVE = 1;
   byte FB_BUSY = 2;
   byte FB_OUT_OF_OFFICE = 3;
   byte SENSITIVITY_NORMAL = 0;
   byte SENSITIVITY_PERSONAL = 1;
   byte SENSITIVITY_PRIVATE = 2;
   byte SENSITIVITY_CONFIDENTIAL = 3;
}
