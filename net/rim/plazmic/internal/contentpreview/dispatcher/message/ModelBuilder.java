package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class ModelBuilder implements DispatcherEventHandler {
   private Model _model;
   public static final String rcsid;

   public final Model getModel() {
      return this._model;
   }

   protected final void modelBuilt() {
   }

   @Override
   public final void openSession(String device, boolean hidden) {
      this.setModel(new OpenSession(device, hidden));
   }

   @Override
   public final void enumerateDevices() {
      this.setModel(new EnumerateDevices());
   }

   @Override
   public final void getValidDevice(String candidateDevice) {
      this.setModel(new GetValidDevice(candidateDevice));
   }

   @Override
   public final void waitForSessionReady(String sessionName, int timeout) {
      this.setModel(new WaitForSessionReady(sessionName, timeout));
   }

   @Override
   public final void pushFile(String sessionName, String fileName) {
      this.setModel(new PushFile(sessionName, fileName));
   }

   @Override
   public final void raiseWindow(String sessionName) {
      this.setModel(new RaiseWindow(sessionName));
   }

   @Override
   public final void closeSession(String sessionName) {
      this.setModel(new CloseSession(sessionName));
   }

   @Override
   public final void shutdownDispatcherService() {
      this.setModel(new ShutdownDispatcherService());
   }

   @Override
   public final void getServerVersion() {
      this.setModel(new GetServerVersion());
   }

   @Override
   public final void getRecentSession() {
      this.setModel(new GetRecentSession());
   }

   @Override
   public final void getSpecificSession(int pin) {
      this.setModel(new GetSpecificSession(pin));
   }

   @Override
   public final void getControlPanelPort(String sessionName) {
      this.setModel(new GetControlPanelPort(sessionName));
   }

   @Override
   public final void getPlaybackCommandPort(String sessionName) {
      this.setModel(new GetPlaybackCommandPort(sessionName));
   }

   @Override
   public final void logMessage(String sessionName, int type, String message, String[] data) {
      this.setModel(new LogMessage(sessionName, type, message, data));
   }

   @Override
   public final void getSessionProgress(String sessionName) {
      this.setModel(new GetSessionProgress(sessionName));
   }

   @Override
   public final void sessionReady(int pin) {
      this.setModel(new SessionReady(pin));
   }

   @Override
   public final void dequeueThemeRegistrationRequest(int pin) {
      this.setModel(new DequeueThemeRegistrationRequest(pin));
   }

   @Override
   public final void dequeueThemeActivationRequest(int pin) {
      this.setModel(new DequeueThemeActivationRequest(pin));
   }

   @Override
   public final void voidMessage() {
      this.setModel(new VoidMessage());
   }

   @Override
   public final void sessionOk(String sessionName) {
      this.setModel(new SessionOk(sessionName));
   }

   @Override
   public final void sessionPort(int port) {
      this.setModel(new SessionPort(port));
   }

   @Override
   public final void sessionProgress(int progress) {
      this.setModel(new SessionProgress(progress));
   }

   @Override
   public final void deviceType(String device) {
      this.setModel(new DeviceType(device));
   }

   @Override
   public final void dispatcherServiceFailure(String message) {
      this.setModel(new DispatcherServiceFailure(message));
   }

   @Override
   public final void noSuchSession(String message) {
      this.setModel(new NoSuchSession(message));
   }

   @Override
   public final void invalidDevice(String message) {
      this.setModel(new InvalidDevice(message));
   }

   @Override
   public final void timeoutExpiry(String message) {
      this.setModel(new TimeoutExpiry(message));
   }

   @Override
   public final void filePushFailure(String message) {
      this.setModel(new FilePushFailure(message));
   }

   @Override
   public final void themeRequest(String themeName) {
      this.setModel(new ThemeRequest(themeName));
   }

   @Override
   public final void serverProperties(int version) {
      this.setModel(new ServerProperties(version));
   }

   private final void setModel(Model model) {
      this._model = model;
      this.modelBuilt();
   }
}
