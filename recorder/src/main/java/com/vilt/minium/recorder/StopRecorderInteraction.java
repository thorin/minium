package com.vilt.minium.recorder;

import java.io.IOException;

import org.monte.screenrecorder.ScreenRecorder.State;

import com.vilt.minium.recorder.impl.MiniumScreenRecorder;

public class StopRecorderInteraction extends RecorderInteraction {

	public StopRecorderInteraction(MiniumScreenRecorder recorder) {
		super(null, recorder);
	}
	
	@Override
	protected void doPerform() {
		try {
			if (recorder.getState() != State.RECORDING) throw new IllegalStateException("Recorder is not recording");
			recorder.stop();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
