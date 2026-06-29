package net.rim.device.apps.internal.phone.api.livecall;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.model.PhoneNumberConverter;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class TransferCallVerb extends Verb {
   private int _action;
   private LiveCall _call;
   private int _callId;

   TransferCallVerb(LiveCall call, int action) {
      super(getOrdering(action));
      this._action = action;
      this._call = call;
      this._callId = call.getCallId();
   }

   @Override
   public final Object invoke(Object parameter) {
      String number = null;
      if (this._action == 0) {
         number = this.promptForNumber();
         this._call.setTransferNumber(number);
      }

      Phone.getInstance().invokeCallTransferAction(this._callId, this._action, number);
      return null;
   }

   @Override
   public final String toString() {
      switch (this._action) {
         case -1:
            return null;
         case 0:
         default:
            return PhoneResources.getString(6126);
         case 1:
            return PhoneResources.getString(408);
         case 2:
            return PhoneResources.getString(427);
         case 3:
            return PhoneResources.getString(6310);
         case 4:
            return PhoneResources.getString(6311);
      }
   }

   private static final int getOrdering(int action) {
      switch (action) {
         case -1:
            return 0;
         case 0:
         case 3:
            return 70917;
         case 1:
            return 70915;
         case 2:
         default:
            return 70914;
         case 4:
            return 70916;
      }
   }

   private final String promptForNumber() {
      ContextObject selectionContext = PhoneUtilities.selectFromAddressBook(null, null, null, null, false, false);
      if (selectionContext != null) {
         Object number = selectionContext.get(247);
         Object address = selectionContext.get(252);
         Object connectionParams = PhoneUtilities.getCallConnectionParameters(number, address, null, null);
         if (connectionParams != null) {
            String phoneNumber = (String)ContextObject.get(connectionParams, 6486659828352467672L);
            boolean smartDialing = address != null;
            boolean canDirectDialExtensions = Phone.getInstance().supportsCorporateExtensions(this._callId);
            StringBuffer output = new StringBuffer();
            StringBuffer dtmf = new StringBuffer();
            PhoneNumberConverter.convertForTransmission(
               output, dtmf, phoneNumber.toCharArray(), smartDialing, false, false, false, false, canDirectDialExtensions
            );
            return output.toString();
         }
      }

      return null;
   }
}
