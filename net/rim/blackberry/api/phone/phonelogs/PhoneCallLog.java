package net.rim.blackberry.api.phone.phonelogs;

import java.util.Date;
import java.util.Vector;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;

public final class PhoneCallLog extends CallLog {
   public static final int TYPE_RECEIVED_CALL;
   public static final int TYPE_PLACED_CALL;
   public static final int TYPE_MISSED_CALL_UNOPENED;
   public static final int TYPE_MISSED_CALL_OPENED;

   public PhoneCallLog(Date date, int callType, int callDuration, int callStatus, PhoneCallLogID participant, String notes) {
      super(date, callType, callDuration, callStatus, (Vector)(new Object()), notes);
      super._participants.addElement(participant);
   }

   PhoneCallLog(PhoneCallModelImpl call, PhoneCallLogID participant) {
      super(call);
      super._participants.addElement(participant);
   }

   public final PhoneCallLogID getParticipant() {
      return (PhoneCallLogID)super._participants.elementAt(0);
   }

   public final void setType(int callType) {
      super._callType = callType;
      if (super._callmodel != null) {
         super._callmodel.setType((byte)callType);
         this.invokeListeners();
      }
   }

   public final int getType() {
      return super._callmodel != null ? super._callmodel.getType() : super._callType;
   }

   public final void setParticipant(PhoneCallLogID participant) {
      super._participants.setElementAt(participant, 0);
   }
}
