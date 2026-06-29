package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;

interface CallManager$Listener {
   int CALLS_EMPTY;
   int CALLS_NON_EMPTY;
   int CALLS_CHANGED;
   int ANSWERED_NEW_INCOMING_CALL;
   int CALL_FAILED;
   int CALL_FAILED_REPLACED_BY_STK;
   int CALL_DISCONNECTED;
   int DROPPED_CONFERENCE_MEMBER;

   void onCallManagerEvent(int var1, Vector var2, LiveCall var3, int var4, Object var5);
}
