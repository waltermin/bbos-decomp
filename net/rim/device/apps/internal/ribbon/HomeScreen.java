package net.rim.device.apps.internal.ribbon;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.FolderEntryPointDescriptor;

final class HomeScreen extends Screen {
   private RibbonLauncherImpl _ribbonBarImpl;
   private ContextObject _contextObject = new ContextObject();
   private boolean _ignoreKeyUp = true;
   private static Tag TAG = Tag.create("homescreen");

   HomeScreen(RibbonLauncherImpl ribbonBarImpl, boolean compressedBanners) {
      super(new RibbonScreenManager(compressedBanners), 65536);
      this._ribbonBarImpl = ribbonBarImpl;
      this.setTag(TAG);
      this.setCatchPaintExceptions(true);
      this.setTrackballFilter(0);
   }

   @Override
   protected final void applyTheme() {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         ThemeAttributeSet tas = theme.getAttributeSet(TAG);
         if (tas != null && tas.getBackground() == null) {
            tas.getWriterInternal().setColor(0, 16777215);
         }
      }

      RibbonOptions options = RibbonOptions.getOptions();
      options.setShowHiddenApps(false);
      super.applyTheme();
   }

   final void setHomeScreenContents(Hashtable[] layout, Field[] fields) {
      RibbonScreenManager manager = (RibbonScreenManager)this.getDelegate();
      manager.setHomeScreenContents(layout, fields);
   }

   final void setBackgroundImage(Bitmap bitmap) {
      RibbonScreenManager manager = (RibbonScreenManager)this.getDelegate();
      manager.setBackgroundImage(bitmap);
   }

   @Override
   public final Menu getMenu(int instance) {
      SystemEnabledMenu menu = new SystemEnabledMenu(this._contextObject, null);
      this.makeMenuWithContext(menu, instance);
      this.makeMenu(menu, instance);
      menu.promoteVerbs();
      return menu;
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(width, height);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
   }

   @Override
   public final boolean defaultStylusAction(int context) {
      this._ribbonBarImpl.launch();
      return true;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         this._ignoreKeyUp = true;
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected final void onExposed() {
      Field iconArea = (Field)this._ribbonBarImpl._applicationIconArea;
      if (iconArea.getFocusListener() == null) {
         label19:
         try {
            iconArea.setFocusListener(this._ribbonBarImpl);
         } finally {
            break label19;
         }
      }

      this._ignoreKeyUp = true;
   }

   final void onAppActivated() {
      this._ignoreKeyUp = true;
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      return this._ignoreKeyUp ? true : super.keyUp(keycode, time);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (this._ignoreKeyUp) {
         this._ignoreKeyUp = false;
      }

      return super.keyDown(keycode, time);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      RibbonLauncherImpl ribbon = RibbonLauncherImpl._instanceImpl;
      if (instance == 0) {
         ApplicationEntry selectedApplication = ribbon._applicationIconArea.getSelectedApplication();
         if (ribbon._applicationIconArea.moveApplicationInProgress()) {
            menu.add(CompleteMoveIconVerb.getInstance(ribbon));
            menu.add(CancelMoveIconVerb.getInstance(ribbon));
         } else {
            if (ribbon._hierarchyManager.movingIconsAllowed() && !FolderEntryPointDescriptor.isUpFolder(selectedApplication)) {
               menu.add(MoveIconVerb.getInstance(ribbon));
            }

            if (ribbon._hierarchyManager.hidingIconsAllowed()) {
               if (ribbon._applicationIconArea.hasHiddenApplications()) {
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
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (!RibbonLauncherImpl._instanceImpl._applicationIconArea.moveApplicationInProgress()) {
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

   @Override
   public final boolean trackwheelClick(int status, int time) {
      if ((status & 1) == 0) {
         if (RibbonLauncherImpl._instanceImpl._applicationIconArea.moveApplicationInProgress()) {
            RibbonLauncherImpl._instanceImpl.completeMoveApplication(true);
            return true;
         }

         if (!DeviceInfo.isInHolster()) {
            RibbonLauncherImpl._instanceImpl.launch();
            return true;
         }
      } else {
         this.onMenu(1073741824);
      }

      return true;
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
      RibbonLauncherImpl ribbon = RibbonLauncherImpl._instanceImpl;
      if (ribbon._applicationIconArea.moveApplicationInProgress()) {
         ribbon._applicationIconArea.updateMovingApplicationPosition();
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
}
