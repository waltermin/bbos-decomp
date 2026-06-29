package net.rim.device.apps.internal.ldap;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.verb.Verb;

public final class LDAPFetchCertificatesVerb extends Verb {
   private String _description;
   private String _ldapBrowserContextString;
   private String _emailAddress;
   private MatchProvider _matchProvider;
   private boolean _autoFetch;
   private LDAPBrowserContext _ldapBrowserContext;

   public LDAPFetchCertificatesVerb(String ldapBrowserContextString) {
      this(null, ldapBrowserContextString, null, null, false);
   }

   public LDAPFetchCertificatesVerb(String description, String ldapBrowserContextString, String emailAddress, MatchProvider matchProvider, boolean autoFetch) {
      super(1200256);
      this._ldapBrowserContextString = ldapBrowserContextString;
      this._ldapBrowserContext = LDAPBrowserContextFactory.getContext(this._ldapBrowserContextString);
      if (this._ldapBrowserContext == null) {
         throw new Object();
      }

      this._description = description;
      this._emailAddress = emailAddress;
      this._matchProvider = matchProvider;
      this._autoFetch = autoFetch;
   }

   @Override
   public final String toString() {
      return this._description != null ? this._description : this._ldapBrowserContext.getFetchStringPlural();
   }

   @Override
   public final Object invoke(Object parameter) {
      LDAPBrowser ldapBrowser = new LDAPBrowser(this._ldapBrowserContextString, this._matchProvider, null, null, this._emailAddress);
      ldapBrowser.open(this._autoFetch);
      return null;
   }

   @Override
   public final int hashCode() {
      return this._ldapBrowserContextString.hashCode();
   }

   @Override
   public final boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (!(o instanceof LDAPFetchCertificatesVerb)) {
         return false;
      }

      LDAPFetchCertificatesVerb other = (LDAPFetchCertificatesVerb)o;
      return StringUtilities.strEqual(other._ldapBrowserContextString, this._ldapBrowserContextString);
   }
}
