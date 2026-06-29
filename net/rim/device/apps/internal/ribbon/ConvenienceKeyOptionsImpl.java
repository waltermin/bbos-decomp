package net.rim.device.apps.internal.ribbon;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.FolderEntryPointDescriptor;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;
import net.rim.device.internal.ui.UiOptionsRegistry;

public final class ConvenienceKeyOptionsImpl extends ConvenienceKeyOptionsProvider {
   private final String[] _optionStringIDArray = new String[]{"FrontConvenienceKey", "SideConvenienceKey"};
   private final String[] _defaultStringIDArray = new String[]{"net_rim_bb_browser_daemon.default", "net_rim_bb_profiles_app.Profiles"};
   private ObjectChoiceField[] _choiceFields;
   private ConvenienceKeyOptionsImpl$ApplicationEntryWrapper[] _appList = new ConvenienceKeyOptionsImpl$ApplicationEntryWrapper[0];
   private Object _appListLock = new Object();
   private static final long GUID = 4869070239850015547L;
   static final String NOTHING_OPTION = "null";
   static final String DEFAULT_OPTION = "default";
   static final String APPLICATION_SWITCHER = "app-switcher";
   static final String POWER_OFF_ENTRY = "net_rim_PowerOff";
   static final String RADIO_OFF_ENTRY = "net_rim_Radio";
   static final String SINGLE_BROWSER_ENTRY = "net_rim_bb_browser_daemon.single";
   static final String DEFAULT_KEY_1_NAME = "net_rim_bb_browser_daemondefault";
   private static final int TYPE_CONVENIENCE_KEY_1 = 0;
   private static final int TYPE_CONVENIENCE_KEY_2 = 1;
   private static final int KEY_CLICK = 0;
   private static final int KEY_CLICK_AND_HOLD = 1;
   private static final String LOCK_CONVENIENCE_KEY_1 = "LockFrontConvenienceKey";
   private static final String LOCK_CONVENIENCE_KEY_2 = "LockSideConvenienceKey";
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   private static Comparator _comparator = new ConvenienceKeyOptionsImpl$1();

   private static final boolean isConvenienceKeyLocked(int type) {
      Theme currentTheme = ThemeManager.getActiveTheme();
      if (currentTheme != null) {
         switch (type) {
            case -1:
               break;
            case 0:
            default:
               if (currentTheme.getOption("LockFrontConvenienceKey") != null) {
                  return true;
               }
               break;
            case 1:
               if (currentTheme.getOption("LockSideConvenienceKey") != null) {
                  return true;
               }
         }
      }

      return false;
   }

   private final boolean isConvenienceKeySupported(int type) {
      switch (type) {
         case -1:
            return false;
         case 0:
         default:
            if (!Keypad.hasFrontConvenienceKey() && !Keypad.hasRightSideConvenienceKey()) {
               return false;
            }

            return true;
         case 1:
            return !DirectConnect.isSupported() && Keypad.hasLeftSideConvenienceKey();
      }
   }

   private final ConvenienceKeyOptionsImpl$ConvenienceKeyItem[] getConvenienceKeyItems() {
      ConvenienceKeyOptionsImpl$ConvenienceKeyItem[] keys = new ConvenienceKeyOptionsImpl$ConvenienceKeyItem[0];
      if (!isConvenienceKeyLocked(0) && this.isConvenienceKeySupported(0)) {
         Arrays.add(keys, new ConvenienceKeyOptionsImpl$ConvenienceKeyItem(Keypad.hasRightSideConvenienceKey() ? 156 : 89, 0));
      }

      if (!isConvenienceKeyLocked(1) && this.isConvenienceKeySupported(1)) {
         Arrays.add(keys, new ConvenienceKeyOptionsImpl$ConvenienceKeyItem(155, 1));
      }

      return keys;
   }

   private final String getConvenienceKey(int type) {
      return this.getConvenienceKey(type, 0);
   }

