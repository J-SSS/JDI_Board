package com.board.jdi_board.service;

import com.board.jdi_board.dto.BoardsDto;

import java.util.List;

public interface BoardsService {

    List<BoardsDto> list();

    BoardsDto detail(int bId);
}
