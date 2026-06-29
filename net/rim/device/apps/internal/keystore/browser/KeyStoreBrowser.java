package net.rim.device.apps.internal.keystore.browser;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

public final class KeyStoreBrowser implements OptionsProviderRegistration$OptionsProvider {
   private Hashtable _registeredContexts = new Hashtable();
   private static final long OPTIONS_KEY_STORE_DISPLAY = -4706105373277579899L;

   public final void register(String name, KeyStoreBrowserContext context) {
      this.verifyName(name);
      if (context == null) {
         throw new IllegalArgumentException();
      }

      if (!this._registeredContexts.containsKey(name)) {
         this._registeredContexts.put(name, context);
      }
   }

   public final KeyStoreBrowserContext getContext(String name) {
      this.verifyName(name);
      return (KeyStoreBrowserContext)this._registeredContexts.get(name);
   }

   public final void show(String browserContext, Object displayContext) {
      KeyStoreBrowserContext context = this.getContext(browserContext);
      if (context == null) {
         throw new IllegalArgumentException();
      }

      KeyStoreBrowserOptionsItem item = new KeyStoreBrowserOptionsItem(context, displayContext);
      item.openModal();
   }

   @Override
   public final Vector getOptionsItems() {
      Vector items = new Vector();
      Enumeration enumeration = this._registeredContexts.elements();

      while (enumeration.hasMoreElements()) {
         KeyStoreBrowserContext context = (KeyStoreBrowserContext)enumeration.nextElement();
         items.addElement(new KeyStoreBrowserOptionsItem(context, null));
      }

      return items;
   }

   private final void verifyName(String name) {
      if (name == null || name.length() <= 0) {
         throw new IllegalArgumentException();
      }
   }

   public static final KeyStoreBrowser getInstance() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      KeyStoreBrowser keyStoreBrowser = (KeyStoreBrowser)appRegistry.getOrWaitFor(-4706105373277579899L);
      if (keyStoreBrowser == null) {
         keyStoreBrowser = new KeyStoreBrowser();
         appRegistry.put(-4706105373277579899L, keyStoreBrowser);
      }

      return keyStoreBrowser;
   }

   private KeyStoreBrowser() {
      OptionsProviderRegistration.registerOptionsProvider(this);
   }
}
