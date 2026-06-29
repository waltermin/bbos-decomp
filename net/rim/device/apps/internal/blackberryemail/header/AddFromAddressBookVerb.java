package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class AddFromAddressBookVerb extends Verb {
   EmailHeaderModel _origin;
   EmailEditorScreen _editor;
   Object _context;

   public AddFromAddressBookVerb(EmailHeaderModel origin, EmailEditorScreen editor, Object context) {
      super(16859696);
      this._origin = origin;
      this._editor = editor;
      this._context = context;
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject creationContext = ContextObject.clone(this._context);
      creationContext.setFlag(5);
      creationContext.setFlag(115);
      creationContext.put(-4054673099568009991L, HeaderTypes._typesAsInteger[this._origin.getHeaderType()]);
      EmailHeaderModel header = EmailHeaderModelFactory.createEmailHeaderModel(creationContext);
      PersistableRIMModel insideModel = header.getInsideModel();
      if (insideModel == null) {
         return null;
      }

      EmailHeaderModel blankModel = null;
      if (this._origin.isBlank()) {
         blankModel = this._origin;
      } else {
         Field blankField = this._editor.findBlankHeader(this._origin.getHeaderType());
         if (blankField != null) {
            blankModel = (EmailHeaderModel)blankField.getCookie();
         }
      }

      if (blankModel != null) {
         this._editor.deleteModel(blankModel);
      }

      this._editor.insertModel(header);
      this._editor.addBlankHeader(this._origin.getHeaderType(), true);
      return header;
   }

   @Override
   public final String toString() {
      return EmailResources.getString(101);
   }
}
