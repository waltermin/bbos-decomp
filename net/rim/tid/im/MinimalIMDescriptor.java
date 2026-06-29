package net.rim.tid.im;

import net.rim.device.api.i18n.Locale;
import net.rim.device.internal.i18n.ResourceBundleFetcher;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.util.StringUtilitiesInternal;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.awt.im.spi.InputMethod;
import net.rim.tid.awt.im.spi.InputMethodDescriptor;

public class MinimalIMDescriptor implements InputMethodDescriptor {
   private Locale[] _locales = new Locale[]{
      Locale.get("en", "US", "Multitap"),
      Locale.get("en"),
      Locale.get("en", "US"),
      Locale.get("en", "GB"),
      Locale.get("en", "NL"),
      Locale.get("de"),
      Locale.get("es"),
      Locale.get("fr"),
      Locale.get("it"),
      Locale.get("pt"),
      Locale.get("pt", "BR"),
      Locale.get("cs"),
      Locale.get("hu"),
      Locale.get("pl"),
      Locale.get("ca"),
      Locale.get("af"),
      Locale.get("nl"),
      Locale.get("tr"),
      Locale.get("el"),
      Locale.get("sv"),
      Locale.get("no"),
      Locale.get("he"),
      Locale.get("he", "US"),
      Locale.get("he", "IL")
   };
   private Locale[] _displayLocales;
   private Locale[] _availableLocales;

   @Override
   public Locale[] getAvailableLocales() {
      if (InternalServices.isReducedFormFactor()) {
         this._displayLocales = new Locale[0];
         return new Locale[0];
      }

      if (this._availableLocales != null) {
         return this._availableLocales;
      }

      byte IS_AVAILABLE = 1;
      byte IS_DISPLAYABLE = 2;
      byte[] matrix = new byte[this._locales.length];
      int availableCount = 0;
      int displayCount = 0;

      for (int i = 0; i < this._locales.length; i++) {
         if (verifySystemModulePresent(this._locales[i])) {
            matrix[i] = IS_DISPLAYABLE;
            availableCount++;
            displayCount++;
         } else if (this._locales[i].getVariant().length() > 0
            && verifySystemModulePresent(Locale.get(this._locales[i].getLanguage(), this._locales[i].getCountry()))) {
            matrix[i] = IS_AVAILABLE;
            availableCount++;
         }
      }

      this._availableLocales = new Locale[availableCount];
      this._displayLocales = new Locale[displayCount];
      availableCount = 0;
      displayCount = 0;

      for (int i = 0; i < this._locales.length; i++) {
         if (matrix[i] > 0) {
            this._availableLocales[availableCount++] = this._locales[i];
            if (matrix[i] == IS_DISPLAYABLE) {
               this._displayLocales[displayCount++] = this._locales[i];
            }
         }
      }

      return this._availableLocales;
   }

   @Override
   public Locale[] getDisplayLocales() {
      if (this._displayLocales == null) {
         this.getAvailableLocales();
      }

      return this._displayLocales;
   }

   @Override
   public boolean hasDynamicLocaleList() {
      return true;
   }

   @Override
   public String getInputMethodDisplayName(Locale inputLocale, Locale displayLanguage) {
      return "";
   }

   @Override
   public InputMethod createInputMethod() {
      return new MinimalInputMethod(this.getAvailableLocales());
   }

   protected static boolean verifySystemModulePresent(Locale locale) {
      try {
         StringBuffer scratch = StringUtilitiesInternal.getScratchBuffer();
         String name;
         synchronized (scratch) {
            scratch.append("net.rim.device.internal.resource.Input");
            scratch.append('£');
            scratch.append(locale);
            name = scratch.toString();
            scratch.setLength(0);
         }

         boolean exists = ResourceBundleFetcher.verifyCompressedResourcePresent(name + ".crb");
         if (exists) {
            return true;
         }

         Class.forName(name);
         return true;
      } catch (ClassNotFoundException var6) {
         return false;
      }
   }

   @Override
   public long getInputMethodID() {
      return 1;
   }

   @Override
   public boolean forTestOnly() {
      return false;
   }

   @Override
   public CustomWordsRepository getRepository(int type) {
      return null;
   }

   @Override
   public void disposeCache() {
   }
}
