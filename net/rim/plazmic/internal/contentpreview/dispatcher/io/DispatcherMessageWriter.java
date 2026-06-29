package net.rim.plazmic.internal.contentpreview.dispatcher.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;
import net.rim.plazmic.internal.contentpreview.dispatcher.ServiceInfo;
import net.rim.plazmic.internal.contentpreview.message.AbstractMessageWriter;

public final class DispatcherMessageWriter extends AbstractMessageWriter implements DispatcherEventHandler {
   private final byte[] HEADER = new byte[]{76};
   public static final String rcsid;

   public DispatcherMessageWriter(OutputStream os) {
      super(os);
   }

   @Override
   public final void openSession(String device, boolean hidden) {
      this.writeMessage((byte)1, device, new Object(hidden));
   }

   @Override
   public final void enumerateDevices() {
      this.writeMessage((byte)2);
   }

   @Override
   public final void getValidDevice(String candidateDevice) {
      this.writeMessage((byte)3, candidateDevice);
   }

   @Override
   public final void waitForSessionReady(String sessionName, int timeout) {
      this.writeMessage((byte)4, sessionName, new Object(timeout));
   }

   @Override
   public final void pushFile(String sessionName, String fileName) {
      this.writeMessage((byte)5, sessionName, fileName);
   }

   @Override
   public final void raiseWindow(String sessionName) {
      this.writeMessage((byte)6, sessionName);
   }

   @Override
   public final void closeSession(String sessionName) {
      this.writeMessage((byte)7, sessionName);
   }

   @Override
   public final void shutdownDispatcherService() {
      this.writeMessage((byte)8);
   }

   @Override
   public final void getServerVersion() {
      this.writeMessage((byte)16);
   }

   @Override
   public final void getRecentSession() {
      this.writeMessage((byte)9);
   }

   @Override
   public final void getSpecificSession(int pin) {
      this.writeMessage((byte)10, pin);
   }

   @Override
   public final void getControlPanelPort(String sessionName) {
      this.writeMessage((byte)11, sessionName);
   }

   @Override
   public final void getPlaybackCommandPort(String sessionName) {
      this.writeMessage((byte)12, sessionName);
   }

   @Override
   public final void sessionReady(int pin) {
      this.writeMessage((byte)13, pin);
   }

   @Override
   public final void logMessage(String sessionName, int type, String message, String[] data) {
      this.writeMessage((byte)14, sessionName, new Object(type), message, data);
   }

   @Override
   public final void getSessionProgress(String sessionName) {
      this.writeMessage((byte)15, sessionName);
   }

   @Override
   public final void dequeueThemeRegistrationRequest(int pin) {
      this.writeMessage((byte)17, pin);
   }

   @Override
   public final void dequeueThemeActivationRequest(int pin) {
      this.writeMessage((byte)18, pin);
   }

   @Override
   public final void voidMessage() {
      this.writeMessage((byte)65);
   }

   @Override
   public final void sessionOk(String sessionName) {
      this.writeMessage((byte)66, sessionName);
   }

   @Override
   public final void sessionPort(int port) {
      this.writeMessage((byte)67, port);
   }

   @Override
   public final void sessionProgress(int progress) {
      this.writeMessage((byte)74, progress);
   }

   @Override
   public final void deviceType(String device) {
      this.writeMessage((byte)68, device);
   }

   @Override
   public final void dispatcherServiceFailure(String message) {
      this.writeMessage((byte)69, message);
   }

   @Override
   public final void noSuchSession(String message) {
      this.writeMessage((byte)70, message);
   }

   @Override
   public final void invalidDevice(String message) {
      this.writeMessage((byte)71, message);
   }

   @Override
   public final void timeoutExpiry(String message) {
      this.writeMessage((byte)72, message);
   }

   @Override
   public final void filePushFailure(String message) {
      this.writeMessage((byte)73, message);
   }

   @Override
   public final void themeRequest(String themeName) {
      this.writeMessage((byte)75, themeName);
   }

   @Override
   public final void serverProperties(int version) {
      this.writeMessage((byte)76, version);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final byte[] getHeader() {
      ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object());
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         DataOutputStream ioe = new Object(baos);
         ((DataOutputStream)ioe).writeByte(77);
         ((DataOutputStream)ioe).writeInt(ServiceInfo.getDispatcherServiceInfo().getVersion());
         ((DataOutputStream)ioe).close();
         var4 = false;
      } finally {
         if (var4) {
            return this.HEADER;
         }
      }

      return baos.toByteArray();
   }
}
