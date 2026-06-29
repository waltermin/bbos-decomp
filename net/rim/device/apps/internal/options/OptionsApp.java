package net.rim.device.apps.internal.options;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ValidationProvider;
import net.rim.device.apps.api.options.OptionsContext;
import net.rim.device.apps.api.options.OptionsListItem;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.setupwizard.SetupWizardRegistration;
import net.rim.device.apps.internal.options.items.AutoTextOptionsItem;
import net.rim.device.apps.internal.options.items.network.ManualModeNetworkListener;
import net.rim.device.apps.internal.options.items.network.ModemCmdListener;
import net.rim.device.apps.internal.options.items.network.SimCardStatusHandler;
import net.rim.device.internal.deviceoptions.AutoOnOff;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;
import net.rim.tid.awt.im.InputContext;

public final class OptionsApp extends UiApplication implements GlobalEventListener {
   private Comparator _comparator = new OptionsApp$OptionsItemComparator();
   private LongHashtable _groups;
   private boolean _isSureType;
   private static OptionsApp _app;
   private static Object _context = new Object(3);
   private static final long SELECTED_INDEX_ID_KEY = -7960768038040625127L;
   private static final long DATE_TIME_OPTIONS_SCREEN_KEY = 6173420044896290124L;
   public static final long NETWORK_OPTIONS_SCREEN_KEY = -908968825740058750L;
   public static final long EVENT_LOGGER_GUID = -4272982832973947638L;

   public static final void main(String[] args) {
      String className = null;
      if (args != null && args.length == 1) {
         if (args[0].equals("init")) {
            initializeOptionsApp();
            initializeOptionsShortcuts();
            System.exit(0);
         } else if (args[0] instanceof Object) {
            className = args[0];
         }
      }

      EventLogger.register(-4272982832973947638L, "net.rim.optionsapp", 2);
      _app = new OptionsApp(className);
      _app.enterEventDispatcher();
   }

   private static final void initializeOptionsApp() {
      OptionsProviderRegistration.registerOptionsProvider(new OptionsApp$DefaultDeviceOptionsProvider());
      SetupWizardRegistration.registerWizardProvider(new OptionsApp$OptionsWizardPageProvider());
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      ApplicationDescriptor descript = ApplicationDescriptor.currentApplicationDescriptor();
      reg.put(6173420044896290124L, new OptionsApp$DateTimeOptionsVerb());
      reg.put(-908968825740058750L, new OptionsApp$DisplayOptionRunnable(descript, "net.rim.device.apps.internal.options.items.network.NetworkOptionsItem"));
      initializeOptions();
      ApplicationEntryPoint.setPropertyOverride("net_rim_bb_options_app.Options", 7, Boolean.FALSE);
   }

   private static final void initializeOptions() {
      AutoTextOptionsItem.initializeAutoTextOptions();
      AutoOnOff.init();
      Proxy proxy = Proxy.getInstance();
      if (SIMCard.isSupported()) {
         SIMCard.addListener(proxy, new SimCardStatusHandler());
      }

      proxy.addRadioListener(new ModemCmdListener());
      ManualModeNetworkListener.getInstance();
   }

