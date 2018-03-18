package com.github.chen0040.bootslingshot.entities;

import com.github.chen0040.bootslingshot.utils.StringUtils;
import com.github.chen0040.bootslingshot.viewmodels.ImageObj;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Getter
@Setter
public class ImageObjEntity {

    private long id;

    private byte[] model;

    private String tag;

    private long parentId;

    public ImageObj toImageObj() {
        ImageObj result = new ImageObj();
        result.setId(id);
        result.setTag(tag);
        result.setParentId(parentId);
        if(model != null) {
            result.setModel(Base64.getEncoder().encodeToString(model));
        }
        return result;
    }

    public void copy(ImageObj rhs) {
        this.id = rhs.getId();
        this.tag = rhs.getTag();
        this.parentId = rhs.getParentId();
        if(!StringUtils.isEmpty(rhs.getModel())){
            this.model = Base64.getDecoder().decode(rhs.getModel());
        }
    }
}
