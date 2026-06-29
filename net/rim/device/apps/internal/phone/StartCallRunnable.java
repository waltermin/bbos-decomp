package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.OutgoingCallConnector;

final class StartCallRunnable implements Runnable {
   private String _number;
   private Object _addressCard;
   private String _friendlyName;
   private boolean _smartDial;

   StartCallRunnable(String number, Object addressCard, String friendlyName) {
      this(number, addressCard, friendlyName, false);
   }

   StartCallRunnable(String number, Object addressCard, String friendlyName, boolean smartDial) {
      this._number = number;
      this._addressCard = addressCard;
      this._friendlyName = friendlyName;
      this._smartDial = smartDial;
   }

   @Override
   public final void run() {
      ContextObject context = ContextObject.castOrCreate(null);
      if (this._smartDial) {
         context.setFlag(117);
      }

      Object connectionParams = PhoneUtilities.getCallConnectionParameters(this._number, this._addressCard, this._friendlyName, context);
      OutgoingCallConnector.startCall(connectionParams);
   }
}
