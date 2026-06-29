package javax.microedition.location;

public class Landmark {
   private String _name;
   private String _description;
   private QualifiedCoordinates _qualifiedCoordinates;
   private AddressInfo _addressInfo;
   String _landmarkStore;
   String _landmarkID;

   public Landmark(String name, String description, QualifiedCoordinates coordinates, AddressInfo addressInfo) {
      if (name == null) {
         throw new Object("Name of the Landmark cannot be null");
      }

      this._name = name;
      this._description = description;
      this._qualifiedCoordinates = coordinates;
      this._addressInfo = addressInfo;
      this._landmarkID = ((StringBuffer)(new Object())).append(this._name).append(description).append(this.hashCode()).toString();
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      if (name == null) {
         throw new Object("Name cannot be set to null");
      }

      this._name = name;
   }

   public QualifiedCoordinates getQualifiedCoordinates() {
      return this._qualifiedCoordinates;
   }

   public void setQualifiedCoordinates(QualifiedCoordinates cooordinates) {
      this._qualifiedCoordinates = cooordinates;
   }

   public AddressInfo getAddressInfo() {
      return this._addressInfo;
   }

   public void setAddressInfo(AddressInfo addressInfo) {
      this._addressInfo = addressInfo;
   }

   public String getDescription() {
      return this._description;
   }

   public void setDescription(String description) {
      this._description = description;
   }
}
