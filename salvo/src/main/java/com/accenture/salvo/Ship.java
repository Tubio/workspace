package com.accenture.salvo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class Ship {
    @ElementCollection
    @Column(name = "locations")
    private List<String> locations;

    //Constructor
    public Ship() {
        
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
        locations = new ArrayList<>();
    }
}


