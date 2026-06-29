package net.rim.device.apps.internal.setupwizard;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.setupwizard.Log;
import net.rim.device.apps.api.setupwizard.LogManager;
import net.rim.device.apps.api.setupwizard.SavableWizardPage;
import net.rim.device.apps.api.setupwizard.SetupWizardRegistration;
import net.rim.device.apps.api.setupwizard.WizardCategory;
import net.rim.device.apps.api.setupwizard.WizardController;
import net.rim.device.apps.api.setupwizard.WizardController$Listener;
import net.rim.device.apps.api.setupwizard.WizardPage;
import net.rim.vm.PersistentInteger;

public final class SetupWizardScreen extends MainScreen implements GlobalEventListener, WizardController$Listener {
   private int _lastViewedPageID;
   private Vector _allPages;
   private WizardListField _wizardList;
   private WizardController _controller;
   private boolean _isAutoRun;
   private WizardPage _lastViewedPage;
   private Log _log;
   private LogManager _logManager;
   private SetupWizardOptions _options;
   private static final long LAST_VIEWED_PAGE_INDEX = -4908619151704813594L;
   private static final int CATEGORY_MODE = 1;

   SetupWizardScreen(boolean isAutoRun, LogManager logManager) {
      super(299067162755072L);
      this.reloadTitle();
      this._lastViewedPageID = PersistentInteger.getId(-4908619151704813594L, -1);
      this._options = SetupWizardOptions.getOptions();
      this._logManager = logManager;
      if (this._logManager != null) {
         this._log = this._logManager.getCategory("SetupWizard");
         if (isAutoRun) {
            this._log.log("STARTUP - Launched from welcome dialog.");
         } else {
            this._log.log("STARTUP - Index Screen");
         }
      }

      this._isAutoRun = isAutoRun;
      this.initialize();
   }

   protected final void initialize() {
      this._allPages = SetupWizardRegistration.getWizardPages();
      Vector allCategories = SetupWizardRegistration.extractWizardCategories(this._allPages);
      int numCategories = allCategories.size();

      for (int i = 0; i < numCategories; i++) {
         this._allPages.addElement(allCategories.elementAt(i));
      }

      if (this._isAutoRun || !this._options.getWizardCompleted()) {
         this._allPages.addElement(new InitialWizardPage());
      }

      FinishWizardPage finishWizardPage = new FinishWizardPage();
      this._allPages.addElement(finishWizardPage);
      this._controller = (WizardController)(new Object("Root", this._allPages, 0));
      this._controller.addWizardControllerListener(this);
      this._controller.setLogManager(this._logManager);
      this._controller.setCompletingWizardPage(finishWizardPage);
      this._wizardList = new WizardListField(this._controller, 1, new SetupWizardScreen$LaunchWizardVerb(this));
      this.add(this._wizardList);
      this.resetFonts();
   }

