package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.localeremoval.LocaleRemovalStatusListener;
import net.rim.device.apps.api.localeremoval.LocaleRemovalUtility;
import net.rim.device.apps.api.setupwizard.BasicWizardPage$WizardVerb;
import net.rim.device.apps.api.setupwizard.CheckListWizardPage;
import net.rim.device.apps.api.setupwizard.SavableWizardPage;
import net.rim.device.apps.api.setupwizard.WizardDialog;
import net.rim.device.apps.api.setupwizard.resources.SetupWizardAPIResources;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.ProgressDialog;
import net.rim.vm.Array;

class LanguageRemovalWizardPage extends CheckListWizardPage implements SavableWizardPage, LocaleRemovalStatusListener {
   private RichTextField _headerLabel = null;
   private Application _appRef = null;
   private int[] _localeHashes = new int[0];
   private ProgressDialog _progressDialog;
   private boolean _localeRemovalFinished = false;

   protected void updateLocaleListing() {
      throw null;
   }

   protected boolean canSkipLanguageRemoval() {
      throw null;
   }

   protected int getLocaleSelectionIndex() {
      throw null;
   }

   protected DisplayableLocale[] getDisplayableLocales() {
      throw null;
   }

   @Override
   public void localeRemovalStateChanged(byte newState) {
      if (newState == 2) {
         this.updateLocaleListing();
         synchronized (this._appRef.getAppEventLock()) {
            this._progressDialog.close();
         }

         this._localeRemovalFinished = true;
         this._appRef.invokeLater(new LanguageRemovalWizardPage$1(this));
      }
   }

   @Override
   public void localeRemovalProgressUpdated(int executedOperations, int totalOperations) {
      if (this._progressDialog != null) {
         int currentProgress = executedOperations * 100 / totalOperations;
         this._progressDialog.setProgress(currentProgress);
         synchronized (this._appRef.getAppEventLock()) {
            this._progressDialog.doPaint();
         }
      }
   }

   @Override
   public byte getPageKey() {
      return 1;
   }

   @Override
   public void setPageOptions(int[] localeHashes) {
      this._localeHashes = Arrays.copy(localeHashes);
   }

   @Override
   public int[] getPageOptions() {
      return this._localeHashes;
   }

   private int findNewSelectedLocale(DisplayableLocale[] displayableLocales, Locale currentLocale) {
      int localeCode = currentLocale.getCode();
      String localeVariant = currentLocale.getVariant();

      for (int i = 0; i <= displayableLocales.length - 1; i++) {
         if (displayableLocales[i].getLocale().getCode() == localeCode && localeVariant.equals(displayableLocales[i].getLocale().getVariant())) {
            return i;
         }
      }

      return -1;
   }

   private boolean promptUserToProceed() {
      return WizardDialog.ask(4, OptionsResources.getString(2050), 0, super._warnOnCloseOrHotKey) == 0;
   }

   @Override
   protected void populateFields() {
      this._headerLabel = new RichTextField(36028797018963968L);
      this._headerLabel.setFont(this.getHeaderFont());
      this._headerLabel.setText(OptionsResources.getString(2048));
      this.setHeaderField(this._headerLabel);
      DisplayableLocale[] displayableLocales = this.getDisplayableLocales();
      Locale currentLocale = Locale.getDefaultForSystem();
      int localeSelectionIndex = this.getLocaleSelectionIndex();
      int numLocales = displayableLocales.length;
      if (localeSelectionIndex == -1) {
         localeSelectionIndex = this.findNewSelectedLocale(displayableLocales, currentLocale);
      }

      IntHashtable tmpSelectedHashes = new IntHashtable(this._localeHashes.length);
      Object dummyObject = new Object();
      if (this._localeHashes.length > 0) {
         for (int i = this._localeHashes.length - 1; i >= 0; i--) {
            tmpSelectedHashes.put(this._localeHashes[i], dummyObject);
         }
      }

      CheckboxField[] items = new CheckboxField[numLocales];
      Locale locale = null;
      int localeCode = 0;

      for (int i = 0; i < numLocales; i++) {
         locale = displayableLocales[i].getLocale();
         localeCode = locale.getCode();
         if (localeCode != 1701726018 && localeCode != 1701729619) {
            items[i] = new CheckboxField("\u200e" + locale.getDisplayName(), false);
            items[i].setCookie(locale);
            if (localeCode == 1701707776 || i == localeSelectionIndex) {
               items[i].setEditable(false);
               items[i].setChecked(true);
            } else if (tmpSelectedHashes.containsKey(locale.hashCode())) {
               items[i].setChecked(true);
            }
         }
      }

      this.setListItems(items);
      this.reloadTitle();
   }

