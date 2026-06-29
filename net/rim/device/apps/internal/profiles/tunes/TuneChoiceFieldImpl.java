package net.rim.device.apps.internal.profiles.tunes;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UserInputEventListener;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.file.FileSelector;
import net.rim.device.apps.api.framework.profiles.TuneChoiceField;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.internal.profiles.AlertConsequence;
import net.rim.device.apps.internal.profiles.AlertEngine;
import net.rim.device.apps.internal.profiles.Profiles;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.system.AlertPlayer;

public final class TuneChoiceFieldImpl extends TuneChoiceField implements FieldChangeListener, UserInputEventListener {
   private String[] _sortedTuneNames;
   private boolean _addMute;
   private AlertEngine _alertEngine;
   private int _volume = AlertConsequence.getLowVolume(0);
   private int _outputDevice = 0;
   private String _defaultTuneName;
   static final int SKIP_BROWSE = Integer.MAX_VALUE;
   private static String _muteTuneDisplayName;
   private static String _selectTuneDisplayName;
   private static String _selectedTune;
   private static int SELECT_TUNE_INDEX = 0;
   private static final int MUTE_INDEX = 1;

   public TuneChoiceFieldImpl(String label, String selectedTuneName, String defaultTuneName, boolean addMute) {
      this._defaultTuneName = defaultTuneName;
      this._addMute = addMute;
      this.setLabel(label);
      if (!TuneManager.isTuneManagerAvailable()) {
         throw new IllegalStateException();
      }

      this._alertEngine = AlertEngine.getInstance();
      Ui.getUiEngine().addUserInputEventListener(this);
      this.generateList(selectedTuneName);
   }

   private final void generateList(String selectedTuneName) {
      TuneManager tuneManager = TuneManager.getTuneManager();
      String[] allTuneNames = tuneManager.getAllTuneFileNames();
      int numTunes = allTuneNames.length;
      numTunes++;
      if (this._addMute) {
         numTunes++;
      }

      this._sortedTuneNames = new String[numTunes];
      this._sortedTuneNames[SELECT_TUNE_INDEX] = _selectTuneDisplayName;
      if (this._addMute) {
         this._sortedTuneNames[1] = _muteTuneDisplayName;
      }

      System.arraycopy(allTuneNames, 0, this._sortedTuneNames, this._addMute ? 2 : SELECT_TUNE_INDEX + 1, allTuneNames.length);
      this.setChoices(this._sortedTuneNames);
      int selectedTuneIndex = -1;
      if (selectedTuneName == null) {
         String[] defaultTuneNames = Profiles.getDefaultTuneNames(2868625504212929964L);
         selectedTuneName = defaultTuneNames[0];
      }

      selectedTuneIndex = tuneManager.getIndex(this._sortedTuneNames, selectedTuneName);
      if (selectedTuneIndex == -1) {
         label80:
         try {
            FileConnection fconn = (FileConnection)Connector.open(FileUtilities.makeFileURL(selectedTuneName));
            if (fconn.exists()) {
               selectedTuneIndex = this._addMute ? 2 : SELECT_TUNE_INDEX + 1;
               Arrays.insertAt(this._sortedTuneNames, selectedTuneName, selectedTuneIndex);
               this.setChoices(this._sortedTuneNames);
            }
         } finally {
            break label80;
         }
      }

      if ((selectedTuneIndex == -1 || selectedTuneIndex >= this._sortedTuneNames.length) && this._defaultTuneName != null) {
         selectedTuneIndex = tuneManager.getIndex(this._sortedTuneNames, this._defaultTuneName);
      }

      if (selectedTuneIndex == -1 || selectedTuneIndex >= this._sortedTuneNames.length) {
         selectedTuneIndex = 0;
      }

      this.setSelectedIndex(selectedTuneIndex, Integer.MAX_VALUE);
      this.setChangeListener(this);
   }

   @Override
   public final Object getChoice(int index) {
      if (this._addMute && index == 1) {
         return _muteTuneDisplayName;
      }

      String choice = (String)super.getChoice(index);
      return index == 0 ? choice : TuneManager.getTuneManager().getDisplayName(choice);
   }

   @Override
   protected final void setSelectedIndex(int index, int context) {
      if (index == SELECT_TUNE_INDEX && context != Integer.MAX_VALUE) {
         FileSelector fileSelector = new FileSelector(null, 2);
         String selectedTune = fileSelector.selectFile(null);
         if (selectedTune != null) {
            index = TuneManager.getTuneManager().getIndex(this._sortedTuneNames, selectedTune);
            if (index == -1) {
               index = this._addMute ? 2 : SELECT_TUNE_INDEX + 1;
               Arrays.insertAt(this._sortedTuneNames, selectedTune, index);
            }

            _selectedTune = selectedTune;
            this.setChoices(this._sortedTuneNames);
            super.setSelectedIndex(index, context);
            return;
         }
      } else {
         super.setSelectedIndex(index, context == Integer.MAX_VALUE ? Integer.MIN_VALUE : context);
      }
   }

   @Override
   protected final void setSize(int size) {
      int originalValue = SELECT_TUNE_INDEX;
      SELECT_TUNE_INDEX = Integer.MAX_VALUE;
      super.setSize(size);
      SELECT_TUNE_INDEX = originalValue;
   }

   @Override
   public final String getSelectedTuneName() {
      int index = this.getSelectedIndex();
      if (this._addMute && index == 1) {
         return TuneChoiceField.MUTE_TUNE_NAME;
      } else {
         return index == SELECT_TUNE_INDEX ? _selectedTune : (String)super.getChoice(index);
      }
   }

   @Override
   public final void fieldChanged(Field aField, int context) {
      ChoiceField fieldWithChanges = (ChoiceField)aField;
      long sourceId = 0;
      int alertType = 1;
      int tuneIndex = fieldWithChanges.getSelectedIndex();
      if (!this._addMute || tuneIndex != 1) {
         if (tuneIndex != SELECT_TUNE_INDEX) {
            int beeps = 1;
            String tuneName = this._sortedTuneNames[tuneIndex];
            int vibrates = 1;
            if ((context & 2) == 0) {
               AlertPlayer tune = TuneManager.getTuneManager().getTune(tuneName);
               if (tune != null) {
                  this._alertEngine.startNewAlertLater(alertType, tune, beeps, vibrates, this._volume, this._volume, sourceId, -1, -1, -1, -1);
               }
            }
         }
      }
   }

   @Override
   public final void setVolumeIndex(int volumeIndex) {
      this.setVolumeIndex(volumeIndex, this._outputDevice);
   }

   @Override
   public final void setVolumeIndex(int volumeIndex, int device) {
      int[] volumes = AlertConsequence.getVolumeLevels(device);
      int index = MathUtilities.clamp(1, volumeIndex, volumes.length - 1);
      this._volume = volumes[index];
      this._outputDevice = device;
   }

   @Override
   public final void onUserInput(int device, int flags) {
      this._alertEngine.cancelPendingEvents();
      if (this._alertEngine.isAlertInProgress()) {
         this._alertEngine.stopAlert(0);
      }
   }

   @Override
   public final void onUndisplay() {
      this._alertEngine.closePlayer();
      Ui.getUiEngine().removeUserInputEventListener(this);
      super.onUndisplay();
   }

   static {
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      _muteTuneDisplayName = resources.getString(245);
      _selectTuneDisplayName = resources.getString(253);
   }
}
