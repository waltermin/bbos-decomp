package net.rim.plazmic.mediaengine;

import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.service.BasicService;
import net.rim.plazmic.internal.mediaengine.service.EventSubscription;
import net.rim.plazmic.internal.mediaengine.util.MEUtilities;
import net.rim.plazmic.internal.mediaengine.util.SafeArray;
import net.rim.vm.TraceBack;

public class MediaPlayer {
   private BasicService _ui;
   private MediaServices _services;
   private Object _media;
   private SafeArray _listeners;
   private MediaListener _internalListener;
   private MediaListener _eventFilter;
   private int _state = 0;
   private Object _lock;
   public static final int UNREALIZED;
   public static final int REALIZED;
   public static final int STARTED;
   private static final String UICOMPONENT_ID;
   public static final String ID;

   public MediaPlayer() {
      this._listeners = new SafeArray();
      this._eventFilter = new MediaPlayer$EventFilter(this);
      this._lock = MediaFactory.getPlatform().getUILock();
   }

   public synchronized int getState() {
      return this._state;
   }

   public synchronized void start() {
      this.assertPermission();
      if (this._media != null && this._services != null && this._state != 0) {
         this._state = 2;
         this._services.getEngine().start();
         this._lock = MediaFactory.getPlatform().getUILock();
         this.fireMediaEvent(100, -1, this._media);
      } else {
         throw new MediaException();
      }
   }

   public synchronized void stop() {
      this.assertPermission();
      if (this._state == 2) {
         this._state = 1;
         this._services.getEngine().stop();
         this.fireMediaEvent(101, -1, this._media);
      }
   }

   public synchronized void setMediaTime(long time) {
      if (this._state != 1) {
         throw new MediaException();
      }

      this._services.getEngine().setTime(time);
      this._lock = MediaFactory.getPlatform().getUILock();
   }

   public synchronized long getMediaTime() {
      return this._services == null ? 0 : this._services.getEngine().getTime();
   }

   public synchronized Object getMedia() {
      return this._media;
   }

   public void setMedia(Object media) {
      synchronized (MediaFactory.getPlatform().getUILock()) {
         synchronized (this) {
            if (this._state == 2) {
               this.stop();
            }

            if (media != null) {
               if (media instanceof MediaServices) {
                  this.setServices((MediaServices)media);
               } else {
                  if (this._services == null || !this._services.isSupported(media)) {
                     this.setServices(MediaFactory.createMediaServices(media));
                  }

                  this._services.setMedia(media);
               }
            } else {
               this.releaseMedia(false);
            }

            this._media = media;
            if (media instanceof MediaModel) {
               String[] images = ((MediaModel)media).getExternalResources(2);
               if (images != null) {
                  for (int i = 0; i < images.length; i++) {
                     Object obj = ((MediaModel)media).getResource(images[i]);
                     if (obj instanceof MediaListener) {
                        MediaListener listener = (MediaListener)obj;
                        this.addMediaListener(listener);
                     }
                  }
               }
            }

            this._state = this._media == null ? 0 : 1;
            if (this._state == 1) {
               this.setMediaTime(0);
            }

            if (this._ui != null) {
               this._ui.setServices(this._services);
            }

            this.fireMediaEvent(21, -1, media);
         }
      }
   }

   private void releaseMedia(boolean dispose) {
      this.assertPermission();
      Object object = this._media;
      if (this._media instanceof MediaServices) {
         object = ((MediaServices)this._media).getMedia();
         if (this._services == this._media) {
            this.setServices(null);
         }

         if (dispose) {
            ((MediaServices)this._media).dispose();
         }
      }

      if (dispose && object instanceof MediaModel) {
         try {
            ((MediaModel)object).disposeModel();
         } catch (MediaException var4) {
         }
      }

      if (this._services != null) {
         this._services.setMedia(null);
      }

      this._media = null;
   }

   public synchronized Object getUI() {
      if (this._ui == null) {
         this._ui = MediaFactory.createDefaultUI();
         this._ui.setServices(this._services);
      }

      return this._ui;
   }

   public void close() {
      this.assertPermission();
      synchronized (this._lock) {
         synchronized (this) {
            this.stop();
            if (this._ui != null) {
               this._ui.setServices(null);
               this._ui = null;
            }

            this.releaseMedia(true);
            this.setServices(null);
            this._state = 0;
         }
      }
   }

   public void addMediaListener(MediaListener l) {
      this.assertPermission();
      if (l == null) {
         throw new Object("Listener can not be null");
      }

      this._listeners.add(l);
   }

   public void removeMediaListener(MediaListener l) {
      this.assertPermission();
      if (l == null) {
         throw new Object("Listener can not be null");
      }

      this._listeners.remove(l);
   }

   private void setServices(MediaServices s) {
      if (this._services != s) {
         if (this._services != null) {
            Object ui = this._services.getService("UIComponent");
            if (this._ui == ui && this._ui != null) {
               this.setUI(null);
            }

            EventSubscription subscription = (EventSubscription)this._services.getService("EventSubscription");
            if (subscription != null) {
               subscription.removeListener(this._eventFilter);
            }

            if (this._services != this._media) {
               this._services.dispose();
            }
         }

         this._services = s;
         if (this._services != null) {
            Object ui = this._services.getService("UIComponent");
            if (ui instanceof BasicService && this._ui != ui) {
               this.setUI((BasicService)ui);
            }

            EventSubscription subscription = (EventSubscription)this._services.getService("EventSubscription");
            if (subscription != null) {
               subscription.addListener(this._eventFilter);
            }

            if (!this._services.hasService("MediaPlayer")) {
               this._services.registerService("MediaPlayer", this);
            }
         }
      }
   }

   public void setUI(BasicService ui) {
      synchronized (MediaFactory.getPlatform().getUILock()) {
         synchronized (this) {
            if (ui != this._ui) {
               if (this._ui != null) {
                  this._ui.setServices(null);
               }

               this._ui = ui;
               if (this._ui != null) {
                  this._ui.setServices(this._services);
               }
            }
         }
      }
   }

   public synchronized Object getServices() {
      return this._services;
   }

   public void setInternalMediaListener(MediaListener l) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._internalListener = l;
   }

   private final void fireMediaEvent(int event, int eventParam, Object data) {
      MEUtilities.fireMediaEvent(this, this._listeners, event, eventParam, data);
   }

   private void assertPermission() {
      ApplicationControl.assertMediaPermitted(true, CommonResource.getBundle(), 10177);
   }
}
