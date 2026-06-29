package net.rim.device.apps.api.framework.model;

public interface VisibilityControl {
   byte NONE = 0;
   byte NOT_SENT = 1;
   byte PIN = 2;
   byte NOT_PIN = 4;
   byte NEW = 8;
   byte PHONE = 16;

   byte getVisibilityFlags();
}
