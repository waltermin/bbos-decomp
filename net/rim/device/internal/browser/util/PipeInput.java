package net.rim.device.internal.browser.util;

public interface PipeInput {
   int readByteArray(PipePtr var1, int var2);

   int readCompressedInt();

   String readInlineString(String var1);

   void skipInlineString();

   Pipe getPipe();

   PipeContext getPosition();
}
