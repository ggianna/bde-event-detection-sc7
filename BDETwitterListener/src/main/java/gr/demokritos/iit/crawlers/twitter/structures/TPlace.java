/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.crawlers.twitter.structures;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;
import java.util.Objects;
import twitter4j.Place;

/**
 * naive wrapper of {@link Place} interface, ignores boundingBoxCoordinates,
 * GeometryCoordinates and ContainedWithin members
 *
 * @author George K. <gkiom@iit.demokritos.gr>
 */
@UDT(name = "twitter_place", keyspace = "bde_twitter")
public class TPlace {

    @Field(name = "id")
    private String id;
    @Field(name = "full_name")
    private String fullName;
    @Field(name = "name")
    private String name;
    @Field(name = "street_address")
    private String streetAddress;
    @Field(name = "country_code")
    private String countryCode;
    @Field(name = "country")
    private String country;
    @Field(name = "place_type")
    private String placeType;
    @Field(name = "url")
    private String URL;
    @Field(name = "bounding_box_type")
    private String boundingBoxType;
    @Field(name = "geometry_type")
    private String geometryType;

    public TPlace(Place place) {
        this.fullName = place.getFullName() == null ? "" : place.getFullName();
        this.name = place.getName() == null ? "" : place.getName();
        this.streetAddress = place.getStreetAddress() == null ? "" : place.getStreetAddress();
        this.countryCode = place.getCountryCode() == null ? "" : place.getCountryCode();
        this.id = place.getId() == null ? "" : place.getId();
        this.country = place.getCountry() == null ? "" : place.getCountry();
        this.placeType = place.getPlaceType() == null ? "" : place.getPlaceType();
        this.URL = place.getURL() == null ? "" : place.getURL();
        this.boundingBoxType = place.getBoundingBoxType() == null ? "" : place.getBoundingBoxType();
        this.geometryType = place.getGeometryType() == null ? "" : place.getGeometryType();
    }

    /**
     * all members are instantiated as empty strings
     */
    public TPlace() {
        this.fullName = "";
        this.name = "";
        this.streetAddress = "";
        this.countryCode = "";
        this.id = "";
        this.country = "";
        this.placeType = "";
        this.URL = "";
        this.boundingBoxType = "";
        this.geometryType = "";
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String url) {
        this.URL = url;
    }

    public String getBoundingBoxType() {
        return boundingBoxType;
    }

    public void setBoundingBoxType(String boundingBoxType) {
        this.boundingBoxType = boundingBoxType;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(String geometryType) {
        this.geometryType = geometryType;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.countryCode);
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TPlace other = (TPlace) obj;
        if (!Objects.equals(this.countryCode, other.countryCode)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TPlace{"
                + "full_name=" + fullName
                + ", name=" + name
                + ", street_address=" + streetAddress
                + ", country_code=" + countryCode
                + ", id=" + id
                + ", country=" + country
                + ", place_type=" + placeType
                + ", url=" + URL
                + ", bounding_box_type=" + boundingBoxType
                + ", geometry_type=" + geometryType
                + "}";
    }
}
