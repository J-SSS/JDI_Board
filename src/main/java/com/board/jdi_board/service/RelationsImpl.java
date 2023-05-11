package com.board.jdi_board.service;

import com.board.jdi_board.dto.RelationsDto;
import com.board.jdi_board.mapper.RelationsMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RelationsImpl implements RelationsService {
    private RelationsMapper relationsMapper;
    @Override
    public int register(RelationsDto relationsDto) {
        return relationsMapper.insertOne(relationsDto);
    }

    @Override
    public int modify(RelationsDto relationsDto) {
        return relationsMapper.updateOne(relationsDto);
    }
}
