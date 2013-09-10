package nodamushi.jfx.scene.control;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import nodamushi.jfx.bean.ChangeableObjectProperty;

public class ObjectListCell<T> extends ListCell<ChangeableObjectProperty<T>>{
    
    /**
     * ObjectListCellのupdateメソッドから呼ばれます。<br>
     * ObjectListCellのGUIを構築してください。
     * @author nodamushi
     *
     * @param <T>
     */
    public static interface CellUpdateFunction<T>{
        public void update(T item,ObjectListCell<T> cell);
    }
    
    /**
     * ファクトリを生成して返します
     * @param function updateメソッドで使用するオブジェクト。null可
     * @return
     */
    public static <T> Callback<ListView<ChangeableObjectProperty<T>>, ListCell<ChangeableObjectProperty<T>>>
      createFacotry(final CellUpdateFunction<T> function){
        return new Callback<ListView<ChangeableObjectProperty<T>>, ListCell<ChangeableObjectProperty<T>>>(){
            @Override
            public ListCell<ChangeableObjectProperty<T>> call(
                    ListView<ChangeableObjectProperty<T>> l){
                return new ObjectListCell<>(function);
            }
            
        };
    }
    
    
    
    
    private InvalidationListener listener = new InvalidationListener(){
        @SuppressWarnings("unchecked")
        @Override
        public void invalidated(Observable observable){
            update(((ChangeableObjectProperty<T>)observable).getValue());
        }
    };
    
    
    public ObjectListCell(){
        this(null);
    }
    
    public ObjectListCell(CellUpdateFunction<T> function){
        if(function!=null)
            setCellUpdateFunction(function);
        itemProperty().addListener(new ChangeListener<ChangeableObjectProperty<T>>(){
            @Override
            public void changed(
                    ObservableValue<? extends ChangeableObjectProperty<T>> observable ,
                    ChangeableObjectProperty<T> oldValue ,
                    ChangeableObjectProperty<T> newValue){
                if(oldValue!=null)oldValue.removeListener(listener);
                if(newValue!=null){
                    newValue.addListener(listener);
                    update(newValue.getValue());
                }else{
                    update(null);
                }
            }
        });
        cellUpdateFunctionProperty().addListener(new ChangeListener<CellUpdateFunction<T>>(){
            @Override
            public void changed(
                    ObservableValue<? extends CellUpdateFunction<T>> ob ,
                    CellUpdateFunction<T> oldValue ,CellUpdateFunction<T> newValue){
                update(getItem().getValue());
            }
        });
    }
    private ObjectProperty<CellUpdateFunction<T>> cellupdatefunctionProeprty =
            new ObjectPropertyBase<ObjectListCell.CellUpdateFunction<T>>(){
                @Override
                public Object getBean(){
                    return ObjectListCell.this;
                }

                @Override
                public String getName(){
                    return "cellUpdateFunction";
                }        
    };

    public void setCellUpdateFunction(CellUpdateFunction<T> f){
        cellupdatefunctionProeprty.set(f);
    }
    
    public CellUpdateFunction<T> getCellUpdateFunction(){
        return cellupdatefunctionProeprty.get();
    }
    
    public ObjectProperty<CellUpdateFunction<T>> cellUpdateFunctionProperty(){
        return cellupdatefunctionProeprty;
    }
    
    /**
     * Tの値を用いてCellを構築します。<br>
     * 必要な場合はupdateItemではなく、こちらをオーバーライドしてください。
     * @param t
     */
    protected void update(T t){
        CellUpdateFunction<T> function =getCellUpdateFunction();
        if(function!=null){
            function.update(t, this);
        }else{
            if(t==null){
                setText(null);
            }else{
                setText(t.toString());
            }
        }
    }
    
    
    
    //---------------------ユーティリティー------------------------------
    
    
    /**
     * oを持つChangeableObjectPropertyの最初に出てくる位置を返します
     * @param o 検索するオブジェクト
     * @param list 検索対象のリスト
     * @return oのインデックス。listにない場合は-1
     */
    public static int indexOf(Object o,ObservableList<ChangeableObjectProperty<?>> list){
        int i=0;
        for(ChangeableObjectProperty<?> c:list){
            if(c.getValue()==o)return i;
            i++;
        }
        return -1;
    }
    
    /**
     * oを持つChangeableObjectPropertyの最初に出てくる位置を返します
     * @param o 検索するオブジェクト
     * @param listview 検索対象のListView
     * @return oのインデックス。listviewにない場合は-1
     */
    public static int indexOf(Object o,ListView<ChangeableObjectProperty<?>> listview){
        return indexOf(o, listview.getItems());
    }
    
    
    /**
     * list内のoを持つChangeableObjectPropertyを全てupdateします。
     * @param o
     * @param list
     */
    public static void allUpdate(Object o,ObservableList<ChangeableObjectProperty<?>> list){
        for(ChangeableObjectProperty<?> c:list){
            if(c.getValue()==o)c.update();
        }
    }
    
    /**
     * listview内のoを持つChangeableObjectPropertyを全てupdateします。
     * @param o
     * @param listview
     */
    public static void allUpdate(Object o,ListView<ChangeableObjectProperty<?>> listview){
        allUpdate(o, listview.getItems());
    }
}
