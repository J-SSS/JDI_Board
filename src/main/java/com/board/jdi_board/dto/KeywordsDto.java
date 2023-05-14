package com.board.jdi_board.dto;

import lombok.Data;

@Data
public class KeywordsDto {
    int kId; // 관계 테이블 PK
    String uniqueList; // 모든 게시글의 형태소 리스트를 중복 없는 배열로 저장
    String totalList; // 모든 게시글의 형태소 리스트를 리스트의 리스트로 저장
    String mapList; // 모든 게시글의 모든 게시글의 tf-idf 행렬을 bid와 함께 맵으로 저장 리스트를 리스트의 리스트로 저장
    int isUnique; // 유니크 리스트 레코드인지?
    int isTotal; // 토탈 리스트 레코드인지?
    int isMap; // 맵 리스트 레코드인지?
}
