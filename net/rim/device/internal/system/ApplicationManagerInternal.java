package net.rim.device.internal.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.vm.Message;
import net.rim.vm.Process;

public interface ApplicationManagerInternal {
   long GUID_MODULE_ADDED;
   long GUID_MODULE_DELETED;
   long GUID_MODULES_CHANGED;
   long GUID_INVOKE_TASK_SWITCHER;
   long GUID_DISPLAY_MESSAGE;
   long GUID_PROCESS_EXITED;
   long GUID_INVOKE_INPUT_LOCALE_SWITCHER;
   long GUID_OTASL_STATE_CHANGED;
   long net_rim_device_api_i18n_DateFormat_GUID_DATE_FORMAT_CHANGED;
   long net_rim_device_api_util_DateTimeUtilities_GUID_DATE_CHANGED;
   long net_rim_device_api_util_DateTimeUtilities_GUID_TIMEZONE_CHANGED;
   long net_rim_device_api_i18n_Locale_Locale_GUID_INPUT_LOCALE_CHANGED;
   long net_rim_device_api_i18n_Locale_Locale_GUID_LOCALE_CHANGED;
   long net_rim_device_api_itpolicy_ITPolicy_GUID_IT_POLICY_CHANGED;
   long net_rim_device_api_itpolicy_ITPolicy_GUID_IT_POLICY_CHANGED_LOCKED_HANDHELD;
   long net_rim_device_api_itpolicy_ITPolicy_GUID_PASSWORD_SET_BY_ITADMIN;
   long net_rim_device_api_itpolicy_ITPolicy_GUID_OWNER_INFO_CHANGED;
   long net_rim_device_api_itpolicy_ITPolicy_DURESS_NOTIFICATION;
   long net_rim_device_api_lowmemory_LowMemoryManager_GUID_FLASH_LOW;
   long net_rim_device_api_ui_Font_GUID_FONT_CHANGED;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_ADDED;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_BR_END;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_BR_START;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_CHANGED;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_OTA_SWITCH;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_OTA_UPDATE;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_POLICY_CHANGED;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_REMOVED;
   long net_rim_device_api_servicebook_ServiceBook_GUID_SB_PIN_CHANGE;
   long net_rim_device_api_io_DatagramStatusListener_GUID_GME_CRYPTO_FAILURE;
   long net_rim_rim_device_api_ui_Keypad_GUID_KEYPAD_CHANGED;
   long net_rim_rim_device_api_ui_Keypad_GUID_KEYPAD_OPTIONS_CHANGED;
   long net_rim_device_apps_api_service_ServiceIdentifier_DEFAULT_SERVICE_SET;
   long net_rim_device_apps_api_service_ServiceIdentifier_SERVICE_EVENT_ADDED;
   long net_rim_device_apps_api_service_ServiceIdentifier_SERVICE_EVENT_CHANGED;
   long net_rim_device_apps_api_service_ServiceIdentifier_SERVICE_EVENT_REMOVED;
   long net_rim_device_apps_api_service_ServiceIdentifier_SERVICE_EVENT_NONEXPIRE;

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
