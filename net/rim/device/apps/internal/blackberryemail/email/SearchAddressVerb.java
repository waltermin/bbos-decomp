package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.messaging.search.MessageSearch;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

public final class SearchAddressVerb extends Verb {
   private RIMModel _model;
   private Recognizer _recognizer;

   SearchAddressVerb(RIMModel model, int ordering, ResourceBundleFamily rb, int rbKey, Recognizer recognizer) {
      super(ordering, rb, rbKey);
      this._model = model;
      this._recognizer = recognizer;
   }

   @Override
   public final Object invoke(Object context) {
      EmailMessageModelImpl message = (EmailMessageModelImpl)this._model;
      Object[] addresses = SubmemberUtilities.getSubmembers(message, this._recognizer);
      if (addresses.length > 0) {
         int selectedAddressIndex = 0;
         if (addresses.length > 1) {
            Dialog selectionDialog = (Dialog)(new Object(MessageResources.getString(110), addresses, null, 0, Bitmap.getPredefinedBitmap(1)));
            selectionDialog.setEscapeEnabled(true);
            selectedAddressIndex = selectionDialog.doModal();
            if (selectedAddressIndex == -1) {
               return null;
            }
         }

         EmailHeaderModel headerModel = (LargeAttachmentModel$LargeCachedAttachmentModel)addresses[selectedAddressIndex];
         ConversionProvider converter = headerModel;
         String[] names = new Object[2];
         converter.convert(null, names);
         String address = names[1];
         if (address == null || address.length() == 0) {
            address = names[0];
         }

         MessageSearch search = MessageSearch.getInstance();
         if (search != null) {
            search.nameSearch(address, false, context, false);
         }
      }

      return null;
   }
}
