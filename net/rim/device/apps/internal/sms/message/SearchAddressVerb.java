package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.search.MessageSearch;

public final class SearchAddressVerb extends Verb {
   private RIMModel _model;

   public SearchAddressVerb(RIMModel model, int ordering, ResourceBundleFamily rb, int rbKey) {
      super(ordering, rb, rbKey);
      this._model = model;
   }

   @Override
   public final Object invoke(Object context) {
      String[] names = new Object[2];
      ConversionProvider converter = (ConversionProvider)this._model;
      converter.convert(new Object(10), names);
      String address = names[1];
      if (address == null || address.length() == 0) {
         address = names[0];
      }

      MessageSearch search = MessageSearch.getInstance();
      if (search != null) {
         search.nameSearch(address, false, context, false);
      }

      return null;
   }
}
