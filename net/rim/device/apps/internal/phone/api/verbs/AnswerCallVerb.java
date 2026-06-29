package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.api.IncomingCallConnector;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class AnswerCallVerb extends Verb {
   private int _callId;
   private RIMModel _callerIDInfo;
   private int _answeringOption;
   private Object _connectionParameters;

   public AnswerCallVerb(int callId, RIMModel callerIDInfo, int answeringOption, int descriptionStringId, Object context) {
      super(0, PhoneResources.getResourceBundle(), descriptionStringId);
      this._callId = callId;
      this._callerIDInfo = callerIDInfo;
      this._answeringOption = answeringOption;
      this._connectionParameters = PhoneUtilities.getCallAnsweringParameters(this._callId, this._callerIDInfo, context);
   }

   public final int getAnsweringOption() {
      return this._answeringOption;
   }

   @Override
   public final Object invoke(Object context) {
      switch (this._answeringOption) {
         case 1:
            IncomingCallConnector.connect(this._callId, this._answeringOption, this._connectionParameters);
            return null;
         case 2:
            new AnswerDropCurrentCall(this._callId, this._connectionParameters).answer();
            return null;
         case 3:
         default:
            new AnswerDropAllCalls(this._callId, this._connectionParameters).answer();
            return null;
      }
   }
}
