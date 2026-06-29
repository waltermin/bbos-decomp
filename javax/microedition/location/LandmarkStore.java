package javax.microedition.location;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.vm.TraceBack;

public class LandmarkStore {
   Vector _landmarkStore;
   Hashtable _categories;
   String _name;
   private static final long ID = 6274003102685756378L;
   private static Hashtable _landmarkStores;
   static String DEFAULT_STORE = "DEFAULT";
   private static PersistentObject _store = PersistentStore.getPersistentObject(6274003102685756378L);

   private LandmarkStore(String name) {
      this._name = name;
      this._landmarkStore = (Vector)(new Object());
      this._categories = (Hashtable)(new Object());
   }

   public static LandmarkStore getInstance(String storeName) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_read");
      if (storeName == null) {
         storeName = DEFAULT_STORE;
      }

      LandmarkStore$ProxyLandmarkStore pstore = (LandmarkStore$ProxyLandmarkStore)_landmarkStores.get(storeName);
      return pstore == null ? null : pstore.getLandmarkStore();
   }

   public void addLandmark(Landmark landmark, String category) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_write");
      if (landmark == null) {
         throw new Object("Landmark cannot be null");
      }

      if (category != null && !this._categories.containsKey(category)) {
         throw new Object("The LandmarkStore does not contain this category");
      }

      LandmarkStore$ProxyLandmark plm = new LandmarkStore$ProxyLandmark(landmark);
      if (category != null) {
         Vector v = (Vector)this._categories.get(category);
         if (v == null) {
            new Object();
         } else {
            v.addElement(plm);
         }
      }

      if (!this._landmarkStore.contains(plm)) {
         plm._landmarkStore = this._name;
         this._landmarkStore.addElement(plm);
      }

      save();
   }

   public Enumeration getLandmarks(String category, String name) {
      Vector v = (Vector)(new Object());
      Enumeration enumeration = this._landmarkStore.elements();
      Vector categoryLandmarks = null;
      if (category != null) {
         categoryLandmarks = (Vector)this._categories.get(category);
      }

      while (enumeration.hasMoreElements()) {
         LandmarkStore$ProxyLandmark plm = (LandmarkStore$ProxyLandmark)enumeration.nextElement();
         Landmark lm = plm.getLandmark();
         lm._landmarkStore = this._name;
         if (category == null) {
            if (name == null) {
               v.addElement(lm);
            } else if (plm._name.equals(name)) {
               v.addElement(lm);
            }
         } else {
            if (categoryLandmarks == null) {
               return null;
            }

            if (categoryLandmarks.contains(plm)) {
               if (name == null) {
                  v.addElement(lm);
               } else if (plm._name.equals(name)) {
                  v.addElement(lm);
               }
            }
         }
      }

      return v.size() == 0 ? null : v.elements();
   }

   public Enumeration getLandmarks() {
      if (this._landmarkStore.size() == 0) {
         return null;
      }

      Vector v = (Vector)(new Object());
      Enumeration enumeration = this._landmarkStore.elements();

      while (enumeration != null && enumeration.hasMoreElements()) {
         Landmark current = ((LandmarkStore$ProxyLandmark)enumeration.nextElement()).getLandmark();
         current._landmarkStore = this._name;
         v.addElement(current);
      }

      return v.elements();
   }

   public Enumeration getLandmarks(String category, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
      if (minLatitude < -4587338432941916160L || minLatitude > 4636033603912859648L) {
         throw new Object("Illegal value of minLatitude");
      }

      if (maxLatitude < -4587338432941916160L || maxLatitude > 4636033603912859648L) {
         throw new Object("Illegal value of maxLatitude");
      }

      if (minLongitude < -4582834833314545664L || minLongitude >= 4640537203540230144L) {
         throw new Object("Illegal value of minLongitude");
      }

      if (maxLongitude < -4582834833314545664L || maxLongitude >= 4640537203540230144L) {
         throw new Object("Illegal value of maxLongitude");
      }

      if (minLatitude > maxLatitude) {
         throw new Object("MinLatitude cannot be less than maxLatitude");
      }

      Vector landmarks = (Vector)(new Object());
      Vector categoryLandmarks = null;
      if (category != null) {
         categoryLandmarks = (Vector)this._categories.get(category);
      }

      Enumeration enumeration = this._landmarkStore.elements();
      if (minLongitude <= maxLongitude) {
         while (enumeration.hasMoreElements()) {
            LandmarkStore$ProxyLandmark plm = (LandmarkStore$ProxyLandmark)enumeration.nextElement();
            Landmark lm = plm.getLandmark();
            lm._landmarkStore = this._name;
            double longitude = lm.getQualifiedCoordinates().getLongitude();
            double latitude = lm.getQualifiedCoordinates().getLatitude();
            if (longitude >= minLongitude
               && longitude <= maxLongitude
               && latitude >= minLatitude
               && latitude <= maxLatitude
               && (category == null || categoryLandmarks != null && categoryLandmarks.contains(plm))) {
               landmarks.addElement(lm);
            }
         }
      } else if (minLongitude > maxLongitude) {
         while (enumeration.hasMoreElements()) {
            LandmarkStore$ProxyLandmark plm = (LandmarkStore$ProxyLandmark)enumeration.nextElement();
            Landmark lm = plm.getLandmark();
            lm._landmarkStore = this._name;
            double longitude = lm.getQualifiedCoordinates().getLongitude();
            double latitude = lm.getQualifiedCoordinates().getLatitude();
            if ((longitude >= -4582834833314545664L && longitude <= maxLongitude || longitude >= minLongitude && longitude <= 4640537203540230144L)
               && latitude >= minLatitude
               && latitude <= maxLatitude
               && (category == null || categoryLandmarks != null && categoryLandmarks.contains(plm))) {
               landmarks.addElement(lm);
            }
         }
      }

      return landmarks.size() == 0 ? null : landmarks.elements();
   }

   public void deleteLandmark(Landmark lm) throws LandmarkException {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_write");
      if (lm == null) {
         throw new Object("Landmark is null");
      }

      LandmarkStore$ProxyLandmark plm = new LandmarkStore$ProxyLandmark(lm);
      if ((plm._landmarkStore == null || !plm._landmarkStore.equals(this._name)) && !this._landmarkStore.contains(plm)) {
         throw new LandmarkException("Landmark does not belong to this landmarkstore");
      }

      this._landmarkStore.removeElement(plm);
      Enumeration enumeration = this._categories.elements();

      while (enumeration.hasMoreElements()) {
         Vector v = (Vector)enumeration.nextElement();
         v.removeElement(plm);
      }

      save();
   }

   public void addCategory(String categoryName) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_category");
      if (categoryName == null) {
         throw new Object("Category name is null");
      }

      if (this._categories.containsKey(categoryName)) {
         throw new Object("Already contains the category");
      }

      this._categories.put(categoryName, new Object());
      save();
   }

   public static void createLandmarkStore(String storeName) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_management");
      if (storeName == null) {
         throw new Object("Store name cannot be null");
      }

      if (_landmarkStores.containsKey(storeName)) {
         throw new Object("Landmark store with the specified name already exists");
      }

      LandmarkStore$ProxyLandmarkStore plandmarkStore = new LandmarkStore$ProxyLandmarkStore(new LandmarkStore(storeName));
      _landmarkStores.put(storeName, plandmarkStore);
      save();
   }

   public void deleteCategory(String categoryName) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_category");
      if (categoryName == null) {
         throw new Object("Category name cannot be null");
      }

      if (this._categories.containsKey(categoryName)) {
         this._categories.remove(categoryName);
         save();
      }
   }

   public static void deleteLandmarkStore(String storeName) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_management");
      if (storeName == null) {
         throw new Object("Store name cannot be null");
      }

      if (_landmarkStores.containsKey(storeName)) {
         _landmarkStores.remove(storeName);
         save();
      }
   }

   public Enumeration getCategories() {
      return this._categories.keys();
   }

   public static String[] listLandmarkStores() {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_read");
      Enumeration enumeration = _landmarkStores.keys();
      String[] landmarkStores = new Object[_landmarkStores.size() - 1];
      int i = 0;

      while (enumeration.hasMoreElements()) {
         String s = (String)enumeration.nextElement();
         if (!s.equals(DEFAULT_STORE)) {
            landmarkStores[i] = s;
            i++;
         }
      }

      return landmarkStores.length == 0 ? null : landmarkStores;
   }

   public void removeLandmarkFromCategory(Landmark lm, String category) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_write");
      if (lm != null && category != null) {
         LandmarkStore$ProxyLandmark plm = new LandmarkStore$ProxyLandmark(lm);
         if (this._landmarkStore.contains(plm)) {
            Vector v = (Vector)this._categories.get(category);
            if (v != null) {
               v.removeElement(plm);
            }

            save();
         }
      } else {
         throw new Object("Landmark or Category cannot be null");
      }
   }

   public void updateLandmark(Landmark lm) throws LandmarkException {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_landmarkstore_write");
      if (lm == null) {
         throw new Object("Null parameter passed");
      }

      boolean present = false;
      Enumeration enumeration = this._landmarkStore.elements();

      while (enumeration.hasMoreElements()) {
         LandmarkStore$ProxyLandmark plm = (LandmarkStore$ProxyLandmark)enumeration.nextElement();
         if (plm._landmarkID.equals(lm._landmarkID)) {
            present = true;
            plm._name = lm.getName();
            plm._description = lm.getDescription();
            plm._qualifiedCoordinates = new LandmarkStore$ProxyQualifiedCoordinates(lm.getQualifiedCoordinates());
            plm._addressInfo = new LandmarkStore$ProxyAddressInfo(lm.getAddressInfo());
         }
      }

      if (!present) {
         throw new LandmarkException("Landmark instance passed as the parameter does not belong to his LandmarkStore");
      }

      save();
   }

   private static void save() {
      synchronized (_store) {
         _store.setContents(_landmarkStores, 51);
         _store.commit();
      }
   }

   static void clear() {
      synchronized (_store) {
         _store.setContents(null, 51);
         _store.commit();
      }
   }

   LandmarkStore(String x0, LandmarkStore$1 x1) {
      this(x0);
   }

   static {
      synchronized (_store) {
         if (_store.getContents() == null) {
            _landmarkStores = (Hashtable)(new Object());
            _landmarkStores.put(DEFAULT_STORE, new LandmarkStore$ProxyLandmarkStore(DEFAULT_STORE));
            _store.setContents(_landmarkStores, 51);
            _store.commit();
         }
      }

      _landmarkStores = (Hashtable)_store.getContents();
   }
}
