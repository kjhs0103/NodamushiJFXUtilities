package nodamushi.jfx.scene.dialog;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Skin;


public class NDialogSkin implements Skin<NDialog>{
    private NDialog skinnable;
    private Parent contents;
    private NDialogController controller;
    
    public NDialogSkin(NDialog nd){
        skinnable = nd;
        FXMLLoader loader = new FXMLLoader(NDialogSkin.class.getResource("ndialog.fxml"));
        loader.setController(controller=new NDialogController(nd));
        try {
            contents = (Parent)loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public NDialog getSkinnable(){
        return skinnable;
    }

    @Override
    public Node getNode(){
        return contents;
    }

    @Override
    public void dispose(){
        contents = null;
        controller = null;
    }
    
}