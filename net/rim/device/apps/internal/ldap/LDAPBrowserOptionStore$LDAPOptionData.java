package net.rim.device.apps.internal.ldap;

import net.rim.device.api.util.Persistable;

final class LDAPBrowserOptionStore$LDAPOptionData implements Persistable {
   private int _fetchCertStatus;
   private boolean _promptForCertLabel;
   private final LDAPBrowserOptionStore this$0;

   LDAPBrowserOptionStore$LDAPOptionData(LDAPBrowserOptionStore _1) {
      this.this$0 = _1;
      this.resetOptions();
   }

   public final void resetOptions() {
      this._fetchCertStatus = 2;
      this._promptForCertLabel = true;
   }
}
