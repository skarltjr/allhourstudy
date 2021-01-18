package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NamedEntityGraph(name = "Board.withAll",attributeNodes = {
        @NamedAttributeNode("writer"),
        @NamedAttributeNode("comments")
})
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "board_id")
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

    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

    public void addView() {
        this.viewCount++;
    }
    //순환참조 예상
}
