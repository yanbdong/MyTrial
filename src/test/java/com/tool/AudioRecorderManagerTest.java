package com.tool;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yanbdong@cienet.com.cn
 * @since Nov 26, 2020
 */
class AudioRecorderManagerTest {


    @Test
    public void testRecordAudio() throws FunctionException, InterruptedException {
        AudioRecorderManager instance = AudioRecorderManager.getInstance();
        instance.startSession("a", new AudioRecorderManager.AudioRecordConfig().setOutputFolder(new File("audio1")).setSampleRate(0F).recordOne());
        instance.startSession("b", new AudioRecorderManager.AudioRecordConfig().setOutputFolder(new File("audio2")).setSampleRate(0F).recordOne());
        Thread.sleep(10000);
        instance.finishSession("a");
        instance.finishSession("b");
    }

    @Test
    void startSession() {
    }
}