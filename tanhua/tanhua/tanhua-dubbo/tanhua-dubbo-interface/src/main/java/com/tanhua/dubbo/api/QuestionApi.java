package com.tanhua.dubbo.api;


import com.tanhua.model.domian.Question;

public interface QuestionApi {
    //根据id查询陌生人问题
    Question findByUserId(Long userId);

    //保存陌生人问题
    void save(Question question);

    //更新
    void update(Question question);
}
