package net.rim.tid.im;

public interface ISupplementaryInputData {
   StringBuffer getOriginatingKeys();

   int getOriginatingKeysCommittedCount();

   String[] getSupplementarySearchReadings();

   StringBuffer getAlternativeIdeographicReading();

   int getAlternativeReadingCommittedCharacterCount();

   StringBuffer getOriginalReading();

   int getOriginalReadingCommitedCharacterCount();
}
