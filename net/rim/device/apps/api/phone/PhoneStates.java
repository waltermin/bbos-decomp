package net.rim.device.apps.api.phone;

public interface PhoneStates {
   byte INVALID_STATE;
   byte IDLE;
   byte INCOMING;
   byte SINGLE_ACTIVE;
   byte SINGLE_HELD;
   byte ACTIVE_WAITING;
   byte HELD_WAITING;
   byte CONNECTING;
   byte HELD_CONNECTING;
   byte DUAL;
   byte IN_CALL_FAILURE;
   byte DIRECT_CALL_ACTIVE;
   byte DIRECT_ALERT_ACTIVE;
}
