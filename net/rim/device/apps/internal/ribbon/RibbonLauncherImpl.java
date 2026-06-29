package net.rim.device.apps.internal.ribbon;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.BackdoorKeyListener;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.PMEGraphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.quickcontact.ClickAndHoldKeyListener;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonApi;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.RibbonKeyListener;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ribbon.RibbonListener;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.api.ribbon.system.StandbyManager;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.apps.internal.profiles.ProfileQuickToggle;
import net.rim.device.apps.internal.ribbon.components.ComponentManager;
import net.rim.device.apps.internal.ribbon.components.ImageFactory;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationLauncherField;
import net.rim.device.apps.internal.ribbon.launcher.FolderEntryPointDescriptor;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;
import net.rim.device.apps.internal.ribbon.system.AggregatedNetworkProps;
import net.rim.device.apps.internal.ribbon.system.SystemActions;
import net.rim.device.apps.internal.ribbon.system.SystemMonitor;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.LockEventLogger;
import net.rim.device.internal.ui.ApplicationSwitcher;
import net.rim.device.internal.ui.Background;
import net.rim.device.internal.ui.IMSwitcherOption;
import net.rim.device.internal.ui.InputMethodSwitcher;
import net.rim.device.internal.ui.component.TraceBackDialog;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.layout.DefaultKeyLayout;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.tid.util.Utils;
import net.rim.vm.WeakReference;

