package net.rim.device.apps.api.localeremoval;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.PersistentInteger;

public final class LocaleRemovalUtility {
   private static final long GUID_LOCALE_REMOVAL_STATUS = 2337153136350881996L;
   private static final long GUID_LOCALES_SCHEDULED_FOR_REMOVAL = 8374008272057660071L;
   private static final long THREAD_GUID = 3915939125099425057L;
   static final int REMOVAL_STATUS_INCOMPLETE = 0;
   static final int REMOVAL_STATUS_RESET_PENDING = 1;
   static final int REMOVAL_STATUS_COMPLETE = 2;
   static final int REMOVAL_STATUS_STARTED = 3;
   public static final int BUILD_TYPE_NON_MULTILANG = 0;
   public static final int BUILD_TYPE_MULTILANG_DISPLAY = 1;
   public static final int BUILD_TYPE_MULTILANG_INPUT = 2;
   public static final int BUILD_TYPE_MULTILANG_ALL = 3;
   public static final String LOCALE_REMOVAL_RESET_TEXT = "SetupWizard - LocaleRemoval";

   public static final void removeLocales(String[] locales, String[] helpLocalesToKeep, LocaleRemovalStatusListener listener) {
      RemovalWorkerThread thread = getRunningThread();
      if (thread != null) {
         thread.setListener(listener);
         thread.addAdditionalLocalesForRemoval(locales);
      } else {
         setLocalesToPersist(locales, helpLocalesToKeep);
         setRemovalStatus(3);
         startLocaleRemovalProcess(listener, locales, helpLocalesToKeep, false);
      }
   }

   private static final RemovalWorkerThread getRunningThread() {
      Object obj = ApplicationRegistry.getApplicationRegistry().get(3915939125099425057L);
      if (obj instanceof RemovalWorkerThread) {
         RemovalWorkerThread thread = (RemovalWorkerThread)obj;
         if (thread.isAlive()) {
            return thread;
         }
      }

      return null;
   }

   private static final void setLocalesToPersist(String[] localesA, String[] localesB) {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(8374008272057660071L);
      Object[] obj = new Object[]{localesA, localesB};
      persistentObject.setContents(obj, 51);
      persistentObject.commit();
   }

   public static final boolean isLocaleRemovalComplete() {
      return getRemovalStatus() == 2;
   }

   public static final int getMultiLanguageBuildType(boolean countryCodeSensitive) {
      return isMultiLanguageBuild(Locale.getDefault().getCode(), Locale.getAvailableLocales(), 1, countryCodeSensitive)
         | isMultiLanguageBuild(Locale.getDefaultInputForSystem().getCode(), Locale.getAvailableInputLocales(), 2, countryCodeSensitive);
   }

   private static final int isMultiLanguageBuild(int defaultLocale, Locale[] locales, int buildType, boolean countryCodeSensitive) {
      if (!countryCodeSensitive) {
         defaultLocale &= -65536;
      }

      for (int i = 0; i < locales.length; i++) {
         int locale = countryCodeSensitive ? locales[i].getCode() : locales[i].getCode() & -65536;
         if (locale != 0 && locale != defaultLocale) {
            return buildType;
         }
      }

      return 0;
   }

   static final void setRemovalStatus(int newStatus) {
      if (newStatus == 2) {
         label19:
         try {
            ApplicationRegistry.getApplicationRegistry().remove(3915939125099425057L);
         } finally {
            break label19;
         }
      }

      int id = PersistentInteger.getId(2337153136350881996L, 0);
      PersistentInteger.set(id, newStatus);
   }

   static final int getRemovalStatus() {
      int id = PersistentInteger.getId(2337153136350881996L, 0);
      return PersistentInteger.get(id);
   }

   static final void startLocaleRemovalProcess(LocaleRemovalStatusListener listener, String[] locales, String[] helpLocalesToKeep, boolean onRestart) {
      if (locales == null) {
         PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(8374008272057660071L);
         Object obj = persistentObject.getContents();
         if (obj instanceof Object[]) {
            Object[] objArrays = (Object[])obj;
            if (objArrays[0] instanceof String[]) {
               locales = (String[])objArrays[0];
            }

            if (objArrays[1] instanceof String[]) {
               helpLocalesToKeep = (String[])objArrays[1];
            }
         }
      }

      if (locales != null) {
         LocaleRemovalLowMemoryListener.unloadListner();
         Thread thread = new RemovalWorkerThread(listener, locales, helpLocalesToKeep, onRestart);
         ApplicationRegistry.getApplicationRegistry().put(3915939125099425057L, thread);
         Proxy.getInstance().startThread(thread);
      }
   }
}
