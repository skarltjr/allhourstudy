package com.allhour.allhourstudy.modules.tag;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Tag {
    @Id @Column(name = "tag_id")
    @GeneratedValue
    private Long id;

    @Column(unique = true,nullable = false)
    private String title;
}
