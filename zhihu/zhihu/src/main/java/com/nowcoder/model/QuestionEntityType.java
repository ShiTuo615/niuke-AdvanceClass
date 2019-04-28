package com.nowcoder.model;

import java.util.ArrayList;
import java.util.List;

public class QuestionEntityType {
    public static List<String> questionType = new ArrayList<String>();
    static{
        questionType.add("生活方式");
        questionType.add("经济学");
        questionType.add("运动");
        questionType.add("互联网");
        questionType.add("艺术");
        questionType.add("阅读");
        questionType.add("美食");
        questionType.add("动漫");
        questionType.add("汽车");
        questionType.add("足球");
        questionType.add("无分类");
    }
}
