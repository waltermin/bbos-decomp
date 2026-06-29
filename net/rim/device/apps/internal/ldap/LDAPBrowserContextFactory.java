package net.rim.device.apps.internal.ldap;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;

public final class LDAPBrowserContextFactory {
   private static final long ID = 5563981518107675018L;
   private static Hashtable _registeredContexts;
   private static final String RIBBON_ID_PREFIX = "net.rim.blackberry.ldapbrowser.";

   public static final void register(String name, LDAPBrowserContext context) {
      if (name == null || name.length() == 0 || context == null) {
         throw new IllegalArgumentException();
      }

      if (!_registeredContexts.containsKey(name)) {
         _registeredContexts.put(name, context);
      }
   }

   public static final void addLDAPBrowserEntryPoints(String name) {
      LDAPBrowserContext context = getContext(name);
      if (context != null) {
         VerbRepository.getVerbRepository(-3067780115376710723L).register(new LDAPFetchCertificatesVerb(name), context.getObjectTypesConstant());
         RibbonLauncher ribbonLauncher = RibbonLauncher.getInstance();
         if (ribbonLauncher != null) {
            ribbonLauncher.registerAction("net.rim.blackberry.ldapbrowser." + name, new ApplicationEntryPoint(context.getRibbonApplicationDescriptor()));
         }
      }
   }

   public static final void removeLDAPBrowserEntryPoints(String name) {
      LDAPBrowserContext context = getContext(name);
      if (context != null) {
         VerbRepository.getVerbRepository(-3067780115376710723L).deregister(new LDAPFetchCertificatesVerb(name), context.getObjectTypesConstant());
         RibbonLauncher ribbonLauncher = RibbonLauncher.getInstance();
         if (ribbonLauncher != null) {
            ribbonLauncher.unregisterAction("net.rim.blackberry.ldapbrowser." + name);
         }
      }
   }

   public static final LDAPBrowserContext getContext(String name) {
      if (name != null && name.length() != 0) {
         return (LDAPBrowserContext)_registeredContexts.get(name);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final boolean isAvailable(String name) {
      if (name == null || name.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         return _registeredContexts.get(name) != null;
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      _registeredContexts = registry.getHashtable(5563981518107675018L);
   }
}
