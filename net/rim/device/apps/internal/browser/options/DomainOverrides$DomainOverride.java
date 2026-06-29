package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.util.DomainUtilities;

final class DomainOverrides$DomainOverride {
   private String _configUID;
   private int _configType;
   private String _authority;
   private String _path;

   public DomainOverrides$DomainOverride(String configUID, int configType, String authority, String path) throws Exception {
      String domainName = authority;
      if (authority.startsWith(".")) {
         domainName = authority.substring(1);
      }

      if (domainName.length() != 0 && (DomainUtilities.isFQDN(domainName) || DomainUtilities.isPartialDN(domainName))) {
         this._configUID = configUID;
         this._configType = configType;
         this._authority = StringUtilities.toLowerCase(authority, 1701707776);
         this._path = path;
      } else {
         throw new Exception();
      }
   }

   @Override
   public final boolean equals(Object object) {
      if (!(object instanceof DomainOverrides$DomainOverride)) {
         return false;
      }

      DomainOverrides$DomainOverride override = (DomainOverrides$DomainOverride)object;
      return override._configType == this._configType
         && StringUtilities.strEqualIgnoreCase(override._configUID, this._configUID, 1701707776)
         && override._authority.equals(this._authority)
         && override._path.equals(this._path);
   }
}
