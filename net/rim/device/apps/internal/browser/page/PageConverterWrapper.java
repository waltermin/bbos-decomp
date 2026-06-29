package net.rim.device.apps.internal.browser.page;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.apps.internal.browser.stack.FetchRequest;

public final class PageConverterWrapper {
   private FetchRequest _fetchRequest;
   private InputStream _inStream;
   private InputConnection _inConnection;

   public PageConverterWrapper(FetchRequest fr, InputStream in, InputConnection inConn) {
      this._inConnection = inConn;
      this._inStream = in;
      this._fetchRequest = fr;
   }

   public final InputConnection getInputConnection() {
      return this._inConnection;
   }

   public final InputStream getInStream() {
      return this._inStream;
   }

   public final FetchRequest getFetchRequest() {
      return this._fetchRequest;
   }
}
