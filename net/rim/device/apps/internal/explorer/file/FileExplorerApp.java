package net.rim.device.apps.internal.explorer.file;

import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;
import net.rim.device.apps.internal.explorer.Media.MediaHubScreen;
import net.rim.device.apps.internal.explorer.Media.MediaLauncher;
import net.rim.device.apps.internal.explorer.Media.PictureExploreScreen;
import net.rim.device.apps.internal.explorer.file.options.ExplorerOptions;
import net.rim.device.apps.internal.explorer.file.options.MediaCardOptionsProvider;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.RootRegister;
import net.rim.device.internal.media.MediaOptionsRegistry;
import net.rim.device.internal.media.MediaOptionsUtilities;

public final class FileExplorerApp extends UiApplication implements KeyListener, GlobalEventListener, FileSystemListener, DialogClosedListener {
   private boolean _escapeCharPressed;
   private Dialog _mediaDialog;
   private static final long DISPLAY_MEDIA_GUID = 5632215308423875952L;
   private static final long DISPLAY_MUSIC_GUID = 1126161790194782224L;
   private static final long DISPLAY_VOICENOTE_GUID = -8370052414117276795L;
   private static String _entry = null;
   public static final int PICTURES_ROOT = 1;
   public static final int TUNES_ROOT = 2;

   public FileExplorerApp(String entry) {
      ContextObject context = new ContextObject();
      context.putIntegerData(1);
      if (entry != null) {
         if (entry.equals("pictures")) {
            context.setFlag(45);
            this.pushScreen(new PictureExploreScreen(context));
         }
      } else {
         this.pushScreen(new MediaHubScreen(null));
      }

      this.mediaCardCheck();
      this.enableKeyUpEvents(true);
      this.addKeyListener(this);
      this.addGlobalEventListener(this);
      this.addFileSystemListener(this);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      if (this._escapeCharPressed && Keypad.map(keycode) == 27) {
         this._escapeCharPressed = false;
         this.closeAllScreens(null);
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
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (Keypad.map(keycode) == 27) {
         this._escapeCharPressed = true;
         return false;
      } else if ((key == 19 || key == 21) && this.explorerOwnsConvenienceKey(key)) {
         MediaLauncher.showPlayer();
         return true;
      } else {
         return false;
      }
   }

   private final void closeAllScreens(Screen newScreen) {
      for (Screen activeScreen = this.getActiveScreen(); activeScreen != null; activeScreen = this.getActiveScreen()) {
         if (activeScreen instanceof MediaHubScreen && newScreen != null) {
            this.pushScreen(newScreen);
            return;
         }

         activeScreen.close();
      }
   }

   public static final String getEntry() {
      return _entry;
   }

   public static final void main(String[] args) {
      if (args != null && args.length == 1) {
         if (args[0].equals("init")) {
            PackageManager.registerOnceOnSystemStart();
            MediaCardOptionsProvider.register();
            ExplorerOptions.getOptions().enableSynchronization();
            configureVolumeBoost();
            return;
         }

         if (args[0].equals("music")) {
            postAction(1126161790194782224L);
            return;
         }

         if (args[0].equals("media")) {
            postAction(5632215308423875952L);
            return;
         }

         if (args[0].equals("voicenote")) {
            postAction(-8370052414117276795L);
            return;
         }

         if (args[0].equals("pictures")) {
            new FileExplorerApp("pictures").enterEventDispatcher();
            return;
         }
      } else {
         new FileExplorerApp(null).enterEventDispatcher();
      }
   }

   private static final void postAction(long guid) {
      try {
         ApplicationDescriptor app = getFileExplorerApplicationDescriptor();
         if (app != null) {
            int pid = ApplicationManager.getApplicationManager().runApplication(app, false);
            RIMGlobalMessagePoster.postGlobalEvent(pid, guid, 0, 0, null, null);
            return;
         }
      } finally {
         return;
      }
   }

   private static final ApplicationDescriptor getFileExplorerApplicationDescriptor() {
      int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_file_explorer");
      if (moduleHandle == -1) {
         return null;
      }

      try {
         ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
         ApplicationDescriptor descriptor = null;

         for (int i = descriptors.length - 1; i >= 0; i--) {
            descriptor = descriptors[i];
            String[] arguments = descriptor.getArgs();
            if (arguments == null || arguments.length == 0) {
               return descriptor;
            }
         }
      } finally {
         return null;
      }

      return null;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 5632215308423875952L) {
         _entry = "media";
         this.requestForeground();
      } else if (guid == 1126161790194782224L) {
         _entry = "music";
         this.invokeLater(new FileExplorerApp$DisplayMusicRunnable(this));
      } else {
         if (guid == -8370052414117276795L) {
            _entry = "voicenote";
            this.invokeLater(new FileExplorerApp$DisplayVoiceNoteRunnable(this));
         }
      }
   }

   private final boolean explorerOwnsConvenienceKey(int key) {
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

      return keyOwner == null ? false : keyOwner.startsWith("net_rim_bb_file_explorer");
   }

   @Override
   public final void rootChanged(int state, String rootName) {
      this.mediaCardCheck();
   }

   private final void mediaCardCheck() {
      RootRegister register = RootRegister.getInstance();
      if (register.isMassStorageActive() && !register.isBatteryDoorOpen()) {
         this._mediaDialog = new Dialog(0, ExplorerResources.getString(167), 0, null, 0);
         this._mediaDialog.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
         this._mediaDialog.setEscapeEnabled(true);
         this._mediaDialog.setDialogClosedListener(this);
         this.invokeLater(new FileExplorerApp$1(this));
      } else {
         if (this._mediaDialog != null) {
            this._mediaDialog.close();
            this._mediaDialog = null;
         }
      }
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (this._mediaDialog == dialog) {
         this._mediaDialog = null;
      }
   }

   private static final void configureVolumeBoost() {
      if (MediaOptionsRegistry.getInstance().getBoolean(2886183832722201160L)) {
         if (!AudioRouter.getInstance().isVolumeBoostModeSupported() || MediaOptionsUtilities.isVolumeBoostKeyExpired()) {
            MediaOptionsRegistry.getInstance().setBoolean(2886183832722201160L, false);
            MediaOptionsRegistry.getInstance().setBoolean(-4387502259448276168L, false);
            return;
         }

         AudioRouter.getInstance().setVolumeBoostMode(true);
      }
   }
}
