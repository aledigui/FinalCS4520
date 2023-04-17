package com.example.finalcs4520;

import java.io.Serializable;
import java.util.ArrayList;

public class TransitRoute implements Serializable {
    private ArrayList<TransitSection> sections;

    public TransitRoute(ArrayList<TransitSection> sections) {
        this.sections = sections;
    }

    public ArrayList<TransitSection> getSections() {
        return sections;
    }

    public void setSections(ArrayList<TransitSection> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "TransitRoute{" +
                "sections=" + sections +
                '}';
    }
}
