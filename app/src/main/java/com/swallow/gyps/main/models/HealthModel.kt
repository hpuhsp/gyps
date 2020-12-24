package com.swallow.gyps.main.models

import java.io.Serializable

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/12/24 10:30
 * @UpdateRemark:   更新说明：
 */
data class HealthModel(
    val answer1: String = "河南省 郑州市",
    var answer10: String = "36.6",
    val answer11: String = "",
    val answer12: String = "否",
    val answer2: String = "否",
    val answer3: String = "否",
    val answer4: String = "否",
    val answer5: String = "否",
    val answer6: String = "否",
    val answer7: String = "无症状",
    val answer8: String = "",
    val answer9: String = "否",
    val blongSystem: String = "xxxt",
    val orgID: String = "001-005-009-005",
    val street: String = "河南省郑东新区绿地双子塔3301",
    val userID: String = "365545"
) : Serializable