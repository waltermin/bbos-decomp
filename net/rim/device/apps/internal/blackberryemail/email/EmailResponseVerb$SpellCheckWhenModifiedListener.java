package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;

class EmailResponseVerb$SpellCheckWhenModifiedListener implements FieldChangeListener {
   private final EmailResponseVerb this$0;

   private EmailResponseVerb$SpellCheckWhenModifiedListener(EmailResponseVerb _1) {
      this.this$0 = _1;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      Object cookie = field.getCookie();
      if (field instanceof TextField && cookie instanceof SubjectModel) {
         String newText = ((TextField)field).getText();
         String oldText = ((SubjectModel)cookie).getSubject();
         if (!newText.equals(oldText)) {
            field.setNonSpellCheckable(false);
            field.setChangeListener(null);
         }
      }
   }

   EmailResponseVerb$SpellCheckWhenModifiedListener(EmailResponseVerb x0, EmailResponseVerb$1 x1) {
      this(x0);
   }
}
