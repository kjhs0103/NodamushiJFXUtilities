package nodamushi.jfx.bean;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * 保持するオブジェクトは変化しないが、そのオブジェクトの状態変化を伝えたい為に作ったProperty<br>
 * Chage Eventは一切起こらないのでInvalidationListenerを利用してください。<br><br>
 * 内容が変化したときにはupdateを呼び出してください。
 * @author nodamushi
 *
 * @param <T> 保持するオブジェクトの型
 */
public class ChangeableObjectProperty<T> implements ObservableValue<T>{
    
    


    private final T t;
    private final Runnable run = new Runnable(){
        @Override
        public void run(){
            fireValueChangedEvent();
        }
    };
    
    public ChangeableObjectProperty(T t){
        this.t = t;
    }
    
    
    /**
     * 内容が変化した場合に呼び出してください。
     */
    public void update(){
        if(Platform.isFxApplicationThread()){
            fireValueChangedEvent();
        }
        else Platform.runLater(run);
    }
    
    
    private void fireValueChangedEvent(){
        for(InvalidationListener i:ilisteners){
            i.invalidated(this);
        }
    }


    @Override
    public T getValue(){
        return t;
    }
    
    private List<InvalidationListener> ilisteners = new CopyOnWriteArrayList<>();

    @Override
    public void addListener(InvalidationListener inv){
        ilisteners.add(inv);
    }



    @Override
    public void removeListener(InvalidationListener inv){
        ilisteners.remove(inv);
    }



    @Override@Deprecated
    public void addListener(ChangeListener<? super T> listener){}

    @Override@Deprecated
    public void removeListener(ChangeListener<? super T> listener){}

}
