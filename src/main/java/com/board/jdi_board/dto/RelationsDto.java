package com.board.jdi_board.dto;

import lombok.Data;

import java.util.List;

@Data
public class RelationsDto {
    int brId; // 관계 테이블 PK
    int bId; // 게시글 FK
    String terms; // 형태소 명사 리스트
    String tfIdf; // TF-IDF 분석 리스트
    String relBIdList; // 연관된 게시글 bid 리스트

}
