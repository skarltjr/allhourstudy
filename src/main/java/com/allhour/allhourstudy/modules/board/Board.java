package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@NamedEntityGraph(name = "Board.withAll",attributeNodes = {
        @NamedAttributeNode("writer")
})
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Board {

    @Id @Column(name = "board_id")
    @GeneratedValue
    private Long id;

    private String title;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String content;

    private int viewCount;

    private LocalDateTime createdDateTime;

    private LocalDateTime updatedDateTime;

    // 작성자 account
    @ManyToOne(fetch = FetchType.LAZY)
    private Account writer;

    //순환참조 예상
}
