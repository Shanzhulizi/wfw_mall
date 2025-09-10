package com.lm.promotion.Enum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券状态枚举
 */
public enum CouponStatus {

    UNUSED(0, "未使用", "优惠券未使用，可用状态"),
    LOCKED(1, "已锁定", "优惠券已被订单锁定，等待确认"),
    USED(2, "已使用", "优惠券已被使用，不可再使用"),
    EXPIRED(3, "已过期", "优惠券已过期"),
    CANCELED(4, "已取消", "优惠券已被取消"),
    DISABLED(5, "已禁用", "优惠券已被管理员禁用");

    private final int code;
    private final String name;
    private final String description;

    CouponStatus(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举实例
     */
    public static CouponStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CouponStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据name获取枚举实例
     */
    public static CouponStatus getByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        for (CouponStatus status : values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为可用状态（可以锁定和使用）
     */
    public boolean isAvailable() {
        return this == UNUSED;
    }

    /**
     * 判断是否为终态（不可再变更的状态）
     */
    public boolean isFinalStatus() {
        return this == USED || this == EXPIRED || this == CANCELED || this == DISABLED;
    }

    /**
     * 判断是否可以锁定
     */
    public boolean canLock() {
        return this == UNUSED;
    }

    /**
     * 判断是否可以确认使用
     */
    public boolean canConfirmUse() {
        return this == LOCKED;
    }

    /**
     * 判断是否可以取消锁定
     */
    public boolean canCancelLock() {
        return this == LOCKED;
    }

    /**
     * 获取所有可用状态的枚举
     */
    public static List<CouponStatus> getAvailableStatus() {
        return Arrays.stream(values())
                .filter(CouponStatus::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有终态的枚举
     */
    public static List<CouponStatus> getFinalStatus() {
        return Arrays.stream(values())
                .filter(CouponStatus::isFinalStatus)
                .collect(Collectors.toList());
    }

    /**
     * 验证状态流转是否合法
     */
    public static boolean isValidTransition(CouponStatus from, CouponStatus to) {
        if (from == null || to == null) {
            return false;
        }

        // 终态不能再转换
        if (from.isFinalStatus()) {
            return false;
        }

        // 具体的状态流转规则
        switch (from) {
            case UNUSED:
                return to == LOCKED || to == EXPIRED || to == CANCELED || to == DISABLED;
            case LOCKED:
                return to == USED || to == UNUSED; // USED:确认使用, UNUSED:取消锁定
            default:
                return false;
        }
    }

    /**
     * 获取状态提示信息
     */
    public String getTipMessage() {
        switch (this) {
            case UNUSED:
                return "优惠券可用";
            case LOCKED:
                return "优惠券已被锁定，请在30分钟内完成订单";
            case USED:
                return "优惠券已使用";
            case EXPIRED:
                return "优惠券已过期";
            case CANCELED:
                return "优惠券已取消";
            case DISABLED:
                return "优惠券已被禁用";
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return name + "(" + code + ") - " + description;
    }

    /**
     * 获取所有枚举值的代码列表
     */
    public static List<Integer> getAllCodes() {
        return Arrays.stream(values())
                .map(CouponStatus::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有枚举值的名称列表
     */
    public static List<String> getAllNames() {
        return Arrays.stream(values())
                .map(CouponStatus::getName)
                .collect(Collectors.toList());
    }
}