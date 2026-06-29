package net.rim.plazmic.internal.mediaengine;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.service.BasicService;
import net.rim.plazmic.internal.mediaengine.service.MediaService;
import net.rim.plazmic.mediaengine.MediaException;

public class MediaServices {
   private EventEngine _engine;
   private Object _media;
   private String _mediaType;
   private Hashtable _servicesTable;
   private Random _random;

   public MediaServices(String mediaType) {
      this._mediaType = mediaType;
      this._servicesTable = new Hashtable();
      this._random = new Random();
   }

   public String getUniqueId() {
      String uniqueId;
      do {
         uniqueId = Integer.toHexString(this._random.nextInt());
      } while (this._servicesTable.get(uniqueId) != null);

      return uniqueId;
   }

   public boolean isSupported(Object media) {
      if (media == null) {
         return false;
      }

      String mediaType = !(media instanceof MediaModel) ? media.getClass().getName() : ((MediaModel)media).getMediaClass().getName();
      return this._mediaType.equals(mediaType);
   }

   public Object getMedia() {
      return this._media;
   }

   public synchronized void setMedia(Object m) {
      if (this._engine != null) {
         this._engine.cancelAllEvents();
      }

      this._media = m;
      Enumeration e = this._servicesTable.elements();

      while (e.hasMoreElements()) {
         Object o = e.nextElement();
         if (o instanceof MediaService) {
            ((MediaService)o).setMedia(m);
         }
      }

      if (m instanceof BasicService) {
         ((BasicService)m).setServices(this);
      }
   }

   public synchronized Object getService(String serviceId) {
      Object service = this._servicesTable.get(serviceId);
      if (service == null) {
         try {
            service = MediaFactory.createService(this._mediaType, serviceId);
            this.registerService(serviceId, service);
            return service;
         } catch (MediaException e) {
            MediaFactory.getPlatform().logDebug(this, 22, -1, e);
            return service;
         }
      } else {
         if (service instanceof MediaService && ((MediaService)service).getMedia() != this._media) {
            ((MediaService)service).setMedia(this._media);
         }

         return service;
      }
   }

   public synchronized void registerService(String serviceId, Object service) {
      if (service != null && this._servicesTable.get(serviceId) == null) {
         if (!this._servicesTable.contains(service)) {
            this._servicesTable.put(serviceId, service);
            if (service instanceof BasicService) {
               ((BasicService)service).setServices(this);
            }

            if (service instanceof MediaService) {
               ((MediaService)service).setMedia(this._media);
               return;
            }
         } else {
            this._servicesTable.put(serviceId, service);
         }
      }
   }

   public synchronized boolean hasService(String serviceId) {
      return this._servicesTable.get(serviceId) != null;
   }

   public EventEngine getEngine() {
      if (this._engine == null) {
         this._engine = new EventEngine();
      }

      return this._engine;
   }

   public void setEngine(EventEngine engine) {
      this._engine = engine;
   }

   public void dispose() {
      Enumeration e = this._servicesTable.elements();

      while (e.hasMoreElements()) {
         Object o = e.nextElement();
         if (o instanceof MediaService) {
            MediaService s = (MediaService)o;
            s.setMedia(null);
            s.dispose();
         }
      }

      this._servicesTable.clear();
      if (this._engine != null) {
         this._engine.shutdown(true);
         this._engine = null;
      }
   }
}