   @Override
   protected void afterClose() {
      this._localeRemovalFinished = false;
   }

   @Override
   protected boolean saveWizard(Verb sender) {
      boolean allowedToProgress = true;
      boolean isBackVerb = false;
      if (sender instanceof BasicWizardPage$WizardVerb) {
         isBackVerb = !((BasicWizardPage$WizardVerb)sender).canAutoSave() && !(sender instanceof ExitVerb);
      }

      if (!isBackVerb && !this._localeRemovalFinished && !this.getAllItemsSelected()) {
         if (this.promptUserToProceed()) {
            CheckboxField[] items = this.getCheckboxFields();
            Locale locale = null;
            int uncheckedLocales = -1;
            String[] localesToRemove = new String[items.length];
            String[] helpLocalesToKeep = new String[0];

            for (int i = 0; i <= items.length - 1; i++) {
               if (items[i] != null && items[i].getCookie() instanceof Locale) {
                  locale = (Locale)items[i].getCookie();
                  if (!items[i].getChecked()) {
                     localesToRemove[++uncheckedLocales] = locale.toString();
                     Locale.removeLocaleInternal(locale);
                  } else if (!items[i].isEditable()) {
                     Arrays.add(helpLocalesToKeep, locale.toString());
                  }
               }
            }

            Array.resize(localesToRemove, uncheckedLocales + 1);
            this._progressDialog = new ProgressDialog(SetupWizardAPIResources.getString(25));
            this._progressDialog.show();
            LocaleRemovalUtility.removeLocales(localesToRemove, helpLocalesToKeep, this);
            return false;
         }

         allowedToProgress = false;
      }

      return allowedToProgress;
   }

   @Override
   public boolean confirm(Verb verb, Object context) {
      boolean saveRequired = false;
      boolean isExitVerb = verb instanceof ExitVerb;
      boolean isBackVerb = verb instanceof BasicWizardPage$WizardVerb && !((BasicWizardPage$WizardVerb)verb).canAutoSave() && !isExitVerb;
      if ((!(verb instanceof BasicWizardPage$WizardVerb) || !((BasicWizardPage$WizardVerb)verb).canAutoSave()) && !isExitVerb) {
         int result = -1;
         if (isBackVerb) {
            boolean changesMade = false;
            CheckboxField[] itemsC = this.getCheckboxFields();

            for (int i = 0; i <= itemsC.length - 1; i++) {
               if (itemsC[i] != null && itemsC[i].isDirty()) {
                  changesMade = true;
                  break;
               }
            }

            if (changesMade) {
               this.log("Prompting for save.");
               result = WizardDialog.ask(1, CommonResource.getString(10003), super._warnOnCloseOrHotKey);
               switch (result) {
                  case 0:
                     this.log("Cancel from save prompt");
                     saveRequired = false;
                     break;
                  case 1:
                  default:
                     this.log("Saving");
                     saveRequired = true;
                     break;
                  case 2:
                     this.log("Discarding");
                     this.discardWizard();
                     saveRequired = true;
               }
            } else {
               this.discardWizard();
               saveRequired = true;
            }
         }

         if (saveRequired && result == 1) {
            CheckboxField[] items = this.getCheckboxFields();
            Locale locale = null;
            int checkedLocales = -1;
            this._localeHashes = new int[items.length];

            for (int i = 0; i <= items.length - 1; i++) {
               if (items[i] != null && items[i].getChecked() && items[i].getCookie() instanceof Locale) {
                  locale = (Locale)items[i].getCookie();
                  this._localeHashes[++checkedLocales] = locale.hashCode();
               }
            }

            Array.resize(this._localeHashes, checkedLocales + 1);
         }

         return saveRequired;
      } else {
         return super.confirm(verb, context);
      }
   }

   @Override
   protected boolean canSkipWizardInternal() {
      return this.canSkipLanguageRemoval();
   }

   LanguageRemovalWizardPage(ResourceBundle rb) {
      super(rb, 2049, 200, null, 0);
      this._appRef = Application.getApplication();
      this.setFloatDisabledItemsToTop(true);
   }

   LanguageRemovalWizardPage() {
      super(OptionsResources.getString(2049), 200, null, 0);
      this._appRef = Application.getApplication();
      this.setFloatDisabledItemsToTop(true);
   }

   static void access$000(LanguageRemovalWizardPage x0) {
      x0.doNext();
   }
}
