package com.board.jdi_board.service;

import com.board.jdi_board.dto.KeywordsDto;
import com.board.jdi_board.dto.RelationsDto;

import java.util.List;

public interface KeywordsService {
    List<KeywordsDto> list();
    KeywordsDto detail(int kId);
    int register(KeywordsDto keywordsDto);
    int modify(KeywordsDto keywordsDto);
}
