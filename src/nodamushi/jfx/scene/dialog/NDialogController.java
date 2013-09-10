package nodamushi.jfx.scene.dialog;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class NDialogController implements Initializable{

    @FXML
    public Label messageLabel,titleLabel;
    @FXML
    public Button cancelButton,noButton,yesButton;
    @FXML
    public ImageView imageView;
    
    
    private NDialog ndialog;
    public NDialogController(NDialog d){
        ndialog=d;
    }
    
    @Override
    public void initialize(URL location ,ResourceBundle resources){
        messageLabel.textProperty().bind(ndialog.messageProperty());
        titleLabel.textProperty().bind(ndialog.titleProperty());
        cancelButton.textProperty().bind(ndialog.cancelTextProperty());
        cancelButton.disableProperty().bind(ndialog.disableCancelProperty());
        
        yesButton.textProperty().bind(ndialog.yesTextProperty());
        yesButton.disableProperty().bind(ndialog.disableYesProperty());
        
        noButton.textProperty().bind(ndialog.noTextProperty());
        noButton.disableProperty().bind(ndialog.disableNoProperty());
        
        imageView.imageProperty().bind(ndialog.imageProperty());
        final ObjectProperty<Image> imageProp = ndialog.imageProperty();
        DoubleBinding imageWidth= new DoubleBinding(){
            {
                bind(imageProp);
            }
            @Override
            protected double computeValue(){
                Image img = imageProp.get();
                if(img==null)return 0;
                return img.getWidth();
            }
        };
        
        DoubleBinding imageHeight= new DoubleBinding(){
            {
                bind(imageProp);
            }
            @Override
            protected double computeValue(){
                Image img = imageProp.get();
                if(img==null)return 0;
                return img.getHeight();
            }
        };
        
        imageView.fitWidthProperty().bind(imageWidth);
        imageView.fitHeightProperty().bind(imageHeight);
        cancelButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event){
                ndialog.cancel();
            }
        });
        noButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                ndialog.no();
            }
        });
        yesButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                ndialog.yes();
            }
        });
    }


}
