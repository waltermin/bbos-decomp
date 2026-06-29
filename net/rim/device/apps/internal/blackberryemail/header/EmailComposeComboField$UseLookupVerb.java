package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.lookup.Request;

class EmailComposeComboField$UseLookupVerb extends Verb {
   private Request _myRequest;
   private final EmailComposeComboField this$0;

   public EmailComposeComboField$UseLookupVerb(EmailComposeComboField _1, Request request) {
      super(413952);
      this.this$0 = _1;
      this._myRequest = request;
   }

   @Override
   public Object invoke(Object parameter) {
      this.this$0.useLookupModel(this._myRequest);
      return null;
   }

   @Override
   public String toString() {
      return this._myRequest.toString();
   }

   @Override
   public String toString(Object context) {
      return this.toString();
   }
}
