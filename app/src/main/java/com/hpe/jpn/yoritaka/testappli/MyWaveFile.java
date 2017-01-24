package com.hpe.jpn.yoritaka.testappli;

import android.app.Application;
import android.os.Environment;

import com.hpe.jpn.yoritaka.testappli.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by YoritakaK on 1/18/2017.
 */

public class MyWaveFile {
    private final int FILESIZE_SEEK = 4;
    private final int DATASIZE_SEEK = 40;
    private final int SAMPLING_RATE = 44100;

    private RandomAccessFile raf; //Because of real time processing, use RnadomAccessFile
    private File recFile; // After recording, for writing and reading.
    private String fileName = "testWavFile.wav";
    private byte[] RIFF = {'R', 'I', 'F', 'F'};
    private int fileSize = 36;
    private byte[] WAVE = {'W', 'A', 'V', 'E'};
    private byte[] fmt = {'f', 'm', 't'};
    private int fmtSize = 16;
    private byte[] fmtID = {1, 0};
    private short chCount = 1;
    private int bytePerSec = SAMPLING_RATE * (fmtSize / 8) * chCount;
    private short blockSize = (short) ((fmtSize / 8) * chCount);
    private short bitPerSample = 16;
    private byte[] data = {'d', 'a', 't', 'a'};
    private int dataSize = 0;

    public void createFile(String fName) {
        File externalStoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        Logger.i("External storage path = " + externalStoragePath.toString());

        fileName = fName;
        recFile = new File(fileName);

        if (recFile.exists()) {
            recFile.delete();
        }
        try {
            recFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            raf = new RandomAccessFile(recFile, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            raf.seek(0);
            raf.write(RIFF);
            raf.write(littleEndianInteger(fileSize));
            raf.write(WAVE);
            raf.write(fmt);
            raf.write(littleEndianInteger(fmtSize));
            raf.write(fmtID);
            raf.write(littleEndianShort(chCount));
            raf.write(littleEndianInteger(SAMPLING_RATE));
            raf.write(littleEndianInteger(bytePerSec));
            raf.write(littleEndianShort(blockSize));
            raf.write(littleEndianShort(bitPerSample));
            raf.write(data);
            raf.write(littleEndianInteger(dataSize));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private byte[] littleEndianInteger(int i) {
        byte[] buffer = new byte[4];
        buffer[0] = (byte) i;
        buffer[1] = (byte)(i >> 8);
        buffer[2] = (byte)(i >> 16);
        buffer[3] = (byte)(i >> 24);
        return buffer;
    }

    public void addBigEndianData(short[] shortData) {
        try {
            raf.seek(raf.length());
            raf.write(littleEndianShorts(shortData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateFileSize() {
        fileSize = (int) (recFile.length() - 8);
        byte[] fileSizeBytes = littleEndianInteger(fileSize);
        try {
            raf.seek(FILESIZE_SEEK);
            raf.write(fileSizeBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDataSize() {
        dataSize = (int) (recFile.length() - 44);
        byte[] dataSizeBytes = littleEndianInteger(dataSize);
        try {
            raf.seek(DATASIZE_SEEK);
            raf.write(dataSizeBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] littleEndianShort(short s) {
        byte[] buffer = new byte[2];
        buffer[0] = (byte) s;
        buffer[1] = (byte) (s >> 8);

        return buffer;
    }

    private byte[] littleEndianShorts(short[] s) {
        byte[] buffer = new byte[s.length * 2];
        int i;
        for (i=0; i < s.length; i++) {
            buffer[2*i] = (byte) s[i];
            buffer[2*i+1] = (byte) (s[i] >> 8);
        }
        return buffer;
    }

    public void close() {
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
