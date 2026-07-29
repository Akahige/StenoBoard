// 29jul26abu
// (c) Software Lab. Alexander Burger

package de.software_lab.stenoboard;

import android.view.*;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.EditorInfo;

public class StenoIME extends InputMethodService {
   StenoView SV;

   @Override public View onCreateCandidatesView() {
      SV = (StenoView)getLayoutInflater().inflate(R.layout.input, null);
      SV.Ime = this;
      SV.reset(null, true);
      return SV;
   }

   @Override public void onInitializeInterface() {
      super.onInitializeInterface();
      setCandidatesViewShown(true);
   }

   @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
      super.onStartInput(attribute, restarting);
      setCandidatesViewShown(true);
   }

   @Override public void onFinishInput() {
      super.onFinishInput();
      setCandidatesViewShown(false);
   }

   @Override public boolean onEvaluateFullscreenMode() {
      return false;
   }
}
