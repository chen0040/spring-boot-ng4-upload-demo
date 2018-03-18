package com.github.chen0040.bootslingshot.entities;

import com.github.chen0040.bootslingshot.utils.StringUtils;
import com.github.chen0040.bootslingshot.viewmodels.BinaryObj;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Getter
@Setter
public class BinaryObjEntity {

    private long id;

    private byte[] model;

    private String tag;

    public BinaryObj toBinaryObj() {
        BinaryObj result = new BinaryObj();
        result.setId(id);
        result.setTag(tag);
        if(model != null) {
            result.setModel(Base64.getEncoder().encodeToString(model));
        }
        return result;
    }

    public void copy(BinaryObj rhs) {
        this.id = rhs.getId();
        this.tag = rhs.getTag();
        if(!StringUtils.isEmpty(rhs.getModel())){
            this.model = Base64.getDecoder().decode(rhs.getModel());
        }
    }
}
