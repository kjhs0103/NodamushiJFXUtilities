package nodamushi.jfx.bean;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatPropertyBase;

public class ReadOnlyFloatPropertyFactory{
    private static abstract class Base extends ReadOnlyFloatPropertyBase{
        
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

        abstract void set(float d);
    }
    private static class Property extends Base{
        private float value;
        Property(Object bean,String name,float init){
            super(bean,name);
            value =init;
        }

        @Override
        public float get(){
            return value;
        }
        @Override
        void set(final float v){
            if(Platform.isFxApplicationThread()){   
                float o = value;
                if(o!=v){
                    value = v;
                    fireValueChangedEvent();
                }
            }else{
                float o = value;
                if(o!=v)
                    value = v;
                Platform.runLater(new Runnable(){
                    public void run(){
                        float o = value;
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
        private AtomicInteger value;
        private Runnable run = new Runnable(){
            public void run(){ fireValueChangedEvent();}
        };
        AtomicProperty(Object bean,String name,float init){
            super(bean,name);
            value.set(Float.floatToIntBits(init));
        }

        @Override
        public float get(){
            return Float.intBitsToFloat(value.get());
        }
        @Override
        void set(final float v){
            value.set(Float.floatToIntBits(v));
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
    public ReadOnlyFloatPropertyFactory(Object bean,String name){
        this(bean,name,0,false);
    }
    /**
     * 初期値0でプロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyFloatPropertyFactory(Object bean,String name,boolean useAtomic){
        this(bean,name,0,useAtomic);
    }
    /**
     * プロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param initValue 初期値
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyFloatPropertyFactory(Object bean,String name,float initValue,boolean useAtomic){
        property =useAtomic?new AtomicProperty(bean, name, initValue): new Property(bean, name, initValue);
    }
    
    public void setValue(float value){
        property.set(value);
    }
    
    public float getValue(){return property.get();}
    
    public ReadOnlyFloatProperty get(){return property;}
}
