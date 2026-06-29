package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;

interface CallManager$Listener {
   int CALLS_EMPTY = 10;
   int CALLS_NON_EMPTY = 20;
   int CALLS_CHANGED = 30;
   int ANSWERED_NEW_INCOMING_CALL = 2;
   int CALL_FAILED = 4;
   int CALL_FAILED_REPLACED_BY_STK = 8;
   int CALL_DISCONNECTED = 16;
   int DROPPED_CONFERENCE_MEMBER = 32;

   void onCallManagerEvent(int var1, Vector var2, LiveCall var3, int var4, Object var5);
}
