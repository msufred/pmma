package com.gemseeker.pmma.components;

import static com.gemseeker.pmma.AppConstants.IN_ANIM_INTERPOLATOR;
import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.components.ViewPager.PagerAdapter;
import com.gemseeker.pmma.ui.components.RippleSkinFactory;
import com.sun.javafx.scene.control.skin.ButtonSkin;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * A tab layout with animated tab indicator. SlidingTabsLayout requires a
 * ViewPager which can be set by invoking SlidingTabsLayout.setViewPager().
 * 
 * @author Gem Seeker
 */
public class SlidingTabsLayout extends VBox {

    final Duration ANIM_DURATION = new Duration(400);
    final double DEF_TAB_HEIGHT = 48;
    
    private VBox tabsContainer;
    private StackPane tabsStripContainer;
    private HBox tabsStrip;
    private ViewPager viewPager;
    private Line tabIndicator;
    
    private Tab mCurrentTab;
    
    public SlidingTabsLayout(){
        initComponents();
    }
    
    private void initComponents(){
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("sliding-tabs-layout");
        
        tabsContainer = new VBox();
        tabsContainer.setFillWidth(false);
        tabsContainer.setAlignment(Pos.TOP_CENTER);
        tabsContainer.getStyleClass().add("tabs-container");
        tabsContainer.setPrefWidth(USE_COMPUTED_SIZE);
        VBox.setVgrow(tabsContainer, Priority.NEVER);
        
        tabsStrip = new HBox();
        tabsStrip.setAlignment(Pos.CENTER_LEFT);
        tabsStrip.setPrefHeight(USE_COMPUTED_SIZE);
        
        tabIndicator = new Line();
        tabIndicator.setStrokeWidth(3);
        tabIndicator.setManaged(false);
        tabIndicator.translateYProperty().bind(tabsContainer.heightProperty().subtract(1.5));
        tabIndicator.getStyleClass().add("tab-indicator");
        
        tabsStripContainer = new StackPane();
        tabsStripContainer.setAlignment(Pos.CENTER);
        tabsStripContainer.getChildren().addAll(tabsStrip, tabIndicator);
        
        tabsContainer.getChildren().add(tabsStripContainer);
        getChildren().add(tabsContainer);
    }
    
    public void setViewPager(ViewPager pager){
        if(pager != null){
            pager.setOnPageSelectedListener(new OnPageSelectedListener());
            populateTabsStrip(pager);
            getChildren().add(pager);
        }
        viewPager = pager;
    }
    
    public void setTabHeight(double height){
        // TODO:
    }
    
    public void setTabsSpacing(double spacing){
        tabsStrip.setSpacing(spacing);
    }
    
    private void populateTabsStrip(ViewPager pager){
        PagerAdapter adapter = pager.getAdapter();
        for(int i=0; i<adapter.getCount(); i++){
            Tab tab = new Tab();
            tab.setText(adapter.getTitleAt(i));
            tab.getStyleClass().add("tab");
            tab.setPrefHeight(DEF_TAB_HEIGHT);
            tab.setOnAction(evt -> {
                Tab tabPressed = (Tab)evt.getSource();
                if(mCurrentTab != tabPressed){
                    pager.setCurrentItem(tabsStrip.getChildren().indexOf(tabPressed));
                    mCurrentTab = tabPressed;
                }
            });
            Object controller = adapter.getControllerAt(i);
            if(controller instanceof ControlledScreen){
                ((ControlledScreen)controller).onStart();
            }
            tabsStrip.getChildren().add(tab);
        }
    }
    
    private void moveIndicator(double width, double x){
        Timeline anim = new Timeline(new KeyFrame(ANIM_DURATION,
                new KeyValue(tabIndicator.layoutXProperty(), x, IN_ANIM_INTERPOLATOR),
                new KeyValue(tabIndicator.endXProperty(), width, IN_ANIM_INTERPOLATOR)
        ));
        anim.play();
    }
    
    public static class Tab extends Button {
        @Override
        protected Skin<?> createDefaultSkin() {
            SkinBase skin = new ButtonSkin(this);
            RippleSkinFactory.getRippleEffect(skin, this);
            return super.createDefaultSkin();
        }
    }
    
    public class OnPageSelectedListener implements ViewPager.PageListener {
        @Override
        public void onPageSelected(int position) {
            Tab tab = (Tab) tabsStrip.getChildren().get(position);
            if (tab.getWidth() > 0) {
                moveIndicator(tab.getWidth(), getPadding().getLeft() + tab.getLayoutX());
            } else {
                tab.widthProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        if (newValue.intValue() > 0) {
                            moveIndicator(tab.getWidth(), getPadding().getLeft() + tab.getLayoutX());
                            tab.widthProperty().removeListener(this);
                        }
                    }
                });
            }
        }
    }
}
