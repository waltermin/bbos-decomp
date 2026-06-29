package net.rim.device.apps.internal.voicenotesrecorder;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.file.AliasFileEntry;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;
import net.rim.device.apps.api.framework.verb.Verb;

final class VoiceNotesRecorderMain$VoiceNotesRecorderAliasFileEntry extends AliasFileEntry {
   private VoiceNotesRecorderMain$VoiceNotesRecorderAliasFileEntry(String name, Verb verb, String imageName) {
   }

   @Override
   public final boolean isActiveInDirectory(String path, FileSelectionFilter filter) {
      if (ITPolicy.getBoolean(21, 10, false)) {
         return false;
      }

      if (ShowVoiceNotesRecorderApp.isVoiceNoteRecorderInForegroundProcess()) {
         return false;
      }

      String filename = path;
      if (filename.startsWith("/")) {
         filename = "file://" + filename;
      }

      return StringUtilities.startsWithIgnoreCase(filename, VoiceNotesRecorderMain.SDFOLDER, 1701707776)
         || StringUtilities.startsWithIgnoreCase(filename, VoiceNotesRecorderMain.DEVICEFOLDER, 1701707776);
   }

   VoiceNotesRecorderMain$VoiceNotesRecorderAliasFileEntry(String x0, Verb x1, String x2, VoiceNotesRecorderMain$1 x3) {
      this(x0, x1, x2);
   }
}
