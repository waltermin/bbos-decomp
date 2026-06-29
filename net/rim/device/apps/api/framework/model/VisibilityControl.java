package net.rim.device.apps.api.framework.model;

public interface VisibilityControl {
   byte NONE;
   byte NOT_SENT;
   byte PIN;
   byte NOT_PIN;
   byte NEW;
   byte PHONE;

   byte getVisibilityFlags();
}
