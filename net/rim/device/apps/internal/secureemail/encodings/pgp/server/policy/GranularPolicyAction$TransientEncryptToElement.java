package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

final class GranularPolicyAction$TransientEncryptToElement extends GranularPolicyElement {
   public boolean _recipientKey;
   public boolean _recipientKey_requireTrusted;
   public boolean _recipientKey_requireEndToEnd;
   public boolean _senderKey;
   public String[] _otherKey_data_value;

   public GranularPolicyAction$TransientEncryptToElement(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public final GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      if (StringUtilities.strEqualIgnoreCase(localName, "recipient-key")) {
         this._recipientKey = true;
         return new GranularPolicyAction$TransientRecipientKeyElement(localName, this);
      } else if (StringUtilities.strEqualIgnoreCase(localName, "sender-key")) {
         this._senderKey = true;
         return this;
      } else {
         return StringUtilities.strEqualIgnoreCase(localName, "other-key")
            ? new GranularPolicyAction$TransientOtherKeyElement(localName, this)
            : super.handleStartElement(localName, attributes);
      }
   }

   @Override
   public final void addChildElement(GranularPolicyElement childElement) {
      if (!(childElement instanceof GranularPolicyAction$TransientRecipientKeyElement)) {
         if (!(childElement instanceof GranularPolicyAction$TransientOtherKeyElement)) {
            super.addChildElement(childElement);
         } else {
            GranularPolicyAction$TransientOtherKeyElement transientOtherKeyElement = (GranularPolicyAction$TransientOtherKeyElement)childElement;
            if (this._otherKey_data_value == null) {
               this._otherKey_data_value = new Object[0];
            }

            Arrays.add(this._otherKey_data_value, transientOtherKeyElement._data_value);
         }
      } else {
         GranularPolicyAction$TransientRecipientKeyElement transientRecipientKeyElement = (GranularPolicyAction$TransientRecipientKeyElement)childElement;
         this._recipientKey_requireTrusted = transientRecipientKeyElement._requireTrusted;
         this._recipientKey_requireEndToEnd = transientRecipientKeyElement._requireEndToEnd;
      }
   }
}
