package net.rim.device.internal.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.vm.Message;
import net.rim.vm.Process;

public interface ApplicationManagerInternal {
   long GUID_MODULE_ADDED = 256826950193107649L;
   long GUID_MODULE_DELETED = -4232371946002803201L;
   long GUID_MODULES_CHANGED = 3542951112318790170L;
   long GUID_INVOKE_TASK_SWITCHER = 7563637690172082503L;
   long GUID_DISPLAY_MESSAGE = 9056933960126321432L;
   long GUID_PROCESS_EXITED = -1270659756336956134L;
   long GUID_INVOKE_INPUT_LOCALE_SWITCHER = -8249535590121003989L;
   long GUID_OTASL_STATE_CHANGED = -5179361672050507927L;
   long net_rim_device_api_i18n_DateFormat_GUID_DATE_FORMAT_CHANGED = 7207871974803693937L;
   long net_rim_device_api_util_DateTimeUtilities_GUID_DATE_CHANGED = 8877632280522743328L;
   long net_rim_device_api_util_DateTimeUtilities_GUID_TIMEZONE_CHANGED = 3596208183088439728L;
   long net_rim_device_api_i18n_Locale_Locale_GUID_INPUT_LOCALE_CHANGED = -8040378802380461050L;
   long net_rim_device_api_i18n_Locale_Locale_GUID_LOCALE_CHANGED = -7464003439710973532L;
   long net_rim_device_api_itpolicy_ITPolicy_GUID_IT_POLICY_CHANGED = 8508406279413621091L;
   long net_rim_device_api_itpolicy_ITPolicy_GUID_IT_POLICY_CHANGED_LOCKED_HANDHELD = -594020114676189989L;
   long net_rim_device_api_itpolicy_ITPolicy_GUID_PASSWORD_SET_BY_ITADMIN = 1309561383038111736L;
   long net_rim_device_api_itpolicy_ITPolicy_GUID_OWNER_INFO_CHANGED = -8392006003204551101L;
   long net_rim_device_api_itpolicy_ITPolicy_DURESS_NOTIFICATION = 4681343386835470834L;
   long net_rim_device_api_lowmemory_LowMemoryManager_GUID_FLASH_LOW = 945659952435832745L;
   long net_rim_device_api_ui_Font_GUID_FONT_CHANGED = -4394903006263251010L;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_ADDED = -4220058463650496006L;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_BR_END = -583230596614878690L;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_BR_START = 1348796660760556312L;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_CHANGED = 8288627527798139133L;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_OTA_SWITCH = -5256071285987383000L;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_OTA_UPDATE = 6213587377148297993L;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_POLICY_CHANGED = 1077267820605375385L;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_REMOVED = 2522898683889177438L;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_PIN_CHANGE = -1426098722237447363L;
   long net_rim_device_api_io_DatagramStatusListener_GUID_GME_CRYPTO_FAILURE = -5448760422790860711L;
   long net_rim_rim_device_api_ui_Keypad_GUID_KEYPAD_CHANGED = -3769281743063593175L;
   long net_rim_rim_device_api_ui_Keypad_GUID_KEYPAD_OPTIONS_CHANGED = 6498096261923284925L;
   long net_rim_device_apps_api_service_ServiceIdentifier_DEFAULT_SERVICE_SET = 158775118060600435L;
   long net_rim_device_apps_api_service_ServiceIdentifier_SERVICE_EVENT_ADDED = -860845403685493259L;
   long net_rim_device_apps_api_service_ServiceIdentifier_SERVICE_EVENT_CHANGED = 8478935834746748823L;
   long net_rim_device_apps_api_service_ServiceIdentifier_SERVICE_EVENT_REMOVED = -7853136852381124900L;
   long net_rim_device_apps_api_service_ServiceIdentifier_SERVICE_EVENT_NONEXPIRE = 5061624963542184609L;

   boolean isSecurityLockSupercedingProcessForeground();

   boolean setConsoleProcess();

   boolean setSecurityLockSupercedingProcess();

   boolean setSecurityManager(SecurityManager var1);

   SecurityManager getSecurityManager();

   void setNativeSocketProcess();

   boolean setEngScreenDescriptor(ApplicationDescriptor var1);

   ApplicationDescriptor getEngScreenDescriptor();

   void redirectInput(Process var1, boolean var2);

   void repaintForeground();

   boolean postMessage(int var1, Message var2);

   void postMessage(Message var1);

   void addForegroundChangeListener(Runnable var1);

   void removeForegroundChangeListener(Runnable var1);

   Application getForegroundApplication();

   int runApplication(ApplicationDescriptor var1, Thread var2);

   String[] getSchedulingLog();

   void enableSchedulingLog(boolean var1, int var2);

   ApplicationProcess[] getProcesses();

   void updateAppDescriptor(String var1, String var2, int var3);

   void resetAppDescriptorOverrides();
}
