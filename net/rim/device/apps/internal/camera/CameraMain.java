package net.rim.device.apps.internal.camera;

import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.file.ExplorerRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.file.RootRegister;
import net.rim.vm.PersistentInteger;
import net.rim.vm.Process;

public final class CameraMain extends UiApplication implements FileSystemListener, DialogClosedListener {
   private CameraOptions _options = CameraOptions.getOptions();
   private CameraScreen _screen;
   private Dialog _mediaDialog;
   private static final long MEDIA_CARD_PRESENCE = -4855661188771997500L;
   private static final int MEDIACARD_NOT_FOUND = 0;
   private static final int MEDIACARD_FOUND = 1;
   private static int _mediaCardFlag = PersistentInteger.getId(-4855661188771997500L, 0);
   static ResourceBundle _rb = ResourceBundle.getBundle(7839140414916824787L, "net.rim.device.apps.internal.camera.Camera");
   private static CameraMain$CameraAliasFileEntry _cameraAppAlias = new CameraMain$CameraAliasFileEntry(
      _rb.getString(0), new CameraApplicationVerb(), "Camera", null
   );

   CameraMain() {
      ApplicationProcess applicationProcess = (ApplicationProcess)Process.currentProcess();
      if (applicationProcess != null) {
         applicationProcess.addCleanupRunnable(new CameraMain$CameraCleanupRunnable(this, null));
      }

      if (ITPolicy.getBoolean(47, 1, false)) {
         this.invokeLater(new CameraMain$1(this));
      } else {
         this._screen = CameraScreen.getInstance();
         this.pushScreen(this._screen);
         this.checkMediaCard();
         this.addFileSystemListener(this);
      }
   }

   @Override
   protected final boolean acceptsForeground() {
      return !ITPolicy.getBoolean(47, 1, false);
   }

   public static final void main(String[] args) {
      if (DeviceInfo.hasCamera()) {
         boolean flagged = isMediaCardPresent();
         boolean inserted = RootRegister.getInstance().testIfCardInserted();
         if (flagged && !inserted) {
            PersistentInteger.set(_mediaCardFlag, 0);
         }

         if (args != null && args.length == 1 && args[0].equals("init")) {
            ExplorerRegistry.getInstance().addGlobalAlias(_cameraAppAlias, 0);
            ShowCameraApp.register();
            CameraOptions.getOptions().enableSynchronization();
         } else {
            CameraMain cm = new CameraMain();
            Camera.addListener(cm, CameraScreen.getInstance());
            cm.enterEventDispatcher();
         }
      }
   }

   @Override
   public final void activate() {
      if (this._screen != null && this._screen.isUiEngineAttached()) {
         this._screen.suspendViewfinder(false);
      }
   }

   @Override
   public final void deactivate() {
      if (this._screen != null && this._screen.isUiEngineAttached()) {
         this._screen.suspendViewfinder(true);
      }
   }

   private final void checkMediaCard() {
      ContextObject ctxt = ShowCameraApp.getCameraContext();
      String path = (String)ContextObject.get(ctxt, 2765042845091913199L);
      RootRegister root = RootRegister.getInstance();
      boolean isMounted = FileUtilities.isSDCardMounted();
      boolean inUse = root.isMassStorageActive();
      boolean isFlagged = isMediaCardPresent();
      boolean isSelected = this._options.getMemoryType() == 1;
      if (!isFlagged && isMounted) {
         if (!isSelected && this._mediaDialog == null) {
            this._mediaDialog = new Dialog(3, _rb.getString(26), 4, null, 0);
            this._mediaDialog.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
            this._mediaDialog.setEscapeEnabled(true);
            this._mediaDialog.setDialogClosedListener(this);
            this.invokeLater(new CameraMain$2(this));
         }

         PersistentInteger.set(_mediaCardFlag, 1);
      } else if (!isMounted && isSelected && path == null) {
         if (!inUse) {
            Application.getApplication().invokeLater(new CameraMain$3(this));
            this._options.setMemoryType(0);
            this._options.setDestinationFolder(CameraOptions.getDefaultPath(0));
            this._options.commit();
            PersistentInteger.set(_mediaCardFlag, 0);
         }

         if (this._mediaDialog != null) {
            this._mediaDialog.cancel();
            if (isFlagged) {
               PersistentInteger.set(_mediaCardFlag, 0);
            }
         }
      } else if (this._mediaDialog != null) {
         this._mediaDialog.cancel();
         if (isFlagged) {
            PersistentInteger.set(_mediaCardFlag, 0);
         }
      }

      this._screen.setContext(ctxt);
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (this._mediaDialog == dialog) {
         this._mediaDialog = null;
         if (choice == 4) {
            this._options.setMemoryType(1);
            this._options.setDestinationFolder(CameraOptions.getDefaultPath(1));
            this._options.commit();
         }
      }
   }

   static final boolean isMediaCardPresent() {
      return PersistentInteger.get(_mediaCardFlag) == 1;
   }

   @Override
   public final void rootChanged(int state, String rootName) {
      this.checkMediaCard();
   }
}
