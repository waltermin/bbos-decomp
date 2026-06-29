package net.rim.device.apps.api.localeremoval;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Process;

class RemovalWorkerThread extends Thread {
   private LocaleRemovalStatusListener _listener;
   private String[] _locales;
   private String[] _helpLocalesToKeep;
   private int[] _handles = new int[0];
   private boolean _onStartup = false;
   private String CORE_IM_RESOURCE_IDENTIFIER = "_im_";
   private String CORE_HELP_RESOURCE_INDENTIFIER = "_help_";
   private boolean _slowDelete = false;
   private int IDLE_TIME = 30;

   RemovalWorkerThread(LocaleRemovalStatusListener listener, String[] locales, String[] helpLocalesToKeep, boolean onStartup) {
      this._locales = locales;
      this._listener = listener;
      this._onStartup = onStartup;
      this._helpLocalesToKeep = helpLocalesToKeep;
      if (this._listener != null) {
         this._slowDelete = true;
      }
   }

   @Override
   public void run() {
      int[] moduleHandles = this.startRemovalProcess(this._locales, this._helpLocalesToKeep);
      synchronized (this._handles) {
         Arrays.append(this._handles, moduleHandles);
      }

      boolean resetRequired = this.removeResourceModules();
      if (this._onStartup && resetRequired) {
         LocaleRemovalUtility.setRemovalStatus(1);
         InternalServices.initiateReset("SetupWizard - LocaleRemoval");
      } else {
         LocaleRemovalUtility.setRemovalStatus(2);
      }
   }

   private int[] startRemovalProcess(String[] locales, String[] helpLocalesToKeep) {
      this.notifyListenerStateChanged((byte)0);
      int[] handles;
      synchronized (this._handles) {
         handles = this.findResourceModulesForLocales(locales, helpLocalesToKeep);
      }

      this.notifyListenerStateChanged((byte)1);
      this.notifyListenerStateChanged((byte)2);
      return handles;
   }

   void addAdditionalLocalesForRemoval(String[] locales) {
      int[] handles = this.startRemovalProcess(locales, null);
      synchronized (this._handles) {
         Arrays.append(this._handles, handles);
      }
   }

   private int[] findResourceModulesForLocales(String[] localesToRemove, String[] helpLocalesToKeep) {
      int[] allHandles = CodeModuleManager.getModuleHandles();
      int[] deleteHandles = new int[0];
      Locale moduleLocale = null;
      Hashtable modulesToRemove = (Hashtable)(new Object());
      Hashtable helpModulesToKeep = (Hashtable)(new Object());
      Object someObject = new Object();
      if (localesToRemove == null) {
         return new int[0];
      }

      for (int j = 0; j <= localesToRemove.length - 1; j++) {
         modulesToRemove.put(localesToRemove[j], someObject);
      }

      if (helpLocalesToKeep != null) {
         for (int j = 0; j <= helpLocalesToKeep.length - 1; j++) {
            helpModulesToKeep.put(helpLocalesToKeep[j], someObject);
         }
      }

      String locale = null;

      for (int i = 0; i <= allHandles.length - 1; i++) {
         boolean removeLocale = false;
         byte[] rawLanguageData = CodeModuleManager.getModuleLanguageData(allHandles[i]);
         if (rawLanguageData != null && rawLanguageData.length > 0) {
            moduleLocale = this.getLocaleFromBytes(rawLanguageData);
            if (moduleLocale != null) {
               locale = moduleLocale.toString();
               if (modulesToRemove.containsKey(locale)
                     && Locale.isLocaleEligbleForRemovable(moduleLocale, this.isCodOfType(allHandles[i], this.CORE_IM_RESOURCE_IDENTIFIER))
                  || this.isCodOfType(allHandles[i], this.CORE_HELP_RESOURCE_INDENTIFIER) && !helpModulesToKeep.containsKey(locale)) {
                  removeLocale = true;
               }
            }

            if (removeLocale) {
               Arrays.add(deleteHandles, allHandles[i]);
            }
         }

         this.notifyListenerProgressUpdated(i, allHandles.length);
         Thread.yield();
      }

      this.notifyListenerProgressUpdated(1, 1);
      Thread.yield();
      return deleteHandles;
   }

   private boolean isCodOfType(int moduleHandle, String identifier) {
      return CodeModuleManager.getModuleName(moduleHandle).indexOf(identifier) >= 0;
   }

   private boolean removeResourceModules() {
      boolean restartRequired = false;
      boolean forceWait = false;
      Thread.currentThread().setPriority(1);
      int deletionStatus = 0;
      int i = 0;

      while (i <= this._handles.length - 1) {
         if (this._slowDelete) {
            if (Process.ensureMinimumIdleTime(this.IDLE_TIME) <= 0 || forceWait) {
               synchronized (this) {
                  label147:
                  try {
                     this.wait(this.IDLE_TIME * 1000);
                  } finally {
                     break label147;
                  }
               }

               forceWait = false;
               continue;
            }

            synchronized (this._handles) {
               label135:
               try {
                  deletionStatus = CodeModuleManager.deleteModuleEx(this._handles[i], true);
               } finally {
                  break label135;
               }
            }

            forceWait = true;
         } else {
            synchronized (this._handles) {
               deletionStatus = CodeStore.scheduleDeleteModule(this._handles[i]);
            }
         }

         i++;
         if (deletionStatus == 6 && !restartRequired) {
            restartRequired = true;
         }

         Thread.yield();
      }

      return restartRequired;
   }

   private void notifyListenerStateChanged(byte state) {
      if (this._listener != null) {
         this._listener.localeRemovalStateChanged(state);
      }
   }

   private void notifyListenerProgressUpdated(int executedOperations, int totalOperations) {
      if (this._listener != null) {
         this._listener.localeRemovalProgressUpdated(executedOperations, totalOperations);
      }
   }

   private Locale getLocaleFromBytes(byte[] byteArray) {
      Locale locale = null;
      String language = null;
      String country = null;
      String variant = null;

      try {
         String temp = (String)(new Object(byteArray, 0, byteArray.length, "UTF-8"));
         temp = StringUtilities.removeLineBreaksInString(temp.trim());
         StringTokenizer localeTokenizer = (StringTokenizer)(new Object(temp, "_", false));
         if (localeTokenizer.hasMoreTokens()) {
            language = localeTokenizer.nextToken();
            if (localeTokenizer.hasMoreTokens()) {
               country = localeTokenizer.nextToken();
               if (localeTokenizer.hasMoreTokens()) {
                  variant = localeTokenizer.nextToken();
               }
            }
         }

         try {
            locale = Locale.get(language, country, variant);
         } finally {
            ;
         }
      } finally {
         return locale;
      }

      return locale;
   }

   public void setListener(LocaleRemovalStatusListener listener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
