package camera;

/**
 * 简易直播
 */
public class Live {

    public static boolean isAction;

    public static void main(String[] args) throws Exception {
        LiveClientFrame liveClientFrame = new LiveClientFrame("直播推流中...");
        // 加载配置窗口
        LiveConfigFrame configFrame = new LiveConfigFrame("配置");
        // 打开配置窗口
        configFrame.setVisible(true);

        while (!isAction) {
            Thread.sleep(1000);
        }
        liveClientFrame.setVisible(true);
        liveClientFrame.setAlwaysOnTop(true);
        liveClientFrame.play();
    }

}
