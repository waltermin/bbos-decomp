package net.rim.device.apps.internal.ldap;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

class LDAPBrowserOptionScreen$LDAPBrowserVerb extends Verb {
   private int _type;
   private final LDAPBrowserOptionScreen this$0;
   static final int SAVE;
   static final int CLOSE;

   public LDAPBrowserOptionScreen$LDAPBrowserVerb(LDAPBrowserOptionScreen _1, int type, int ordering, ResourceBundleFamily rb, int rbKey) {
      super(ordering, rb, rbKey);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public Object invoke(Object parameter) {
      switch (this._type) {
         case 1:
         default:
            this.this$0.doSave();
            return null;
         case 2:
            if (this.this$0.hasChanged()) {
               this.this$0.promptForSave();
               return null;
            } else {
               this.this$0.doClose();
            }
         case 0:
            return null;
      }
   }
}
