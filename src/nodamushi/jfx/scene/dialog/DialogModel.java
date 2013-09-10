package nodamushi.jfx.scene.dialog;

import java.util.Collection;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * ダイアログの動作とダイアログのデータを保持するモデルです。
 * @author nodamushi
 *
 */
public interface DialogModel{
    
    /**
     * ダイアログを閉じる操作を実装したfuncを保持してください。<br>
     * このメソッドはDialogFactoryからダイアログが表示されるたびに呼び出されます。<br><br>
     * キャンセルボタン、OKボタン等により、ダイアログを閉じるときはこのfuncを利用してください。
     * @param func ダイアログを閉じる操作を定義したオブジェクト。
     * @see DialogCloseFunction#closeDialog()
     */
    public void setDialogCloseFunction(DialogCloseFunction func);
    /**
     * ダイアログ×ボタンを押されてがクローズする際に呼ばれます。<br>
     * setDialogCloseFunctionで設定されたfuncを用いてクローズしてください。<br>
     */
    public void closeDialog();
    
    

    /**
     * ダイアログに表示するタイトルを返します
     * @return
     */
    public ReadOnlyStringProperty dialogTitleProperty();
    /*
     * public default String getWindowTitle(){return "Dialog";}
     */
    /**
     * ダイアログをリサイズすることが出来るかどうかを返します。
     * @return trueの場合、ダイアログをリサイズできます。
     */
    public ReadOnlyBooleanProperty dialogResizableProperty();
    /*
     * public default boolean isWindowResizable(){return false;}
     */

    /**
     * ダイアログのモーダルの種類を返します。<br>
     * nullの場合はNoneと見なします。
     * @return ダイアログのモーダルの種類。
     */
    public Modality getDialogModality();
    //public default Modality getDialogModalit(){return Modality.None;}
    /**
     * ダイアログに表示するアイコンを返します。<br>
     * nullの場合は何も表示しません
     * @return
     */
    public  Collection<? extends Image> getIcons();
    //public default Collection<? extends Image>getIcons(){return null;}
    /**
     * StageStyleを返します。
     * @return
     */
    public StageStyle getStageStyle();
    //public default StageStyle getStageStyle(){return null;}
    
}
