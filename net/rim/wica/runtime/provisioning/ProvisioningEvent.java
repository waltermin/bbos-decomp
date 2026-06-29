package net.rim.wica.runtime.provisioning;

public class ProvisioningEvent {
   private ProvisioningTaskInfo provisioningTaskInfo;
   private int type;
   private int param;
   private Object _eventCookie;
   private Throwable provisioningError;
   private String errorMessage;
   public static final int TYPE_STARTED;
   public static final int TYPE_SUCCESSFUL_INSTALL;
   public static final int TYPE_SUCCESSFUL_UPGRADE;
   public static final int TYPE_ERROR;
   public static final int TYPE_PROGRESS_UPDATE;
   public static final int PARAM_PROGRESS_UPDATE_DOWNLOADING;
   public static final int PARAM_PROGRESS_UPDATE_CONFIGURING;
   public static final int PARAM_PROGRESS_UPDATE_INSTALLING;
   public static final int PARAM_PROGRESS_UPDATE_FINISHED;
   public static final int PARAM_PROGRESS_UPDATE_CANCELLED;
   public static final int PARAM_ERROR_UNEXPECTED_PROVISIONING_PROBLEM;
   public static final int PARAM_ERROR_HANDSHAKE_FAILED;
   public static final int PARAM_ERROR_INVALID_WICLET_URL;
   public static final int PARAM_ERROR_EXPIRED_WICLET_DOWNLOAD;
   public static final int PARAM_ERROR_PROVISIONING_ENCODING;
   public static final int PARAM_ERROR_APPLICATION_ALREADY_INSTALLED;

   public ProvisioningEvent() {
   }

   public ProvisioningEvent(ProvisioningTaskInfo provisioningTaskInfo, int type, int param, Throwable provisioningError, String errorMessage) {
      this.provisioningTaskInfo = provisioningTaskInfo;
      this.type = type;
      this.param = param;
      this.provisioningError = provisioningError;
      this.errorMessage = errorMessage;
   }

   public ProvisioningEvent(ProvisioningTaskInfo provisioningTaskInfo, Object cookie, int type, int param, Throwable provisioningError, String errorMessage) {
      this(provisioningTaskInfo, type, param, provisioningError, errorMessage);
      this._eventCookie = cookie;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public int getParam() {
      return this.param;
   }

   public Throwable getProvisioningError() {
      return this.provisioningError;
   }

   public int getType() {
      return this.type;
   }

   public ProvisioningTaskInfo getProvisioningTaskInfo() {
      return this.provisioningTaskInfo;
   }

   public Object getEventCookie() {
      return this._eventCookie;
   }
}
