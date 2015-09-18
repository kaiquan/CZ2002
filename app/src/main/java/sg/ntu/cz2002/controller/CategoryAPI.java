package sg.ntu.cz2002.controller;

import java.util.ArrayList;

import sg.ntu.cz2002.entity.Location;

/**
 * Created by Moistyburger on 8/9/15.
 */
public class CategoryAPI {

    public ArrayList<Location.Category> getCategoriesOptions(boolean isGood){
        ArrayList<Location.Category> categories = new ArrayList<Location.Category>();
        categories.add(Location.Category.HawkerCentres);
        if(isGood){
            categories.add(Location.Category.Libraries);
        }
        else{

        }
        return null;
    }


}
