package net.rim.blackberry.api.phone.phonelogs;

import java.util.Date;
import java.util.Vector;
import net.rim.device.apps.internal.commonmodels.body.BodyModelImpl;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;

public class CallLog {
   protected Date _date;
   protected int _callType;
   protected int _callDuration;
   protected int _callStatus;
   protected Vector _participants;
   protected String _notes;
   protected PhoneCallModelImpl _callmodel;
   private PhoneLogListenerManager _listenerManager;
   public static final int STATUS_NORMAL;
   public static final int STATUS_BUSY;
   public static final int STATUS_CONGESTION;
   public static final int STATUS_PATH_UNAVAILABLE;
   public static final int STATUS_NUMBER_UNOBTAINABLE;
   public static final int STATUS_AUTHENTICATION_FAILURE;
   public static final int STATUS_EMERGENCY_CALLS_ONLY;
   public static final int STATUS_HOLD_ERROR;
   public static final int STATUS_OUTGOING_CALLS_BARRED;
   public static final int STATUS_GENERAL_ERROR;
   public static final int STATUS_MAINTENANCE_REQUIRED;
   public static final int STATUS_SERVICE_NOT_AVAILABLE;
   public static final int STATUS_CALL_FAIL_DUE_TO_FADING;
   public static final int STATUS_CALL_LOST_DUE_TO_FADING;
   public static final int STATUS_CALL_FAILED_TRY_AGAIN;
   public static final int STATUS_FDN_MISMATCH;
   public static final int STATUS_CONNECTION_DENIED;
   public static final int STATUS_INCOMING_CALL_BARRED;

   CallLog(Date date, int callType, int callDuration, int callStatus, Vector participants, String notes) {
      this._date = date;
      this._callType = callType;
      this._callDuration = callDuration;
      this._callStatus = callStatus;
      this._participants = participants;
      this._notes = notes;
   }

   CallLog(PhoneCallModelImpl call) {
      this._callmodel = call;
      this._participants = (Vector)(new Object());
   }

   public Date getDate() {
      return (Date)(this._callmodel != null ? new Object(this._callmodel.getTimeStamp()) : this._date);
   }

   public int getDuration() {
      return this._callmodel != null ? this._callmodel.getElapsedTime() : this._callDuration;
   }

   public int getStatus() {
      return this._callmodel != null ? this._callmodel.getErrorCode() : this._callStatus;
   }

   public String getNotes() {
      return this._callmodel != null ? ((BodyModelImpl)this._callmodel.getAt(2)).getText() : this._notes;
   }

   public void setDate(Date date) {
      this._date = date;
      if (this._callmodel != null) {
         this._callmodel.setTimeStamp(this._date.getTime());
         this.invokeListeners();
      }
   }

   public void setDuration(int callDuration) {
      this._callDuration = callDuration;
      if (this._callmodel != null) {
         this._callmodel.setElapsedTime(callDuration);
         this.invokeListeners();
      }
   }

   public void setStatus(int callStatus) {
      this._callStatus = callStatus;
      if (this._callmodel != null) {
         this._callmodel.setErrorCode(callStatus);
         this.invokeListeners();
      }
   }

   public void setNotes(String notes) {
      this._notes = notes;
      if (this._callmodel != null) {
         BodyModelImpl bmi = (BodyModelImpl)this._callmodel.getAt(2);
         bmi.setText(notes);
         this.invokeListeners();
      }
   }

   public void setLineId(int lineId) {
      if (this._callmodel != null) {
         this._callmodel.setLineId(lineId);
      }
   }

   public int getLineId() {
      return this._callmodel != null ? this._callmodel.getLineId() : 1;
   }

   void invokeListeners() {
      if (this._listenerManager == null) {
         this._listenerManager = PhoneLogListenerManager.getInstance();
      }

      this._listenerManager.dispatch(1, this, null);
   }
}
