package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;

public class FontManagerProvider implements ResourceProvider {
   public static final String EXTENSION_ID;

   @Override
   public Object createResource(String type, Object data, ResourceContext context, Object referrer) {
      Object resource = null;
      if (data instanceof Object) {
         resource = this.createFontManager((String)data);
      }

      return resource;
   }

   @Override
   public Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer) {
      Object resource = null;
      int query = uri.indexOf(63);
      if (query != -1) {
         resource = this.createFontManager(uri);
      }

      return resource;
   }

   private Object createFontManager(String uri) {
      return FontManagerForeignObjectFactory.getFactory().createInstance(uri);
   }
}
