package net.rim.blackberry.api.invoke;

import javax.microedition.pim.Contact;
import net.rim.blackberry.api.maps.MapView;

public final class MapsArguments extends ApplicationArguments {
   private Contact _contact;
   private int _addressIndex;
   private MapView _mapView;
   public static final String ARG_LOCATION_DOCUMENT = "location_document";

   public MapsArguments() {
   }

   public MapsArguments(String arg, String locationDocument) {
      if (arg != null && arg.equals("location_document") && locationDocument != null) {
         super._args = new Object[2];
         super._args[0] = arg;
         super._args[1] = locationDocument;
      } else {
         throw new Object("Invalid argument.");
      }
   }

   public MapsArguments(MapView mapView) {
      this._mapView = mapView;
   }

   final MapView getMapViewArg() {
      return this._mapView;
   }

   public MapsArguments(Contact contact, int addressIndex) {
      this._contact = contact;
      this._addressIndex = addressIndex;
   }

   final Contact getContactArg() {
      return this._contact;
   }

   final int getAddressIndexArg() {
      return this._addressIndex;
   }
}
