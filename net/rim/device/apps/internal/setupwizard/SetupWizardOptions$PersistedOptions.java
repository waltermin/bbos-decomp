package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.util.IntHashtable;
import net.rim.vm.Persistable;

final class SetupWizardOptions$PersistedOptions implements Persistable {
   boolean _offerSetupWizard = true;
   boolean _loggingEnabled = false;
   boolean _wizardCompleted = false;
   IntHashtable _wizardPageOptions = new IntHashtable();
}
