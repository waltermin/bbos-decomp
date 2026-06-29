package net.rim.device.apps.internal.lbs;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.mailingaddress.MailingAddressModelImpl;
import net.rim.device.apps.internal.lbs.finder.FindAddress;
import net.rim.device.apps.internal.lbs.finder.FinderHistory;
import net.rim.device.apps.internal.lbs.finder.SearchableHistoryList;
import net.rim.device.apps.internal.lbs.finder.SearchableHistoryList$Callback;
import net.rim.device.apps.internal.lbs.locator.Directions;
import net.rim.device.apps.internal.lbs.model.SearchAddressModel;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.ui.component.VerticalSpacerField;
import net.rim.vm.Array;

public final class DirectionsChoiceScreen extends MainScreen implements ListFieldCallback, SearchableHistoryList$Callback {
   private MapField _field;
   private ListField _list = null;
   private LabelField _labelFrom = null;
   private LabelField _labelTo = null;
   private LabelField _labelInstruction = null;
   private SeparatorField _separator = null;
   private VerticalSpacerField _space = null;
   private Location[] _location = new Location[2];
   int _selectedIndex = -1;
   int _startSelectedIndex = -1;
   int _oldSelectedIndex = this._selectedIndex;
   int _pointNum = 0;
   private SearchAddressModel _model;
   private SearchableHistoryList _searchHistoryList;
   private boolean _showWhereAmI;
   private boolean _showFavorite;
   private Object _addressModel;
   private MailingAddressModelImpl _address;
   private String _addressLoc;
   public static final int SEARCH_HISTORYLIST;
   public static final int SEARCH_ENTEREDADDRESS;
   public static final int SEARCH_ADDRESSBOOK;
   public static final int SEARCH_UPDATEFROMHERE;
   public static final int SEARCH_REVERSDIRECTION;

   public final void callbackSelect(Location location) {
      if (location == null) {
         this._selectedIndex = this._oldSelectedIndex;
      } else {
         this._location[this._pointNum] = location;
         if (this._location[this._pointNum]._address == null || this._location[this._pointNum]._address.length() == 0) {
            this._location[this._pointNum]._address = this._location[this._pointNum]._label;
         }

         this.handleSelection();
      }
   }

   final void UpdateFromHere(Location destLocation) {
      int oldPointNum = this._pointNum;
      this._pointNum = 0;
      if (!this.getCurrentLocation()) {
         this._pointNum = oldPointNum;
      } else {
         this._location[1] = destLocation;
         Directions directions = new Directions(
            this._location[0]._label,
            this._location[0]._latitude,
            this._location[0]._longitude,
            this._location[1]._label,
            this._location[1]._latitude,
            this._location[1]._longitude
         );
         directions.setCallback(this, null, 3);
         String routeXmlString = directions.getDirections();
      }
   }

   public final void UpdateDirections(String routeXmlString) {
      if (routeXmlString != null) {
         this._field.openDocument("XML", routeXmlString, 0, true, "DIRECTIONS_SERVER");
      }

      if (this._field.getRoute() != null && this._field._directionsListScreen != null) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
         UiApplication.getUiApplication().pushScreen((Screen)this._field._directionsListScreen);
         this._field._pushDirectionsScreen = false;
      }