   protected final void resetFonts() {
      if (this._wizardList != null) {
         Font font = Font.getDefault();
         int fontHeight = font.getHeight();
         int fontPt = Ui.convertSize(fontHeight, 0, 3);
         if (fontPt > 8) {
            this._wizardList.setFont(font.derive(font.getStyle(), fontPt - 1, 3));
         } else {
            this._wizardList.setFont(null);
         }
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         this._lastViewedPage = this.loadLastViewedPage();
         if (this._lastViewedPage != null) {
            if (this._wizardList.getCategoryMode() == 2 && this._lastViewedPage.getCategory() != null) {
               this._wizardList.setSelectedPage(this._lastViewedPage.getCategory());
            } else {
               this._wizardList.setSelectedPage(this._lastViewedPage);
            }
         }

         Application.getApplication().addGlobalEventListener(this);
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   public final boolean onClose() {
      this.cleanup();
      Application.getApplication().removeGlobalEventListener(this);
      return super.onClose();
   }

   protected final void cleanup() {
      this.saveLastViewedPage(this._lastViewedPage);
   }

   public final void startAutoRun() {
      int context = 0;
      if (this._isAutoRun) {
         context |= 2;
      }

      if (!this._options.getWizardCompleted()) {
         context |= 4;
      }

      this.startWizard(this.loadLastViewedPage(), context);
      if (this._isAutoRun && !this._options.getWizardCompleted()) {
         Dialog exitDialog = SetupWizard.createExitDialog();
         Application.getApplication().requestBackground();
         Ui.getUiEngine().pushGlobalScreen(exitDialog, 100, 1);
      }

      this.cleanup();
   }

   protected final WizardPage loadLastViewedPage() {
      int index = PersistentInteger.get(this._lastViewedPageID);
      return (WizardPage)(index >= 0 && index < this._allPages.size() ? this._allPages.elementAt(index) : this._controller.getFirstPage());
   }

   protected final void saveLastViewedPage(WizardPage page) {
      int index = -1;
      if (!(page instanceof FinishWizardPage)) {
         int numPages = this._allPages.size();

         for (int i = 0; i < numPages; i++) {
            if (page == this._allPages.elementAt(i)) {
               index = i;
               break;
            }
         }
      }

      PersistentInteger.set(this._lastViewedPageID, index);
   }

   protected final int startWizard(WizardPage page, int context) {
      int result = 2;
      if (page instanceof Object) {
         if (this._wizardList.getCategoryMode() != 2) {
            return 0;
         }

         this._wizardList.setCategoryFilter((WizardCategory)page);
         this._wizardList.setSelectedPage(this._lastViewedPage);
         return result;
      } else {
         result = this._controller.showWizardPage(page, context);
         this._lastViewedPage = this._controller.getLastViewedPage();
         this._wizardList.setCategoryFilter(this._lastViewedPage.getCategory());
         this._wizardList.setSelectedPage(this._lastViewedPage);
         return result;
      }
   }

   @Override
   protected final boolean openProductionBackdoor(int backdoorCode) {
      super.openProductionBackdoor(backdoorCode);
      if (backdoorCode == 1414878287) {
         this._lastViewedPage = null;
         this._options.setSetupWizardDesired(true);
         this._options.setWizardCompleted(false);
         this._options.commit();
         SetupWizard.scheduleReminder(false);
         Dialog.inform("The setup wizard dialog is scheduled for 24 hours from now and will also show on power up.");
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      super.openDevelopmentBackdoor(backdoorCode);
      if (backdoorCode == 1280264005) {
         boolean enabled = !this._options.getLoggingEnabled();
         this._options.setLoggingEnabled(enabled);
         this._options.commit();
         if (enabled) {
            Dialog.inform("Logging will be enabled when the setup wizard is restarted.");
            return true;
         } else {
            Dialog.inform("Logging will be disabled when the setup wizard is restarted.");
            return true;
         }
      } else {
         return false;
      }
   }

   protected final void reloadTitle() {
      this.setTitle(new SetupWizardScreen$TitleField(SetupWizardResources.getString(0)));
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L) {
         int numPages = this._allPages.size();

         for (int i = 0; i < numPages; i++) {
            WizardPage page = (WizardPage)this._allPages.elementAt(i);
            page.reloadTitle();
         }

         this.reloadTitle();
      }

      if (guid == -4394903006263251010L) {
         this.resetFonts();
      }
   }

   @Override
   public final void openingWizardPage(WizardController sender, WizardPage page, int entranceCommand, int context) {
      this.saveLastViewedPage(page);
      if (page instanceof Object) {
         SavableWizardPage savablePage = (SavableWizardPage)page;
         int[] pageOptions = this._options.getPageOptions(savablePage.getPageKey());
         if (pageOptions.length > 0) {
            savablePage.setPageOptions(pageOptions);
         }
      }

      if (page instanceof Object) {
         WizardController controller = (WizardController)page;
         controller.addWizardControllerListener(this);
      }
   }

   @Override
   public final void closingWizardPage(WizardController sender, WizardPage page, int result, int context) {
      if (page instanceof Object) {
         SavableWizardPage savablePage = (SavableWizardPage)page;
         this._options.setPageOptions(savablePage.getPageKey(), savablePage.getPageOptions());
      }
   }
}
