package nodamushi.jfx.scene.dialog;

/**
 * DialogContainerに渡される、ダイアログを閉じる操作を定義したインターフェースです。<br>
 * ダイアログはStageで表現されるタイプであろうと、
 * モダンブラウザでalertがウィンドウ内に表示されるタイプのダイアログでも
 * 、クローズ作業においてその違いを意識しない為のインターフェースです。<br><br>
 * 一度closeDialog()を呼び出したオブジェクトを使い回すことは出来ません。
 * @author nodamushi
 *
 */
public interface DialogCloseFunction{
    /**
     * ダイアログを閉じます。<br>
     * このメソッドを呼び出した後はこのオブジェクトは動作をしません。<br>
     * 必ずJavaFXのスレッドから呼び出してください。
     */
    public void closeDialog();
}
