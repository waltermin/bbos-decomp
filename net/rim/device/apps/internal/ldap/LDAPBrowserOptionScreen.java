package net.rim.device.apps.internal.ldap;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.i18n.CommonResource;

final class LDAPBrowserOptionScreen extends AppsMainScreen {
   private LDAPBrowserOptionStore _optionStore;
   private String _contextString;
   private ObjectChoiceField _fetchCertStatusField;
   private BooleanChoiceField _promptForCertLabelField;
   private LDAPBrowserOptionScreen$LDAPBrowserVerb _saveVerb;
   private LDAPBrowserOptionScreen$LDAPBrowserVerb _closeVerb;

   public LDAPBrowserOptionScreen(String applicationTitle, String contextString) {
      super(0);
      this.setDefaultClose(false);
      this._optionStore = LDAPBrowserOptionStore.getInstance();
      this._contextString = contextString;
      String optionsTitle = MessageFormat.format(LDAPBrowser.getString(80), new String[]{applicationTitle});
      this.setTitle(optionsTitle);
      String[] choices = new String[]{CommonResources.getString(100), CommonResources.getString(101), LDAPBrowser.getString(84)};
      this._fetchCertStatusField = new ObjectChoiceField(LDAPBrowser.getString(82), choices, this._optionStore.getFetchCertStatus(this._contextString));
      this.add(this._fetchCertStatusField);
      this._promptForCertLabelField = new BooleanChoiceField(LDAPBrowser.getString(93), 0, this._optionStore.getPromptForCertLabel(this._contextString));
      this.add(this._promptForCertLabelField);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean retval = false;
      if (super.keyChar(key, status, time)) {
         return true;
      }

      switch (key) {
         case '\u001b':
            retval = true;
            if (this.hasChanged()) {
               this.promptForSave();
            } else {
               this.doClose();
            }
         default:
            return retval;
      }
   }

   private final void doSave() {
      this._optionStore.setFetchCertStatus(this._contextString, this._fetchCertStatusField.getSelectedIndex());
      this._optionStore.setPromptForCertLabel(this._contextString, this._promptForCertLabelField.isAffirmative());
      this.doClose();
   }

   private final void doClose() {
      UiApplication.getUiApplication().popScreen(this);
   }

   private final boolean hasChanged() {
      return this._fetchCertStatusField.getSelectedIndex() != this._optionStore.getFetchCertStatus(this._contextString)
         ? true
         : this._promptForCertLabelField.isAffirmative() != this._optionStore.getPromptForCertLabel(this._contextString);
   }

   private final void promptForSave() {
      int choice = Dialog.ask(1);
      if (choice == 1) {
         this.doSave();
      } else {
         if (choice == 2) {
            this.doClose();
         }
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._saveVerb == null) {
         this._saveVerb = new LDAPBrowserOptionScreen$LDAPBrowserVerb(this, 1, 332288, CommonResource.getBundle(), 18);
      }

      menu.add(this._saveVerb);
      if (this._closeVerb == null) {
         this._closeVerb = new LDAPBrowserOptionScreen$LDAPBrowserVerb(this, 2, 268501008, CommonResource.getBundle(), 9);
      }

      menu.add(this._closeVerb);
      if (this.hasChanged()) {
         menu.setDefault(this._saveVerb);
      }
   }
}
