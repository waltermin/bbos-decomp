package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.i18n.CommonResource;

final class EditCallNotesScreen$ExitNotesVerb extends Verb {
   private final EditCallNotesScreen this$0;

   public EditCallNotesScreen$ExitNotesVerb(EditCallNotesScreen _1) {
      super(268501008);
      this.this$0 = _1;
   }

   @Override
   public final int getOrdering() {
      return this.this$0._liveCall.getFlag(4) && this.this$0._notesField.isDirty() ? 332288 : 268501008;
   }

   @Override
   public final String toString() {
      return this.this$0._liveCall.getFlag(4) && this.this$0._notesField.isDirty() ? CommonResource.getBundle().getString(18) : PhoneResources.getString(440);
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.saveNotes();
      this.this$0._voiceApp.stopEditingCallNotes();
      return null;
   }
}
