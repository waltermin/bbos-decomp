package net.rim.device.apps.internal.camera;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.api.framework.model.ContextObject;

public final class ShowCameraApp {
   private ApplicationDescriptor _cameraAppDescriptor;
   private ContextObject _obj;
   private static final long SHOW_CAMERA_APP_ID = -7500318162183416676L;

   private ShowCameraApp(ApplicationDescriptor applicationDescriptor) {
      this._cameraAppDescriptor = applicationDescriptor;
   }

   public static final void register() {
      if (!isRegistered()) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_camera");
         ApplicationDescriptor applicationDescriptor = CodeModuleManager.getApplicationDescriptors(moduleHandle)[0];
         applicationRegistry.put(-7500318162183416676L, new ShowCameraApp(applicationDescriptor));
      }
   }

   public static final boolean isRegistered() {
      return ApplicationRegistry.getApplicationRegistry().get(-7500318162183416676L) != null;
   }

   private final int startCameraApp() {
      try {
         return ApplicationManager.getApplicationManager().runApplication(this._cameraAppDescriptor);
      } finally {
         ;
      }
   }

   private static final int showCameraApp(long uid) {
      ShowCameraApp runner = (ShowCameraApp)ApplicationRegistry.getApplicationRegistry().get(uid);
      return runner.startCameraApp();
   }

   public static final int showCameraApp() {
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      ShowCameraApp showCameraAppInstance = (ShowCameraApp)ApplicationRegistry.getApplicationRegistry().get(-7500318162183416676L);
      int cameraPid = applicationManager.getProcessId(showCameraAppInstance._cameraAppDescriptor);
      int foregroundPid = applicationManager.getForegroundProcessId();
      return cameraPid != foregroundPid ? showCameraApp(-7500318162183416676L) : cameraPid;
   }

   public static final boolean isCameraInForegroundProcess() {
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      ShowCameraApp showCameraAppInstance = (ShowCameraApp)ApplicationRegistry.getApplicationRegistry().get(-7500318162183416676L);
      int cameraPid = applicationManager.getProcessId(showCameraAppInstance._cameraAppDescriptor);
      int foregroundPid = applicationManager.getForegroundProcessId();
      return cameraPid == foregroundPid;
   }

   public static final ContextObject setCameraContext(ContextObject obj) {
      ShowCameraApp runner = (ShowCameraApp)ApplicationRegistry.getApplicationRegistry().get(-7500318162183416676L);
      ContextObject temp = runner._obj;
      runner._obj = obj;
      return temp;
   }

   public static final ContextObject getCameraContext() {
      return setCameraContext(null);
   }
}
