package net.rim.device.api.system;

public interface AudioFileListener extends AudioListener {
   int OPERATION_PLAY;
   int OPERATION_RECORD;
   int OPERATION_STOP;

   void audioFileOperationComplete(int var1);

   void audioFileOperationFailed(int var1, int var2);
}
