package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class EditNotesVerb extends Verb {
   private PhoneCallModelImpl _callLog;
   private String _notes;
   private EditNotesVerb$CallNotesListener _listener;

   EditNotesVerb(EditNotesVerb$CallNotesListener listener, PhoneCallModelImpl callLog, int ordering, int resourceId, String notes) {
      super(ordering, PhoneResources.getResourceBundle(), resourceId);
      this._listener = listener;
      this._callLog = callLog;
      this._notes = notes;
   }

   @Override
   public final Object invoke(Object parameter) {
      Screen screen = new EditNotesScreen(this._listener, this._callLog, this._notes);
      UiApplication.getUiApplication().pushModalScreen(screen);
      return null;
   }
}