   private final String getConvenienceKey(int type, int action) {
      if (!this.isConvenienceKeySupported(type)) {
         return "null";
      }

      long paramName = getConvenienceKeyOptionParamName(type, action);
      String key = UiOptionsRegistry.getInstance().getString(paramName);
      if (key == null && action != 0) {
         paramName = getConvenienceKeyOptionParamName(type, 0);
         key = UiOptionsRegistry.getInstance().getString(paramName);
      }

      if (isConvenienceKeyLocked(type) || key == null) {
         if (this.getConvenienceKeyDefault(type) == null) {
            key = "null";
         } else {
            key = "default";
         }
      }

      return key;
   }

   private static final long getConvenienceKeyOptionParamName(int type, int action) {
      switch (type) {
         case -1:
            break;
         case 0:
         default:
            switch (action) {
               case -1:
                  return 0;
               case 0:
               default:
                  return 2233945566378975683L;
               case 1:
                  return -8314535069799645276L;
            }
         case 1:
            switch (action) {
               case -1:
                  break;
               case 0:
               default:
                  return -2722801897997978716L;
               case 1:
                  return -5923542525557144557L;
            }
      }

      return 0;
   }

   private static final long getConvenienceKeyArgumentParamName(int type, int action) {
      switch (type) {
         case -1:
            break;
         case 0:
         default:
            switch (action) {
               case -1:
                  return 0;
               case 0:
               default:
                  return 4095133221719688519L;
               case 1:
                  return 3617424389142429872L;
            }
         case 1:
            switch (action) {
               case -1:
                  break;
               case 0:
               default:
                  return -1668923100586784160L;
               case 1:
                  return -5864237357067334068L;
            }
      }

      return 0;
   }

   private final String getConvenienceKeyDefault(int type) {
      String default_entry = ThemeManager.getActiveTheme().getOption(this._optionStringIDArray[type]);
      return default_entry != null ? default_entry : this._defaultStringIDArray[type];
   }

   @Override
   public final void onThemeChangeEvent(int themeChangedActionFlag) {
      String key1Option = null;
      String key2Option = null;
      if (themeChangedActionFlag == 2) {
         key1Option = UiOptionsRegistry.getInstance().getString(2233945566378975683L);
         key2Option = UiOptionsRegistry.getInstance().getString(-2722801897997978716L);
      } else {
         key1Option = this.getConvenienceKey(0);
         key2Option = this.getConvenienceKey(1);
      }

      if (this.isConvenienceKeySupported(0)) {
         setConvenienceKey(0, key1Option);
      }

      if (this.isConvenienceKeySupported(1)) {
         setConvenienceKey(1, key2Option);
      }
   }

   private static final void setConvenienceKey(int type, String app) {
      setConvenienceKey(type, 0, app, null);
   }

   private static final void setConvenienceKey(int type, int action, String appName, String appArg) {
      if (isConvenienceKeyLocked(type)) {
         label61:
         try {
            Object options = ConvenienceKeyOptionsProvider.getInstance();
            appName = ((ConvenienceKeyOptionsImpl)options).getConvenienceKeyDefault(type);
         } finally {
            break label61;
         }
      }

      long appKey;
      long appKeyArg;
      UiOptionsRegistry reg;
      reg = UiOptionsRegistry.getInstance();
      appKey = 0;
      appKeyArg = 0;
      label58:
      switch (type) {
         case -1:
            return;
         case 0:
         default:
            switch (action) {
               case -1:
                  break label58;
               case 0:
               default:
                  appKey = 2233945566378975683L;
                  appKeyArg = 4095133221719688519L;
                  break label58;
               case 1:
                  appKey = -8314535069799645276L;
                  appKeyArg = 3617424389142429872L;
                  break label58;
            }
         case 1:
            switch (action) {
               case -1:
                  break;
               case 0:
               default:
                  appKey = -2722801897997978716L;
                  appKeyArg = -1668923100586784160L;
                  break;
               case 1:
                  appKey = -5923542525557144557L;
                  appKeyArg = -5864237357067334068L;
            }
      }

      if (appKey != 0 && appName != null) {
         reg.setString(appKey, appName);
         if (appArg != null && appArg.length() > 0) {
            reg.setString(appKeyArg, appArg);
            return;
         }

         String currentArg = reg.getString(appKeyArg);
         if (currentArg != null) {
            reg.setString(appKeyArg, null);
         }
      }
   }

