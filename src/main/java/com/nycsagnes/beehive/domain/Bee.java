package com.nycsagnes.beehive.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Bee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bee_id")
    private Long id;

    @Column(name = "bee_name")
    private String beeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "bee_type")
    private BeeType beeType;

    @ManyToOne
    @JoinColumn(name = "hive_id")
    private Hive hive;
}
