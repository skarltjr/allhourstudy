package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(name = "Enrollment.withAll",attributeNodes = {
        @NamedAttributeNode("account"),
        @NamedAttributeNode("event")
})
@NamedEntityGraph(
        name = "Enrollment.withAllAndStudy",
        attributeNodes ={
                @NamedAttributeNode("account"),
                @NamedAttributeNode(value = "event",subgraph = "study")
        },
        subgraphs =@NamedSubgraph(name = "study",attributeNodes = @NamedAttributeNode("study"))
)
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Enrollment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;


}
