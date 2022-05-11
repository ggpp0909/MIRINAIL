package com.nail.backend.domain.nailart.db.repository;


import com.nail.backend.domain.book.db.repository.BookRepositorySupport;
import com.nail.backend.domain.favorite.db.entity.Favorite;
import com.nail.backend.domain.favorite.db.repository.FavoriteRepositorySupport;
import com.nail.backend.domain.nailart.request.NailartUpdatePutReq;
import com.nail.backend.domain.nailart.response.NailartListGetRes;
import com.nail.backend.domain.favorite.db.entity.QFavorite;
import com.nail.backend.domain.nailart.db.entity.Nailart;
import com.nail.backend.domain.nailart.db.entity.QNailart;
import com.nail.backend.domain.qna.request.QnaModifyPutReq;
import com.nail.backend.domain.user.db.repository.UserRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NailartRepositorySupport {

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    NailartRepository nailartRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NailartImgRepository nailartImgRepository;

    @Autowired
    BookRepositorySupport bookRepositorySupport;

    @Autowired
    FavoriteRepositorySupport favoriteRepositorySupport;

    QNailart qNailart = QNailart.nailart;

    QFavorite qFavorite = QFavorite.favorite;


    
    //네일 아트 수정
    @Transactional
    public Long updateNailartByNailartSeq(NailartUpdatePutReq nailartUpdatePutReq) {
        long execute = jpaQueryFactory.update(qNailart)
                .set(qNailart.nailartName, nailartUpdatePutReq.getNailartName())
                .set(qNailart.nailartDesc, nailartUpdatePutReq.getNailartDesc())
                .set(qNailart.nailartType, nailartUpdatePutReq.getNailartType())
                .set(qNailart.nailartColor, nailartUpdatePutReq.getNailartColor())
                .set(qNailart.nailartDetailColor, nailartUpdatePutReq.getNailartDetailColor())
                .set(qNailart.nailartWeather, nailartUpdatePutReq.getNailartWeather())
                .set(qNailart.nailartPrice, nailartUpdatePutReq.getNailartPrice())
                .set(qNailart.nailartRegedAt, Timestamp.valueOf(LocalDateTime.now()))
                .set(qNailart.nailartThumbnailUrl, nailartUpdatePutReq.getNailartThumbnailUrl())
                .where(qNailart.nailartSeq.eq(nailartUpdatePutReq.getNailartSeq()))
                .execute();
        return execute;
    }
    // 네일 아트 삭제
    @Transactional
    public boolean deleteNailartByNailartSeq(long nailartSeq){
        if(nailartRepository.findByNailartSeq(nailartSeq) != null){
            if(bookRepositorySupport.findByNailartSeq(nailartSeq) != null)
                bookRepositorySupport.deleteByNailartSeq(nailartSeq);
            nailartRepository.deleteByNailartSeq(nailartSeq);
            nailartImgRepository.deleteAllByNailartSeq(nailartSeq);
            return true;
        }else
        return false;
    }

   // 색상 x, 타입 x, 최신 순
    public List<NailartListGetRes> getListbyLatest(long userSeq, int page, int size){
        List<NailartListGetRes> result = new ArrayList<>();
        List<Long> nailartSeq = jpaQueryFactory.select(qNailart.nailartSeq)
                .from(qNailart)
                .orderBy(qNailart.nailartSeq.desc())
                .limit(size)
                .offset((page-1)*size)
                .fetch();
        List<Long> totalCount = jpaQueryFactory.select(qNailart.nailartSeq)
                .from(qNailart)
                .orderBy(qNailart.nailartSeq.desc())
                .fetch();
        nailartSeq.forEach( num -> {
            NailartListGetRes tmp = new NailartListGetRes();
            Nailart art = nailartRepository.findByNailartSeq(num);
            tmp.setNailartSeq(art.getNailartSeq());
            tmp.setDesignerNickname(userRepository.findByUserSeq(art.getDesignerSeq()).getUserNickname());
            tmp.setDesignerSeq(art.getDesignerSeq());
            tmp.setTokenId(art.getTokenId());
            tmp.setNailartName(art.getNailartName());
            tmp.setNailartDesc(art.getNailartDesc());
            tmp.setNailartColor(art.getNailartColor());
            tmp.setNailartDetailColor(art.getNailartDetailColor());
            tmp.setNailartWeather(art.getNailartWeather());
            tmp.setNailartThumbnailUrl(art.getNailartThumbnailUrl());
            tmp.setNailartType(art.getNailartType());
            tmp.setNailartPrice(art.getNailartPrice());
            tmp.setNailartRegedAt(art.getNailartRegedAt());
            tmp.setNailartRating(art.getNailartRating());
            tmp.setTotalCount(totalCount.size());
            tmp.setFavorited(favoriteRepositorySupport.getIsFavorited(userSeq, art.getNailartSeq()));
            result.add(tmp);
        });
        return result;
    }

    // 색상 x, 타입 x, 좋아요 순
    public List<NailartListGetRes> getListbyFavoite(long userSeq, int page, int size){

        List<NailartListGetRes> result = new ArrayList<>();
        List<Tuple> list = jpaQueryFactory.select(qNailart.nailartSeq, qFavorite.nailart.count())
                .from(qNailart)
                    .leftJoin(qFavorite)
                    .on(qNailart.eq(qFavorite.nailart))
                .groupBy(qNailart.nailartSeq)
                .orderBy(qFavorite.nailart.count().desc())
                .limit(size)
                .offset((page-1)*size)
                .fetch();
        List<Tuple> totalCount = jpaQueryFactory.select(qNailart.nailartSeq, qFavorite.nailart.count())
                .from(qNailart)
                .leftJoin(qFavorite)
                .on(qNailart.eq(qFavorite.nailart))
                .groupBy(qNailart.nailartSeq)
                .orderBy(qFavorite.nailart.count().desc())
                .fetch();
        list.forEach( num -> {
            NailartListGetRes tmp = new NailartListGetRes();
            Nailart art = nailartRepository.findByNailartSeq(num.get(qNailart.nailartSeq));
            tmp.setNailartSeq(art.getNailartSeq());
            tmp.setDesignerNickname(userRepository.findByUserSeq(art.getDesignerSeq()).getUserNickname());
            tmp.setDesignerSeq(art.getDesignerSeq());
            tmp.setTokenId(art.getTokenId());
            tmp.setNailartName(art.getNailartName());
            tmp.setNailartDesc(art.getNailartDesc());
            tmp.setNailartColor(art.getNailartColor());
            tmp.setNailartDetailColor(art.getNailartDetailColor());
            tmp.setNailartWeather(art.getNailartWeather());
            tmp.setNailartThumbnailUrl(art.getNailartThumbnailUrl());
            tmp.setNailartType(art.getNailartType());
//            tmp.setNailartAvailable(art.get);
            tmp.setNailartPrice(art.getNailartPrice());
            tmp.setNailartRegedAt(art.getNailartRegedAt());
            tmp.setNailartRating(art.getNailartRating());
            tmp.setTotalCount(totalCount.size());
            tmp.setFavorited(favoriteRepositorySupport.getIsFavorited(userSeq, art.getNailartSeq()));
            result.add(tmp);
        });

        return result;
    }

    // 색상 o, 최신순
    public List<NailartListGetRes> getListbyColorLatest(long userSeq, String color, int page, int size){
        List<NailartListGetRes> result = new ArrayList<>();
        List<Long> nailartSeq = jpaQueryFactory.select(qNailart.nailartSeq)
                .from(qNailart)
                .where(qNailart.nailartColor.eq(color))
                .orderBy(qNailart.nailartSeq.desc())
                .limit(size)
                .offset((page-1)*size)
                .fetch();
        List<Long> totalCount = jpaQueryFactory.select(qNailart.nailartSeq)
                .from(qNailart)
                .where(qNailart.nailartColor.eq(color))
                .orderBy(qNailart.nailartSeq.desc())
                .fetch();
        nailartSeq.forEach( num -> {
            NailartListGetRes tmp = new NailartListGetRes();
            Nailart art = nailartRepository.findByNailartSeq(num);
            tmp.setNailartSeq(art.getNailartSeq());
            tmp.setDesignerNickname(userRepository.findByUserSeq(art.getDesignerSeq()).getUserNickname());
            tmp.setDesignerSeq(art.getDesignerSeq());
            tmp.setTokenId(art.getTokenId());
            tmp.setNailartName(art.getNailartName());
            tmp.setNailartDesc(art.getNailartDesc());
            tmp.setNailartColor(art.getNailartColor());
            tmp.setNailartDetailColor(art.getNailartDetailColor());
            tmp.setNailartWeather(art.getNailartWeather());
            tmp.setNailartThumbnailUrl(art.getNailartThumbnailUrl());
            tmp.setNailartType(art.getNailartType());
//            tmp.setNailartAvailable(art.get);
            tmp.setNailartPrice(art.getNailartPrice());
            tmp.setNailartRegedAt(art.getNailartRegedAt());
            tmp.setNailartRating(art.getNailartRating());
            tmp.setTotalCount(totalCount.size());
            tmp.setFavorited(favoriteRepositorySupport.getIsFavorited(userSeq, art.getNailartSeq()));
            result.add(tmp);
        });
        return result;
    }

    // 색상 o, 좋아요 순
    public List<NailartListGetRes> getListbyColorFavoite(long userSeq, String color, int page, int size){
        List<NailartListGetRes> result = new ArrayList<>();
        List<Tuple> list = jpaQueryFactory.select(qNailart.nailartSeq, qFavorite.nailart.count())
                .from(qNailart)
                .leftJoin(qFavorite)
                .on(qNailart.eq(qFavorite.nailart))
                .where(qNailart.nailartColor.eq(color))
                .groupBy(qNailart.nailartSeq)
                .orderBy(qFavorite.nailart.count().desc())
                .limit(size)
                .offset((page-1)*size)
                .fetch();
        List<Tuple> totalCount = jpaQueryFactory.select(qNailart.nailartSeq, qFavorite.nailart.count())
                .from(qNailart)
                .leftJoin(qFavorite)
                .on(qNailart.eq(qFavorite.nailart))
                .where(qNailart.nailartColor.eq(color))
                .groupBy(qNailart.nailartSeq)
                .orderBy(qFavorite.nailart.count().desc())
                .fetch();
        list.forEach( num -> {
            NailartListGetRes tmp = new NailartListGetRes();
            Nailart art = nailartRepository.findByNailartSeq(num.get(qNailart.nailartSeq));
            tmp.setNailartSeq(art.getNailartSeq());
            tmp.setDesignerNickname(userRepository.findByUserSeq(art.getDesignerSeq()).getUserNickname());
            tmp.setDesignerSeq(art.getDesignerSeq());
            tmp.setTokenId(art.getTokenId());
            tmp.setNailartName(art.getNailartName());
            tmp.setNailartDesc(art.getNailartDesc());
            tmp.setNailartColor(art.getNailartColor());
            tmp.setNailartDetailColor(art.getNailartDetailColor());
            tmp.setNailartWeather(art.getNailartWeather());
            tmp.setNailartThumbnailUrl(art.getNailartThumbnailUrl());
            tmp.setNailartType(art.getNailartType());
//            tmp.setNailartAvailable(art.get);
            tmp.setNailartPrice(art.getNailartPrice());
            tmp.setNailartRegedAt(art.getNailartRegedAt());
            tmp.setNailartRating(art.getNailartRating());
            tmp.setTotalCount(totalCount.size());
            tmp.setFavorited(favoriteRepositorySupport.getIsFavorited(userSeq, art.getNailartSeq()));
            result.add(tmp);
        });

        return result;
    }

    // 타입 o, 최신순
    public List<NailartListGetRes> getListbyTypeLatest(long userSeq, String type, int page, int size){
        List<NailartListGetRes> result = new ArrayList<>();
        List<Long> nailartSeq = jpaQueryFactory.select(qNailart.nailartSeq)
                .from(qNailart)
                .where(qNailart.nailartType.eq(type))
                .orderBy(qNailart.nailartSeq.desc())
                .limit(size)
                .offset((page-1)*size)
                .fetch();
        List<Long> totalCount = jpaQueryFactory.select(qNailart.nailartSeq)
                .from(qNailart)
                .where(qNailart.nailartType.eq(type))
                .orderBy(qNailart.nailartSeq.desc())
                .fetch();
        nailartSeq.forEach( num -> {
            NailartListGetRes tmp = new NailartListGetRes();
            Nailart art = nailartRepository.findByNailartSeq(num);
            tmp.setNailartSeq(art.getNailartSeq());
            tmp.setDesignerNickname(userRepository.findByUserSeq(art.getDesignerSeq()).getUserNickname());
            tmp.setDesignerSeq(art.getDesignerSeq());
            tmp.setTokenId(art.getTokenId());
            tmp.setNailartName(art.getNailartName());
            tmp.setNailartDesc(art.getNailartDesc());
            tmp.setNailartColor(art.getNailartColor());
            tmp.setNailartDetailColor(art.getNailartDetailColor());
            tmp.setNailartWeather(art.getNailartWeather());
            tmp.setNailartThumbnailUrl(art.getNailartThumbnailUrl());
            tmp.setNailartType(art.getNailartType());
//            tmp.setNailartAvailable(art.get);
            tmp.setNailartPrice(art.getNailartPrice());
            tmp.setNailartRegedAt(art.getNailartRegedAt());
            tmp.setNailartRating(art.getNailartRating());
            tmp.setTotalCount(totalCount.size());
            tmp.setFavorited(favoriteRepositorySupport.getIsFavorited(userSeq, art.getNailartSeq()));
            result.add(tmp);
        });
        return result;
    }

    // 타입 o, 좋아요 순
    public List<NailartListGetRes> getListbyTypeFavoite(long userSeq, String type, int page, int size){
        List<NailartListGetRes> result = new ArrayList<>();
        List<Tuple> list = jpaQueryFactory.select(qNailart.nailartSeq, qFavorite.nailart.count())
                .from(qNailart)
                .leftJoin(qFavorite)
                .on(qNailart.eq(qFavorite.nailart))
                .where(qNailart.nailartType.eq(type))
                .groupBy(qNailart.nailartSeq)
                .orderBy(qFavorite.nailart.count().desc())
                .limit(size)
                .offset((page-1)*size)
                .fetch();
        List<Tuple> totalCount = jpaQueryFactory.select(qNailart.nailartSeq, qFavorite.nailart.count())
                .from(qNailart)
                .leftJoin(qFavorite)
                .on(qNailart.eq(qFavorite.nailart))
                .where(qNailart.nailartType.eq(type))
                .groupBy(qNailart.nailartSeq)
                .orderBy(qFavorite.nailart.count().desc())
                .fetch();
        list.forEach( num -> {
            NailartListGetRes tmp = new NailartListGetRes();
            Nailart art = nailartRepository.findByNailartSeq(num.get(qNailart.nailartSeq));
            tmp.setNailartSeq(art.getNailartSeq());
            tmp.setDesignerNickname(userRepository.findByUserSeq(art.getDesignerSeq()).getUserNickname());
            tmp.setDesignerSeq(art.getDesignerSeq());
            tmp.setTokenId(art.getTokenId());
            tmp.setNailartName(art.getNailartName());
            tmp.setNailartDesc(art.getNailartDesc());
            tmp.setNailartColor(art.getNailartColor());
            tmp.setNailartDetailColor(art.getNailartDetailColor());
            tmp.setNailartWeather(art.getNailartWeather());
            tmp.setNailartThumbnailUrl(art.getNailartThumbnailUrl());
            tmp.setNailartType(art.getNailartType());
            tmp.setNailartPrice(art.getNailartPrice());
            tmp.setNailartRegedAt(art.getNailartRegedAt());
            tmp.setNailartRating(art.getNailartRating());
            tmp.setTotalCount(totalCount.size());
            tmp.setFavorited(favoriteRepositorySupport.getIsFavorited(userSeq, art.getNailartSeq()));
            result.add(tmp);
        });

        return result;
    }

    // 이삭 작성
    // 리뷰 평점 update
    public Long modifyRatingByNailartSeq(float rate , Long nailartSeq){
        Long execute = jpaQueryFactory.update(qNailart)
                .set(qNailart.nailartRating,rate)
                .where(qNailart.nailartSeq.eq(nailartSeq))
                .execute();
        return execute;
    }
}
