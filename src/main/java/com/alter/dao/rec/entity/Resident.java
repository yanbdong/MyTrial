package com.alter.dao.rec.entity;

import java.math.BigDecimal;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 数字居民表
 * </p>
 *
 * @author yanbdong
 * @since 2021-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Resident对象", description="数字居民表")
public class Resident implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
//    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long uid;

    @ApiModelProperty(value = "领地领地ID")
    private String territoryId;

    @ApiModelProperty(value = "贡献值")
    private BigDecimal contribution;

    @ApiModelProperty(value = "1、正常；2、拉黑；3、删除；4、注销；")
    private Integer status;

    @ApiModelProperty(value = "数字居民简介")
    private String mark;

    @ApiModelProperty(value = "加入时间")
    private LocalDateTime joinTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "身份类别，1、原著民；2、普通居民")
    private Integer identityCategory;

}
