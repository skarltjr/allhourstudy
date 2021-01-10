package com.allhour.allhourstudy.modules.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreate(TagForm tagForm) {
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            Tag newTag = new Tag();
            newTag.setTitle(tagForm.getTagTitle());
            return tagRepository.save(newTag);
        }
        return tag;
    }
}
