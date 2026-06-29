package javax.microedition.location;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.Persistable;

class LandmarkStore$ProxyLandmarkStore implements Persistable {
   private String _name;
   private Vector _landmarkStore;
   private Hashtable _categories;

   LandmarkStore$ProxyLandmarkStore(String name) {
      this._name = name;
      this._landmarkStore = (Vector)(new Object());
      this._categories = (Hashtable)(new Object());
   }

   LandmarkStore$ProxyLandmarkStore(LandmarkStore landmarkStore) {
      this._name = landmarkStore._name;
      this._landmarkStore = landmarkStore._landmarkStore;
      this._categories = landmarkStore._categories;
   }

   LandmarkStore getLandmarkStore() {
      LandmarkStore landmarkStore = new LandmarkStore(this._name, null);
      landmarkStore._categories = this._categories;
      landmarkStore._landmarkStore = this._landmarkStore;
      return landmarkStore;
   }
}
