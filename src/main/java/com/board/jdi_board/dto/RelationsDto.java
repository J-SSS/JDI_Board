package com.board.jdi_board.dto;

import lombok.Data;

@Data
public class RelationsDto {
    int brId; // 관계 테이블 PK
    int bId; // 게시글 FK
    String terms; // 형태소 명사 리스트
    String thIdf; // TH-IDF 분석 리스트
    int uniqueList; // TRUE인 레코드는 모든 데이터의 형태소 리스트를 중복없는 리스트로 가짐. default는 FALSE
    int totalList; // TRUE인 레코드는 모든 데이터의 형태소 리스트를 리스트의 리스트로 가짐. default는 FALSE
}