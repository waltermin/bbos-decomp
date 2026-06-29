package net.rim.blackberry.api.phone.phonelogs;

import java.util.Date;
import java.util.Vector;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;

public final class ConferencePhoneCallLog extends CallLog {
   private static final int CONFERENCE_CALL = 4;

   public ConferencePhoneCallLog(Date date, int callDuration, int callStatus, PhoneCallLogID caller1, PhoneCallLogID caller2, String notes) {
      super(date, 4, callDuration, callStatus, (Vector)(new Object()), notes);
      super._participants.addElement(caller1);
      super._participants.addElement(caller2);
   }

   ConferencePhoneCallLog(PhoneCallModelImpl call, PhoneCallLogID caller1, PhoneCallLogID caller2) {
      super(call);
      super._participants.addElement(caller1);
      super._participants.addElement(caller2);
   }

   public final PhoneCallLogID getParticipantAt(int index) {
      PhoneCallLogID participant = null;
      if (index >= 0 && index < super._participants.size()) {
         return (PhoneCallLogID)super._participants.elementAt(index);
      } else {
         throw new Object(index);
      }
   }

   public final void setParticipantAt(int index, PhoneCallLogID participant) {
      if (index >= 0 && index < super._participants.size()) {
         super._participants.setElementAt(participant, index);
      } else {
         throw new Object(index);
      }
   }

   public final void addParticipant(PhoneCallLogID participant) {
      super._participants.addElement(participant);
   }

   public final void removeParticipantAt(int index) {
      if (super._participants.size() > 2) {
         if (index >= 0 && index < super._participants.size()) {
            super._participants.removeElementAt(index);
         } else {
            throw new Object(index);
         }
      }
   }

   public final int numberOfParticipants() {
      return super._participants.size();
   }
}
