package nodamushi.jfx.scene.dialog;
import java.util.Collection;

import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import nodamushi.jfx.bean.ReadOnlyObjectPropertyFactory;


public class NDialog extends Control implements DialogModel{
   
    public NDialog(){
        getStyleClass().add("ndialog");
        setResult(Result.CANCELLED);
        stageStyleProperty.set(StageStyle.UTILITY);
        setYesText("Yes");
        setNoText("No");
        setCancelText("Cancel");
        
        setSkinClassName(NDialogSkin.class.getName());
        
    }
    
    
    private DialogCloseFunction func;
    @Override
    public void setDialogCloseFunction(DialogCloseFunction func){
        if(this.func!=null)throw new RuntimeException(
                "setDialogCloseFunction:closeDialogが呼ばれる前に再設定は出来ません");
        this.func = func;
    }

    @Override
    public void closeDialog(){
        if(func!=null){
            func.closeDialog();
            func=null;
        }
    }

    public void cancel(){
        if(isDisableCancel())return;
        setResult(Result.CANCELLED);
        closeDialog();
    }
    
    public void yes(){
        if(isDisableYes())return;
        setResult(Result.YES);
        closeDialog();
    }
    
    public void no(){
        if(isDisableNo())return;
        setResult(Result.NO);
        closeDialog();
    }
    
    //-----------------------------------------------
    //                  property
    //-----------------------------------------------
    
    
    private StringProperty messageProperty = new SimpleStringProperty(this, "message");
    public String getMessage(){return messageProperty.get();}
    public void setMessage(String str){messageProperty.set(str);}
    public StringProperty messageProperty(){return messageProperty;}
    
    private StringProperty titleProperty = new SimpleStringProperty(this, "title");
    public String getTitle(){return titleProperty.get();}
    public void setTitle(String str){titleProperty.set(str);}
    public StringProperty titleProperty(){return titleProperty;}
    
    private BooleanProperty disableCancelProperty=new SimpleBooleanProperty(this,"disableCancel");
    public boolean isDisableCancel(){return disableCancelProperty.get();}
    public void setDisableCancel(boolean b){disableCancelProperty.set(b);}
    public BooleanProperty disableCancelProperty(){return disableCancelProperty;}
    
    private BooleanProperty disableYesProperty=new SimpleBooleanProperty(this,"disableYes");
    public boolean isDisableYes(){return disableYesProperty.get();}
    public void setDisableYes(boolean b){disableYesProperty.set(b);}
    public BooleanProperty disableYesProperty(){return disableYesProperty;}
    
    private BooleanProperty disableNoProperty=new SimpleBooleanProperty(this,"disableNo");
    public boolean isDisableNo(){return disableNoProperty.get();}
    public void setDisableNo(boolean b){disableNoProperty.set(b);}
    public BooleanProperty disableNoProperty(){return disableNoProperty;}
    
    private StringProperty cancelname = new SimpleStringProperty(this,"cancelText");
    public String getCancelText(){return cancelname.get();}
    public void setCancelText(String name){cancelname.set(name);}
    public StringProperty cancelTextProperty(){return cancelname;}
    
    private StringProperty yesname = new SimpleStringProperty(this,"yesText");
    public String getYesText(){return yesname.get();}
    public void setYesText(String name){yesname.set(name);}
    public StringProperty yesTextProperty(){return yesname;}
    
    private StringProperty noname = new SimpleStringProperty(this,"noText");
    public String getNoText(){return noname.get();}
    public void setNoText(String name){noname.set(name);}
    public StringProperty noTextProperty(){return noname;}
    
    
    //------for dialog model---------
    private StringProperty windowTitleProperty = new SimpleStringProperty(this, "windowTitle");
    public String getDialogTitle(){return windowTitleProperty.get();}
    public void setDialogTitle(String str){windowTitleProperty.set(str);}
    @Override public StringProperty dialogTitleProperty(){return windowTitleProperty;}
    
    private ObjectProperty<Image> imageProperty=new SimpleObjectProperty<Image>(this,"image");
    public Image getImage(){return imageProperty.get();}
    public void setImage(Image img){imageProperty.set(img);}
    public ObjectProperty<Image> imageProperty(){return imageProperty;}
    
    private BooleanProperty resizableProperty=new SimpleBooleanProperty(this,"resizable");
    public boolean isDialogResizable(){return resizableProperty.get();}
    public void setDialogResizable(boolean b){resizableProperty.set(b);}
    @Override public BooleanProperty dialogResizableProperty(){return resizableProperty;}

    private ReadOnlyObjectPropertyFactory<Result> resultFactory =
            new ReadOnlyObjectPropertyFactory<>(this, "result");
    private ReadOnlyObjectProperty<Result> resultProperty = resultFactory.get();
    public ReadOnlyObjectProperty<Result> resultProperty(){return resultProperty;}
    public Result getResult(){return resultProperty.get();}
    private void setResult(Result value){resultFactory.setValue(value);}
    
    private ObjectProperty<Modality> modalityProperty = new SimpleObjectProperty<>(this, "modality");
    @Override public Modality getDialogModality(){return modalityProperty.get();}
    public void setDialogModality(Modality m){modalityProperty.set(m);}
    public ObjectProperty<Modality> dialogModalityProperty(){return modalityProperty;}
    
    private ObjectProperty<Collection<? extends Image>> iconProperty =
            new SimpleObjectProperty<Collection<? extends Image>>(this,"icon");
    @Override public Collection<? extends Image> getIcons(){return iconProperty.get();}
    public void setIcons(Collection<Image> icon){iconProperty.set(icon);}
    public ObjectProperty<Collection<? extends Image>> iconsProperty(){return iconProperty;}
    

    private ObjectProperty<StageStyle> stageStyleProperty =
            new SimpleObjectProperty<>(this,"stageStyle");
    @Override public StageStyle getStageStyle(){return stageStyleProperty.get();}
    public void setStageStyle(StageStyle style){stageStyleProperty.set(style);}
    public ObjectProperty<StageStyle> stageStyleProperty(){return stageStyleProperty;}
    


}




