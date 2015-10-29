package sg.ntu.cz2002.controller;

import java.util.ArrayList;

import sg.ntu.cz2002.entity.Location;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */
public class CategoriesController {

    public ArrayList<Location.Category> getCategoriesOptions(boolean isGood){
        ArrayList<Location.Category> categories = new ArrayList<Location.Category>();
        if(isGood){
            categories.add(Location.Category.Libraries);
            categories.add(Location.Category.Parks);
            categories.add(Location.Category.WaterVentures);
            categories.add(Location.Category.HawkerCentres);
            categories.add(Location.Category.TouristAttractions);
        }
        else{
            categories.add(Location.Category.HawkerCentres);
            categories.add(Location.Category.Museums);
            categories.add(Location.Category.Libraries);
        }
        return categories;
    }
    


}
