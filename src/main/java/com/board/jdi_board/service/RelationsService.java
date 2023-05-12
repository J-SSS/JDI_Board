package com.board.jdi_board.service;

import com.board.jdi_board.dto.RelationsDto;

import java.util.List;

public interface RelationsService {

    List<RelationsDto> list();
    RelationsDto detail(int brId);
    int register(RelationsDto relationsDto);
    int modify(RelationsDto relationsDto);
}
