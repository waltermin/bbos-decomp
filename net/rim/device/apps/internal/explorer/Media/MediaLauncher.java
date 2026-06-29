package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.vm.Process;

public final class MediaLauncher {
   private static final long GUID_LAUNCH_PLAYER = -2364922203810937362L;
   private static final long GUID_STOP_PLAYER = 7880603250674304426L;
   private static final long GUID_SHOW_PLAYER = 110711286572786542L;

   private MediaLauncher() {
      ApplicationProcess applicationProcess = (ApplicationProcess)Process.currentProcess();
      if (applicationProcess != null) {
         applicationProcess.addCleanupRunnable(new MediaLauncher$CleanupRunnable());
      }
   }

   public static final void launch(Object content, Object context) {
      postAction(-2364922203810937362L, content, context);
   }

   public static final void stop() {
      if (isPlayerRunning()) {
         postAction(7880603250674304426L, null, null);
      }
   }

   public static final void showPlayer() {
      if (isPlayerRunning()) {
         postAction(110711286572786542L, null, null);
      }
   }

   public static final boolean isPlayerRunning() {
      ApplicationDescriptor descriptor = getMediaPlayerApplicationDescriptor();
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      int pid = applicationManager.getProcessId(descriptor);
      return pid != -1;
   }

   private static final void postAction(long guid, Object content, Object context) {
      ApplicationDescriptor descriptor = getMediaPlayerApplicationDescriptor();
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      if (descriptor != null && applicationManager != null) {
         try {
            int pid = applicationManager.runApplication(descriptor, false);
            RIMGlobalMessagePoster.postGlobalEvent(pid, guid, 0, 0, content, context);
         } finally {
            return;
         }
      }
   }

   private static final ApplicationDescriptor getMediaPlayerApplicationDescriptor() {
      int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_medialibraryplayer");
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
}
