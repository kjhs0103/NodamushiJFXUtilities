package nodamushi.jfx.scene.dialog;

import java.util.Collection;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;
import javafx.stage.*;



public final class DialogFactory{

    private static class WindowDialogCloseFunction 
    implements DialogCloseFunction{
        private Stage stage;

        
        public WindowDialogCloseFunction(Stage stage){
            this.stage = stage;
        }
        
        @Override
        public void closeDialog(){
            if(!Platform.isFxApplicationThread())return;
            if(stage!=null){
                stage.close();
                stage = null;
            }                
        }
    }
    private static class InnerDialogCloseFunction 
    implements DialogCloseFunction{
        private StackPane parentPane;
        private FlowPane back;
        private StackPane base;
        private Node contents;
        private Object key = new Object();
        private Object obj;
        private volatile boolean wait = false;
        private boolean closed = false;
        public InnerDialogCloseFunction(
                StackPane parentPane, FlowPane back,
                StackPane base, Node contents                
                ){
            this.parentPane=parentPane;
            this.back = back;
            this.base = base;
            this.contents = contents;
        }
        void _wait(){
            wait = true;
            obj=com.sun.javafx.tk.Toolkit.getToolkit().enterNestedEventLoop(key);
        }
        
        @Override
        public void closeDialog(){
            if(!Platform.isFxApplicationThread())return;
            if(!closed){
                closed=true;
                parentPane.getChildren().remove(back);
                base.getChildren().remove(contents);
                if(wait)
                    com.sun.javafx.tk.Toolkit
                    .getToolkit().exitNestedEventLoop(key, obj);
                
                parentPane=null;
                back = null;
                base = null;
                contents=null;
                obj =key= null;
            }
        }
        
    }
    
    private static EventHandler<Event> ALL_CONSUME=new EventHandler<Event>(){
        public void handle(Event event){event.consume();}
    };
    
    
    
    /**
     * インナーウィンドウ形式のダイアログを開きます。<br>
     * parentPaneの最も上でダイアログを開き、ダイアログが閉じるまで処理を戻しません。
     * @param contents
     * @param model
     * @param parentPane
     * @throws InterruptedException 
     * このメソッドをJavaFXのスレッド以外から呼び出した時、待機中に割り込まれた場合にのみ発生します。
     * @throws NullPointerException contents,model,parentPaneがnull
     */
    public static void showInnerDialogAndWait(final Node contents,final DialogModel model,
            final StackPane parentPane) throws InterruptedException,NullPointerException{
        _showInnerDialog(contents, model, parentPane, true);
        
        if(Platform.isFxApplicationThread()){
            _showInnerDialog(contents, model, parentPane, true);
        }else{
            final Object lock = new Object();
            Runnable run = new Runnable(){
                @Override
                public void run(){
                    try{
                        _showInnerDialog(contents, model, parentPane, true);
                    }finally{
                        synchronized (lock) {
                            lock.notifyAll();                        
                        }
                    }
                }
            };
            synchronized (lock) {
                Platform.runLater(run);
                lock.wait();
            }
        }
    }
    
    /**
     * インナーウィンドウ形式のダイアログを開きます。<br>
     * parentPaneの最も上でダイアログを開きます。
     * @param contents
     * @param model
     * @param parentPane
     * @throws NullPointerException contents,model,parentPaneがnull
     */
    public static void showInnerDialog(final Node contents,final DialogModel model,
            final StackPane parentPane)throws NullPointerException{
        if(Platform.isFxApplicationThread()){
            _showInnerDialog(contents, model, parentPane, false);
        }else{
            final Object lock = new Object();
            Runnable run = new Runnable(){
                @Override
                public void run(){
                    try{
                        _showInnerDialog(contents, model, parentPane, false);
                    }finally{
                        synchronized (lock) {
                            lock.notifyAll();                        
                        }
                    }
                }
            };
            Platform.runLater(run);
        }
    }
    
