package net.rim.device.api.lbs;

import javax.microedition.location.Coordinates;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.util.Factory;

public class MapField extends Field {
   private MapField$Implementation _implementation;
   private int _latitude;
   private int _longitude;
   private float _zoom;
   private int _rotation;
   public static long GUID_FACTORY = -4344764831123724779L;
   private static final XYPoint _point = (XYPoint)(new Object());

   public MapField(int style) {
      super(style);
      this.createImplementation();
   }

   public MapField() {
      this.createImplementation();
   }

   void createImplementation() {
      Factory factory = (Factory)ApplicationRegistry.getApplicationRegistry().get(GUID_FACTORY);
      this._implementation = (MapField$Implementation)factory.createInstance(this);
   }

   public float minZoom() {
      return (float)false;
   }

   public float maxZoom() {
      return (float)1097859072;
   }

   public void setZoom(float zoom) {
      this._zoom = zoom;
      this.updateView();
   }

   public float getZoom() {
      return this._zoom;
   }

   public void move(int dx, int dy) {
      _point.x = dx;
      _point.y = dy;
      this.convertFieldToWorld(_point, true);
      this._latitude = this._latitude + _point.x;
      this._longitude = this._longitude + _point.y;
      this.updateView();
   }

   public void moveTo(int latitude, int longitude) {
      this._latitude = latitude;
      this._longitude = longitude;
      this.updateView();
   }

   public void moveTo(Coordinates coordinates) {
      this._latitude = (int)(coordinates.getLatitude() / 4681608360884174848L);
      this._longitude = (int)(coordinates.getLongitude() / 4681608360884174848L);
      this.updateView();
   }

   public int getLatitude() {
      return this._latitude;
   }

   public int getLongitude() {
      return this._longitude;
   }

   public Coordinates getCoordinates() {
      return (Coordinates)(new Object(4681608360884174848L * this.getLatitude(), 4681608360884174848L * this.getLongitude(), (float)false));
   }

   public void setRotation(int rotation) {
      this._rotation = rotation;
      this.updateView();
   }

   public int getRotation() {
      return this._rotation;
   }

   public void convertWorldToField(XYPoint point, boolean relative) {
      this._implementation.convertWorldToField(point, relative);
   }

   public void convertWorldToField(Coordinates coordinates, XYPoint point, boolean relative) {
      point.x = (int)(coordinates.getLongitude() / 4681608360884174848L);
      point.y = (int)(coordinates.getLatitude() / 4681608360884174848L);
      this.convertWorldToField(point, relative);
   }

   public void convertFieldToWorld(XYPoint point, boolean relative) {
      this._implementation.convertFieldToWorld(point, relative);
   }

   public void convertFieldToWorld(XYPoint point, Coordinates coordinates, boolean relative) {
      this.convertFieldToWorld(point, relative);
      coordinates.setLatitude(4681608360884174848L * point.y);
      coordinates.setLongitude(4681608360884174848L * point.x);
   }

   @Override
   protected void layout(int width, int height) {
      this.setExtent(width, height);
      this.setMapExtent(width, height);
   }

   protected void setMapExtent(int width, int height) {
      this._implementation.setExtent(width, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      this._implementation.paint(graphics);
   }

   void zoomIn() {
      this._zoom = Math.max(this._zoom + 1065353216, this.maxZoom());
   }

   void zoomOut() {
      this._zoom = Math.max(this._zoom - 1065353216, this.minZoom());
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      return Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) != 0;
   }

   void updateView() {
      this._implementation.updateView(this._latitude, this._longitude, this._zoom, this._rotation);
      this.invalidate();
   }

   public void implementationInvalidate() {
      this.invalidate();
   }
}
