package nodamushi.jfx.bean;

import java.util.concurrent.atomic.AtomicLong;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongPropertyBase;

public class ReadOnlyLongPropertyFactory{
    private static abstract class Base extends ReadOnlyLongPropertyBase{
        
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

        abstract void set(long d);
    }
    private static class Property extends Base{
        private long value;
        Property(Object bean,String name,long init){
            super(bean,name);
            value =init;
        }

        @Override
        public long get(){
            return value;
        }
        @Override
        void set(final long v){
            if(Platform.isFxApplicationThread()){   
                long o = value;
                if(o!=v){
                    value = v;
                    fireValueChangedEvent();
                }
            }else{
                long o = value;
                if(o!=v)
                    value = v;
                Platform.runLater(new Runnable(){
                    public void run(){
                        long o = value;
                        if(o!=v){
                            value = v;
                            fireValueChangedEvent();
                        }
                    }
                });
            }
        }
    }
    
    private static class AtomicProperty extends Base{
        private AtomicLong value;
        private Runnable run = new Runnable(){
            public void run(){ fireValueChangedEvent();}
        };
        AtomicProperty(Object bean,String name,long init){
            super(bean,name);
            value.set(init);
        }

        @Override
        public long get(){
            return value.get();
        }
        @Override
        void set(final long v){
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
    public ReadOnlyLongPropertyFactory(Object bean,String name){
        this(bean,name,0l,false);
    }
    /**
     * 初期値0でプロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyLongPropertyFactory(Object bean,String name,boolean useAtomic){
        this(bean,name,0l,useAtomic);
    }
    /**
     * プロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param initValue 初期値
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyLongPropertyFactory(Object bean,String name,long initValue,boolean useAtomic){
        property =useAtomic?new AtomicProperty(bean, name, initValue): new Property(bean, name, initValue);
    }
    
    public void setValue(long value){
        property.set(value);
    }
    
    public long getValue(){return property.get();}
    
    public ReadOnlyLongProperty get(){return property;}
}
