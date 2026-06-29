package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.internal.i18n.CommonResource;

final class AddressCardVerb extends Verb {
   private ContextObject _context;
   private AddressCardModel _model;
   private Verb _storeAction;
   private Screen _screenToClose;
   private int _type;
   static final int NEW_ADDRESS_CARD_VERB;
   static final int VIEW_ADDRESS_CARD_VERB;
   static final int EDIT_ADDRESS_CARD_VERB;
   static final int ADD_ADDRESS_CARD_VERB;

   public AddressCardVerb(int type, int menuOrdering) {
      super(menuOrdering);
      this._type = type;
   }

   public AddressCardVerb(int type, int menuOrdering, Object model) {
      this(type, menuOrdering);
      this._model = (AddressCardModel)model;
   }

   public AddressCardVerb(int type, int menuOrdering, Object model, Object context) {
      this(type, menuOrdering, model);
      switch (this._type) {
         case 0:
            break;
         case 1:
         default:
            this._context = ContextObject.clone(context);
            this._context.clearFlags(0, 3);
            if (ContextObject.getFlag(context, 43)) {
               this._context.setFlag(54);
               super._ordering = 16863840;
               return;
            }
            break;
         case 2:
         case 3:
            this._storeAction = (Verb)context;
      }
   }

   public AddressCardVerb(int type, int menuOrdering, Object model, Object context, Screen screen) {
      this(type, menuOrdering, model, context);
      this._screenToClose = screen;
   }

   @Override
   public final String toString() {
      switch (this._type) {
         case -1:
            return "";
         case 0:
         default:
            return AddressBookResources.getString(401);
         case 1:
            if (this._context.getFlag(54)) {
               return AddressBookResources.getString(306);
            }

            return CommonResource.getString(14);
         case 2:
            return CommonResource.getString(16);
         case 3:
            return AddressBookResources.getString(1706);
      }
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._type) {
         case -1:
            break;
         case 0:
         default:
            if (AddressBookServices.getAddressBook().isFull()) {
               Status.show(AddressBookResources.getString(1735), Bitmap.getPredefinedBitmap(2), 5000, 0, true, false, 100);
               return null;
            }

            AddressCardModel newEntry = new AddressCardModelImpl(parameter);
            String title = AddressBookResources.getString(401);
            Verb verb = (Verb)ContextObject.get(parameter, 7760782369919591658L);
            if (verb != null) {
               EditAddressCardScreen screen = new EditAddressCardScreen(verb, title);
               screen.setAddressCardModel(newEntry, null, false);
               screen.go();
               return null;
            }
            break;
         case 1:
            ViewAddressCardScreen mainScreen = new ViewAddressCardScreen(this._context, this._model);
            return mainScreen.go();
         case 2:
            AddressCardModel theModel = this._model;
            if (AddressCardUtilities.isInGroup(theModel)) {
               theModel = new AddressCardModelImpl(theModel);
               AddressCardUtilities.expandGroup(theModel);
            }

            EditAddressCardScreen editScreen = new EditAddressCardScreen(this._storeAction, AddressBookResources.getString(400));
            Object originalModel = ContextObject.get(parameter, -4055106280780392421L);
            if (originalModel == null) {
               originalModel = ContextObject.get(parameter, 3696141428889703675L);
            }

            editScreen.setAddressCardModel(theModel, originalModel, true);
            editScreen.go();
            if (this._screenToClose != null) {
               UiApplication.getUiApplication().popScreen(this._screenToClose);
               return null;
            }
            break;
         case 3:
            if (this._storeAction != null) {
               this._storeAction.invoke(this._model);
            }
      }

      return null;
   }
}
