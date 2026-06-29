package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.api.framework.model.ContextObject;

public final class ShowVideoCameraApp {
   private ApplicationDescriptor _videoCameraAppDescriptor;
   private ContextObject _obj;
   private static final long SHOW_VIDEO_CAMERA_APP_ID;

   private ShowVideoCameraApp(ApplicationDescriptor applicationDescriptor) {
      this._videoCameraAppDescriptor = applicationDescriptor;
   }

   public static final void register() {
      if (!isRegistered()) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_videorecorder");
         ApplicationDescriptor applicationDescriptor = CodeModuleManager.getApplicationDescriptors(moduleHandle)[0];
         applicationRegistry.put(4557057938713831249L, new ShowVideoCameraApp(applicationDescriptor));
      }
   }

   public static final boolean isRegistered() {
      return ApplicationRegistry.getApplicationRegistry().get(4557057938713831249L) != null;
   }

   private final int startVideoCameraApp() {
      try {
         return ApplicationManager.getApplicationManager().runApplication(this._videoCameraAppDescriptor);
      } finally {
         ;
      }
   }

   private static final int showVideoCameraApp(long uid) {
      ShowVideoCameraApp runner = (ShowVideoCameraApp)ApplicationRegistry.getApplicationRegistry().get(uid);
      return runner.startVideoCameraApp();
   }

   public static final int showVideoCameraApp() {
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      ShowVideoCameraApp showVideoCameraAppInstance = (ShowVideoCameraApp)ApplicationRegistry.getApplicationRegistry().get(4557057938713831249L);
      int cameraPid = applicationManager.getProcessId(showVideoCameraAppInstance._videoCameraAppDescriptor);
      int foregroundPid = applicationManager.getForegroundProcessId();
      return cameraPid != foregroundPid ? showVideoCameraApp(4557057938713831249L) : cameraPid;
   }

   public static final boolean isVideoCameraInForegroundProcess() {
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      ShowVideoCameraApp showVideoCameraAppInstance = (ShowVideoCameraApp)ApplicationRegistry.getApplicationRegistry().get(4557057938713831249L);
      int cameraPid = applicationManager.getProcessId(showVideoCameraAppInstance._videoCameraAppDescriptor);
      int foregroundPid = applicationManager.getForegroundProcessId();
      return cameraPid == foregroundPid;
   }

   public static final ContextObject setVideoCameraContext(ContextObject obj) {
      ShowVideoCameraApp runner = (ShowVideoCameraApp)ApplicationRegistry.getApplicationRegistry().get(4557057938713831249L);
      ContextObject temp = runner._obj;
      runner._obj = obj;
      return temp;
   }

   public static final ContextObject getAndClearVideoCameraContext() {
      return setVideoCameraContext(null);
   }
}
