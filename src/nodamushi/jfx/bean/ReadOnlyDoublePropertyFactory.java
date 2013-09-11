package nodamushi.jfx.bean;

import java.util.concurrent.atomic.AtomicLong;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * ReadOnlyPropertyDoubleを作りたいけど、自分からは値を書き換えられるようにしたい。<br>
 * でも、いちいちクラス作るの面倒くさいので、ファクトリを作ってみた。
 * @author nodamushi
 *
 */
public class ReadOnlyDoublePropertyFactory extends ReadOnlyFactoryBase<Double>{
    private static abstract class Base extends ReadOnlyDoublePropertyBase{
        
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
        public void addListener(ChangeListener<? super Number> listener){
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
        public void removeListener(ChangeListener<? super Number> listener){
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
        
        abstract void set(double d);
    }
    private static class Property extends Base{
        private double value;
        Property(Object bean,String name,double init){
            super(bean,name);
            value =init;
        }

        @Override
        public double get(){
            return value;
        }
        @Override
        void set(final double d){
            if(Platform.isFxApplicationThread()){   
                double o = value;
                if(o!=d){
                    value = d;
                    fireValueChangedEvent();
                }
            }else{
                double o = value;
                if(o!=d){
                    value = d;
                    Platform.runLater(new Runnable(){
                        public void run(){
                            value = d;
                            fireValueChangedEvent();
                        }
                    });
                }
            }
        }
    }
    
    private static class AtomicProperty extends Base{
        private AtomicLong value;
        private Runnable run = new Runnable(){
            public void run(){ fireValueChangedEvent();}
        };
        AtomicProperty(Object bean,String name,double init){
            super(bean,name);
            value.set(Double.doubleToLongBits(init));
        }

        @Override
        public double get(){
            return Double.longBitsToDouble(value.get());
        }
        @Override
        void set(final double d){
            value.set(Double.doubleToLongBits(d));
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
    public ReadOnlyDoublePropertyFactory(Object bean,String name){
        this(bean,name,0d,false);
    }
    /**
     * 初期値0でプロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyDoublePropertyFactory(Object bean,String name,boolean useAtomic){
        this(bean,name,0d,useAtomic);
    }
    /**
     * プロパティを作成します。
     * @param bean ReadOnlyPropertyのgetBeanで返すオブジェクト
     * @param name ReadOnlyPropertyのgetNameで返す名前
     * @param initValue 初期値
     * @param useAtomic java.util.concurrent.atomicパッケージを使うかどうか。
     */
    public ReadOnlyDoublePropertyFactory(Object bean,String name,double initValue,boolean useAtomic){
        super(useAtomic);
        property =useAtomic?new AtomicProperty(bean, name, initValue): new Property(bean, name, initValue);
    }
    
    public void setValue(double value){
        checkIsnotBound();
        property.set(value);
    }
    
    public double getValue(){return property.get();}
    
    public ReadOnlyDoubleProperty get(){return property;}
    @Override
    protected void update(ObservableValue<? extends Double> observable){
        double value = observable.getValue();
        property.set(value);
    }
}