   private static final void initializeOptionsShortcuts() {
      RibbonLauncher ribbon = RibbonLauncher.getInstance();
      if (ribbon != null) {
         ribbon.unregisterAction("net_rim_bb_options_app.ScreenKeyboard");
         ribbon.unregisterAction("net_rim_bb_options_app.Network");
         ribbon.unregisterAction("net_rim_bb_options_app.CustomWordlist");
         ribbon.unregisterAction("net_rim_bb_options_app.BluetoothConfig");
         ApplicationDescriptor currentAppDescriptor = ApplicationDescriptor.currentApplicationDescriptor();
         if (CodeModuleManager.getModuleHandle("net_rim_bb_options_app") != 0) {
            if (Display.isColor()) {
               ApplicationDescriptor newDescriptor = (ApplicationDescriptor)(new Object(
                  currentAppDescriptor,
                  "ScreenKeyboard",
                  new String[]{"net.rim.device.apps.internal.options.items.ScreenKeyboardOptionsItem"},
                  null,
                  200,
                  "net.rim.device.apps.internal.resource.Options",
                  1100
               ));
               ApplicationEntryPoint newEntryPoint = (ApplicationEntryPoint)(new Object(newDescriptor));
               ribbon.registerAction("net_rim_bb_options_app.ScreenKeyboard", newEntryPoint);
               if (RadioInfo.areWAFsSupported(-5)) {
                  newDescriptor = (ApplicationDescriptor)(new Object(
                     currentAppDescriptor,
                     "Network",
                     new String[]{"net.rim.device.apps.internal.options.items.network.NetworkOptionsItem"},
                     null,
                     200,
                     "net.rim.device.apps.internal.resource.Options",
                     900
                  ));
                  newEntryPoint = (ApplicationEntryPoint)(new Object(newDescriptor));
                  ribbon.registerAction("net_rim_bb_options_app.Network", newEntryPoint);
               }

               boolean isSureType = InputContext.getInstance().getActiveInputMethodID() == 4096;
               if (CodeModuleManager.getModuleHandle("net_rim_bb_options_fastEuropean") > 0 && isSureType) {
                  newDescriptor = (ApplicationDescriptor)(new Object(
                     currentAppDescriptor,
                     "CustomWordlist",
                     new String[]{"net.rim.device.apps.internal.options.items.FastEuropean.FastEuropeanOptionsItem"},
                     null,
                     200,
                     "net.rim.device.apps.internal.resource.Options",
                     1491
                  ));
                  newEntryPoint = (ApplicationEntryPoint)(new Object(newDescriptor));
                  ribbon.registerAction("net_rim_bb_options_app.CustomWordlist", newEntryPoint);
               }

               boolean disabledByITPolicy = ITPolicy.getBoolean(34, 1, false);
               boolean isBluetoothCapable = InternalServices.isDeviceCapable(8);
               if (CodeModuleManager.getModuleHandle("net_rim_bluetooth") > 0 & !disabledByITPolicy & isBluetoothCapable) {
                  newDescriptor = (ApplicationDescriptor)(new Object(
                     currentAppDescriptor,
                     "BluetoothConfig",
                     new String[]{"net.rim.device.apps.internal.bluetooth.BluetoothMainScreen"},
                     null,
                     200,
                     "net.rim.device.apps.internal.resource.Options",
                     1968
                  ));
                  newEntryPoint = (ApplicationEntryPoint)(new Object(newDescriptor));
                  ribbon.registerAction("net_rim_bb_options_app.BluetoothConfig", newEntryPoint);
               }
            }
         }
      }
   }

   private OptionsApp(String className) {
      this.initializeOptionGroups();
      this.populateOptionsItemsList();
      if (className == null) {
         this.initializeUI();
         this.getGroup(1888231790844671165L).pushScreen();
         this.addGlobalEventListener(this);
      } else if (!this.openSingleItem(className)) {
         System.exit(1);
      }

      this._isSureType = InputContext.getInstance(false).isSureType();
   }

   private final OptionsApp$OptionGroup getGroup(long groupId) {
      OptionsApp$OptionGroup group = (OptionsApp$OptionGroup)this._groups.get(groupId);
      if (group == null) {
         throw new Object(((StringBuffer)(new Object("OptionsApp: Illegal group id: "))).append(groupId).toString());
      } else {
         return group;
      }
   }

   private final void initializeOptionGroups() {
      this._groups = (LongHashtable)(new Object(3));
      this._groups.put(1888231790844671165L, new OptionsApp$OptionGroup(1888231790844671165L, -1, true));
      this._groups.put(-1514481539159318190L, new OptionsApp$OptionGroup(-1514481539159318190L, 1954, false));
      this._groups.put(5294015899860238835L, new OptionsApp$OptionGroup(5294015899860238835L, 1955, false));
   }

   private final void initializeUI() {
      Enumeration e = this._groups.elements();

      while (e.hasMoreElements()) {
         OptionsApp$OptionGroup group = (OptionsApp$OptionGroup)e.nextElement();
         group.createMainScreen();
      }
   }

