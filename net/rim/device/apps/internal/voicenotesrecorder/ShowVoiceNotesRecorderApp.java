package net.rim.device.apps.internal.voicenotesrecorder;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.api.framework.model.ContextObject;

public final class ShowVoiceNotesRecorderApp {
   private ApplicationDescriptor _voiceNotesRecorderAppDescriptor;
   private ContextObject _obj;
   private static final long SHOW_VOICENOTESRECORDER_APP_ID;

   private ShowVoiceNotesRecorderApp(ApplicationDescriptor applicationDescriptor) {
      this._voiceNotesRecorderAppDescriptor = applicationDescriptor;
   }

   public static final void register() {
      if (!isRegistered()) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_voicenotesrecorder");
         ApplicationDescriptor applicationDescriptor = CodeModuleManager.getApplicationDescriptors(moduleHandle)[0];
         applicationRegistry.put(1432492449967531212L, new ShowVoiceNotesRecorderApp(applicationDescriptor));
      }
   }

   public static final boolean isRegistered() {
      return ApplicationRegistry.getApplicationRegistry().get(1432492449967531212L) != null;
   }

   private final int startVoiceNotesRecorderApp() {
      try {
         return ApplicationManager.getApplicationManager().runApplication(this._voiceNotesRecorderAppDescriptor);
      } finally {
         ;
      }
   }

   private static final int ShowVoiceNotesRecorderApp(long uid) {
      ShowVoiceNotesRecorderApp runner = (ShowVoiceNotesRecorderApp)ApplicationRegistry.getApplicationRegistry().get(uid);
      return runner.startVoiceNotesRecorderApp();
   }

   public static final int ShowVoiceNotesRecorderApp() {
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      ShowVoiceNotesRecorderApp showVoiceNotesRecorderAppInstance = (ShowVoiceNotesRecorderApp)ApplicationRegistry.getApplicationRegistry()
         .get(1432492449967531212L);
      int voiceNotesRecorderPid = applicationManager.getProcessId(showVoiceNotesRecorderAppInstance._voiceNotesRecorderAppDescriptor);
      int foregroundPid = applicationManager.getForegroundProcessId();
      return voiceNotesRecorderPid != foregroundPid ? ShowVoiceNotesRecorderApp(1432492449967531212L) : voiceNotesRecorderPid;
   }

   public static final boolean isVoiceNoteRecorderInForegroundProcess() {
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      ShowVoiceNotesRecorderApp showVoiceNotesRecorderAppInstance = (ShowVoiceNotesRecorderApp)ApplicationRegistry.getApplicationRegistry()
         .get(1432492449967531212L);
      int voiceNoteRecorderPid = applicationManager.getProcessId(showVoiceNotesRecorderAppInstance._voiceNotesRecorderAppDescriptor);
      int foregroundPid = applicationManager.getForegroundProcessId();
      return voiceNoteRecorderPid == foregroundPid;
   }

   public static final ContextObject setVoiceNoteRecorderContext(ContextObject obj) {
      ShowVoiceNotesRecorderApp runner = (ShowVoiceNotesRecorderApp)ApplicationRegistry.getApplicationRegistry().get(1432492449967531212L);
      ContextObject temp = runner._obj;
      runner._obj = obj;
      return temp;
   }

   public static final ContextObject getVoiceNoteRecorderContext() {
      return setVoiceNoteRecorderContext(null);
   }
}
