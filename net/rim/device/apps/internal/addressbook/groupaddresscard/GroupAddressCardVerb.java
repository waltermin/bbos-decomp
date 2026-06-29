package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class GroupAddressCardVerb extends Verb {
   private GroupAddressCardModel _gacm;
   private int _type;
   Screen _screenToClose;
   static final int NEW_GROUP_VERB_TYPE = 0;
   static final int EDIT_GROUP_VERB_TYPE = 1;
   static final int VIEW_GROUP_VERB_TYPE = 2;
   private static final int[] resourceStringID = new int[]{
      706, 1005, 810, 712179968, 712179968, 16806977, -2104615050, 1870004480, 16803179, 16827829, 13157813, -1073325306
   };
   private static final int[] menuOrdering = new int[]{610816, 611328, 611088, -805044223, 1, -805044223, 3, -804651004, 607760, 607776, 607792, 610640};

   GroupAddressCardVerb(int type, Object model) {
      super(menuOrdering[type], AddressBookResources.getResourceBundleFamily(), resourceStringID[type]);
      this._type = type;
      if (model != null) {
         this._gacm = (GroupAddressCardModel)model;
      }
   }

   GroupAddressCardVerb(int type, Object model, Screen screen) {
      this(type, model);
      this._screenToClose = screen;
   }

   @Override
   public final Object invoke(Object parameter) {
      GroupAddressCardScreen screen;
      switch (this._type) {
         case -1:
            return null;
         case 0:
         default:
            GroupAddressCardModel newEntry = new GroupAddressCardModelImpl(parameter);
            screen = new EditGroupAddressCardScreen(newEntry, true);
            break;
         case 1:
            if (this._screenToClose != null) {
               UiApplication.getUiApplication().popScreen(this._screenToClose);
            }

            screen = new EditGroupAddressCardScreen(this._gacm, false);
            break;
         case 2:
            screen = new GroupAddressCardScreen(this._gacm);
      }

      return screen.go();
   }
}
