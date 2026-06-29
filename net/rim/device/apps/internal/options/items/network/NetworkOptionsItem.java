package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GAN;
import net.rim.device.api.system.GANConnectivityPreference;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.ModemListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.iota.IOTAManager;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.DataServices;
import net.rim.device.internal.system.NetworkInfo;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.SystemIcon;
import net.rim.vm.Message;

public final class NetworkOptionsItem
   extends SaveableMainScreenOptionsListItem
   implements GlobalEventListener,
   RadioStatusListener,
   ModemListener,
   ListFieldCallback,
   FieldChangeListener,
   SIMCardStatusListener {
   private ObjectChoiceField _dataServicesField;
   private Field _GANConnectivityPreferenceField;
   private NetworkOptionsItem$ActiveNetworkField _activeNetworkField;
   private ObjectChoiceField _selectionModeField;
   private ObjectChoiceField _autoSelectHomeNetworkField;
   private NetworkOptionsItem$NetworkModeFieldProvider _networkModeFieldProvider;
   private NetworkOptionsItem$MyList _networkListField;
   private ButtonField _multiButton;
   private Field _ribbonBanner;
   private MainScreen _mainScreen;
   private VerticalFieldManager _netListPanel;
   private int[] _rtfOffsets;
   private byte[] _rtfAttributes;
   private Font[] _rtfFonts;
   private NetworkInfo[] _networkInfos;
   private int[] _indexToPref;
   private int[] _indexToNetSelMode;
   private int[] _indexToNetMode;
   private int _oldSignalLevel;
   private long _lastScanTimestamp;
   private ResourceBundleFamily _rb;
   private IOTAManager _iotaManager;
   private ManualModeNetworkListener _manualModeNetworkListener;
   private int _savedNetSelMode = -1;
   private int _savedNetMode;
   private int _savedAutoSelectHomeFlags;
   private static final int STRS_AUTOMATIC = 0;
   private static final int STRS_MANUAL = 1;
   private static final int STRS_HOME_ONLY = 2;
   private static final int STRS_AUTOMATIC_A = 3;
   private static final int STRS_AUTOMATIC_B = 4;
   private static final int STRS_EVDO_ONLY = 5;
   private static final int STRS_CDMA_ONLY = 6;
   private static final int STRS_ROAM_ONLY = 7;
   private static final int STRS_MODE_UMTS = 0;
   private static final int STRS_MODE_GPRS = 1;
   private static final int STRS_MODE_DUAL = 2;
   private static final int DS_MODE_ON = 0;
   private static final int DS_MODE_OFF = 1;
   private static final int DS_MODE_ROAM = 2;
   private static final int STRS_PROMPT = 0;
   private static final int STRS_YES = 1;
   private static final int STRS_NO = 2;
   private static final boolean _isSynchronousOperation = RadioInfo.getNetworkType() != 7;
   private static final int WORLD_PHONE_RADIOS = 3;
   private static final boolean CDMA_GSM_WORLD_PHONE = (RadioInternal.getSupportedRadios() & 3) == 3;
   private static final int SUPPORTED_RADIOS = RadioInternal.getSupportedRadios();

   public NetworkOptionsItem() {
      super(OptionsResources.getString(900));
      this.setBackdoorAltStatus(true);
      ContextObject.put(super._context, 244, "coverage");
      this._rb = OptionsResources.getResourceBundle();
      this._rtfOffsets = new int[3];
      this._rtfAttributes = new byte[2];
      this._rtfAttributes[1] = 1;
      this._rtfFonts = new Object[2];
      this._rtfFonts[1] = Font.getDefault().derive(1);
   }

   private final void setupDefaultFields() {
      if (DataServices.getInstance().getMode() != 0) {
         int index;
         switch (DataServices.getInstance().getMode()) {
            case 1:
               index = 0;
               break;
            case 2:
            default:
               index = 1;
               break;
            case 3:
               index = 2;
         }

         this._dataServicesField = (ObjectChoiceField)(new Object(OptionsResources.getString(1991), OptionsResources.getStringArray(1992), index));
      } else {
         this._dataServicesField = null;
      }

      this._activeNetworkField = new NetworkOptionsItem$ActiveNetworkField(this);
      this._manualModeNetworkListener = ManualModeNetworkListener.getInstance();
      if ((SUPPORTED_RADIOS & 1) != 0) {
         this._savedAutoSelectHomeFlags = this._manualModeNetworkListener.getFlags();
         int defChoice;
         if ((this._savedAutoSelectHomeFlags & 2) == 0) {
            defChoice = 0;
         } else if ((this._savedAutoSelectHomeFlags & 1) != 0) {
            defChoice = 1;
         } else {
            defChoice = 2;
         }

         this._autoSelectHomeNetworkField = (ObjectChoiceField)(new Object(OptionsResources.getString(2066), CommonResources.getStringArray(9174), defChoice));
         this._autoSelectHomeNetworkField.setChangeListener(this);
      }

      this._multiButton = (ButtonField)(new Object(this._rb.getString(1840), 12884967424L));
      this._multiButton.setChangeListener(this);
      RibbonBanner rb = RibbonBanner.getInstance();
      if (rb != null) {
         this._ribbonBanner = rb.getRibbonBanner();
      }
   }

   private final void setupGANConnectivityPreferenceField() {
      if (WLAN.isSupported() && GAN.isGANAllowed()) {
         GANConnectivityPreference connectionPref = GANConnectivityPreference.getInstance();
         String[] strs = this._rb.getStringArray(2033);
         if (connectionPref.getEditable()) {
            int[] map = connectionPref.getOptions();
            String[] args = new Object[map.length];
            int initialIndex = -1;

            for (int i = 0; i < map.length; i++) {
               args[i] = strs[map[i]];
               if (map[i] == connectionPref.getPreference()) {
                  initialIndex = i;
               }
            }

            this._indexToPref = map;
            this._GANConnectivityPreferenceField = (Field)(new Object(OptionsResources.getString(2034), args, initialIndex));
            this._GANConnectivityPreferenceField.setChangeListener(this);
         } else {
            this._GANConnectivityPreferenceField = (Field)(new Object(OptionsResources.getString(2034), strs[connectionPref.getPreference()]));
            this._GANConnectivityPreferenceField.setEditable(false);
         }
      } else {
         this._GANConnectivityPreferenceField = null;
      }
   }

   private final void setupNetworkSelectionModeField(boolean refresh) {
      if (this._savedNetSelMode == -1) {
         this._savedNetSelMode = RadioInternal.getNetworkSelectionMode();
      }

      String[] strs = this._rb.getStringArray(921);
      String[] args = new Object[0];
      int[] map = new int[0];
      int selectionModes = RadioInternal.getAvailableNetworkSelectionModes();
      this.addOption(selectionModes, 1, strs, args, map, 0, 0);
      this.addOption(selectionModes, 2, strs, args, map, 3, 1);
      this.addOption(selectionModes, 4, strs, args, map, 4, 2);
      this.addOption(selectionModes, 32, strs, args, map, 2, 5);
      this.addOption(selectionModes, 8, strs, args, map, 1, 3);
      this.addOption(selectionModes, 128, strs, args, map, 6, 7);
      this.addOption(selectionModes, 64, strs, args, map, 5, 6);
      this.addOption(selectionModes, 256, strs, args, map, 7, 8);
      this._indexToNetSelMode = map;
      if (args.length <= 1 && !CDMA_GSM_WORLD_PHONE) {
         if (map.length == 1) {
            this.setNetworkSelectionMode(map[0]);
         }

         if (refresh && this._mainScreen != null) {
            if (this._selectionModeField != null && this._selectionModeField.getManager() != null) {
               this._mainScreen.delete(this._selectionModeField);
            }

            if (this._multiButton != null) {
               this.setMultiButtonVisibility();
            }

            if (this._networkListField != null) {
               this.setNetListPanelTraits();
            }
         }

         this._selectionModeField = null;
      } else {
         int i = -1;
         int autoIndex = 0;
         int curMode = RadioInternal.getNetworkSelectionMode();
         i = map.length - 1;

         while (true) {
            if (i >= 0) {
               if (map[i] == 0) {
                  autoIndex = i;
               }

               if (curMode != map[i]) {
                  i--;
                  continue;
               }
            }

            if (i < 0) {
               this.setNetworkSelectionMode(0);
               this._savedNetSelMode = 0;
               i = autoIndex;
            }

            if (!refresh || this._mainScreen == null) {
               this._selectionModeField = (ObjectChoiceField)(new Object(OptionsResources.getString(1450), args, i));
               this._selectionModeField.setChangeListener(this);
               return;
            }

            if (this._selectionModeField != null) {
               this._selectionModeField.setChoices(args);
               this._selectionModeField.setSelectedIndex(i);
            }

            if (this._multiButton != null) {
               this.setMultiButtonVisibility();
            }

            if (this._networkListField != null) {
               this.setNetListPanelTraits();
               return;
            }
            break;
         }
      }
   }

   private final void setupNetworkListField() {
      int activeNetworks = RadioInternal.getActiveRadios();
      if ((activeNetworks & 2) != 0) {
         this._networkInfos = NetworkOptionsUtils.getAvailableNetworks();
      } else if ((activeNetworks & 1) != 0 && System.currentTimeMillis() - this._lastScanTimestamp < 20000) {
         this._networkInfos = NetworkOptionsUtils.getAvailableNetworks();
      }

      this._networkListField = new NetworkOptionsItem$MyList(this, this._networkInfos != null ? this._networkInfos.length : 0);
      this._networkListField.setCallback(this);
      this._networkListField.setEmptyString(RadioInfo.getState() == 1 ? this._rb.getString(923) : this._rb.getString(1841), 68);
   }

   private final void addFields(MainScreen ms) {
      if (this._dataServicesField != null) {
         ms.add(this._dataServicesField);
         ms.add((Field)(new Object()));
      }

      if (this._GANConnectivityPreferenceField != null) {
         ms.add(this._GANConnectivityPreferenceField);
         ms.add((Field)(new Object()));
      }

      if (this._activeNetworkField != null) {
         ms.add(this._activeNetworkField);
      }

      if (this._selectionModeField != null) {
         ms.add(this._selectionModeField);
      }

      if (this._networkModeFieldProvider != null) {
         ms.add(this._networkModeFieldProvider.getField());
      }

      if (this._multiButton != null) {
         this.setMultiButtonVisibility();
      }

      if (this._networkListField != null) {
         this._netListPanel = (VerticalFieldManager)(new Object());
         this._netListPanel.add((Field)(new Object()));
         this._netListPanel.add((Field)(new Object(this._rb.getString(922), 12884901952L)));
         this._netListPanel.add(this._networkListField);
         this.setNetListPanelTraits();
      }
   }

   private final void setNetListPanelTraits() {
      boolean visible;
      boolean focusable;
      if ((SUPPORTED_RADIOS & 1) != 0) {
         visible = this.canUserSelectNetwork() && this._networkInfos != null;
         focusable = visible;
      } else if ((SUPPORTED_RADIOS & 2) != 0) {
         visible = true;
         focusable = false;
      } else {
         visible = false;
         focusable = false;
      }

      if (visible && this._netListPanel.getManager() == null) {
         this._mainScreen.add(this._netListPanel);
         this._networkListField.invalidate();
      } else if (!visible && this._netListPanel.getManager() != null) {
         boolean hadFocus = this._mainScreen.getLeafFieldWithFocus() == this._networkListField;
         this._mainScreen.delete(this._netListPanel);
         if (hadFocus && this._selectionModeField != null) {
            this._selectionModeField.setFocus();
         }
      }

      this._networkListField.setFocusable(focusable);
   }

   private final void setMultiButtonVisibility() {
      boolean userCanSelectNetwork = this.canUserSelectNetwork();
      boolean visible = RadioInfo.getState() == 1 && userCanSelectNetwork;
      if (visible && this._multiButton.getManager() == null) {
         int index = this._mainScreen.getFieldCount();
         if (this._mainScreen.getField(index - 1) == this._netListPanel) {
            index--;
         }

         this._mainScreen.insert(this._multiButton, index);
      } else if (!visible && this._multiButton.getManager() != null) {
         this._mainScreen.delete(this._multiButton);
      }

      if (this._autoSelectHomeNetworkField != null) {
         if (userCanSelectNetwork && this._autoSelectHomeNetworkField.getManager() == null) {
            int insertPos = this._selectionModeField != null ? this._selectionModeField.getIndex() + 1 : this._activeNetworkField.getIndex() + 1;
            this._mainScreen.insert(this._autoSelectHomeNetworkField, insertPos);
            return;
         }

         if (!userCanSelectNetwork && this._autoSelectHomeNetworkField.getManager() != null) {
            boolean hadFocus = this._mainScreen.getLeafFieldWithFocus() == this._autoSelectHomeNetworkField;
            this._mainScreen.delete(this._autoSelectHomeNetworkField);
            if (hadFocus && this._selectionModeField != null) {
               this._selectionModeField.setFocus();
            }
         }
      }
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      String str;
      switch (backdoorCode) {
         case 1195462214:
            GAN.setGANOverride(false);
            str = "Show Options->GAN only when provisioned";
            break;
         case 1195462222:
            GAN.setGANOverride(true);
            str = "Show Options->GAN always";
            break;
         case 1464615238:
            WLAN.setWLANOverride(false);
            str = "Show Options->Wi-Fi only when provisioned";
            break;
         case 1464615246:
            WLAN.setWLANOverride(true);
            str = "Show Options->Wi-Fi always";
            break;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }

      Dialog.inform(str);
      return true;
   }

   @Override
   protected final Verb getSaveVerb() {
      return (this._selectionModeField == null || !this._selectionModeField.isDirty())
            && (this._dataServicesField == null || !this._dataServicesField.isDirty())
            && (this._networkModeFieldProvider == null || !this._networkModeFieldProvider.isDirty())
            && (this._GANConnectivityPreferenceField == null || !this._GANConnectivityPreferenceField.isDirty())
            && (this._autoSelectHomeNetworkField == null || !this._autoSelectHomeNetworkField.isDirty())
         ? null
         : super.getSaveVerb();
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      Verb defaultVerb = super.addCurrentItemVerbs(verbToMenu, instance);
      if (PrefNetworkListOptions.isFeatureSupported() && (this.manualSelectionModeEnabled() || CDMA_GSM_WORLD_PHONE && RadioInternal.getEnabledRadios() == 1)) {
         if (RadioInfo.getState() == 1 && this._mainScreen.getFieldWithFocus() == this._activeNetworkField && instance != 65536) {
            int index = RadioInfo.getCurrentNetworkIndex();
            if (index != -1 && (RadioInternal.getNetworkCategory(index) & 4) == 0) {
               verbToMenu.addVerb(new NetworkOptionsItem$MyVerb(this, 1910, 16785669));
            }
         }

         verbToMenu.addVerb(new NetworkOptionsItem$MyVerb(this, 1872, 16785680));
      }

      this._iotaManager = IOTAManager.getInstance();
      if (this._iotaManager != null) {
         switch (this._iotaManager.currentStatus()) {
            case -1:
            case 1:
               break;
            case 0:
            case 2:
            default:
               verbToMenu.addVerb(new NetworkOptionsItem$MyVerb(this, 1921, 0));
               return defaultVerb;
            case 3:
               verbToMenu.addVerb(new NetworkOptionsItem$MyVerb(this, 1922, 0));
         }
      }

      return defaultVerb;
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._mainScreen = mainScreen;
      this._networkInfos = null;
      this.setupDefaultFields();
      this.setupNetworkSelectionModeField(false);
      if (RadioInfo.getNetworkType() == 7) {
         this._networkModeFieldProvider = new NetworkOptionsItem$UMTS_NetworkModeField(this);
      } else if (CDMA_GSM_WORLD_PHONE) {
         this._networkModeFieldProvider = new NetworkOptionsItem$CDMA_GSM_NetworkModeField(this);
      }

      this.setupGANConnectivityPreferenceField();
      this.setupNetworkListField();
      this.addFields(mainScreen);
      if (this._ribbonBanner != null) {
         mainScreen.setTitle(this._ribbonBanner);
         Manager mgr = this._ribbonBanner.getManager();
         if (mgr != null) {
            mgr.setTag(null);
         }
      }

      this.updateActiveNetworkString();
      this._oldSignalLevel = RadioInfo.getSignalLevel();
      Application app = Application.getApplication();
      app.addGlobalEventListener(this);
      app.addRadioListener(this);
      SIMCard.addListener(app, this);
      this.moveFocusToCorrectField();
      this.populateMenuFromRepository(mainScreen);
   }

   private final void populateMenuFromRepository(MainScreen mainScreen) {
      VerbRepository statusRepository = VerbRepository.getVerbRepository(1479696779947759213L);
      Verb[] verbs = statusRepository.getVerbs(null);
      if (verbs != null) {
         for (int i = verbs.length - 1; i >= 0; i--) {
            Verb verbToAdd = verbs[i];
            if (verbToAdd != null) {
               mainScreen.addMenuItem((MenuItem)(new Object(verbToAdd, Integer.MAX_VALUE)));
            }
         }
      }
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean result = super.confirm(verb, context);
      if (result) {
         Application app = Application.getApplication();
         app.removeGlobalEventListener(this);
         app.removeRadioListener(this);
         SIMCard.removeListener(app, this);
      }

      return result;
   }

   @Override
   protected final boolean save() {
      if (this._dataServicesField != null && this._dataServicesField.isDirty()) {
         int mode;
         switch (this._dataServicesField.getSelectedIndex()) {
            case 0:
               mode = 1;
               break;
            case 1:
            default:
               mode = 2;
               break;
            case 2:
               mode = 3;
         }

         DataServices.getInstance().setMode(mode);
      }

      if (this._GANConnectivityPreferenceField != null && this._GANConnectivityPreferenceField.isDirty()) {
         try {
            GANConnectivityPreference.getInstance().setPreference(this._indexToPref[((ChoiceField)this._GANConnectivityPreferenceField).getSelectedIndex()]);
         } finally {
            ;
         }
      }

      this._manualModeNetworkListener.setActive(true);
      return this.saveSelectionMode() && this.saveNetworkMode() && super.save();
   }

   @Override
   protected final boolean discard() {
      if (RadioInternal.getNetworkSelectionMode() != this._savedNetSelMode) {
         this.setNetworkSelectionMode(this._savedNetSelMode);
      }

      if (this._manualModeNetworkListener.getFlags() != this._savedAutoSelectHomeFlags) {
         this._manualModeNetworkListener.setFlags(this._savedAutoSelectHomeFlags);
      }

      if (this._networkModeFieldProvider != null) {
         this._networkModeFieldProvider.discard();
      }

      this._manualModeNetworkListener.setActive(true);
      return super.discard();
   }

   private final boolean canUserSelectNetwork() {
      boolean result = false;
      if (this.manualSelectionModeEnabled()) {
         result = this._selectionModeField == null || this._indexToNetSelMode[this._selectionModeField.getSelectedIndex()] == 3;
      }

      return result;
   }

   private final boolean manualSelectionModeEnabled() {
      boolean result = false;
      int modes = RadioInternal.getAvailableNetworkSelectionModes();
      if ((modes & 8) != 0) {
         result = true;
      }

      return result;
   }

   private final void updateActiveNetworkString() {
      this._activeNetworkField.setText(this.getActiveNetworkName());
   }

   private final boolean saveSelectionMode() {
      if (this._selectionModeField != null && this._selectionModeField.isDirty()) {
         int newSelMode = this._indexToNetSelMode[this._selectionModeField.getSelectedIndex()];
         if (newSelMode != RadioInternal.getNetworkSelectionMode()) {
            this.setNetworkSelectionMode(newSelMode);
         }

         if (_isSynchronousOperation) {
            this._savedNetSelMode = newSelMode;
            this._selectionModeField.setDirty(false);
         }
      }

      return true;
   }

   private final boolean saveNetworkMode() {
      if (this._networkModeFieldProvider != null) {
         this._networkModeFieldProvider.save();
      }

      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean saveNetwork(int index) {
      boolean var8 = false /* VF: Semaphore variable */;

      boolean var5;
      label47: {
         label46: {
            boolean var10;
            try {
               var8 = true;
               this._manualModeNetworkListener.setActive(false);
               int networkId = this._networkInfos[index].getNetworkId();
               SpinnerDialog d = new SpinnerDialog(2, index, networkId);
               switch (d.go()) {
                  case -1001:
                  case -1:
                     var5 = false;
                     var8 = false;
                     break label46;
                  case -1000:
                     Dialog.alert(OptionsResources.getString(1478));
                     var5 = false;
                     var8 = false;
                     break label47;
                  case 1:
                     String informStr;
                     if ((RadioInfo.getNetworkService() & 1) != 0) {
                        informStr = OptionsResources.getString(2039);
                     } else {
                        informStr = OptionsResources.getString(1817);
                     }

                     Dialog.inform(informStr);
               }

               var10 = true;
               var8 = false;
            } finally {
               if (var8) {
                  this._manualModeNetworkListener.setActive(true);
               }
            }

            this._manualModeNetworkListener.setActive(true);
            return var10;
         }

         this._manualModeNetworkListener.setActive(true);
         return var5;
      }

      this._manualModeNetworkListener.setActive(true);
      return var5;
   }

   private final void addOption(int flags, int flag, String[] strs, String[] args, int[] map, int str, int value) {
      if ((flags & flag) != 0) {
         Arrays.add(args, strs[str]);
         Arrays.add(map, value);
      }
   }

   private final String getActiveNetworkName() {
      String networkName = OptionsResources.getString(907);
      if (RadioInfo.getSignalLevel() != -256 && RadioInfo.getState() == 1) {
         int index = RadioInfo.getCurrentNetworkIndex();
         if (index != -1) {
            networkName = NetworkOptionsUtils.getAvailableNetworkName(index);
         }
      }

      return networkName;
   }

   private final void doScan() {
      if (NetworkOptionsUtils.scanForNetworks() == 1) {
         this._networkInfos = NetworkOptionsUtils.getAvailableNetworks();
         this._networkListField.setSize(this._networkInfos.length, this._networkListField.getSelectedIndex());
         this._lastScanTimestamp = System.currentTimeMillis();
         this.setNetListPanelTraits();
         if (this.canUserSelectNetwork() && this._networkListField.isVisible()) {
            this._networkListField.setFocus();
         }
      }
   }

   private final boolean manualSelectNetwork(int index) {
      boolean result = false;
      if (this.canUserSelectNetwork() && this._networkInfos != null && index >= 0 && index < this._networkInfos.length) {
         if ((this._networkInfos[index].getCategory() & 2) != 0 && Dialog.ask(3, this._rb.getString(1839)) != 4) {
            return false;
         }

         result = this.saveSelectionMode();
         if (result) {
            result = this.saveNetwork(index);
         }

         if ((RadioInternal.getActiveRadios() & 1) != 0) {
            this._networkInfos = null;
         }

         this.setNetListPanelTraits();
      }

      return result;
   }

   private final void moveFocusToCorrectField() {
      if (this.canUserSelectNetwork()) {
         if (this._networkInfos == null && this._multiButton != null) {
            if (this._multiButton.getManager() != null) {
               this._multiButton.setFocus();
            }
         } else if (this._netListPanel != null && this._netListPanel.getManager() != null) {
            this._networkListField.setFocus();
            return;
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7219683504990287771L) {
         this.updateActiveNetworkString();
      }
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.updateActiveNetworkString();
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.updateActiveNetworkString();
   }

   @Override
   public final void signalLevel(int level) {
      if (level == -256 && this._oldSignalLevel != -256) {
         this.updateActiveNetworkString();
      } else if (level != -256 && this._oldSignalLevel == -256) {
         this.updateActiveNetworkString();
      }

      this._oldSignalLevel = level;
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int status) {
   }

   @Override
   public final void networkSelectionModeChanged(int mode) {
      if ((1 << mode & RadioInternal.getAvailableNetworkSelectionModes()) != 0 && this._selectionModeField != null) {
         this._savedNetSelMode = mode;
         this._selectionModeField.setDirty(false);
         if (mode != this._indexToNetSelMode[this._selectionModeField.getSelectedIndex()]) {
            for (int i = this._selectionModeField.getSize() - 1; i >= 0; i--) {
               if (this._indexToNetSelMode[i] == mode) {
                  this._selectionModeField.setSelectedIndex(i);
                  this.setMultiButtonVisibility();
                  this.setNetListPanelTraits();
                  return;
               }
            }
         }
      }
   }

   @Override
   public final void queryNetworkDisplayName(int networkId) {
   }

   @Override
   public final void networkChangeResult(int pendingOperation, int result) {
      if (result != 1) {
         Status.show(OptionsResources.getString(1985));
         String logString = ((StringBuffer)(new Object("Ntwk chg resp="))).append(result).toString();
         EventLogger.logEvent(-4272982832973947638L, logString.getBytes(), 0);
      }

      switch (pendingOperation) {
         case 1593:
            int selectionMode = RadioInternal.getNetworkSelectionMode();
            if (selectionMode != this._indexToNetSelMode[this._selectionModeField.getSelectedIndex()]) {
               for (int i = this._selectionModeField.getSize() - 1; i >= 0; i--) {
                  if (this._indexToNetSelMode[i] == selectionMode) {
                     this._selectionModeField.setSelectedIndex(i);
                     break;
                  }
               }
            }

            this.setMultiButtonVisibility();
            this.setNetListPanelTraits();
            this.moveFocusToCorrectField();
            Message msg = (Message)(new Object(33, 1587, 1282, 28672, 0));
            msg.post();
         case 1592:
            return;
         case 1594:
         default:
            this._networkModeFieldProvider.update();
            this.setMultiButtonVisibility();
            this.setNetListPanelTraits();
      }
   }

   @Override
   public final void cardUpdated() {
      this.setupNetworkSelectionModeField(true);
   }

   @Override
   public final void cardInserted() {
   }

   @Override
   public final void cardReady() {
   }

   @Override
   public final void cardInvalid(int code, int subCode) {
   }

   @Override
   public final void cardFault(int code) {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int offset = 0;
      int catDrawnWidth = 0;
      NetworkInfo info = this._networkInfos[index];
      int cat = info.getCategory();
      String name = info.getName();
      int height = listField.getRowHeight();
      if (this.canUserSelectNetwork()) {
         int icon = 2;
         if (RadioInfo.getCurrentNetworkIndex() == index) {
            icon = 3;
         }

         offset += SystemIcon.COLLECTION.paint(graphics, 0, y, height, height, icon);
      }

      char ch;
      if ((cat & 4) != 0) {
         ch = 'H';
      } else if ((cat & 2) != 0) {
         ch = 'X';
      } else {
         ch = ' ';
      }

      width -= catDrawnWidth = graphics.drawText(ch, offset, y, 5, width - offset) - 5;
      if (RadioInfo.getNetworkType() == 7) {
         String mode;
         if ((cat & 64) != 0) {
            mode = OptionsResources.getStringArray(1970)[0];
         } else {
            mode = OptionsResources.getStringArray(1970)[1];
         }

         width -= graphics.drawText(mode, offset, y, 5, width - (offset + catDrawnWidth + 2)) - 5;
      }

      graphics.drawText(name, offset, y, 64, width);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this._mainScreen.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._networkInfos[index];
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   private final void setNetworkSelectionMode(int mode) {
      if (RadioInternal.getNetworkSelectionMode() != mode) {
         RadioInternal.setNetworkSelectionMode(mode);
         if (mode == 3 && RadioInfo.getState() == 1) {
            this.doScan();
         }

         if (_isSynchronousOperation) {
            Message msg = (Message)(new Object(33, 1587, 1282, 28672, 0));
            msg.post();
         }
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._selectionModeField) {
         int mode = this._indexToNetSelMode[this._selectionModeField.getSelectedIndex()];
         String warningMsg = null;
         switch (mode) {
            case 5:
               break;
            case 6:
            default:
               warningMsg = OptionsResources.getString(1974);
               break;
            case 7:
               warningMsg = OptionsResources.getString(1975);
         }

         if (warningMsg != null) {
            Dialog.alert(warningMsg);
         }

         this.setNetworkSelectionMode(mode);
         if (_isSynchronousOperation) {
            this.setMultiButtonVisibility();
            if (this._networkListField != null) {
               this.setNetListPanelTraits();
            }

            this.moveFocusToCorrectField();
            return;
         }
      } else {
         if (field == this._multiButton) {
            this._multiButton.setDirty(false);
            this.doScan();
            return;
         }

         if (this._autoSelectHomeNetworkField != null && field == this._autoSelectHomeNetworkField) {
            int selectedIndex = this._autoSelectHomeNetworkField.getSelectedIndex();
            int flags;
            if (selectedIndex == 0) {
               flags = 0;
            } else if (selectedIndex == 1) {
               flags = 3;
            } else {
               flags = 2;
            }

            this._manualModeNetworkListener.setFlags(flags);
            return;
         }

         if (field == this._GANConnectivityPreferenceField) {
            int selected = this._indexToPref[((ChoiceField)this._GANConnectivityPreferenceField).getSelectedIndex()];
            if (selected == 2 || selected == 0) {
               Dialog.inform(OptionsResources.getString(2070));
            }
         }
      }
   }

   @Override
   protected final boolean handleEndKey() {
      if (this.doCloseVerb()) {
         ApplicationManager.getApplicationManager().requestForegroundForConsole();
      }

      return true;
   }

   @Override
   public final boolean keyChar(char key, int time, int status) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final boolean keyUp(int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}
