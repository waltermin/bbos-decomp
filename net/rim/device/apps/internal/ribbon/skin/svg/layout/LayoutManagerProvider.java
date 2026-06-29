package net.rim.device.apps.internal.ribbon.skin.svg.layout;

import java.util.Hashtable;
import net.rim.device.apps.internal.ribbon.skin.svg.Util;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;

public class LayoutManagerProvider implements ResourceProvider {
   public static final String EXTENSION_ID;

   @Override
   public Object createResource(String type, Object data, ResourceContext context, Object referrer) {
      Object resource = null;
      if (data instanceof Object) {
         resource = this.createLayoutManager((String)data, context);
      }

      return resource;
   }

   @Override
   public Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer) {
      Object resource = null;
      int query = uri.indexOf(63);
      if (query != -1) {
         resource = this.createLayoutManager(uri.substring(query + 1), context);
      }

      return resource;
   }

   private Object createLayoutManager(String uri, ResourceContext context) {
      ModelInteractorImpl model = (ModelInteractorImpl)context.get("Media");
      Hashtable parameters = (Hashtable)(new Object());
      Util.fillParameters(uri, parameters);
      return LayoutManagerFactory.createInstance(model, parameters);
   }
}
