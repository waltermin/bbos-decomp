package net.rim.device.apps.internal.voicenotesrecorder;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.voicenotesrecorder.resource.RecordVoiceNotesResources;

public final class VoiceNotesRecorderApplicationVerb extends Verb {
   public VoiceNotesRecorderApplicationVerb() {
      super(16986368, ResourceBundle.getBundle(-5703172391667637902L, "net.rim.device.apps.internal.resource.VoiceNotesRecorder"), 0);
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject context;
      if (!(parameter instanceof Object)) {
         context = (ContextObject)(new Object());
      } else {
         context = (ContextObject)parameter;
      }

      ContextObject.put(context, 4086083307293257364L, new Object(true));
      ShowVoiceNotesRecorderApp.setVoiceNoteRecorderContext(context);
      ShowVoiceNotesRecorderApp.ShowVoiceNotesRecorderApp();
      return null;
   }

   @Override
   public final String toString() {
      return RecordVoiceNotesResources.getString(19);
   }
}
