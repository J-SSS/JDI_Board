package com.board.jdi_board.service;

import com.board.jdi_board.dto.BoardsDto;

import java.util.List;

public interface BoardsService {

    List<BoardsDto> list();
    int register(BoardsDto boardsDto);
    BoardsDto detail(int bId);
}
