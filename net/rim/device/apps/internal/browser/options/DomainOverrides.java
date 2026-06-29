package net.rim.device.apps.internal.browser.options;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.util.DomainUtilities;

public final class DomainOverrides {
   private IntHashtable _domains = (IntHashtable)(new Object());
   private Hashtable _overrides = (Hashtable)(new Object());
   private static final long APP_REGISTRY_KEY = -1546612066057717803L;

   private DomainOverrides() {
   }

   public static final DomainOverrides getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      DomainOverrides instance = (DomainOverrides)ar.getOrWaitFor(-1546612066057717803L);
      if (instance == null) {
         instance = new DomainOverrides();
         ar.put(-1546612066057717803L, instance);
      }

      return instance;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void addOverrides(int id, BrowserConfigRecord browserConfig) {
      String configUID = browserConfig.getUid();
      int configType = browserConfig.getPropertyAsInt(12);
      String domainsString = browserConfig.getPropertyAsString(56);
      synchronized (this._domains) {
         DomainOverrides$Domains domains = (DomainOverrides$Domains)this._domains.get(id);
         if (domains != null) {
            if (domains._configType == configType
               && StringUtilities.strEqualIgnoreCase(domains._configUID, configUID, 1701707776)
               && StringUtilities.strEqual(domains._domainsString, domainsString)) {
               return;
            }

            if (domains._domainsString != null && domains._domainsString.length() != 0) {
               this.removeOverrides(domains._configUID);
            }
         }

         domains = new DomainOverrides$Domains(configUID, configType, domainsString);
         this._domains.put(id, domains);
         if (domainsString != null && domainsString.length() != 0) {
            StringTokenizer strings = (StringTokenizer)(new Object(domainsString, ','));

            while (strings.hasMoreTokens()) {
               String token = strings.nextToken();
               boolean var17 = false /* VF: Semaphore variable */;

               try {
                  var17 = true;
                  URI e = new Object(token);
                  String authority = DomainUtilities.parseAuthority((URI)e);
                  String path = DomainUtilities.parsePath((URI)e);
                  DomainOverrides$DomainOverride override = new DomainOverrides$DomainOverride(configUID, configType, authority, path);
                  this.addOverride(override);
                  var17 = false;
               } finally {
                  if (var17) {
                     EventLogger.logEvent(
                        1907089860548946979L, ((StringBuffer)(new Object("Invalid domain override: "))).append(token).toString().getBytes(), 0
                     );
                     continue;
                  }
               }
            }
         }
      }
   }

   private final void addOverride(DomainOverrides$DomainOverride override) {
      String authority = override._authority;
      synchronized (this._overrides) {
         DomainOverrides$DomainOverride[] overrides = (DomainOverrides$DomainOverride[])this._overrides.get(authority);
         if (overrides == null) {
            overrides = new DomainOverrides$DomainOverride[]{override};
            this._overrides.put(authority, overrides);
         } else {
            boolean found = false;

            for (int i = overrides.length - 1; i >= 0; i--) {
               if (overrides[i].equals(override)) {
                  overrides[i] = override;
                  found = true;
                  break;
               }
            }

            if (!found) {
               Arrays.add(overrides, override);
            }
         }
      }
   }

   public final void removeOverrides(int id) {
      synchronized (this._domains) {
         DomainOverrides$Domains domains = (DomainOverrides$Domains)this._domains.get(id);
         if (domains != null && domains._domainsString != null && domains._domainsString.length() != 0) {
            this.removeOverrides(domains._configUID);
         }
      }
   }

   private final void removeOverrides(String configUID) {
      synchronized (this._overrides) {
         Enumeration keys = this._overrides.keys();

         while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            DomainOverrides$DomainOverride[] overrides = (DomainOverrides$DomainOverride[])this._overrides.get(key);
            if (overrides != null) {
               for (int i = overrides.length - 1; i >= 0; i--) {
                  if (StringUtilities.strEqualIgnoreCase(overrides[i]._configUID, configUID, 1701707776)) {
                     Arrays.removeAt(overrides, i);
                  }
               }
            }

            if (overrides == null || overrides.length == 0) {
               this._overrides.remove(key);
            }
         }
      }
   }

   public final String getOverride(String url, int currentConfigType) {
      URI uri = null;

      try {
         uri = (URI)(new Object(url));
      } finally {
         ;
      }

      String configUID = null;
      String authority = DomainUtilities.parseAuthority(uri);
      String path = DomainUtilities.parsePath(uri);
      DomainOverrides$DomainOverride[] overrides = this.getMatchingOverrides(authority, path);
      if (overrides.length > 0) {
         configUID = overrides[0]._configUID;
      } else if (currentConfigType != 1 && authority.indexOf(46) == -1) {
         BrowserConfigRecord[] browserConfigs = BrowserConfigRecord.getValidBrowserConfigRecords();

         for (int i = 0; i < browserConfigs.length; i++) {
            BrowserConfigRecord browserConfig = browserConfigs[i];
            if (browserConfig.getPropertyAsInt(12) == 1) {
               configUID = browserConfig.getUid();
               break;
            }
         }
      }

      return configUID;
   }

   private final DomainOverrides$DomainOverride[] getMatchingOverrides(String authority, String path) {
      DomainOverrides$DomainOverride[] matchingOverrides = new DomainOverrides$DomainOverride[0];
      authority = StringUtilities.toLowerCase(authority, 1701707776);
      DomainOverrides$DomainOverride[] overrides = (DomainOverrides$DomainOverride[])this._overrides.get(authority);
      if (overrides != null) {
         this.addMatchingOverrides(overrides, matchingOverrides, authority, path);
      }

      for (int indexOfDot = authority.indexOf(46); indexOfDot >= 0; indexOfDot = authority.indexOf(46, indexOfDot + 1)) {
         overrides = (DomainOverrides$DomainOverride[])this._overrides.get(authority.substring(indexOfDot));
         if (overrides != null) {
            this.addMatchingOverrides(overrides, matchingOverrides, authority, path);
         }
      }

      if (matchingOverrides.length > 1) {
         Arrays.sort(matchingOverrides, new DomainOverrides$DomainOverrideComparator());
      }

      return matchingOverrides;
   }

   private final void addMatchingOverrides(
      DomainOverrides$DomainOverride[] overrides, DomainOverrides$DomainOverride[] matchingOverrides, String authority, String path
   ) {
      for (int i = overrides.length - 1; i >= 0; i--) {
         DomainOverrides$DomainOverride override = overrides[i];
         if (this.authorityMatch(authority, override._authority) && path.startsWith(override._path)) {
            Arrays.add(matchingOverrides, override);
         }
      }
   }

   private final boolean authorityMatch(String authorityA, String authorityB) {
      if (!authorityA.equals(authorityB) || !DomainUtilities.isFQDN(authorityA) && !DomainUtilities.isPartialDN(authorityA)) {
         if (!DomainUtilities.isFQDN(authorityA)) {
            return false;
         } else if (authorityB.charAt(0) != '.' || !authorityA.endsWith(authorityB)) {
            return false;
         } else {
            return !DomainUtilities.isFQDN(authorityB.substring(1)) ? false : !DomainUtilities.isIPAddress(authorityA);
         }
      } else {
         return true;
      }
   }
}
