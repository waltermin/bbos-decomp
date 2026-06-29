package net.rim.device.apps.internal.vad;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.vad.VADLanguageSetting;
import net.rim.device.internal.vad.VADNatives;

final class VADEngineManager$VADLanguageSettingImpl extends VADLanguageSetting {
   private final VADEngineManager this$0;

   VADEngineManager$VADLanguageSettingImpl(VADEngineManager _1) {
      this.this$0 = _1;
      ApplicationRegistry.getApplicationRegistry().put(7371110663783438069L, this);
   }

   @Override
   public final String[] getSupportedLanguages() {
      int numLanguages = this.this$0._availableLanguages.length;
      String[] strings = new Object[numLanguages];

      for (int i = 0; i < numLanguages; i++) {
         Locale locale = Locale.get(VADEngineManager.LOCALE_IDS[this.this$0._availableLanguages[i]]);
         strings[i] = locale.getDisplayName();
      }

      return strings;
   }

   @Override
   public final int getLanguageIndex() {
      int index = this.getIndex(this.this$0._persistentData._parameters._language);
      return index == -1 ? 0 : index;
   }

   @Override
   public final int getLanguageIndexForLocale(Locale locale) {
      int bestMatch = this.this$0.getVADLanguageForLocale(locale);
      int index = this.getIndex(bestMatch);
      if (index != -1) {
         return index;
      }

      index = this.getIndex(1);
      return index != -1 ? index : 0;
   }

   private final int getIndex(int language) {
      int numLanguages = this.this$0._availableLanguages.length;

      for (int i = 0; i < numLanguages; i++) {
         if (this.this$0._availableLanguages[i] == language) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public final void setLanguageIndex(int index) {
      this.this$0.setLanguage(this.this$0._availableLanguages[index]);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final String[] getVersionInfo() {
      Object[] info = VADNatives.getVersionInfo();
      String[] results = null;
      if (info != null) {
         results = new Object[info.length];

         for (int i = info.length - 1; i >= 0; i--) {
            boolean var6 = false /* VF: Semaphore variable */;

            try {
               var6 = true;
               results[i] = (String)(new Object((byte[])info[i], this.this$0._encoding));
               var6 = false;
            } finally {
               if (var6) {
                  results[i] = (String)(new Object((byte[])info[i]));
                  continue;
               }
            }
         }
      }

      return results;
   }
}
