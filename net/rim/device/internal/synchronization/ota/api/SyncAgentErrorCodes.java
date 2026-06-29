package net.rim.device.internal.synchronization.ota.api;

public interface SyncAgentErrorCodes {
   short SUCCESS = 200;
   short Not_Supported_Protocol_Version = 401;
   short Invalid_Session_Datagram = 402;
   short Invalid_Session_State = 403;
   short Invalid_Sync_State = 404;
   short Database_Not_Found = 405;
   short DataSource_Not_Found = 406;
   short Not_Implemented_Command = 407;
   short Unknown_Command = 408;
   short Invalid_Command = 409;
   short Record_Not_Found = 410;
   short Operation_Failure = 411;
   short Database_Not_Enabled = 412;
   short LowMemory = 413;
   short Obselete_Change = 414;
   short Not_Recevied_Datagram = 415;
   short Database_Table_Not_Found = 416;
   short Suspended_Change = 417;
   short Out_Of_Context_Session = 418;
   short User_Disabled = 419;
   short Retry = 420;
   short Late_Session = 421;
}
