package net.rim.plazmic.internal.mediaengine.ui;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.AnimationModel;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.AnimationViewport;
import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.MediaPlayer;

public class ImageForeignObject extends AbstractForeignObject implements MediaListener {
   AnimationModel _model;
   AnimationModel _superModel;
   boolean _ready;
   AnimationViewport _viewport;
   MediaPlayer _player;
   MediaPlayer _superPlayer;
   int _x;
   int _y;
   int _w;
   int _h;

   public void setReady() {
      this._ready = true;
   }

   public void dispose() {
      this._player.close();
   }

   public boolean isReady() {
      return this._ready;
   }

   @Override
   public void mediaEvent(Object object1, int a, int b, Object object2) {
      switch (a) {
         case 20:
            if (object2 == this._model) {
               MediaServices services = (MediaServices)this._superPlayer.getServices();
               ModelInteractor interactor = (ModelInteractor)services.getService("ModelInteractor");
               interactor.trigger(20, this.getHandle(), this._superModel);
            }
         default:
            return;
         case 22:
         case 101:
            if (object1 == this._player) {
               return;
            }

            this._player.stop();
            return;
         case 100:
            if (object1 != this._player) {
               if (this._superPlayer == null) {
                  this._superPlayer = (MediaPlayer)object1;
                  this._superModel = (AnimationModel)this._superPlayer.getMedia();
               }

               try {
                  this._player.start();
               } finally {
                  return;
               }
            }
      }
   }

   public ImageForeignObject(AnimationModel model) {
      this._model = model;
      this._ready = false;
      this._player = (MediaPlayer)(new Object());
      this._player.setMedia(model);
      this._viewport = new AnimationViewport();
      this._viewport.setServices((MediaServices)this._player.getServices());
      this._viewport.setMedia(model);
      this._player.setUI(this._viewport);
      this._player.setInternalMediaListener(this);
   }

   @Override
   public void setPosition(int x, int y) {
      this._x = x;
      this._y = y;
      this._viewport.setOrigin(x, y);
   }

   @Override
   public void setExtent(int width, int height) {
      this._w = width;
      this._h = height;
      this._viewport.setExtent(width, height);
   }

   @Override
   public int getWidth() {
      return this._w;
   }

   @Override
   public int getHeight() {
      return this._h;
   }

   @Override
   public void draw(Object graphics, int x, int y) {
      this._viewport.paint(graphics, this._x, this._y, this._w, this._h);
   }
}
