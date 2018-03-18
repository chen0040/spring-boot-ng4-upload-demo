package com.github.chen0040.bootslingshot.services;

import com.github.chen0040.bootslingshot.entities.ImageObjEntity;
import com.github.chen0040.bootslingshot.entities.ImageObjEntity;
import com.github.chen0040.bootslingshot.viewmodels.ImageObj;
import com.github.chen0040.bootslingshot.viewmodels.ImageObj;
import com.github.chen0040.bootslingshot.viewmodels.TokenObj;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageObjApiImpl implements ImageObjApi {
    private Map<Long, ImageObjEntity> repo = new HashMap<>();
    private long lastId = 0L;

    @Override
    public List<Long> findAllIdsByTag(String tag, TokenObj tokenObj) {
        List<Long> result = new ArrayList<>();
        for(ImageObjEntity entity : repo.values()){
            if(entity.getTag().equals(tag)){
                result.add(entity.getId());
            }
        }
        return result;
    }

    @Override
    public ImageObj findFirstByParentId(long parentId, TokenObj tokenObj) {
        for(ImageObjEntity entity: repo.values()){
            if(entity.getParentId() == parentId) {
                return entity.toImageObj();
            }
        }
        return ImageObj.createAlert("parentId not found");
    }

    @Override
    public ImageObj findById(long id, TokenObj tokenObj) {
        for(ImageObjEntity entity : repo.values()){
            if(id == entity.getId()){
                return entity.toImageObj();
            }
        }
        return ImageObj.createAlert("id not found");
    }

    @Override
    public void save(ImageObj bo) {

        if(!repo.containsKey(bo.getId())){
            bo.setId(0L);
        }

        if(bo.getId() == 0L){
            bo.setId(++lastId);
        }

        ImageObjEntity entity = new ImageObjEntity();
        entity.copy(bo);

        repo.put(bo.getId(), entity);

    }
}
