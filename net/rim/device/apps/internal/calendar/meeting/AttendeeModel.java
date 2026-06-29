package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.apps.api.utility.general.Copyable;

public final class AttendeeModel extends AddressReference implements Attendee, PersistableRIMModel, Copyable, VerbProvider {
   int _attendeeType;

   AttendeeModel(Object initialData) {
      if (initialData instanceof Object) {
         this.setAddress(initialData);
      }
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof Object)) {
         return false;
      }

      Attendee attendee = (Attendee)other;
      Object attendeeAddress = attendee.getAddress();
      Object myAddress = this.getAddress();
      return myAddress == attendeeAddress || myAddress != null && super._modelData.equals(attendeeAddress);
   }

   @Override
   public final int getType() {
      return this._attendeeType;
   }

   @Override
   public final void setType(int newType) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object getAddress() {
      return this.getInsideModel();
   }

   @Override
   public final void setAddress(Object address) {
      this.setInsideModel((PersistableRIMModel)address, null);
   }

   @Override
   public final Object copy() {
      AttendeeModel thisModel = this;
      if (ObjectGroup.isInGroup(thisModel)) {
         thisModel = (AttendeeModel)ObjectGroup.expandGroup(thisModel);
      }

      Object address = thisModel.getInsideModel();
      if (address instanceof Object) {
         address = ((Copyable)address).copy();
      }

      AttendeeModel theCopy = new AttendeeModel(address);
      theCopy._attendeeType = thisModel._attendeeType;
      return theCopy;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb[] tempVerbs = new Object[0];
      ContextObject localContext = ContextObject.clone(context);
      PersistableRIMModel address = (PersistableRIMModel)this.getAddress();
      if (address instanceof Object) {
         VerbProvider provider = (VerbProvider)address;
         provider.getVerbs(localContext, tempVerbs);
         localContext.setPrivateFlag(-337556985625701066L, 0);
         Arrays.append(verbs, this.getWrappedVerbs(tempVerbs, localContext));
      }

      return null;
   }

   private final WrapperVerb[] getWrappedVerbs(Verb[] verbs, Object context) {
      WrapperVerb[] wrappedVerbs = new Object[verbs.length];

      for (int i = 0; i < verbs.length; i++) {
         wrappedVerbs[i] = (WrapperVerb)(new Object(verbs[i], context, verbs[i].getOrdering()));
      }

      return wrappedVerbs;
   }
}
