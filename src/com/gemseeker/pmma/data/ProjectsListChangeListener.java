package com.gemseeker.pmma.data;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author RAFIS-FRED
 */
public class ProjectsListChangeListener implements ListChangeListener<Project> {
    @Override
    public void onChanged(Change<? extends Project> c) {
        ObservableList<Project> projects = (ObservableList<Project>) c.getList();
        if(c.wasAdded()){
            int from = c.getFrom();
            int to = c.getTo();
            for(int i= from+1; i<=to; i++){
                Project p = projects.get(i);
                System.out.println(p.getName() + " was added.");
            }
        }else if(c.wasUpdated()){
            int from = c.getFrom();
            int to = c.getTo();
            for(int i= from+1; i<=to; i++){
                Project p = projects.get(i);
                System.out.println(p.getName() + " was updated.");
            }
        }else if(c.wasReplaced()){
            int from = c.getFrom();
            int to = c.getTo();
            for(int i= from+1; i<=to; i++){
                Project p = projects.get(i);
                System.out.println(p.getName() + " was replaced.");
            }
        }
    }
}
