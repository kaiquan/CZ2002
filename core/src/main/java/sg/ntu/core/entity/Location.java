package sg.ntu.core.entity;

/**
 * Created by Moistyburger on 7/9/15.
 */

public class Location {
    private Category category;
    private String address;
    private String hyperlink;
    private String descriptions;
    private String name;
    private Coordinate coordinate;
    private String iconURL;

    public enum Category {
        HawkerCentres,
        Museums,
        Libraries,
        WaterVentures,
        TouristAttractions,
        Parks;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}
