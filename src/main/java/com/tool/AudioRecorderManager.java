package com.tool;

import com.google.common.base.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import io.reactivex.rxjava3.annotations.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 * To support multi-access
 *
 * @author yanbdong@cienet.com.cn
 * @since Nov 24, 2020
 */
public class AudioRecorderManager {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AudioRecorderManager.class);
    private static final AudioRecorderManager INSTANCE = new AudioRecorderManager();
    private static ExecutorService mExecutors = Executors.newFixedThreadPool(3);
    private final Map<String, AudioRecorderSession> mSessionCollection = new ConcurrentHashMap<>();

    public static AudioRecorderManager getInstance() {
        return INSTANCE;
    }

    /**
     * Idempotent method
     *
     * @param targetDataLine
     */
    private void stopRecord(TargetDataLine targetDataLine) {
        if (targetDataLine.isOpen()) {
            targetDataLine.stop();
            targetDataLine.close();
        }
    }

    /**
     * Start a session with given config. Use {@code key} as global handler to access across different context.
     *
     * @throws FunctionException If same key existed or thread pool is full
     */
    public void startSession(String key, AudioRecordConfig config) throws FunctionException {
        AudioRecorderSession session = createSession(config);
        AudioRecorderSession previousOne = mSessionCollection.putIfAbsent(key, session);
        if (null != previousOne) {
            log.debug("Concurrent issue happens that another AudioRecorderSession has put same alias preemptively");
            // Set the previous back.
            mSessionCollection.put(key, previousOne);
            throw new FunctionException(3905, key, key);
        }
        // Start recorder
        try {
            session.start();
        } catch (Exception e) {
            throw new FunctionException(3501, e, "Execute record audio task is rejected. Too many tasks exist: "
                    + mSessionCollection.keySet() + ". exception: " + e.getMessage());
        }
    }

    /**
     * Get session result and release session
     *
     * @param key
     * @return
     * @throws FunctionException if not exist such a key or finish fails
     */
    public RecordResult finishSession(String key) throws FunctionException {
        AudioRecorderSession session = mSessionCollection.remove(key);
        if (null == session) {
            throw new FunctionException(3904, key);
        }
        RecordResult result;
        try {
            result = session.stop();
        } catch (Exception e) {
            throw new FunctionException(e);
        }
        if (result == null) {
            throw new FunctionException(3501, "AudioRecord task (" + key + ") has not started yet");
        }
        return result;
    }

    /**
     * A safe way to release resource
     */
    public void releaseSession(String key) {
        AudioRecorderSession session = mSessionCollection.remove(key);
        if (null == session) {
            return;
        }
        try {
            session.stop();
        } catch (Exception e) {
            // ignore
            log.warn("releaseSession error", e);
        }
    }

    private AudioRecorderSession createSession(AudioRecordConfig config) throws FunctionException {
        List<Mixer.Info> infos = Arrays.asList(AudioSystem.getMixerInfo());
        log.debug("Find all mixer: {}", infos);
        AudioRecorderSession session = new AudioRecorderSession();
        for (MicroPhone microPhone : config.getRecordTargets()) {
            Mixer.Info info = findAssociatedMixer(infos, microPhone.getDeviceID());
            TargetDataLine targetDataLine;
            try {
                if (info == null) {
                    // Record default
                    targetDataLine = AudioSystem.getTargetDataLine(config.getAudioFormat());
                } else {
                    targetDataLine = AudioSystem.getTargetDataLine(config.getAudioFormat(), info);
                }
            } catch (LineUnavailableException e) {
                throw new FunctionException(e);
            }
            File recordFile = new File(config.getOutputFolder(), microPhone.getMicName() + ".wav");
            // Create task
            AudioRecorderTask task = new AudioRecorderTask().setOutputFile(recordFile)
                    .setStopRecordTask(() -> stopRecord(targetDataLine)).setStartRecordTask(() -> {
                        try {
                            targetDataLine.open();
                            targetDataLine.start();
                            AudioSystem.write(new AudioInputStream(targetDataLine), AudioFileFormat.Type.WAVE,
                                    recordFile);
                            return null;
                        } catch (Exception e) {
                            return e;
                        } finally {
                            stopRecord(targetDataLine);
                        }
                    });
            session.addRecordAudioTask(task);
        }
        return session;
    }

    /**
     * Try to find the hardware mixer what is the {@code configuredName} associated
     *
     * @param configuredName The name configured by the user in node_config
     * @return If null, default mixer should be used.
     */
    @Nullable
    private Mixer.Info findAssociatedMixer(List<Mixer.Info> infos, String configuredName) {
        String expectedName = configuredName.trim().toLowerCase();
        if (Strings.isNullOrEmpty(expectedName)) {
            log.debug("No config, use default");
            return null;
        }
        for (Mixer.Info hardwareInfo : infos) {
            // since the java cannot get the audio name in a complete form.
            // eg, the audio device name is MicroPhone (test phone AAA).
            // Java can only get the name: MicroPhone (test ph
            // so, make this substring method as a work round.
            String riName = hardwareInfo.getName().trim().toLowerCase();
            if (riName.isEmpty()) {
                continue;
            }
            if (riName.startsWith(expectedName) || expectedName.startsWith(riName)) {
                log.debug("Find mixer {} matches expected {}", hardwareInfo, configuredName);
                return hardwareInfo;
            }
        }
        log.debug("No matched mixer for {}, use default", configuredName);
        return null;
    }

    /**
     * A structure for access result
     */
    public interface IAudioRecorderInfo {

        String getMicName();

        File getOutputFile();
    }

    /**
     * A configuration for this record session. Please remember to set all the parameters otherwise NPE will throw
     */
    public static class AudioRecordConfig {

        private static final AudioFormat audioFormat_16 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000F, 16,
                1, 2, 16000F, false);
        private static final AudioFormat audioFormat_48 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000F, 16,
                1, 2, 16000F, false);
        private File mOutputFolder;
        private AudioFormat mAudioFormat;
        private List<MicroPhone> mRecordTargets;

        public File getOutputFolder() {
            return mOutputFolder;
        }

        public AudioRecordConfig setOutputFolder(File outputFolder) {
            mOutputFolder = outputFolder;
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }
            return this;
        }

        public AudioFormat getAudioFormat() {
            return mAudioFormat;
        }

        public List<MicroPhone> getRecordTargets() {
            return mRecordTargets;
        }

        /**
         * @param sampleRate Just follow the API provider by outer modules.
         */
        public AudioRecordConfig setSampleRate(Float sampleRate) {
            if (sampleRate != null && sampleRate.compareTo(48000F) == 0) {
                mAudioFormat = audioFormat_48;
            } else {
                mAudioFormat = audioFormat_16;
            }
            return this;
        }

        /**
         * Record all mixer provided in config.<br>
         * It's a new feature to provide capability to record multi-audio simultaneously.
         *
         * @return
         */
        public AudioRecordConfig recordAll() {
            return this;
        }

        /**
         * The original behavior to record only one audio.
         *
         * @return
         */
        public AudioRecordConfig recordOne() {
            // Create
            MicroPhone tmp = new MicroPhone();
            tmp.setMicName("Audio");
            tmp.setDeviceID("");
            mRecordTargets = Collections.singletonList(tmp);
            return this;
        }
    }

    public static class RecordResult {

        private List<? extends IAudioRecorderInfo> mAudioRecorderInfos;

        public List<? extends IAudioRecorderInfo> getAudioRecorderInfos() {
            return mAudioRecorderInfos;
        }

        RecordResult setAudioRecorderInfos(List<? extends IAudioRecorderInfo> audioRecorderInfos) {
            mAudioRecorderInfos = audioRecorderInfos;
            return this;
        }
    }

    /**
     * A session to manager tasks
     */
    private static class AudioRecorderSession {

        private final List<AudioRecorderTask> mRecordAudioTaskList = new ArrayList<>();
        private volatile List<Future<Exception>> mRecordAudioFutureList;

        public AudioRecorderSession addRecordAudioTask(AudioRecorderTask recordAudioTask) {
            mRecordAudioTaskList.add(recordAudioTask);
            return this;
        }

        /**
         * Start all record task simultaneously. Throw exception if can not start all.
         */
        public void start() {
            if (mRecordAudioFutureList != null) {
                // To avoid repeated calls by one thread
                return;
            }
            try {
                // An optimization to avoid lock large code segment.
                // Maybe JVM can detect this automatically
                List<Future<Exception>> tmp = mRecordAudioTaskList.stream()
                        .map(it -> mExecutors.submit(it.getStartRecordTask()))
                        .collect(Collectors.toList());
                // No race condition here as there is no such a scene. Thus we simply assign the value without lock
                mRecordAudioFutureList = tmp;
            } catch (Exception e) {
                // To call an idempotent method to release resource
                mRecordAudioTaskList.forEach(it -> it.getStopRecordTask().run());
                throw e;
            }
        }

        /**
         * @return null denotes {@linkplain #start()} is not called yet
         * @throws Exception
         */
        @Nullable
        public RecordResult stop() throws Exception {
            if (mRecordAudioFutureList == null) {
                return null;
            }
            // Stop recording
            mRecordAudioTaskList.forEach(it -> it.getStopRecordTask().run());
            // Check result
            for (Future<Exception> executeResult : mRecordAudioFutureList) {
                Exception exception = executeResult.get(100, TimeUnit.MILLISECONDS);
                if (null != exception) {
                    // Throw if anyone fails
                    mRecordAudioFutureList.forEach(it -> it.cancel(true));
                    throw exception;
                }
            }
            return new RecordResult().setAudioRecorderInfos(mRecordAudioTaskList);
        }
    }

    private static class AudioRecorderTask implements IAudioRecorderInfo {

        private String mMicName;
        private File mOutputFile;
        private Callable<Exception> mStartRecordTask;
        private Runnable mStopRecordTask;

        @Override
        public String getMicName() {
            return mMicName;
        }

        public AudioRecorderTask setMicName(String micName) {
            mMicName = micName;
            return this;
        }

        @Override
        public File getOutputFile() {
            return mOutputFile;
        }

        public AudioRecorderTask setOutputFile(File outputFile) {
            mOutputFile = outputFile;
            return this;
        }

        public Callable<Exception> getStartRecordTask() {
            return mStartRecordTask;
        }

        public AudioRecorderTask setStartRecordTask(Callable<Exception> startRecordTask) {
            mStartRecordTask = startRecordTask;
            return this;
        }

        public Runnable getStopRecordTask() {
            return mStopRecordTask;
        }

        public AudioRecorderTask setStopRecordTask(Runnable stopRecordTask) {
            mStopRecordTask = stopRecordTask;
            return this;
        }
    }

    @Getter
    @Setter
    public static class MicroPhone {

        private String micName;
        private String deviceID;

    }

}
