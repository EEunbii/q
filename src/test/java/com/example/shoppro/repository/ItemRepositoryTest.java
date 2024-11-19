package com.example.shoppro.repository;

import com.example.shoppro.constant.ItemSellStatus;
import com.example.shoppro.entity.Item;
import com.example.shoppro.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Lob;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        for (int i = 0; i < 200; i++) {
            Item item =
                    Item.builder()
                            .itemNm("테스트 상품")
                            .price(10000)
                            .itemDetail("테스트상품 상세설명")
                            .itemSellStatus(ItemSellStatus.SELL)
                            .stockNumber(100)
                            .regTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now())
                            .build();

            item.setItemNm(item.getItemNm() + i);
            item.setItemDetail(item.getItemDetail() + i);
            item.setPrice(item.getPrice() + i);

            Item item1 =
                    itemRepository.save(item);
            log.info(item1);

        }


    }


    @Test
    @DisplayName("제품명으로 검색 2가지에서 다시 2가지 검색")
    public void findByItemNmTest() {

        List<Item> itemListA =
                itemRepository.findByItemNm("테스트 상품1");

        itemListA.forEach(item -> log.info(item));
        System.out.println("--------------");

        itemListA =
                itemRepository.selectwhereItemNm("테스트상품2");
        itemListA.forEach(item -> log.info("----------"));

        itemListA =
                itemRepository.selectWItenNmLike("1");
        itemListA.forEach(item -> log.info(item));
        System.out.println("------------");

    }

    @Test
    public void priceSearchtest() {
        //가격 검색 테스트
        //사용자가 검색창에 혹은
        //검색이 가능하도록 만들어놓은 곳을 값을 입력한
        //이 조건에 부합되는 아이템 리스트 검색
        Integer price = 10000;

        List<Item> itemListA =
                itemRepository.findByPriceLessThan(price);
        for (Item item : itemListA) {
            log.info(item);
            log.info("상품명: " + item.getItemNm());
            log.info("상품가격: " + item.getPrice());
            log.info("상품 상세설명: " + item.getItemDetail());
        }
        List<Item> itemListB =
                itemRepository.findByPriceLessThan(price);
        for (Item item : itemListB) {
            log.info(item);
            log.info("상품명: " + item.getItemNm());
            log.info("상품가격: " + item.getPrice());
            log.info("상품 상세설명: " + item.getItemDetail());
        }


    }
    @Test
    @DisplayName("페이징 추가까지")
    public void findbypricegreaterThanEqalTest(){
        Pageable pageable = (Pageable) PageRequest
                .of(0,5, Sort.by("id").ascending());
        //sort.by의 정렬조건은 entity의 변수명이다.

        Integer price = 10020;
        List<Item> itemList=
                itemRepository.findByPriceGreaterThanEqual(price,pageable);

        itemList.forEach(item ->log.info(item));
    }
    @Test
    public void nativeQueryTest(){

        Pageable pageable=
                (Pageable) PageRequest.of(0,5, Sort.by("price").descending());
        String itemNm="테스트상품1";
        List<Item> itemList=
                itemRepository.nativeQuerySelectwhereNamelike(itemNm,pageable);
        itemList.forEach(item-> log.info(item));
    }
    @Test
    public void queryDslTest(){

        JPAQueryFactory queryFactory =new JPAQueryFactory(entityManager);

        QItem qItem = QItem.item;
        //select * from item
        String keyword=null;

        ItemSellStatus itemSellStatus=ItemSellStatus.SELL;
        JPAQuery<Item> query=
                queryFactory.selectFrom(qItem)
                        .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                        .where(qItem.itemDetail.like("%"+"1"+"%"))
                        .orderBy(qItem.price.desc());
        List<Item> itemList = query.fetch();

        for(Item item:itemList){
            System.out.println(item.getItemNm());
        }
    }
    @Test
    public void queryDslTestB(){


        JPAQueryFactory queryFactory =new JPAQueryFactory(entityManager);

        QItem qItem = QItem.item;
        //select * from item
        String keyword=null;
        ItemSellStatus itemSellStatus=ItemSellStatus.SELL;

        BooleanBuilder booleanBuilder= new BooleanBuilder();
        if(keyword !=null){
            booleanBuilder.and(qItem.itemDetail.like("%"+keyword+"%"));


        }
        if(itemSellStatus !=null){
            if(itemSellStatus == ItemSellStatus.SELL){
                booleanBuilder.or(qItem.itemSellStatus.eq(ItemSellStatus.SELL));
            }else {
                booleanBuilder.or(qItem.itemSellStatus.eq(ItemSellStatus.SOLD_OUT));
            }
        }

        JPAQuery<Item> query=
                queryFactory.selectFrom(qItem)
                        .where(booleanBuilder)
                        .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        for(Item item:itemList){
            System.out.println(item.getItemNm());
        }

    }
}