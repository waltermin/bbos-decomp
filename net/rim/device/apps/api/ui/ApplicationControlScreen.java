package net.rim.device.apps.api.ui;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.ScreenUiEngineAttachedListener;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntVector;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PleaseWaitDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public class ApplicationControlScreen extends AppsMainScreen {
   private ApplicationRegistry _ar;
   private ApplicationControlScreen$TreeExpansionState _tes;
   protected final ApplicationControlScreen$DefaultMenuItem SAVE_MI = new ApplicationControlScreen$DefaultMenuItem(this, 0);
   private final ApplicationControlScreen$DefaultMenuItem SET_AS_DEFAULT_MI = new ApplicationControlScreen$DefaultMenuItem(this, 1);
   private final ApplicationControlScreen$DefaultMenuItem USE_DEFAULTS_MI = new ApplicationControlScreen$DefaultMenuItem(this, 2);
   private final ApplicationControlScreen$DefaultMenuItem ALL_USE_DEFAULTS_MI = new ApplicationControlScreen$DefaultMenuItem(this, 3);
   private final ApplicationControlScreen$DefaultMenuItem EDIT_DEFAULTS_MI = new ApplicationControlScreen$DefaultMenuItem(this, 4);
   private final ApplicationControlScreen$DefaultMenuItem RESET_FW_PROMPTS_MI = new ApplicationControlScreen$DefaultMenuItem(this, 5);
   private final ApplicationControlScreen$DefaultMenuItem RESET_ALL_FW_PROMPTS_MI = new ApplicationControlScreen$DefaultMenuItem(this, 6);
   private final ApplicationControlScreen$DefaultMenuItem SHOW_ALL_MI = new ApplicationControlScreen$DefaultMenuItem(this, 7);
   private ApplicationControlScreen$ItemGroup[] _groups;
   private ApplicationControlScreen$ItemGroup _conns;
   private ApplicationControlScreen$ItemGroup _interact;
   private ApplicationControlScreen$ItemGroup _data;
   private ApplicationControlScreen$States _policySettings;
   private ApplicationControlScreen$States _userSettings;
   private ApplicationControlScreen$States _userDefaults;
   private boolean _policySettingPresent;
   private String _title;
   private int _moduleHandle;
   private int[] _moduleHandles;
   private byte[] _hash;
   private boolean _listModules;
   private long _suppliedPermissions;
   private boolean _werePermissionsSupplied;
   private boolean _showingAll;
   private boolean _cannotApplySupplied;
   private boolean _useDefaults;
   private boolean _atleastOneSuppliedPermissionSet;
   private boolean _isInternalConnectionHidden;
   private boolean _permissionsSavedEqualOrMorePermissive;
   ScreenUiEngineAttachedListener _closeListener;
   private static final long GUID;
   private static ResourceBundle _rb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");
   private static ResourceBundle _prb = ResourceBundle.getBundle(8732645638888225014L, "net.rim.device.internal.resource.PlatformSecurity");
   private static final long RESET_PROMPT_DELAY;
   protected static final String WML_HELP_FILE_NAME;
   private static final String WML_HELP_CONNECTIONS_GROUP = null;
   private static final String WML_HELP_INTERACTIONS_GROUP = null;
   private static final String WML_HELP_USERDATA_GROUP = null;
   private static final String ALLOW = _rb.getString(718);
   private static final String DENY = _rb.getString(719);
   private static final String PROMPT = _rb.getString(720);
   private static final String CUSTOM = _rb.getString(721);
   private static final String DEFAULT = _rb.getString(722);
   protected static final String[] ALLOW_DENY_A = new Object[]{ALLOW, DENY, ALLOW};
   protected static final String[] ALLOW_DENY_D = new Object[]{ALLOW, DENY, DENY};
   protected static final String[] ALLOW_PROMPT_DENY_A = new Object[]{ALLOW, PROMPT, DENY, ALLOW};
   protected static final String[] ALLOW_PROMPT_DENY_P = new Object[]{ALLOW, PROMPT, DENY, PROMPT};
   protected static final String[] ALLOW_PROMPT_DENY_D = new Object[]{ALLOW, PROMPT, DENY, DENY};
   private static final String[] ALLOW_CUSTOM_DENY = new Object[]{ALLOW, CUSTOM, DENY};
   protected static final String[] PROMPT_DENY_P = new Object[]{PROMPT, DENY, PROMPT};
   protected static final String[] PROMPT_DENY_D = new Object[]{PROMPT, DENY, DENY};
   private static final String[] DENY_ONLY = new Object[]{DENY};
   private static final String[] ALLOW_DEFAULT_DENY = new Object[]{ALLOW, DEFAULT, DENY};
   private static final String[] ALLOW_CUSTOM_DEFAULT_DENY = new Object[]{ALLOW, CUSTOM, DEFAULT, DENY};
   private static final String[] ALLOW_DENY = new Object[]{ALLOW, DENY};
   private static final String[] ALLOW_PROMPT_DENY = new Object[]{ALLOW, PROMPT, DENY};
   private static final String[] PROMPT_DENY = new Object[]{PROMPT, DENY};
   private static final String[] DENY_ALLOW = new Object[]{DENY, ALLOW};
   private static final String[] PROMPT_DENY_ALLOW = new Object[]{PROMPT, DENY, ALLOW};
   private static final String[] ALLOW_DENY_PROMPT = new Object[]{ALLOW, DENY, PROMPT};
   private static final String[] DENY_PROMPT = new Object[]{DENY, PROMPT};
   private static final boolean[] ONE_PLUS_ONE = new boolean[]{true, false};
   protected static final boolean[] TWO_PLUS_ONE = new boolean[]{true, true, false};
   protected static final boolean[] THREE_PLUS_ONE = new boolean[]{true, true, true, false};
   private static final boolean[] NONE_PLUS_ONE = new boolean[]{false};
   private static final boolean[] ONE_PLUS_NONE = new boolean[]{true};
   private static final boolean[] ALLOW_DENY_BN = new boolean[]{false, false};
   private static final boolean[] ALLOW_CUSTOM_DENY_BN = new boolean[]{false, false, false};
   private static final boolean[] ALLOW_DEFAULT_DENY_BA = new boolean[]{true, false, true};
   private static final boolean[] ALLOW_CUSTOM_DEFAULT_DENY_BA = new boolean[]{true, true, false, true};
   private static final String[] DEFAULT_MENU_ITEMS = new Object[]{
      CommonResource.getString(18),
      _rb.getString(714),
      _rb.getString(715),
      _rb.getString(716),
      _rb.getString(713),
      _rb.getString(717),
      _rb.getString(756),
      _rb.getString(757)
   };

   public ApplicationControlScreen() {
      this(0);
   }

   public ApplicationControlScreen(int moduleHandle) {
      this(moduleHandle, null);
   }

   public ApplicationControlScreen(int moduleHandle, ApplicationPermissions permsRequest) {
      super(0);
      this._moduleHandle = moduleHandle;
      this._moduleHandles = new int[]{moduleHandle};
      this._listModules = false;
      this._hash = new byte[20];
      this._ar = ApplicationRegistry.getApplicationRegistry();
      if ((this._tes = (ApplicationControlScreen$TreeExpansionState)this._ar.get(2594183781511917644L)) == null) {
         this._tes = new ApplicationControlScreen$TreeExpansionState(null);
      }

      if (permsRequest != null) {
         this._suppliedPermissions = ApplicationControl.getRequestedPermissions(permsRequest);
         this._werePermissionsSupplied = true;
      }

      this.populate();
      this.makeMenu();
      String moduleName = DEFAULT;
      if (this._moduleHandle != 0) {
         moduleName = CodeModuleManager.getModuleName(this._moduleHandle);
         CodeModuleManager.getModuleHash(this._moduleHandle, this._hash);
      }

      this.setTitle(moduleName);
   }

   public ApplicationControlScreen(int[] moduleHandles, String name) {
      this(moduleHandles, name, null);
   }

   public ApplicationControlScreen(int[] moduleHandles, String name, ApplicationPermissions permsRequest) {
      super(0);
      this._moduleHandle = moduleHandles[0];
      this._moduleHandles = moduleHandles;
      this._listModules = true;
      this._hash = new byte[20];
      this._ar = ApplicationRegistry.getApplicationRegistry();
      if ((this._tes = (ApplicationControlScreen$TreeExpansionState)this._ar.get(2594183781511917644L)) == null) {
         this._tes = new ApplicationControlScreen$TreeExpansionState(null);
      }

      if (permsRequest != null) {
         this._suppliedPermissions = ApplicationControl.getRequestedPermissions(permsRequest);
         this._werePermissionsSupplied = true;
      }

      this.populate();
      this.addModules();
      this.makeMenu();
      this.setTitle(name);
   }

   @Override
   public void setTitle(String name) {
      this._title = name;
      HorizontalFieldManager titleField = (HorizontalFieldManager)(new Object(36028797018963968L));
      LabelField title = (LabelField)(new Object(((StringBuffer)(new Object())).append(_rb.getString(759)).append(": ").append(name).toString()));
      titleField.add(title);
      if (this._policySettingPresent) {
         int x = 0;
         int y = title.getContentHeight() + this.getFont().getBaseline() - 5;
         titleField.add((Field)(new Object(x, y)));
      }

      super.setTitle(titleField);
   }

   public String getTitle() {
      return this._title;
   }

   protected void makeMenu() {
      this.addMenuItem(this.SAVE_MI);
      if (this._moduleHandle != 0) {
         this.addMenuItem(this.SET_AS_DEFAULT_MI);
         this.addMenuItem(this.USE_DEFAULTS_MI);
         this.addMenuItem(this.EDIT_DEFAULTS_MI);
         this.addMenuItem(this.RESET_FW_PROMPTS_MI);
      } else {
         this.addMenuItem(this.ALL_USE_DEFAULTS_MI);
         this.addMenuItem(this.RESET_ALL_FW_PROMPTS_MI);
      }

      if (this._werePermissionsSupplied && !this._showingAll) {
         this.addMenuItem(this.SHOW_ALL_MI);
      }

      this.setHelp("third_party_program_control");
   }

   protected void addModules() {
      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)(new Object());
      IntVector apps = (IntVector)(new Object());
      IntVector libs = (IntVector)(new Object());

      for (int i = 0; i < this._moduleHandles.length; i++) {
         int handle = this._moduleHandles[i];
         if (CodeModuleManager.isLibrary(handle)) {
            libs.addElement(handle);
         } else {
            apps.addElement(handle);
         }
      }

      if (!apps.isEmpty()) {
         vifm.add((Field)(new Object(_rb.getString(741))));

         for (int i = 0; i < apps.size(); i++) {
            int handle = apps.elementAt(i);
            vifm.add(new ApplicationControlScreen$ModuleField(this, handle), 5);
         }
      }

      if (!libs.isEmpty()) {
         vifm.add((Field)(new Object(_rb.getString(742))));

         for (int i = 0; i < libs.size(); i++) {
            int handle = libs.elementAt(i);
            vifm.add(new ApplicationControlScreen$ModuleField(this, handle), 5);
         }
      }

      if (!apps.isEmpty() || !libs.isEmpty()) {
         this.add((Field)(new Object()));
         this.add(vifm);
      }
   }

   protected void expandAllNodes() {
      this._conns.showItems();
      this._interact.showItems();
      this._data.showItems();
   }

   private void collectSettings() {
      this._policySettings = new ApplicationControlScreen$States();
      this._userSettings = new ApplicationControlScreen$States();
      this._userDefaults = new ApplicationControlScreen$States();

      for (int i = 0; i < this._groups.length; i++) {
         ApplicationControlScreen$ItemField[] fields = this._groups[i].getItems();

         for (int j = 0; j < fields.length; j++) {
            ApplicationControlScreen$ItemField f = fields[j];
            this._userDefaults.setState(f._allowFlag, ApplicationControl.getDefaultUserPermission(f._allowFlag) != 0);
            if (f._type == 3) {
               this._userDefaults.setState(f._promptFlag, ApplicationControl.getDefaultUserPermission(f._promptFlag) != 0);
            }

            for (int k = 0; k < this._moduleHandles.length; k++) {
               boolean policySettingPresent = false;
               int moduleHandle = this._moduleHandles[k];
               if (moduleHandle != 0) {
                  CodeModuleManager.getModuleHash(moduleHandle, this._hash);
                  policySettingPresent = ApplicationControl.isModuleSettingPresent(this._hash);
                  this._policySettingPresent |= policySettingPresent;
               }

               boolean policyAllowed = ApplicationControl.getPermittedPermission(moduleHandle, this._hash, f._allowFlag) != 0;
               if (k > 0) {
                  policyAllowed |= this._policySettings.getState(f._allowFlag);
               }

               this._policySettings.setState(f._allowFlag, policyAllowed);
               boolean settingAllowed = ApplicationControl.getUserPermission(moduleHandle, f._allowFlag) != 0;
               if (k > 0) {
                  settingAllowed |= this._userSettings.getState(f._allowFlag);
               }

               boolean policySettingApplied = false;
               if (policySettingPresent) {
                  if (!ApplicationControl.isUserSettingPresent(moduleHandle)) {
                     boolean defaultAllowed = ApplicationControl.isFlagSetBoolean(this._hash, f._allowFlag);
                     this._userSettings.setState(f._allowFlag, defaultAllowed);
                     policySettingApplied = true;
                  } else {
                     this._userSettings.setState(f._allowFlag, settingAllowed);
                     policySettingApplied = false;
                  }
               } else {
                  this._userSettings.setState(f._allowFlag, settingAllowed);
                  policySettingApplied = false;
               }

               boolean isSet = policySettingApplied | ApplicationControl.isUserPermissionSet(moduleHandle, f._allowFlag);
               if (k > 0) {
                  isSet |= this._userSettings.getNonDefault(f._allowFlag);
               }

               this._userSettings.setNonDefault(f._allowFlag, isSet);
               if (f._type == 3) {
                  boolean policyPrompt = ApplicationControl.getPermittedPermission(moduleHandle, this._hash, f._promptFlag) != 0;
                  if (k > 0) {
                     policyPrompt &= this._policySettings.getState(f._promptFlag);
                  }

                  this._policySettings.setState(f._promptFlag, policyPrompt);
                  boolean settingPrompt = ApplicationControl.getUserPermission(moduleHandle, f._promptFlag) != 0;
                  if (k > 0) {
                     settingPrompt &= this._userSettings.getState(f._promptFlag);
                  }

                  boolean policyPromptSettingApplied = false;
                  if (policySettingPresent) {
                     if (!ApplicationControl.isUserSettingPresent(moduleHandle)) {
                        this._userSettings.setState(f._promptFlag, policyPrompt);
                        policyPromptSettingApplied = true;
                     } else {
                        this._userSettings.setState(f._promptFlag, settingPrompt);
                        policyPromptSettingApplied = false;
                     }
                  } else {
                     this._userSettings.setState(f._promptFlag, settingPrompt);
                     policyPromptSettingApplied = false;
                  }

                  boolean promptSet = policyPromptSettingApplied | ApplicationControl.isUserPermissionSet(moduleHandle, f._promptFlag);
                  if (k > 0) {
                     promptSet |= this._userSettings.getNonDefault(f._promptFlag);
                  }

                  this._userSettings.setNonDefault(f._promptFlag, promptSet);
               }
            }

            if (this._werePermissionsSupplied && !this._useDefaults && ApplicationControl.isBitSet(this._suppliedPermissions, f._allowFlag)) {
               if (this._policySettingPresent) {
                  if (this._policySettings.getState(f._allowFlag)) {
                     this._userSettings.setState(f._allowFlag, true);
                     this._userSettings.setState(f._promptFlag, false);
                     this._userSettings.setNonDefault(f._allowFlag, true);
                     this._userSettings.setNonDefault(f._promptFlag, true);
                     this._atleastOneSuppliedPermissionSet = true;
                  } else if (f._type == 3 && this._policySettings.getState(f._promptFlag)) {
                     this._userSettings.setState(f._allowFlag, true);
                     this._userSettings.setState(f._promptFlag, true);
                     this._userSettings.setNonDefault(f._allowFlag, true);
                     this._userSettings.setNonDefault(f._promptFlag, true);
                     this._atleastOneSuppliedPermissionSet = true;
                  } else {
                     this._cannotApplySupplied = true;
                  }
               } else {
                  this._userSettings.setState(f._allowFlag, true);
                  this._userSettings.setState(f._promptFlag, false);
                  this._userSettings.setNonDefault(f._allowFlag, true);
                  this._userSettings.setNonDefault(f._promptFlag, true);
                  this._atleastOneSuppliedPermissionSet = true;
               }
            }
         }
      }
   }

   private void buildFields() {
      this._conns = new ApplicationControlScreen$ItemGroup(this, _rb.getString(723), WML_HELP_CONNECTIONS_GROUP);
      this._conns.addItem(new ApplicationControlScreen$ItemField(_prb.getString(19), ALLOW_DENY, 7));
      if (BluetoothME.isSupported()) {
         this._conns.addItem(new ApplicationControlScreen$ItemField(_prb.getString(0), ALLOW_DENY, 14));
      }

      if (Phone.isSupported()) {
         this._conns.addItem(new ApplicationControlScreen$ItemField(_prb.getString(15), ALLOW_PROMPT_DENY, 8, 9));
      }

      this._conns.addItem(new ApplicationControlScreen$ItemField(_prb.getString(12), ALLOW_PROMPT_DENY, 17, 18));
      if (this._moduleHandle != 0 && !ITPolicyInternal.isITPolicyEnabled()) {
         this._isInternalConnectionHidden = true;
      } else {
         this._conns.addItem(new ApplicationControlScreen$ItemField(_prb.getString(9), ALLOW_PROMPT_DENY, 3, 4));
      }

      this._conns.addItem(new ApplicationControlScreen$ItemField(_prb.getString(8), ALLOW_PROMPT_DENY, 5, 6));
      if (RadioInfo.areWAFsSupported(4)) {
         this._conns.addItem(new ApplicationControlScreen$ItemField(_prb.getString(20), ALLOW_PROMPT_DENY, 27, 28));
      }

      this._interact = new ApplicationControlScreen$ItemGroup(this, _rb.getString(724), WML_HELP_INTERACTIONS_GROUP);
      this._interact.addItem(new ApplicationControlScreen$ItemField(_prb.getString(7), ALLOW_DENY, 2));
      this._interact.addItem(new ApplicationControlScreen$ItemField(_prb.getString(2), ALLOW_DENY_PROMPT, 23, 24));
      this._interact.addItem(new ApplicationControlScreen$ItemField(_prb.getString(13), ALLOW_PROMPT_DENY, 31, 32));
      this._interact.addItem(new ApplicationControlScreen$ItemField(_prb.getString(14), ALLOW_DENY, 22));
      this._interact.addItem(new ApplicationControlScreen$ItemField(_prb.getString(18), ALLOW_DENY, 19));
      this._interact.addItem(new ApplicationControlScreen$ItemField(_prb.getString(4), ALLOW_DENY, 13));
      this._interact.addItem(new ApplicationControlScreen$ItemField(_prb.getString(1), ALLOW_DENY, 12));
      this._interact.addItem(new ApplicationControlScreen$ItemField(_prb.getString(17), ALLOW_PROMPT_DENY, 25, 26));
      this._interact.addItem(new ApplicationControlScreen$ItemField(_prb.getString(6), ALLOW_PROMPT_DENY, 29, 30));
      this._data = new ApplicationControlScreen$ItemGroup(this, _rb.getString(725), WML_HELP_USERDATA_GROUP);
      this._data.addItem(new ApplicationControlScreen$ItemField(_prb.getString(3), ALLOW_DENY, 10));
      this._data.addItem(new ApplicationControlScreen$ItemField(_prb.getString(16), ALLOW_DENY, 11));
      this._data.addItem(new ApplicationControlScreen$ItemField(_prb.getString(5), ALLOW_DENY, 21));
      this._data.addItem(new ApplicationControlScreen$ItemField(_prb.getString(10), ALLOW_DENY, 15));
      this._data.addItem(new ApplicationControlScreen$ItemField(_prb.getString(11), ALLOW_DENY, 16));
      this._groups = new ApplicationControlScreen$ItemGroup[0];
      if (this._conns.getNumberOfItems() > 0) {
         Arrays.add(this._groups, this._conns);
      }

      if (this._interact.getNumberOfItems() > 0) {
         Arrays.add(this._groups, this._interact);
      }

      if (this._data.getNumberOfItems() > 0) {
         Arrays.add(this._groups, this._data);
      }
   }

   private void populate() {
      String[] choices = null;
      boolean[] bolding = null;
      this.buildFields();
      this.collectSettings();

      for (int i = 0; i < this._groups.length; i++) {
         ApplicationControlScreen$ItemField[] fields = this._groups[i].getItems();

         for (int j = 0; j < fields.length; j++) {
            ApplicationControlScreen$ItemField field = fields[j];
            boolean allowed = this._policySettings.getState(field._allowFlag);
            if (field._type == 2) {
               if (allowed) {
                  choices = this.getChoicesBinary(this._userDefaults.getState(field._allowFlag));
                  bolding = this.getBoldingBinary();
               } else {
                  choices = DENY_ONLY;
                  bolding = !this._userDefaults.getState(field._allowFlag) ? NONE_PLUS_ONE : ONE_PLUS_NONE;
               }
            } else if (field._type == 3) {
               boolean prompted = this._policySettings.getState(field._promptFlag);
               if (allowed) {
                  if (prompted) {
                     choices = this.getChoicesTernary(this._userDefaults.getState(field._allowFlag), this._userDefaults.getState(field._promptFlag), false);
                     bolding = this.getBoldingTernary(false);
                  } else {
                     choices = this.getChoicesTernary(this._userDefaults.getState(field._allowFlag), this._userDefaults.getState(field._promptFlag), true);
                     bolding = this.getBoldingTernary(true);
                  }
               } else {
                  choices = DENY_ONLY;
                  bolding = !this._userDefaults.getState(field._allowFlag) ? NONE_PLUS_ONE : ONE_PLUS_NONE;
               }
            }

            field.setChoices(choices, bolding);
         }

         this._groups[i].update();
         this.add(this._groups[i]);
      }

      this.updateSelections();
      if (this._werePermissionsSupplied || this._tes._conns) {
         this._conns.showItems();
      }

      if (this._werePermissionsSupplied || this._tes._interact) {
         this._interact.showItems();
      }

      if (this._werePermissionsSupplied || this._tes._data) {
         this._data.showItems();
      }

      if (this._atleastOneSuppliedPermissionSet) {
         this.setDirty(true);
      } else {
         this.setAllClean();
      }

      if (this._cannotApplySupplied) {
         this.promptUserCannotApplySupplied();
      }
   }

   protected String[] getChoicesBinary(boolean state) {
      if (this._moduleHandle != 0) {
         return state ? ALLOW_DENY_A : ALLOW_DENY_D;
      } else {
         return state ? DENY_ALLOW : ALLOW_DENY;
      }
   }

   protected boolean[] getBoldingBinary() {
      return this._moduleHandle != 0 ? TWO_PLUS_ONE : ONE_PLUS_ONE;
   }

   protected String[] getChoicesTernary(boolean allowState, boolean promptState, boolean allow) {
      if (allow) {
         if (this._moduleHandle != 0) {
            if (allowState) {
               return promptState ? ALLOW_PROMPT_DENY_P : ALLOW_PROMPT_DENY_A;
            } else {
               return ALLOW_PROMPT_DENY_D;
            }
         } else if (allowState) {
            return promptState ? ALLOW_DENY_PROMPT : PROMPT_DENY_ALLOW;
         } else {
            return ALLOW_PROMPT_DENY;
         }
      } else if (this._moduleHandle != 0) {
         return promptState ? PROMPT_DENY_P : PROMPT_DENY_D;
      } else {
         return promptState ? DENY_PROMPT : PROMPT_DENY;
      }
   }

   protected boolean[] getBoldingTernary(boolean allow) {
      if (allow) {
         return this._moduleHandle != 0 ? THREE_PLUS_ONE : TWO_PLUS_ONE;
      } else {
         return this._moduleHandle != 0 ? TWO_PLUS_ONE : ONE_PLUS_ONE;
      }
   }

   private void promptUserCannotApplySupplied() {
      String message = _rb.getString(751);
      String[] options = CommonResource.getStringArray(10004);
      SimpleChoiceDialog scd = (SimpleChoiceDialog)(new Object(message, options, 0, Bitmap.getPredefinedBitmap(2), 134217728));
      scd.setCancelAllowed(true);
      BackgroundDialog.show(scd);
   }

   private void updateSelections() {
      for (int i = 0; i < this._groups.length; i++) {
         ApplicationControlScreen$ItemField[] fields = this._groups[i].getItems();

         for (int j = 0; j < fields.length; j++) {
            ApplicationControlScreen$ItemField field = fields[j];
            if (this._moduleHandle == 0) {
               field.setSelectedIndex(field._choices.length - 1);
            } else {
               boolean allowed = this._userSettings.getState(field._allowFlag);
               if (field._type == 2) {
                  if (this._userSettings.getNonDefault(field._allowFlag)) {
                     field.setSelectedIndex(allowed ? ALLOW : DENY);
                  } else if (field._choices == ALLOW_DENY_A || field._choices == ALLOW_DENY_D) {
                     field.setSelectedIndex(field._choices.length - 1);
                  }
               } else if (field._type == 3) {
                  boolean prompted = this._userSettings.getState(field._promptFlag);
                  if (this._userSettings.getNonDefault(field._allowFlag)) {
                     field.setSelectedIndex(!allowed ? DENY : (prompted ? PROMPT : ALLOW));
                  } else if (field._choices == ALLOW_PROMPT_DENY_A
                     || field._choices == ALLOW_PROMPT_DENY_P
                     || field._choices == ALLOW_PROMPT_DENY_D
                     || field._choices == PROMPT_DENY_P
                     || field._choices == PROMPT_DENY_D) {
                     field.setSelectedIndex(field._choices.length - 1);
                  }
               }
            }
         }
      }
   }

   private void updateSelections(int moduleHandle) {
      for (int i = 0; i < this._groups.length; i++) {
         ApplicationControlScreen$ItemField[] fields = this._groups[i].getItems();

         for (int j = 0; j < fields.length; j++) {
            ApplicationControlScreen$ItemField f = fields[j];
            if (f._type == 2) {
               boolean bool = ApplicationControl.getUserPermission(moduleHandle, f._allowFlag) != 0;
               if (moduleHandle == 0) {
                  f.setSelectedIndex(f._choices.length - 1);
               } else {
                  f.setSelectedIndex(bool ? ALLOW : DENY);
               }
            } else if (f._type == 3) {
               int ternary = this.getTernaryUserValue(moduleHandle, f._allowFlag, f._promptFlag);
               if (moduleHandle == 0) {
                  f.setSelectedIndex(f._choices.length - 1);
               } else {
                  f.setSelectedIndex(ternary == 0 ? ALLOW : (ternary == 2 ? PROMPT : DENY));
               }
            }
         }
      }
   }

   private int getTernaryUserValue(int moduleHandle, int allowFlag, int promptFlag) {
      if (ApplicationControl.getUserPermission(moduleHandle, allowFlag) != 0) {
         return ApplicationControl.getUserPermission(moduleHandle, promptFlag) != 0 ? 2 : 0;
      } else {
         return 1;
      }
   }

   @Override
   protected boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               Field f = this.getLeafFieldWithFocus();
               if (f instanceof ApplicationControlScreen$ModuleField) {
                  ApplicationControlScreen$ModuleField m = (ApplicationControlScreen$ModuleField)f;
                  this.editModulePermissions(m._handle);
                  return true;
               }
         }
      }

      return handled;
   }

   private boolean applySettings(int moduleHandle, ApplicationPermissions permissions) {
      byte[] hash = new byte[20];
      return moduleHandle != 0 && !CodeModuleManager.getModuleHash(moduleHandle, hash)
         ? false
         : ApplicationControl.setModuleUserPermission(hash, moduleHandle, permissions);
   }

   private void commitSettings(int moduleHandle) {
      ApplicationPermissions permissions = this.getSelectedPermissions();
      boolean resetRequired = false;
      resetRequired = this.applySettings(moduleHandle, permissions);
      if (moduleHandle == 0) {
         resetRequired |= ApplicationControl.reloadDefaultModulePermissions();
      }

      if (resetRequired) {
         ApplicationControl.scheduleDeviceReset("USR", 3600000);
      }
   }

   private void commitApplicationSettings() {
      ApplicationPermissions permissions = this.getSelectedPermissions();
      boolean resetRequired = false;

      for (int k = 0; k < this._moduleHandles.length; k++) {
         int moduleHandle = this._moduleHandles[k];
         if (moduleHandle != 0) {
            resetRequired |= this.applySettings(moduleHandle, permissions);
         }
      }

      if (resetRequired) {
         ApplicationControl.scheduleDeviceReset("USR", 3600000);
      }
   }

   protected ApplicationPermissions getSelectedPermissions() {
      long permissions = 0;
      long selectedFlags = 0;
      ApplicationControlScreen$ItemField externalConnectionField = null;

      for (int i = 0; i < this._groups.length; i++) {
         ApplicationControlScreen$ItemField[] fields = this._groups[i].getItems();

         for (int j = 0; j < fields.length; j++) {
            ApplicationControlScreen$ItemField f = fields[j];
            int index = f.getSelectedIndex();
            if (f.isBold(index)) {
               selectedFlags |= ApplicationControl.getPermissionFlags(f._allowFlag);
            }

            if (this.isAllowSelected(f)) {
               permissions |= ApplicationControl.getPermissionFlags(f._allowFlag);
            }

            if (f._type == 3 && this.isPromptSelected(f)) {
               permissions |= ApplicationControl.getPermissionFlags(f._allowFlag, f._promptFlag);
            }

            if (f._allowFlag == 5) {
               externalConnectionField = f;
            }
         }
      }

      if (externalConnectionField != null && this._isInternalConnectionHidden) {
         int index = externalConnectionField.getSelectedIndex();
         if (externalConnectionField.isBold(index)) {
            selectedFlags |= ApplicationControl.getPermissionFlags(3);
         }

         if (this.isAllowSelected(externalConnectionField)) {
            permissions |= ApplicationControl.getPermissionFlags(3);
         }

         if (this.isPromptSelected(externalConnectionField)) {
            permissions |= ApplicationControl.getPermissionFlags(3, 4);
         }
      }

      return ApplicationControl.buildPermissions(permissions, selectedFlags);
   }

   private boolean isAllowSelected(ApplicationControlScreen$ItemField item) {
      return item.getChoice(item.getSelectedIndex()) == ALLOW;
   }

   private boolean isPromptSelected(ApplicationControlScreen$ItemField item) {
      return item.getChoice(item.getSelectedIndex()) == PROMPT;
   }

   @Override
   public void save() {
      PleaseWaitDialog pwd = (PleaseWaitDialog)(new Object(new ApplicationControlScreen$SavingThread(this, null)));
      pwd.display();
      this._permissionsSavedEqualOrMorePermissive = true;
      if (this._werePermissionsSupplied) {
         this._permissionsSavedEqualOrMorePermissive = this.didSaveEqualOrMorePermissiveThanRequested();
      }
   }

   private boolean didSaveEqualOrMorePermissiveThanRequested() {
      boolean equalOrMorePermissive = true;

      for (int i = 0; i < this._groups.length && equalOrMorePermissive; i++) {
         ApplicationControlScreen$ItemField[] fields = this._groups[i].getItems();

         for (int j = 0; j < fields.length; j++) {
            ApplicationControlScreen$ItemField f = fields[j];
            if (ApplicationControl.isBitSet(this._suppliedPermissions, f._allowFlag) && !this.isAllowSelected(f)) {
               equalOrMorePermissive = false;
               break;
            }
         }
      }

      return equalOrMorePermissive;
   }

   @Override
   public void close() {
      this.alertCloseListener();
      if (!this._werePermissionsSupplied) {
         this.saveTreeExpansionState();
      }

      super.close();
   }

   public void setCloseListener(ScreenUiEngineAttachedListener listener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private void alertCloseListener() {
      if (this._closeListener != null) {
         this._closeListener.onScreenUiEngineAttached(this, false);
      }
   }

   private void saveTreeExpansionState() {
      if (this._tes != null) {
         this._tes._conns = this._conns._expanded;
         this._tes._interact = this._interact._expanded;
         this._tes._data = this._data._expanded;
         this._ar.replace(2594183781511917644L, this._tes);
      }
   }

   private void setAllClean() {
      for (int i = this._groups.length - 1; i >= 0; i--) {
         ApplicationControlScreen$ItemField[] fs = this._groups[i].getItems();

         for (int j = fs.length - 1; j >= 0; j--) {
            fs[j].setDirty(false);
         }
      }

      this.setDirty(false);
   }

   private boolean aGroupIsDirty() {
      boolean dirty = false;

      for (int i = this._groups.length - 1; i >= 0; i--) {
         if (this._groups[i].isDirty()) {
            return true;
         }
      }

      return dirty;
   }

   public boolean werePermissionsSavedEqualOrMorePermissive() {
      return this._werePermissionsSupplied ? this._permissionsSavedEqualOrMorePermissive : true;
   }

   private void editDefaults() {
      if (!this.aGroupIsDirty() || this.onSavePrompt()) {
         new ApplicationControlInformation(0).open();
         this.getScreen().deleteAll();
         this.populate();
         if (this._listModules) {
            this.addModules();
         }
      }
   }

   private void editModulePermissions(int handle) {
      if (!this.aGroupIsDirty() || this.onSavePrompt()) {
         this.saveTreeExpansionState();
         new ApplicationControlInformation(handle).open();
         this.getScreen().deleteAll();
         this.populate();
         this.addModules();
      }
   }
}
