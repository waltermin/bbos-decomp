package net.rim.wica.runtime.provisioning.internal;

import net.rim.wica.runtime.provisioning.internal.elements.AbstractElement;

public class ProvisioningEncodingException extends RuntimeException {
   private AbstractElement _encodingErrorElement;

   public ProvisioningEncodingException() {
   }

   public ProvisioningEncodingException(AbstractElement arg0) {
      this._encodingErrorElement = arg0;
   }

   public AbstractElement getErrorElement() {
      return this._encodingErrorElement;
   }

   @Override
   public String getMessage() {
      return this._encodingErrorElement != null ? this._encodingErrorElement.toString() : "Undefined element";
   }
}
