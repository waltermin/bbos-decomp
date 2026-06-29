package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSLocationData;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.addressbook.mailingaddress.MailingAddressModelImpl;
import net.rim.device.apps.internal.lbs.FavouritesManager;
import net.rim.device.apps.internal.lbs.FavouritesScreen;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.locator.GPSFixProgressDialog;
import net.rim.device.apps.internal.lbs.model.SearchAddressModel;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.vm.Array;

public final class FindLocationScreen extends ModelScreen implements ListFieldCallback, SearchableHistoryList$Callback {
   private ListField _listActions;
   private SearchAddressModel _model;
   private SearchableHistoryList _searchHistoryList;
   private GPSLocationData _gpsLocationData;
   private int _currentScreenZoom;
   private boolean _showGPS;
   static final int ACTION_GPS_LOCATION;
   static final int ACTION_ENTER_ADDRESS;
   static final int ACTION_ADDRESS_BOOK;
   static final int ACTION_FAVOURITES;

   public FindLocationScreen(GPSLocationData gpsLocationData, int currentScreenZoom) {
      super(281474976907264L, LBSResources.getString(317), null);
      int numActions = 2;
      GPSDevice device = GPSProvider.getInstance().getDeviceInUse();
      this._showGPS = device != null && !device.equals(GPSDevice.NO_DEVICE) && (GPSProvider.isGPSSupported() || !device.isInternalGPS());
      if (this._showGPS) {
         numActions++;
      }

      if (FavouritesManager.hasFavourites()) {
         numActions++;
      }

      this.add((Field)(new Object(10)));
      this.add(this._listActions = (ListField)(new Object(numActions)));
      this._listActions.setCallback(this);
      this.add((Field)(new Object()));
      this.add(this._searchHistoryList = new SearchableHistoryList(this));
      this._model = new SearchAddressModel(null);
      this._gpsLocationData = gpsLocationData;
      this._currentScreenZoom = currentScreenZoom;
   }

