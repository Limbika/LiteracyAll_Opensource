package com.literacyall.app.utilities;

/*import org.apache.commons.lang3.builder.ToStringBuilder;*/

import java.io.Serializable;

/**
 * Created by Pooja Gaikwad on 6/22/2016.
 */
public class CustomList implements Serializable {

    public static final long serialID = 1234L;

    public Long key;
    public String imageName;

    /**
     * No args constructor for use in serialization
     */
    public CustomList() {
    }

    /**
     * @param key
     * @param imageName
     */
    public CustomList(long key, String imageName) {
        this.key = key;
        this.imageName = imageName;
    }

   /* @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }*/
}