final class RibbonLauncherImpl
   extends RibbonLauncher
   implements KeyListener,
   FocusChangeListener,
   GlobalEventListener,
   BackdoorKeyListener,
   FileSystemListener,
   FileSystemJournalListener {
   private UiApplication _app;
   private HomeScreen _ribbonAppScreen;
   private Screen _externalMainScreen;
   private Field _ribbonAppBanner;
   ApplicationLauncherField _applicationIconArea;
   private RibbonDescriptionField _description;
   private RibbonLauncherImpl$DescriptionUpdater _descriptionUpdater = new RibbonLauncherImpl$DescriptionUpdater(this);
   private RibbonLauncherImpl$ActionUpdater _actionUpdater = new RibbonLauncherImpl$ActionUpdater(this);
   private BackdoorKeyProcessor _backdoor;
   private ApplicationSwitcher _appSwitchScreen;
   private InputMethodSwitcher _imSwitchScreen;
   private Runnable _appSwitchEnd = new RibbonLauncherImpl$ApplicationSwitchEnd(this);
   private boolean _appLaunchesAllowed = true;
   private long _lastLaunchTime;
   private Vector _ribbonListeners;
   RibbonOptions _ribbonOptions;
   private boolean _compressedBanners = Display.getHeight() < 160;
   private boolean _hotKeysDisabled;
   HierarchyManager _hierarchyManager;
   private ClickAndHoldKeyListener _clickAndHoldKeyListener = new ClickAndHoldKeyListener(null);
   private Hashtable _homeScreenFactories;
   RibbonGlobalKeyListenerImpl _unhandledGlobalKeyListener = new RibbonGlobalKeyListenerImpl();
   private boolean _isReducedKeyboard = InternalServices.isReducedFormFactor();
   private char _keyChar;
   private long _myStoredUSN;
   private boolean _bkgrLoaded;
   String _longIdleModeText;
   String _longImmediateCellBroadcastText;
   private RibbonLauncherImpl$AppsUpdater _appsUpdater = new RibbonLauncherImpl$AppsUpdater(this, true);
   private RibbonLauncherImpl$AppsUpdater _iconUpdater = new RibbonLauncherImpl$AppsUpdater(this, false);
   static RibbonLauncherImpl _instanceImpl;
   public static final int CANCEL = -1;
   private static final long MIN_TIME_BETWEEN_LAUNCHES = 200L;
   private static final int NORMAL_BANNERS_SCREEN_HEIGHT = 160;
   private static boolean _showDateConfirmation;
   private static final int KEY_CHAR = 0;
   private static final int KEY_DOWN = 1;
   private static final int KEY_UP = 2;
   private static final int KEY_REPEAT = 3;

   final Screen getLayoutScreen() {
      Field layout = this.getLayout();
      if (layout == null) {
         ApplicationRegistry.getApplicationRegistry().remove(7112795868593646517L);
      } else {
         ApplicationRegistry.getApplicationRegistry().replace(7112795868593646517L, layout);
      }

      RIMGlobalMessagePoster.postGlobalEvent(7112795868593646517L);
      if (layout == null) {
         return null;
      }

      Screen screen = new RibbonLauncherImpl$4(this, 35184372088832L);
      screen.add(layout);
      screen.addKeyListener(this);
      return screen;
   }

   final Field getLayout() {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme == null) {
         return null;
      }

      ThemeAttributeSet attributes = theme.getAttributeSet(Tag.create("homescreen"));
      if (attributes == null) {
         return null;
      }

      ContextObject context = new ContextObject();
      ContextObject.put(context, 265370977573465368L, new Long(3476778912330022912L));
      return attributes.getLayout(context);
   }

   final void recreateMainScreen() {
      Screen screen = this._externalMainScreen;
      if (screen == null) {
         screen = this._ribbonAppScreen;
      }

      if (screen.getUiEngine() != null) {
         label25:
         try {
            this._app.popScreen(screen);
         } finally {
            break label25;
         }
      }

      this._app.invokeLater(new RibbonLauncherImpl$5(this));
   }

   final void activate() {
      this.updateApplicationDescription();
      if (_showDateConfirmation) {
         _showDateConfirmation = false;
         this.setAppLaunchesAllowed(false);
         ConfirmationDialog d = new ConfirmationDialog();
         d.show();
         this.setAppLaunchesAllowed(true);
      }

      if (UiApplication.getUiApplication().getActiveScreen() == this._ribbonAppScreen) {
         this._ribbonAppScreen.onAppActivated();
      }
   }

   public final void deactivate() {
      this.setAppLaunchesAllowed(true);
   }

   final void populateRibbon() {
      synchronized (this._applicationIconArea) {
         this._applicationIconArea.setShowHiddenApps(this._ribbonOptions.getShowHiddenApps());
         this._applicationIconArea.loadApplications();
         this.updateApplicationDescription();
         this._hierarchyManager.fireOnEntryChange(null);
      }
   }

   final boolean internalActivateFolder(String folderName) {
      boolean result = this._applicationIconArea.goToFolder(folderName);
      if (result) {
         this.updateApplicationDescription();
         this.setAppLaunchesAllowed(true);
      }

      return result;
   }

   final void showApplicationEntry(ApplicationEntry applicationEntry, boolean show) {
      this._hierarchyManager.showApplication(applicationEntry, show);
      this.populateRibbon();
   }

   public final boolean getDisableHotKeys() {
      return this._hierarchyManager.getDisableHotKeys();
   }

   final void completeMoveApplication(boolean doMove) {
      if (this._applicationIconArea.moveApplicationInProgress()) {
         this._applicationIconArea.completeMoveApplication(doMove);
         this.updateApplicationDescription();
         if (doMove) {
            this._ribbonOptions.commit();
         }
      }
   }

   final void moveIcon() {
      this._applicationIconArea.beginMoveApplication();
      this.updateApplicationDescription();
   }

   final void launch() {
      this.launch(this._applicationIconArea.getSelectedApplication());
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1161904965:
         case 1163084626:
            ApplicationManager am = ApplicationManager.getApplicationManager();

            try {
               am.runApplication(((ApplicationManagerInternal)am).getEngScreenDescriptor());
               return true;
            } finally {
               ;
            }
         case 1246451286:
            int cause = 0;
            RibbonNetworkInfo ni = RibbonNetworkInfo.getInstance();
            int[] causeArray = (int[])ni.getNetworkPropsCollection().get(-7072296818759564103L);
            if (causeArray != null && causeArray.length > 0) {
               cause = causeArray[0];
            }

            Dialog.ask(0, RibbonResources.getString(54) + cause);
            return true;
         case 1279740999:
            EventLogger.startEventLogViewer();
            return true;
         case 1280265811:
            RibbonApi.toggleLogONS();
            return true;
         case 1313688652:
            ComponentManager.toggleDisplayMode();
            return true;
         case 1330533185:
            SystemMonitor.getSystemMonitor().setONSRenderMode(0);
            return true;
         case 1330533186:
            SystemMonitor.getSystemMonitor().setONSRenderMode(1);
            return true;
         case 1330533205:
            SystemMonitor.getSystemMonitor().setONSRenderMode(2);
            return true;
         case 1397838416:
            QuincyManager qm = QuincyManager.getInstance();
            if (qm != null) {
               qm.sendReport(qm.getReport(687180051664214235L, 1), false, true);
               return true;
            }

            return true;
         default:
            return false;
      }
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      if (this._isReducedKeyboard && Keypad.key(keycode) == 261 && ProfileQuickToggle.handleKeyUp()) {
         return true;
      }

      if (!this._clickAndHoldKeyListener.keyUp(keycode, time)) {
         char key = DeviceInfo.isSimulator() ? this._keyChar : Keypad.map(keycode);
         if (this.notifyRibbonKeyListeners(Keypad.map(keycode), keycode, Keypad.status(keycode), time, 0)) {
            return true;
         } else {
            return this._hotKeysDisabled ? false : this.processRibbonHotKey(key, Keypad.status(keycode));
         }
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      if (this._isReducedKeyboard) {
         if (Keypad.key(keycode) == 20) {
            LockEventLogger.logLockEvent(1282567787);
            SystemActions.lock();
            return true;
         }

         if (Keypad.key(keycode) == 261) {
            return ProfileQuickToggle.handleKeyRepeat(time);
         }
      }

      if (this._clickAndHoldKeyListener.keyRepeat(keycode, time)) {
         this.notifyRibbonKeyListeners('\u0000', keycode, 0, time, 3);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final void focusChanged(Field field, int action) {
      if (action == 1 || action == 2) {
         this.updateApplicationDescription();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 3596208183088439728L || guid == 8877632280522743328L || guid == 7207871974803693937L) {
         UIDGenerator.resetSeed();
      } else if (guid == 256826950193107649L) {
         this.moduleListChanged();
      } else if (guid == -4232371946002803201L) {
         this._hierarchyManager.moduleDeleted();
         this.moduleListChanged();
      } else {
         if (guid == 7563637690172082503L) {
            if (this._appSwitchScreen != null) {
               this._appSwitchScreen.selectNext(data0);
               return;
            }

            if (this._imSwitchScreen == null) {
               this._appSwitchScreen = new ApplicationSwitcher(this._appSwitchEnd, data1);
               return;
            }
         } else if (guid == -8249535590121003989L) {
            if (this._imSwitchScreen != null) {
               this._imSwitchScreen.selectNext(data0);
               return;
            }

            if (this._appSwitchScreen == null) {
               boolean applyLock = false;
               if (IMSwitcherOption.getInstance().getState() != 3) {
                  Locale[] imLocales = Utils.getAvailableInputLocales(true);
                  if (imLocales.length > 1 && InputContext.getInstance().isIMSwitchAllowed()) {
                     this._imSwitchScreen = new InputMethodSwitcher(imLocales, Utils.getInputLocalesDisplayNames(imLocales), false, this._appSwitchEnd);
                  } else {
                     applyLock = true;
                  }
               } else {
                  applyLock = true;
               }

               if (applyLock && UiApplication.getUiApplication().isForeground()) {
                  this.onKeyChar('\n', 1, 0);
                  return;
               }
            }
         } else if (guid == 9056933960126321432L) {
            if (object0 instanceof String) {
               TraceBackDialog.show(this._app, (String)object0, object1);
               return;
            }
         } else {
            if (guid == -7464003439710973532L) {
               this._hierarchyManager.resetApplicationDescriptions();
               if (this._description != null) {
                  this._description.applyTheme();
               }

               this.updateApplicationDescription();
               return;
            }

            if (guid == 8508406279413621091L || guid == -594020114676189989L) {
               this.populateRibbon();
               return;
            }

            if (guid == 6345609069135580235L) {
               if (PersistentContent.isEncryptionEnabled()) {
                  this.updateApplicationDescription();
               }

               this._applicationIconArea.resetFocusToTop();
               return;
            }

            if (guid == -4220058463650496006L || guid == 8288627527798139133L || guid == 2522898683889177438L) {
               ServiceBookIndicator sbi = ServiceBookIndicator.getInstance();
               if (sbi != null) {
                  sbi.updateIndicator();
                  return;
               }
            } else if (guid == 4169543157268029527L) {
               setShowDateConfirmationFlag(true);
               if (this._app.isForeground()) {
                  this.activate();
                  return;
               }
            } else {
               if (guid == 7596964640041972456L) {
                  this.moduleListChanged();
                  this.updateBackgroundImage();
                  return;
               }

               if (guid == -4645495483836102462L) {
                  ThemeManager.resetDefaultFont();
                  this._ribbonOptions.setBackgroundImage(ThemeManager.getActiveName(), null, null);
                  this.updateBackgroundImage();
                  return;
               }

               if (guid == 2573494863350550132L) {
                  this.recreateMainScreen();
                  ConvenienceKeyOptionsProvider.getInstance().onThemeChangeEvent(data0);
                  return;
               }

               if (guid == 3536078662966037734L) {
                  this._hierarchyManager.setHotKeysDisabled(this._hotKeysDisabled);
               }
            }
         }
      }
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      String status = null;
      switch (backdoorCode) {
         case 1146309971:
            ApplicationMenu.dismiss();
            break;
         case 1179601988:
            this._hierarchyManager.toggleSubFolders();
            break;
         case 1229212741:
            RibbonNetworkInfo networkInfo = RibbonNetworkInfo.getInstance();
            ReadableLongMap networkProps = networkInfo.getNetworkPropsCollection();
            String idleText = (String)networkProps.get(-7608742199570488450L);
            if (idleText == null) {
               idleText = "The SIM shall supply a text string, which shall be displayed by the ME as an idle mode text if the ME is able to do it.  The presentation style is left as an implementation decision to the ME manufacturer.  The idle mode text shall be displayed in a manner that ensures that neither the network name nor the service providers name are affected.  If idle mode text is competing with other information to be displayed on the same area, for instance a CB message, the idle mode text shall be replaced by the other information.  It is up to the ME to restore the idle mode text when the other information has no longer to be displayed.";
            } else if (idleText.length() > 50) {
               idleText = "Short message";
            } else {
               idleText = null;
            }

            networkInfo.setIdleModeText(idleText);
            break;
         case 1347241295:
            String strDisable = PMEGraphics.isDisabled() ? "Enable" : "Disable";
            String strBounds = PMEGraphics.showBounds() ? "Hide Bounds" : "Show Bounds";
            String strClip = PMEGraphics.showClip() ? "Hide Clip" : "Show Clip";
            String strStats = PMEGraphics.logStats() ? "Disable Stats" : "Enable Stats";
            String[] choices = new String[]{strDisable, strBounds, strClip, strStats, "Cancel"};
            int[] values = new int[]{
               1,
               3,
               2,
               4,
               -1,
               -805044214,
               1718183726,
               1852845578,
               2663,
               -804651007,
               51,
               -805044095,
               944130375,
               1859895,
               8388636,
               0,
               754974719,
               0,
               1835036,
               -1939865088
            };
            Dialog dialogOptions = new Dialog("PMEGraphics Options", choices, values, 0, null);
            int choice = dialogOptions.doModal();
            PMEGraphics.toggleRegOption(choice);
            break;
         case 1381188948:
            this._hierarchyManager.resetHierarchies();
            break;
         case 1414024530:
            RibbonOptions.getOptions().getSyncItem().removeAllSyncObjects();
            ThemeManager.setActiveTheme(ThemeManager.getActiveName());
            break;
         default:
            return false;
      }

      if (status != null) {
         Dialog.inform(status);
      }

      return true;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      if (this._isReducedKeyboard && Keypad.key(keycode) == 20) {
         return true;
      } else if (Keypad.key(keycode) == 261) {
         ProfileQuickToggle.handleKeyDown(time);
         return true;
      } else if (Keypad.key(keycode) == 127 || Keypad.key(keycode) == 8) {
         DeleteVerb.getInstance(_instanceImpl).invoke(null);
         return true;
      } else if (this._externalMainScreen != null && Keypad.key(keycode) == 4098) {
         ApplicationMenu.show();
         return true;
      } else {
         this.notifyRibbonKeyListeners(Keypad.map(keycode), keycode, Keypad.status(keycode), time, 1);
         this._keyChar = 0;
         return this._backdoor.keyDown(keycode);
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      this._keyChar = key;
      return this._clickAndHoldKeyListener.keyChar(key, status, time) ? true : this.onKeyChar(key, status, time);
   }

   @Override
   public final void rootChanged(int type, String rootName) {
      if (!this._bkgrLoaded && type == 0) {
         BackgroundImage image = this._ribbonOptions.getBackgroundImage(ThemeManager.getActiveName());
         if (image != null) {
            String imageName = image._name;
            if (imageName != null && imageName.startsWith('/' + rootName)) {
               this.updateBackgroundImage();
            }
         }
      }
   }

   @Override
   public final void fileJournalChanged() {
      long nextUSN = FileSystemJournal.getNextUSN();
      BackgroundImage image = this._ribbonOptions.getBackgroundImage(ThemeManager.getActiveName());
      if (image != null) {
         String imageName = image._name;

         for (long lookUSN = nextUSN - 1; lookUSN >= this._myStoredUSN; lookUSN -= 1) {
            FileSystemJournalEntry entry = FileSystemJournal.getEntry(lookUSN);
            if (entry == null) {
               break;
            }

            String oldPath = entry.getOldPath();
            int type = entry.getEvent();
            if (FileUtilities.filenamesMatch(oldPath, imageName)) {
               if (type == 3 || type == 2) {
                  this.setBackgroundImage(entry.getPath(), image._properties);
               } else if (type == 1) {
                  this.setBackgroundImage(null, null);
               }
            }
         }
      }

      this._myStoredUSN = nextUSN;
   }

   @Override
   public final void addRibbonListener(RibbonListener listener) {
      this._ribbonListeners.addElement(new WeakReference(listener));
   }

   @Override
   public final void removeRibbonListener(RibbonListener listener) {
      synchronized (this._ribbonListeners) {
         for (int i = this._ribbonListeners.size() - 1; i >= 0; i--) {
            if (((WeakReference)this._ribbonListeners.elementAt(i)).get() == listener) {
               this._ribbonListeners.removeElementAt(i);
            }
         }
      }
   }

   @Override
   public final void registerAction(String name, EntryPointDescriptor descriptor) {
      System.out.println("HomeScreen registerAction " + name);
      ApplicationEntry application = new ApplicationEntry(descriptor, this._hotKeysDisabled);
      synchronized (this._app.getAppEventLock()) {
         this.completeMoveApplication(false);
         this._hierarchyManager.registerTemporaryApplication(name, application);
         this.iconListChanged();
      }
   }

   @Override
   public final void unregisterAction(String name) {
      System.out.println("HomeScreen unRegisterAction " + name);
      synchronized (this._app.getAppEventLock()) {
         this.completeMoveApplication(false);
         if (this._hierarchyManager.unregisterTemporaryApplication(name)) {
            this.iconListChanged();
         }
      }
   }

   @Override
   public final EntryPointDescriptor getRegisteredAction(String name) {
      ApplicationEntry application = this._hierarchyManager.getTemporaryApplication(name);
      return application != null ? application.getDescriptor() : null;
   }

   @Override
   public final EntryPointDescriptor getEntryPointDescriptor(String name) {
      ApplicationEntry application = this.getApplicationEntryFromURL(name);
      return application != null ? application.getDescriptor() : null;
   }

   public static final void setShowDateConfirmationFlag(boolean flag) {
      _showDateConfirmation = flag;
   }

   @Override
   public final void disableHotKeys(boolean disableHotKeys) {
      this._hotKeysDisabled = disableHotKeys;
      this._hierarchyManager.setHotKeysDisabled(disableHotKeys);
      this.updateApplicationDescription();
   }

   @Override
   public final void launch(String url) {
      ApplicationEntry entry = this.getApplicationEntryFromURL(url);
      if (entry != null) {
         this.launch(entry);
      } else {
         ApplicationLauncher.launch(url);
      }
   }

   @Override
   public final void setApplicationEntryPointProperty(String uniqueName, long propertyId, Object property) {
      this._hierarchyManager.setApplicationEntryPointProperty(uniqueName, propertyId, property);
   }

   @Override
   public final boolean isFirstBoot() {
      return SystemActions.isFirstBoot();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final ApplicationEntry getApplicationEntryFromURL(String id) {
      if (id.startsWith("slot")) {
         int position = 0;
         boolean var5 = false /* VF: Semaphore variable */;

         try {
            var5 = true;
            position = Integer.parseInt(id.substring(4));
            var5 = false;
         } finally {
            if (var5) {
               System.out.println("Invalid number format parsing position in RibbonLauncherImpl");
               return HierarchyManager.getInstance().getAppByIndexInFolder("", position);
            }
         }

         return HierarchyManager.getInstance().getAppByIndexInFolder("", position);
      } else {
         return HierarchyManager.getInstance().getApplicationEntry(id);
      }
   }

   private final void updateBackgroundImage() {
      String activeThemeName = ThemeManager.getActiveName();
      BackgroundImage image = this._ribbonOptions.getBackgroundImage(activeThemeName);
      Object object = null;
      String[] params = null;
      if (image != null) {
         object = image._name;
         params = image._properties;
      }

      this.setBackgroundImage(object, params);
   }

   @Override
   public final void setBackgroundImage(Object object, String[] params) {
      this.setBackgroundImage(object, params, true);
   }

   private final void setBackgroundImage(Object object, String[] params, boolean updateRibbonOptions) {
      String imageName = null;
      Theme theme = ThemeManager.getActiveTheme();
      if (!ThemeManager.allowUserWallpaper(ThemeManager.getActiveName())) {
         object = null;
      }

      if (object == null && theme != null && ThemeManager.getNameOfDefaulTheme().equals(ThemeManager.getActiveName())) {
         byte[] data = Branding.getData(2);
         if (data != null) {
            try {
               object = EncodedImage.createEncodedImage(data, 0, data.length);
            } finally {
               ;
            }
         }
      }

      if (object == null && theme != null) {
         ThemeAttributeSet attributes = theme.getAttributeSet(Tag.create("homescreen"));
         if (attributes != null) {
            Bitmap bitmap = new Bitmap(Display.getWidth(), Display.getHeight());
            Graphics g2 = new Graphics(bitmap);
            Background background = attributes.getBackground();
            if (background != null) {
               background.draw(g2, new XYRect(0, 0, Display.getWidth(), Display.getHeight()));
               object = bitmap;
            }
         }
      }

      if (object instanceof String) {
         imageName = (String)object;
         object = FileUtilities.getEncodedImage("file://" + imageName);
         if (object == null) {
            this.setBackgroundImage(null, null, false);
            return;
         }

         if (!this._bkgrLoaded) {
            this._bkgrLoaded = true;
         }
      }

      if (object instanceof EncodedImage) {
         if (params != null && params.length >= 4) {
            int scaleFP = params[0] == null ? 0 : Integer.parseInt(params[0]);
            int rotation = params[1] == null ? 0 : Integer.parseInt(params[1]);
            int topX = params[2] == null ? 0 : Integer.parseInt(params[2]);
            int topY = params[3] == null ? 0 : Integer.parseInt(params[3]);
            EncodedImage image = (EncodedImage)object;
            int screenWidth = Display.getWidth();
            int screenHeight = Display.getHeight();
            Bitmap newBitmap = new Bitmap(screenWidth, screenHeight);
            synchronized (Application.getEventLock()) {
               RibbonLauncherImpl$ZoomBitmapFieldExt zoom = new RibbonLauncherImpl$ZoomBitmapFieldExt(image, 1, 36);
               RibbonLauncherImpl$DummyScreen screen = new RibbonLauncherImpl$DummyScreen();
               screen.add(zoom);
               screen.doLayoutHack();
               zoom.setScale(scaleFP, topX, topY, rotation);
               Graphics graphics = new Graphics(newBitmap);
               zoom.paintHack(graphics);
            }

            object = newBitmap;
         } else {
            int width = Display.getWidth();
            int height = Display.getHeight();
            EncodedImage image = (EncodedImage)object;
            if (image.getWidth() == width && image.getHeight() == height) {
               object = image.getBitmap();
            } else {
               synchronized (Application.getEventLock()) {
                  Bitmap newBitmap = new Bitmap(width, height);
                  RibbonLauncherImpl$ZoomBitmapFieldExt zoom = new RibbonLauncherImpl$ZoomBitmapFieldExt(image, 1, 36);
                  RibbonLauncherImpl$DummyScreen screen = new RibbonLauncherImpl$DummyScreen();
                  screen.add(zoom);
                  zoom.scaleToFit(width, height, true, 0, 0, 0);
                  screen.doLayoutHack();
                  Graphics graphics = new Graphics(newBitmap);
                  zoom.paintHack(graphics);
                  object = newBitmap;
               }
            }
         }
      }

      Bitmap bitmap = null;
      if (object instanceof Bitmap) {
         bitmap = (Bitmap)object;
      }

      if (updateRibbonOptions) {
         this._ribbonOptions.setBackgroundImage(ThemeManager.getActiveName(), imageName, params);
      }

      this._ribbonAppScreen.setBackgroundImage(bitmap);
      ApplicationMenu.setBackgroundBitmap(bitmap);
      ImageFactory.getInstance().setBackgroundImage(bitmap);
   }

   @Override
   public final void updateRegisteredAction(String name) {
      this._actionUpdater.invokeLater(name);
   }

   @Override
   public final void setNetworkImmediateDisplayString(String string) {
      AggregatedNetworkProps networkProps = (AggregatedNetworkProps)RibbonNetworkInfo.getInstance().getNetworkPropsCollection();
      networkProps.internalSet(6665563664396523075L, string);
   }

   private final void layoutRibbonFromTheme() {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         String[] layout = null;
         ThemeAttributeSet attributes = theme.getAttributeSet(Tag.create("homescreen"), null, 0);
         if (attributes != null) {
            layout = attributes.getLayoutParameters();
         }

         if (layout == null) {
            layout = new String[]{"Banner?align=title", "AppChooser.GridAppChooser?chooser", "AppDescription?align=bottom"};
         }

         Field[] fields = new Field[layout.length];
         if (this._applicationIconArea != null) {
            ((Manager)this._applicationIconArea).setFocusListener(null);
         }

         ((RibbonScreenManager)this._ribbonAppScreen.getDelegate())._noLayouts = true;
         Hashtable[] layoutArgs = new Hashtable[layout.length];

         for (int i = 0; i < layout.length; i++) {
            String layoutInfo = layout[i];
            int index = layoutInfo.indexOf(63);
            String factory = layoutInfo;
            layoutArgs[i] = new Hashtable();
            if (index >= 0) {
               StringTokenizer tokenizer = new StringTokenizer(layoutInfo.substring(index + 1), '&');
               int count = tokenizer.countTokens();
               factory = layoutInfo.substring(0, index);

               for (int j = 0; j < count; j++) {
                  String arg = tokenizer.nextToken();
                  int offset = arg.indexOf(61);
                  String label;
                  String value;
                  if (offset == -1) {
                     label = arg;
                     value = "";
                  } else {
                     label = arg.substring(0, offset);
                     value = arg.substring(offset + 1);
                  }

                  layoutArgs[i].put(label, value);
               }
            }

            String factoryArgs = null;
            index = factory.indexOf(46);
            if (index > 0) {
               factoryArgs = factory.substring(index + 1);
               factory = factory.substring(0, index);
            }

            fields[i] = (Field)((Factory)this._homeScreenFactories.get(factory)).createInstance(factoryArgs);
            Field var10000 = fields[i];
            if (fields[i] instanceof RibbonComponentInitializer) {
               ((RibbonComponentInitializer)var10000).initialize(layoutArgs[i], null);
            }
         }

         this._ribbonAppScreen.setHomeScreenContents(layoutArgs, fields);
         this.updateBackgroundImage();
         ((Manager)this._applicationIconArea).setFocusListener(this);
         this.eventOccurred(-4394903006263251010L, 0, 0, null, null);
      }
   }

   private final boolean onKeyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            if (this._applicationIconArea.moveApplicationInProgress()) {
               this.completeMoveApplication(true);
               return true;
            } else {
               if ((status & 1) == 0) {
                  this.launch();
                  return true;
               }

               if (!this._isReducedKeyboard) {
                  LockEventLogger.logLockEvent(1282564460);
                  ApplicationManager.getApplicationManager().lockSystem(true);
               }

               return true;
            }
         case '\u001b':
            if (this._applicationIconArea.moveApplicationInProgress()) {
               this.completeMoveApplication(false);
               return true;
            }

            this._applicationIconArea.popFolder();
            this.updateApplicationDescription();
            if (ApplicationMenu.isSkipRootFolderSet()) {
               ApplicationMenu.setSkipRootFolder(false);
               ApplicationMenu.dismiss();
            }

            return true;
         default:
            return this._hotKeysDisabled ? false : this.processRibbonHotKey(key, status);
      }
   }

   private final boolean processRibbonHotKey(char key, int status) {
      if ((status & 1) != 0) {
         return false;
      }

      if (this._applicationIconArea.moveApplicationInProgress()) {
         return true;
      }

      int modifiers = SLKeyLayout.convertStatusToModifiers(status);
      int keyCode = Keypad.getLayout().getOriginalKeyCode(key, modifiers);
      StringBuffer keyChars = DefaultKeyLayout.getDefaultKeyLayout().getKeyChars(keyCode, 0);
      int numChars = keyChars.length();
      key = keyChars.charAt(0);
      ApplicationEntry application = this._applicationIconArea.getApplicationByHotKey(key);
      if (application != null && this.appLaunchesAllowed()) {
         if (application.isVisible()) {
            this._applicationIconArea.setFocus(application);
         }

         this.launch(application);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void showRootFolder(boolean moveFocusToFirst) {
      if (!StandbyManager.getInstance().isInStandby()) {
         this._app.invokeLater(new RibbonLauncherImpl$6(this, moveFocusToFirst));
      }
   }

   private final void iconListChanged() {
      this._iconUpdater.invokeLater();
   }

   private final boolean appLaunchesAllowed() {
      if (!this._appLaunchesAllowed) {
         long systemTime = System.currentTimeMillis();
         if (systemTime - this._lastLaunchTime > 200 || systemTime < this._lastLaunchTime - 200) {
            this.setAppLaunchesAllowed(true);
         }
      }

      return this._appLaunchesAllowed;
   }

   private final void locateApplications() {
      synchronized (this._applicationIconArea) {
         this._hierarchyManager.loadActiveHierarchy();
      }
   }

   private final void initializeHomeScreen() {
      if (this._applicationIconArea != null) {
         ((Manager)this._applicationIconArea).setFocusListener(null);
         this._applicationIconArea = null;
      }

      this._description = null;
      this._externalMainScreen = this.getLayoutScreen();
      if (this._externalMainScreen == null) {
         this.layoutRibbonFromTheme();
      } else {
         this.updateBackgroundImage();
      }

      if (this._applicationIconArea == null) {
         if (this._description == null) {
            this._description = new RibbonDescriptionField(this._compressedBanners);
         }

         this._applicationIconArea = this._hierarchyManager.getLauncherField(ApplicationMenu.getDefaultChooserName());
         ApplicationMenu.register(this, (Manager)this._applicationIconArea, this._ribbonAppScreen, this._description);
      }
   }

   static final boolean activateFolder(String folderName) {
      return _instanceImpl.internalActivateFolder(folderName);
   }

   private final boolean notifyRibbonKeyListeners(char key, int keycode, int status, int time, int eventId) {
      synchronized (this._ribbonListeners) {
         for (int i = this._ribbonListeners.size() - 1; i >= 0; i--) {
            WeakReference wr = (WeakReference)this._ribbonListeners.elementAt(i);
            Object listenerObject = wr.get();
            if (listenerObject == null) {
               this._ribbonListeners.removeElementAt(i);
            } else if (listenerObject instanceof RibbonKeyListener) {
               RibbonKeyListener listener = (RibbonKeyListener)listenerObject;

               try {
                  switch (eventId) {
                     case -1:
                        break;
                     case 0:
                        if (listener.keyChar(key, status, time)) {
                           return true;
                        }
                        break;
                     case 1:
                     default:
                        listener.keyDown(keycode, time);
                        break;
                     case 2:
                        if (listener.keyUp(keycode, time)) {
                           return true;
                        }
                        break;
                     case 3:
                        if (listener.keyRepeat(keycode, time)) {
                           return true;
                        }
                  }
               } finally {
                  ;
               }
            }
         }

         return false;
      }
   }

   @Override
   public final void setLongIdleModeText(String text) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private final void moduleListChanged() {
      this._appsUpdater.invokeLater();
   }

   private final void setAppLaunchesAllowed(boolean newValue) {
      this._appLaunchesAllowed = newValue;
      if (!newValue) {
         this._lastLaunchTime = System.currentTimeMillis();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void launch(ApplicationEntry applicationEntry) {
      try {
         if (applicationEntry != null && this.appLaunchesAllowed()) {
            this.setAppLaunchesAllowed(false);
            applicationEntry.invoke();
            return;
         }
      } catch (Throwable var4) {
         this.setAppLaunchesAllowed(true);
         Dialog.alert(t.getMessage());
         return;
      }
   }

   RibbonLauncherImpl() {
      EventLogger.register(-6210296463828503575L, "net.rim.ribbon", 2);
      _instanceImpl = this;
      this._ribbonOptions = RibbonOptions.getOptions();
      this._ribbonListeners = new Vector();
      this._app = UiApplication.getUiApplication();
      this._app.addGlobalEventListener(this);
      ConvenienceKeyOptionsImpl.register();
      this._app.addKeyListener(this._unhandledGlobalKeyListener);
      RibbonOptions.getOptions().enableSynchronization();
      this._hierarchyManager = HierarchyManager.getInstance();
      this._ribbonAppScreen = new HomeScreen(this, this._compressedBanners);
      this._ribbonAppScreen.addKeyListener(this);
      this._homeScreenFactories = new Hashtable();
      this._homeScreenFactories.put("Banner", new RibbonLauncherImpl$1(this));
      this._homeScreenFactories.put("AppChooser", new RibbonLauncherImpl$2(this));
      this._homeScreenFactories.put("AppDescription", new RibbonLauncherImpl$3(this));
      this.initializeHomeScreen();
      this.locateApplications();
      this.populateRibbon();
      this._app.pushScreen(this._externalMainScreen == null ? this._ribbonAppScreen : this._externalMainScreen);
      ServiceBookIndicator sbi = ServiceBookIndicator.getInstance();
      if (sbi != null) {
         sbi.updateIndicator();
      }

      ContentProtectionIndicator.initialize();
      this._backdoor = new BackdoorKeyProcessor(true, this);
      this._app.addFileSystemListener(this);
      this._app.addFileSystemJournalListener(this);
      this._myStoredUSN = FileSystemJournal.getNextUSN();
   }

   @Override
   public final void setLongImmediateCellBroadcastText(String text) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private final void updateApplicationDescription() {
      try {
         if (Application.getApplication() != this._app) {
            this._descriptionUpdater.invokeLater();
            return;
         }

         this.internalUpdateApplicationDescription();
      } finally {
         return;
      }
   }

   private final void internalUpdateApplicationDescription() {
      ApplicationEntry selectedEntry = this._applicationIconArea.getSelectedApplication();
      if (selectedEntry != null) {
         String text = "";
         if (!this._applicationIconArea.moveApplicationInProgress()) {
            text = selectedEntry.getDescription(this._hotKeysDisabled);
         } else {
            ApplicationEntry movingEntry = this._applicationIconArea.getMovingApplication();
            if (movingEntry == null) {
               throw new NullPointerException("Moving entry not found with move in progress");
            }

            if (!(selectedEntry.getDescriptor() instanceof FolderEntryPointDescriptor) || movingEntry.getDescriptor() instanceof FolderEntryPointDescriptor) {
               text = text + RibbonResources.getString(20) + ' ' + selectedEntry.getDescription(this._hotKeysDisabled);
            } else if (selectedEntry.getUniqueName().length() == 0) {
               text = text + RibbonResources.getString(21) + ' ' + selectedEntry.getDescription(this._hotKeysDisabled);
            } else {
               text = text + RibbonResources.getString(21) + ' ' + RibbonResources.getString(173) + ' ' + selectedEntry.getDescription(this._hotKeysDisabled);
            }
         }

         if (this._description != null) {
            this._description.setText(text);
         }
      }
   }
}
