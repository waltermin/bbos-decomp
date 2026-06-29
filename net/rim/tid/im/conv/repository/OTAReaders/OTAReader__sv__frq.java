package net.rim.tid.im.conv.repository.OTAReaders;

public class OTAReader__sv__frq extends FastEuropeanWordLearningOTAReader {
   @Override
   public void init(byte type) {
      super.init(type);
      super._header.setLocaleStr("sv");
   }
}
