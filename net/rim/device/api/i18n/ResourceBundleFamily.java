package net.rim.device.api.i18n;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.i18n.ResourceBundleFetcher;
import net.rim.device.internal.util.StringUtilitiesInternal;
import net.rim.vm.TraceBack;

public class ResourceBundleFamily extends ResourceBundle {
   private final long _id;
   private final String _name;
   private CodeSigningKey _key;
   private String _module;
   private Hashtable _table = new Hashtable();
   private ResourceBundle _bundleFallback;
   private ResourceBundle _bundleSystem;
   private ResourceBundle _bundleApp;
   private Locale _localeSystem;
   private Locale _localeApp;
   private IntHashtable _cache = new ResourceBundleFamily$MyIntHashtable(this, 37);

   ResourceBundleFamily(long id, String name, CodeSigningKey key) {
      super(null);
      this._id = id;
      this._name = name;
      this._key = key;
      this.setFamily(this);
      this.setFallbackLocale();
   }

   private final void setFallbackLocale() {
      Locale localeFallback = Locale.get(1701729619);
      this._bundleFallback = this.getBundle(localeFallback);
   }

   private void checkLocale() {
      Locale localeSystem = Locale.getDefaultForSystem();
      if (this._localeSystem != localeSystem) {
         this._bundleSystem = this.getBundle(localeSystem);
         if (this._bundleSystem == null) {
            throw new MissingResourceException("Missing resource " + this._name);
         }

         this._localeSystem = localeSystem;
         this._cache.clear();
      }

      Locale localeApp = Locale.getDefault();
      if (this._localeApp != localeApp) {
         this._bundleApp = this.getBundle(localeApp);
         if (this._bundleApp == null) {
            throw new MissingResourceException("Missing resource " + this._name);
         }

         this._localeApp = localeApp;
         this._cache.clear();
      }
   }

   final void clearEntry(int key) {
      this._cache.remove(key);
   }

   public synchronized ResourceBundle getBundle(Locale locale) {
      if (locale == null) {
         return null;
      }

      ResourceBundle bundle = null;

      try {
         bundle = this.getInstance(locale);
      } catch (MissingResourceException var4) {
      }

      if (bundle == null) {
         Locale parent = locale.getParent();
         bundle = this.getBundle(parent);
      }

      return bundle;
   }

   @Override
   public long getId() {
      return this._id;
   }

   public String getName() {
      return this._name;
   }

   @Override
   protected Object handleGetObject(int key) {
      this.checkLocale();
      Object result = this._cache.get(key);
      if (result == null) {
         result = this._bundleApp.getObject(key, true);
         if (result == null) {
            result = this._bundleSystem.getObject(key, true);
            if (result == null) {
               result = this._bundleFallback.getObject(key, true);
            }
         }

         if (result != null) {
            this._cache.put(key, result);
         }
      }

      return result;
   }

   public boolean isEmpty() {
      boolean result = true;
      Enumeration enumeration = this._table.elements();

      while (enumeration.hasMoreElements()) {
         ResourceBundle bundle = (ResourceBundle)enumeration.nextElement();
         if (!(bundle instanceof EmptyResourceBundle)) {
            return false;
         }
      }

      return result;
   }

   private synchronized ResourceBundle getInstance(Locale locale) {
      ResourceBundle bundle = (ResourceBundle)this._table.get(locale);
      if (bundle == null) {
         StringBuffer scratch = StringUtilitiesInternal.getScratchBuffer();
         String name;
         synchronized (scratch) {
            scratch.append(this._name);
            scratch.append('£');
            scratch.append(locale);
            name = scratch.toString();
            scratch.setLength(0);
         }

         bundle = ResourceBundleFetcher.fetch(name + ".crb");
         if (bundle == null) {
            bundle = ResourceBundleFetcher.fetch(name);
            if (bundle != null && ((CompiledResourceBundle)bundle).getId() != this._id) {
               throw new IllegalStateException();
            }
         }

         if (bundle != null) {
            Locale localeParent = locale.getParent();
            if (localeParent != null) {
               ResourceBundle parent = this.getInstance(localeParent);
               bundle.setParent(parent);
            }

            if (this._key != null) {
               int module = CodeModuleManager.getModuleHandleForObject(bundle);
               if (!ControlledAccess.verifyCodeModuleSignature(module, this._key)) {
                  throw new MissingResourceException("Invalid signature on Resource.");
               }
            }

            this._table.put(locale, bundle);
         }

         if (bundle == null) {
            bundle = new EmptyResourceBundle(locale);
            this._table.put(locale, bundle);
         }
      }

      if (bundle instanceof EmptyResourceBundle) {
         bundle = null;
      }

      return bundle;
   }

   void onModuleLoad() {
      IntEnumeration keys = this._cache.keys();

      while (keys.hasMoreElements()) {
         int key = keys.nextElement();
         if (this._cache.get(key) instanceof EmptyResourceBundle) {
            this._cache.remove(key);
         }
      }

      this._localeSystem = null;
      this._localeApp = null;
      this.checkLocale();
   }

   public synchronized void put(Locale locale, ResourceBundle bundle) {
      if (this._key != null && !ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), this._key)) {
         throw new ControlledAccessException();
      }

      if (this._module == null) {
         this.setModule(ApplicationDescriptor.currentApplicationDescriptor().getModuleName());
      } else if (!ApplicationDescriptor.currentApplicationDescriptor().getModuleName().startsWith(this._module)) {
         throw new IllegalStateException();
      }

      Locale localeParent = locale.getParent();
      if (localeParent != null) {
         ResourceBundle parent = this.getInstance(localeParent);
         bundle.setParent(parent);
      }

      if (this._id == 8736789735327653723L) {
         Locale.addLocaleInternal(locale);
      }

      ResourceBundle oldBundle = (ResourceBundle)this._table.get(locale);
      if (oldBundle == null || oldBundle instanceof EmptyResourceBundle) {
         bundle.setFamily(this);
         this._table.put(locale, bundle);
      }
   }

   private synchronized void setModule(String module) {
      this._module = module;
      int end = this._module.indexOf("__");
      if (end != -1) {
         this._module = this._module.substring(0, end);
      }
   }

   public boolean verify(CodeSigningKey key) {
      if (key == null) {
         return true;
      }

      if (this._key != null) {
         return key.equals(this._key);
      }

      Enumeration enumeration = this._table.elements();

      while (enumeration.hasMoreElements()) {
         Object bundle = enumeration.nextElement();
         int module = CodeModuleManager.getModuleHandleForObject(bundle);
         if (!ControlledAccess.verifyCodeModuleSignature(module, key)) {
            return false;
         }
      }

      this._key = key;
      return true;
   }
}
