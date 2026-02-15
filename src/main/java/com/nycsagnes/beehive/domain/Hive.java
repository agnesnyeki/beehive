package com.nycsagnes.beehive.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Hive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hive_id")
    private Long id;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "hive_name")
    private String hiveName;

    @OneToMany(mappedBy = "hive", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Bee> bees;

    @PreRemove
    private void preRemove() {
        for (Bee bee : bees) {
            bee.setHive(null);
        }
    }

}
