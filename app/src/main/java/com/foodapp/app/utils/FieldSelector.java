package com.foodapp.app.utils;

import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldSelector {

    private final List<PlaceField> placeFields;

    public FieldSelector() {
        this(Arrays.asList(Place.Field.values()));
    }

    public FieldSelector(List<Place.Field> validFields) {
        placeFields = new ArrayList<>();
        for (Place.Field field : validFields) {
            placeFields.add(new PlaceField(field));
        }
    }

    public List<Place.Field> getAllFields() {
        List<Place.Field> list = new ArrayList<>();
        for (PlaceField placeField : placeFields) {
            list.add(placeField.field);
        }

        return list;
    }

    private static class PlaceField {
        final Place.Field field;

        public PlaceField(Place.Field field) {
            this.field = field;
        }
    }

}
