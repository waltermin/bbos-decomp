package net.rim.device.apps.internal.blackberryemail.replywithouttextstub;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.internal.blackberryemail.email.CMIMEReferenceIdProvider;

final class ReplyWithoutTextStubModel implements PersistableRIMModel, ConversionProvider, CMIMEReferenceIdProvider {
   private int _referenceIdentifier;

   @Override
   public final int getCMIMEReferenceIdentifier() {
      return this._referenceIdentifier;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         return ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19);
      }

      RIMMessagingOutgoingMessage outgoingTransmission = (RIMMessagingOutgoingMessage)target;
      outgoingTransmission.setOriginalReferenceIdentifier(this._referenceIdentifier);
      return true;
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof ReplyWithoutTextStubModel)) {
         return false;
      }

      ReplyWithoutTextStubModel otherModel = (ReplyWithoutTextStubModel)object;
      return otherModel._referenceIdentifier == this._referenceIdentifier;
   }

   ReplyWithoutTextStubModel(Object initialData) {
      ContextObject contextObject = ContextObject.castOrCreate(initialData);
      this._referenceIdentifier = contextObject.getIntegerData(0);
   }
}
