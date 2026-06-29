package net.rim.device.apps.internal.bis.launch.protocol;

import java.io.InputStream;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.apps.internal.bis.launch.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.launch.http.HttpClient;
import net.rim.device.apps.internal.bis.launch.http.HttpResponse;

public final class RestClient {
   private static final String REST_RESPONSECODE_PROPERTY = "RESTResponseCode";
   private static final String[] REQUIRED_REST_RESPONSE_PROPERTIES = new String[]{"RESTResponseCode"};
   private static final String DEFAULT_SCHEME = "https";
   private static final String CORE_STATUS_URL_PATTERN = "{0}://{1}/{2}/clientApp/{3}/core/status";
   private static final String CORE_STATUS_URL_PATTERN2 = "{0}://{1}/{2}/updates";
   private static final String WAP_CID = "WAP";

   public static final String createAppStatusPayload() {
      String deviceName = DeviceInfo.getDeviceName();
      String deviceId = ((StringBuffer)(new Object(""))).append(DeviceInfo.getDeviceId()).toString();
      String manufacturerName = DeviceInfo.getManufacturerName();
      String osVersion = DeviceInfo.getPlatformVersion();
      String platformVersion = "0.9";
      int platformHandle = CodeModuleManager.getModuleHandle("net_rim_os");
      if (platformHandle > 0) {
         platformVersion = CodeModuleManager.getModuleVersion(platformHandle);
      }

      String isSimulator = DeviceInfo.isSimulator() ? "true" : "false";
      StringBuffer sb = (StringBuffer)(new Object());
      beginTag(sb, "status");
      sb.append("\r\n");
      addTag(sb, "deviceName", deviceName);
      addTag(sb, "deviceId", deviceId);
      addTag(sb, "manufacturerName", manufacturerName);
      addTag(sb, "osVersion", osVersion);
      addTag(sb, "platformVersion", platformVersion);
      addTag(sb, "isSimulator", isSimulator);
      beginTag(sb, "modules");
      sb.append("\r\n");
      int[] handles = CodeModuleManager.getModuleHandles();

      for (int i = 0; i < handles.length; i++) {
         int handle = handles[i];
         String moduleName = CodeModuleManager.getModuleName(handle);
         if (moduleName.startsWith("net_rim_bis")) {
            String version = CodeModuleManager.getModuleVersion(handle);
            beginTag(sb, "module");
            sb.append("\r\n");
            addTag(sb, "name", moduleName);
            addTag(sb, "version", version);
            endTag(sb, "module");
         }
      }

      endTag(sb, "modules");
      endTag(sb, "status");
      return sb.toString();
   }

   private static final void addTag(StringBuffer sb, String tagName, String value) {
      beginTag(sb, tagName);
      sb.append(value);
      endTag(sb, tagName);
   }

   private static final void beginTag(StringBuffer sb, String tagName) {
      sb.append('<');
      sb.append(tagName);
      sb.append('>');
   }

   private static final void endTag(StringBuffer sb, String tagName) {
      sb.append("</");
      sb.append(tagName);
      sb.append('>');
      sb.append("\r\n");
   }

   public static final UpdateInfo getAppStatus2(String siteName) {
      Object[] urlArgs = new Object[]{"https", BISClientConfigRecord.getBISClientConfigRecord().getServerURL(), siteName};
      String url = MessageFormat.format("{0}://{1}/{2}/updates", urlArgs);
      String transportUID = BISClientConfigRecord.getBISClientConfigRecord().getTransportUID();
      long downloadTimeout = BISClientConfigRecord.getBISClientConfigRecord().getServiceTimeout();
      boolean useWapGateway = "WAP".equalsIgnoreCase(BISClientConfigRecord.getBISClientConfigRecord().getTransportCID());
      HttpClient httpClient = new HttpClient(transportUID, downloadTimeout, null, 0, useWapGateway);
      String payload = createAppStatusPayload();
      HttpResponse response = httpClient.doXmlExchange(url, "POST", null, payload.getBytes(), REQUIRED_REST_RESPONSE_PROPERTIES, true);
      if (response.getHttpResponseCode() != 200) {
         return null;
      }

      try {
         long restResponseCode = Long.parseLong(response.getResponseProperty("RESTResponseCode"));
      } finally {
         ;
      }

      UpdateStatusHandler updateStatusHandler = new UpdateStatusHandler();
      if (response.getResponsePayload() == null) {
      }

      try {
         return updateStatusHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
      } finally {
         ;
      }
   }
}
