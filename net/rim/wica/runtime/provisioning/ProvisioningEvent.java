package net.rim.wica.runtime.provisioning;

public class ProvisioningEvent {
   private ProvisioningTaskInfo provisioningTaskInfo;
   private int type;
   private int param;
   private Object _eventCookie;
   private Throwable provisioningError;
   private String errorMessage;
   public static final int TYPE_STARTED = 1;
   public static final int TYPE_SUCCESSFUL_INSTALL = 2;
   public static final int TYPE_SUCCESSFUL_UPGRADE = 5;
   public static final int TYPE_ERROR = 3;
   public static final int TYPE_PROGRESS_UPDATE = 4;
   public static final int PARAM_PROGRESS_UPDATE_DOWNLOADING = 1;
   public static final int PARAM_PROGRESS_UPDATE_CONFIGURING = 2;
   public static final int PARAM_PROGRESS_UPDATE_INSTALLING = 3;
   public static final int PARAM_PROGRESS_UPDATE_FINISHED = 4;
   public static final int PARAM_PROGRESS_UPDATE_CANCELLED = 5;
   public static final int PARAM_ERROR_UNEXPECTED_PROVISIONING_PROBLEM = 190;
   public static final int PARAM_ERROR_HANDSHAKE_FAILED = 191;
   public static final int PARAM_ERROR_INVALID_WICLET_URL = 192;
   public static final int PARAM_ERROR_EXPIRED_WICLET_DOWNLOAD = 193;
   public static final int PARAM_ERROR_PROVISIONING_ENCODING = 194;
   public static final int PARAM_ERROR_APPLICATION_ALREADY_INSTALLED = 195;

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
