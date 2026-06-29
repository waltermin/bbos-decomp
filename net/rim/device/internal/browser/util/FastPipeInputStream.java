package net.rim.device.internal.browser.util;

public class FastPipeInputStream extends PipeInputStream {
   FastPipeInputStream(Pipe pipe) {
      super(pipe);
   }

   FastPipeInputStream(Pipe pipe, int packet, int offset, int length) {
      super(pipe, packet, offset, length);
   }

   @Override
   public int read() {
      return super._pipe.fastRead(super._context);
   }
}
