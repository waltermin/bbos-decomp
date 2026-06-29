package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public final class EmailHeaderAddVerb extends Verb {
   private int _typeOfHeader;

   EmailHeaderAddVerb(int typeOfHeader) {
      super(0);
      this._typeOfHeader = typeOfHeader;
      super._ordering = HeaderTypes.getMenuOrdering(this._typeOfHeader);
   }

   public final int getHeaderType() {
      return this._typeOfHeader;
   }

   @Override
   public final String toString() {
      return EmailResources.getString(25) + HeaderTypes.getStringForHeaderType(this._typeOfHeader);
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject creationContext = ContextObject.clone(context);
      creationContext.put(254, EmailHeaderModel.createBlankFreeFormAddress(context));
      creationContext.remove(-4055106280780392421L);
      creationContext.put(-4054673099568009991L, HeaderTypes._typesAsInteger[this._typeOfHeader]);
      EmailHeaderModel header = EmailHeaderModelFactory.createEmailHeaderModel(creationContext);
      return header.getInsideModel() == null ? null : header;
   }
}
