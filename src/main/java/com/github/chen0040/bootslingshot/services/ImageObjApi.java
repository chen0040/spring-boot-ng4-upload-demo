package com.github.chen0040.bootslingshot.services;

import com.github.chen0040.bootslingshot.viewmodels.ImageObj;
import com.github.chen0040.bootslingshot.viewmodels.TokenObj;

import java.util.List;

public interface ImageObjApi {
    List<Long> findAllIdsByTag(String tag, TokenObj tokenObj);

    ImageObj findFirstByParentId(long parentId, TokenObj tokenObj);

    ImageObj findById(long id, TokenObj tokenObj);
}