      this.removeFields();
      this.populateFields();
   }

   final void ReverseDirections(Location startLoc, Location destLoc) {
      this._location[0] = startLoc;
      this._location[1] = destLoc;
      Directions directions = new Directions(
         this._location[0]._label,
         this._location[0]._latitude,
         this._location[0]._longitude,
         this._location[1]._label,
         this._location[1]._latitude,
         this._location[1]._longitude
      );
      directions.setCallback(this, null, 4);
      String routeXmlString = directions.getDirections();
   }

   public final void searchDirections() {
      if (this._pointNum == 0) {
         this._pointNum++;
         this.removeFields();
         this.populateFields();
      } else if (this._pointNum != 1) {
         System.out.println("DirectionsChoiceScreen: _pointNum is > 1!");
      } else if (this._location[0]._latitude == this._location[1]._latitude && this._location[0]._longitude == this._location[1]._longitude) {
         Dialog.alert(LBSResources.getString(230));
         this._selectedIndex = this._oldSelectedIndex;
      } else {
         this._pointNum++;
         this.removeFields();
         this.populateFields();
         Directions directions = new Directions(
            this._location[0]._label,
            this._location[0]._latitude,
            this._location[0]._longitude,
            this._location[1]._label,
            this._location[1]._latitude,
            this._location[1]._longitude
         );
         directions.setCallback(this, null, -1);
         String var2 = directions.getDirections();
      }
   }

   public final void showDirections(String routeXmlString, Directions directions) {
      if (routeXmlString != null) {
         this._field.openDocument("XML", directions.getRawData(), 0, true, "DIRECTIONS_SERVER");
      }

      if (this._field.getRoute() != null && this._field._directionsListScreen != null) {
         UiApplication.getUiApplication().pushScreen((Screen)this._field._directionsListScreen);
         this._field._pushDirectionsScreen = false;
      }

      this.close();
   }

   public final boolean locationFound(Location location) {
      if (location == null) {
         this._selectedIndex = this._oldSelectedIndex;
         return false;
      } else {
         location._address = ((AddressCardModel)this._addressModel).getName().toString();
         String addressLine = this._address.getAddressLine1();
         String cityLine = this._address.getCity();
         location._address = ((StringBuffer)(new Object()))
            .append(location._address)
            .append(this._addressLoc)
            .append(addressLine != null && !addressLine.equals("") ? ((StringBuffer)(new Object(", "))).append(addressLine).toString() : "")
            .append(cityLine != null && !cityLine.equals("") ? ((StringBuffer)(new Object(", "))).append(cityLine).toString() : "")
            .toString();
         location._label = location._address;
         this._location[this._pointNum] = location;
         return true;
      }
   }

   final boolean getCurrentLocation() {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   final boolean getUserEnteredAddress() {
      String title = ((StringBuffer)(new Object())).append(LBSResources.getString(104)).append(": ").toString();
      if (this._pointNum == 0) {
         title = ((StringBuffer)(new Object())).append(title).append(LBSResources.getString(302)).toString();
      } else {
         title = ((StringBuffer)(new Object())).append(title).append(LBSResources.getString(303)).toString();
      }

      FindAddress find = new FindAddress(LBSResources.getString(325), true);
      find.setCallback(null, null, this, 1);
      Location location = (Location)find.invoke(this._model);
      return this.searchUserEnteredAddress(location);
   }

   public final boolean searchUserEnteredAddress(Location location) {
      if (location != null) {
         this._model = new SearchAddressModel(null);
         this._location[this._pointNum] = location;
         this._searchHistoryList.update();
         return true;
      } else {
         this._selectedIndex = this._oldSelectedIndex;
         return false;
      }
   }

   final boolean getFavorite() {
      FavouritesScreen favScrn = new MapField();
      Location location = favScrn.getSelectedLocation();
      if (location != null) {
         if (location instanceof Route) {
            UiApplication.getUiApplication().popScreen(this);
            LBSApplication.displayMap(location);
            return false;
         }

         FinderHistory.getInstance().add(location);
         this._location[this._pointNum] = location;
         if (this._location[this._pointNum]._address == null || this._location[this._pointNum]._address.length() == 0) {
            this._location[this._pointNum]._address = this._location[this._pointNum]._label;
         }

         this._searchHistoryList.update();
         return true;
      } else {
         this._selectedIndex = this._oldSelectedIndex;
         return false;
      }
   }

   final void getFocusedLocation() {
      Location loc = this._field.getFocusLocation();
      loc = loc.copy(loc);
      FinderHistory.getInstance().add(loc);
      this._location[this._pointNum] = loc;
      String label = loc._label != null ? loc._label : "";
      loc._address = ((StringBuffer)(new Object()))
         .append(label)
         .append(
            loc._address != null && !loc._address.equals("") && !label.equals(loc._address)
               ? ((StringBuffer)(new Object(", "))).append(loc._address).toString()
               : ""
         )
         .toString();
      this._searchHistoryList.update();
   }

   final void reset() {
      this._selectedIndex = -1;
      this._oldSelectedIndex = -1;
      this._pointNum = 0;
      this.removeFields();
      this.populateFields();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return super.getPreferredWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      switch (this._selectedIndex) {
         case -2:
            break;
         case -1:
         case 1:
         case 2:
         case 3:
         default:
            if (!this._showWhereAmI) {
               index++;
            }

            if (!this._showFavorite && index == 3) {
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
               case 4:
                  Location location = this._field.getFocusLocation();
                  if (location != null && location._label != null && location._label.length() > 0) {
                     return location._label;
                  }

                  return null;
            }
         case 0:
            if (!this._showFavorite && index == 2) {
               index++;
            }

            switch (index) {
               case -1:
                  return null;
               case 0:
               default:
                  return LBSResources.getString(325);
               case 1:
                  return LBSResources.getString(404);
               case 2:
                  return LBSResources.getString(326);
               case 3:
                  Location location = this._field.getFocusLocation();
                  if (location != null && location._label != null && location._label.length() > 0) {
                     return location._label;
                  }

                  return null;
            }
         case 4:
            if (!this._showWhereAmI) {
               index++;
            }

            switch (index) {
               case -1:
                  break;
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

      return null;
   }

   @Override
   public final void onLocation(Location location, int action) {
      switch (action) {
         case 1:
         default:
            this._model.setAddressLine1(location._address);
            this._model.setCity(location._city);
            this._model.setArea(location._region);
            this._model.setCountry(location._country);
            FindAddress find = new FindAddress(LBSResources.getString(325), true);
            find.setCallback(null, null, this, 0);
            location = (Location)find.invoke(this._model);
         case 0:
            this.callbackSelect(location);
         case -1:
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (graphics != null && this._field != null) {
         String text = (String)this.get(listField, index);
         graphics.drawText(((StringBuffer)(new Object("    "))).append(text).toString(), 0, y, 64);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this.getFieldWithFocus() == this._searchHistoryList) {
         this.onMenu(0);
         return true;
      } else {
         this.handleSelection();
         return true;
      }
   }

   private final GPSDevice getCurrentDevice() {
      GPSProvider locProvider = GPSProvider.getInstance();
      GPSDevice[] gpsDevices = locProvider.getLocationDevices(false);
      GPSDevice currentDevice = null;
      String deviceID = LBSOptions.getString(6531936621597631078L, null);
      if (deviceID != null) {
         for (int i = 0; i < gpsDevices.length; i++) {
            if (gpsDevices[i].equals(deviceID)) {
               currentDevice = gpsDevices[i];
               break;
            }
         }
      }

      if (currentDevice == null) {
         GPSDeviceSelectionDialog selectionScreen = new GPSDeviceSelectionDialog();
         ((MapScreen)this._field._screen)._gpsSelectionScreen = selectionScreen;
         if (selectionScreen.doModal()) {
            ((MapScreen)this._field._screen)._gpsSelectionScreen = null;
            currentDevice = selectionScreen.getSelectedDevice();
            if (currentDevice != null) {
               LBSOptions.setString(6531936621597631078L, currentDevice.getDeviceID().toString());
            }
         }
      }

      return currentDevice;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (this.getLeafFieldWithFocus() instanceof Object) {
         this.onMenu(0);
         return true;
      }

      if (this.getFieldWithFocus() == this._searchHistoryList) {
         Location location = this._searchHistoryList.getSelectedLocation();
         if (location != null) {
            this.onLocation(location, 0);
         }

         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   private final boolean getFromAddressBook() {
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
         this._selectedIndex = this._oldSelectedIndex;
         return false;
      }

      String addressLoc = "";
      if (mailingAddressModel.length == 0) {
         Dialog.alert(LBSResources.getString(273));
         this._selectedIndex = this._oldSelectedIndex;
         return false;
      }

      MailingAddressModelImpl address;
      if (mailingAddressModel.length != 2) {
         address = mailingAddressModel[0];
      } else {
         String address1 = mailingAddressModel[0].getAddressLine1();
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
         DirectionsChoiceScreen$AddressChoiceDialog d = new DirectionsChoiceScreen$AddressChoiceDialog(
            title, choices, new int[]{0, 1, 51, -805040680, 1196314761, 169478669, 218103808, 1380206665}, 0
         );
         int choice = d.doModal();
         if (choice == -1) {
            this._selectedIndex = this._oldSelectedIndex;
            return false;
         }

         address = mailingAddressModel[choice];
         addressLoc = ((StringBuffer)(new Object(" (")))
            .append(address.getType() == 1 ? LBSResources.getString(33) : LBSResources.getString(34))
            .append(")")
            .toString();
      }

      FindAddress find = new FindAddress(LBSResources.getString(325), true);
      find.setCallback(null, null, this, 2);
      Location location = (Location)find.invoke(address);
      this.setLocationInfo(addressModel, address, addressLoc);
      return this.locationFound(location);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      return this._searchHistoryList.keyChar(key, status, time);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 10) {
         if (this.getFieldWithFocus() != this._searchHistoryList) {
            this.handleSelection();
            return true;
         }
      } else if (key == 27 && this._pointNum == 1) {
         this.reset();
         return true;
      }

      return super.keyDown(keycode, time);
   }

   private final void handleSelection() {
      this._startSelectedIndex = this._selectedIndex;
      this._oldSelectedIndex = this._selectedIndex;
      this._selectedIndex = this._list.getSelectedIndex();
      if (this.getFieldWithFocus() == this._searchHistoryList) {
         this._selectedIndex = -1;
      } else {
         int selectedIndex = this._selectedIndex;
         label76:
         switch (this._startSelectedIndex) {
            case -2:
               break;
            case -1:
            case 1:
            case 2:
            case 3:
            default:
               if (!this._showWhereAmI) {
                  selectedIndex++;
               }

               if (!this._showFavorite && selectedIndex == 3) {
                  selectedIndex++;
               }

               switch (selectedIndex) {
                  case -1:
                     break label76;
                  case 0:
                  default:
                     if (!this.getCurrentLocation()) {
                        this._selectedIndex = -1;
                        return;
                     }
                     break label76;
                  case 1:
                     if (!this.getUserEnteredAddress()) {
                        return;
                     }
                     break label76;
                  case 2:
                     if (!this.getFromAddressBook()) {
                        return;
                     }
                     break label76;
                  case 3:
                     if (!this.getFavorite()) {
                        return;
                     }
                     break label76;
                  case 4:
                     this.getFocusedLocation();
                     break label76;
               }
            case 0:
               if (!this._showFavorite && selectedIndex == 2) {
                  selectedIndex++;
               }

               switch (selectedIndex) {
                  case -1:
                     break label76;
                  case 0:
                  default:
                     if (!this.getUserEnteredAddress()) {
                        return;
                     }
                     break label76;
                  case 1:
                     if (!this.getFromAddressBook()) {
                        return;
                     }
                     break label76;
                  case 2:
                     if (!this.getFavorite()) {
                        return;
                     }
                     break label76;
                  case 3:
                     this.getFocusedLocation();
                     break label76;
               }
            case 4:
               if (!this._showWhereAmI) {
                  selectedIndex++;
               }

               switch (selectedIndex) {
                  case -1:
                     break;
                  case 0:
                  default:
                     if (!this.getCurrentLocation()) {
                        this._selectedIndex = -1;
                        return;
                     }
                     break;
                  case 1:
                     if (!this.getUserEnteredAddress()) {
                        return;
                     }
                     break;
                  case 2:
                     if (!this.getFromAddressBook()) {
                        return;
                     }
                     break;
                  case 3:
                     if (!this.getFavorite()) {
                        return;
                     }
               }
         }
      }

      this.searchDirections();
   }

   private final void removeFields() {
      if (this._labelFrom != null && this._labelFrom.isVisible()) {
         this.delete(this._labelFrom);
      }

      if (this._labelTo != null && this._labelTo.isVisible()) {
         this.delete(this._labelTo);
      }

      if (this._labelInstruction != null && this._labelInstruction.isVisible()) {
         this.delete(this._labelInstruction);
      }

      if (this._separator != null && this._separator.isVisible()) {
         this.delete(this._separator);
      }

      if (this._space != null && this._space.isVisible()) {
         this.delete(this._space);
      }

      if (this._list != null && this._list.isVisible()) {
         this.delete(this._list);
      }
   }

   private final void populateFields() {
      this.setTitle((Field)(new Object(LBSResources.getString(319))));
      int position = 0;
      if (this._pointNum == 0) {
         this._labelInstruction = (LabelField)(new Object(MessageFormat.format(LBSResources.getString(302), new String[]{""})));
         this._labelInstruction.setFont(Font.getDefault().derive(1));
         this.insert(this._labelInstruction, position++);
         this._space = (VerticalSpacerField)(new Object(5));
         this.insert(this._space, position++);
      } else if (this._pointNum != 1) {
         if (this._startSelectedIndex == 0 && this._showWhereAmI) {
            this._labelFrom = (LabelField)(new Object(MessageFormat.format(LBSResources.getString(291), new Object[]{LBSResources.getString(416)})));
         } else {
            this._labelFrom = (LabelField)(new Object(MessageFormat.format(LBSResources.getString(291), new Object[]{this._location[0]._label})));
         }

         this.insert(this._labelFrom, position++);
         if (this._startSelectedIndex == 0) {
            this._labelTo = (LabelField)(new Object(MessageFormat.format(LBSResources.getString(292), new Object[]{this._location[1]._label})));
         } else if (this._selectedIndex == 0 && this._showWhereAmI) {
            this._labelTo = (LabelField)(new Object(MessageFormat.format(LBSResources.getString(292), new Object[]{LBSResources.getString(416)})));
         } else {
            this._labelTo = (LabelField)(new Object(MessageFormat.format(LBSResources.getString(292), new Object[]{this._location[1]._label})));
         }

         this.insert(this._labelTo, position++);
         this._separator = (SeparatorField)(new Object(65536));
         this._space = (VerticalSpacerField)(new Object(10));
         this.insert(this._separator, position++);
         this.insert(this._space, position++);
      } else {
         if (this._selectedIndex == 0 && this._showWhereAmI) {
            this._labelFrom = (LabelField)(new Object(MessageFormat.format(LBSResources.getString(291), new Object[]{LBSResources.getString(416)})));
         } else {
            this._labelFrom = (LabelField)(new Object(MessageFormat.format(LBSResources.getString(291), new Object[]{this._location[0]._label})));
         }

         this.insert(this._labelFrom, position++);
         this._separator = (SeparatorField)(new Object(65536));
         this._space = (VerticalSpacerField)(new Object(5));
         this.insert(this._separator, position++);
         this.insert(this._space, position++);
         this._labelInstruction = (LabelField)(new Object(MessageFormat.format(LBSResources.getString(303), new String[]{""})));
         this._labelInstruction.setFont(Font.getDefault().derive(1));
         this.insert(this._labelInstruction, position++);
         this.insert((Field)(new Object(5)), position++);
      }

      int numLines = 4;
      if (this._field.getFocusLocation() != null) {
         numLines++;
      }

      if (!this._showWhereAmI) {
         numLines--;
      }

      if (this._selectedIndex == 0 || this._selectedIndex == 4) {
         numLines--;
      }

      if (!this._showFavorite) {
         numLines--;
      }

      this._list = (ListField)(new Object(numLines));
      this._list.setCallback(this);
      this._list.setSelectedIndex(0);
      this.insert(this._list, position++);
      this._list.setFocus();
      this._searchHistoryList.setSelectedIndex(0);
      Manager manager = this.getMainManager();
      if (manager != null) {
         manager.setVerticalScroll(0);
      }
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size && size > 0) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   private final void setLocationInfo(Object addressModel, MailingAddressModelImpl address, String addressLoc) {
      this._addressModel = addressModel;
      this._address = address;
      this._addressLoc = addressLoc;
   }

   public DirectionsChoiceScreen(MapField mapField) {
      super(1153220623309406208L);
      this._field = mapField;
      this._model = new SearchAddressModel(null);
      this.applyTheme();
      GPSDevice device = GPSProvider.getInstance().getDeviceInUse();
      this._showWhereAmI = GPSProvider.isGPSSupported() && !device.equals(GPSDevice.NO_DEVICE);
      this._showFavorite = FavouritesManager.hasFavourites();
      this._searchHistoryList = new SearchableHistoryList(this);
      this.populateFields();
      this.add((Field)(new Object()));
      this.add(this._searchHistoryList);
   }

   @Override
   public final boolean onSavePrompt() {
      return true;
   }
}
