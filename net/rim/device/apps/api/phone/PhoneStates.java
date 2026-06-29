package net.rim.device.apps.api.phone;

public interface PhoneStates {
   byte INVALID_STATE = -1;
   byte IDLE = 0;
   byte INCOMING = 1;
   byte SINGLE_ACTIVE = 2;
   byte SINGLE_HELD = 3;
   byte ACTIVE_WAITING = 4;
   byte HELD_WAITING = 5;
   byte CONNECTING = 6;
   byte HELD_CONNECTING = 7;
   byte DUAL = 8;
   byte IN_CALL_FAILURE = 9;
   byte DIRECT_CALL_ACTIVE = 10;
   byte DIRECT_ALERT_ACTIVE = 11;
}