   static final void register() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      if (applicationRegistry.getOrWaitFor(4869070239850015547L) == null) {
         ConvenienceKeyOptionsImpl instance = new ConvenienceKeyOptionsImpl();
         applicationRegistry.put(4869070239850015547L, instance);
         ConvenienceKeyOptionsProvider.register(instance);
         instance.onThemeChangeEvent(1);
      }
   }

   private final boolean isApplicationSwitcherOnConvenienceKey() {
      String key = this.getConvenienceKey(0, 0);
      if (key.equals("app-switcher")) {
         return true;
      }

      key = this.getConvenienceKey(1, 0);
      return key.equals("app-switcher");
   }

   private ConvenienceKeyOptionsImpl() {
      VerbFactoryRepository.addFactory(8522643724050848398L, new ConvenienceKeyOptionsImpl$SwitchApplicationVerbFactory(this));
   }

   private final Object getConvenienceKeyApp(int type, boolean rebuild) {
      return this.getConvenienceKeyApp(type, 0, rebuild);
   }

   private final Object getConvenienceKeyApp(int type, int action, boolean rebuild) {
      synchronized (this._appListLock) {
         if (!this.isConvenienceKeySupported(type)) {
            return null;
         }

         if (rebuild) {
            this.buildAppList();
         }

         String uniqueId = this.getConvenienceKey(type, action);
         if (uniqueId.equals("null")) {
            return null;
         }

         if (uniqueId.equals("default")) {
            return this.getSelectedObjectGivenId(this.getConvenienceKeyDefault(type));
         }

         if (uniqueId.equals("app-switcher")) {
            return new ConvenienceKeyOptionsImpl$InvokeTaskSwitcherRunnable();
         }

         Object appEntry = this.getSelectedObjectGivenId(uniqueId);
         if (appEntry != null) {
            return appEntry;
         }

         int handle = CodeModuleManager.getModuleHandle(uniqueId);
         if (handle == 0) {
            return null;
         }

         ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(handle);
         return descriptors != null && descriptors.length > 0 ? descriptors[0] : null;
      }
   }

   @Override
   public final Object getConvenienceKey1App(boolean rebuild) {
      return this.getConvenienceKeyApp(0, true);
   }

   @Override
   public final Object getConvenienceKey2App(boolean rebuild) {
      return this.getConvenienceKeyApp(1, true);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean invokeConvenienceKeyApp(int type, int action) {
      boolean result = true;
      Object application = this.getConvenienceKeyApp(type, action, true);
      long paramName = getConvenienceKeyArgumentParamName(type, action);
      String appArg = UiOptionsRegistry.getInstance().getString(paramName);

      try {
         if (!(application instanceof ApplicationEntry)) {
            if (application instanceof ApplicationDescriptor) {
               this.runApplication((ApplicationDescriptor)application, appArg);
            } else {
               if (!(application instanceof ConvenienceKeyOptionsImpl$InvokeTaskSwitcherRunnable)) {
                  return false;
               }

               ConvenienceKeyOptionsImpl$InvokeTaskSwitcherRunnable taskSwitcher = (ConvenienceKeyOptionsImpl$InvokeTaskSwitcherRunnable)application;
               taskSwitcher.setConvenienceKey(type + 1);
               taskSwitcher.run();
            }
         } else {
            ApplicationEntry appEntry = (ApplicationEntry)application;
            if (appArg != null && appArg.length() > 0) {
               ApplicationEntryPoint appEntryPoint = (ApplicationEntryPoint)appEntry.getDescriptor();
               ApplicationDescriptor appDescriptor = appEntryPoint.getApplicationDescriptor();
               this.runApplication(appDescriptor, appArg);
            } else {
               ((ApplicationEntry)application).invoke();
            }
         }
      } catch (Throwable var12) {
         Dialog.alert(t.getMessage());
         return result;
      }

      return result;
   }

   private final void runApplication(ApplicationDescriptor appDescriptor, String appArg) {
      ApplicationDescriptor desc = appDescriptor;
      if (appArg != null && appArg.length() > 0) {
         desc = new ApplicationDescriptor(appDescriptor, new String[]{appArg});
      }

      ApplicationManager.getApplicationManager().runApplication(desc);
   }

   @Override
   public final boolean invokeConvenienceKey1App() {
      return this.invokeConvenienceKeyApp(0, 0);
   }

   @Override
   public final boolean invokeConvenienceKey2App() {
      return this.invokeConvenienceKeyApp(1, 0);
   }

   @Override
   public final boolean invokeConvenienceKey2ClickAndHoldApp() {
      return this.invokeConvenienceKeyApp(1, 1);
   }

   private final String getConvenienceKeyOwner(int type) {
      String id = this.getConvenienceKey(type);
      return id.equals("default") ? this.getConvenienceKeyDefault(type) : id;
   }

   @Override
   public final String getConvenienceKey1Owner() {
      return this.getConvenienceKeyOwner(0);
   }

   @Override
   public final String getConvenienceKey2Owner() {
      return this.getConvenienceKeyOwner(1);
   }

   @Override
   public final void setConvenienceKey2App(String appName, String appArg) {
      setConvenienceKey(1, 0, appName, appArg);
   }

   @Override
   public final void setConvenienceKey2ClickAndHoldApp(String appName, String appArg) {
      setConvenienceKey(1, 1, appName, appArg);
   }

   @Override
   public final void populateMainScreen(MainScreen screen) {
      this.populateMainScreen(screen, screen);
   }

   protected final void populateMainScreen(MainScreen screen, Manager content) {
      synchronized (this._appListLock) {
         ConvenienceKeyOptionsImpl$ConvenienceKeyItem[] convenienceKeys = this.getConvenienceKeyItems();
         int length = convenienceKeys.length;
         if (length != 0) {
            this.buildAppList();
            this.sort();
            String nothing = _resources.getString(96);
            Arrays.insertAt(this._appList, new ConvenienceKeyOptionsImpl$ApplicationEntryWrapper(nothing, "null"), 0);
            content.add(new SeparatorField());
            this._choiceFields = new ObjectChoiceField[length];

            for (int i = 0; i < length; i++) {
               int resourceId;
               if (length == 1) {
                  resourceId = 101;
               } else {
                  resourceId = convenienceKeys[i].getResourceId();
               }

               this._choiceFields[i] = this.createChoiceField(convenienceKeys[i].getType(), resourceId, i > 0);
               this._choiceFields[i].setFont(screen.getFontIfSet());
               this._choiceFields[i].setCookie(convenienceKeys[i]);
               content.add(this._choiceFields[i]);
            }
         }
      }
   }

   private final ObjectChoiceField createChoiceField(int type, int resourceKey, boolean removeDefault) {
      String appLbl = "";
      String defaultLbl = null;
      ApplicationEntry app = this.getSelectedObjectGivenId(this.getConvenienceKeyDefault(type));
      if (app != null) {
         appLbl = " (" + app.getDescriptionNoHotkey() + ")";
      }

      defaultLbl = _resources.getString(147) + appLbl;
      if (removeDefault) {
         Arrays.removeAt(this._appList, 1);
      }

      Arrays.insertAt(this._appList, new ConvenienceKeyOptionsImpl$ApplicationEntryWrapper(defaultLbl, "default"), 1);
      int index = this.getSelectedIndexGivenId(this.getConvenienceKey(type));
      if (index == -2) {
         index = 1;
      } else if (index < 0) {
         index = 0;
      }

      return new ObjectChoiceField(_resources.getString(resourceKey), this._appList, index);
   }

   @Override
   public final void save() {
      if (this._choiceFields != null) {
         ConvenienceKeyOptionsImpl$ApplicationEntryWrapper wrapper = null;
         int index = -1;

         for (int i = 0; i < this._choiceFields.length; i++) {
            if (this._choiceFields[i] != null && this._choiceFields[i].isDirty()) {
               Object cookie = this._choiceFields[i].getCookie();
               if (!(cookie instanceof ConvenienceKeyOptionsImpl$ConvenienceKeyItem)) {
                  break;
               }

               ConvenienceKeyOptionsImpl$ConvenienceKeyItem key = (ConvenienceKeyOptionsImpl$ConvenienceKeyItem)cookie;
               index = this._choiceFields[i].getSelectedIndex();
               int type = key.getType();
               if (index == 0) {
                  setConvenienceKey(type, "null");
               } else if (index == 1) {
                  setConvenienceKey(type, "default");
               } else {
                  wrapper = (ConvenienceKeyOptionsImpl$ApplicationEntryWrapper)this._choiceFields[i].getChoice(index);
                  if (wrapper != null) {
                     setConvenienceKey(type, wrapper.getUniqueId());
                  }
               }
            }
         }

         this.releaseRefs();
      }
   }

   @Override
   public final void discard() {
      this.releaseRefs();
   }

   private final void releaseRefs() {
      this._choiceFields = null;
      this.clearAppList();
   }

   private final void addApplication(ApplicationEntry app) {
      if (!(app.getDescriptor() instanceof FolderEntryPointDescriptor)) {
         ConvenienceKeyOptionsImpl$ApplicationEntryWrapper wrapper = new ConvenienceKeyOptionsImpl$ApplicationEntryWrapper(app);
         if (wrapper.toString() != null) {
            String id = wrapper.getUniqueId();
            if (!id.equals("net_rim_PowerOff") && !id.equals("net_rim_Radio") && !id.equals("net_rim_bb_browser_daemon.single")) {
               Arrays.add(this._appList, wrapper);
            }
         }
      }
   }

   private final void buildAppList() {
      this.clearAppList();
      Hashtable folders = HierarchyManager.getInstance().getActiveFolders();
      if (folders != null) {
         Enumeration enumeration = folders.keys();

         while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            ApplicationEntry[] entries = HierarchyManager.getInstance().getAppsInFolder(key);
            if (entries != null) {
               for (int i = entries.length - 1; i >= 0; i--) {
                  this.addApplication(entries[i]);
               }
            }
         }
      }

      ApplicationEntry entry = HierarchyManager.getInstance().getTemporaryApplication("net_rim_bb_browser_daemondefault");
      if (entry != null && this.getSelectedIndexGivenId(entry.getPropertiesName()) < 0) {
         this.addApplication(entry);
      }

      if (HierarchyManager.isApplicationMenuValid()) {
         ApplicationEntry applicationMenu = HierarchyManager.getInstance().getApplicationEntry("net_rim_application_menu");
         if (applicationMenu != null) {
            this.addApplication(applicationMenu);
         }
      }

      Arrays.add(this._appList, new ConvenienceKeyOptionsImpl$ApplicationEntryWrapper(_resources.getString(154), "app-switcher"));
   }

   private final void clearAppList() {
      this._appList = new ConvenienceKeyOptionsImpl$ApplicationEntryWrapper[0];
   }

   private final void sort() {
      Arrays.sort(this._appList, _comparator);
   }

   private final ApplicationEntry getSelectedObjectGivenId(String uniqueId) {
      if (uniqueId != null) {
         for (int i = this._appList.length - 1; i >= 0; i--) {
            if (uniqueId.equals(this._appList[i].getUniqueId())) {
               return this._appList[i].getApplicationEntry();
            }
         }
      }

      return null;
   }

   private final int getSelectedIndexGivenId(String uniqueId) {
      if (uniqueId != null) {
         if (uniqueId.equals("null")) {
            return -1;
         }

         if (uniqueId.equals("default")) {
            return -2;
         }

         for (int i = this._appList.length - 1; i >= 0; i--) {
            if (uniqueId.equals(this._appList[i].getUniqueId())) {
               return i;
            }
         }
      }

      return -1;
   }
}
