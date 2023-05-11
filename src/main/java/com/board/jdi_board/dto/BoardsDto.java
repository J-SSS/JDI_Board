package com.board.jdi_board.dto;

import lombok.Data;

@Data
public class BoardsDto {

    private int bId; // 게시글 PK
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private java.util.Date postTime; // 게시글 작성시간

}
