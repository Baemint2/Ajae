package com.ajae.uhtm.repository.joke;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;


public class QueryJokeRepositoryImpl implements QueryJokeRepository {

    JPAQueryFactory queryFactory;

    public QueryJokeRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

}
