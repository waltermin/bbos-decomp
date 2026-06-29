package net.rim.device.apps.internal.videorecorder;

import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.file.ExplorerRegistry;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.io.file.RootRegister;
import net.rim.vm.Process;

public final class VideoRecorderMain extends UiApplication implements DialogClosedListener, FileSystemListener {
   private VideoRecorderScreen _screen;
   private Dialog _mediaDialog;
   private static VideoCameraApplicationVerb _videoAppVerb = new VideoCameraApplicationVerb();
   private static VideoRecorderMain$VideoCameraAliasFileEntry _videoCameraAppAlias = new VideoRecorderMain$VideoCameraAliasFileEntry(
      VideoRecorderResources.getString(2), _videoAppVerb, "VideoCamera", null
   );

   VideoRecorderMain() {
      this.mediaCardCheck();
      if (ITPolicy.getBoolean(47, 2, false)) {
         this.invokeLater(new VideoRecorderMain$1(this));
      } else {
         ApplicationProcess applicationProcess = (ApplicationProcess)Process.currentProcess();
         if (applicationProcess != null) {
            applicationProcess.addCleanupRunnable(new VideoRecorderMain$VideoRecordCleanupRunnable(this, null));
         }

         this._screen = new VideoRecorderScreen();
         this.pushScreen(this._screen);
         Camera.addListener(this, this._screen);
         this.addFileSystemListener(this);
      }
   }

   public static final void main(String[] args) {
      if (DeviceInfo.hasCamera()) {
         if (args != null && args.length == 1 && args[0].equals("init")) {
            ExplorerRegistry.getInstance().addGlobalAlias(_videoCameraAppAlias, 0);
            ShowVideoCameraApp.register();
            VideoRecorderOptions.getOptions().enableSynchronization();
            VerbRepository.getVerbRepository(50498946589467127L).register(_videoAppVerb, -7287235942111338224L);
         } else {
            VideoRecorderMain vm = new VideoRecorderMain();
            vm.enterEventDispatcher();
         }
      }
   }

   @Override
   protected final boolean acceptsForeground() {
      return !ITPolicy.getBoolean(47, 2, false);
   }

   @Override
   public final void rootChanged(int state, String rootName) {
      this.mediaCardCheck();
   }

   private final void mediaCardCheck() {
      RootRegister register = RootRegister.getInstance();
      if (!register.isCardMounted()) {
         int msg = register.isMassStorageActive() ? 24 : 23;
         this._mediaDialog = new Dialog(0, VideoRecorderResources.getString(msg), 0, null, 0);
         this._mediaDialog.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
         this._mediaDialog.setEscapeEnabled(true);
         this._mediaDialog.setDialogClosedListener(this);
         this.invokeLater(new VideoRecorderMain$2(this));
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
         RootRegister register = RootRegister.getInstance();
         if (!register.isCardMounted()) {
            this.cleanup();
            System.exit(0);
         }
      }
   }

   private final void cleanup() {
      AudioRouter.getInstance().removeSource(7);
      Camera.removeListener(this, this._screen);
      this.removeFileSystemListener(this);
      if (this._screen != null) {
         this._screen.cleanup();
      }
   }
}
