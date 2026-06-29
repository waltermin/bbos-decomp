package net.rim.device.apps.internal.ldap;

class LDAPBrowser$1 implements Runnable {
   private final LDAPBrowser this$0;

   LDAPBrowser$1(LDAPBrowser _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.invokeSearch();
   }
}
