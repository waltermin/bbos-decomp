package net.rim.device.apps.internal.ribbon;

import java.util.Hashtable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationLauncherField;
import net.rim.device.apps.internal.ribbon.launcher.FolderEntryPointDescriptor;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;
import net.rim.device.apps.internal.ribbon.launcher.OrganizeApplications;
import net.rim.device.internal.system.InternalServices;

public final class ApplicationMenu extends PopupScreen implements Runnable {
   private boolean _allowToShow = true;
   private UiApplication _app;
   ApplicationLauncherField _iconArea;
   protected ButtonField _organizeApplications;
   private SeparatorField _separator;
   private XYRect _position;
   public int _alpha = 255;
   public int _fadeInterval = 60;
   private ResourceBundleFamily _rbf;
   private RibbonDescriptionField _descriptionField;
   private FocusChangeListener _descriptionFocusListener;
   private ContextObject _contextObject = new ContextObject();
   private int _menuType = 0;
   private static final long SKINNED_APPLICATION_MENU_GUID = 1619104265458180924L;
   private static final long BACKGROUND_BITMAP_GUID = 3396006904009726662L;
   public static final int MENUTYPE_VERTICAL = 0;
   public static final int MENUTYPE_GRID = 1;
   private static ApplicationMenu _instance;
   private static boolean _skipRootFolder;
   private static boolean _previousHotkeyValue;
   private static boolean _isReducedKeyboard = InternalServices.isReducedFormFactor();
   private static Runnable _dismissTimerRunnable;
   private static final long TIMER_DELAY = 30000L;
   private static boolean _isTimerActive;
   private static SystemEnabledMenu _currentSystemMenu;
   static final Tag VERTICAL_TAG = Tag.create("application-menu");
   static final Tag GRID_TAG = Tag.create("application-menu-icon");

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      RibbonLauncherImpl ribbon = RibbonLauncherImpl._instanceImpl;
      _currentSystemMenu = menu;
      if (instance == 0) {
         ApplicationEntry selectedApplication = this._iconArea.getSelectedApplication();
         if (this._iconArea.moveApplicationInProgress()) {
            menu.add(CompleteMoveIconVerb.getInstance(ribbon));
            menu.add(CancelMoveIconVerb.getInstance(ribbon));
         } else {
            if (ribbon._hierarchyManager.movingIconsAllowed() && !FolderEntryPointDescriptor.isUpFolder(selectedApplication)) {
               menu.add(MoveIconVerb.getInstance(ribbon));
            }

            if (ribbon._hierarchyManager.hidingIconsAllowed()) {
               if (this._iconArea.hasHiddenApplications()) {
                  menu.add(ShowAllToggleVerb.getInstance(ribbon));
               }

               if (selectedApplication != null && selectedApplication.canHide() && !FolderEntryPointDescriptor.isUpFolder(selectedApplication)) {
                  menu.add(HideIconToggleVerb.getInstance(ribbon, selectedApplication));
               }
            }

            if (ribbon._applicationIconArea.isRootFolder()) {
               menu.add(AddFolderVerb.getInstance(ribbon));
            }

            if (selectedApplication != null
               && selectedApplication.getDescriptor() instanceof FolderEntryPointDescriptor
               && !FolderEntryPointDescriptor.isUpFolder(selectedApplication)) {
               menu.add(EditFolderPropertiesVerb.getInstance(ribbon));
               if (ribbon._applicationIconArea.isRootFolder()) {
                  menu.add(DeleteVerb.getInstance(ribbon));
               }
            }
         }

         if (ribbon._longIdleModeText != null) {
            menu.add(ViewIdleModeTextVerb.getInstance(ribbon, ribbon._longIdleModeText));
         }

         if (ribbon._longImmediateCellBroadcastText != null) {
            menu.add(ViewImmediateCellBroadcastTextVerb.getInstance(ribbon, ribbon._longImmediateCellBroadcastText));
         }

         VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(-4612983506188396850L);
         if (verbFactories != null && selectedApplication != null) {
            EntryPointDescriptor entry = selectedApplication.getDescriptor();
            if (entry instanceof ObjectProps) {
               ObjectProps oprops = (ObjectProps)entry;
               Object o = oprops.get(-8880124975077471920L, (Object)null);
               ApplicationDescriptor applicationDescriptor = null;
               if (o instanceof ApplicationDescriptor) {
                  applicationDescriptor = (ApplicationDescriptor)o;
               }

               if (applicationDescriptor != null) {
                  this._contextObject.put(4130651187691035806L, applicationDescriptor);

                  for (int i = verbFactories.length - 1; i >= 0; i--) {
                     VerbFactory factory = verbFactories[i];
                     boolean var14 = false /* VF: Semaphore variable */;

                     try {
                        var14 = true;
                        menu.add(factory.getVerbs(this._contextObject));
                        var14 = false;
                     } finally {
                        if (var14) {
                           VerbFactoryRepository.removeFactory(-4612983506188396850L, factory);
                           continue;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public final void run() {
      if (this._alpha < 255) {
         this._alpha = this._alpha + this._fadeInterval;
         if (this._alpha > 255) {
            this._alpha = 255;
         }

         this.getDelegate().invalidate();
         this._app.invokeLater(this, 60, false);
      }
   }

   public static final boolean containsApplicationMenu() {
      ThemeAttributeSet attributes = ThemeManager.getActiveTheme().getAttributeSet(VERTICAL_TAG);
      if (attributes != null) {
         XYRect position = attributes.getPosition();
         return position != null && position.width > 0 && position.height > 0;
      } else {
         return false;
      }
   }

   public static final void setSkipRootFolder(boolean value) {
      _skipRootFolder = value;
   }

   public static final boolean isSkipRootFolderSet() {
      return _skipRootFolder;
   }

   static final Tag getTag(int menuType) {
      switch (menuType) {
         case 1:
            return GRID_TAG;
         default:
            return VERTICAL_TAG;
      }
   }

   static final int getDefaultMenuType() {
      ThemeAttributeSet gridAttributes = ThemeManager.getActiveTheme().getAttributeSet(GRID_TAG);
      if (gridAttributes != null) {
         XYRect position = gridAttributes.getPosition();
         if (position != null && position.width > 0 && position.height > 0) {
            return 1;
         }
      }

      return 0;
   }

   static final String getDefaultChooserName() {
      switch (getDefaultMenuType()) {
         case 1:
            return "GridAppChooser";
         default:
            return "VerticalAppChooser";
      }
   }

   private final boolean doFade() {
      return this._menuType == 0;
   }

   @Override
   protected final void onObscured() {
      if (this._menuType == 0) {
         dismiss();
      }
   }

   static final void dismissMenu() {
      if (_instance != null && _instance._menuType == 0) {
         dismiss();
      }
   }

   public static final void dismiss() {
      if (_instance != null && _instance.isDisplayed()) {
         if (_instance._descriptionField != null) {
            ((Field)_instance._iconArea).setFocusListener(null);
         }

         if (enableHotkeys()) {
            RibbonLauncherImpl._instanceImpl.disableHotKeys(_previousHotkeyValue);
         }

         if (_instance._menuType == 1) {
            HierarchyManager.getInstance().fireOnEntryChange(null);
         }

         UiApplication.getUiApplication().popScreen(_instance);
         _isTimerActive = false;
         if (_instance._iconArea.moveApplicationInProgress()) {
            _instance._iconArea.completeMoveApplication(false);
         }

         if (_currentSystemMenu != null && _currentSystemMenu.isDisplayed()) {
            _currentSystemMenu.close();
         }
      }
   }

   public static final void show() {
      if (_instance != null) {
         UiApplication app = UiApplication.getUiApplication();
         if (_instance.isVisible()) {
            dismiss();
         } else {
            if (!_instance.isDisplayed()) {
               if (_instance._menuType == 1) {
                  ((RibbonScreenManager)_instance.getDelegate()).setBackgroundImage(getBackgroundBitmap());
               }

               if (isAllowToShow()) {
                  if (enableHotkeys()) {
                     _previousHotkeyValue = RibbonLauncherImpl._instanceImpl.getDisableHotKeys();
                     RibbonLauncherImpl._instanceImpl.disableHotKeys(false);
                  }

                  if (_instance._descriptionField != null) {
                     ((Field)_instance._iconArea).setFocusListener(null);
                     ((Field)_instance._iconArea).setFocusListener(_instance._descriptionFocusListener);
                  }

                  if (_instance._menuType == 0) {
                     app.pushGlobalScreen(_instance, 100, 2);
                  } else {
                     app.pushScreen(_instance);
                  }
               }
            }

            if (_instance.isDisplayed()) {
               if (!app.isForeground()) {
                  app.requestForeground();
               }

               startDismissTimer();
            }
         }
      }
   }

   static final boolean enableHotkeys() {
      return !_isReducedKeyboard && _instance._menuType == 0;
   }

   static final Bitmap getBackgroundBitmap() {
      return (Bitmap)ApplicationRegistry.getApplicationRegistry().get(3396006904009726662L);
   }

   static final void setBackgroundBitmap(Bitmap bitmap) {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      ApplicationMenu am = (ApplicationMenu)appRegistry.get(1619104265458180924L);
      if (bitmap != null) {
         appRegistry.replace(3396006904009726662L, bitmap);
      } else {
         appRegistry.remove(3396006904009726662L);
      }

      if (am != null && am._menuType == 1) {
         ((RibbonScreenManager)am.getDelegate()).setBackgroundImage(bitmap);
      }
   }

   @Override
   protected final boolean isTransparentBackground() {
      return this._menuType == 1 && getBackgroundBitmap() != null ? false : super.isTransparentBackground();
   }

   @Override
   public final boolean dispatchKeyEvent(int event, char key, int keycode, int time) {
      int keypress = Keypad.key(keycode);
      if (keypress == 18 && event == 513) {
         dismiss();
         return false;
      } else {
         return super.dispatchKeyEvent(event, key, keycode, time);
      }
   }

   public static final void setAllowToShow(boolean show) {
      ApplicationMenu am = (ApplicationMenu)ApplicationRegistry.getApplicationRegistry().get(1619104265458180924L);
      if (am != null) {
         am.internalSetAllowToShow(show);
      }
   }

   private static final boolean isAllowToShow() {
      ApplicationMenu am = (ApplicationMenu)ApplicationRegistry.getApplicationRegistry().get(1619104265458180924L);
      return am.internalIsAllowToShow();
   }

   public static final boolean isAppMenuDisplayed() {
      ApplicationMenu am = (ApplicationMenu)ApplicationRegistry.getApplicationRegistry().get(1619104265458180924L);
      return am != null ? am.isDisplayed() : false;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private ApplicationMenu(Manager delegate, Manager appArea, int menuType) {
      super(delegate, 65536);
      this._rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
      this._menuType = menuType;
      Tag tag = getTag(menuType);
      this.setId("chooser");
      this._position = ThemeManager.getActiveTheme().getAttributeSet(tag).getPosition();
      this._app = UiApplication.getUiApplication();

      label23:
      try {
         Manager mgr = appArea.getManager();
         if (mgr != null) {
            mgr.delete(appArea);
         }
      } catch (Throwable var7) {
         System.out.println("Exception removing the Field from the manager in Appplication Menu : " + any.toString());
         break label23;
      }

      ApplicationRegistry.getApplicationRegistry().replace(1619104265458180924L, this);
      this._iconArea = (ApplicationLauncherField)appArea;
      this.setTag(tag);
      this._organizeApplications = new ApplicationMenu$1(this, this._rbf.getString(149), 1152921504606846976L);
      this._separator = new SeparatorField();
   }

   private final boolean onOrganizeApplications() {
      boolean screenPushed = false;
      OrganizeApplications organize = new OrganizeApplications();
      if (!this._app.isForeground()) {
         ApplicationManager appManager = ApplicationManager.getApplicationManager();
         appManager.requestForeground(this._app.getProcessId());
      }

      this._app.pushScreen(organize.createMainScreen());
      screenPushed = true;
      dismiss();
      return screenPushed;
   }

   private final boolean internalIsAllowToShow() {
      return this._allowToShow;
   }

   private final void internalSetAllowToShow(boolean show) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void onUiEngineAttached(boolean attached) {
      if (attached) {
         if (this._iconArea != null && this._iconArea.getApplicationAt(0) != null) {
            this._organizeApplications.setLabel(this._rbf.getString(149));
            if (this._menuType == 0) {
               this.add(this._separator);
               this.add(this._organizeApplications);
            }
         }

         if (this.doFade()) {
            this._alpha = 20;
            this._app.invokeLater(this);
         }
      } else if (this._menuType == 0) {
         label37:
         try {
            this.delete(this._separator);
            this.delete(this._organizeApplications);
         } finally {
            break label37;
         }
      }

      super.onUiEngineAttached(attached);
   }

   public static final boolean isApplicationMenuValid(int menuType) {
      ApplicationMenu am = (ApplicationMenu)ApplicationRegistry.getApplicationRegistry().get(1619104265458180924L);
      return am != null ? am._menuType == menuType && ApplicationRegistry.getApplicationRegistry().get(7112795868593646517L) != null : false;
   }

   @Override
   public final void paint(Graphics g) {
      if (this.doFade()) {
         int currentGlobalAlpha = g.getGlobalAlpha();
         g.setGlobalAlpha(this._alpha);
         super.paint(g);
         g.setGlobalAlpha(currentGlobalAlpha);
      } else {
         super.paint(g);
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      if ((status & 1) != 0) {
         this.onMenu(0);
         return true;
      }

      if (!super.trackwheelClick(status, time)) {
         if (!this._iconArea.moveApplicationInProgress()) {
            if (!DeviceInfo.isInHolster()) {
               if (this._menuType == 0) {
                  dismiss();
               }

               RibbonLauncherImpl._instanceImpl.launch();
               return true;
            }
         } else {
            RibbonLauncherImpl._instanceImpl.completeMoveApplication(true);
         }
      }

      return true;
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      return this.trackwheelClick(status, time);
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      return this._menuType == 1 ? RibbonLauncherImpl._instanceImpl.keyUp(keycode, time) : true;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key != 27 && key != 17 && key != 18 && key != 19 && key != 21 && key != 4098) {
         if (key == 10) {
            if (this._menuType == 0) {
               if (this._organizeApplications == null || !this._organizeApplications.isFocus()) {
                  dismiss();
               }

               return false;
            }

            if (this._menuType == 1) {
               return RibbonLauncherImpl._instanceImpl.keyDown(keycode, time);
            }
         } else if (key == 273) {
            return false;
         }

         return this._menuType == 1 ? RibbonLauncherImpl._instanceImpl.keyDown(keycode, time) : true;
      } else if (this._menuType != 0 && key != 27 && key != 4098 && key != 18) {
         return this._menuType == 1 ? RibbonLauncherImpl._instanceImpl.keyDown(keycode, time) : this.menuOwnsConvenienceKey(key);
      } else if (this._menuType == 1 && key == 4098) {
         this.onMenu(0);
         return true;
      } else if (key == 27 && !this._iconArea.isRootFolder()) {
         return RibbonLauncherImpl._instanceImpl.keyDown(keycode, time);
      } else if (this._iconArea.moveApplicationInProgress()) {
         this._iconArea.completeMoveApplication(false);
         return true;
      } else {
         dismiss();
         return true;
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return this._organizeApplications.isFocus() ? super.keyChar(key, status, time) : RibbonLauncherImpl._instanceImpl.keyChar(key, status, time);
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      boolean result = RibbonLauncherImpl._instanceImpl.keyRepeat(keycode, time);
      if (result) {
         dismissMenu();
      }

      return result;
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._position != null) {
         this.setPositionDelegate(0, 0);
         this.layoutDelegate(this._position.width, this._position.height);
         this.setPosition(this._position.x, this._position.y);
         this.setExtent(this._position.width, this._position.height);
      } else {
         super.sublayout(width, height);
      }
   }

   private final boolean menuOwnsConvenienceKey(int key) {
      ConvenienceKeyOptionsProvider convKeyProvider = ConvenienceKeyOptionsProvider.getInstance();
      String keyOwner = null;
      if (convKeyProvider != null) {
         switch (key) {
            case 19:
               keyOwner = convKeyProvider.getConvenienceKey1Owner();
               break;
            case 21:
               keyOwner = convKeyProvider.getConvenienceKey2Owner();
               break;
            default:
               return false;
         }
      }

      return keyOwner != null ? keyOwner.startsWith("net_rim_application_menu") : false;
   }

   @Override
   public final Menu getMenu(int instance) {
      SystemEnabledMenu menu = new SystemEnabledMenu(this._contextObject, null);
      menu.setAlignment(4294967296L, 34359738368L);
      this.makeMenuWithContext(menu, instance);
      this.makeMenu(menu, instance);
      menu.promoteVerbs();
      return menu;
   }

   public static final void register(RibbonLauncher ribbonLauncher, Manager appArea, HomeScreen screen, RibbonDescriptionField descriptionField) {
      int menuType = getDefaultMenuType();
      boolean b = ThemeManager.getActiveTheme().getAttributeSet(getTag(menuType)) != null;
      ApplicationMenuAction.register(ribbonLauncher, b);
      if (b) {
         if (menuType == 0) {
            _instance = new ApplicationMenu(new VerticalFieldManager(18313465672237056L), appArea, menuType);
            _instance.add(appArea);
            appArea.invalidate();
            return;
         }

         RibbonScreenManager rsm = new RibbonScreenManager(false);
         Hashtable[] layoutArgs = new Hashtable[2];
         _instance = new ApplicationMenu(rsm, appArea, menuType);
         _instance._descriptionField = descriptionField;
         _instance._descriptionFocusListener = (FocusChangeListener)ribbonLauncher;
         layoutArgs[0] = new Hashtable();
         layoutArgs[0].put("chooser", "");
         layoutArgs[1] = new Hashtable();
         layoutArgs[1].put("align", "bottom");
         Field[] layoutFields = new Field[]{appArea, descriptionField};
         rsm.setHomeScreenContents(layoutArgs, layoutFields);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (!this._iconArea.moveApplicationInProgress()) {
               if (!DeviceInfo.isInHolster()) {
                  RibbonLauncherImpl._instanceImpl.launch();
                  return true;
               }
            } else {
               RibbonLauncherImpl._instanceImpl.completeMoveApplication(true);
            }
         default:
            return true;
      }
   }

   private final boolean consumeNavigationMovement(int dy) {
      boolean retVal = this.consumeNavigationMovement();
      if (!retVal) {
         Field field = this.getLeafFieldWithFocus();
         if (field != null) {
            Object obj = RibbonLauncherImpl._instanceImpl._applicationIconArea;
            if (obj instanceof FlowFieldManager) {
               FlowFieldManager mgr = (FlowFieldManager)obj;
               if (dy != 0) {
                  retVal |= mgr.nextFocus(dy, true) == -1;
               }
            }
         }
      }

      return retVal;
   }

   private final boolean consumeNavigationMovement() {
      if (this._iconArea.moveApplicationInProgress()) {
         this._iconArea.updateMovingApplicationPosition();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      boolean returnValue = super.navigationMovement(dx, dy, status, time);
      return returnValue | this.consumeNavigationMovement(dy);
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      boolean returnValue = super.trackwheelRoll(amount, status, time);
      return returnValue | this.consumeNavigationMovement();
   }

   private static final void startDismissTimer() {
      _isTimerActive = true;
      if (_dismissTimerRunnable == null) {
         _dismissTimerRunnable = new ApplicationMenu$2();
         Application.getApplication().invokeLater(_dismissTimerRunnable, 30250, false);
      }
   }

   static final Runnable access$402(Runnable x0) {
      _dismissTimerRunnable = x0;
      return x0;
   }
}
