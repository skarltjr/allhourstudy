package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.tag.QTag;
import com.allhour.allhourstudy.modules.zone.QZone;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import static com.allhour.allhourstudy.modules.study.QStudy.study;
import static com.allhour.allhourstudy.modules.tag.QTag.tag;
import static com.allhour.allhourstudy.modules.zone.QZone.zone;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {
    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public Page<Study> findByKeyword(String keyWord, Pageable pageable) {
        JPQLQuery<Study> query = from(study)
                .where(study.published.isTrue()
                        .and(study.title.containsIgnoreCase(keyWord))
                        .or(study.tags.any().title.containsIgnoreCase(keyWord))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyWord)))
                .leftJoin(study.tags, tag).fetchJoin()
                .leftJoin(study.zones, zone).fetchJoin()
                .distinct();
        JPQLQuery<Study> studyJPQLQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Study> result = studyJPQLQuery.fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }
}
