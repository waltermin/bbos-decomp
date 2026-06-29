package net.rim.blackberry.api.maps;

import net.rim.device.apps.api.framework.model.ContextObject;

public class MapView {
   private int _latitude;
   private int _longitude;
   private int _zoom;
   private int _rotation;
   public static final int MAX_ZOOM = 15;
   public static final long CONTEXT_LATITUDE = -200747095229876690L;
   public static final long CONTEXT_LONGITUDE = 6606581876924152793L;
   public static final long CONTEXT_ZOOM = 581052036187634982L;
   public static final long CONTEXT_ROTATION = 8035222542232379495L;

   public MapView() {
   }

   public void setLatitude(int latitude) {
      this._latitude = latitude;
   }

   public int getLatitude() {
      return this._latitude;
   }

   public void setLongitude(int longitude) {
      this._longitude = longitude;
   }

   public int getLongitude() {
      return this._longitude;
   }

   public void setZoom(int zoom) {
      if (zoom < 0) {
         this._zoom = 0;
      } else if (zoom > 15) {
         this._zoom = 15;
      } else {
         this._zoom = zoom;
      }
   }

   public int getZoom() {
      return this._zoom;
   }

   public void setRotation(int rotation) {
      this._rotation = rotation;
   }

   public int getRotation() {
      return this._rotation;
   }

   public MapView(ContextObject context) {
      this._latitude = context.get(-200747095229876690L);
      this._longitude = context.get(6606581876924152793L);
      this._zoom = context.get(581052036187634982L);
      this._rotation = context.get(8035222542232379495L);
   }

   public ContextObject toContextObject() {
      ContextObject context = (ContextObject)(new Object());
      ContextObject.put(context, -200747095229876690L, new Object(this._latitude));
      ContextObject.put(context, 6606581876924152793L, new Object(this._longitude));
      ContextObject.put(context, 581052036187634982L, new Object(this._zoom));
      ContextObject.put(context, 8035222542232379495L, new Object(this._rotation));
      return context;
   }
}