   private final void populateOptionsItemsList() {
      Enumeration e = this._groups.elements();

      while (e.hasMoreElements()) {
         OptionsApp$OptionGroup group = (OptionsApp$OptionGroup)e.nextElement();
         group.resetList();
      }

      Vector optionsProviders = OptionsProviderRegistration.getOptionsProviders();
      ContextObject context = OptionsContext.getContextObject();

      for (int i = optionsProviders.size() - 1; i >= 0; i--) {
         OptionsProviderRegistration$OptionsProvider provider = (OptionsProviderRegistration$OptionsProvider)optionsProviders.elementAt(i);
         if (provider != null) {
            Vector optionsItems = provider.getOptionsItems();
            if (optionsItems != null) {
               for (int j = optionsItems.size() - 1; j >= 0; j--) {
                  Object item = optionsItems.elementAt(j);
                  if (!(item instanceof Object) || ((ValidationProvider)item).isValid(context)) {
                     long groupId = 1888231790844671165L;
                     if (item instanceof Object) {
                        groupId = ((OptionsListItem)item).getGroupId();
                     }

                     this.getGroup(groupId).addItem(item);
                  }
               }
            }
         }
      }

      this.getGroup(1888231790844671165L).addItem(new OptionsApp$PushSubScreenItem(this, 1952, -1514481539159318190L));
      this.getGroup(1888231790844671165L).addItem(new OptionsApp$PushSubScreenItem(this, 1953, 5294015899860238835L));
      Enumeration ex = this._groups.elements();

      while (ex.hasMoreElements()) {
         OptionsApp$OptionGroup group = (OptionsApp$OptionGroup)ex.nextElement();
         group.finalizeAddItems(this._comparator);
      }
   }

   private final boolean openSingleItem(String className) {
      Vector optionsProviders = OptionsProviderRegistration.getOptionsProviders();
      Object item = null;

      for (int i = optionsProviders.size() - 1; i >= 0; i--) {
         OptionsProviderRegistration$OptionsProvider provider = (OptionsProviderRegistration$OptionsProvider)optionsProviders.elementAt(i);
         item = this.checkVectorForClass(provider.getOptionsItems(), className);
         if (item != null) {
            break;
         }
      }

      if (item == null) {
         item = this.checkVectorForClass(className);
      }

      if (item != null) {
         Screen screen = new OptionsApp$ExitOnExposedScreen();
         this.pushScreen(screen);
         openItem(item, new Object());
         return true;
      } else {
         return false;
      }
   }

   private final Object checkVectorForClass(String className) {
      Enumeration e = this._groups.elements();

      while (e.hasMoreElements()) {
         OptionsApp$OptionGroup group = (OptionsApp$OptionGroup)e.nextElement();
         Vector vec = group.getListItems();
         Object result = this.checkVectorForClass(vec, className);
         if (result != null) {
            return result;
         }
      }

      return null;
   }

   private final Object checkVectorForClass(Vector vec, String className) {
      if (vec == null) {
         return null;
      }

      for (int i = vec.size() - 1; i >= 0; i--) {
         Object obj = vec.elementAt(i);
         if (obj.getClass().getName().equals(className)) {
            return obj;
         }
      }

      return null;
   }

   private final void closeDeviceOptions() {
      this.removeGlobalEventListener(this);
      Enumeration e = this._groups.elements();

      while (e.hasMoreElements()) {
         OptionsApp$OptionGroup group = (OptionsApp$OptionGroup)e.nextElement();
         group.shutDown();
      }

      System.exit(0);
   }

   private static final boolean openItem(Object item, Object context) {
      if (!(item instanceof Object)) {
         return false;
      }

      ActionProvider actionProvider = (ActionProvider)item;
      return actionProvider.perform(6099736323056465049L, context);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      boolean isSureType = InputContext.getInstance().isSureType();
      if (guid == -7464003439710973532L || guid == -8040378802380461050L && isSureType != this._isSureType) {
         if ((data0 & 1) == 0) {
            if (guid == -8040378802380461050L) {
               initializeOptionsShortcuts();
            }

            OptionsApp$OptionGroup basicGroup = this.getGroup(1888231790844671165L);
            basicGroup.close(false);
            this.populateOptionsItemsList();
            this.initializeUI();
            basicGroup.pushScreen();
            basicGroup.localeChanged();
         }

         HotKeys.clearHotKeys();
         RIMGlobalMessagePoster.postGlobalEvent(-273986034351666339L, 0, 0, null, null);
         this._isSureType = isSureType;
      }

      if (guid == 2573494863350550132L) {
         initializeOptionsShortcuts();
         OptionsApp$OptionGroup basicGroup = this.getGroup(1888231790844671165L);
         basicGroup.close(false);
         this.populateOptionsItemsList();
         this.initializeUI();
         basicGroup.pushScreen();
      }
   }
}
