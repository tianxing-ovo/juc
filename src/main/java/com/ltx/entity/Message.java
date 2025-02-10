package com.ltx.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 消息
 *
 * @author tianxing
 */
@Data
@AllArgsConstructor
public class Message {
    private int id;
    private Object value;
}