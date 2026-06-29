package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.SearchResultField;
import net.rim.device.apps.internal.addressbook.ui.DeleteEntryVerb;
import net.rim.vm.Array;

final class AddressBookSearchResultField extends CollectionListField implements SearchResultField, VerbProvider {
   public AddressBookSearchResultField(AddressBookSearchResultCollection results, ListFieldCallback callback) {
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      Object selectedElement = this.getSelectedElement();
      if (selectedElement != null) {
         if (key == '\n') {
            this.getScreen().invokeDefaultMenuItem(0);
            return true;
         }

         if (key == 127 || Keypad.getAltedChar(key) == 127) {
            new DeleteEntryVerb(selectedElement).invoke(null);
            return true;
         }
      }

      return true;
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            Object selectedObject = this.getSelectedObject();
            if (selectedObject != null) {
               this.getScreen().invokeDefaultMenuItem(0);
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   public final Object getSelectedObject() {
      return this.getSelectedElement();
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Object selectedElement = this.getSelectedElement();
      if (!(selectedElement instanceof Object)) {
         return null;
      }

      Verb defaultVerb = ((VerbProvider)selectedElement).getVerbs(context, verbs);
      int numVerbs = verbs.length;
      Array.resize(verbs, numVerbs + 1);
      verbs[numVerbs] = new DeleteEntryVerb(selectedElement);
      return defaultVerb;
   }
}
