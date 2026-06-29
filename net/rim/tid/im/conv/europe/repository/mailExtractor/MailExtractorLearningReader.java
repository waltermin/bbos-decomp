package net.rim.tid.im.conv.europe.repository.mailExtractor;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.data.LearningData;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.europe.repository.WordLearningReader;

class MailExtractorLearningReader extends WordLearningReader {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean setLocale(Locale aLocale) {
      synchronized (LearningDataManager.getLock()) {
         if (aLocale != null && (super._currentLocale == null || !super._currentLocale.equals(aLocale))) {
            String lang = aLocale.getLanguage();
            String locale_str = lang;
            super._currentLearnName = this.getFilename(locale_str);
            super._header.setLocaleStr(locale_str);

            label42:
            try {
               this.loadLearningWordlist();
            } catch (Throwable var9) {
               e.printStackTrace();
               break label42;
            }

            super._currentLocale = aLocale;
            super._alphabet.setLocale(super._currentLocale);
            return true;
         } else {
            return true;
         }
      }
   }

   @Override
   protected void loadLearningWordlist() {
      synchronized (LearningDataManager.getLock()) {
         LearningData data = LearningDataManager.getLearningData(super._currentLearnName);
         if (data != null) {
            this.loadLearningWordlist(data.getData());
         }
      }
   }
}
