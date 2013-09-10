

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import nodamushi.jfx.scene.dialog.DialogFactory;
import nodamushi.jfx.scene.dialog.NDialog;

//ダイアログのテスト
public class Test extends Application{

    public static void main(String[] args) throws InterruptedException{
        Thread t = new Thread(new Runnable(){
            
            @Override
            public void run(){
                try {
                    //FXスレッドが起動するまで適当に待機
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("JavaFXスレッド以外から起動してみます");
                NDialog d=TestCont.createDialog();
                try {
                    DialogFactory.showWindowDialogAndWait(d, d, (Window)null);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(d.getResult());
                
            }
        });
        t.start();
        launch(args);
        
    }
    
    
    ObservableList<Node> n;
    @Override
    public void start(Stage stage) throws Exception{
//        StackPane st = new StackPane();
        Parent p = FXMLLoader.load(Test.class.getResource("test.fxml"));
//        st.getChildren().add(p);
        Scene s = new Scene(p);
        stage.setScene(s);
        stage.show();
        
    }
    
}
