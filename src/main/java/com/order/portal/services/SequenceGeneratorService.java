package com.order.portal.services;

import com.order.portal.models.DatabaseSequence;
import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    public long generateSequence(String sequenceName) {
        DatabaseSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(sequenceName)),
                new Update().inc("value",1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class
        );

        assert counter != null;

        return counter.getValue();
    }
}
