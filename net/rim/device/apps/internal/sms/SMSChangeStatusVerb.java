package net.rim.device.apps.internal.sms;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.sms.resources.SMSResources;

public final class SMSChangeStatusVerb extends Verb {
   private SMSModel _message;
   private int _flagsToSet;
   private int _flagsToClear;
   private int _newStatus;
   private Object _context;

   public SMSChangeStatusVerb(
      int menuOrdering, int resourceIdWithingSMSResources, int flagsToSet, int flagsToClear, int newStatus, SMSModel message, Object contextObject
   ) {
      this(menuOrdering, resourceIdWithingSMSResources, flagsToSet, flagsToClear, newStatus, contextObject);
      this._message = message;
   }

   public SMSChangeStatusVerb(int menuOrdering, int resourceIdWithinSMSResources, int flagsToSet, int flagsToClear, int newStatus, Object contextObject) {
      super(menuOrdering, SMSResources.getResourceBundle(), resourceIdWithinSMSResources);
      this._flagsToSet = flagsToSet;
      this._flagsToClear = flagsToClear;
      this._newStatus = newStatus;
      this._context = contextObject;
   }

   public final void setParameters(SMSModel message) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object context) {
      this._message.changeStatus(this._flagsToSet, this._flagsToClear, this._newStatus, 0, true, true, true, this._context != null ? this._context : context);
      return null;
   }
}
