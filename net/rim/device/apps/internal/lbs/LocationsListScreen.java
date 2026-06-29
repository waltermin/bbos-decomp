package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class LocationsListScreen extends MainScreen implements ListFieldCallback, FocusChangeListener {
   private MapField _field;
   private ListField _list;
   private ListField _listActions;
   private Field _activeField;
   private Location _location;
   private int _numPOIs = 0;
   private int _selectedIndex = 0;
   private int _selectedPOI = 0;
   private int _numSubItems = 1;
   private LocationsListScreen$AutoExpandRunnable _autoExpandRunnable = new LocationsListScreen$AutoExpandRunnable(this);
   private int _autoExpandPID = -1;
   private boolean _expanded = false;

   public final void setFocusIndex(int focusIndex) {
      if (focusIndex == -1) {
         if (this._activeField != this._listActions) {
            this._listActions.setFocus();
            this.handleFocusChanged(this._listActions, 1);
         }

         this._listActions.setSelectedIndex(1);
         this.handleFocusChanged(this._listActions, 1);
      } else {
         if (this._activeField != this._list && this._list.getManager() != null) {
            this._list.setSelectedIndex(focusIndex);
            this._list.setFocus();
            this._activeField = this._list;
            this._selectedIndex = focusIndex;
         }

         if (focusIndex <= this._selectedPOI) {
            this._list.setSelectedIndex(focusIndex);
            this._selectedIndex = focusIndex;
         } else {
            this._list.setSelectedIndex(focusIndex + this._numSubItems);
            this._selectedIndex = focusIndex + this._numSubItems;
         }

         this.handleFocusChanged(this._list, 2);
         this.updateDisplay();
      }
   }

   public final void popLocationListScreen() {
      UiApplication.getUiApplication().popScreen(this);
   }

   @Override
   public final Object get(ListField listField, int index) {
      return index > -1 && index < this._field._currentPOIs.length ? this._field._currentPOIs[index] : null;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return super.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (listField == this._listActions) {
         if (index == 0) {
            graphics.drawText(LBSResources.getString(288), 0, y, 64);
            return;
         }

         if (index == 1) {
            graphics.drawText(LBSResources.getString(298), 0, y, 64);
            return;
         }
      } else if (listField == this._list) {
         if (this._activeField == this._listActions) {
            this.displayName(graphics, index, 0, y);
            return;
         }

         this._selectedPOI = Math.min(this._selectedPOI, this._numPOIs - 1);
         boolean addressExists = this._field._currentPOIs[this._selectedPOI]._address != null
            && this._field._currentPOIs[this._selectedPOI]._address.length() > 0;
         boolean phoneExists = this._field._currentPOIs[this._selectedPOI]._phone != null && this._field._currentPOIs[this._selectedPOI]._phone.length() > 0;
         if (index <= this._selectedPOI) {
            this.displayName(graphics, index, 0, y);
            return;
         }

         if (index == this._selectedPOI + 1 && this._expanded) {
            if (addressExists) {
               graphics.drawText(((StringBuffer)(new Object("  "))).append(this._field._currentPOIs[this._selectedPOI]._address).toString(), 0, y, 64);
               return;
            }

            if (phoneExists) {
               graphics.drawText(((StringBuffer)(new Object("  "))).append(this._field._currentPOIs[this._selectedPOI]._phone).toString(), 0, y, 64);
               return;
            }

            graphics.drawText(((StringBuffer)(new Object("  "))).append(LBSResources.getString(94)).toString(), 0, y, 64);
            return;
         }

         if (index == this._selectedPOI + 2 && this._expanded) {
            if (addressExists && phoneExists) {
               graphics.drawText(((StringBuffer)(new Object("  "))).append(this._field._currentPOIs[this._selectedPOI]._phone).toString(), 0, y, 64);
               return;
            }

            graphics.drawText(((StringBuffer)(new Object("  "))).append(LBSResources.getString(94)).toString(), 0, y, 64);
            return;
         }

         if (index == this._selectedPOI + 3 && this._expanded) {
            if (addressExists && phoneExists) {
               graphics.drawText(((StringBuffer)(new Object("  "))).append(LBSResources.getString(94)).toString(), 0, y, 64);
               return;
            }

            this.displayName(graphics, index - this._numSubItems, 0, y);
            return;
         }

         if (this._expanded) {
            this.displayName(graphics, index - this._numSubItems, 0, y);
            return;
         }

         this.displayName(graphics, index, 0, y);
      }
   }

   @Override
   public final void focusChanged(Field field, int arg1) {
      this.handleFocusChanged(field, arg1);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 10) {
         Field field = this.getLeafFieldWithFocus();
         if (!(field instanceof LocationsListScreen$HTTPLink)) {
            this.handleSelection(true);
            this._field.setLastUsedScreen(this);
            return true;
         } else {
            ((LocationsListScreen$HTTPLink)field).launchURL();
            return true;
         }
      } else {
         if (key == 8) {
            this.deleteSelection();
            return true;
         }

         if (key == 17 && this._selectedIndex == this._selectedPOI + 2) {
            this._selectedPOI = Math.min(this._selectedPOI, this._numPOIs - 1);
            Location location = this._field._currentPOIs[this._selectedPOI];
            boolean addressExists = location._address != null && location._address.length() > 0;
            boolean phoneExists = location._phone != null && location._phone.length() > 0;
            if (addressExists && phoneExists) {
               this.makePhoneCall(location._phone);
            }

            this._field.setLastUsedScreen(this);
            return true;
         } else {
            return super.keyDown(keycode, time);
         }
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this._field.setLastUsedScreen(this);
      if (this._activeField == this._list && this._selectedIndex - this._selectedPOI == 0) {
         return false;
      }

      this.handleSelection(false);
      return true;
   }

   @Override
   public final boolean onClose() {
      this._field.setLastUsedScreen(null);
      return super.onClose();
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      if (instance == 0) {
         menu.add(
            new LocationsListScreen$2(
               this, LBSResources.getString(288), 4, this._activeField == this._listActions && this._listActions.getSelectedIndex() == 0 ? 0 : 524287
            )
         );
      }

      menu.add(new LocationsListScreen$3(this, LBSResources.getString(298), 0, this._selectedIndex - this._selectedPOI == 1 ? 0 : 1));
      if (this._activeField == this._list) {
         if (this._location._phone != null) {
            menu.add(
               new LocationsListScreen$4(
                  this,
                  MessageFormat.format(LBSResources.getString(306), new Object[]{this._location._phone}),
                  1,
                  this._selectedIndex - this._selectedPOI == 2 ? 0 : 2
               )
            );
         }

         menu.add(new LocationsListScreen$5(this, LBSResources.getString(94), 2, this._selectedIndex - this._selectedPOI == 3 ? 0 : 3));
      }

      super.makeMenu(menu, instance);
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

   private final void handleFocusChanged(Field field, int arg1) {
      if (field != this._listActions || arg1 != 1 && arg1 != 2) {
         if (field == this._list && (arg1 == 1 || arg1 == 2)) {
            this._activeField = field;
            if (this._autoExpandPID != -1) {
               UiApplication.getUiApplication().cancelInvokeLater(this._autoExpandPID);
               this._autoExpandPID = -1;
            }

            this._autoExpandPID = UiApplication.getUiApplication().invokeLater(this._autoExpandRunnable, 375, false);
            if (this._autoExpandPID == -1) {
               this._autoExpandPID = UiApplication.getUiApplication().invokeLaterInternal(this._autoExpandRunnable, 375, false);
               return;
            }
         } else {
            this._activeField = null;
         }
      } else {
         this._activeField = field;
         this._numSubItems = 0;
         this._list.setSize(this._field._currentPOIs.length);
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (this._selectedIndex == -1) {
            this.setFocusIndex(this._selectedIndex);
         } else {
            this.setFocusIndex(this._field._currentLocations._focus);
         }

         this._autoExpandPID = -1;
      }
   }

   private final void displayName(Graphics graphics, int index, int x, int y) {
      String distance = Distance.formatDistance(
         this._field._searchLatitude, this._field._searchLongitude, this._field._currentPOIs[index]._latitude, this._field._currentPOIs[index]._longitude
      );
      graphics.drawText(
         ((StringBuffer)(new Object())).append(this._field._currentPOIs[index]._label).append(" (").append(distance).append(")").toString(), x, y, 64
      );
   }

   private final void addDescription(String description) {
      if (description != null && description != "") {
         ActiveRichTextField descriptionField = (ActiveRichTextField)(new Object(description));
         Font f = descriptionField.getFont();
         int height = (int)(f.getHeight() * 1062836634);
         if (height < 12) {
            height = 12;
         }

         f = f.derive(f.getStyle(), height);
         descriptionField.setFont(f);
         this.add(descriptionField);
      }
   }

   private final void addLink(String text, String url) {
      if (url != null && url.length() > 0) {
         LocationsListScreen$HTTPLink urlField = new LocationsListScreen$HTTPLink(this, text, url);
         this.add(urlField);
      }
   }

   private final void addTitle() {
      boolean bGoogle = false;
      if (this._field._currentPOIs != null) {
         for (int i = 0; i < this._field._currentPOIs.length; i++) {
            Location location = this._field._currentPOIs[i];
            if (location._source != null && location._source.equalsIgnoreCase("Google")) {
               bGoogle = true;
               break;
            }
         }
      }

      if (bGoogle) {
         this.setTitle(
            (Field)(new Object(
               ((StringBuffer)(new Object("Google "))).append(LBSResources.getString(287)).append(": ").append(this._field._poiKeywords).toString()
            ))
         );
      } else {
         this.setTitle(
            (Field)(new Object(((StringBuffer)(new Object())).append(LBSResources.getString(287)).append(": ").append(this._field._poiKeywords).toString()))
         );
      }
   }

   private final void performNewSearch() {
      this._field._screen.getPOIs(this);
      if (this._field._currentPOIs.length > 0) {
         this._numPOIs = this._field._currentPOIs.length;
         this._list.setSize(this._numPOIs);
         this._list.invalidate();
      }

      this.addTitle();
   }

   private final void addLabel(String title) {
      if (title != null && title.length() > 0) {
         LabelField labelField = (LabelField)(new Object(title));
         labelField.setFont(Font.getDefault().derive(1));
         this.add(labelField);
      }
   }

   private final void handleSelection(boolean keyPressed) {
      if (this._autoExpandPID != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._autoExpandPID);
         this._autoExpandPID = -1;
      }

      if (this._activeField == this._listActions) {
         if (this._listActions.getSelectedIndex() == 0) {
            this.performNewSearch();
            return;
         }

         if (this._listActions.getSelectedIndex() == 1) {
            this.showOnMap(this._field._currentPOIs[0]);
            return;
         }
      } else if (this._activeField == this._list) {
         this._selectedPOI = Math.min(this._selectedPOI, this._numPOIs - 1);
         Location location = this._field._currentPOIs[this._selectedPOI];
         boolean addressExists = location._address != null && location._address.length() > 0;
         boolean phoneExists = location._phone != null && location._phone.length() > 0;
         if (this._selectedIndex == this._selectedPOI) {
            this._location = location;
            if (keyPressed) {
               this.showOnMap(location);
               return;
            }
         } else if (this._selectedIndex == this._selectedPOI + 1) {
            if (addressExists) {
               this.showOnMap(location);
               return;
            }

            if (phoneExists) {
               this.makePhoneCall(location._phone);
               return;
            }
         } else if (this._selectedIndex == this._selectedPOI + 2) {
            if (addressExists && phoneExists) {
               this.makePhoneCall(location._phone);
               return;
            }
         } else if (this._selectedIndex == this._selectedPOI + this._numSubItems) {
            new LocationDialog(location, this._field._currentLegalNotices).doModal();
         }
      }
   }

   private final void cleanup() {
      this._field.setLastUsedScreen(this);
      if (this._autoExpandPID != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._autoExpandPID);
         this._autoExpandPID = -1;
      }
   }

   private final void showOnMap(Location location) {
      this._field._currentLocations.setFocus(location);
      int zoom = 0;
      int screenHeight = Math.abs(this._field._transform._screenView.height());
      int screenWidth = this._field._transform._screenView.width();
      int height = 2 * Math.abs(location._latitude - this._field._searchLatitude);
      int width = 2 * Math.abs(location._longitude - this._field._searchLongitude);
      int widthCorrected;
      if (LBSOptions.SPHERICAL_CORRECTION) {
         int sphericalCorrection = Transform.getSphericalCorrection(this._field._searchLatitude, zoom);
         widthCorrected = Fixed32.mul(width, sphericalCorrection);
      } else {
         widthCorrected = width;
      }

      while (zoom < 15 && (widthCorrected > screenWidth << zoom || height > screenHeight << zoom)) {
         zoom++;
         if (LBSOptions.SPHERICAL_CORRECTION) {
            int sphericalCorrection = Transform.getSphericalCorrection(this._field._searchLatitude, zoom);
            widthCorrected = Fixed32.mul(width, sphericalCorrection);
         } else {
            widthCorrected = width;
         }
      }

      this._field.setZoom(zoom);
      if (this._field._latitude == this._field._searchLatitude && this._field._longitude != this._field._searchLongitude) {
      }

      this._field._latitude = this._field._searchLatitude;
      this._field._longitude = this._field._searchLongitude;
      this._field.updateScreenPosition();
      this._field.update(true);
      if (this._field._currentLocations._count > 1) {
         this._field.showPOIDirHintLable(0, true);
      }

      this.close();
   }

   private final void makePhoneCall(String phoneNumber) {
      ContextObject tempContext = (ContextObject)(new Object());
      tempContext.reset();
      if (phoneNumber != null) {
         long contextId = 253;
         tempContext.put(contextId, phoneNumber);
         RIMModel numberModel = (RIMModel)FactoryUtil.createInstance(3797587162219887872L, tempContext);
         if (numberModel == null) {
            throw new Object();
         }

         ContextObject contextObject = (ContextObject)(new Object(34));
         ContextObject.setFlag(contextObject, 20);
         contextObject.put(253, phoneNumber);
         Verb[] verbs = new Object[0];
         Verb defaultVerb = ((VerbProvider)numberModel).getVerbs(contextObject, verbs);
         if (Dialog.ask(3, MessageFormat.format(LBSResources.getString(286), new Object[]{phoneNumber}), 4) == 4) {
            defaultVerb.invoke(contextObject);
         }
      }
   }

   private final void deleteSelection() {
      if (this._activeField == this._list) {
         this._selectedPOI = Math.min(this._selectedPOI, this._numPOIs - 1);
         Location location = this._field._currentPOIs[this._selectedPOI];
         this._field._currentLocations.clear(location);
         if (this._selectedPOI == this._list.getSize() - this._numSubItems - 1 && this._selectedPOI > 0) {
            this._selectedPOI--;
            this._list.setSelectedIndex(this._selectedPOI);
         }

         if (this._field._currentPOIs.length > 0) {
            this.focusChanged(this._list, 2);
            return;
         }

         this.close();
      }
   }

   public LocationsListScreen(MapField mapField, int focusIndex) {
      super(1153220571769798656L);
      this._field = mapField;
      this.applyTheme();
      this.addTitle();
      if (this._field._currentPOIs != null) {
         this._numPOIs = this._field._currentPOIs.length;
         this._list = (ListField)(new Object(this._numPOIs));
         this._list.setCallback(this);
         this._list.setFocusListener(this);
         this._listActions = (ListField)(new Object(2));
         this._listActions.setCallback(this);
         this._listActions.setFocusListener(this);
         if (focusIndex == -1) {
            this._selectedIndex = focusIndex;
         }

         int spaceHeight = this._list.getFont().getHeight() >> 1;
         this.add((Field)(new Object(spaceHeight)));
         this.add(this._listActions);
         this.add((Field)(new Object(65536)));
         this.add((Field)(new Object(spaceHeight)));
         this.add(this._list);
         if (this._field._currentLegalNotices != null && this._field._currentLegalNotices.length > 0) {
            this.add((Field)(new Object(spaceHeight)));
            this.add((Field)(new Object(65536)));
            this.add((Field)(new Object(spaceHeight)));

            for (int i = 0; i < this._field._currentLegalNotices.length; i++) {
               this.add((Field)(new Object(this._field._currentLegalNotices[i])));
               this.add((Field)(new Object(spaceHeight)));
            }
         }

         boolean bAds = false;
         if (this._field._currentAds.length > 0) {
            bAds = true;
         }

         if (!bAds) {
            for (int i = 0; i < this._numPOIs; i++) {
               Location location = this._field._currentLocations._locations[i];
               if (location._sponsored != null && location._sponsored.equalsIgnoreCase("yes")) {
                  bAds = true;
                  break;
               }
            }
         }

         if (bAds) {
            this.addLabel(LBSResources.getString(493));
         }

         for (int i = 0; i < mapField._currentAds.length; i++) {
            Location location = mapField._currentAds[i];
            this.addLink(location._label, location._url);
            this.addDescription(location._description);
            this.add((Field)(new Object()));
         }

         for (int var9 = 0; var9 < this._numPOIs; var9++) {
            Location location = this._field._currentLocations._locations[var9];
            if (location._sponsored != null && location._sponsored.equalsIgnoreCase("yes")) {
               this.addLink(location._label, location._url);
               this.addDescription(location._description);
               this.add((Field)(new Object()));
            }
         }

         this.add((Field)(new Object()));
      }
   }

   static final int access$1008(LocationsListScreen x0) {
      return x0._numSubItems++;
   }

   static final int access$1220(LocationsListScreen x0, int x1) {
      return x0._selectedIndex -= x1;
   }
}
