package com.recording.manager.util;

import org.junit.jupiter.api.Test;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class AudioUtilsTest {

    @Test
    public void testGetDuration() throws Exception {
        // Create a temporary WAV file for testing
        File wavFile = createTestWavFile("temp_test.wav", 5); // 5 seconds
        File mp3File = new File("src/test/resources/test.mp3");

        System.out.println("Testing AudioUtils...");

        try {
            if (wavFile.exists()) {
                System.out.println("Testing generated WAV file: " + wavFile.getAbsolutePath());
                int duration = AudioUtils.getDuration(wavFile);
                System.out.println("WAV Duration: " + duration + " seconds");
                
                // Allow small margin of error or exact match
                assertEquals(5, duration, "WAV duration should be 5 seconds");
                System.out.println("Formatted WAV: " + AudioUtils.formatDuration(duration));
            }

            if (mp3File.exists()) {
                System.out.println("Testing MP3 file: " + mp3File.getAbsolutePath());
                int duration = AudioUtils.getDuration(mp3File);
                System.out.println("MP3 Duration: " + duration + " seconds");
                assertTrue(duration > 0, "MP3 duration should be positive");
                System.out.println("Formatted MP3: " + AudioUtils.formatDuration(duration));
                
                // Test native MP3 support specifically
                System.out.println("Testing native MP3 support (MP3SPI)...");
                int nativeDuration = AudioUtils.getNativeDuration(mp3File);
                System.out.println("Native MP3 Duration: " + nativeDuration + " seconds");
                if (nativeDuration > 0) {
                     System.out.println("MP3SPI works! Native duration: " + nativeDuration);
                } else {
                     System.out.println("MP3SPI failed or file invalid for native parser.");
                }
            } else {
                 System.out.println("Warning: test.mp3 not found, skipping MP3 test.");
            }
        } finally {
            // Clean up
            if (wavFile.exists()) {
                wavFile.delete();
            }
        }
    }

    private File createTestWavFile(String filename, int durationSeconds) throws Exception {
        float sampleRate = 44100;
        int channels = 2;
        int sampleSizeInBits = 16;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, true, true);
        
        long numFrames = (long)(durationSeconds * sampleRate);
        // Calculate buffer size: frames * frameSize
        // frameSize = channels * (sampleSizeInBits/8)
        int frameSize = channels * (sampleSizeInBits / 8);
        byte[] data = new byte[(int)(numFrames * frameSize)];
        
        File file = new File(filename);
        try (AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, numFrames)) {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
        }
        return file;
    }
}
