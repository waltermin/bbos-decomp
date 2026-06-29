package net.rim.device.apps.internal.browser.content;

import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.RendererControl;

public class ContentHandlerRenderingConverter extends BrowserContentProvider {
   private final String[] _type;

   public ContentHandlerRenderingConverter(String type) {
      this._type = new Object[]{type};
   }

   @Override
   public String[] getSupportedMimeTypes() {
      return this._type;
   }

   @Override
   public String[] getAccept(RenderingOptions context) {
      return this._type;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public BrowserContent getBrowserContent(BrowserContentProviderContext context) {
      int flags = context.getFlags();
      if ((flags & 16) != 0) {
         throw new Object("Content Handlers cannot render embedded content");
      }

      InputConnection connection = context.getInputConnection();
      String url = RendererControl.getUrl(connection);
      if (url != null && url.length() != 0) {
         Registry registry = Registry.getRegistry("net.rim.device.apps.internal.browser.core.BrowserLoader");
         boolean var16 = false /* VF: Semaphore variable */;

         try {
            var16 = true;
            Invocation application = new Object(url);
            ((Invocation)application).setResponseRequired(false);
            ((Invocation)application).setAction("open");
            registry.invoke((Invocation)application);
            var16 = false;
         } finally {
            if (var16) {
               throw new Object("Could not start content handler");
            }
         }

         RenderingApplication application = context.getRenderingApplication();
         RenderingOptions options = context.getRenderingSession().getRenderingOptions();
         BrowserContentBaseImpl content = (BrowserContentBaseImpl)(new Object(url, null, application, options, flags));
         VerticalFieldManager manager = (VerticalFieldManager)(new Object(5764607523034234880L));
         int slash = url.lastIndexOf(47);
         String filename = slash == -1 ? url : url.substring(slash + 1);
         String msg = MessageFormat.format(BrowserResources.getString(900), new Object[]{filename});
         RichTextField field = (RichTextField)(new Object(msg, 2305843073705312256L));
         int padding = Font.getDefault().getHeight() * 2;
         field.setPadding(padding, 0, padding, 0);
         manager.add(field);
         content.setContent(manager);
         return content;
      } else {
         throw new Object("Content Handler cannot be invoked without a URL");
      }
   }
}
