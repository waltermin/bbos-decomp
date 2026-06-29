package net.rim.device.apps.internal.browser.plugin.vcard;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.RendererControl;

public final class VCardRenderingConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"text/x-vcard"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return ACCEPT;
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext providerContext) {
      RenderingApplication renderingApplication = providerContext.getRenderingApplication();
      RenderingOptions renderingOptions = providerContext.getRenderingSession().getRenderingOptions();
      InputConnection inputConnection = providerContext.getInputConnection();
      int flags = providerContext.getFlags();
      String baseUrl = RendererControl.getUrl(inputConnection);
      InputStream in = providerContext.getInputStream();
      long fieldStyle = (flags & 16) == 0 ? 2305843009213693952L : 0;
      BrowserContentBaseImpl browserContent = new BrowserContentBaseImpl(baseUrl, null, renderingApplication, renderingOptions, flags);
      boolean contentSet = false;

      label55:
      try {
         if (in == null) {
            in = inputConnection.openInputStream();
         }

         byte[] inputBytes = RendererControl.readBytesFromInputStream(in);
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         Factory factory = (Factory)ar.get(9048770516632928843L);
         if (factory != null) {
            ContextObject contextObject = new ContextObject();
            String encoding = RendererControl.getCharacterEncoding(inputConnection);
            if (encoding == null) {
               encoding = "ASCII";
            }

            contextObject.put(8849067667159082262L, inputBytes);
            contextObject.put(253, encoding);
            contextObject.put(4086083307293257364L, Boolean.TRUE);
            Object pimObject = factory.createInstance(contextObject);
            if (pimObject instanceof AddressCardModel && pimObject instanceof FieldProvider) {
               browserContent.setContent(new VCardField(fieldStyle, (AddressCardModel)pimObject));
               contentSet = true;
            }
         }
      } finally {
         break label55;
      }

      if (!contentSet) {
         VerticalFieldManager vfm = new VerticalFieldManager();
         vfm.add(new RichTextField(BrowserResources.getString(235)));
         browserContent.setContent(vfm);
      }

      return browserContent;
   }
}
