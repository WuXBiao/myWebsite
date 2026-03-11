package com.recording.manager.util;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

/**
 * 音频工具类
 * 用于获取 WAV 和 MP3 文件的时长等信息
 */
public class AudioUtils {

    private static final Logger logger = LoggerFactory.getLogger(AudioUtils.class);

    /**
     * 获取音频文件时长（秒）
     * 优先使用 jaudiotagger 解析，如果失败则尝试使用 Java 原生 API (支持 WAV, 需 MP3SPI 支持 MP3)
     *
     * @param file 音频文件
     * @return 时长（秒），如果获取失败返回 0
     */
    public static int getDuration(File file) {
        if (file == null || !file.exists()) {
            logger.error("文件不存在: {}", file);
            return 0;
        }

        // 尝试使用 jaudiotagger 解析 (支持 mp3, wav 等多种格式)
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            AudioHeader audioHeader = audioFile.getAudioHeader();
            return audioHeader.getTrackLength();
        } catch (Exception e) {
            logger.warn("Jaudiotagger 解析失败: {}, 尝试原生方法...", e.getMessage());
        }

        // 如果 jaudiotagger 失败，尝试使用 javax.sound.sampled
        // 只要文件存在，我们就可以尝试使用原生 API 解析，不再限制为 .wav 后缀
        // 这样如果添加了 MP3SPI，也可以解析 MP3
        return getNativeDuration(file);
    }

    /**
     * 使用 Java 原生 javax.sound.sampled API 获取音频时长
     * 支持 WAV，如果引入了 SPI (如 MP3SPI) 也支持 MP3
     *
     * @param file 音频文件
     * @return 时长（秒）
     */
    public static int getNativeDuration(File file) {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            
            // 如果无法直接获取帧数，有些格式可能返回 -1
            if (frames == AudioSystem.NOT_SPECIFIED) {
                // 对于 MP3SPI，时长信息通常在属性中
                Long durationMicroseconds = (Long) format.properties().get("duration");
                if (durationMicroseconds != null) {
                    return (int) (durationMicroseconds / 1000000);
                }
                logger.warn("无法直接获取帧数且无 duration 属性: {}", file.getName());
                return 0;
            }

            double durationInSeconds = (frames + 0.0) / format.getFrameRate();
            return (int) Math.round(durationInSeconds);
        } catch (Exception e) {
            logger.error("原生解析失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 格式化时长为 MM:ss 格式
     *
     * @param seconds 总秒数
     * @return 格式化后的时间字符串
     */
    public static String formatDuration(int seconds) {
        if (seconds < 0) {
            return "00:00";
        }
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
    
    /**
     * 格式化时长为 HH:mm:ss 格式
     * 
     * @param seconds 总秒数
     * @return 格式化后的时间字符串
     */
    public static String formatDurationToHHMMSS(int seconds) {
        if (seconds < 0) {
            return "00:00:00";
        }
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}
