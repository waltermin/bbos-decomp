package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import net.rim.device.apps.api.pim.TimeBasedObject;

public interface BareEvent extends TimeBasedObject {
   byte FB_FREE;
   byte FB_TENTATIVE;
   byte FB_BUSY;
   byte FB_OUT_OF_OFFICE;
   byte SENSITIVITY_NORMAL;
   byte SENSITIVITY_PERSONAL;
   byte SENSITIVITY_PRIVATE;
   byte SENSITIVITY_CONFIDENTIAL;
}
