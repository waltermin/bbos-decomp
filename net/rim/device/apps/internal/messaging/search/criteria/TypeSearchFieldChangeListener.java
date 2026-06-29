package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;

public final class TypeSearchFieldChangeListener implements FieldChangeListener {
   @Override
   public final void fieldChanged(Field f, int context) {
      label56:
      try {
         EditorUsingRIMModelFactory editor = (EditorUsingRIMModelFactory)f.getScreen();
         RIMModelFactory sf = SubjectSearchModelFactory.getInstance();
         boolean removeSubjectModel = false;
         switch (TypeSearchModel.mapChoiceFieldSelectedIndexToType(((ChoiceField)f).getSelectedIndex())) {
            case 2:
            case 3:
            case 4:
            default:
               removeSubjectModel = true;
            case 1:
               if (removeSubjectModel) {
                  Object o = TypeSearchModel.getFirstModel(editor, sf);
                  if (o != null) {
                     SubjectSearchModel m = (SubjectSearchModel)o;
                     EditField ef = (EditField)TypeSearchModel.getFieldFromModel(editor, m);
                     TypeSearchModel._removedSubjectText = ef.getText();
                     editor.deleteModel(m);
                  }
               } else if (TypeSearchModel._removedSubjectText != null) {
                  Object o = TypeSearchModel.getFirstModel(editor, sf);
                  if (o == null) {
                     RIMModel m = (SubjectSearchModel)sf.createInstance(null);
                     editor.insertModel(m);
                     EditField ef = (EditField)TypeSearchModel.getFieldFromModel(editor, m);
                     if (ef != null) {
                        String removedSubjectText = TypeSearchModel._removedSubjectText;
                        ef.setText(removedSubjectText);
                        if (removedSubjectText != null && removedSubjectText.length() > 0) {
                           ef.setDirty(true);
                        }

                        TypeSearchModel._removedSubjectText = null;
                     }

                     f.setFocus();
                  }
               }
         }
      } finally {
         break label56;
      }

      if (TypeSearchModel._oldChangeListener != null) {
         TypeSearchModel._oldChangeListener.fieldChanged(f, context);
      }
   }
}
