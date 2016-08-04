package test;

import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.Project;
import java.util.ArrayList;

/**
 *
 * @author RAFIS-FRED
 */
public class Test {

    public static void main(String[] args) {
        DBManager db = new DBManager();
        ArrayList<Project> projects = db.getProjects();
        projects.stream().forEach((p) -> {
            System.out.println(p.toString());
        });
    }
    
}
