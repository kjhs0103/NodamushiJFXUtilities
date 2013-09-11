package nodamushi.jfx.bean;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

public abstract class ReadOnlyFactoryBase<T>{
    private final boolean isSynchronized;
    private final Object lock;
    private ObservableValue<? extends T> observable;
    private Listener listener;
    public ReadOnlyFactoryBase(boolean issynchronized){
        isSynchronized=issynchronized;
        lock = issynchronized?new Object():null;
    }
    public void bind(ObservableValue<? extends T> newObservable){
        if(isSynchronized){
            synchronized (lock) {
                unbind();
                if(listener==null)listener=new Listener();
                observable=newObservable;
            }
            newObservable.addListener(listener);
            update(newObservable);
        }else{
            unbind();
            if(listener==null)listener=new Listener();
            observable=newObservable;
            newObservable.addListener(listener);
            update(newObservable);
        }
    }
    
    public void unbind(){
        if(isSynchronized){
            synchronized (lock) {
                if(observable!=null){
                    observable.removeListener(listener);
                    observable=null;
                } 
            }
        }else{
            if(observable!=null){
                observable.removeListener(listener);
                observable=null;
            }
        }
    }
    
    public boolean isBound(){
        return observable!=null;
    }
    
    protected void checkIsnotBound()
    throws RuntimeException{
        if(isBound())throw new RuntimeException("A bound value cannot be set.");
    }
    
    /**bindしているObservableValueの値が変更されたときに呼び出されます。*/
    protected abstract void update(ObservableValue<? extends T> observable);
    
    private class Listener implements InvalidationListener{
        @Override
        public void invalidated(Observable ob){
            if(observable==ob)
                update(observable);
        }
    }
}
