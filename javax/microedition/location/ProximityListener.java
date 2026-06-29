package javax.microedition.location;

public interface ProximityListener {
   void monitoringStateChanged(boolean var1);

   void proximityEvent(Coordinates var1, Location var2);
}
