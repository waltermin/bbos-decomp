package javax.microedition.location;

import net.rim.device.api.util.Persistable;

class LandmarkStore$ProxyLandmark implements Persistable {
   String _name;
   String _description;
   LandmarkStore$ProxyQualifiedCoordinates _qualifiedCoordinates;
   LandmarkStore$ProxyAddressInfo _addressInfo;
   String _landmarkID;
   String _landmarkStore;

   LandmarkStore$ProxyLandmark(Landmark landmark) {
      this._name = landmark.getName();
      this._description = landmark.getDescription();
      if (landmark.getQualifiedCoordinates() != null) {
         this._qualifiedCoordinates = new LandmarkStore$ProxyQualifiedCoordinates(landmark.getQualifiedCoordinates());
      }

      if (landmark.getAddressInfo() != null) {
         this._addressInfo = new LandmarkStore$ProxyAddressInfo(landmark.getAddressInfo());
      }

      this._landmarkID = landmark._landmarkID;
      this._landmarkStore = landmark._landmarkStore;
   }

   Landmark getLandmark() {
      QualifiedCoordinates qcood = null;
      AddressInfo addressInfo = null;
      if (this._qualifiedCoordinates != null) {
         qcood = this._qualifiedCoordinates.getQualifiedCoordinates();
      }

      if (this._addressInfo != null) {
         addressInfo = this._addressInfo.getAddressInfo();
      }

      Landmark landmark = new Landmark(this._name, this._description, qcood, addressInfo);
      landmark._landmarkID = this._landmarkID;
      landmark._landmarkStore = this._landmarkStore;
      return landmark;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof LandmarkStore$ProxyLandmark)) {
         return false;
      }

      LandmarkStore$ProxyLandmark plm = (LandmarkStore$ProxyLandmark)obj;
      return plm._name == this._name && plm._description == this._description && plm._landmarkID == this._landmarkID;
   }
}
