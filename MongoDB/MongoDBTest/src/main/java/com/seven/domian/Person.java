package com.seven.domian;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document("tb_person")
@Data
public class Person {
    @Id
    private ObjectId id;
    @Field("myname")
    private String name;
    private Integer age;
    private String address;
}
