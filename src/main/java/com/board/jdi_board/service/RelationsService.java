package com.board.jdi_board.service;

import com.board.jdi_board.dto.RelationsDto;

public interface RelationsService {

    int register(RelationsDto relationsDto);
    int modify(RelationsDto relationsDto);
}
