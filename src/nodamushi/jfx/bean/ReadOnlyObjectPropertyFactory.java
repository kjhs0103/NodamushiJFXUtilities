package nodamushi.jfx.bean;

import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;

public class ReadOnlyObjectPropertyFactory<T>{
    private static abstract class Base<T> extends ReadOnlyObjectPropertyBase<T>{
        
        private final String name;
        private final Object bean;
        Base(Object bean,String name){
            this.bean = bean;
            this.name = name;
        }
        @Override
        public final Object getBean(){
            return bean;
        }

        @Override
        public final String getName(){
            return name;
        }

        abstract void set(T d);
    }
    private static class Property<T> extends Base<T>{
        private T value;
        Property(Object bean,String name,T init){
            super(bean,name);
            value =init;
        }

        @Override
        public T get(){
            return value;
        }
        @Override
        void set(final T v){
            if(Platform.isFxApplicationThread()){   
                T o = value;
                if(o!=v){
                    value = v;
                    fireValueChangedEvent();
                }
            }else{
                T o = value;
                if(o!=v)
                    value = v;
                Platform.runLater(new Runnable(){
                    public void run(){
                        T o = value;
                        if(o!=v){
                            value = v;
                            fireValueChangedEvent();
                        }
                    }
                });
            }
        }
    }
    
    private static class AtomicProperty<T> extends Base<T>{
        private AtomicReference<T> value;
        private Runnable run = new Runnable(){
            public void run(){ fireValueChangedEvent();}
        };
        AtomicProperty(Object bean,String name,T init){
            super(bean,name);
            value.set(init);
        }

        @Override
        public T get(){
            return value.get();
        }
        @Override
        void set(final T v){
            value.set(v);
            if(Platform.isFxApplicationThread()){
                    fireValueChangedEvent();
            }else{
                Platform.runLater(run);
            }
        }
    }
    
    
    private final Base<T> property;
    /**
     * 初期値0、java.util.concurrent.atomicパッケージは使わないでプロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     */
    public ReadOnlyObjectPropertyFactory(Object bean,String name){
        this(bean,name,null,false);
    }
    /**
     * 初期値0でプロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyObjectPropertyFactory(Object bean,String name,boolean useAtomic){
        this(bean,name,null,useAtomic);
    }
    /**
     * プロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param initValue 初期値
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyObjectPropertyFactory(Object bean,String name,T initValue,boolean useAtomic){
        property =useAtomic?new AtomicProperty<>(bean, name, initValue): 
            new Property<>(bean, name, initValue);
    }
    
    public void setValue(T value){
        property.set(value);
    }
    
    public T getValue(){return property.get();}
    
    public ReadOnlyObjectProperty<T> get(){return property;}
}
