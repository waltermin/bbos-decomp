package net.rim.device.api.i18n;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.WeakReference;

public class ResourceBundle {
   private ResourceBundleFamily _family;
   private Locale _locale;
   private ResourceBundle _parent;
   private static final long COLLECTION_ID = 2465954242248413450L;
   private static LongHashtable _table = ApplicationRegistry.getApplicationRegistry().getLongHashtable(2465954242248413450L);

   protected ResourceBundle(Locale locale) {
      this._locale = locale;
   }

   long getId() {
      return -1;
   }

   public static final ResourceBundleFamily getBundle(String name) {
      return getBundle(StringUtilities.stringHashToLong(name), name, null);
   }

   public static final ResourceBundleFamily getBundle(long bundle, String name) {
      return getBundle(bundle, name, null);
   }

   public static final ResourceBundleFamily getBundle(long bundle, String name, CodeSigningKey key) {
      ResourceBundleFamily result = null;
      WeakReference ref = null;
      synchronized (_table) {
         ref = (WeakReference)_table.get(bundle);
         if (ref == null) {
            ref = new WeakReference(null);
            _table.put(bundle, ref);
         }
      }

      synchronized (ref) {
         result = (ResourceBundleFamily)ref.get();
         if (result == null) {
            verifyHash(bundle, name);
            result = new ResourceBundleFamily(bundle, name, key);
            ref.set(result);
         }
      }

      if (!result.verify(key)) {
         throw new MissingResourceException("Invalid signature");
      } else {
         return result;
      }
   }

   public final ResourceBundleFamily getFamily() {
      return this._family;
   }

   public final Locale getLocale() {
      return this._locale;
   }

   public final Object getObject(int key) {
      Object obj = this.getObject(key, true);
      if (obj == null) {
         throw new MissingResourceException();
      } else {
         return obj;
      }
   }

   public final Object getObject(int key, boolean searchParent) {
      Object obj = this.handleGetObject(key);
      if (obj == null && this._parent != null && searchParent) {
         obj = this._parent.getObject(key, searchParent);
      }

      return obj;
   }

   public final String getString(int key) {
      return (String)this.getObject(key);
   }

   public final String[] getStringArray(int key) {
      return (String[])this.getObject(key);
   }

   protected Object handleGetObject(int _1) {
      throw null;
   }

   public static void registerGlobalEventListener() {
      Application.getApplication().addGlobalEventListener(new ResourceBundle$1());
   }

   void setFamily(ResourceBundleFamily family) {
      this._family = family;
   }

   void setParent(ResourceBundle parent) {
      this._parent = parent;
   }

   private static void verifyHash(long bundle, String name) {
      if (name != null && name.length() != 0) {
         long hash = StringUtilities.stringHashToLong(name);
         if (hash != bundle) {
            throw new IllegalArgumentException("Invalid bundle hash");
         }
      } else {
         throw new IllegalArgumentException("Invalid name");
      }
   }
}