    private static void _showInnerDialog(Node contents,DialogModel model,
            StackPane parentPane,boolean isWait){
        FlowPane back = new FlowPane();
        back.setAlignment(Pos.CENTER);
        back.getStyleClass().add("innerdialog");
        back.setStyle("-fx-background-color:rgba(60,60,60,0.5);");
        back.addEventHandler(Event.ANY, ALL_CONSUME);
        //TODO 単に白い背景じゃなくてウィンドウっぽくしたい（面倒くさかった）
        StackPane base = new StackPane();
        base.setStyle("-fx-background-color:white;");
        base.getStyleClass().add("innerdialog-base");
        base.getChildren().add(contents);
        back.getChildren().add(base);
        parentPane.getChildren().add(back);
        InnerDialogCloseFunction f = new InnerDialogCloseFunction(parentPane, back, base, contents);
        model.setDialogCloseFunction(f);
        
        //TODO フォーカスを何とかしたい。
        
        
        //待機
        if(isWait)
            f._wait();
    }
    
    
    private static class WindowCloseEventHandler
    implements EventHandler<WindowEvent>{
        private volatile DialogModel con;
        public WindowCloseEventHandler(DialogModel con){
            this.con = con;
        }
        @Override
        public void handle(WindowEvent event){
            if(con!=null){
                con.closeDialog();
                con = null;
                ((Window)event.getSource()).
                removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this);
            }
            
        }
    }
    

    
    private static void _showWindowDialog(final Parent contents,
            final DialogModel model,final Node parentNode,final boolean wait)
                    throws NullPointerException{
        Window parentWindow =(parentNode!=null && parentNode.getScene()!=null)?parentNode.getScene().getWindow():null;
        if(!parentWindow.isShowing())parentWindow=null;
        double x = 0,y=0;
        
        if(parentWindow!=null){
            Bounds b= parentNode.getBoundsInParent();
            double bw = b.getWidth();
            double bh = b.getHeight();
            double w = parentNode.prefWidth(-1);
            double h = parentNode.prefHeight(-1);
            Parent p = parentNode.getParent();
            if(p!=null){
                Transform tf = p.getLocalToSceneTransform();
                x = parentNode.getLayoutX();
                y = parentNode.getLayoutY();
                x = x*tf.getMxx()+y*tf.getMxy()+tf.getTx();
                y = x*tf.getMyx()+y*tf.getMyy()+tf.getTy();
            }
            Scene scene = parentNode.getScene();
            x +=scene.getX()+parentWindow.getX();
            y +=scene.getY()+parentWindow.getY();
            if(bw>0 && w > 0 && bw-w>0)
                x+=(bw-w)/2d;
            
            
            if(bh>0 && h > 0 && bh-h>0)
                y+=(bh-h)/2d;
        }        
        _showWindowDialog(contents, model, parentWindow, x, y,wait);
    }
    

    
    private  static void _showWindowDialog(Parent contents,
            DialogModel model,
            Window parentWindow,boolean wait)
                    throws NullPointerException{
        if(parentWindow!=null && !parentWindow.isShowing())parentWindow=null;
        double x = 0,y=0;
        if(parentWindow!=null){
            double ww = parentWindow.getWidth();
            double wh = parentWindow.getHeight();
            double w = contents.prefWidth(-1);
            double h = contents.prefHeight(-1);
            if(ww > 0 && w>0 && ww-w>0)
                x = (ww-w)/2d;
            if(wh > 0 && h>0 && wh-h>0)
                y = (wh-h)/2d;
        }
        _showWindowDialog(contents, model, parentWindow, x, y,wait);
    }
    

    
    private static void _showWindowDialog(Parent contents,
            DialogModel model,
            Window parentWindow,double x,double y,boolean isWait)
                    throws NullPointerException{

        if(parentWindow!=null && !parentWindow.isShowing())parentWindow=null;
        
        
        StageBuilder<?> builder=StageBuilder.create();
        
        StageStyle style = model.getStageStyle();
        if(style!=null)builder.style(style);
        
        builder
        .scene(new Scene(contents))
        .onCloseRequest(new WindowCloseEventHandler(model));
        Collection<? extends Image> icon  = model.getIcons();
        if(icon!=null)builder.icons(icon);
        Stage s = builder.build();
        if(parentWindow!=null){
            s.initOwner(parentWindow);
            s.setX(x);
            s.setY(y);
        }
        Modality m = model.getDialogModality();
        if(m==null||(m==Modality.WINDOW_MODAL&&parentWindow==null))
            m=Modality.NONE;
        s.initModality(m);
        s.resizableProperty().bind(model.dialogResizableProperty());
        s.titleProperty().bind(model.dialogTitleProperty());
        contents.requestFocus();
        
        
        WindowDialogCloseFunction f = new WindowDialogCloseFunction(s);
        model.setDialogCloseFunction(f);
        
        com.sun.javafx.css.StyleManager.getInstance().reloadStylesheets(s.getScene());
        
        
        if(isWait)
            s.showAndWait();
        else
            s.show();
    }
    
    /**
     * contentsをルートとするStageによって作られるダイアログを表示し、クローズするまで待機します。<br>
     * ダイアログはparentNodeがあるウィンドウを親ウィンドウとして作成され、parentNodeの中央に
     * ダイアログが表示されます。parentNodeがnull、または、ウィンドウが見つからない場合はこれらの設定は
     * されません。
     * @param contents 表示する内容
     * @param model ダイアログのコントローラー
     * @param parentNode 表示位置の基準となるノード
     * @throws InterruptedException 
     *このメソッドをJavaFXのスレッド以外から呼び出した時、待機中に割り込まれた場合にのみ発生します。
     * @throws NullPointerException contents,modelがnullの時
     */
    public static void showWindowDialogAndWait(final Parent contents,
            final DialogModel model,final Node parentNode)
    throws InterruptedException,NullPointerException{
        if(Platform.isFxApplicationThread()){
            _showWindowDialog(contents, model, parentNode,true);
        }else{
            final Object lock = new Object();
            Runnable run = new Runnable(){
                @Override
                public void run(){
                    try{
                        _showWindowDialog(contents, model, parentNode,true);
                    }finally{
                        synchronized (lock) {
                            lock.notifyAll();                        
                        }
                    }
                }
            };
            synchronized (lock) {
                Platform.runLater(run);
                lock.wait();
            }
        }
    }
    
    
    /**
     * contentsをルートとするStageによって作られるダイアログを表示し、クローズするまで待機します。<br>
     * ダイアログはparentWindowを親ウィンドウとして作成され、ダイアログがparentWindowの中央に来るように
     * 表示されます。parentWindowがnullの場合はそれらの設定はされません。
     * @param contents 表示する内容
     * @param model ダイアログのコントローラー
     * @param parentWindow ダイアログの親ウィンドウ
     * @throws InterruptedException
     *このメソッドをJavaFXのスレッド以外から呼び出した時、待機中に割り込まれた場合にのみ発生します。
     * @throws NullPointerException contents,modelがnullの時
     */
    public static void showWindowDialogAndWait(final Parent contents,
            final DialogModel model,
            final Window parentWindow)
    throws InterruptedException,NullPointerException{
        if(Platform.isFxApplicationThread()){
            _showWindowDialog(contents, model, parentWindow,true);
        }else{
            final Object lock = new Object();
            Runnable run = new Runnable(){
                @Override
                public void run(){
                    try{
                        _showWindowDialog(contents, model, parentWindow,true);
                    }finally{
                        synchronized (lock) {
                            lock.notifyAll();                        
                        }
                    }
                }
            };
            synchronized (lock) {
                Platform.runLater(run);
                lock.wait();
            }
        }
    }
    
    /**
     * contentsをルートとするStageによって作られるダイアログを表示し、クローズするまで待機します。<br>
     * ダイアログはparentWindowを親ウィンドウとして作成され、ウィンドウの左上から(x,y)の位置に
     * 表示されます。
     * @param contents 表示する内容
     * @param model ダイアログのコントローラー
     * @param parentWindow ダイアログの親ウィンドウ
     * @param x ダイアログの表示位置のx座標（スクリーン座標系）
     * @param y ダイアログの表示位置のy座標（スクリーン座標系）
     * @throws InterruptedException
     *このメソッドをJavaFXのスレッド以外から呼び出した時、待機中に割り込まれた場合にのみ発生します。
     * @throws NullPointerException contents,modelがnullの時
     */
    public static void showWindowDialogAndWait(final Parent contents,
            final DialogModel model,
            final Window parentWindow,final double x,final double y)
    throws InterruptedException,NullPointerException{
        if(Platform.isFxApplicationThread()){
            _showWindowDialog(contents, model, parentWindow,x,y,true);
        }else{
            final Object lock = new Object();
            Runnable run = new Runnable(){
                @Override
                public void run(){
                    try{
                        _showWindowDialog(contents, model, parentWindow,x,y,true);
                    }finally{
                        synchronized (lock) {
                            lock.notifyAll();                        
                        }
                    }
                }
            };
            synchronized (lock) {
                Platform.runLater(run);
                lock.wait();
            }
        }
    }
}
