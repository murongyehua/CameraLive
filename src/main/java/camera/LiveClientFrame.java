package camera;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class LiveClientFrame extends CanvasFrame {
    private OpenCVFrameGrabber grabber;
    private FFmpegFrameRecorder recorder;
    private final Map<String, String> videoOption;

    public LiveClientFrame(String title) {
        super(title);
        // 设置程序icon
        this.setIconImage(new ImageIcon(LiveConfigFrame.class.getResource("q2.png")).getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(false);

        this.videoOption = new HashMap<>();
        // 降低延迟
        this.videoOption.put("tune", "zerolatency");
        /**
         * 权衡quality(视频质量)和encode speed(编码速度) values(值)： *
         * ultrafast(终极快),superfast(超级快), veryfast(非常快), faster(很快), fast(快), *
         * medium(中等), slow(慢), slower(很慢), veryslow(非常慢) *
         * ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；而veryslow(非常慢)提供最佳的压缩（高编码器CPU）的同时降低视频流的大小
         */
        this.videoOption.put("preset", "ultrafast");
        // 画面质量参数，0~51，建议18~28
        this.videoOption.put("crf", "25");
    }

    public void play() {
        grabber = new OpenCVFrameGrabber(0);
        try {
            // 开始抓取摄像头画面
            grabber.start();
            while (true) {
                // 如果当前窗口已关闭，则停止抓取，释放资源
                if (!this.isDisplayable()) {
                    grabber.stop();
                    System.exit(-1);
                }
                // 获取一帧画面
                Frame frame = grabber.grab();
                // 在当前窗体显示
                this.showImage(frame);
                // 获取推送视频流的地址
                String putPath = String.format("rtmp://%s:%s/live/stream", CommonConfig.putHost,
                        CommonConfig.putPort);
                // 开始推送视频流
                this.putStream(putPath, frame);
                Thread.sleep(10);
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "直播推流出现异常", "提醒", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void putStream(String rtmpUrl, Frame frame) throws FrameRecorder.Exception {
        if (frame == null) {
            return;
        }
        // 只有第一次调用的时候进行初始化设置
        if (recorder == null) {
            // 帧率
            double framerate;
            if (grabber.getFrameRate() > 0 && grabber.getFrameRate() < 100) {
                framerate = grabber.getFrameRate();
            } else {
                framerate = 25.0;
            }
            recorder = new FFmpegFrameRecorder(rtmpUrl, grabber.getImageWidth(), grabber.getImageHeight(), 0);
            recorder.setInterleaved(true);
            recorder.setVideoOptions(this.videoOption);
            // 设置比特率
            // 比特率
            int bitrate = 2500000;
            recorder.setVideoBitrate(bitrate);
            // h264编/解码器
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            // 封装flv格式
            recorder.setFormat("flv");
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            // 视频帧率
            recorder.setFrameRate(framerate);
            // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍
            recorder.setGopSize((int) framerate * 2);
            // 开始推流
            recorder.start();
        }
        // 推送一帧画面
        recorder.record(frame);
    }

}