   private final void handleAction(int action, boolean usesConstant) {
      if (!this._showGPS && !usesConstant) {
         action++;
      }

      switch (action) {
         case -1:
            break;
         case 0:
            Location whereAmI = null;
            if (this._gpsLocationData != null && this._gpsLocationData.isValid()) {
               whereAmI = new Location(
                  this._gpsLocationData.getLatitudeInt(), this._gpsLocationData.getLongitudeInt(), this._currentScreenZoom, LBSResources.getString(393)
               );
            } else {
               whereAmI = getGPSLocation();
            }

            if (whereAmI != null) {
               whereAmI._zoom = this._currentScreenZoom;
               super._returnValue = whereAmI;
               UiApplication.getUiApplication().popScreen(this);
               LBSApplication.displayMap(whereAmI);
               return;
            }
            break;
         case 1:
         default:
            FindAddress find = new FindAddress(LBSResources.getString(325), false);
            Location locationxx = (Location)find.invoke(this._model);
            if (locationxx != null) {
               UiApplication.getUiApplication().popScreen(this);
               LBSApplication.displayMap(locationxx);
               return;
            }
            break;
         case 2:
            Location locationx = getFromAddressBook();
            if (locationx != null) {
               UiApplication.getUiApplication().popScreen(this);
               LBSApplication.displayMap(locationx);
               return;
            }
            break;
         case 3:
            FavouritesScreen favs = new FavouritesScreen();
            Location location = favs.getSelectedLocation();
            if (location != null) {
               FinderHistory.getInstance().add(location);
               UiApplication.getUiApplication().popScreen(this);
               LBSApplication.displayMap(location);
            }
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      if (field == this._listActions) {
         this.handleAction(this._listActions.getSelectedIndex(), false);
         return true;
      } else {
         return super.navigationClick(status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      Field field = this.getFieldWithFocus();
      if (field == this._searchHistoryList) {
         this.onMenu(0);
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      Field field = this.getLeafFieldWithFocus();
      if (instance == 0 && field == this._listActions) {
         menu.add(new FindLocationScreen$1(this, LBSResources.getString(72), 0, 524287));
      }

      super.makeMenu(menu, instance);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            Field field = this.getLeafFieldWithFocus();
            if (field == this._listActions) {
               this.handleAction(this._listActions.getSelectedIndex(), false);
               return true;
            }
         default:
            return this._searchHistoryList.keyChar(key, status, time);
         case '\u001b':
            this.close();
            return true;
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (!this._showGPS) {
         index++;
      }

      switch (index) {
         case 0:
         default:
            graphics.drawText(LBSResources.getString(416), 0, y, 64);
            return;
         case 1:
            graphics.drawText(LBSResources.getString(325), 0, y, 64);
            return;
         case 2:
            graphics.drawText(LBSResources.getString(404), 0, y, 64);
            return;
         case 3:
            graphics.drawText(LBSResources.getString(326), 0, y, 64);
         case -1:
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      if (!this._showGPS) {
         index++;
      }

      switch (index) {
         case -1:
            return null;
         case 0:
         default:
            return LBSResources.getString(416);
         case 1:
            return LBSResources.getString(325);
         case 2:
            return LBSResources.getString(404);
         case 3:
            return LBSResources.getString(326);
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return 0;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   public final boolean onSavePrompt() {
      return true;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._searchHistoryList.reinit();
      } else {
         FinderHistory history = FinderHistory.getInstance();
         history.unsort();
      }
   }

   @Override
   public final void onLocation(Location location, int action) {
      switch (action) {
         case 0:
            UiApplication.getUiApplication().popScreen(this);
            LBSApplication.displayMap(location);
         case -1:
            return;
         case 1:
         default:
            this._model.setAddressLine1(location._address);
            this._model.setCity(location._city);
            this._model.setArea(location._region);
            this._model.setCountry(location._country);
            this.handleAction(1, true);
      }
   }

   public static final Location getGPSLocation() {
      Location whereAmI = null;
      GPSProvider gps = GPSProvider.getInstance();
      GPSDevice device = gps.getDeviceInUse();
      if (device != null) {
         GPSFixProgressDialog fixDialog = new GPSFixProgressDialog(device, LBSResources.getString(395));
         GPSLocationData locationData = fixDialog.doModal();
         if (!fixDialog.isCancelPressed() && locationData != null && locationData.isValid()) {
            whereAmI = new Location(locationData.getLatitudeInt(), locationData.getLongitudeInt(), 0, LBSResources.getString(393));
         }
      }

      return whereAmI;
   }

   public static final Location getFromAddressBook() {
      VerbRepository repo = VerbRepository.getVerbRepository(-1789952090272871921L);
      Verb[] items = repo.getVerbs(-1789952090272871921L);
      Recognizer[] recognizers = new Object[]{RecognizerRepository.getRecognizers(-3124646573404667739L)};
      AddressSelectionContext asc = (AddressSelectionContext)(new Object(
         LBSResources.getString(21), LBSResources.getString(22), LBSResources.getString(23), recognizers, null
      ));
      Object addressModel = items[0].invoke(asc);
      MailingAddressModelImpl[] mailingAddressModel = null;
      if (addressModel instanceof Object) {
         mailingAddressModel = new Object[0];
         ReadableList list = (ReadableList)addressModel;

         for (int i = 0; i < list.size(); i++) {
            Object o = list.getAt(i);
            if (o instanceof Object) {
               Array.resize(mailingAddressModel, mailingAddressModel.length + 1);
               mailingAddressModel[mailingAddressModel.length - 1] = (MailingAddressModelImpl)o;
            }
         }
      }

      if (mailingAddressModel == null) {
         return null;
      }

      String addressLoc = "";
      if (mailingAddressModel.length == 0) {
         Dialog.alert(LBSResources.getString(273));
         return null;
      }

      MailingAddressModelImpl address;
      if (mailingAddressModel.length != 2) {
         address = mailingAddressModel[0];
      } else {
         String address1 = getFromAddressLine(mailingAddressModel[0]);
         String city = mailingAddressModel[0].getCity();
         String work = ((StringBuffer)(new Object()))
            .append(LBSResources.getString(34))
            .append(": ")
            .append(address1 != null && !address1.equals("") ? ((StringBuffer)(new Object())).append(address1).append(", ").toString() : "")
            .append(city != null ? city : "")
            .toString();
         address1 = mailingAddressModel[1].getAddressLine1();
         city = mailingAddressModel[1].getCity();
         String home = ((StringBuffer)(new Object()))
            .append(LBSResources.getString(33))
            .append(": ")
            .append(address1 != null && !address1.equals("") ? ((StringBuffer)(new Object())).append(address1).append(", ").toString() : "")
            .append(city != null ? city : "")
            .toString();
         String[] choices = mailingAddressModel[0].getType() == 1 ? new Object[]{home, work} : new Object[]{work, home};
         String title = MessageFormat.format(LBSResources.getString(278), new Object[]{((AddressCardModel)addressModel).getName().toString()});
         FindLocationScreen$AddressChoiceDialog d = new FindLocationScreen$AddressChoiceDialog(
            title, choices, new int[]{0, 1, -804651004, 0, 6, -6, 0, -804651000}, 0
         );
         int choice = d.doModal();
         if (choice == -1) {
            return null;
         }

         address = mailingAddressModel[choice];
         addressLoc = ((StringBuffer)(new Object(" (")))
            .append(address.getType() == 1 ? LBSResources.getString(33) : LBSResources.getString(34))
            .append(")")
            .toString();
      }

      FindAddress finder = new FindAddress(null, false);
      return (Location)finder.invoke(address);
   }

   private static final String getFromAddressLine(MailingAddressModel model) {
      String addressText = "";
      if (model != null) {
         addressText = model.getAddressLine1();
         if (addressText == null || addressText.length() == 0) {
            addressText = model.getAddressLine2();
         }

         if (addressText == null) {
            addressText = "";
         }
      }

      return addressText;
   }
}
