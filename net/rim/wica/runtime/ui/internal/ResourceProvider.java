package net.rim.wica.runtime.ui.internal;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.browser.util.UAProf;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.wica.runtime.comm.CommunicationService;
import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.comm.Response;
import net.rim.wica.runtime.comm.ResponseListener;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.ui.ResourceCollection;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.persistence.WebResource;
import net.rim.wica.runtime.persistence.WicletStore;
import net.rim.wica.runtime.resources.RuntimeResources;

public final class ResourceProvider implements ResponseListener {
   private String _uaProf;
   private String _acceptTypes;
   private Hashtable _pendingResources = new Hashtable(10);
   private Wiclet _definition;
   private ResourceCollection _resources;
   private WicletStore _store;
   private PersistenceService _persistenceService;
   private CommunicationService _commService;
   private UiApplication _application;
   public static final String CONTENT_TYPE_MEDIA_PLAZMIC_B = "application/x-vnd.rim.pme.b";
   private static final String CONTENT_TYPE_MEDIA_PLAZMIC = "application/x-vnd.rim.pme";
   private static final String HTTP_SCHEME = "http://";
   private static final String HTTPS_SCHEME = "https://";
   private static final String LOCAL_SCHEME = "local://";
   private static final String[] ACCEPT_TYPES = new String[]{
      "image/png", "image/vnd.rim.png", "image/gif", "image/jpeg", "application/x-vnd.rim.pme", "application/x-vnd.rim.pme.b"
   };

   ResourceProvider(Wiclet definition, PersistenceService persistenceService, CommunicationService commService) {
      this._definition = definition;
      this._resources = definition.getResources();
      this._store = definition.getContext().getWicletStore();
      this._persistenceService = persistenceService;
      this._commService = commService;
      this._application = UiApplication.getUiApplication();
   }

   public static final Bitmap getDefaultBitmapImage() {
      return RuntimeResources.getBitmapResource("default.png");
   }

   public static final byte[] getDefaultMediaContent() {
      return RuntimeResources.getBinaryResource("default.pmb");
   }

   public static final boolean isMediaEngineContent(String contentType) {
      return contentType.equals("application/x-vnd.rim.pme") || contentType.equals("application/x-vnd.rim.pme.b");
   }

   public final Resource getResource(String resourceName, ResourceListener listener) {
      Resource resource = null;
      if (resourceName.startsWith("http://") || resourceName.startsWith("https://")) {
         WebResource r = (WebResource)this._store.getResource(resourceName);
         if (r != null && r.getExpiration() >= System.currentTimeMillis()) {
            return r;
         }

         try {
            Vector listeners = (Vector)this._pendingResources.get(resourceName);
            if (listeners == null) {
               listeners = new Vector(1);
               this._pendingResources.put(resourceName, listeners);
               OutgoingRequest request = this._commService.createOutgoingRequestInstance(resourceName);
               request.setHeader("Accept", this.getAcceptTypes());
               request.setHeader("profile", this.getCurrentUAProfValue());
               request.setHeader("x-rim-transcode-content", "*/*");
               if (r != null) {
                  String lastModified = r.getHeaders().getPropertyValue("Last-Modified");
                  if (lastModified != null) {
                     request.setHeader("If-Modified-Since", lastModified);
                  }
               }

               request.setResponseListener(this);
               request.setCustomData(r);
               this._commService.sendRequest(request);
            }

            listeners.addElement(new WeakReference(listener));
            return resource;
         } finally {
            ;
         }
      } else if (resourceName.startsWith("local://")) {
         String path = resourceName.substring(8);
         int index = path.lastIndexOf(47);
         if (index >= 0) {
            String uri = path.substring(0, index);
            String fileName = path.substring(index + 1);
            return this._persistenceService.getApplication(uri).getResource(fileName);
         } else {
            return resource;
         }
      } else {
         return this.getNamedResource(resourceName);
      }
   }

   public final Resource getApplicationResource(int resourceId) {
      String uri = this._resources.getResourceURI(resourceId);
      return this._store.getResource(uri);
   }

   private final Resource getNamedResource(String name) {
      Resource resource = null;
      int resourceId = this._definition.getDefHandle(name);
      if (resourceId != -1) {
         resource = this.getApplicationResource(resourceId);
      }

      return resource;
   }

   public final String getResourceContentType(int resourceId) {
      return this._resources.getResourceContentType(resourceId);
   }

   private final String getCurrentUAProfValue() {
      if (this._uaProf != null) {
         return this._uaProf;
      }

      this._uaProf = UAProf.getDefaultUAProfURI();
      return this._uaProf;
   }

   private final String getAcceptTypes() {
      if (this._acceptTypes != null) {
         return this._acceptTypes;
      }

      StringBuffer acceptTypes = new StringBuffer();
      boolean addSeparator = false;
      int length = ACCEPT_TYPES.length;

      for (int i = 0; i < length; i++) {
         if (addSeparator) {
            acceptTypes.append(',');
         } else {
            addSeparator = true;
         }

         acceptTypes.append(ACCEPT_TYPES[i]);
      }

      this._acceptTypes = acceptTypes.toString();
      return this._acceptTypes;
   }

   @Override
   public final void processResponse(Response response, OutgoingRequest request) {
      String uri = request.getUrl();
      int status = response.getResponseCode();
      WebResource originalResource = (WebResource)request.getCustomData();
      WebResource resource = null;
      if (status == 304 && originalResource != null) {
         originalResource.setHeaders(response.getHeaders());
         resource = originalResource;
         this._store.storeResource(resource);
      } else if ((status == 200 || status == 203 || status == 300 || status == 301) && response.hasData()) {
         resource = new WebResource(uri, response.getData(), response.getHeaders());
         this._store.storeResource(resource);
      }

      Vector listeners = (Vector)this._pendingResources.remove(uri);
      if (resource != null && listeners != null) {
         int length = listeners.size();

         for (int i = 0; i < length; i++) {
            WeakReference reference = (WeakReference)listeners.elementAt(i);
            ResourceListener listener = (ResourceListener)reference.get();
            Resource r = resource;
            if (listener != null) {
               this._application.invokeLater(new ResourceProvider$1(this, listener, r));
            }
         }
      }
   }
}
