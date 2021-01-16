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
@NamedEntityGraph(name = "Study.withTagsAndZones",attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones")
})
@NamedEntityGraph(name = "Study.withManagers",attributeNodes = {
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Study.withManagersAndMembers",attributeNodes = {
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")
})
@Entity @Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Study {
    @Id @Column(name = "study_id")
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

    @Lob @Basic(fetch = FetchType.EAGER)
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
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);
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

    public void publish() {
        if (!this.closed && !published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("스터디를 공개할 수 없습니다. 이미 공개되었거나 종료된 스터디입니다.");
        }
    }

    public void close() {
        if (!this.closed && this.published) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("공개되지 않은 스터디이거나 이미 종료된 스터디는 종료할 수 없습니다.");
        }
    }

    public boolean canUpdateRecruit() {
        return this.published && this.recruitingUpdateDateTime == null
                || this.published && this.recruitingUpdateDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }

    public boolean isRemovable() {
        return !this.published;
    }

    public void memberJoin(Account account) {
        this.members.add(account);
        this.memberCount++;
    }

    public void memberDisjoin(Account account) {
        this.members.remove(account);
        this.memberCount--;
    }

    public void addManager(Account account) {
        this.getManagers().add(account);
    }

    public void addMember(Account account) {
        this.getMembers().add(account);
        this.memberCount++;
    }
}
