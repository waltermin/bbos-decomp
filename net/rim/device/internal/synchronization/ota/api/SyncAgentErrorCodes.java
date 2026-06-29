package net.rim.device.internal.synchronization.ota.api;

public interface SyncAgentErrorCodes {
   short SUCCESS;
   short Not_Supported_Protocol_Version;
   short Invalid_Session_Datagram;
   short Invalid_Session_State;
   short Invalid_Sync_State;
   short Database_Not_Found;
   short DataSource_Not_Found;
   short Not_Implemented_Command;
   short Unknown_Command;
   short Invalid_Command;
   short Record_Not_Found;
   short Operation_Failure;
   short Database_Not_Enabled;
   short LowMemory;
   short Obselete_Change;
   short Not_Recevied_Datagram;
   short Database_Table_Not_Found;
   short Suspended_Change;
   short Out_Of_Context_Session;
   short User_Disabled;
   short Retry;
   short Late_Session;
}
