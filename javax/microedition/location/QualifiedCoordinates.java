package javax.microedition.location;

public class QualifiedCoordinates extends Coordinates {
   private float _horizontalAccuracy;
   private float _verticalAccuracy;

   public QualifiedCoordinates(double latitude, double longitude, float altitude, float horizontalAccuracy, float verticalAccuracy) {
      super(latitude, longitude, altitude);
      if (horizontalAccuracy < 0L && !Float.isNaN(horizontalAccuracy)) {
         throw new Object("Invalid value for horizontal accuracy");
      }

      if (verticalAccuracy < 0L && !Float.isNaN(verticalAccuracy)) {
         throw new Object("Invalid value for vertical accuracy");
      }

      this._horizontalAccuracy = horizontalAccuracy;
      this._verticalAccuracy = verticalAccuracy;
   }

   public float getHorizontalAccuracy() {
      return this._horizontalAccuracy;
   }

   public float getVerticalAccuracy() {
      return this._verticalAccuracy;
   }

   public void setHorizontalAccuracy(float horizontalAccuracy) {
      if (horizontalAccuracy < 0 && !Float.isNaN(horizontalAccuracy)) {
         throw new Object("Invalid value for horizontal accuracy");
      }

      this._horizontalAccuracy = horizontalAccuracy;
   }

   public void setVerticalAccuracy(float verticalAccuracy) {
      if (verticalAccuracy < 0 && !Float.isNaN(verticalAccuracy)) {
         throw new Object("Invalid value for vertical accuracy");
      }

      this._verticalAccuracy = verticalAccuracy;
   }
}
