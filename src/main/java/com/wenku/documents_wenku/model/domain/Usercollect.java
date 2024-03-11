package com.wenku.documents_wenku.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户点赞表
 * @TableName userCollect
 */
@TableName(value ="userCollect")
@Data
public class Usercollect implements Serializable {
    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 点赞用户id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 被点赞文档id
     */
    @TableField(value = "documentId")
    private Long documentId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}