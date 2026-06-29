package net.rim.tid.im.conv.repository.OTAReaders;

public class OTAReader__tr__frq extends FastEuropeanWordLearningOTAReader {
   @Override
   public void init(byte type) {
      super.init(type);
      super._header.setLocaleStr("tr");
   }
}
