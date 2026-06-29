package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.SelectionListener;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.AddressBookUtilities;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

public class RequestVerb extends Verb {
   Request _request;
   private int _resId;
   private boolean _terminalResult;
   private SelectionListener _selectionListener;

   public RequestVerb(Request request, int ordering, int resId, boolean terminalResult, SelectionListener selectionListener) {
      super(ordering, AddressBookResources.getResourceBundleFamily(), resId);
      this._resId = resId;
      this._request = request;
      this._terminalResult = terminalResult;
      this._selectionListener = selectionListener;
   }

   public RequestVerb(Request request, int ordering, int resId) {
      this(request, ordering, resId, false, null);
   }

   @Override
   public Object invoke(Object parameter) {
      ContextObject invokeContextObject = ContextObject.castOrCreate(parameter);
      ALPManager manager = ALPConfiguration.getManager();
      boolean terminateScreen = false;
      Object result = null;
      switch (this._resId) {
         case 1703:
            AddressBookOptions options = AddressBookServices.getAddressBookOptions();
            long sortOrder = options.getSortOrder();
            KeywordFilterList filterList = (KeywordFilterList)(new Object(this._request, new SearchViewIndexerHelper(parameter, sortOrder), true));
            invokeContextObject.put(614335798810617774L, new Object(sortOrder));
            this._request._result.resort(parameter, sortOrder);
            this._request.getResult().addCollectionListener(filterList);
            Screen screen = new SearchViewScreen(this._request, filterList, parameter, this._selectionListener);
            UiApplication.getUiApplication().pushScreen(screen);
            screen.doPaint();
            screen.updateDisplay();
            break;
         case 1704:
            if (AddressBookUtilities.confirmDelete(this._request)) {
               manager.deleteRequest(this._request);
               if (this._terminalResult) {
                  terminateScreen = true;
               }
            }
            break;
         case 1706:
            Object selectedAddress = this._request.getSelectedAddress();
            Request.addLookupResultToAddressBook(selectedAddress);
            result = selectedAddress;
            if (this._request.getIncludedMatches() == 1) {
               manager.deleteRequest(this._request);
            }
            break;
         case 1722:
            this._request._offsetIntoMatches = 0;
            manager.retryRequest(this._request);
      }

      if (terminateScreen) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      }

      return result;
   }
}
