package net.rim.device.apps.internal.voicenotesrecorder;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.file.ExplorerRegistry;
import net.rim.device.apps.internal.voicenotesrecorder.resource.RecordVoiceNotesResources;

public final class VoiceNotesRecorderMain extends UiApplication implements GlobalEventListener {
   public static String SDFOLDER = "file:///SDCard/BlackBerry/voicenotes/";
   public static String DEVICEFOLDER = "file:///store/home/user/voicenotes/";
   private static VoiceNotesRecorderMain$VoiceNotesRecorderAliasFileEntry _voiceNotesRecorderAppAlias = new VoiceNotesRecorderMain$VoiceNotesRecorderAliasFileEntry(
      RecordVoiceNotesResources.getString(7), new VoiceNotesRecorderApplicationVerb(), "VoiceNotesRecorder", null
   );

   VoiceNotesRecorderMain() {
      if (!this.isSupported()) {
         this.invokeLater(new VoiceNotesRecorderMain$1(this));
      } else {
         RecordVoiceNoteScreen screen = new RecordVoiceNoteScreen();
         this.pushScreen(screen);
      }
   }

   @Override
   protected final boolean acceptsForeground() {
      return this.isSupported();
   }

   public final boolean isSupported() {
      return !ITPolicy.getBoolean(21, 10, false) && VoiceRecordController.isSupported();
   }

   public static final void main(String[] args) {
      if (args != null && args.length == 1 && args[0].equals("init")) {
         if (!ITPolicy.getBoolean(21, 10, false) && VoiceRecordController.isSupported()) {
            ExplorerRegistry.getInstance().addGlobalAlias(_voiceNotesRecorderAppAlias, 0);
            ShowVoiceNotesRecorderApp.register();
            return;
         }
      } else {
         VoiceNotesRecorderMain main = new VoiceNotesRecorderMain();
         main.enterEventDispatcher();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L) {
         ExplorerRegistry.getInstance().addGlobalAlias(_voiceNotesRecorderAppAlias, 0);
         ShowVoiceNotesRecorderApp.register();
      }
   }
}
