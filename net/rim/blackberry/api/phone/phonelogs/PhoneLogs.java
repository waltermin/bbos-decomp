package net.rim.blackberry.api.phone.phonelogs;

import java.util.Date;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;
import net.rim.device.apps.internal.phone.data.PhoneFolders;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.Array;

public final class PhoneLogs {
   public static final long FOLDER_MISSED_CALLS = 7042951934619290849L;
   public static final long FOLDER_NORMAL_CALLS = 5390902206192375236L;
   private static PhoneLogs _phoneLogs;
   private static PersistedSortedCollection _calls;
   private static PersistedSortedCollection _missedCalls;

   private static final void assertPermission() {
      ApplicationControl.assertPhonePermitted(true, CommonResource.getBundle(), 10095);
   }

   private PhoneLogs() {
      _calls = PhoneFolders.getContainedItems(5390902206192375236L);
      _missedCalls = PhoneFolders.getContainedItems(7042951934619290849L);
   }

   public static final PhoneLogs getInstance() {
      assertPermission();
      if (_phoneLogs == null) {
         _phoneLogs = new PhoneLogs();
      }

      return _phoneLogs;
   }

   public static final CallLog createCallLogFromInternalModel(Object model) {
      if (!(model instanceof Object)) {
         throw new Object("Incorrect internal model specified");
      }

      PhoneCallModelImpl call = (PhoneCallModelImpl)model;
      return createCallLog(call);
   }

   public final void addCall(CallLog call) {
      assertPermission();
      PhoneCallModelImpl newCall = this.createPhoneModel(call);
      MessageLookups.put(-7579072715623987642L, newCall.getRefId(), newCall);
      PhoneFolders.addItem(newCall);
   }

   public final void swapCall(CallLog call, int index, long folderID) {
      assertPermission();
      if (index >= 0 && index < this.numberOfCalls(folderID)) {
         PhoneCallModelImpl newCall = this.createPhoneModel(call);
         int type = newCall.getType();
         if (folderID == 5390902206192375236L) {
            if (type != 3 && type != 2) {
               PhoneFolders.replaceItem(_calls.getAt(index), newCall);
            } else {
               throw new Object("Cannot replace normal call with missed call");
            }
         } else {
            if (folderID != 7042951934619290849L) {
               throw new Object("Must specify valid folderID");
            }

            if (type != 3 && type != 2) {
               throw new Object("Cannot replace missed call with normal call");
            }

            PhoneFolders.replaceItem(_missedCalls.getAt(index), newCall);
         }
      } else {
         throw new Object(index);
      }
   }

   public final void deleteCall(int index, long folderID) {
      assertPermission();
      if (index < 0 || index >= this.numberOfCalls(folderID)) {
         throw new Object(index);
      }

      if (folderID == 5390902206192375236L) {
         PhoneFolders.removeItem(_calls.getAt(index));
      } else if (folderID == 7042951934619290849L) {
         PhoneFolders.removeItem(_missedCalls.getAt(index));
      } else {
         throw new Object("Must specify valid folderID");
      }
   }

   public final int numberOfCalls(long folderID) {
      assertPermission();
      if (folderID == 5390902206192375236L) {
         return _calls.size();
      } else if (folderID == 7042951934619290849L) {
         return _missedCalls.size();
      } else {
         throw new Object("Must specify valid folderID");
      }
   }

   public final CallLog callAt(int index, long folderID) {
      assertPermission();
      if (index >= 0 && index < this.numberOfCalls(folderID)) {
         PhoneCallModelImpl call;
         if (folderID == 5390902206192375236L) {
            call = PhoneFolders.getWritableCallLog((PhoneCallModelImpl)_calls.getAt(index));
         } else {
            if (folderID != 7042951934619290849L) {
               throw new Object("Invalid folderID");
            }

            call = PhoneFolders.getWritableCallLog((PhoneCallModelImpl)_missedCalls.getAt(index));
         }

         return createCallLog(call);
      } else {
         throw new Object(index);
      }
   }

