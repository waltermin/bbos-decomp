package net.rim.device.api.browser.plugin;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;

public class BrowserContentProviderContext {
   private InputConnection _inputConnection;
   private InputStream _inputStream;
   private RenderingApplication _renderingApplication;
   private RenderingSession _renderingSession;
   private int _flags;
   private Event _event;

   public BrowserContentProviderContext(
      HttpConnection httpConnection, RenderingApplication renderingApplication, RenderingSession renderingSession, int flags, Event event
   ) {
      this._inputConnection = httpConnection;
      this._renderingApplication = renderingApplication;
      this._renderingSession = renderingSession;
      this._flags = flags;
      this._event = event;
   }

   public BrowserContentProviderContext(
      InputConnection inputConnection, InputStream in, RenderingApplication renderingApplication, RenderingSession renderingSession, int flags, Event event
   ) {
      this._inputConnection = inputConnection;
      this._inputStream = in;
      this._renderingApplication = renderingApplication;
      this._renderingSession = renderingSession;
      this._flags = flags;
      this._event = event;
   }

   public Event getEvent() {
      return this._event;
   }

   public int getFlags() {
      return this._flags;
   }

   public HttpConnection getHttpConnection() {
      return (HttpConnection)(!(this._inputConnection instanceof Object) ? null : this._inputConnection);
   }

   public InputConnection getInputConnection() {
      return this._inputConnection;
   }

   public InputStream getInputStream() {
      return this._inputStream;
   }

   public RenderingApplication getRenderingApplication() {
      return this._renderingApplication;
   }

   public RenderingSession getRenderingSession() {
      return this._renderingSession;
   }
}
