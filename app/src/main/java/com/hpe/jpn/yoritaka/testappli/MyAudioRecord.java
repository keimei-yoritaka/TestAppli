package com.hpe.jpn.yoritaka.testappli;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.hpe.jpn.yoritaka.testappli.util.Logger;

/**
 * Created by YoritakaK on 1/18/2017.
 */

public class MyAudioRecord {
    AudioRecord audioRecord;
    final static int SAMPLING_RATE = 44100;
    final String FILE_NAME = "/data/user/0/com.hpe.jpn.yoritaka.testappli/files/testAudioRecord.wav";
    private int bufSize;
    private short[] shortData;
    private MyWaveFile myWaveFile = new MyWaveFile();
    private boolean recordingFlag = false;

    public void initAudioRecord() {
        Logger.i("Initializing myAudioRecord.");
        myWaveFile.createFile(FILE_NAME);
        bufSize = android.media.AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        //audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_CALL, SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufSize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufSize);

        shortData = new short[bufSize / 2];

        audioRecord.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
            @Override
            public void onPeriodicNotification(AudioRecord recorder) {
                audioRecord.read(shortData, 0, bufSize / 2);
                myWaveFile.addBigEndianData(shortData);
            }

            @Override
            public void onMarkerReached(AudioRecord recorder) {
                //TODO
            }
        });

        audioRecord.setPositionNotificationPeriod(bufSize / 2);
    }





    public void startAudioRecord() {
        audioRecord.startRecording();
        audioRecord.read(shortData, 0, bufSize/2);
        recordingFlag = true;
    }

    public void stopAudioRecord() {
        audioRecord.stop();
        recordingFlag = false;
    }

    public boolean isRecording() {
        return recordingFlag;
    }
}
