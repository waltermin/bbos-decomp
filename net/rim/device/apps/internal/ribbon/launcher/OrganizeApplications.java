package net.rim.device.apps.internal.ribbon.launcher;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.EventInjector;
import net.rim.device.api.system.EventInjector$TrackwheelEvent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.ribbon.ApplicationMenu;
import net.rim.device.apps.internal.ribbon.RibbonOptions;

public final class OrganizeApplications extends SaveableMainScreenOptionsListItem implements OptionsProviderRegistration$OptionsProvider, FocusChangeListener {
   private ApplicationLauncherField _iconArea;
   private ApplicationEntry _selectedApplication;
   private HierarchyManager _hierarchyManager;
   private RibbonOptions _ribbonOptions = RibbonOptions.getOptions();
   private Manager _previousManager;
   private Manager _currentIconLauncherfield;
   private static ResourceBundleFamily _rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");

   public static final void register() {
      OptionsProviderRegistration.registerOptionsProvider(new OrganizeApplications());
   }

   public OrganizeApplications() {
      super(_rbf, 149);
      this._hierarchyManager = HierarchyManager.getInstance();
   }

   @Override
   public final Vector getOptionsItems() {
      if (!HierarchyManager.isApplicationMenuValid(0)) {
         return null;
      }

      Vector items = new Vector();
      items.addElement(this);
      return items;
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      if (HierarchyManager.isApplicationMenuValid(0)) {
         ApplicationMenu.setAllowToShow(false);
         this._currentIconLauncherfield = (Manager)HierarchyManager.getInstance().getApplicationLauncher();
         this._iconArea = (ApplicationLauncherField)this._currentIconLauncherfield;
         if (this._iconArea != null) {
            this._iconArea.resetFocusToTop();
            this._iconArea.setShowHiddenApps(true);
            this._currentIconLauncherfield.setFocusListener(null);
            this._currentIconLauncherfield.setFocusListener(this);
            this._previousManager = this._currentIconLauncherfield.getManager();
            if (this._previousManager != null) {
               this._previousManager.delete(this._currentIconLauncherfield);
            }
         }

         this.showAllApps(true);
      }

      if (this._currentIconLauncherfield != null) {
         mainScreen.add(this._currentIconLauncherfield);
      }

      super._mainScreen = mainScreen;
   }

   @Override
   protected final boolean save() {
      return this.discard() ? true : super.save();
   }

   private final void showAllApps(boolean show) {
      if (this._iconArea != null) {
         synchronized (this._iconArea) {
            this._iconArea.setShowHiddenApps(show);
            this._iconArea.loadApplications();
            this._hierarchyManager.fireOnEntryChange(null);
         }
      }
   }

   @Override
   protected final boolean discard() {
      if (this._iconArea != null && this._iconArea.moveApplicationInProgress()) {
         this._iconArea.completeMoveApplication(false);
      }

      if (this._currentIconLauncherfield != null) {
         label28:
         try {
            super._mainScreen.delete(this._currentIconLauncherfield);
            this._previousManager.add(this._currentIconLauncherfield);
         } finally {
            break label28;
         }
      }

      ApplicationMenu.setAllowToShow(true);
      this.showAllApps(false);
      return super.discard();
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      switch (key) {
         case 10:
            EventInjector.invokeEvent(new EventInjector$TrackwheelEvent(516, 0, 0));
            return true;
         case 17:
         case 18:
         case 19:
         case 21:
            this.discard();
            UiApplication.getUiApplication().popScreen(super._mainScreen);
            break;
         case 27:
            if (this.invokeMoveCompleteAction(false)) {
               return true;
            }

            this.discard();
      }

      return super.keyDown(keycode, time);
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      return this.invokeMoveCompleteAction(true) ? true : super.trackwheelClick(status, time);
   }

   @Override
   protected final boolean invokeOptionsAction(int action) {
      switch (action) {
         case 1:
            if (!this.invokeMoveCompleteAction(true)) {
               this.invokeMoveAction();
            }

            return true;
         default:
            return false;
      }
   }

   private final boolean invokeMoveCompleteAction(boolean bComplete) {
      if (this._iconArea != null && this._iconArea.moveApplicationInProgress()) {
         this._iconArea.completeMoveApplication(bComplete);
         super._mainScreen.invalidate();
         return true;
      } else {
         return false;
      }
   }

   private final void invokeMoveAction() {
      this._iconArea.beginMoveApplication();
      super._mainScreen.invalidate();
   }

   @Override
   public final MainScreen createMainScreen() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final void populateMenuVerbs(VerbToMenu verbToMenu, int instance) {
      if (!HierarchyManager.isApplicationMenuValid(0)) {
         super.populateMenuVerbs(verbToMenu, instance);
      } else {
         this._selectedApplication = this._iconArea.getSelectedApplication();
         ApplicationDescriptor appDesc = ApplicationDescriptor.currentApplicationDescriptor();
         String name = this._selectedApplication != null ? this._selectedApplication.getUniqueName() : "";
         boolean currentAppSelected = name.equals(appDesc.getModuleName() + "." + appDesc.getName());
         if (!this._iconArea.moveApplicationInProgress()) {
            if (this._hierarchyManager.movingIconsAllowed()) {
               verbToMenu.addVerb(new OrganizeApplications$OrganizeApplicationsVerb(this, 614656, 21));
            }

            if (this._hierarchyManager.hidingIconsAllowed() && !currentAppSelected && this._selectedApplication != null && this._selectedApplication.canHide()) {
               verbToMenu.addVerb(new OrganizeApplications$OrganizeApplicationsVerb(this, 614912, 31));
            }

            verbToMenu.addVerb(new OrganizeApplications$ResetThemeVerb(this, 268435456));
         } else {
            verbToMenu.addVerb(new OrganizeApplications$OrganizeApplicationsVerb(this, 614656, 23));
            verbToMenu.addVerb(new OrganizeApplications$OrganizeApplicationsVerb(this, 268500992, 22));
         }

         verbToMenu.addVerb(new OrganizeApplications$OrganizeApplicationsVerb(this, 268501008, 151));
      }
   }

   @Override
   public final void focusChanged(Field field, int eventType) {
      if (this._iconArea.moveApplicationInProgress()) {
         this._iconArea.updateMovingApplicationPosition();
      }
   }

   static final MainScreen access$100(OrganizeApplications x0) {
      return x0._mainScreen;
   }

   static final MainScreen access$600(OrganizeApplications x0) {
      return x0._mainScreen;
   }

   static final MainScreen access$700(OrganizeApplications x0) {
      return x0._mainScreen;
   }
}