   private final PhoneCallModelImpl createPhoneModel(CallLog call) {
      PhoneCallLogID participant = null;
      if (!(call instanceof PhoneCallLog)) {
         if (!(call instanceof ConferencePhoneCallLog)) {
            throw new Object();
         }

         participant = ((ConferencePhoneCallLog)call).getParticipantAt(0);
      } else {
         participant = ((PhoneCallLog)call).getParticipant();
      }

      Object connectionParameters = PhoneUtilities.getCallConnectionParameters(participant.getNumber(), null, null, null);
      CallerIDInfo callID = PhoneUtilities.createCallerIDInfo(participant.getNumber());
      PhoneCallInitialData phoneData = (PhoneCallInitialData)(new Object(1, (byte)1, 0, callID, connectionParameters));
      PhoneCallModelImpl newCall = (PhoneCallModelImpl)PhoneUtilities.createPhoneCallModel(phoneData);
      if (newCall == null) {
         throw new Object();
      }

      if (newCall == null) {
         throw new Object();
      }

      newCall.setElapsedTime(call.getDuration());
      newCall.setErrorCode(call.getStatus());
      Date callDate = call.getDate();
      newCall.setTimeStamp(callDate.getTime());
      newCall.setLineId(call.getLineId());
      if (!(call instanceof ConferencePhoneCallLog)) {
         PhoneCallLog callNotes = (PhoneCallLog)call;
         newCall.setType((byte)callNotes.getType());
      } else {
         ConferencePhoneCallLog confCall = (ConferencePhoneCallLog)call;
         int size = confCall.numberOfParticipants();

         for (int i = 1; i < size; i++) {
            PhoneCallLogID caller = confCall.getParticipantAt(i);
            CallerIDInfo callerID = PhoneUtilities.createCallerIDInfo(caller.getNumber());
            newCall.addCallerIDInfo(callerID);
         }

         newCall.setType((byte)4);
      }

      BodyModel callNotes = (BodyModel)newCall.getAt(2);
      callNotes.setText(call.getNotes());
      return newCall;
   }

   private static final CallerIDInfo[] getCallerIDsFromCallLog(PhoneCallModelImpl call) {
      int submemberCount = call.size();
      CallerIDInfo[] callerIDs = new Object[submemberCount];
      int callerIDCount = 0;

      for (int i = 0; i < submemberCount; i++) {
         Object o = call.getAt(i);
         if (o instanceof Object) {
            callerIDs[callerIDCount++] = (CallerIDInfo)o;
         }
      }

      Array.resize(callerIDs, callerIDCount);
      return callerIDs;
   }

   private static final CallLog createCallLog(PhoneCallModelImpl call) {
      CallerIDInfo[] callerIDs = getCallerIDsFromCallLog(call);
      if (callerIDs.length == 1) {
         CallerIDInfo callID = callerIDs[0];
         PhoneCallLogID participant = new PhoneCallLogID(callID);
         return new PhoneCallLog(call, participant);
      }

      CallerIDInfo callID1 = callerIDs[0];
      CallerIDInfo callID2 = callerIDs[1];
      PhoneCallLogID participant1 = new PhoneCallLogID(callID1);
      PhoneCallLogID participant2 = new PhoneCallLogID(callID2);
      ConferencePhoneCallLog callResult = new ConferencePhoneCallLog(call, participant1, participant2);

      for (int i = 2; i < callerIDs.length; i++) {
         CallerIDInfo callID = callerIDs[i];
         PhoneCallLogID participant = new PhoneCallLogID(callID);
         callResult.addParticipant(participant);
      }

      return callResult;
   }

   public static final void addListener(PhoneLogListener listener) {
      if (listener == null) {
         throw new Object();
      }

      assertPermission();
      PhoneLogListenerManager pllm = PhoneLogListenerManager.getInstance();
      pllm.register(listener);
   }

   public static final void removeListener(PhoneLogListener listener) {
      PhoneLogListenerManager pllm = PhoneLogListenerManager.getInstance();
      pllm.deregister(listener);
   }
}
