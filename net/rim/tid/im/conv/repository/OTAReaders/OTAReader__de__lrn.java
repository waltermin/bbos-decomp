package net.rim.tid.im.conv.repository.OTAReaders;

public class OTAReader__de__lrn extends FastEuropeanWordLearningOTAReader {
   @Override
   public void init(byte type) {
      super.init(type);
      super._header.setLocaleStr("de");
   }
}
