package com.github.chen0040.bootslingshot.services;

import com.github.chen0040.bootslingshot.viewmodels.BinaryObj;
import com.github.chen0040.bootslingshot.viewmodels.TokenObj;

import java.util.List;

public interface BinaryObjApi {
    List<Long> findAllIdsByTag(String tag, TokenObj tokenObj);

    BinaryObj findById(long id, TokenObj tokenObj);

    BinaryObj deleteById(long id, TokenObj tokenObj);

    void save(BinaryObj bo);
}
