package net.rim.device.apps.internal.lbs;

import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.internal.lbs.maplet.MapletCache;
import net.rim.device.internal.system.InternalServices;

final class InternalLBSOptionsScreen extends MainScreen implements FieldChangeListener {
   private BasicEditField _lbsServerField;
   private BasicEditField _locatorServerField;
   private BasicEditField _directionsServerField;
   private BasicEditField _poiServerField;
   private CheckboxField _useCustomULRs;
   private CheckboxField _useBISRadio;
   private CheckboxField _useBISWiFi;
   private ButtonField _wipePersist;

   InternalLBSOptionsScreen() {
      super(65536);
      this.setTitle("Internal LBS Options");
      this.add((Field)(new Object("Maps version: ", LBSOptions.getString(7544824750646866888L, "unknown"), 1000000, 9007199254740992L)));
      this._lbsServerField = (BasicEditField)(new Object(117440512));
      this._lbsServerField.setText(LBSOptions.getURL(-7064416726417485961L));
      this._lbsServerField.setLabel("Map Server: ");
      this.add(this._lbsServerField);
      this._lbsServerField.setChangeListener(this);
      this.add((Field)(new Object(((StringBuffer)(new Object("Current Maplet Version: "))).append(LBSOptions.getInt(3743068244816784828L, 1)).toString())));
      this._locatorServerField = (BasicEditField)(new Object(117440512));
      this._locatorServerField.setText(LBSOptions.getURL(6933732722635403673L));
      this._locatorServerField.setLabel("Locator Server: ");
      this.add(this._locatorServerField);
      this._locatorServerField.setChangeListener(this);
      this._directionsServerField = (BasicEditField)(new Object(117440512));
      this._directionsServerField.setText(LBSOptions.getURL(-254277793043409026L));
      this._directionsServerField.setLabel("Directions Server: ");
      this.add(this._directionsServerField);
      this._directionsServerField.setChangeListener(this);
      this._poiServerField = (BasicEditField)(new Object(117440512));
      this._poiServerField.setText(LBSOptions.getURL(3589376987760903020L));
      this._poiServerField.setLabel("POI Server: ");
      this.add(this._poiServerField);
      this._poiServerField.setChangeListener(this);
      this._useCustomULRs = (CheckboxField)(new Object("Use Custom URL's", LBSOptions.getBoolean(-6271428560607580713L, false)));
      this.add(this._useCustomULRs);
      if (!InternalServices.isDeviceSecure()) {
         this._useBISRadio = (CheckboxField)(new Object("Use BIS for Radio", LBSOptions.getBoolean(2585038783968687563L, true)));
         this.add(this._useBISRadio);
      }

      if (!InternalServices.isDeviceSecure()) {
         this._useBISWiFi = (CheckboxField)(new Object("Use BIS for WiFi", LBSOptions.getBoolean(-6773447903022085068L, false)));
         this.add(this._useBISWiFi);
      }

      this._wipePersist = (ButtonField)(new Object("Clear Maps Persistence", 12884901888L));
      this.add(this._wipePersist);
      this._wipePersist.setChangeListener(this);
      if (!InternalServices.isDeviceSecure()) {
         String appURL = LBSOptions.getString(-9040565055715388692L, "http://maps.blackberry.com");
         boolean isDefaultURL = "http://maps.blackberry.com".equals(appURL);
         this.add(
            (Field)(new Object(
               ((StringBuffer)(new Object("App Update URL ("))).append(isDefaultURL ? "default" : "provided").append("): ").toString(),
               appURL,
               1000000,
               9007199254740992L
            ))
         );
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      menu.add(MenuItem.getPrefab(14));
      menu.add(MenuItem.getPrefab(15));
      menu.add(new InternalLBSOptionsScreen$1(this, "Reset URL Default", 1, 10000));
   }

   @Override
   public final void save() {
      boolean commit = false;
      boolean useCustomURLs = this._useCustomULRs.getChecked();
      if (this._useCustomULRs.isDirty()) {
         commit = true;
         LBSOptions.setInt(3743068244816784828L, 0);
         LBSOptions.setBoolean(-6271428560607580713L, useCustomURLs);
         LBSOptions.setBoolean(4717295063260546653L, false);
         if (Dialog.ask(3, "Clear Map Cache?") == 4) {
            MapletCache.getInstance().clear();
         }
      }

      if (this._lbsServerField.isDirty() && useCustomURLs) {
         LBSOptions.setURL(-7064416726417485961L, this._lbsServerField.getText(), true);
         LBSOptions.setInt(3743068244816784828L, 0);
         commit = true;
         if (Dialog.ask(3, "Clear Map Cache?") == 4) {
            MapletCache.getInstance().clear();
         }
      }

      if (this._locatorServerField.isDirty() && useCustomURLs) {
         commit = true;
         LBSOptions.setURL(6933732722635403673L, this._locatorServerField.getText(), true);
      }

      if (this._directionsServerField.isDirty() && useCustomURLs) {
         commit = true;
         LBSOptions.setURL(-254277793043409026L, this._directionsServerField.getText(), true);
      }

      if (this._poiServerField.isDirty() && useCustomURLs) {
         commit = true;
         LBSOptions.setURL(3589376987760903020L, this._poiServerField.getText(), true);
         LBSOptions.setBoolean(4717295063260546653L, false);
      }

      if (!InternalServices.isDeviceSecure() && this._useBISRadio.isDirty()) {
         commit = true;
         LBSOptions.setBoolean(2585038783968687563L, this._useBISRadio.getChecked());
      }

      if (!InternalServices.isDeviceSecure() && this._useBISWiFi.isDirty()) {
         commit = true;
         LBSOptions.setBoolean(-6773447903022085068L, this._useBISWiFi.getChecked());
      }

      if (commit) {
         LBSOptions.getInstance().commit();
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._wipePersist) {
         if (Dialog.ask(3, "Maps persistent store will be entirely deleted. Proceed?") == 4) {
            byte version = (byte)(LBSOptions.getInt(3743068244816784828L, 1) & 0xFF);
            LBSOptions.wipeMapsStore(this);
            LBSOptions.setInt(3743068244816784828L, version);
            GPSProvider.getInstance().clearDeviceUsed();
            if (Dialog.ask(3, "Reboot device?") == 4) {
               InternalServices.initiateReset("LBS Maps reset");
               return;
            }

            PopupStatus.show("Exiting Maps...", 1500);
            System.exit(0);
            return;
         }
      } else if (field instanceof Object && !this._useCustomULRs.getChecked()) {
         this._useCustomULRs.setChecked(true);
         this._useCustomULRs.setDirty(true);
      }
   }
}
