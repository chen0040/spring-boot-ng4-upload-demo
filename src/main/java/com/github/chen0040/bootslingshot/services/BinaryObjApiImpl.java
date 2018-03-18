package com.github.chen0040.bootslingshot.services;

import com.github.chen0040.bootslingshot.entities.BinaryObjEntity;
import com.github.chen0040.bootslingshot.viewmodels.BinaryObj;
import com.github.chen0040.bootslingshot.viewmodels.TokenObj;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BinaryObjApiImpl implements BinaryObjApi {
    private Map<Long, BinaryObjEntity> repo = new HashMap<>();
    private long lastId = 0L;

    @Override
    public List<Long> findAllIdsByTag(String tag, TokenObj tokenObj) {
        List<Long> result = new ArrayList<>();
        for(BinaryObjEntity entity : repo.values()){
            if(entity.getTag().equals(tag)) {
                result.add(entity.getId());
            }
        }
        return result;
    }

    @Override
    public BinaryObj findById(long id, TokenObj tokenObj) {
        for(BinaryObjEntity entity : repo.values()){
            if(id == entity.getId()) {
                return entity.toBinaryObj();
            }
        }
        return BinaryObj.createAlert("id cannot be found");
    }

    @Override
    public BinaryObj deleteById(long id, TokenObj tokenObj) {
        if(repo.containsKey(id)){
            BinaryObj obj = repo.get(id).toBinaryObj();
            repo.remove(id);
            return obj;
        }
        return BinaryObj.createAlert("id cannot be found");
    }

    @Override
    public void save(BinaryObj bo) {

        if(!repo.containsKey(bo.getId())){
            bo.setId(0L);
        }

        if(bo.getId() == 0L){
            bo.setId(++lastId);
        }

        BinaryObjEntity entity = new BinaryObjEntity();
        entity.copy(bo);

        repo.put(bo.getId(), entity);

    }
}
