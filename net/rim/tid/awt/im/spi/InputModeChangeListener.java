package net.rim.tid.awt.im.spi;

public interface InputModeChangeListener {
   int LISTENER_ALREADY_SET = 1;
   int NOTIFIER_IS_NOT_INITIALIZED = 2;
   int NOT_SUPPORTED = 3;

   int inputModeChanged(int var1);
}
