package com.ltx.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 筷子
 *
 * @author tianxing
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
public class Chopstick extends ReentrantLock {
    private String name;
}
