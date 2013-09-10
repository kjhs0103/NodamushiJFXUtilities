import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import nodamushi.jfx.scene.dialog.DialogFactory;
import nodamushi.jfx.scene.dialog.NDialog;


public class TestCont{
    
    @FXML
    public StackPane stackpane;
    
    public static NDialog createDialog(){
        NDialog d = new NDialog();
        d.setDialogModality(Modality.APPLICATION_MODAL);
        
        //↓デフォルトでは表示されません
        d.setDialogTitle("どうあがいても店長ルート");
        
        d.setTitle("選択してください");
        d.setMessage("まきますか？まきませんか？");
        
        d.setYesText("まきます");
        d.setNoText("まきません");
        d.setCancelText("知りません");
        d.setDisableYes(true);
        return d;
    }
    
    @FXML
    public void showWindowDialog(ActionEvent e){
        NDialog d = createDialog();
        try {
            DialogFactory.showWindowDialogAndWait(
                    d,d,(Button)e.getSource());
        }catch (InterruptedException ie) {
            // javafxスレッドから呼ぶ限りは発生しません。
        }
        System.out.println(d.getResult());
    }
    
    @FXML
    public void showInnerDialog(ActionEvent e){
        NDialog d = createDialog();
        try {
            DialogFactory.showInnerDialogAndWait(d, d, stackpane);
        }catch (InterruptedException e1) {
            // javafxスレッドから呼ぶ限りは発生しません
        }        
        System.out.println(d.getResult());
    }
    
}
