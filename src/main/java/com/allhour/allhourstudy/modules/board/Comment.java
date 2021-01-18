package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "comment_id")
public class Comment {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

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
