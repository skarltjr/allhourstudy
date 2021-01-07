package com.allhour.allhourstudy.modules.tag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Long> {
    Tag findByTitle(String tagTitle);
}
