package net.rim.device.apps.internal.memo;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.SearchResultField;
import net.rim.vm.Array;

final class MemoSearchResultField extends CollectionListField implements SearchResultField, VerbProvider {
   public MemoSearchResultField(MemoSearchResultCollection results, ListFieldCallback callback) {
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      Object selectedElement = this.getSelectedElement();
      if (selectedElement instanceof MemoModelImpl) {
         if (key == '\n') {
            this.getScreen().invokeDefaultMenuItem(0);
            return true;
         }

         if (key == 127 || Keypad.getAltedChar(key) == 127) {
            new DeleteMemoVerb((MemoModelImpl)selectedElement).invoke(null);
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
      if (!(selectedElement instanceof MemoModelImpl)) {
         return null;
      }

      MemoModelImpl memo = (MemoModelImpl)selectedElement;
      Array.resize(verbs, 2);
      verbs[0] = new EditMemoVerb(memo);
      verbs[1] = new DeleteMemoVerb(memo);
      return verbs[0];
   }
}
