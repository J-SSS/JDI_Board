package com.board.jdi_board.service;

import com.board.jdi_board.dto.KeywordsDto;
import com.board.jdi_board.dto.RelationsDto;
import com.board.jdi_board.mapper.KeywordsMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class KeywordsImpl implements KeywordsService {
    private KeywordsMapper keywordsMapper;
    @Override
    public List<KeywordsDto> list() {
        return keywordsMapper.findAll();
    }

    @Override
    public KeywordsDto detail(int kId) {
        return keywordsMapper.findByKId(kId);
    }

    @Override
    public int register(KeywordsDto keywordsDto) {
        return keywordsMapper.insertOne(keywordsDto);
    }

    @Override
    public int modify(KeywordsDto keywordsDto) {
        return keywordsMapper.updateOne(keywordsDto);
    }
}
