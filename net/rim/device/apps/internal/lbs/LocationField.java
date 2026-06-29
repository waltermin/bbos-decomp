package net.rim.device.apps.internal.lbs;

import net.rim.device.api.collection.ReadableIntMap;
import net.rim.device.api.gps.GPSRegistry;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.lbs.model.SearchAddressModel;

final class LocationField extends ObjectChoiceField implements ReadableIntMap {
   SearchAddressModel _model = new SearchAddressModel(null);
   LocationField$LocationChoice[] _choices = new LocationField$LocationChoice[0];
   private static final long LOCATION_FIELD_UID;
   private static final long RECENT_LOCATIONS_UID;
   private static final int PROPERTY_CONTEXT;
   private static final int PROPERTY_MIDP_COORDINDATES;
   private static final int PROPERTY_MAPS_URL;
   private static final int PROPERTY_MAPS_LOCATION_XML;
   private static final int PROPERTY_MAPS_LOCATION_TLE;
   static Location[] _recentLocations;
   static SimpleDateFormat _timeFormat = (SimpleDateFormat)(new Object("MMM d h:mma"));

   final void add(LocationField$LocationChoice choice) {
      Arrays.add(this._choices, choice);
   }

   final void selectLocation(Location location) {
      if (location == null) {
         this.setSelectedIndex(0);
      } else {
         Arrays.insertAt(_recentLocations, location, 0);
         Arrays.insertAt(this._choices, new LocationField$RecentLocationChoice(location), 1);
         int length = _recentLocations.length;

         while (length > 5) {
            Arrays.removeAt(_recentLocations, --length);
            Arrays.removeAt(this._choices, length + 1);
         }

         this.setChoices(this._choices);
         this.setSelectedIndex(1);
      }
   }

   final void createChoices() {
      this.add(new LocationField$LocationActionChoice(441));
      int count = _recentLocations.length;

      for (int i = 0; i < count; i++) {
         this.add(new LocationField$RecentLocationChoice(_recentLocations[i]));
      }

      GPSRegistry gpsRegistry = GPSRegistry.getInstance();
      if (gpsRegistry != null) {
         long lastFixTime = gpsRegistry.getLastFixTime(0);
         if (lastFixTime > 0) {
            this.add(new LocationField$1(this, 438));
         }
      }

      if (GPSProvider.isGPSSupported()) {
         this.add(new LocationField$2(this, 416));
      }

      this.add(new LocationField$3(this, 325));
      this.add(new LocationField$4(this, 404));
      this.add(new LocationField$5(this, 326));
   }

   final Location getSelectedLocation() {
      int index = this.getSelectedIndex();
      return index <= 0 ? null : _recentLocations[index - 1];
   }

   @Override
   public final int size() {
      return 0;
   }

   @Override
   public final Object get(int key) {
      Location location = this.getSelectedLocation();
      if (location == null) {
         return null;
      }

      switch (key) {
         case 0:
            return null;
         case 1:
         case 4:
         case 5:
            return null;
         case 2:
         default:
            double latitude = location._latitude / 4681608360884174848L;
            double longitude = location._longitude / 4681608360884174848L;
            return new Object(latitude, longitude, (float)false);
         case 3:
            return Utilities.createLbsUrl(location);
      }
   }

   @Override
   public final int getKey(Object element) {
      return -1;
   }

   @Override
   public final boolean contains(int key) {
      switch (key) {
         case 0:
            return false;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         default:
            return true;
      }
   }

   LocationField(Object initialData) {
      super("Location: ", null);
      if (initialData instanceof Object) {
      }

      this.createChoices();
      this.setChoices(this._choices);
      this.setSelectedIndex(0);
   }

   @Override
   protected final void fieldChangeNotify(int context) {
      super.fieldChangeNotify(context);
      if ((context & -2147483648) == 0) {
         int selection = this.getSelectedIndex();
         if (selection != -1) {
            this._choices[selection].onSelect();
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _recentLocations = (Location[])ar.getOrWaitFor(-8558719100366407608L);
      if (_recentLocations == null) {
         _recentLocations = new Location[0];
         ar.put(-8558719100366407608L, _recentLocations);
      }
   }
}
