package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class EditNotesScreen extends AppsMainScreen {
   private PhoneCallModelImpl _callLog;
   private Field _notesField;
   private EditNotesVerb$CallNotesListener _listener;

   EditNotesScreen(EditNotesVerb$CallNotesListener listener, PhoneCallModelImpl callLog, String notes) {
      super(2306142076376449024L);
      this.setTitle((Field)(new Object(PhoneResources.getString(425))));
      this._listener = listener;
      this._callLog = callLog;
      ContextObject context = (ContextObject)(new Object(35, 0));
      this._notesField = this._callLog.getField(context);
      this.add(this._notesField);
      if (this._notesField instanceof Object) {
         EditField ef = (EditField)this._notesField;
         int length = ef.getText().length();
         if (length > 0) {
            ef.setCursorPosition(length);
         }
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._notesField.isDirty()) {
         MenuItem saveItem = MenuItem.getPrefab(15);
         menu.add(saveItem);
         menu.setDefaultIgnoreContextMenuDefault(saveItem);
      }
   }

   @Override
   protected final void setDefaultMenuItem(SystemEnabledMenu menu, ContextObject menuContext) {
      Verb defaultVerb = (Verb)ContextObject.get(menuContext, -3185095355580406181L);
      if (defaultVerb != null) {
         menu.setDefault(defaultVerb);
      }
   }

   @Override
   protected final boolean onSave() {
      if (this._notesField instanceof Object) {
         String text = ((BasicEditField)this._notesField).getText();
         PhoneFolders.replaceCallLogNotes(this._callLog, text);
         this._listener.onNotesSaved(text);
      }

      return true;
   }
}
