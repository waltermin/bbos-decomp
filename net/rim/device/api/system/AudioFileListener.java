package net.rim.device.api.system;

public interface AudioFileListener extends AudioListener {
   int OPERATION_PLAY = 0;
   int OPERATION_RECORD = 1;
   int OPERATION_STOP = 2;

   void audioFileOperationComplete(int var1);

   void audioFileOperationFailed(int var1, int var2);
}
