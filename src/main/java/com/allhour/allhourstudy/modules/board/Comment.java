package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.Account;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(name = "Comment.withAll",attributeNodes = {
        @NamedAttributeNode("board"),
        @NamedAttributeNode("commentWriter")
})
@Entity @Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "comment_id")
public class Comment {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Length(max = 200)
    private String chat;

    private LocalDateTime createdDateTime;

    private LocalDateTime updatedDateTime;

    //보드
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    //작성자
    @ManyToOne(fetch = FetchType.LAZY)
    private Account commentWriter;
}
