package net.rim.plazmic.internal.mediaengine.ui;

public class AbstractForeignObject implements ForeignObject {
   private ForeignObjectPeer _peer;
   private int _handle = -1;

   @Override
   public int getHandle() {
      return this._handle;
   }

   @Override
   public void setHandle(int handle) {
      this._handle = handle;
   }

   @Override
   public int getX() {
      return 0;
   }

   @Override
   public int getY() {
      return 0;
   }

   @Override
   public int getWidth() {
      throw null;
   }

   @Override
   public int getHeight() {
      throw null;
   }

   @Override
   public void draw(Object _1, int _2, int _3) {
      throw null;
   }

   @Override
   public void setPeer(ForeignObjectPeer peer) {
      this._peer = peer;
   }

   @Override
   public ForeignObjectPeer getPeer() {
      return this._peer;
   }

   @Override
   public Object getInstance() {
      return null;
   }

   @Override
   public void setPosition(int x, int y) {
   }

   @Override
   public void setExtent(int width, int height) {
   }

   @Override
   public void setFocus() {
   }

   @Override
   public void killFocus() {
   }

   @Override
   public boolean isFocusable() {
      return false;
   }
}
