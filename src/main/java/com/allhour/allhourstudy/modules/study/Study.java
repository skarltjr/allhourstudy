package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.UserAccount;
import com.allhour.allhourstudy.modules.tag.Tag;
import com.allhour.allhourstudy.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@NamedEntityGraph(name = "Study.withAll",attributeNodes = {
        @NamedAttributeNode("members"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("tags")
})
@NamedEntityGraph(name = "Study.withTagsAndManagers",attributeNodes = {
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("tags")
})
@NamedEntityGraph(name = "Study.withZonesAndManagers",attributeNodes = {
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("zones")
})
@Entity @Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Study {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdateDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    private int memberCount;

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        if (this.published && this.recruiting &&
                !this.managers.contains(userAccount) && this.members.contains(account)) {
            return true;
        }
        return false;
    }
    public boolean isManager(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.managers.contains(account);
    }
    public boolean isMember(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.members.contains(account);
    }

    public String getImage() {
        return this.image != null ? image : "/images/default_banner.png";
    }
}
