package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailChangeStatusVerb extends Verb {
   private EmailMessageModelImpl _message;
   private int _flagsToSet;
   private int _flagsToClear;
   private int _newStatus;

   EmailChangeStatusVerb(int menuOrdering, int resourceIdWithinEmailResources, int flagsToSet, int flagsToClear, int newStatus, EmailMessageModelImpl message) {
      this(menuOrdering, resourceIdWithinEmailResources, flagsToSet, flagsToClear, newStatus);
      this._message = message;
   }

   EmailChangeStatusVerb(int menuOrdering, int resourceIdWithinEmailResources, int flagsToSet, int flagsToClear, int newStatus) {
      super(menuOrdering, EmailResources.getResourceBundle(), resourceIdWithinEmailResources);
      this._flagsToSet = flagsToSet;
      this._flagsToClear = flagsToClear;
      this._newStatus = newStatus;
   }

   final void setParameters(EmailMessageModelImpl message) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object context) {
      this._message.changeStatus(this._flagsToSet, this._flagsToClear, this._newStatus, 0, true, true, true, false, context);
      return null;
   }
}
