package com.gemseeker.pmma.components;

import com.gemseeker.pmma.ControlledScreen;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * A container that allows application to switch content at any point of time.
 * The content or contents are exposed and determined by a PagerAdapter object.
 * That said, to be able to use ViewPager, a PagerAdapter must be created and
 * the use it as parameter to ViewPager.setAdapter() method.
 * 
 * @author Gem Seeker
 */
public class ViewPager extends StackPane {

//    private Object mCurrentController;
//    private int mCurrentIndex = -1;
    
    private PagerAdapter mAdapter;
    private PageListener mPageListener;
    
    private final SimpleObjectProperty mCurrentItemProperty = new SimpleObjectProperty();
    private final SimpleIntegerProperty mSelectedIndexProperty = new SimpleIntegerProperty(-1);
    
    public void setAdapter(PagerAdapter adapter){
        mAdapter = adapter;
    }
    
    public PagerAdapter getAdapter(){
        return mAdapter;
    }
    
    public void setOnPageSelectedListener(PageListener pageListener){
        mPageListener = pageListener;
    }
    
    public PageListener getPageSelectedListener(){
        return mPageListener;
    }
    
    public void setCurrentItem(int position){
        if(mAdapter == null){
            return;
        }
        if(!getChildren().isEmpty()){
            getChildren().remove(0);
            if(mCurrentItemProperty != null && mCurrentItemProperty.get() instanceof ControlledScreen){
                ((ControlledScreen)mCurrentItemProperty.get()).onPause();
            }
        }
        
        getChildren().add(mAdapter.onCreateView(position));
        Object controller = mAdapter.getControllerAt(position);
        if(controller instanceof ControlledScreen){
            ((ControlledScreen)controller).onResume();
        }
        
        mCurrentItemProperty.set(controller);
        mSelectedIndexProperty.set(position);
        
        if(mPageListener != null){
            mPageListener.onPageSelected(position);
        }
    }
    
    public int getSelectedIndex(){
        return mSelectedIndexProperty.get();
    }
    
    /**
     * Interface PagerAdapter defines what view to display on a specific page
     * index. This interface can be used on a SlidingTabsLayout etc.
     */
    public static interface PagerAdapter {
        /**
         * Defines what view to return for a specific index (or position)
         * @param position 
         * @return Node content
         */
        public Node onCreateView(int position);
        
        /**
         * Returns the controller of the view for a specific index.
         * @param position
         * @return 
         */
        public ControlledScreen getControllerAt(int position);
        
        /**
         * Defines the title or name of the view for a specific position. This
         * can be a String displayed on a tab view.
         * @param position
         * @return 
         */
        public String getTitleAt(int position);
        public int getCount();
    }
    
    /**
     * Interface PageListener defines actions when a page is selected. Implementing
     * the methods of PageListener allows programmer to manipulate the action or
     * logic to be executed when a page is selected.
     */
    public static interface PageListener {
        /**
         * Defines the action or logic to execute when a page is selected on a
         * specific index or position.
         * @param position 
         */
        public void onPageSelected(int position);
    }
    
    /**
     * PageViewHolder serves as a container (or so it seems) of ControlledScreen,
     * its Node view and its index.
     */
    private class PageViewHolder {
        private ControlledScreen controller;
        private Node view;
        private final int index;
        
        public PageViewHolder(int index){
            this.index = index;
        }
        
        public int getIndex(){
            return index;
        }
        
        public void setView(Node view){
            this.view = view;
        }
        
        public Node getView(){
            return view;
        }
        
        public void setPagerViewController(ControlledScreen controller){
            this.controller = controller;
        }
        
        public ControlledScreen getPagerViewController(){
            return controller;
        }
    }
}
