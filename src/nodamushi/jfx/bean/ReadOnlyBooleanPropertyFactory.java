package nodamushi.jfx.bean;

import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * ReadOnlyBooleanPropertyWraperとの違いは、
 * Propertyは一つしか作らないことと、
 * マルチスレッドをちょっと考慮しています。<br>
 * @author nodamushi
 *
 */
public class ReadOnlyBooleanPropertyFactory extends ReadOnlyFactoryBase<Boolean>{
    private static abstract class Base extends ReadOnlyBooleanPropertyBase{
        
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
        @Override
        public void addListener(ChangeListener<? super Boolean> listener){
            if(Platform.isFxApplicationThread())
                super.addListener(listener);
            else
                synchronized (this) {//runnableの方が良いか？
                    super.addListener(listener);
                }
        }
        
        @Override
        public void addListener(InvalidationListener listener){
            if(Platform.isFxApplicationThread())
                super.addListener(listener);
            else
                synchronized (this) {//runnableの方が良いか？
                    super.addListener(listener);
                }
        }
        
        @Override
        public void removeListener(ChangeListener<? super Boolean> listener){
            if(Platform.isFxApplicationThread())
                super.removeListener(listener);
            else
                synchronized (this) {//runnableの方が良いか？
                    super.removeListener(listener);
                }
        }
        
        @Override
        public void removeListener(InvalidationListener listener){
            if(Platform.isFxApplicationThread())
                super.removeListener(listener);
            else
                synchronized (this) {//runnableの方が良いか？
                    super.removeListener(listener);
                }
        }
        abstract void set(boolean d);
    }
    private static class Property extends Base{
        private boolean value;
        Property(Object bean,String name,boolean init){
            super(bean,name);
            value =init;
        }

        @Override
        public boolean get(){
            return value;
        }
        @Override
        void set(final boolean v){
            if(Platform.isFxApplicationThread()){   
                boolean o = value;
                if(o!=v){
                    value = v;
                    fireValueChangedEvent();
                }
            }else{
                boolean o = value;
                if(o!=v){
                    value = v;
                    Platform.runLater(new Runnable(){
                        public void run(){
                            value = v;
                            fireValueChangedEvent();
                            
                        }
                    });
                }
            }
        }
    }
    
    private static class AtomicProperty extends Base{
        private AtomicBoolean value;
        private Runnable run = new Runnable(){
            public void run(){ fireValueChangedEvent();}
        };
        AtomicProperty(Object bean,String name,boolean init){
            super(bean,name);
            value.set(init);
        }

        @Override
        public boolean get(){
            return value.get();
        }
        @Override
        void set(final boolean v){
            value.set(v);
            if(Platform.isFxApplicationThread()){
                    fireValueChangedEvent();
            }else{
                Platform.runLater(run);
            }
        }
    }
    
    
    private final Base property;
    /**
     * 初期値0、java.util.concurrent.atomicパッケージは使わないでプロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     */
    public ReadOnlyBooleanPropertyFactory(Object bean,String name){
        this(bean,name,false,false);
    }
    /**
     * 初期値0でプロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyBooleanPropertyFactory(Object bean,String name,boolean useAtomic){
        this(bean,name,false,useAtomic);
    }
    /**
     * プロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param initValue 初期値
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyBooleanPropertyFactory(Object bean,String name,boolean initValue,boolean useAtomic){
        super(useAtomic);
        property =useAtomic?new AtomicProperty(bean, name, initValue): new Property(bean, name, initValue);
    }
    
    public void setValue(boolean value){
        checkIsnotBound();
        property.set(value);
    }
    
    public boolean getValue(){return property.get();}
    
    
    public ReadOnlyBooleanProperty get(){return property;}
    
    @Override
    protected void update(ObservableValue<? extends Boolean> observable){
        boolean value = observable.getValue();
        property.set(value);
    }
}
