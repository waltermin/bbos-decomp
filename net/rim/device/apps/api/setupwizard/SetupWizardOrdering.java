package net.rim.device.apps.api.setupwizard;

import net.rim.device.apps.api.setupwizard.categories.DevicePersonalizationWizardCategory;
import net.rim.device.apps.api.setupwizard.categories.DeviceSetupWizardCategory;

public class SetupWizardOrdering {
   public static final int INITIAL_PAGE;
   public static final int SETUP;
   public static final int PERSONALIZATION;
   public static final int EMAIL_SETUP;
   public static final int FINISHING_WIZARD;
   public static final int SHORTCUTS;
   public static final int APPLICATION_PERMISSIONS;
   public static final int TIPS_AND_TRICKS;
   public static final int LANGUAGE;
   public static final int DATE_AND_TIME;
   public static final int INTRO;
   public static final int SURETYPE_TUTORIAL;
   public static final int SIM_IMPORT;
   public static final int OWNER_INFORMATION;
   public static final int THEME;
   public static final int FONT;
   public static final int CONVENIENCE_KEYS;
   public static final WizardCategory LANGUAGE_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory DATE_AND_TIME_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory INTRO_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory SURETYPE_TUTORIAL_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory SIM_IMPORT_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory OWNER_INFORMATION_CATEGORY = DevicePersonalizationWizardCategory.getInstance();
   public static final WizardCategory FONT_CATEGORY = DevicePersonalizationWizardCategory.getInstance();
   public static final WizardCategory THEME_CATEGORY = DevicePersonalizationWizardCategory.getInstance();
   public static final WizardCategory CONVENIENCE_KEYS_CATEGORY = DevicePersonalizationWizardCategory.getInstance();
   public static final WizardCategory EMAIL_SETUP_CATEGORY = null;
   public static final WizardCategory TIPS_AND_TRICKS_CATEGORY = null;
   public static final WizardCategory SHORTCUTS_CATEGORY = null;
   public static final WizardCategory INITIAL_PAGE_CATEGORY = null;
   public static final WizardCategory FINISHING_WIZARD_CATEGORY = null;
}
