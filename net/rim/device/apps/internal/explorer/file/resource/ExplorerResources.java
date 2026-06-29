package net.rim.device.apps.internal.explorer.file.resource;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.internal.resource.ExplorerResource;

public final class ExplorerResources implements ExplorerResource {
   private static ResourceBundleFamily _strings;

   public static final String getString(int id) {
      if (_strings == null) {
         getResourceBundleFamily();
      }

      return _strings.getString(id);
   }

   public static final String[] getStringArray(int id) {
      if (_strings == null) {
         getResourceBundleFamily();
      }

      return _strings.getStringArray(id);
   }

   public static final String[] getTokenizedStringArrayElement(int id, int index) {
      return parseTokenizedString(getStringArray(id)[index]);
   }

   public static final String[] getTokenizedStringArrayElement(int locale, int id, int index) {
      return parseTokenizedString(getResourceBundleFamily().getFamily().getBundle(Locale.get(locale)).getStringArray(id)[index]);
   }

   private static final String[] parseTokenizedString(String s) {
      String[] result = new Object[0];
      StringTokenizer tokenizer = (StringTokenizer)(new Object(s, '%'));

      while (tokenizer.hasMoreElements()) {
         Arrays.add(result, tokenizer.nextElement());
      }

      return result;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final ResourceBundleFamily getResourceBundleFamily() {
      if (_strings == null) {
         boolean var2 = false /* VF: Semaphore variable */;

         try {
            var2 = true;
            _strings = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
            var2 = false;
         } finally {
            if (var2) {
               throw new Object("No file explorer resources");
            }
         }
      }

      return _strings;
   }
}
