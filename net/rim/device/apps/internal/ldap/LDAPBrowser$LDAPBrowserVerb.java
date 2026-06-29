package net.rim.device.apps.internal.ldap;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

class LDAPBrowser$LDAPBrowserVerb extends Verb {
   private int _type;
   private boolean _multiple;
   private final LDAPBrowser this$0;
   static final int SEARCH;
   static final int CERTVIEW;
   static final int CERTADD;
   static final int FETCHROOT;
   static final int NEW_SERVER;
   static final int VIEW_SERVER;
   static final int CERT_STATUS;
   static final int CROSS_CERT;
   static final int OPTIONS;
   static final int CLOSE;

   LDAPBrowser$LDAPBrowserVerb(LDAPBrowser _1, int type, int ordering) {
      super(ordering);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public Object invoke(Object parameter) {
      switch (this._type) {
         case 1:
         default:
            this.this$0.doSearch();
            return null;
         case 2:
            this.this$0.doCertView();
            return null;
         case 3:
            this.this$0.doCertAdd();
            return null;
         case 4:
            this.this$0.doFetchRoot();
            return null;
         case 5:
            this.this$0.doNewServer();
            return null;
         case 6:
            this.this$0.doViewServer();
            return null;
         case 7:
            this.this$0.doCertStatus();
            return null;
         case 8:
            this.this$0.doCrossCerts();
            return null;
         case 9:
            this.this$0.doShowOptions();
            return null;
         case 10:
            this.this$0.doClose();
         case 0:
            return null;
      }
   }

   @Override
   public String toString() {
      switch (this._type) {
         case 0:
            throw new Object();
         case 1:
         default:
            return LDAPBrowser.getString(1);
         case 2:
            return this.this$0._context.getMenuViewCertString();
         case 3:
            if (this._multiple) {
               return this.this$0._context.getMenuAddCertStringPlural();
            }

            return this.this$0._context.getMenuAddCertString();
         case 4:
            return this.this$0._context.getMenuFetchRootString();
         case 5:
            return LDAPBrowser.getString(105);
         case 6:
            return LDAPBrowser.getString(7);
         case 7:
            return this.this$0._context.getMenuFetchStatusString();
         case 8:
            return LDAPBrowser.getString(102);
         case 9:
            return CommonResource.getString(20);
         case 10:
            return CommonResource.getString(9);
      }
   }

   public void setMultiple(boolean multiple) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
