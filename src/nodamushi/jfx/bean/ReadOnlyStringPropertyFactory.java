package nodamushi.jfx.bean;

import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ReadOnlyStringPropertyFactory extends ReadOnlyFactoryBase<String>{
    private static abstract class Base extends ReadOnlyStringPropertyBase{
        
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
        public void addListener(ChangeListener<? super String> listener){
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
        public void removeListener(ChangeListener<? super String> listener){
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

        abstract void set(String d);
    }
    private static class Property extends Base{
        private String value;
        Property(Object bean,String name,String init){
            super(bean,name);
            value =init;
        }

        @Override
        public String get(){
            return value;
        }
        @Override
        void set(final String v){
            if(Platform.isFxApplicationThread()){   
                String o = value;
                if(o!=v){
                    value = v;
                    fireValueChangedEvent();
                }
            }else{
                String o = value;
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
        private AtomicReference<String> value;
        private Runnable run = new Runnable(){
            public void run(){ fireValueChangedEvent();}
        };
        AtomicProperty(Object bean,String name,String init){
            super(bean,name);
            value.set(init);
        }

        @Override
        public String get(){
            return value.get();
        }
        @Override
        void set(final String v){
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
     * 初期値null、java.util.concurrent.atomicパッケージは使わないでプロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     */
    public ReadOnlyStringPropertyFactory(Object bean,String name){
        this(bean,name,null,false);
    }
    /**
     * 初期値nullでプロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyStringPropertyFactory(Object bean,String name,boolean useAtomic){
        this(bean,name,null,useAtomic);
    }
    /**
     * プロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param initValue 初期値
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyStringPropertyFactory(Object bean,String name,String initValue,boolean useAtomic){
        super(useAtomic);
        property =useAtomic?new AtomicProperty(bean, name, initValue): 
            new Property(bean, name, initValue);
    }
    
    public void setValue(String value){
        property.set(value);
    }
    
    public String getValue(){return property.get();}
    
    public ReadOnlyStringProperty get(){return property;}
    
    @Override
    protected void update(ObservableValue<? extends String> observable){
        String value = observable.getValue();
        property.set(value);
    }
}
