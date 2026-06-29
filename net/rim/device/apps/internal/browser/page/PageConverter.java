package net.rim.device.apps.internal.browser.page;

import java.io.DataInput;
import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.plugin.BrowserPageContext;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.util.RendererControl;

public class PageConverter extends BaseConverter {
   private static PageConverter _instance;

   public static PageConverter getInstance() {
      if (_instance == null) {
         _instance = new PageConverter();
      }

      return _instance;
   }

   @Override
   public boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public Object convert(byte[] inputBytes, Object contextObject) {
      return !(contextObject instanceof PageConverterWrapper) ? null : this.convert((PageConverterWrapper)contextObject);
   }

   @Override
   public Object convert(DataInput in, Object contextObject) {
      return !(contextObject instanceof PageConverterWrapper) ? null : this.convert((PageConverterWrapper)contextObject);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public Object convert(PageConverterWrapper wrapper) {
      InputConnection inputConnection = wrapper.getInputConnection();
      InputStream inputStream = wrapper.getInStream();
      FetchRequest fetchRequest = wrapper.getFetchRequest();
      ModelResult modelResult = fetchRequest.getModelResult();
      String url = null;
      if (!(inputConnection instanceof HttpConnection)) {
         url = modelResult.getURL();
      } else {
         url = ((HttpConnection)inputConnection).getURL();
      }

      RenderingSession renderingSession = BrowserDaemonRegistry.getInstance().getCurrentRenderingSession();
      int flags = modelResult.getRenderingFlags();
      Page page = new Page(fetchRequest, null, 0);
      RenderingApplication renderingApplication = page;
      if ((flags & 16) != 0) {
         BrowserContent field = BrowserImpl.getSource(fetchRequest.getEvent());
         if (field != null) {
            renderingApplication = field.getRenderingApplication();
         }
      }

      BrowserContent browserContent = null;

      try {
         browserContent = RendererControl.renderBrowserContent(
            renderingSession,
            inputConnection,
            inputStream,
            url,
            renderingApplication,
            flags,
            fetchRequest.getEvent(),
            modelResult.getContext(),
            fetchRequest.getTarget()
         );
      } catch (Throwable var16) {
         fetchRequest.setError(re);
         return null;
      }

      if (browserContent != null) {
         fetchRequest.setError(browserContent.getError());
         if (browserContent.getRenderingApplication() instanceof Page) {
            page = (Page)browserContent.getRenderingApplication();
         }

         BrowserContentImpl field = null;
         if (!(browserContent instanceof BrowserContentImpl)) {
            field = new BrowserContentWrapper(browserContent);
         } else {
            field = (BrowserContentImpl)browserContent;
         }

         page.setBrowserContent(field);
         if ((flags & 16) == 0) {
            BrowserPageContext browserPageContext = browserContent.getBrowserPageContext();
            if (browserPageContext != null) {
               int style = browserPageContext.getPropertyWithIntValue(2, 0);
               page.setStyle(style);
            }
         }

         return page;
      } else {
         return page;
      }
   }
}
